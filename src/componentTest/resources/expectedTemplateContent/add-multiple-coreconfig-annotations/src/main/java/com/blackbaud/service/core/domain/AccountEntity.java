package com.blackbaud.service.core.domain;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity(name = "account")
@Table(name = "account")
@Data
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountEntity {

    @Id
    @org.hibernate.annotations.Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID id;

}
