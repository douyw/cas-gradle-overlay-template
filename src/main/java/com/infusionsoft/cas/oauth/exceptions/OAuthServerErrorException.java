package com.infusionsoft.cas.oauth.exceptions;

/**
 * This is an exception that is used OAuth Error Responses
 *
 * @see <a href="http://tools.ietf.org/html/draft-ietf-oauth-v2-31#section-4.1.2.1"/>
 */
public class OAuthServerErrorException extends OAuthException {

    public OAuthServerErrorException() {
        super("invalid_request");
    }

    public OAuthServerErrorException(Exception e) {
        super("super", e);
    }

}
