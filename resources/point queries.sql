CREATE EXTENSION postgis;


-- Table: temp_points

-- DROP TABLE temp__points;

CREATE TABLE temp_points
(
  id SERIAL NOT NULL,
  latitude double precision,
  longitude double precision,
  weight integer,
  type character varying(12),
  CONSTRAINT temp_points_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);


--  in psql => 
-- \copy temp_points (latitude, longitude, weight) from 'C:\\src\\Geocoder\\resources\\test_weighted_points.csv' with HEADER DELIMITER AS ',' CSV

SELECT AddGeometryColumn ('temp_points','geom',4326,'POINT',2);

update temp_points set geom = st_setsrid(st_makepoint(latitude, longitude),4326)

select count (*) 
from temp_grouped_points
where ST_Contains((select geom from city_boundary), geom)


select st_astext(st_centroid(geom)) from city_boundary



CREATE TABLE temp_grouped_points
(
  id SERIAL NOT NULL,
  latitude double precision,
  longitude double precision,
  weight integer,
  grouping integer,
  type character varying(12),
  CONSTRAINT temp_grouped_points_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

-- I'm reversing latitude and longitude here, because at some point I got it backwards.  It didn't matter for any previous calculations,
--   but now that I'm creating KML files, it matters.
-- in psql => 
-- \copy temp_grouped_points (latitude, longitude, weight, grouping, type) from 'C:\\src\\Geocoder\\resources\\chicago_trauma_for_import.csv' with HEADER DELIMITER AS ',' CSV

-- delete from temp_grouped_points

-- old function -- SELECT AddGeometryColumn ('temp_grouped_points','geom',4326,'POINT',2);

-- ALTER TABLE temp_grouped_points DROP COLUMN geom

ALTER TABLE temp_grouped_points ADD COLUMN geom geometry(Point,4326);

update temp_grouped_points set geom = st_setsrid(st_makepoint(latitude, longitude),4326)



select ST_AsKML(ST_Union(ST_Expand(geom, .0051)), 5)
from temp_grouped_points
where weight < 6

select count(*) from temp_grouped_points where weight < 6 and weight is not null and weight > 0

select * from temp_grouped_points where weight < 6 limit 100


Select 30, ST_AsKML(ST_SymDifference(a.the_geom ,b.the_geom), 5)
from 
	(select ST_Union(ST_Expand(geom, .0051)) as the_geom
	from temp_grouped_points
	where weight >= 30) as a,
	(select ST_Union(ST_Expand(geom, .0051)) as the_geom
	from temp_grouped_points
	where weight >= 27) as b


CREATE TYPE kml_result AS (f1 int, f2 text);

CREATE OR REPLACE FUNCTION retrieveKml(value INT, increment INT, out f2 text)
AS
'Select ST_AsKML(ST_SymDifference(a.the_geom ,b.the_geom), 5)
from 
	(select ST_Union(ST_Expand(geom, .0051)) as the_geom
	from temp_grouped_points
	where weight >= (value + increment)) as a,
	(select ST_Union(ST_Expand(geom, .0051)) as the_geom
	from temp_grouped_points
	where weight >= value) as b'
LANGUAGE SQL;

CREATE OR REPLACE FUNCTION dup(value INT, increment int) RETURNS setof kml_result AS 
$$
	DECLARE r text;
	DECLARE i int := value;
        BEGIN
                WHILE i > 0 LOOP
			SELECT retrieveKml(i, increment) into r;
			return next (i, r);
			i = i - increment;
                END LOOP;
        END;
$$
LANGUAGE plpgsql;

select * from dup(36, 6)









CREATE OR REPLACE FUNCTION get_domains_n(lname varchar, geom varchar, gid varchar, radius numeric)
    RETURNS SETOF record AS
$$
DECLARE
    lid_new    integer;
    dmn_number integer := 1;
    outr       record;
    innr       record;
    r          record;
BEGIN

    DROP TABLE IF EXISTS tmp;
    EXECUTE 'CREATE TEMPORARY TABLE tmp AS SELECT '||gid||', '||geom||' FROM '||lname;
    ALTER TABLE tmp ADD COLUMN dmn integer;
    ALTER TABLE tmp ADD COLUMN chk boolean DEFAULT FALSE;
    EXECUTE 'UPDATE tmp SET dmn = '||dmn_number||', chk = FALSE WHERE '||gid||' = (SELECT MIN('||gid||') FROM tmp)';

    LOOP
        LOOP
            FOR outr IN EXECUTE 'SELECT '||gid||' AS gid, '||geom||' AS geom FROM tmp WHERE dmn = '||dmn_number||' AND NOT chk' LOOP
                FOR innr IN EXECUTE 'SELECT '||gid||' AS gid, '||geom||' AS geom FROM tmp WHERE dmn IS NULL' LOOP
                    IF ST_DWithin(ST_Transform(ST_SetSRID(outr.geom, 4326), 3785), ST_Transform(ST_SetSRID(innr.geom, 4326), 3785), radius) THEN
                    --IF ST_DWithin(outr.geom, innr.geom, radius) THEN
                        EXECUTE 'UPDATE tmp SET dmn = '||dmn_number||', chk = FALSE WHERE '||gid||' = '||innr.gid;
                    END IF;
                END LOOP;
                EXECUTE 'UPDATE tmp SET chk = TRUE WHERE '||gid||' = '||outr.gid;
            END LOOP;
            SELECT INTO r dmn FROM tmp WHERE dmn = dmn_number AND NOT chk LIMIT 1;
            EXIT WHEN NOT FOUND;
       END LOOP;
       SELECT INTO r dmn FROM tmp WHERE dmn IS NULL LIMIT 1;
       IF FOUND THEN
           dmn_number := dmn_number + 1;
           EXECUTE 'UPDATE tmp SET dmn = '||dmn_number||', chk = FALSE WHERE '||gid||' = (SELECT MIN('||gid||') FROM tmp WHERE dmn IS NULL LIMIT 1)';
       ELSE
           EXIT;
       END IF;
    END LOOP;

    RETURN QUERY EXECUTE 'SELECT ST_ConvexHull(ST_Collect('||geom||')) FROM tmp GROUP by dmn';

    RETURN;
END
$$
LANGUAGE plpgsql;


SELECT * FROM get_domains_n('temp_weighted_points', 'geom', 'id', 1000) AS g(gm geometry)


select * from temp_weighted_points



select ST_Distance((select geom from temp_weighted_points where id = 1), (select geom from temp_weighted_points where id = 2))



SELECT
    array_agg(id) AS ids,
    COUNT( geom ) AS count,
    ST_AsText( ST_Centroid(ST_Collect( geom )) ) AS center
FROM temp_weighted_points
where weight >= 500
GROUP BY
    ST_SnapToGrid( ST_SetSRID(geom, 4326), 22.25, 11.125)
ORDER BY
    count DESC
;


select * from temp_weighted_points where id in (13,14,22,23,24,25,32,33,34,35,43,44,45,55,66,77,78,79,88,89,90,91)


select ST_AsText(ST_ConvexHull(ST_Collect(geom))) from temp_weighted_points where weight > 2000


select ST_ConcaveHull(ST_Collect(geom), 1) from temp_weighted_points where weight > 2000
