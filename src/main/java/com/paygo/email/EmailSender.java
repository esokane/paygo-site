package com.paygo.email;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * class provide email sending functions
 */
public class EmailSender {
    private static final Logger logger = LoggerFactory.getLogger(EmailSender.class);

    private static final String transportType = "smtp";
    private static Properties mailServerProperties = new Properties();
    private static Session getMailSession;
    private static String bodyTemplate;

    private String senderMail;
    private String senderPass;
    private String subject;
    private String mailBodyContenType;

    public EmailSender(String mailSmtpPort, String mailSmtpAuth, String mailSmtpStartTlsEnable, String mailBodyFileName) {
        mailServerProperties.put("mail.smtp.port", mailSmtpPort);
        mailServerProperties.put("mail.smtp.auth", mailSmtpAuth);
        mailServerProperties.put("mail.smtp.starttls.enable", mailSmtpStartTlsEnable);
        getMailSession = Session.getDefaultInstance(mailServerProperties, null);
        bodyTemplate = loadBodyTemplate(mailBodyFileName);
    }

    public void sendEmail(String email, Map<String, String> parameters) throws MessagingException {
        logger.debug("sendEmail([{}],[{}]) -> started", email, parameters);
        MimeMessage generateMailMessage = new MimeMessage(getMailSession);
        generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(email));

        generateMailMessage.setSubject(subject);

        String emailBody = generateMail(bodyTemplate, parameters);
        generateMailMessage.setContent(emailBody, mailBodyContenType);

        Transport transport = getMailSession.getTransport(transportType);
        transport.connect("smtp.gmail.com", senderMail, senderPass);

        transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
        transport.close();

        logger.debug("sendEmail -> ended");
    }

    public String generateMail(String template, Map<String, String> parameters) {
        logger.debug("generateMail([{}],[{}]) -> started", template, parameters);
        Pattern p = Pattern.compile("%(\\w+)%");
        Matcher m = p.matcher(template);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            if (parameters.containsKey(m.group(1))) {
                m.appendReplacement(sb, parameters.get(m.group(1)));
            }
        }
        m.appendTail(sb);
        String message = sb.toString();
        logger.debug("generateMail -> ended. message: [{}]", message);
        return message;
    }

    public String loadBodyTemplate(String mailBodyFileName) {
        InputStream is = EmailSender.class.getResourceAsStream(mailBodyFileName);
        try {
            try (BufferedReader buffer = new BufferedReader(new InputStreamReader(is))) {
                return buffer.lines().collect(Collectors.joining("\n"));
            }
        } catch (IOException e) {
            logger.error("Body template not found", e);
        }
        return null;
    }

    public void setSenderMail(String senderMail) {
        this.senderMail = senderMail;
    }

    public void setSenderPass(String senderPass) {
        this.senderPass = senderPass;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setMailBodyContenType(String mailBodyContenType) {
        this.mailBodyContenType = mailBodyContenType;
    }

}
