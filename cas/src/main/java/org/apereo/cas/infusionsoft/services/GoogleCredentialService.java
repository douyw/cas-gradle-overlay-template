package org.apereo.cas.infusionsoft.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

class GoogleCredentialService {

    private static final Logger log = LoggerFactory.getLogger(GoogleCredentialService.class);

    @Value("${infusionsoft.cas.google.credentials}")
    private String credentials;

    protected GoogleCredential getGoogleCredential() {
        InputStream credentialsInputStream = new ByteArrayInputStream(Base64.decodeBase64(credentials));

        GoogleCredential googleCredential = null;
        try {
            googleCredential = GoogleCredential.fromStream(credentialsInputStream);
        } catch (IOException e) {
            log.error("Google Cloud authorization failed.", e);
            throw new RuntimeException(e);
        }

        return googleCredential;
    }

}
