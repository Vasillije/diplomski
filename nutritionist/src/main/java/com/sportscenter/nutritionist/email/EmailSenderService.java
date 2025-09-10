package com.sportscenter.nutritionist.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class EmailSenderService {
    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendEmail(String toEmail, String subject, String body){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("dojcinovvasilije2@gmail.com");
        message.setTo("petrovdragan211@gmail.com");
        message.setText(body);
        message.setSubject(subject);

        mailSender.send(message);
    }

    @Async
    public void sendEmailWithAttachment(String toEmail, String subject, String body, ByteArrayOutputStream pdfStream) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("dojcinovvasilije2@gmail.com");
        helper.setTo("petrovdragan211@gmail.com");
        helper.setSubject(subject);
        helper.setText(body);


        helper.addAttachment("plan_ishrane.pdf", new ByteArrayDataSource(pdfStream.toByteArray(), "application/pdf"));

        mailSender.send(message);
    }
}
