package jame.dev.utils;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.NonNull;
import lombok.extern.java.Log;

import java.util.Properties;
@Log
public final class EmailSender {
    private static final Dotenv env = Dotenv.load();
    private static final String FROM = env.get("MAIL_FROM");
    private static final String PWD = env.get("PWD_APP");

    public static void mailTo(@NonNull String to, String token){
        //Properties SMTP
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // AUTH
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM, PWD);
            }
        });

        try {
            // BUILD MESSAGE
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Library System - jame-dev13 :)");
            String html = """
                    <h1>Hello!</h1>
                    <p>Please enter the code on the Message Box on the application to verify your account:</p>
                    <code><h2>%s</h2></code>
                    """.formatted(token);
            message.setContent(html, "text/html");

            // SEND MESSAGE
            Transport.send(message);
            log.info("Email sent.\n");
        } catch (MessagingException e) {
            log.severe("Cannot sent username.\n");
        }
    }

    public static void mailToWPassword(@NonNull String to, String password){
        //Properties SMTP
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // AUTH
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM, PWD);
            }
        });

        try {
            // BUILD MESSAGE
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Library System - jame-dev13 :)");
            String html = """
                    <h1>Hello!</h1>
                    <p>This is your admin password login for the system, you can change it latter in the app: </p>
                    <code><h2>%s</h2></code>
                    """.formatted(password);
            message.setContent(html, "text/html");

            // SEND MESSAGE
            Transport.send(message);
            log.info("Email sent.\n");
        } catch (MessagingException e) {
            log.severe("Cannot sent username.\n");
        }
    }
}

