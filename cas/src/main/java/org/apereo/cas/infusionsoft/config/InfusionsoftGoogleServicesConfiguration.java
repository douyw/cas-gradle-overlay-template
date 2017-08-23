package org.apereo.cas.infusionsoft.config;

import org.apereo.cas.infusionsoft.config.properties.InfusionsoftConfigurationProperties;
import org.apereo.cas.infusionsoft.services.AccountApiService;
import org.apereo.cas.infusionsoft.services.AccountApiUserService;
import org.apereo.cas.infusionsoft.services.GoogleCredentialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("infusionsoftAccountApiConfiguration")
@EnableConfigurationProperties({InfusionsoftConfigurationProperties.class})
public class InfusionsoftGoogleServicesConfiguration {

    @Autowired
    private BuildProperties buildProperties;

    @Autowired
    private InfusionsoftConfigurationProperties infusionsoftConfigurationProperties;

    @Bean
    public AccountApiService accountApiService() {
        return new AccountApiService(buildProperties, googleCredentialService(), infusionsoftConfigurationProperties.getGoogle().getAccountApi());
    }

    @Bean
    public AccountApiUserService accountApiUserService() {
        return new AccountApiUserService(accountApiService());
    }

    @Bean
    public GoogleCredentialService googleCredentialService() {
        return new GoogleCredentialService(infusionsoftConfigurationProperties.getGoogle());
    }

}
