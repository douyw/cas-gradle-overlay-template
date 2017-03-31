package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.domain.OAuthClient;
import com.infusionsoft.cas.domain.OAuthServiceConfig;
import com.infusionsoft.cas.oauth.exceptions.OAuthAccessDeniedException;
import com.infusionsoft.cas.oauth.exceptions.OAuthInvalidClientException;
import com.infusionsoft.cas.oauth.exceptions.OAuthInvalidRequestException;
import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import com.infusionsoft.cas.services.OAuthClientService;
import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class OAuthTicketGrantingTicketTokenProviderTest {

    @InjectMocks
    private OAuthTicketGrantingTicketTokenProvider tokenProviderToTest = new OAuthTicketGrantingTicketTokenProvider();

    @Mock
    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    @Mock
    private OAuthClientService oAuthClientService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private TicketGrantingTicket ticketGrantingTicket;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final OAuthClient oAuthClient = new OAuthClient();
    private static final String scope = "scope";
    private static final String application = "application";
    private static final String grantType = "urn:infusionsoft:params:oauth:grant-type:ticket-granting-ticket";
    private static final OAuthServiceConfig oAuthServiceConfig = new OAuthServiceConfig();
    private static final String clientId = "clientId";
    private static final String clientSecret = "clientSecret";
    private static final String originHeaderValue = "originHeaderValue";
    private static final Cookie userTrackingCookie = new Cookie("userUUID", UUID.randomUUID().toString());
    private static final Cookie[] cookies = new Cookie[]{userTrackingCookie};
    private static final String ORIGIN_HEADER = "Origin";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSuccessNoCookie() throws Exception {
        doReturn(ticketGrantingTicket).when(infusionsoftAuthenticationService).getTicketGrantingTicket(request);
        doReturn(oAuthClient).when(oAuthClientService).loadOAuthClient(clientId);
        doReturn(originHeaderValue).when(request).getHeader(ORIGIN_HEADER);
        doReturn(new Cookie[0]).when(request).getCookies();
        doReturn(true).when(oAuthClientService).isOriginAllowedByOAuthClient(oAuthClient, originHeaderValue);

        OAuthTicketGrantingTicketAuthenticationToken actualReturn = tokenProviderToTest.createAuthenticationToken(request, response, scope, application, grantType, oAuthServiceConfig, clientId, clientSecret);

        String expectedCookieValue = validateCookie(true);
        validateCorsHeader(true);
        validateToken(actualReturn, expectedCookieValue, false);
    }

    @Test
    public void testSuccessHadCookie() throws Exception {
        doReturn(ticketGrantingTicket).when(infusionsoftAuthenticationService).getTicketGrantingTicket(request);
        doReturn(oAuthClient).when(oAuthClientService).loadOAuthClient(clientId);
        doReturn(originHeaderValue).when(request).getHeader(ORIGIN_HEADER);
        doReturn(cookies).when(request).getCookies();
        doReturn(true).when(oAuthClientService).isOriginAllowedByOAuthClient(oAuthClient, originHeaderValue);

        OAuthTicketGrantingTicketAuthenticationToken actualReturn = tokenProviderToTest.createAuthenticationToken(request, response, scope, application, grantType, oAuthServiceConfig, clientId, clientSecret);

        String expectedCookieValue = validateCookie(false);
        validateCorsHeader(true);
        validateToken(actualReturn, expectedCookieValue, false);
    }

    @Test
    public void testSuccessAnonymousNoCookie() throws Exception {
        doReturn(null).when(infusionsoftAuthenticationService).getTicketGrantingTicket(request);
        doReturn(oAuthClient).when(oAuthClientService).loadOAuthClient(clientId);
        doReturn(originHeaderValue).when(request).getHeader(ORIGIN_HEADER);
        doReturn(new Cookie[0]).when(request).getCookies();
        doReturn(true).when(oAuthClientService).isOriginAllowedByOAuthClient(oAuthClient, originHeaderValue);

        OAuthTicketGrantingTicketAuthenticationToken actualReturn = tokenProviderToTest.createAuthenticationToken(request, response, scope, application, grantType, oAuthServiceConfig, clientId, clientSecret);

        String expectedCookieValue = validateCookie(true);
        validateCorsHeader(true);
        validateToken(actualReturn, expectedCookieValue, true);
    }

    @Test
    public void testSuccessAnonymousHadCookie() throws Exception {
        doReturn(null).when(infusionsoftAuthenticationService).getTicketGrantingTicket(request);
        doReturn(oAuthClient).when(oAuthClientService).loadOAuthClient(clientId);
        doReturn(originHeaderValue).when(request).getHeader(ORIGIN_HEADER);
        doReturn(cookies).when(request).getCookies();
        doReturn(true).when(oAuthClientService).isOriginAllowedByOAuthClient(oAuthClient, originHeaderValue);

        OAuthTicketGrantingTicketAuthenticationToken actualReturn = tokenProviderToTest.createAuthenticationToken(request, response, scope, application, grantType, oAuthServiceConfig, clientId, clientSecret);

        String expectedCookieValue = validateCookie(false);
        validateCorsHeader(true);
        validateToken(actualReturn, expectedCookieValue, true);
    }

    @Test
    public void testBadGrantType() throws Exception {
        doReturn(ticketGrantingTicket).when(infusionsoftAuthenticationService).getTicketGrantingTicket(request);
        doReturn(oAuthClient).when(oAuthClientService).loadOAuthClient(clientId);
        doReturn(originHeaderValue).when(request).getHeader(ORIGIN_HEADER);
        doReturn(new Cookie[0]).when(request).getCookies();
        doReturn(true).when(oAuthClientService).isOriginAllowedByOAuthClient(oAuthClient, originHeaderValue);

        OAuthTicketGrantingTicketAuthenticationToken actualReturn = tokenProviderToTest.createAuthenticationToken(request, response, scope, application, "Bad grantType", oAuthServiceConfig, clientId, clientSecret);

        validateCookie(false);
        validateCorsHeader(false);
        Assert.assertNull(actualReturn);
    }

    @Test
    public void testMissingClientId() throws Exception {
        doReturn(ticketGrantingTicket).when(infusionsoftAuthenticationService).getTicketGrantingTicket(request);
        doReturn(oAuthClient).when(oAuthClientService).loadOAuthClient(clientId);
        doReturn(originHeaderValue).when(request).getHeader(ORIGIN_HEADER);
        doReturn(new Cookie[0]).when(request).getCookies();
        doReturn(true).when(oAuthClientService).isOriginAllowedByOAuthClient(oAuthClient, originHeaderValue);

        thrown.expect(OAuthInvalidRequestException.class);
        thrown.expectMessage("oauth.exception.clientId.missing");

        tokenProviderToTest.createAuthenticationToken(request, response, scope, application, grantType, oAuthServiceConfig, null, clientSecret);
    }

    @Test
    public void testBadClientId() throws Exception {
        doReturn(ticketGrantingTicket).when(infusionsoftAuthenticationService).getTicketGrantingTicket(request);
        doReturn(null).when(oAuthClientService).loadOAuthClient(clientId);
        doReturn(originHeaderValue).when(request).getHeader(ORIGIN_HEADER);
        doReturn(new Cookie[0]).when(request).getCookies();
        doReturn(true).when(oAuthClientService).isOriginAllowedByOAuthClient(oAuthClient, originHeaderValue);

        thrown.expect(OAuthInvalidClientException.class);
        thrown.expectMessage("oauth.exception.invalid.client");

        tokenProviderToTest.createAuthenticationToken(request, response, scope, application, grantType, oAuthServiceConfig, clientId, clientSecret);
    }

    @Test
    public void testOriginNotAllowedForClientId() throws Exception {
        doReturn(ticketGrantingTicket).when(infusionsoftAuthenticationService).getTicketGrantingTicket(request);
        doReturn(oAuthClient).when(oAuthClientService).loadOAuthClient(clientId);
        doReturn(originHeaderValue).when(request).getHeader(ORIGIN_HEADER);
        doReturn(new Cookie[0]).when(request).getCookies();
        doReturn(false).when(oAuthClientService).isOriginAllowedByOAuthClient(oAuthClient, originHeaderValue);

        thrown.expect(OAuthAccessDeniedException.class);
        thrown.expectMessage("oauth.exception.origin.not.authorized");

        tokenProviderToTest.createAuthenticationToken(request, response, scope, application, grantType, oAuthServiceConfig, clientId, clientSecret);
    }

    @Test
    public void testNoOriginHeader() throws Exception {
        doReturn(ticketGrantingTicket).when(infusionsoftAuthenticationService).getTicketGrantingTicket(request);
        doReturn(oAuthClient).when(oAuthClientService).loadOAuthClient(clientId);
        doReturn(null).when(request).getHeader(ORIGIN_HEADER);
        doReturn(new Cookie[0]).when(request).getCookies();
        doReturn(true).when(oAuthClientService).isOriginAllowedByOAuthClient(oAuthClient, originHeaderValue);

        thrown.expect(OAuthInvalidRequestException.class);
        thrown.expectMessage("oauth.exception.origin.missing");

        tokenProviderToTest.createAuthenticationToken(request, response, scope, application, grantType, oAuthServiceConfig, clientId, clientSecret);
    }

    @Test
    public void testBlankOriginHeader() throws Exception {
        doReturn(ticketGrantingTicket).when(infusionsoftAuthenticationService).getTicketGrantingTicket(request);
        doReturn(oAuthClient).when(oAuthClientService).loadOAuthClient(clientId);
        doReturn(" ").when(request).getHeader(ORIGIN_HEADER);
        doReturn(new Cookie[0]).when(request).getCookies();
        doReturn(true).when(oAuthClientService).isOriginAllowedByOAuthClient(oAuthClient, originHeaderValue);

        thrown.expect(OAuthInvalidRequestException.class);
        thrown.expectMessage("oauth.exception.origin.missing");

        tokenProviderToTest.createAuthenticationToken(request, response, scope, application, grantType, oAuthServiceConfig, clientId, clientSecret);
    }

    @Test
    public void testInvalidGrantType() {
        OAuthTicketGrantingTicketAuthenticationToken authToken = tokenProviderToTest.createAuthenticationToken(request, response, scope, application, "invalid_grant_type", null, clientId, clientSecret);
        Assert.assertTrue(authToken == null);
    }

    private void validateCorsHeader(boolean shouldExist) {
        if (shouldExist) {
            verify(response, times(1)).setHeader("Access-Control-Allow-Origin", originHeaderValue);
            verify(response, times(1)).setHeader("Access-Control-Allow-Credentials", "true");
            verify(response, times(1)).setHeader("Access-Control-Allow-Methods", "POST");
            verify(response, times(1)).setHeader("Access-Control-Allow-Headers", "Content-Type, Authentication");
            verify(response, times(1)).setHeader("Access-Control-Max-Age", "0");
        } else {
            verify(response, never()).setHeader(anyString(), anyString());
        }
    }

    private String validateCookie(boolean shouldBeCreated) {
        String expectedCookieValue = null;
        if (shouldBeCreated) {
            ArgumentCaptor<Cookie> cookieCapture = ArgumentCaptor.forClass(Cookie.class);
            verify(response, times(1)).addCookie(cookieCapture.capture());
            Cookie actualCookie = cookieCapture.getValue();
            Assert.assertEquals(actualCookie.getName(), userTrackingCookie.getName());
            Assert.assertEquals(actualCookie.getMaxAge(), Integer.MAX_VALUE);
            expectedCookieValue = actualCookie.getValue();
            Assert.assertTrue(StringUtils.isNotBlank(expectedCookieValue));
        } else {
            verify(response, never()).addCookie(any(Cookie.class));
            expectedCookieValue = userTrackingCookie.getValue();
        }
        return expectedCookieValue;
    }

    private void validateToken(OAuthTicketGrantingTicketAuthenticationToken actualToken, String expectedCookieValue, boolean shouldBeAnonymous) {
        OAuthTicketGrantingTicketAuthenticationToken expectedToken = new OAuthTicketGrantingTicketAuthenticationToken(oAuthServiceConfig, clientId, oAuthClient.getClientSecret(), scope, grantType, application, expectedCookieValue, shouldBeAnonymous ? null : ticketGrantingTicket);

        Assert.assertEquals(expectedToken.getPrincipal(), actualToken.getPrincipal());
        Assert.assertEquals(expectedToken.getServiceConfig(), actualToken.getServiceConfig());
        Assert.assertEquals(expectedToken.getClientId(), actualToken.getClientId());
        Assert.assertEquals(expectedToken.getClientSecret(), actualToken.getClientSecret());
        Assert.assertEquals(expectedToken.getScope(), actualToken.getScope());
        Assert.assertEquals(expectedToken.getGrantType(), actualToken.getGrantType());
        Assert.assertEquals(expectedToken.getApplication(), actualToken.getApplication());
        Assert.assertEquals(expectedToken.getTrackingUUID(), actualToken.getTrackingUUID());
        Assert.assertEquals(expectedToken.getCredentials(), actualToken.getCredentials());
        Assert.assertEquals(expectedToken.getTicketGrantingTicket(), actualToken.getTicketGrantingTicket());
    }

}
