drop table if exists client_details cascade;

drop table if exists call_history cascade;

drop table if exists phone_numbers cascade;

drop table if exists clients cascade;

drop table if exists tariffs_config cascade;

drop table if exists tariffs cascade;

drop table if exists internet_packages cascade;

drop table if exists telephony_packages cascade;

drop table if exists call_types cascade;

drop table if exists telecom_operators cascade;

drop table if exists users cascade;

drop function if exists get_calls_report(phone_number_param varchar, start_date_param timestamp, end_date_param timestamp);
