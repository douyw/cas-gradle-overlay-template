package org.apereo.cas.infusionsoft.services;

import com.google.common.collect.ImmutableList;
import org.apereo.cas.infusionsoft.domain.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.regex.Pattern;

public class UserEmailValidationTest {
    @Test
    public void testUserEmailValidatorOnValidEmailAddresses() throws Exception {
        List<String> validEmailAddresses = ImmutableList.of(
                "КсенияАкопян@things.gov",
                "ЛеонидКовальский@testerly.org",
                "МарияТиммерман@thisthat.org.ru",
                "НатальяАничкина@тест.цом.ру",
                "ОлегСтроев@домаин.нет.ру",
                "ПавелЛапшин@яхоо.цом.ру",
                "РусланНуриев@яхоо.ру",
                "СветланаСтасова@тхингс.гов",
                "ТатьянаТерентьева@тестерлы.орг",
                "test@example.com",
                "test@yahoo.com.br",
                "abc__abc@domain.com",
                "abc-@insta.com",
                "abc-def@insta.com",
                "abc+@insta.com",
                "abc%@insta.com",
                "abc/@insta.com");

        validEmailAddresses.forEach(this::validateValidEmailAddress);
    }

    @Test
    public void testUserEmailValidatorOnInvalidEmailAddresses() throws AssertionError {
        List<String> invalidEmailAddresses = ImmutableList.of(
                "ар,тём@емаилс.цом",
                "abc@@insta.com.com.com",
                "abc@insta..com",
                "abc@insta%.com",
                "abc@insta/.com",
                "abc@insta\\.com",
                "abc\\@insta.com",
                "abc..abc@domain.com",
                "abc.@insta.com",
                "abc..de@insta.com",
                "abc123..45@insta.com",
                "abc@insta-.com",
                "this@that@this.com");

        invalidEmailAddresses.forEach(this::validateInvalidEmailAddress);
    }

    private void validateValidEmailAddress(String emailAddress) {
        Pattern userEmailPattern = Pattern.compile(User.getEmailRegex(), Pattern.CASE_INSENSITIVE);
        boolean isValid = userEmailPattern.matcher(emailAddress).matches();

        Assert.assertTrue("This email should be considered invalid: " + emailAddress, isValid);
    }

    private void validateInvalidEmailAddress(String emailAddress) {
        Pattern userEmailPattern = Pattern.compile(User.getEmailRegex(), Pattern.CASE_INSENSITIVE);
        boolean isValid = userEmailPattern.matcher(emailAddress).matches();

        Assert.assertTrue("This email should be considered valid: " + emailAddress, !isValid);
    }
}
