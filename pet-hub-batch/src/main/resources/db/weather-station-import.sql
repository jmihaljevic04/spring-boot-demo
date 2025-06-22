drop procedure if exists weather_station_import;

create procedure weather_station_import(importId bigint, enabled_countries text[])
    language plpgsql
as
$$
begin

    insert into weather_station_staging(external_id, name, country, timezone, latitude, longitude, elevation, import_id)
    select jdata.external_id,
           jdata.name,
           upper(jdata.country),
           jdata.timezone,
           jdata.latitude,
           jdata.longitude,
           jdata.elevation,
           importId
    from (select jt.*
          from json_table((select file_content from weather_station_json_temp),
                          '$[*]' columns (external_id text path '$.id',
                              name text path '$.name',
                              country text path '$.country',
                              timezone text path '$.timezone',
                              latitude decimal(3, 2) path '$.latitude',
                              longitude decimal(3, 2) path '$.longitude',
                              elevation decimal(3, 2) path '$.elevation'
                              )) as jt) as jdata;

    update weather_station_staging set enabled = true where country in (enabled_countries);

end;
$$;
