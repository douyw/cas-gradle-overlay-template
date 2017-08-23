package org.apereo.cas.infusionsoft.config.properties;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

public class GoogleConfigurationProperties {

    private String credentials;

    @NestedConfigurationProperty
    private GoogleServiceConfigurationProperties accountApi;

    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

    public GoogleServiceConfigurationProperties getAccountApi() {
        return accountApi;
    }

    public void setAccountApi(GoogleServiceConfigurationProperties accountApi) {
        this.accountApi = accountApi;
    }

}
