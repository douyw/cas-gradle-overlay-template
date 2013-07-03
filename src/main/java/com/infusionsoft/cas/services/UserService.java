package com.infusionsoft.cas.services;

import com.infusionsoft.cas.domain.*;
import com.infusionsoft.cas.exceptions.AccountException;
import com.infusionsoft.cas.exceptions.InfusionsoftValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    User saveUser(User user) throws InfusionsoftValidationException;

    User saveUser(User user, String plainTextPassword) throws InfusionsoftValidationException;

    User loadUser(String username);

    User loadUser(Long id);

    User findUserByRecoveryCode(String recoveryCode);

    User updatePasswordRecoveryCode(User user);

    UserAccount findUserAccount(User user, Long accountId);

    UserAccount findUserAccount(User user, String appName, AppType appType);

    List<UserAccount> findSortedUserAccounts(User user);

    PendingUserAccount createPendingUserAccount(AppType appType, String appName, String appUsername, String firstName, String lastName, String email, boolean passwordVerificationRequired);

    UserAccount associateAccountToUser(User user, AppType appType, String appName, String appUsername) throws AccountException;

    UserAccount associatePendingAccountToUser(User user, String registrationCode) throws AccountException;

    List<UserAccount> findEnabledUserAccounts(String appName, AppType appType, String appUsername);

    List<UserAccount> findEnabledUserAccounts(String appName, AppType appType, long casGlobalId);

    List<UserAccount> findUserAccounts(String appName, AppType appType, String appUsername);

    List<UserAccount> findUserAccounts(String appName, AppType appType, long casGlobalId);

    List<UserAccount> findDisabledUserAccounts(String appName, AppType appType, String appUsername);

    List<UserAccount> findDisabledUserAccounts(String appName, AppType appType, long casGlobalId);

    void deleteAccount(UserAccount account);

    void disableAccount(UserAccount account);

    void enableUserAccount(UserAccount account);

    PendingUserAccount findPendingUserAccount(String registrationCode);

    User findEnabledUser(String username);

    void cleanupLoginAttempts();

    List<UserAccount> findByUserAndDisabled(User user, boolean disabled);

    UserAccount saveUserAccount(UserAccount userAccount);

    Page<User> findByUsernameLike(String usernameWildcard, Pageable pageable);

    boolean isDuplicateUsername(User user);

    String resetPassword(User user);

    void changeAssociatedAppUsername(User user, String appName, AppType appType, String newAppUsername) throws AccountException;

    List<Authority> findAllAuthorities();

    Authority findAuthorityByName(String authorityName);
}
