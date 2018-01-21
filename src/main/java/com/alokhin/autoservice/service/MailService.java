package com.alokhin.autoservice.service;

public interface MailService {
    void sendMailMessage(String from, String to, String subject, String message);
}
