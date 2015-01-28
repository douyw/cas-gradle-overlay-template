package com.infusionsoft.cas.oauth.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

/**
 * This is an exception that is used OAuth Error Responses.
 *
 * @see <a href="http://tools.ietf.org/html/draft-ietf-oauth-v2-31#section-4.1.2.1"/>
 */
public abstract class OAuthException extends AuthenticationException {

    protected static final String DEFAULT_ERROR_DESCRIPTION = "oauth.exception.server.error";
    protected static final String DEFAULT_ERROR_URI = "https://developer.infusionsoft.com/docs/read/Getting_Started_With_OAuth2";

    protected final String errorCode;
    protected final HttpStatus httpStatus;
    protected final String errorDescription;
    protected final String errorUri;

    protected OAuthException(String errorCode, HttpStatus httpStatus) {
        super(DEFAULT_ERROR_DESCRIPTION);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.errorDescription = DEFAULT_ERROR_DESCRIPTION;
        this.errorUri = DEFAULT_ERROR_URI;
    }

    protected OAuthException(String errorCode, HttpStatus httpStatus, String errorDescription) {
        super(errorDescription);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.errorDescription = errorDescription;
        this.errorUri = DEFAULT_ERROR_URI;
    }

    protected OAuthException(String errorCode, HttpStatus httpStatus, String errorDescription, String errorUri) {
        super(errorDescription);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.errorDescription = errorDescription;
        this.errorUri = errorUri;
    }

    protected OAuthException(String errorCode, HttpStatus httpStatus, Exception e) {
        super(DEFAULT_ERROR_DESCRIPTION, e);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.errorDescription = DEFAULT_ERROR_DESCRIPTION;
        this.errorUri = DEFAULT_ERROR_URI;
    }

    protected OAuthException(String errorCode, HttpStatus httpStatus, Exception e, String errorDescription) {
        super(errorDescription, e);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.errorDescription = errorDescription;
        this.errorUri = DEFAULT_ERROR_URI;
    }

    protected OAuthException(String errorCode, HttpStatus httpStatus, Exception e, String errorDescription, String errorUri) {
        super(errorDescription, e);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.errorDescription = errorDescription;
        this.errorUri = errorUri;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public String getErrorUri() {
        return errorUri;
    }
}
