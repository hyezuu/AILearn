drop table if exists tests CASCADE;
create table tests
(
    created_at datetime(6)                               null,
    deleted_at datetime(6)                               null,
    id         bigint auto_increment                     primary key,
    updated_at datetime(6)                               null,
    answer     varchar(255)                              not null,
    question   varchar(255)                              not null,
    grade      enum ('A1', 'A2', 'B1', 'B2', 'C1', 'C2') not null
);

