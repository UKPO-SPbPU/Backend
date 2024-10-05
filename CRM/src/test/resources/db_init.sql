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




INSERT INTO public.tariffs (id, title, description) VALUES ('01', 'Общительный', 'Тариф для тех, кто много разговаривает');
INSERT INTO public.tariffs (id, title, description) VALUES ('02', 'Обычный', 'Тариф с небольшим пакетом минут и интернета');
INSERT INTO public.tariffs (id, title, description) VALUES ('03', 'Блогер', 'Тариф для тех, кто любит смотреть и выкладывать видео');
INSERT INTO public.tariffs (id, title, description) VALUES ('04', 'Оптимум', 'Оптимальное количество минут и интернета');
INSERT INTO public.tariffs (id, title, description) VALUES ('05', 'Только Ромашка', 'Бесплатные звонки от любого абонента оператора Ромашка');
INSERT INTO public.tariffs (id, title, description) VALUES ('06', 'Студент', 'Тариф НЕ для допсёров');

INSERT INTO public.telecom_operators (title) VALUES ('Ромашка');

INSERT INTO public.call_types (id, type) VALUES ('01', 'Исходящий');
INSERT INTO public.call_types (id, type) VALUES ('02', 'Входящий');

INSERT INTO public.telephony_packages (call_type_id, operator_id, package_of_minutes, package_cost, package_cost_per_minute, extra_package_cost, extra_package_cost_per_minute) VALUES (null, null, 100, '$5.00', false, '$0.10', true);
INSERT INTO public.telephony_packages (call_type_id, operator_id, package_of_minutes, package_cost, package_cost_per_minute, extra_package_cost, extra_package_cost_per_minute) VALUES (null, null, 500, '$20.00', false, '$0.10', true);
INSERT INTO public.telephony_packages (call_type_id, operator_id, package_of_minutes, package_cost, package_cost_per_minute, extra_package_cost, extra_package_cost_per_minute) VALUES (null, 1, 60, '$0.00', false, '$0.00', false);

INSERT INTO public.internet_packages (package_of_mb, package_cost, package_cost_per_mb, extra_package_cost, extra_package_cost_per_mb) VALUES (1024, '$10.00', false, '$0.10', true);
INSERT INTO public.internet_packages (package_of_mb, package_cost, package_cost_per_mb, extra_package_cost, extra_package_cost_per_mb) VALUES (10240, '$40.00', false, '$0.10', true);

INSERT INTO public.tariffs_config (tariff_id, telephony_package_id, internet_package_id) VALUES ('01', 2, null);
INSERT INTO public.tariffs_config (tariff_id, telephony_package_id, internet_package_id) VALUES ('02', 1, 1);
INSERT INTO public.tariffs_config (tariff_id, telephony_package_id, internet_package_id) VALUES ('03', null, 2);
INSERT INTO public.tariffs_config (tariff_id, telephony_package_id, internet_package_id) VALUES ('04', 2, 2);
INSERT INTO public.tariffs_config (tariff_id, telephony_package_id, internet_package_id) VALUES ('05', 3, null);
INSERT INTO public.tariffs_config (tariff_id, telephony_package_id, internet_package_id) VALUES ('06', 1, null);

INSERT INTO public.clients (first_name, last_name, patronymic, age, birthday) VALUES ('Artem', 'Kurdikov', 'Sergeevich', 21, '2002-08-08');
INSERT INTO public.clients (first_name, last_name, patronymic, age, birthday) VALUES ('Channa', 'Dongall', '', 49, '1975-12-03');
INSERT INTO public.clients (first_name, last_name, patronymic, age, birthday) VALUES ('Marjie', 'Symcoxe', '', 37, '1987-07-07');
INSERT INTO public.clients (first_name, last_name, patronymic, age, birthday) VALUES ('Sal', 'Riste', '', 21, '2003-12-18');
INSERT INTO public.clients (first_name, last_name, patronymic, age, birthday) VALUES ('Raddy', 'Mehaffey', '', 22, '2001-08-23');
INSERT INTO public.clients (first_name, last_name, patronymic, age, birthday) VALUES ('Rogers', 'Furmonger', '', 31, '1993-03-25');
INSERT INTO public.clients (first_name, last_name, patronymic, age, birthday) VALUES ('Dawna', 'Renfrew', '', 27, '1997-05-13');

INSERT INTO public.client_details (number_personal_account, email, password, region, passport, contract_date, contract_number) VALUES (1, 'kourtema@gmail.com', '$2y$10$Lv.JQZMT985/dC0x4GYu7exD6S6SulXNvqw0fIntXvyammJDbll4G', 'Санкт-Петербург', '1111111111', '2022-12-01', '111111111111111');
INSERT INTO public.client_details (number_personal_account, email, password, region, passport, contract_date, contract_number) VALUES (2, 'cdongal4@gmail.com', '$2y$10$Lv.JQZMT985/dC0x4GYu7exD6S6SulXNvqw0fIntXvyammJDbll4G', 'Нижегородская область', '2222222222', '2022-12-02', '222222222222222');
INSERT INTO public.client_details (number_personal_account, email, password, region, passport, contract_date, contract_number) VALUES (3, 'msymcoxe5@gmail.com', '$2y$10$Lv.JQZMT985/dC0x4GYu7exD6S6SulXNvqw0fIntXvyammJDbll4G', 'Москва', '3333333333', '2022-12-03', '333333333333333');
INSERT INTO public.client_details (number_personal_account, email, password, region, passport, contract_date, contract_number) VALUES (4, 'sriste0@gmail.com', '$2y$10$Lv.JQZMT985/dC0x4GYu7exD6S6SulXNvqw0fIntXvyammJDbll4G', 'Владимирская область', '4444444444', '2022-12-04', '444444444444444');
INSERT INTO public.client_details (number_personal_account, email, password, region, passport, contract_date, contract_number) VALUES (5, 'rmehaffey3@gmail.com', '$2y$10$Lv.JQZMT985/dC0x4GYu7exD6S6SulXNvqw0fIntXvyammJDbll4G', 'Костромская область', '5555555555', '2022-12-05', '555555555555555');
INSERT INTO public.client_details (number_personal_account, email, password, region, passport, contract_date, contract_number) VALUES (6, 'rfurmonger1@gmail.com', '$2y$10$Lv.JQZMT985/dC0x4GYu7exD6S6SulXNvqw0fIntXvyammJDbll4G', 'Республика Карелия', '6666666666', '2022-12-06', '666666666666666');
INSERT INTO public.client_details (number_personal_account, email, password, region, passport, contract_date, contract_number) VALUES (7, 'drenfrew2@gmail.com', '$2y$10$Lv.JQZMT985/dC0x4GYu7exD6S6SulXNvqw0fIntXvyammJDbll4G', 'Ростовская область', '7777777777', '2022-12-07', '777777777777777');

INSERT INTO public.phone_numbers (phone_number, balance, tariff_id) VALUES ('79113332211', '$500.00', '01');
INSERT INTO public.phone_numbers (phone_number, balance, tariff_id) VALUES ('72222222222', '$999.00', '02');
INSERT INTO public.phone_numbers (phone_number, balance, tariff_id) VALUES ('73333333333', '$999.00', '03');
INSERT INTO public.phone_numbers (phone_number, balance, tariff_id) VALUES ('74444444444', '$999.00', '04');
INSERT INTO public.phone_numbers (phone_number, balance, tariff_id) VALUES ('75555555555', '$999.00', '05');
INSERT INTO public.phone_numbers (phone_number, balance, tariff_id) VALUES ('76666666666', '$999.00', '06');
INSERT INTO public.phone_numbers (phone_number, balance, tariff_id) VALUES ('77777777777', '$999.00', '01');

INSERT INTO public.users (role) VALUES ('USER');
INSERT INTO public.users (role) VALUES ('USER');
INSERT INTO public.users (role) VALUES ('USER');
INSERT INTO public.users (role) VALUES ('USER');
INSERT INTO public.users (role) VALUES ('USER');
INSERT INTO public.users (role) VALUES ('USER');
INSERT INTO public.users (role) VALUES ('USER');




CREATE OR REPLACE FUNCTION get_calls_report(
    phone_number_param varchar(16),
    start_date_param timestamp,
    end_date_param timestamp
) RETURNS TABLE (
                    tariff_code char(2),
                    client_phone_number varchar(16),
                    total_duration interval,
                    total_cost numeric,
                    calls_count bigint,
                    call_type_code char(2),
                    start_date timestamp,
                    end_date timestamp,
                    call_cost numeric
                ) AS $$
DECLARE
    client_id_var bigint;
    tariff_code_var char(2);
BEGIN
    -- Получаем информацию о коде тарифа клиента
    SELECT
        client_id,
        tariff_id
    INTO
        client_id_var,
        tariff_code_var
    FROM
        phone_numbers
    WHERE
        phone_number = phone_number_param;


    total_duration := (SELECT sum(date_end - date_start)
                       FROM call_history
                       WHERE client_phone_number_id = client_id_var
                         AND date_start BETWEEN start_date_param AND end_date_param);
    total_cost := (SELECT cast(sum(cost) as numeric)
                   FROM call_history
                   WHERE client_phone_number_id = client_id_var
                     AND date_start BETWEEN start_date_param AND end_date_param);
    calls_count := (SELECT count(*)
                    FROM call_history
                    WHERE client_phone_number_id = client_id_var
                      AND date_start BETWEEN start_date_param AND end_date_param);
    client_phone_number := phone_number_param;

    -- Возвращаем статистику звонков за выбранный период
    RETURN QUERY
        SELECT
            tariff_code_var,
            client_phone_number,
            total_duration,
            total_cost,
            calls_count,
            call_type_id,
            date_start,
            date_end,
            cast(cost as numeric)
        FROM
            call_history
        WHERE
            client_phone_number_id = client_id_var
          AND date_start BETWEEN start_date_param AND end_date_param;
END;
$$ LANGUAGE plpgsql;
