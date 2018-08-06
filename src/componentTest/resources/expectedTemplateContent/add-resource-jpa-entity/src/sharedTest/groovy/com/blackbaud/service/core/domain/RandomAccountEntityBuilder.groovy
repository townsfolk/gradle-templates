package com.blackbaud.service.core.domain

import static com.blackbaud.service.core.CoreARandom.aRandom

class RandomAccountEntityBuilder extends AccountEntity.AccountEntityBuilder {

    private AccountRepository accountRepository

    RandomAccountEntityBuilder(AccountRepository accountRepository) {
        this.accountRepository = accountRepository
        throw new RuntimeException("add some stuff")
    }

    AccountEntity save() {
        accountRepository.save(build())
    }

}
