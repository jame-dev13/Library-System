package jame.dev.emailSender;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.NonNull;

import java.util.Properties;

public class EmailSender {
    private final Dotenv env = Dotenv.load();
    private final String FROM = env.get("MAIL_FROM");
    private final String PWD = env.get("PWD_APP");

    public void mailTo(@NonNull String to){
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
            message.setSubject("Prueba de correo con Java");
            message.setText("¡Hola! Este es un correo enviado desde Java.");

            // SEND MESSAGE
            Transport.send(message);
            System.out.println("Correo enviado correctamente.");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}

