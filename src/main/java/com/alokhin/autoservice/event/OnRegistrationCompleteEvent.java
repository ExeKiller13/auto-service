package com.alokhin.autoservice.event;

import org.springframework.context.ApplicationEvent;
import com.alokhin.autoservice.persistence.model.entity.AccountEntity;

public class OnRegistrationCompleteEvent extends ApplicationEvent {

    private String appUrl;
    private AccountEntity accountEntity;

    public OnRegistrationCompleteEvent(
        AccountEntity accountEntity, String appUrl) {
        super(accountEntity);

        this.accountEntity = accountEntity;
        this.appUrl = appUrl;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public AccountEntity getAccountEntity() {
        return accountEntity;
    }
}
