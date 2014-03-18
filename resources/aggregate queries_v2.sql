


CREATE TABLE temp_weighted_points
(
  id SERIAL NOT NULL,
  latitude double precision,
  longitude double precision,
  weight integer,
  type character varying(12),
  CONSTRAINT temp_weighted_points_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

-- in psql => 
-- \copy temp_weighted_points (latitude, longitude, weight, type) from 'C:\\src\\Geocoder\\resources\\chicago_trauma_for_import.csv' with HEADER DELIMITER AS ',' CSV

-- delete from temp_weighted_points

-- old function -- SELECT AddGeometryColumn ('temp_weighted_points','geom',4326,'POINT',2);

-- ALTER TABLE temp_weighted_points DROP COLUMN geom

ALTER TABLE temp_weighted_points ADD COLUMN geom geometry(Point,4326);

update temp_weighted_points set geom = st_setsrid(st_makepoint(longitude, latitude),4326)


ALTER TABLE temp_weighted_points ADD COLUMN minute_weight int

update temp_weighted_points set minute_weight = weight / 60


select ST_AsKML(ST_Union(ST_Expand(geom, .0051)), 5)
from temp_weighted_points
where minute_weight < 6

select * from temp_weighted_points where minute_weight < 6 limit 100


Select 30, ST_AsKML(ST_SymDifference(a.the_geom ,b.the_geom), 5)
from 
	(select ST_Union(ST_Expand(geom, .0051)) as the_geom
	from temp_weighted_points
	where minute_weight >= 30) as a,
	(select ST_Union(ST_Expand(geom, .0051)) as the_geom
	from temp_weighted_points
	where minute_weight >= 27) as b


CREATE TYPE kml_result AS (f1 int, f2 text);

CREATE OR REPLACE FUNCTION retrieveKml(value INT, increment INT, hType varchar, out f2 text)
AS
'Select ST_AsKML(ST_SymDifference(a.the_geom ,b.the_geom), 5)
from 
	(select ST_Union(ST_Expand(geom, .0051)) as the_geom
	from temp_weighted_points
	where minute_weight >= (value + increment) and type = hType) as a,
	(select ST_Union(ST_Expand(geom, .0051)) as the_geom
	from temp_weighted_points
	where minute_weight >= value and type = hType) as b'
LANGUAGE SQL;

CREATE OR REPLACE FUNCTION dup(value INT, increment int, type varchar) RETURNS setof kml_result AS 
$$
	DECLARE r text;
	DECLARE i int := value;
        BEGIN
                WHILE i >= 0 LOOP
			SELECT retrieveKml(i, increment, type) into r;
			return next (i, r);
			i = i - increment;
                END LOOP;
        END;
$$
LANGUAGE plpgsql;

select * from dup(42, 6, 'trauma')


select ST_AsKML(ST_Union(ST_Expand(geom, .0051)), 5)
from temp_weighted_points 
where type = 'trauma' and minute_weight < 5




Select ST_AsKML(ST_Union(ST_Expand(geom, .0051)), 5)
from temp_weighted_points
where minute_weight <= 30 and  minute_weight > 27 and type = 'trauma'



CREATE OR REPLACE FUNCTION dup(value INT, increment int, min int, type varchar) RETURNS setof kml_result AS 
$$
	DECLARE r text;
	DECLARE i int := value;
        BEGIN
                WHILE i >= min LOOP
			SELECT retrieveKml(i, increment, type) into r;
			return next (i, r);
			i = i - increment;
                END LOOP;
        END;
$$
LANGUAGE plpgsql;


select * from dup(40, 5, 10, 'trauma')

Select 30, ST_AsKML(ST_SymDifference(a.the_geom ,b.the_geom), 5)
from 
	(select ST_Union(ST_Expand(geom, .0051)) as the_geom
	from temp_weighted_points
	where minute_weight >= 10) as a,
	(select ST_Union(ST_Expand(geom, .0051)) as the_geom
	from temp_weighted_points
	where minute_weight >= 0) as b

select ST_AsKML(ST_Union(ST_Expand(geom, .0051)), 5)
from temp_weighted_points
where minute_weight <= 35 and type = 'trauma'





CREATE OR REPLACE FUNCTION retrieveKmlDifference(value INT, increment INT, hType varchar, out f2 text)
AS
'Select ST_AsKML(ST_SymDifference(a.the_geom ,b.the_geom), 5)
from 
	(select ST_Union(ST_Expand(geom, .0051)) as the_geom
	from temp_weighted_points
	where minute_weight >= (value + increment) and type = hType) as a,
	(select ST_Union(ST_Expand(geom, .0051)) as the_geom
	from temp_weighted_points
	where minute_weight >= value and type = hType) as b'
LANGUAGE SQL;

drop function generateKmlTable(int, int, int, varchar)

CREATE OR REPLACE FUNCTION generateKmlTable(value INT, increment int, min int, hType varchar) RETURNS setof kml_result AS 
$$
	DECLARE r text;
	DECLARE i int := value;
        BEGIN
		select ST_AsKML(ST_Union(ST_Expand(geom, .0051)), 5) from temp_weighted_points where minute_weight >= value and type =  hType into r;
		return next (value, r);
		i = i - increment;
		
                WHILE i >= min LOOP
			SELECT retrieveKmlDifference(i, increment,  hType) into r;
			return next (i, r);
			i = i - increment;
                END LOOP;

                Select ST_AsKML(ST_SymDifference(a.the_geom ,b.the_geom), 5) from 
			(select ST_Union(ST_Expand(geom, .0051)) as the_geom from temp_weighted_points where minute_weight >= min and type = hType) as a,
			(select ST_Union(ST_Expand(geom, .0051)) as the_geom from temp_weighted_points where minute_weight >= 0 and type = hType) as b into r;
		return next (0, r);

        END;
$$
LANGUAGE plpgsql;


select * from generateKmlTable(35, 5, 10, 'trauma')

select * from temp_weighted_points limit 10

insert into temp_weighted_points (latitude, longitude, type, weight)
select a.latitude, a.longitude, 'difference', a.weight - b.weight as new_weight from temp_weighted_points a
inner join temp_weighted_points b on a.geom = b.geom and b.type = 'potential'
where a.type = 'trauma'
order by new_weight desc


update temp_weighted_points set minute_weight = CEILING(1.0*weight/60) where type = 'difference'

select * from temp_weighted_points where type = 'difference' order by weight desc




select * from dup(21, 3, 0, 'difference')

Select 3, ST_AsKML(ST_SymDifference(a.the_geom ,b.the_geom), 5)
from 
	(select ST_Union(ST_Expand(geom, .0051)) as the_geom
	from temp_weighted_points
	where minute_weight >= 3 and type = 'difference') as a,
	(select ST_Union(ST_Expand(geom, .0051)) as the_geom
	from temp_weighted_points
	where minute_weight >= 1 and type = 'difference') as b

select ST_AsKML(ST_Union(ST_Expand(geom, .0051)), 5)
from temp_weighted_points
where minute_weight >= 18 and type = 'difference'



select 299/100