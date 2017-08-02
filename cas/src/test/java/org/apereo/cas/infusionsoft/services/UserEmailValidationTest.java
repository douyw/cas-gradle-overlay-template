package org.apereo.cas.infusionsoft.services;

import com.google.common.collect.ImmutableList;
import org.apereo.cas.infusionsoft.domain.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class UserEmailValidationTest {

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
    }

    @Test
    public void testUserEmailValidatorOnValidEmailAddresses() throws Exception {
        List<String> validEmailAddresses = ImmutableList.of("КсенияАкопян@things.gov", "ЛеонидКовальский@testerly.org", "МарияТиммерман@thisthat.org.ru", "НатальяАничкина@тест.цом.ру", "ОлегСтроев@домаин.нет.ру", "ПавелЛапшин@яхоо.цом.ру", "РусланНуриев@яхоо.ру", "СветланаСтасова@тхингс.гов", "ТатьянаТерентьева@тестерлы.орг", "test@example.com", "test@yahoo.com.br", "abc__abc@domain.com", "abc-@insta.com", "abc-def@insta.com", "abc+@insta.com", "abc%@insta.com", "abc/@insta.com");
        validEmailAddresses.forEach(this::validateValidEmailAddress);
    }

    @Test
    public void testUserEmailValidatorOnInvalidEmailAddresses() throws AssertionError {
        List<String> invalidEmailAddresses = ImmutableList.of("ар,тём@емаилс.цом", "abc@@insta.com.com.com", "abc@insta..com", "abc@insta%.com", "abc@insta/.com", "abc@insta\\.com", "abc\\@insta.com", "abc..abc@domain.com", "abc.@insta.com", "abc..de@insta.com", "abc123..45@insta.com", "abc@insta-.com", "ABC.ABC.ABC.ABC0046@domain.com.com.com", "this@that@this.com");

        invalidEmailAddresses.forEach(this::validateInvalidEmailAddress);
    }

    private void validateValidEmailAddress(String emailAddress) {
        boolean validEmail = emailAddress.matches(user.getEMAIL_REGEX());
        Assert.assertTrue("Invalid Email: " + emailAddress, validEmail);
    }

    private void validateInvalidEmailAddress(String emailAddress) {
        boolean validEmail = emailAddress.matches(user.getEMAIL_REGEX());
        Assert.assertFalse("Valid Email: " + emailAddress, validEmail);
    }
}
