

CREATE OR REPLACE FUNCTION makegrid(geometry, integer)
RETURNS geometry AS
'SELECT ST_Collect(st_setsrid(ST_POINT(x/1000000::float,y/1000000::float),st_srid($1))) FROM 
  generate_series(floor(st_xmin($1)*1000000)::int, ceiling(st_xmax($1)*1000000)::int,$2) as x ,
  generate_series(floor(st_ymin($1)*1000000)::int, ceiling(st_ymax($1)*1000000)::int,$2) as y 
WHERE st_intersects($1,ST_SetSRID(ST_POINT(x/1000000::float,y/1000000::float),ST_SRID($1)))'
LANGUAGE sql


-- DONT USE THESE - I didn't add the gid for them
-- 114138 ms
--SELECT makegrid(geom, 1000) from tl_2009_17_place;

-- 333000 ms and quit before it finished
--SELECT makegrid(geom, 100) from tl_2009_17_place;

-- 1202 ms, about a third are blank
--SELECT makegrid(geom, 10000) from tl_2009_17_place;

-- 4245ms - 48543 rows
--SELECT st_astext(makegrid(geom, 5000)) from tl_2009_17_place;

-- 15254 ms and 65804 rows
SELECT st_astext((ST_Dump(makegrid(geom, 1000))).geom) as geom from tl_2009_17_place where gid = 1108;

-- I think this is the best one.  1000 is too many points, but 5000 looks good.  I could narrow it down a bit if necessary to 3000.
-- 641ms and 2622 rows
select st_y(geom), st_x(geom) from
(SELECT (ST_Dump(makegrid(geom, 5000))).geom as geom from tl_2009_17_place where gid = 1108) a

select * from spatial_ref_sys where srid = 9102671


insert into 

+proj=tmerc +lat_0=36.66666666666666 +lon_0=-88.33333333333333 +k=0.9999749999999999 +x_0=300000 +y_0=0 +ellps=GRS80 +datum=NAD83 +to_meter=0.3048006096012192 +no_defs 








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


--  in psql => 
-- \copy temp_points (latitude, longitude, weight) from 'C:\\src\\Geocoder\\resources\\test_weighted_points.csv' with HEADER DELIMITER AS ',' CSV

SELECT AddGeometryColumn ('temp_grouped_points','geom',4326,'POINT',2);

update temp_grouped_points set geom = st_setsrid(st_makepoint(latitude, longitude),4326)

-- this will get some of the contours
select ST_AsText(ST_ConcaveHull(ST_Collect(geom), 1)) from temp_grouped_points where weight = 15 and grouping = 14

select weight, grouping, ST_AsText(ST_ConcaveHull(ST_Collect(geom), .71)) from temp_grouped_points 
group by weight, grouping
order by weight, grouping
