package org.apereo.cas.infusionsoft.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

@ConfigurationProperties("customerhub")
public class CustomerHubConfigurationProperties {

    @NestedConfigurationProperty
    private HostConfigurationProperties host = new HostConfigurationProperties();

    @NestedConfigurationProperty
    private ApiCredentialsConfigurationProperties api = new ApiCredentialsConfigurationProperties();

    public HostConfigurationProperties getHost() {
        return host;
    }

    public void setHost(HostConfigurationProperties host) {
        this.host = host;
    }

    public ApiCredentialsConfigurationProperties getApi() {
        return api;
    }

    public void setApi(ApiCredentialsConfigurationProperties api) {
        this.api = api;
    }
}