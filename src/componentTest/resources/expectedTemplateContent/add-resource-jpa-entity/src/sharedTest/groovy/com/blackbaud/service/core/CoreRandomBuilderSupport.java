package com.blackbaud.service.core;

import org.springframework.beans.factory.annotation.Autowired;
import com.blackbaud.service.core.domain.RandomAccountEntityBuilder;
import com.blackbaud.service.core.domain.AccountRepository;

public class CoreRandomBuilderSupport {

    @Autowired
    private AccountRepository accountRepository;

    public RandomAccountEntityBuilder accountEntity() {
        return new RandomAccountEntityBuilder(accountRepository);
    }

}
