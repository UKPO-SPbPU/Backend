<div align="center">
<img src="polytech-logo.svg" style="width: 25em;" alt="Peter the Great
St. Petersburg Polytechnic
University" title="POLYTECH"/>
<h1>Billing System Model<br>(Backend)</h1>

Backend-часть курсового проекта курса "Технологии разработки качественного программного обеспечения"
</div>


---

12
## Описание проекта
**_Billing System Model_** - это курсовой проект курса "_Технологии разработки качественного программного обеспечения_",
в котором реализуется система имитации работы оператора связи.

## Используемый стек технологий
- Java (JDK 21)
- Сборщик проекта - Apache Maven (3.9.6)
- Spring Framework 6:
  - Spring Boot (3.2.2);
  - Spring Data JPA (3.2.2);
  - Spring Security 6 (JWT tokens);
  - Spring Cloud Config (4.1.0).
- PostgreSQL 15
- Брокер сообщений - Apache ActiveMQ Artemis
- Контейнеризация - Docker (Docker-Compose)
- Тестирование - JUnit 5 + Mockito

## Описание архитектуры и модулей проекта
Проект разработан на основе микросервисной архитектуры и сожержит следующие подсистемы:

### Data Gen
Данный микросервис отвечает за:
1. генерацию звонков (CDR-файл);
   1. CDR (Call Data Record) - файл истории звонков абонентов
2. наполнение тестовыми данными (тарифы, клиенты и т.п.) базы данных.

### BRT (Billing Real Time)
Данный микросервис отвечает за:
1. генерацию CDR+ файла на основе данных из CDR;
   1. CDR+ (Call Data Record+) - файл истории звонков абонентов + их тарифы
2. запуск HRS сервиса для создания отчёта о потраченных средствах клиентов на основе CDR+;
3. изменение баланса зарегестрированных пользователей на основе отчёта от микросервиса HRS.

### HRS (High performance Rating Server)
Данный сервис занимается обработкой CDR+ файла, на сонове которого составляет отчёт
о количестве потраченных минут и средств клиентов.

### CRM (Customer Relationship Management)
Данный сервис предоставляет REST интерфейс для:
1. регистрации нового пользователя (Spring Security);
2. аутентификацию пользователя (Spring Security);
3. смены пароля пользователя;
4. смены тарифа пользователя;
5. пополнения баланса пользователя;
6. изменения данных существующего пользователя;
7. получения данных пользователя по номеру телефона;
8. запуск процесса тарификации клиентов (Data Gen => BRT => HRS => BRT);
9. получения списка всех тарифов;
10. получения тарифа по номеру телефона.

### Config Server
Данный сервис использует Spring Cloud Config Server и отвечает за настройку вышеописанных сервисов.

### Модуль Common
Данный модуль хранит общие зависимости для вышеописанных сервисов.\
Не является отдельным микросервисом.


## Инструкция по запуску проекта
Данное приложение поддерживает два профиля запуска:
1. **dev** - запуск приложения без использования Docker. Данный профиль используется для разработки и дебага проекта.\
**Внимание!** Если вы запускаете приложение с импользованием данного профиля запуска,
то на вашем компьютере должны быть запущены СУБД PostgreSQL и Apache ActiveMQ Artemis
(локально или в виде контейнера Docker).
2. **docker** - развёртывание приложение в Docker при помощи docker-compose.yml.\
**Внимание!** На момент написания данного README.md файла (08.02.2024) docker-compose.yml по какой-то причине
не работает на последних версиях docker-desktop, поэтому если у вас возникает ошибка\
`ERROR: load build context `<br>
`failed to solve: changes out of order: "target/<service_name>.jar"`\
при попытке запуска docker-compose.yml, то установите версию docker-desktop v4.20.0

### Предстартовая подготовка
Чтобы запустить данное приложение, необходимо в корне проекта создать файл `.env`, в котором указываются параметры
запуска сервисов и секреты (логины, пароли и другие секреты).\
Пример `.env` файла представлен ниже:

```
CONFIG_SERVER_PORT=8888
CRM_PORT=8080
DATA_GEN_PORT=8081
BRT_PORT=8082
HRS_PORT=8083

POSTGRESQL_PORT=5432
AMQ_ARTEMIS_PORT=61616

SPRING_ACTIVE_PROFILE=docker

POSTGRESQL_USER=admin
POSTGRESQL_PASSWORD=admin
POSTGRESQL_DB=billing_system

AMQ_ARTEMIS_USER=admin
AMQ_ARTEMIS_PASSWORD=admin

JWT_SECRET=47C6DFD979C465D64E72CAEF4595222F985B470FE6E85A776B50B9D6F86D6D5E

```
Поле `SPRING_ACTIVE_PROFILE=docker` отвечает за выбор профиля запуска приложения
(может быть либо `docker`, либо `dev`)

### Запуск приложения
Склонируйте проект в выбранную директорию и соберите его, используя команды ниже:
```bash
git clone https://github.com/BillingSystemModel/Backend.git
mvn clean install
```

#### dev профиль
Проверьте, что в `.env` файле есть параметр `SPRING_ACTIVE_PROFILE=dev`\
Порадок запуска сервисов на профиле dev:
1. PostgreSQL
2. Apache ActiveMQ Artemis
3. Config Server
4. Data Gen
5. BRT
6. HRS
7. CRM

#### docker профиль
Проверьте, что в `.env` файле есть параметр `SPRING_ACTIVE_PROFILE=docker`, а docker-desktop запущен.
Запустите docker-compose.yml файл:\
``` bash
docker-compose up
```


## Конфигурация
Конфигурация сервисов находится в
[/ConfigServer/src/main/resources](./ConfigServer/src/main/resources)

## Лицензия
Этот проект лицензирован в соответствии с условиями [LICENSE.md](LICENSE.md).
