package org.apereo.cas.infusionsoft.services;

import com.infusionsoft.account.sdk.UserApi;
import com.infusionsoft.account.sdk.dto.UserCreate;
import com.infusionsoft.account.sdk.dto.UserUpdate;
import feign.FeignException;
import feign.RetryableException;
import org.apereo.cas.infusionsoft.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class AccountApiUserService /*implements UserDAO*/ {

    private static final Logger log = LoggerFactory.getLogger(AccountApiUserService.class);

    private final UserApi userApi;

    public AccountApiUserService(AccountApiService accountApiService) {
        userApi = accountApiService.getUserApi();
    }

    public User save(User user) {
        final String userId = Objects.toString(user.getId(), null);
        final String username = user.getUsername();
        final String firstName = user.getFirstName();
        final String lastName = user.getLastName();
        final String fullName = String.format("%s %s", firstName, lastName);
        final boolean enabled = user.isEnabled();

        com.infusionsoft.account.sdk.dto.User result;
        try {
            userApi.retrieveUser(userId);
            final UserUpdate userUpdate = createUserUpdate(username, firstName, lastName, fullName, enabled);
            result = userApi.updateUser(userId, userUpdate);
        } catch (RetryableException exception) {
            final FeignException exceptionCause = (FeignException) exception.getCause();
            if (exceptionCause.status() != 404) {
                throw exception;
            }

            final UserCreate userInput = createUserInput(userId, username, firstName, lastName, fullName, enabled);
            result = userApi.createUser(userInput);
        }
        log.debug("Synced user " + result.getId() + " with the account API");
        return user;
    }

    private UserCreate createUserInput(String userId, String username, String firstName, String lastName, String fullName, boolean enabled) {
        final UserCreate user = new UserCreate();
        user.setLegacyId(userId);
        user.setUsername(username);
        user.setGivenName(firstName);
        user.setFamilyName(lastName);
        user.setFullName(fullName);
        user.setEnabled(enabled);
        return user;
    }

    private UserUpdate createUserUpdate(String username, String firstName, String lastName, String fullName, boolean enabled) {
        final UserUpdate user = new UserUpdate();
        user.setUsername(username);
        user.setGivenName(firstName);
        user.setFamilyName(lastName);
        user.setFullName(fullName);
        user.setEnabled(enabled);
        return user;
    }

}