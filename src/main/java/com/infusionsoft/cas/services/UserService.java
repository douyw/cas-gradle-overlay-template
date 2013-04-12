package com.infusionsoft.cas.services;

import com.infusionsoft.cas.domain.PendingUserAccount;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.exceptions.AccountException;

import java.util.List;

public interface UserService {
    User addUser(User user) throws InfusionsoftValidationException;

    User loadUser(String username);

    User loadUser(Long id);

    User findUserByRecoveryCode(String recoveryCode);

    String createPasswordRecoveryCode(User user);

    UserAccount findUserAccount(User user, Long accountId);

    UserAccount findUserAccount(User user, String appName, String appType, String appUsername);

    List<UserAccount> findSortedUserAccounts(User user);

    PendingUserAccount createPendingUserAccount(String appType, String appName, String appUsername, String firstName, String lastName, String email, boolean passwordVerificationRequired);

    UserAccount associateAccountToUser(User user, String appType, String appName, String appUsername) throws AccountException;

    UserAccount associatePendingAccountToUser(User user, String registrationCode) throws AccountException;

    List<UserAccount> findEnabledUserAccounts(String appName, String appType, String appUsername);

    List<UserAccount> findDisabledUserAccounts(String appName, String appType, String appUsername);

    void disableAccount(UserAccount account);

    void enableUserAccount(UserAccount account);

    PendingUserAccount findPendingUserAccount(String registrationCode);

    User findEnabledUser(String username);

    void cleanupLoginAttempts();

    List<UserAccount> findByUserAndDisabled(User user, boolean disabled);

    void updateUser(User user);

    void updateUserAccount(UserAccount userAccount);

    List<User> findByUsernameWildcard(String usernameWildcard);

    boolean isDuplicateUsername(String username, Long id);

    String resetPassword(User user);
}
