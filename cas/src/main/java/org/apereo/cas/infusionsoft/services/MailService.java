package org.apereo.cas.infusionsoft.services;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.infusionsoft.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.StringWriter;
import java.util.Locale;

/**
 * Simple service for sending transactional emails, like the Forgot Password email.
 */
@Service
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class MailService {
    private static final String NO_REPLY_EMAIL_ADDRESS = "noreply@infusionsoft.com";
    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private VelocityEngine velocityEngine;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private SupportContactService supportContactService;

    /**
     * Utility method for making a transactional email in a "standard" manner.
     */
    private MimeMessage createMessage(User user, String subject) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        message.setHeader("X-InfApp", "cas");
        message.setHeader("X-inf-package", "transactional");
        message.setHeader("Package", "transactional");

        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setTo(user.getUsername());
        helper.setFrom(NO_REPLY_EMAIL_ADDRESS);
        helper.setSubject(subject);

        return message;
    }

    /**
     * Sends a welcome to new users who just created their Infusionsoft ID.
     */
    public void sendWelcomeEmail(User user, Locale locale) {
        try {
            MimeMessage message = createMessage(user, messageSource.getMessage("email.welcome.subject", null, locale));

            StringWriter body = new StringWriter();
            Context context = new VelocityContext();

            context.put("user", user);

            velocityEngine.mergeTemplate("/velocity/welcomeEmail.vm", "UTF-8", context, body);
            message.setContent(body.toString(), "text/html");
            mailSender.send(message);

            log.info("sent welcome email to user " + user.getId());
        } catch (Exception e) {
            log.error("failed to send welcome email to user " + user.getId() + "(" + user.getUsername() + ")", e);
        }
    }

    /**
     * Sends an email with a link and password recovery code.
     */
    public void sendPasswordResetEmail(User user) {
        try {
            // we should be passing in a locale here, or be able to determine the user's locale from the user object...
            MimeMessage message = createMessage(user, messageSource.getMessage("email.reset.password.subject", null, Locale.getDefault()));

            StringWriter body = new StringWriter();
            Context context = new VelocityContext();

            context.put("user", user);
            context.put("code", user.getPasswordRecoveryCode());
            context.put("serverPrefix", casProperties.getServer().getPrefix());
            context.put("supportPhoneNumbers", supportContactService.getSupportPhoneNumbers());

            velocityEngine.mergeTemplate("/velocity/forgotPasswordEmail.vm", "UTF-8", context, body);
            message.setContent(body.toString(), "text/html");
            mailSender.send(message);

            log.info("sent password recovery email to user " + user.getId());
        } catch (Exception e) {
            log.error("failed to send password recovery email to user " + user.getId() + "(" + user.getUsername() + ")", e);
        }
    }
}