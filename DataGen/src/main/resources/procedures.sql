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