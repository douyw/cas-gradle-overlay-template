package com.infusionsoft.cas.web;

import com.infusionsoft.cas.types.User;
import com.infusionsoft.cas.types.UserAccount;
import org.apache.commons.validator.EmailValidator;
import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.AuthenticationManager;
import org.jasig.cas.authentication.handler.PasswordEncoder;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.authentication.principal.SimpleWebApplicationServiceImpl;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.ServiceRegistryDao;
import org.jasig.cas.util.UniqueTicketIdGenerator;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller that powers the registration and association pages.
 */
public class RegistrationMultiActionController extends MultiActionController {
    private static final int PASSWORD_LENGTH_MIN = 7;
    private static final int PASSWORD_LENGTH_MAX = 20;

    private HibernateTemplate hibernateTemplate;
    private PasswordEncoder passwordEncoder;
    private UniqueTicketIdGenerator ticketIdGenerator;
    private CentralAuthenticationService centralAuthenticationService;
    private ServiceRegistryDao serviceRegistryDao;
    
    /**
     * Shows the registration form.
     */
    public ModelAndView welcome(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> model = new HashMap<String, Object>();

        model.put("loginTicket", ticketIdGenerator.getNewTicketId("LT"));

        return new ModelAndView("default/ui/registration/welcome", model);
    }

    /**
     * Registers a new user account.
     */
    public ModelAndView register(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("username");
        String password1 = request.getParameter("password1");
        String password2 = request.getParameter("password2");
        Map<String, Object> model = new HashMap<String, Object>();
        User user = null;

        try {
            user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password1));
            user.setEnabled(true);

            model.put("user", user);

            if (username == null || username.isEmpty() || !EmailValidator.getInstance().isValid(username)) {
                model.put("error", "registration.error.invalidUsername");
            } else if (hibernateTemplate.find("from User u where u.username = ?", username).size() > 0) {
                model.put("error", "registration.error.usernameInUse");
            } else if (password1 == null || password1.length() < PASSWORD_LENGTH_MIN || password1.length() > PASSWORD_LENGTH_MAX) {
                model.put("error", "registration.error.invalidPassword");
            } else if (!password1.equals(password2)) {
                model.put("error", "registration.error.passwordsNoMatch");
            }

            if (model.containsKey("error")) {
                logger.warn("couldn't create new user account: " + model.get("error"));
            } else {
                hibernateTemplate.save(user);

                UsernamePasswordCredentials credentials = new UsernamePasswordCredentials();

                credentials.setUsername(username);
                credentials.setPassword(password1);

                String ticketGrantingTicket = centralAuthenticationService.createTicketGrantingTicket(credentials);
                String contextPath = request.getContextPath();

                if (!contextPath.endsWith("/")) {
                    contextPath = contextPath + "/";
                }

                Cookie cookie = new Cookie("CASTGC", ticketGrantingTicket);
                cookie.setPath(contextPath);
//                cookie.setMaxAge(3600000);

                response.addCookie(cookie);
                
                logger.info("registered new user account " + username);
                logger.info("set cookie CASTGC=" + ticketGrantingTicket);
            }
        } catch (Exception e) {
            logger.error("failed to create user account", e);

            model.put("error", "registration.error.exception");
        }

        if (model.containsKey("error")) {
            return new ModelAndView("default/ui/registration/welcome", model);
        } else {
            return new ModelAndView("redirect:manage?user=" + user.getId());
        }
    }

    /**
     * Shows the management page.
     */
    public ModelAndView manage(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> model = new HashMap<String, Object>();
        User user = hibernateTemplate.get(User.class, Long.parseLong(request.getParameter("user")));

        if (user != null) {
            model.put("user", user);

            return new ModelAndView("default/ui/registration/manage", model);
        } else {
            return new ModelAndView("redirect:welcome", model);
        }
    }
    
    public ModelAndView associateForm(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("default/ui/registration/associate" + request.getParameter("type"));
    }

    public ModelAndView associate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> model = new HashMap<String, Object>();

        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            
            System.out.println("********* user is " + user);
//            User user = getHibernateTemplate().get(User.class, Long.parseLong(request.getParameter("user")));
            UserAccount account = new UserAccount();

            account.setUser(user);
            account.setAppName(request.getParameter("appName"));
            account.setAppUsername(request.getParameter("appUsername"));

            // TODO - big security hole here! need to validate that one of the following is true before mapping:
            // 1. email address matches the username on both accounts
            // 2. we can log in to the app with their username and password, and validate it

            user.getAccounts().add(account);

            hibernateTemplate.save(account);
            hibernateTemplate.update(user);
        } catch (Exception e) {
            logger.error("failed to associate account", e);

            model.put("error", "registration.error.couldNotAssociate");
        }
        
        if (model.containsKey("error")) {
            response.sendError(500, "failed to validate credentials");
        } else {
            response.getWriter().print("OK");
        }
        
        return new ModelAndView();
    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }

    public void setTicketIdGenerator(UniqueTicketIdGenerator ticketIdGenerator) {
        this.ticketIdGenerator = ticketIdGenerator;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void setCentralAuthenticationService(CentralAuthenticationService centralAuthenticationService) {
        this.centralAuthenticationService = centralAuthenticationService;
    }
}
