package org.apereo.cas.infusionsoft.web.controllers;

import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.proc.BadJWEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.apereo.cas.api.APIErrorDTO;
import org.apereo.cas.token.cipher.TokenTicketCipherExecutor;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.extractor.HeaderExtractor;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.HttpAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.Locale;
import java.util.Optional;

/**
 * Really simple controller to allow authentication.
 */
@RestController
@RequestMapping(value = "/jwt")
public class JwtController {
    private static final Logger log = LoggerFactory.getLogger(JwtController.class);

    private MessageSource messageSource;

    private TokenTicketCipherExecutor tokenTicketCipherExecutor;

    public JwtController(TokenTicketCipherExecutor tokenTicketCipherExecutor, MessageSource messageSource) {
        this.tokenTicketCipherExecutor = tokenTicketCipherExecutor;
        this.messageSource = messageSource;
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity decrypt(HttpServletRequest request, HttpServletResponse response, Locale locale) throws CredentialsException, HttpAction {
        J2EContext context = new J2EContext(request, response);

        try {
            HeaderExtractor headerExtractor = new HeaderExtractor("Authorization", "Bearer ", "(CAS) JWT Validator");
            TokenCredentials tokenCredentials = headerExtractor.extract(context);

            if (tokenCredentials != null) {
                String decrypted = tokenTicketCipherExecutor.decode(tokenCredentials.getToken());

                if(decrypted != null) {
                    JWTClaimsSet jwtClaimsSet = JWTClaimsSet.parse(decrypted);

                    DefaultJWTClaimsVerifier defaultJWTClaimsVerifier = new DefaultJWTClaimsVerifier();
                    defaultJWTClaimsVerifier.verify(jwtClaimsSet);

                    return new ResponseEntity<>(decrypted, HttpStatus.OK);
                } else {
                    throw new BadJWEException("Invalid JWE Token");
                }
            } else {
                throw new BadJWEException("Invalid JWE Token");
            }
        } catch (CredentialsException | IllegalArgumentException | BadJWEException | BadJWTException | ParseException e) {
            return logAndReturnError(e, "cas.exception.jwt.invalid", locale, HttpStatus.UNAUTHORIZED);
        }

    }

    private ResponseEntity<APIErrorDTO> logAndReturnError(Exception e, String code, Locale locale, HttpStatus httpStatus) {
        String errorMessage = messageSource.getMessage(code, null, locale);
        log.error(errorMessage, e);
        return new ResponseEntity<>(new APIErrorDTO(code, errorMessage), httpStatus);
    }

}
