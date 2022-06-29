package io;


import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EnviadorEmail {

    public void enviaEmail(String email, String mensagem) {

            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class",
                    "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "465");

            Session session = Session.getDefaultInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication("seuemail@gmail.com", "suasenha123");
                        }
                    });

            session.setDebug(true);

            try {

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("seuemail@gmail.com"));

                Address[] toUser = InternetAddress.parse(email);

                message.setRecipients(Message.RecipientType.TO, toUser);
                message.setSubject("Enviando email com JavaMail");
                message.setText(mensagem);
                Transport.send(message);

                System.out.println("E-mail enviado!!");

    } catch (AddressException e) {
                throw new RuntimeException(e);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
    }
}
