--liquibase formatted sql

--changeset blackbaud:1
create table account (
  id uuid constraint account_pk primary key
)
--rollback drop table account