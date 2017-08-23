package org.apereo.cas.infusionsoft.services;

import com.google.common.base.Preconditions;
import com.infusionsoft.account.sdk.*;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.infusionsoft.config.properties.GoogleServiceConfigurationProperties;
import org.springframework.boot.info.BuildProperties;

public class AccountApiService {

    private static final String CALLER_NAME = "CAS";

    private final AccountApiStandaloneModule accountApiStandaloneModule;

    public AccountApiService(BuildProperties buildProperties, GoogleCredentialService googleCredentialService, GoogleServiceConfigurationProperties accountApiConfigurationProperties) {
        Preconditions.checkNotNull(buildProperties);
        Preconditions.checkNotNull(googleCredentialService);
        final String environment = accountApiConfigurationProperties.getEnv();

        if (StringUtils.equalsIgnoreCase(environment, "local")) {
            accountApiStandaloneModule = AccountApiStandaloneModule.configuredLocalModule(googleCredentialService.getCredential(), CALLER_NAME, buildProperties.getVersion(), "");
        } else {
            accountApiStandaloneModule = AccountApiStandaloneModule.configuredModule(googleCredentialService.getCredential(), environment, CALLER_NAME, buildProperties.getVersion(), "");
        }
    }

    public AccountApi getAccountApi() {
        return accountApiStandaloneModule.accountApi();
    }

    public EditionApi getEditionApi() {
        return accountApiStandaloneModule.editionApi();
    }

    public OrganizationApi getOrganizationApi() {
        return accountApiStandaloneModule.organizationApi();
    }

    public OrganizationUserApi getOrganizationUserApi() {
        return accountApiStandaloneModule.organizationUserApi();
    }

    public UserApi getUserApi() {
        return accountApiStandaloneModule.userApi();
    }

    public UserAccountApi getUserAccountApi() {
        return accountApiStandaloneModule.userAccountApi();
    }

}
