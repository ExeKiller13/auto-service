package com.alokhin.autoservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.alokhin.autoservice.service.MailService;

@Service
public class MailServiceImpl implements MailService {

    private final JavaMailSender javaMailSender;

    @Autowired
    public MailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendMailMessage(String from, String to, String subject, String message) {
        javaMailSender.send(createMessage(from, to, subject, message));
    }

    private SimpleMailMessage createMessage(String from, String to, String subject, String message) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setFrom(from);
        email.setSubject(subject);
        email.setText(message);
        return email;
    }
}
