package org.apereo.cas.infusionsoft.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("host")
public class HostConfigurationProperties {
    private String domain;
    private String protocol;
    private int port = 443;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}