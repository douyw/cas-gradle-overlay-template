package org.apereo.cas.infusionsoft.config;

import org.apereo.cas.authentication.AuthenticationEventExecutionPlan;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.authentication.principal.PrincipalResolver;
import org.apereo.cas.authentication.principal.resolvers.EchoingPrincipalResolver;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.infusionsoft.authentication.InfusionsoftAuthenticationHandler;
import org.apereo.cas.infusionsoft.authentication.InfusionsoftSocialLoginPrincipalFactory;
import org.apereo.cas.infusionsoft.config.properties.InfusionsoftConfigurationProperties;
import org.apereo.cas.infusionsoft.dao.*;
import org.apereo.cas.infusionsoft.services.*;
import org.apereo.cas.infusionsoft.support.UserAccountTransformer;
import org.apereo.cas.infusionsoft.web.controllers.PasswordCheckController;
import org.apereo.cas.services.ServicesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration("infusionsoftCasConfiguration")
@EnableConfigurationProperties({CasConfigurationProperties.class, InfusionsoftConfigurationProperties.class})
public class InfusionsoftCasConfiguration implements AuthenticationEventExecutionPlanConfigurer {

    @Autowired
    private AuthorityDAO authorityDAO;

    @Autowired
    private InfusionsoftConfigurationProperties infusionsoftConfigurationProperties;

    @Autowired
    private LoginAttemptDAO loginAttemptDAO;

    @Autowired
    private MailService mailService;

    @Autowired
    private MarketingOptionsDAO marketingOptionsDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PrincipalFactory principalFactory;

    @Autowired
    @Qualifier("servicesManager")
    private ServicesManager servicesManager;

    @Autowired
    private UserAccountDAO userAccountDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private UserIdentityDAO userIdentityDAO;

    @Autowired
    private UserPasswordDAO userPasswordDAO;

    @Bean
    public UserAccountTransformer userAccountTransformer() {
        return new UserAccountTransformer(infusionsoftConfigurationProperties);
    }

    @Bean
    public PrincipalFactory clientPrincipalFactory() {
        return new InfusionsoftSocialLoginPrincipalFactory(userService());
    }

    @Bean
    InfusionsoftAuthenticationHandler infusionsoftAuthenticationHandler() {
        return new InfusionsoftAuthenticationHandler("Infusionsoft Authentication Handler", servicesManager, principalFactory, 0, infusionsoftAuthenticationService(), userService());
    }

    @Bean
    public InfusionsoftAuthenticationService infusionsoftAuthenticationService() {
        return new InfusionsoftAuthenticationServiceImpl(loginAttemptDAO, userService(), passwordService());
    }

    @Bean
    public MarketingOptionsService marketingOptionsService() {
        return new MarketingOptionsServiceImpl(marketingOptionsDAO);
    }

    @Bean
    public PasswordService passwordService() {
        return new PasswordServiceImpl(passwordEncoder, userPasswordDAO);
    }

    @RefreshScope
    @Bean
    public PrincipalResolver personDirectoryPrincipalResolver() {
        return new EchoingPrincipalResolver();
    }

    @Bean
    public UserService userService() {
        return new UserServiceImpl(userAccountTransformer(), authorityDAO, loginAttemptDAO, mailService, passwordService(), userDAO, userAccountDAO, userIdentityDAO, infusionsoftConfigurationProperties);
    }

    @Override
    public void configureAuthenticationExecutionPlan(final AuthenticationEventExecutionPlan plan) {
        plan.registerAuthenticationHandler(infusionsoftAuthenticationHandler());
    }

}
