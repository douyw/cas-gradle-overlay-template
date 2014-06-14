package com.infusionsoft.cas.web.controllers;

import com.infusionsoft.cas.auth.OAuthClientCredentialAuthenticationToken;
import com.infusionsoft.cas.domain.AppType;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.oauth.dto.OAuthAccessToken;
import com.infusionsoft.cas.oauth.dto.OAuthUserApplication;
import com.infusionsoft.cas.oauth.exceptions.*;
import com.infusionsoft.cas.oauth.mashery.api.domain.MasheryAccessToken;
import com.infusionsoft.cas.oauth.mashery.api.domain.MasheryCreateAccessTokenResponse;
import com.infusionsoft.cas.oauth.mashery.api.domain.MasheryUserApplication;
import com.infusionsoft.cas.oauth.services.OAuthService;
import com.infusionsoft.cas.services.CrmService;
import com.infusionsoft.cas.services.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Controller that provides the required Authorization Server endpoints for OAuth 2.0 Grants
 * These endpoints call the Mashery API to verify clients, obtain authorization codes, and allows the user to accept/deny access.
 */
@Controller
public class OAuthController {

    private Logger logger = LoggerFactory.getLogger(OAuthController.class);

    @Autowired
    CrmService crmService;

    @Autowired
    OAuthService oauthService;

    @Autowired
    UserService userService;

    @Value("${cas.viewResolver.basename}")
    private String viewResolverBaseName;

    private String getViewBase() {
        return "default_views".equals(viewResolverBaseName) ? "" : viewResolverBaseName + "/";
    }

    @ExceptionHandler(OAuthException.class)
    public ModelAndView handleOAuthException(OAuthException e, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        Map<String, String> model = new HashMap<String, String>();

        logger.info("Unhandled OAuthException", e);

        model.put("error", e.getErrorCode());

        if(StringUtils.isNotBlank(e.getErrorDescription()) ){
            model.put("error_description", e.getErrorDescription());
        }

        if(StringUtils.isNotBlank(e.getErrorUri()) ){
            model.put("error_uri", e.getErrorUri());
        }

        String grantType = request.getParameter("grant_type");

        if("code".equals(grantType)) {
            model.put("state", request.getParameter("state"));
            modelAndView.setViewName("redirect:" + request.getParameter("redirect_uri"));
        }

        modelAndView.addAllObjects(model);

        return modelAndView;
    }

    /**
     * Landing page for a user to give a 3rd party application permission to their API
     */
    @RequestMapping
    public String authorize(Model model, String client_id, String redirect_uri, String response_type, String scope, String state) throws Exception {

        model.addAttribute("redirect_uri", redirect_uri);
        model.addAttribute("state", state);

        if (StringUtils.isBlank(client_id) || StringUtils.isBlank(redirect_uri) || StringUtils.isBlank(response_type)) {
            throw new OAuthInvalidRequestException();
        } else if (!"code".equals(response_type)) {
            throw new OAuthUnsupportedResponseTypeException();
        } else {
            model.addAttribute("oauthApplication", oauthService.fetchApplication(client_id, redirect_uri, response_type));

            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            List<UserAccount> accounts = userService.findSortedUserAccountsByAppType(user, AppType.CRM);

            model.addAttribute("client_id", client_id);
            model.addAttribute("redirect_uri", redirect_uri);
            model.addAttribute("requestedScope", scope);
            model.addAttribute("apps", crmService.extractAppNames(accounts));

            return "oauth/" + getViewBase() + "authorize";
        }
    }

    /**
     * Token generation for Resource Grant Type
     */
    @RequestMapping
    @ResponseBody
    public OAuthAccessToken token(String grant_type, String scope) throws Exception {
        OAuthClientCredentialAuthenticationToken oAuthClientCredentialAuthenticationToken = (OAuthClientCredentialAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        if(oAuthClientCredentialAuthenticationToken != null) {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            /**
             * The scope is the application for this grant type
             */
            return oauthService.createAccessToken(oAuthClientCredentialAuthenticationToken.getClientId(), oAuthClientCredentialAuthenticationToken.getClientSecret(), grant_type, "", scope, user.getId());
        } else {
            throw new OAuthInvalidRequestException();
        }
    }

    /**
     * Action to grant access to the requesting application
     */
    @RequestMapping
    public String processAuthorization(String allow, String client_id, String redirect_uri, String requestedScope, String application, String state) throws OAuthException {

        if (allow != null) {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            List<UserAccount> accounts = userService.findSortedUserAccountsByAppType(user, AppType.CRM);
            List<String> crmAccounts = crmService.extractAppNames(accounts);

            if (crmAccounts.contains(application)) {
                String redirectUriWithCode = oauthService.createAuthorizationCode(client_id, requestedScope, application, redirect_uri, user.getId(), state);

                if (StringUtils.isNotBlank(redirectUriWithCode)) {
                    return "redirect:" + redirectUriWithCode;
                } else {
                    throw new OAuthServerErrorException();
                }
            } else {
                logger.error("User " + SecurityContextHolder.getContext().getAuthentication().getName() + " tried to parameter tamper the application scope (" + application + ").");
                throw new OAuthAccessDeniedException();
            }
        } else {
            throw new OAuthAccessDeniedException();
        }
    }

    /**
     * Allows user to view to all apps granted access to their CRM account via oauth.
     */
    @RequestMapping
    public ModelAndView manageAccounts(Long userId, Long infusionsoftAccountId) throws IOException {
        Map<String, Object> model = new HashMap<String, Object>();
        User user = userService.loadUser(userId);
        UserAccount ua = userService.findUserAccount(user, infusionsoftAccountId);
        model.put("appsGrantedAccess", oauthService.fetchUserApplicationsByUserAccount(ua));
        model.put("infusionsoftAccountId", infusionsoftAccountId);

        return new ModelAndView("oauth/manageAccounts", model);
    }

    /**
     * Allows user to revoke access to any app granted access to their CRM account via oauth.
     */
    @RequestMapping
    public ModelAndView revokeAccess(HttpServletResponse response, Long userId, Long infusionsoftAccountId, Long masheryAppId) throws IOException {
        User user = userService.loadUser(userId);
        UserAccount ua = userService.findUserAccount(user, infusionsoftAccountId);
        oauthService.revokeAccessTokensByUserAccount(ua);
//        Set<MasheryUserApplication> masheryUserApplications = oauthService.fetchUserApplicationsByUserAccount(ua);
//        for (MasheryUserApplication ma : masheryUserApplications) {
//            if (masheryAppId == Long.parseLong(ma.getId())) {
//                for (String accessToken : ma.getAccess_tokens()) {
//                    try {
//                        oauthService.revokeAccessToken(ma.getClient_id(), accessToken);
//                    } catch (Exception e) {
//                        logger.error("Failed to revoke app access for app= " + ma.getName(), e);
//                        response.sendError(500);
//                    }
//                }
//                break;
//            }
//        }

        return null;
    }

    /**
     * Admin Level User Application Searching
     *
     * @throws OAuthException
     */
    @RequestMapping
    public String userApplicationSearch(Model model, String username, String appName) throws OAuthException {
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(appName)) {
            UserAccount userAccount = userService.findUserAccountByInfusionsoftId(appName, AppType.CRM, username);
            Set<OAuthUserApplication> userApplications = oauthService.fetchUserApplicationsByUserAccount(userAccount);

            model.addAttribute("userApplications", userApplications);
            model.addAttribute("username", username);
            model.addAttribute("appName", appName);
        }

        return "oauth/userApplicationSearch";
    }

    /**
     * Admin Level Access Token Searching
     *
     * @throws OAuthException
     */
    @RequestMapping
    public String viewAccessToken(Model model, String accessToken) throws OAuthException {
        if (StringUtils.isNotBlank(accessToken)) {
            MasheryAccessToken masheryAccessToken = oauthService.fetchAccessToken(accessToken);

            model.addAttribute("masheryAccessToken", masheryAccessToken);
        }

        return "oauth/viewAccessToken";
    }

}
