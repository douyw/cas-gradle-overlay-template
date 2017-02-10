package org.apereo.cas.infusionsoft.services;

import org.apereo.cas.infusionsoft.config.properties.CustomerHubConfigurationProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Service for communicating back and forth with CustomerHub.
 */
@Service
public class CustomerHubService {
    private static final Logger log = LoggerFactory.getLogger(CustomerHubService.class);

    @Autowired
    private CustomerHubConfigurationProperties customerHubConfigurationProperties;

    private DefaultHttpClient customerHubHttpClient;

    /**
     * Builds a URL where users of this app should be sent after login.
     */
    public String buildUrl(String appName) {
        return buildBaseUrl(appName) + "/admin";
    }

    /**
     * Builds a base URL for web services calls and what-not.
     */
    public String buildBaseUrl(String appName) {
        StringBuilder baseUrl = new StringBuilder(customerHubConfigurationProperties.getHost().getProtocol() + "://" + appName + "." + customerHubConfigurationProperties.getHost().getDomain());

        if (StringUtils.equals("http", customerHubConfigurationProperties.getHost().getProtocol()) && customerHubConfigurationProperties.getHost().getPort() != 80) {
            baseUrl.append(":").append(customerHubConfigurationProperties.getHost().getPort());
        } else if (StringUtils.equals("https", customerHubConfigurationProperties.getHost().getProtocol()) && customerHubConfigurationProperties.getHost().getPort() != 443) {
            baseUrl.append(":").append(customerHubConfigurationProperties.getHost().getPort());
        }

        return baseUrl.toString();
    }

    /**
     * Authenticates a user with CustomerHub.
     */
    public boolean authenticateUser(String appName, String appUsername, String appPassword) {
        try {
            HttpClient client = getHttpClient(appName);
            HttpPost post = new HttpPost(buildBaseUrl(appName) + "/account/authenticate_user");
            StringEntity input = new StringEntity(createAuthenticateUserRequest(appUsername, appPassword));

            post.addHeader("Accept", "application/json");
            post.setHeader("Content-Type", "application/json");
            input.setContentType("application/json");
            post.setEntity(input);

            log.debug("authenticating CustomerHub user " + appUsername + " at " + post.getURI());

            HttpResponse response = client.execute(post);
            int status = response.getStatusLine().getStatusCode();

            log.debug("authentication of CustomerHub user returned status " + status);

            EntityUtils.consume(response.getEntity());

            return status == 200;
        } catch (Exception e) {
            log.error("failed to authenticate CustomerHub user", e);
        }

        return false;
    }

    /**
     * Fetches a URL to a custom logo for a CustomerHub instance, or null if such a logo is unavailable.
     */
    public String getLogoUrl(String appName) {
        try {
            HttpClient client = getHttpClient(appName);
            HttpGet get = new HttpGet(buildBaseUrl(appName) + "/account/logo");

            get.addHeader("Accept", "application/json");
            get.addHeader("Content-Type", "application/json");

            log.debug("getting CustomerHub logo at " + get.getURI());

            HttpResponse response = client.execute(get);
            int status = response.getStatusLine().getStatusCode();

            log.debug("logo request returned status " + status);

            if (status == 200) {
                return parseLogoResponse(response.getEntity().getContent());
            } else {
                EntityUtils.consume(response.getEntity());
            }
        } catch (Exception e) {
            log.error("failed to authenticate CustomerHub user", e);
        }

        return null;
    }

    private synchronized HttpClient getHttpClient(String appName) {
        if (customerHubHttpClient == null) {
            customerHubHttpClient = new DefaultHttpClient();
        }

        customerHubHttpClient.getCredentialsProvider().setCredentials(new AuthScope(appName + "." + customerHubConfigurationProperties.getHost().getDomain(), customerHubConfigurationProperties.getHost().getPort()), new UsernamePasswordCredentials(customerHubConfigurationProperties.getApi().getUsername(), customerHubConfigurationProperties.getApi().getPassword()));

        return customerHubHttpClient;
    }

    private String parseLogoResponse(InputStream input) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(new InputStreamReader(input, "UTF-8"));

        return (String) json.get("logo");
    }

    private String createAuthenticateUserRequest(String appUsername, String appPassword) {
        JSONObject request = new JSONObject();

        request.put("email", appUsername);
        request.put("password", appPassword);

        log.debug("authentication request " + request.toJSONString());

        return request.toJSONString();
    }
}