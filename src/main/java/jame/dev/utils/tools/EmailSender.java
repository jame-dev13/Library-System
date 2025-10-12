package jame.dev.utils.tools;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jame.dev.utils.loader.LoadDotEnvUtil;
import lombok.NonNull;
import lombok.extern.java.Log;

import java.util.Map;
import java.util.Properties;

/**
 * Class to handle the message transport via email.
 */
@Log
public final class EmailSender {
    private static final LoadDotEnvUtil loadInstance = LoadDotEnvUtil.getInstance();
    private static final Map<String, String> map = loadInstance.getMapEmailSender();

   /**
    * Builds properties to send a message email to a user.
    * @param to the internet address of a user.
    * @param value the data.
    * @param msg the msg.
    * @return true if the email was sent, false if some problems shows up on the Message building.
    */
    public static boolean mailTo(@NonNull String to, String value, String msg){
       //Properties SMTP
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", map.get("PORT"));

        // AUTH
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(map.get("FROM"), map.get("APP_PWD"));
            }
        });

        try {
           // BUILD MESSAGE
           Message message = new MimeMessage(session);
           message.setFrom(new InternetAddress(map.get("FROM")));
           message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
           message.setSubject("Library System - jame-dev13 :)");
           String html = """
                    <h1>Hello!</h1>
                    <p>%s:</p>
                    <code><h2>%s</h2></code>
                    """.formatted(msg, value);
            message.setContent(html, "text/html");

            // SEND MESSAGE
            Transport.send(message);
            log.info("Email sent.\n");
            return true;
        } catch (MessagingException e) {
            log.severe("Cannot sent email.\n");
            return false;
        }
    }
}

