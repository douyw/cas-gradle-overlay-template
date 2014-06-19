package com.infusionsoft.cas.oauth.services;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.oauth.dto.OAuthAccessToken;
import com.infusionsoft.cas.oauth.dto.OAuthApplication;
import com.infusionsoft.cas.oauth.mashery.api.client.MasheryApiClientService;
import com.infusionsoft.cas.oauth.mashery.api.domain.*;
import com.infusionsoft.cas.services.CrmService;
import com.infusionsoft.cas.services.UserService;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.modules.testng.PowerMockObjectFactory;
import org.springframework.security.access.AccessDeniedException;
import org.testng.Assert;
import org.testng.IObjectFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class OAuthServiceTest {

    private static final String TEST_APP = "testApp";
    private static final String TEST_APP_HOST_NAME = "testApp.infusionsoft.com";
    private static final String TEST_INVALID_APP_HOST_NAME = "invalid.infusionsoft.com";
    private static final String TEST_STATE = "testState";
    private static final Long TEST_GLOBAL_USER_ID = 1L;
    private static final String TEST_USERNAME = "jojo@infusionsoft.com";
    private static final String TEST_USER_CONTEXT = TEST_GLOBAL_USER_ID + "|" + TEST_APP_HOST_NAME;
    private static final String TOKEN_1 = "token1";
    private static final String TOKEN_2 = "token2";
    private static final String TOKEN_3 = "token3";
    private static final String CLIENT_ID = "client_id";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String TEST_REDIRECT_URI = "http://redirect.com";

    private OAuthService oAuthServiceToTest;
    private MasheryApiClientService masheryApiClientService;
    private CrmService crmService;
    private UserService userService;

    private Set<MasheryUserApplication> testAppSet;
    private UserAccount testAccount;

    @ObjectFactory
    public IObjectFactory getObjectFactory() {
        return new PowerMockObjectFactory();
    }

    @BeforeMethod
    public void beforeMethod() {
        masheryApiClientService = mock(MasheryApiClientService.class);

        crmService = mock(CrmService.class);
        when(crmService.buildCrmHostName(TEST_APP)).thenReturn(TEST_APP_HOST_NAME);

        userService = mock(UserService.class);
        doNothing().when(userService).validateUserApplication(TEST_APP_HOST_NAME);
        doThrow(new AccessDeniedException("access denied")).when(userService).validateUserApplication(TEST_INVALID_APP_HOST_NAME);

        createTestApps();

        oAuthServiceToTest = new OAuthService();

        Whitebox.setInternalState(oAuthServiceToTest, "masheryApiClientService", masheryApiClientService);
        Whitebox.setInternalState(oAuthServiceToTest, "crmService", crmService);
        Whitebox.setInternalState(oAuthServiceToTest, "userService", userService);

        setupUserAccount();
    }

    private void createTestApps() {
        testAppSet = new HashSet<MasheryUserApplication>();
        MasheryUserApplication app = new MasheryUserApplication();
        app.setName("ACME");
        app.setClient_id(CLIENT_ID);
        Set<String> accessTokens = new HashSet<String>();
        Set<MasheryAccessToken> masheryAccessTokens = new HashSet<MasheryAccessToken>();

        MasheryAccessToken masheryAccessToken1 = new MasheryAccessToken();
        masheryAccessToken1.setToken(TOKEN_1);
        masheryAccessTokens.add(masheryAccessToken1);

        MasheryAccessToken masheryAccessToken2 = new MasheryAccessToken();
        masheryAccessToken2.setToken(TOKEN_2);
        masheryAccessTokens.add(masheryAccessToken2);

        MasheryAccessToken masheryAccessToken3 = new MasheryAccessToken();
        masheryAccessToken3.setToken(TOKEN_3);
        masheryAccessTokens.add(masheryAccessToken3);

        accessTokens.add(TOKEN_1);
        accessTokens.add(TOKEN_2);
        accessTokens.add(TOKEN_3);

        app.setTokens(masheryAccessTokens);
        app.setAccess_tokens(accessTokens);

        testAppSet.add(app);
    }

    private void setupUserAccount() {
        testAccount = new UserAccount();
        testAccount.setAppName(TEST_APP);
        User user = new User();
        user.setUsername(TEST_USERNAME);
        user.setId(TEST_GLOBAL_USER_ID);
        testAccount.setUser(user);
    }

    @Test
    public void testFetchOAuthApplication() throws Exception {
        final MasheryOAuthApplication masheryOAuthApplication = new MasheryOAuthApplication();
        masheryOAuthApplication.setId(1);
        when(masheryApiClientService.fetchOAuthApplication(anyString(), anyString(), anyString())).thenReturn(masheryOAuthApplication);

        final MasheryApplication masheryApplication = new MasheryApplication();
        masheryApplication.setDescription("description");
        masheryApplication.setName("name");
        masheryApplication.setUsername("username");
        when(masheryApiClientService.fetchApplication(1)).thenReturn(masheryApplication);

        final MasheryMember masheryMember = new MasheryMember();
        masheryMember.setDisplayName("display name");
        when(masheryApiClientService.fetchMember("username")).thenReturn(masheryMember);

        // verify result
        final String responseType = "responseType";
        OAuthApplication oAuthApplication = oAuthServiceToTest.fetchApplication(CLIENT_ID, TEST_REDIRECT_URI, responseType);
        Assert.assertEquals(oAuthApplication.getDescription(), masheryApplication.getDescription());
        Assert.assertEquals(oAuthApplication.getDevelopedBy(), masheryMember.getDisplayName());
        Assert.assertEquals(oAuthApplication.getName(), masheryApplication.getName());

        // verify what happened for call to Mashery
        verify(masheryApiClientService, times(1)).fetchOAuthApplication(CLIENT_ID, TEST_REDIRECT_URI, responseType);
    }

    @Test
    public void testCreateAuthorizationCode() throws Exception {
        String code = "code";

        final MasheryUri masheryUri = new MasheryUri();
        masheryUri.setState("state");
        masheryUri.setUri("https://redirect.com?code=" + code);

        final MasheryAuthorizationCode masheryAuthorizationCodeInput = new MasheryAuthorizationCode();
        masheryAuthorizationCodeInput.setCode(code);
        masheryAuthorizationCodeInput.setUri(masheryUri);

        when(masheryApiClientService.createAuthorizationCode(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(masheryAuthorizationCodeInput);

        // verify result
        final String requestedScope = "requestedScope";
        String authorizationCode = oAuthServiceToTest.createAuthorizationCode(CLIENT_ID, requestedScope, TEST_APP_HOST_NAME, TEST_REDIRECT_URI, TEST_GLOBAL_USER_ID, TEST_STATE);
        Assert.assertEquals(authorizationCode, masheryAuthorizationCodeInput.getUri().getUri());

        // verify what happened for call to Mashery
        verify(userService, times(1)).validateUserApplication(TEST_APP_HOST_NAME);
        verify(masheryApiClientService, times(1)).createAuthorizationCode(CLIENT_ID, requestedScope + "|" + TEST_APP_HOST_NAME, TEST_REDIRECT_URI, TEST_USER_CONTEXT, TEST_STATE);
    }

    @Test(expectedExceptions = AccessDeniedException.class)
    public void testCreateAuthorizationCodeWithInvalidApplication() throws Exception {
        oAuthServiceToTest.createAuthorizationCode(CLIENT_ID, "requestedScope", TEST_INVALID_APP_HOST_NAME, TEST_REDIRECT_URI, TEST_GLOBAL_USER_ID, TEST_STATE);
    }

    @Test
    public void testRevokeAccessToken() throws Exception {
        when(masheryApiClientService.revokeAccessToken(anyString(), anyString())).thenReturn(true);

        // verify result
        Boolean wasSuccessful = oAuthServiceToTest.revokeAccessToken(CLIENT_ID, ACCESS_TOKEN);
        Assert.assertTrue(wasSuccessful);

        // verify what happened for call to Mashery
        verify(masheryApiClientService, times(1)).revokeAccessToken(CLIENT_ID, ACCESS_TOKEN);
    }

    @Test
    public void testFetchAccessToken() throws Exception {
        final MasheryAccessToken masheryAccessTokenInput = new MasheryAccessToken();
        masheryAccessTokenInput.setClient_id("client_id");
        masheryAccessTokenInput.setExpires(1000);
        masheryAccessTokenInput.setExtended("extended");
        masheryAccessTokenInput.setGrant_type("grant_type");
        masheryAccessTokenInput.setScope("scope");
        masheryAccessTokenInput.setToken("token");
        masheryAccessTokenInput.setUser_context("user_context");
        when(masheryApiClientService.fetchAccessToken(anyString())).thenReturn(masheryAccessTokenInput);

        // verify result
        OAuthAccessToken oAuthAccessToken = oAuthServiceToTest.fetchAccessToken(ACCESS_TOKEN);
        Assert.assertEquals(oAuthAccessToken.getAccessToken(), masheryAccessTokenInput.getToken());
        Assert.assertEquals(oAuthAccessToken.getExpiresIn(), masheryAccessTokenInput.getExpires());
        Assert.assertNull(oAuthAccessToken.getRefreshToken());
        Assert.assertEquals(oAuthAccessToken.getScope(), masheryAccessTokenInput.getScope());
        Assert.assertEquals(oAuthAccessToken.getTokenType(), masheryAccessTokenInput.getToken_type());

        // verify what happened for call to Mashery
        verify(masheryApiClientService, times(1)).fetchAccessToken(ACCESS_TOKEN);
    }

//    @Test
//    public void testFetchUserApplicationsByUserAccount() throws Exception {
//        when(masheryApiClientService.fetchUserApplicationsByUserContext(anyString(), any(TokenStatus.class))).thenReturn(testAppSet);
//
//        // verify result
//        Set<MasheryUserApplication> oauthAppsOutput = oAuthServiceToTest.fetchUserApplicationsByUserAccount(testAccount);
//        Assert.assertSame(oauthAppsOutput, testAppSet);
//
//        // verify what happened for call to Mashery
//        verify(masheryApiClientService, times(1)).fetchUserApplicationsByUserContext(TEST_USER_CONTEXT, TokenStatus.Active);
//    }

    @Test
    public void testRevokeAccessTokensByUserAccount() throws Exception {
        when(masheryApiClientService.fetchUserApplicationsByUserContext(anyString(), any(TokenStatus.class))).thenReturn(testAppSet);
        when(masheryApiClientService.revokeAccessToken(anyString(), anyString())).thenReturn(true);

        // verify result
        Boolean wasSuccessful = oAuthServiceToTest.revokeAccessTokensByUserAccount(testAccount);
        Assert.assertTrue(wasSuccessful);

        // verify what happened for call to Mashery
        verify(masheryApiClientService, times(1)).fetchUserApplicationsByUserContext(TEST_USER_CONTEXT, TokenStatus.Active);
        verify(masheryApiClientService, times(1)).revokeAccessToken(CLIENT_ID, TOKEN_1);
        verify(masheryApiClientService, times(1)).revokeAccessToken(CLIENT_ID, TOKEN_2);
        verify(masheryApiClientService, times(1)).revokeAccessToken(CLIENT_ID, TOKEN_3);
    }

    @Test
    public void testRevokeAccessTokensByUserAccountUnsuccessful() throws Exception {
        when(masheryApiClientService.fetchUserApplicationsByUserContext(anyString(), any(TokenStatus.class))).thenReturn(testAppSet);
        when(masheryApiClientService.revokeAccessToken(CLIENT_ID, TOKEN_1)).thenReturn(true);
        when(masheryApiClientService.revokeAccessToken(CLIENT_ID, TOKEN_2)).thenReturn(false);
        when(masheryApiClientService.revokeAccessToken(CLIENT_ID, TOKEN_3)).thenReturn(true);

        // verify result
        Boolean wasSuccessful = oAuthServiceToTest.revokeAccessTokensByUserAccount(testAccount);
        Assert.assertFalse(wasSuccessful);

        // verify what happened for call to Mashery
        verify(masheryApiClientService, times(1)).fetchUserApplicationsByUserContext(TEST_USER_CONTEXT, TokenStatus.Active);
        verify(masheryApiClientService, times(1)).revokeAccessToken(CLIENT_ID, TOKEN_1);
        verify(masheryApiClientService, times(1)).revokeAccessToken(CLIENT_ID, TOKEN_2);
        verify(masheryApiClientService, times(1)).revokeAccessToken(CLIENT_ID, TOKEN_3);
    }

}
