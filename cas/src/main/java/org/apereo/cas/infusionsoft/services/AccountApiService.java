package org.apereo.cas.infusionsoft.services;

import com.infusionsoft.account.sdk.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class AccountApiService {
    @Value("${infusionsoft.account.api.env}")
    private String environment;

    @Autowired
    private GoogleCredentialService googleCredentialService;

    @Autowired
    private BuildProperties buildProperties;

    protected AccountApiStandaloneModule accountApiStandaloneModule;

    @PostConstruct
    public void init() {
        if (StringUtils.equalsIgnoreCase(environment, "local")) {
            accountApiStandaloneModule = AccountApiStandaloneModule.configuredLocalModule(googleCredentialService.getGoogleCredential(),"CAS", buildProperties.getVersion(),"");
        } else {
            accountApiStandaloneModule = AccountApiStandaloneModule.configuredModule(googleCredentialService.getGoogleCredential(), environment, buildProperties.getVersion(),"");
        }
    }

    //public AccountApi getAccountApi() { return accountApiStandaloneModule.accountApi(); }

    // public EditionApi getEditionApi() { return accountApiStandaloneModule.editionApi(); }

    //public OrganizationApi getOrganizationApi() { return accountApiStandaloneModule.organizationApi(); }

    //public OrganizationUserApi getOrganizationUserApi() { return accountApiStandaloneModule.organizationUserApi(); }

    public UserApi getUserApi() {
        return accountApiStandaloneModule.userApi();
    }

    //public UserAccountApi getUserAccountApi() { return accountApiStandaloneModule.userAccountApi(); }
}
