create table if not exists clients
(
    id         bigint generated always as identity
        constraint clients_pk
            primary key,
    first_name varchar(50) not null,
    last_name  varchar(50) not null,
    patronymic varchar(50),
    age        smallint,
    birthday   date
);

comment on table clients is 'Таблица клиентов оператора "Ромашка"';

comment on column clients.id is 'ID клиента';

comment on column clients.first_name is 'Имя клиента';

comment on column clients.last_name is 'Фамилия клиента';

comment on column clients.patronymic is 'Отчество клиента';

comment on column clients.age is 'Возраст клиента';

comment on column clients.birthday is 'Дата рождения клиента';

create table if not exists client_details
(
    client_id               bigint generated always as identity
        constraint client_details_pk
            primary key
        constraint client_details_clients_id_fk
            references clients,
    number_personal_account integer      not null
        constraint client_details_pk2
            unique,
    email                   varchar(50)
        constraint client_details_pk3
            unique,
    password                varchar(255) not null,
    region                  varchar(255) not null,
    passport                varchar(10)
        constraint client_details_pk4
            unique,
    contract_date           date         not null,
    contract_number         varchar(15)  not null
        constraint client_details_pk5
            unique
);

comment on table client_details is 'Доп. данные о клиенте';

comment on column client_details.client_id is 'ID клиента';

comment on column client_details.number_personal_account is 'Номер аккаунта клиента';

comment on column client_details.email is 'Электронная почта клиента';

comment on column client_details.password is 'Паполь от аккаунта клиента';

comment on column client_details.region is 'Регион клиента';

comment on column client_details.passport is 'Серия и номер паспорта клиента';

comment on column client_details.contract_date is 'Дата заключения договора с клиентом';

comment on column client_details.contract_number is 'Номер лицевого счёта клиента';

create table if not exists tariffs
(
    id          char(2)      not null
        constraint tariffs_pk
            primary key,
    title       varchar(255) not null,
    description text
);

comment on table tariffs is 'Таблица тарифов';

comment on column tariffs.id is 'ID тарифа';

comment on column tariffs.title is 'Название тарифа';

comment on column tariffs.description is 'Описание тарифа';

create table if not exists phone_numbers
(
    client_id    bigint generated always as identity
        constraint phone_numbers_pk
            primary key
        constraint phone_numbers_clients_id_fk
            references clients,
    phone_number varchar(16)     not null
        constraint phone_numbers_pk2
            unique,
    balance      money default 0 not null,
    tariff_id    char(2)
        constraint phone_numbers_tariffs_id_fk
            references tariffs
);

comment on table phone_numbers is 'Номер телефона клиента';

comment on column phone_numbers.client_id is 'ID клиента';

comment on column phone_numbers.phone_number is 'Номер телефона клиента';

comment on column phone_numbers.balance is 'Баланс клиента';

comment on column phone_numbers.tariff_id is 'ID тарифа клиента';

create table if not exists call_types
(
    id   char(2)     not null
        constraint call_types_pk
            primary key,
    type varchar(20) not null
);

comment on table call_types is 'Типы звонков';

comment on column call_types.id is 'ID звонка';

comment on column call_types.type is 'Тип звонка';

create table if not exists telecom_operators
(
    id    smallint generated always as identity
        constraint telecom_operators_pk
            primary key,
    title varchar(255) not null
);

comment on table telecom_operators is 'Таблица операторов связи';

comment on column telecom_operators.id is 'ID оператора связи';

comment on column telecom_operators.title is 'Название оператора связи';

create table if not exists call_history
(
    id                     integer generated always as identity
        constraint call_history_pk
            primary key,
    client_phone_number_id bigint    not null
        constraint call_history_phone_numbers_client_id_fk
            references phone_numbers,
    call_type_id           char(2)   not null
        constraint call_history_call_types_id_fk
            references call_types,
    date_start             timestamp not null,
    date_end               timestamp not null,
    cost                   money     not null
);

comment on table call_history is 'История всех звонков';

comment on column call_history.id is 'ID записи истории';

comment on column call_history.client_phone_number_id is 'ID номера телефона клиента';

comment on column call_history.call_type_id is 'ID типа звонка';

comment on column call_history.date_start is 'Дата и время начала звонка';

comment on column call_history.date_end is 'Дата и время конца звонка';

comment on column call_history.cost is 'Стоимость звонка';

create table if not exists internet_packages
(
    package_id                smallint generated always as identity
        constraint internet_packages_pk
            primary key,
    package_of_mb             smallint not null,
    package_cost              money    not null,
    package_cost_per_mb       boolean  not null,
    extra_package_cost        money    not null,
    extra_package_cost_per_mb boolean  not null
);

comment on table internet_packages is 'Таблица пакетов интернета';

comment on column internet_packages.package_id is 'ID пакета интернета';

comment on column internet_packages.package_of_mb is 'Пакет мегабайт';

comment on column internet_packages.package_cost is 'Стоимость пакета интернета';

comment on column internet_packages.package_cost_per_mb is 'Флаг стоимости пакета за каждый мегабайт (true), иначе стоимость применяется ко всему пакету';

comment on column internet_packages.extra_package_cost is 'Внепакетная стоимость интернета (если первышен лимит Мб)';

comment on column internet_packages.extra_package_cost_per_mb is 'Флаг внепакетной стоимости за каждый Мб (true)';

create table if not exists telephony_packages
(
    package_id                    smallint generated always as identity
        constraint telephony_packages_pk
            primary key,
    call_type_id                  char(2)
        constraint telephony_packages_call_types_id_fk
            references call_types,
    operator_id                   smallint
        constraint telephony_packages_telecom_operators_id_fk
            references telecom_operators,
    package_of_minutes            smallint not null,
    package_cost                  money    not null,
    package_cost_per_minute       boolean  not null,
    extra_package_cost            money    not null,
    extra_package_cost_per_minute boolean  not null
);

comment on table telephony_packages is 'Таблица пакетов звонков';

comment on column telephony_packages.package_id is 'ID пакета звонков';

comment on column telephony_packages.call_type_id is 'ID типа звонка';

comment on column telephony_packages.operator_id is 'ID оператора связи';

comment on column telephony_packages.package_of_minutes is 'Пакет минут';

comment on column telephony_packages.package_cost is 'Стоимость пакета';

comment on column telephony_packages.package_cost_per_minute is 'Флаг стоимости пакета за каждую минуту (true) или за весь пакет (false)';

comment on column telephony_packages.extra_package_cost is 'Внепакетная стоимость звонков';

comment on column telephony_packages.extra_package_cost_per_minute is 'Флаг внепакетной стоимости за каждую минуту (true)';

create table if not exists tariffs_config
(
    id                   bigint generated always as identity
        constraint tariffs_config_pk
            primary key,
    tariff_id            char(2) not null
        constraint tariffs_config_tariffs_id_fk
            references tariffs,
    telephony_package_id smallint
        constraint tariffs_config_telephony_packages_package_id_fk
            references telephony_packages,
    internet_package_id  smallint
        constraint tariffs_config_internet_packages_package_id_fk
            references internet_packages
);

comment on table tariffs_config is 'Таблица конфигураций тарифа';

comment on column tariffs_config.id is 'ID записи';

comment on column tariffs_config.tariff_id is 'ID тарифа';

comment on column tariffs_config.telephony_package_id is 'ID пакета звонков';

comment on column tariffs_config.internet_package_id is 'ID пакета интернета';

create table if not exists users
(
    client_id                     bigint generated always as identity
        constraint users_pk
            primary key
        constraint users_clients_id_fk
            references clients,
    role                   varchar not null
);

comment on table users is 'Таблица пользователей';

comment on column users.client_id is 'ID клиента';


