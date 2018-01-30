package com.alokhin.autoservice.event.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import com.alokhin.autoservice.event.OnRegistrationCompleteEvent;
import com.alokhin.autoservice.persistence.model.entity.AccountEntity;
import com.alokhin.autoservice.persistence.model.entity.VerificationTokenEntity;
import com.alokhin.autoservice.service.MailService;
import com.alokhin.autoservice.service.RegistrationService;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    @Value ("${support.email}")
    private String from;

    private final RegistrationService registrationService;

    private final MailService mailService;

    @Autowired
    public RegistrationListener(RegistrationService registrationService, MailService mailService) {
        this.registrationService = registrationService;
        this.mailService = mailService;
    }

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        AccountEntity accountEntity = event.getAccountEntity();
        VerificationTokenEntity verificationTokenEntity = registrationService.updateVerificationToken(accountEntity);
        String confirmationUrl = event.getAppUrl() + "/confirm?token=" + verificationTokenEntity.getToken();
        mailService.sendMailMessage(from, accountEntity.getLogin(), "Registration confirm",
                                    String.format("Please confirm registration following by link: \r\n %s", confirmationUrl));
    }
}
