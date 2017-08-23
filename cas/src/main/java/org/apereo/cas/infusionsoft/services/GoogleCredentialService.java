package org.apereo.cas.infusionsoft.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import org.apache.commons.codec.binary.Base64;
import org.apereo.cas.infusionsoft.config.properties.GoogleConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class GoogleCredentialService {

    private static final Logger log = LoggerFactory.getLogger(GoogleCredentialService.class);

    private final GoogleCredential credential;

    public GoogleCredentialService(GoogleConfigurationProperties googleConfigurationProperties) {
        this.credential = unpackGoogleCredential(googleConfigurationProperties.getCredentials());
    }

    private static GoogleCredential unpackGoogleCredential(String credential) {
        InputStream credentialsInputStream = new ByteArrayInputStream(Base64.decodeBase64(credential));

        GoogleCredential googleCredential = null;
        try {
            googleCredential = GoogleCredential.fromStream(credentialsInputStream);
        } catch (IOException e) {
            log.error("Google Cloud authorization failed.", e);
            throw new RuntimeException("Invalid Google service account credentials", e);
        }

        return googleCredential;
    }

    public GoogleCredential getCredential() {
        return credential;
    }

}
