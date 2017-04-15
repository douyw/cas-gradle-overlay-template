package org.apereo.cas.infusionsoft.config;

import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.infusionsoft.config.properties.InfusionsoftConfigurationProperties;
import org.apereo.cas.infusionsoft.services.BuildServiceImpl;
import org.apereo.cas.infusionsoft.services.InfusionsoftAuthenticationService;
import org.apereo.cas.infusionsoft.services.MarketingOptionsService;
import org.apereo.cas.infusionsoft.support.AppHelper;
import org.apereo.cas.infusionsoft.webflow.InfusionsoftFlowSetupAction;
import org.apereo.cas.infusionsoft.webflow.InfusionsoftPasswordExpirationEnforcementAction;
import org.apereo.cas.infusionsoft.webflow.InfusionsoftWebflowConfigurer;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.ticket.registry.TicketRegistrySupport;
import org.apereo.cas.web.flow.CasWebflowConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

@Configuration("infusionsoftWebflowConfiguration")
@EnableConfigurationProperties({InfusionsoftConfigurationProperties.class})
public class InfusionsoftWebflowConfiguration {

    @Autowired
    private AppHelper appHelper;

    @Autowired
    @Qualifier("defaultAuthenticationSystemSupport")
    private AuthenticationSystemSupport authenticationSystemSupport;

    @Autowired
    private BuildServiceImpl buildService;

    @Autowired
    @Qualifier("centralAuthenticationService")
    private CentralAuthenticationService centralAuthenticationService;

    @Autowired
    private FlowBuilderServices flowBuilderServices;

    @Autowired
    @Qualifier("loginFlowRegistry")
    private FlowDefinitionRegistry loginFlowDefinitionRegistry;

    @Autowired
    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    @Autowired
    private InfusionsoftConfigurationProperties infusionsoftConfigurationProperties;

    @Autowired
    private MarketingOptionsService marketingOptionsService;

    @Autowired
    private ServicesManager servicesManager;

    @Autowired
    @Qualifier("defaultTicketRegistrySupport")
    private TicketRegistrySupport ticketRegistrySupport;

    @Bean
    public CasWebflowConfigurer infusionsoftWebflowConfigurer() {
        return new InfusionsoftWebflowConfigurer(flowBuilderServices, loginFlowDefinitionRegistry, infusionsoftFlowSetupAction(), infusionsoftPasswordExpirationEnforcementAction());
    }

    @Bean
    public InfusionsoftFlowSetupAction infusionsoftFlowSetupAction() {
        return new InfusionsoftFlowSetupAction(
                appHelper,
                buildService,
                infusionsoftAuthenticationService,
                marketingOptionsService,
                servicesManager,
                infusionsoftConfigurationProperties.getSupportPhoneNumbers());
    }

    @Bean
    public InfusionsoftPasswordExpirationEnforcementAction infusionsoftPasswordExpirationEnforcementAction() {
        return new InfusionsoftPasswordExpirationEnforcementAction(
                authenticationSystemSupport,
                centralAuthenticationService,
                ticketRegistrySupport,
                servicesManager);
    }

}
