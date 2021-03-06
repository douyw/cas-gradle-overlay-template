package org.apereo.cas.infusionsoft.services;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.infusionsoft.config.properties.InfusionsoftConfigurationProperties;
import org.apereo.cas.infusionsoft.dao.*;
import org.apereo.cas.infusionsoft.domain.User;
import org.apereo.cas.infusionsoft.support.UserAccountTransformer;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserServiceImpl serviceToTest;

    @Mock
    private UserAccountTransformer userAccountTransformer;

    @Mock
    private AuthorityDAO authorityDAO;

    @Mock
    private LoginAttemptDAO loginAttemptDAO;

    @Mock
    private MailService mailService;

    @Mock
    private PasswordService passwordService;

    @Mock
    private UserDAO userDAO;

    @Mock
    private UserAccountDAO userAccountDAO;

    @Mock
    private UserIdentityDAO userIdentityDAO;

    private User user;
    private static final String testUsername = "test.user@infusionsoft.com";
    private static final String testFirstName = "Test";
    private static final String testLastName = "User";

    @Before
    public void setupForMethod() {
        user = new User();
        user.setId(13L);
        user.setFirstName(testFirstName);
        user.setLastName(testLastName);
        user.setEnabled(true);
        user.setUsername(testUsername);

        InfusionsoftConfigurationProperties infusionsoftConfigurationProperties = new InfusionsoftConfigurationProperties();

        MockitoAnnotations.initMocks(this);

        serviceToTest = new UserServiceImpl(userAccountTransformer, authorityDAO, loginAttemptDAO, mailService, passwordService, userDAO, userAccountDAO, userIdentityDAO, infusionsoftConfigurationProperties);

        when(userDAO.findOne(user.getId())).thenReturn(user);
        when(userDAO.findByUsername(testUsername)).thenReturn(user);
        when(userDAO.findByUsernameAndEnabled(testUsername, true)).thenReturn(user);
        when(userDAO.save(user)).thenReturn(user);
    }

    // TODO: a bunch of other functions in UserService still have no testing at all

    @Test
    public void testUpdatePasswordRecoveryCode() throws Exception {
        final String oldPasswordRecoveryCode = "Not null";
        final DateTime oldPasswordRecoveryCodeCreatedTime = new DateTime(1);
        user.setPasswordRecoveryCode(oldPasswordRecoveryCode);
        user.setPasswordRecoveryCodeCreatedTime(oldPasswordRecoveryCodeCreatedTime);

        User returnedUser = serviceToTest.updatePasswordRecoveryCode(user.getId());

        // Make sure save was called and with the right user
        verify(userDAO, times(1)).save(returnedUser);
        Assert.assertSame(returnedUser, user);
        // Make sure the recovery code value and date were updated
        Assert.assertFalse(StringUtils.equals(returnedUser.getPasswordRecoveryCode(), oldPasswordRecoveryCode));
        Assert.assertFalse(returnedUser.getPasswordRecoveryCodeCreatedTime() == oldPasswordRecoveryCodeCreatedTime);
        // Nothing else should have changed
        Assert.assertEquals(returnedUser.getUsername(), testUsername);
        Assert.assertEquals(returnedUser.getFirstName(), testFirstName);
        Assert.assertEquals(returnedUser.getLastName(), testLastName);
        Assert.assertTrue(returnedUser.isEnabled());
    }

    @Test
    public void testClearPasswordRecoveryCode() throws Exception {
        user.setPasswordRecoveryCode("Not null");
        user.setPasswordRecoveryCodeCreatedTime(new DateTime());

        User returnedUser = serviceToTest.clearPasswordRecoveryCode(user.getId());

        // Make sure save was called and with the right user
        verify(userDAO, times(1)).save(returnedUser);
        Assert.assertSame(returnedUser, user);
        // Make sure the recovery code value and date were cleared out
        Assert.assertNull(returnedUser.getPasswordRecoveryCode());
        Assert.assertNull(returnedUser.getPasswordRecoveryCodeCreatedTime());
        // Nothing else should have changed
        Assert.assertEquals(returnedUser.getUsername(), testUsername);
        Assert.assertEquals(returnedUser.getFirstName(), testFirstName);
        Assert.assertEquals(returnedUser.getLastName(), testLastName);
        Assert.assertTrue(returnedUser.isEnabled());
    }
}
