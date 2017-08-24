package org.apereo.cas.infusionsoft.services;

import com.infusionsoft.account.sdk.UserApi;
import feign.FeignException;
import feign.RetryableException;
import org.apereo.cas.infusionsoft.domain.User;
import com.infusionsoft.account.sdk.dto.UserUpdate;
import com.infusionsoft.account.sdk.dto.UserCreate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.doThrow;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class AccountApiUserServiceTest {
    @InjectMocks
    private AccountApiUserService serviceToTest;
    @Mock
    private AccountApiService accountApiService;
    @Mock
    private UserApi userApi;
    @Mock
    private RetryableException myException;
    @Mock
    private FeignException  myExceptionCause;

    private static final String testUsername = "test.user@infusionsoft.com";
    private static final String testFirstName = "Test";
    private static final String testLastName = "User";
    private static final String testFullName = "Test User";

    @Before
    public void setupForMethod() {
        myException = new RetryableException("User not found", null);
        MockitoAnnotations.initMocks(this);
        userApi = Mockito.mock(UserApi.class);
        when(accountApiService.getUserApi()).thenReturn(userApi);
        serviceToTest = new AccountApiUserService(accountApiService);
    }

    @Test
    public void testAccountApiUserServiceSaveCallsAccountApiCreate() throws Exception {
        User user;
        user = new User();
        user.setId(14L);
        user.setFirstName(testFirstName);
        user.setLastName(testLastName);
        user.setEnabled(true);
        user.setUsername(testUsername);

        UserCreate userCreate;
        userCreate = new UserCreate();
        userCreate.setLegacyId("14");
        userCreate.setGivenName(testFirstName);
        userCreate.setFamilyName(testLastName);
        userCreate.setFullName(testFullName);
        userCreate.setUsername(testUsername);
        userCreate.setEnabled(true);

        com.infusionsoft.account.sdk.dto.User dtoUser;
        dtoUser = new com.infusionsoft.account.sdk.dto.User();
        dtoUser.setId(user.getId().toString());

        doThrow(myException).when(userApi).retrieveUser("14");
        when(userApi.createUser(userCreate)).thenReturn(dtoUser);

        // Set up the behavior for the case where a new user causes code to
        // execute an exception path.  Have to set up the correct exception and cause
        // in this case the cause is the one expected.
        when(myExceptionCause.status()).thenReturn(404);
        when(myException.getCause()).thenReturn(myExceptionCause);

        User returnedUser = serviceToTest.save(user);

        // Make sure save was called and with the right user
        Assert.assertSame(returnedUser, user);
        verify(userApi, times(1)).createUser(userCreate);
        // Nothing else should have changed
        Assert.assertEquals(returnedUser.getUsername(), testUsername);
        Assert.assertEquals(returnedUser.getFirstName(), testFirstName);
        Assert.assertEquals(returnedUser.getLastName(), testLastName);
        Assert.assertTrue(returnedUser.isEnabled());
    }

    @Test
    public void testAccountApiUserServiceSaveCallsAccountApiUpdate() throws Exception {
        User user;
        user = new User();
        user.setId(13L);
        user.setFirstName(testFirstName);
        user.setLastName(testLastName);
        user.setEnabled(true);
        user.setUsername(testUsername);

        UserUpdate userUpdate;
        userUpdate = new UserUpdate();
        userUpdate.setGivenName(testFirstName);
        userUpdate.setFamilyName(testLastName);
        userUpdate.setFullName(testFullName);
        userUpdate.setUsername(testUsername);
        userUpdate.setEnabled(true);

        com.infusionsoft.account.sdk.dto.User dtoUser;
        dtoUser = new com.infusionsoft.account.sdk.dto.User();
        dtoUser.setId(user.getId().toString());

        when(userApi.updateUser("13", userUpdate)).thenReturn(dtoUser);

        User returnedUser = serviceToTest.save(user);

        // Make sure save was called and with the right user
        Assert.assertSame(returnedUser, user);
        verify(userApi, times(1)).updateUser("13", userUpdate);
        // Nothing else should have changed
        Assert.assertEquals(returnedUser.getUsername(), testUsername);
        Assert.assertEquals(returnedUser.getFirstName(), testFirstName);
        Assert.assertEquals(returnedUser.getLastName(), testLastName);
        Assert.assertTrue(returnedUser.isEnabled());
    }

    @Test (expected = RetryableException.class)
    public void testAccountApiUserServiceSaveTriggersAccountApiExceptionCase() throws Exception {
        User user;
        user = new User();
        user.setId(14L);
        user.setFirstName(testFirstName);
        user.setLastName(testLastName);
        user.setEnabled(true);
        user.setUsername(testUsername);

        doThrow(myException).when(userApi).retrieveUser("14");

        // Set up the behavior for the case where a new user causes code to
        // execute an exception path.  Have to set up the correct exception and cause
        // in this case the cause is not one expected by the code so it is not handled.
        when(myExceptionCause.status()).thenReturn(401);
        when(myException.getCause()).thenReturn(myExceptionCause);

        serviceToTest.save(user);
    }
}

