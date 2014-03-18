SELECT id, latitude, longitude, weight, geom
  FROM temp_points

select * from temp_weighted_points 
where weight > 2300 and type = 'trauma'
order by weight desc 


SELECT AddGeometryColumn ('temp_weighted_points','geom',4326,'POINT',2);

update temp_weighted_points set geom = st_setsrid(st_makepoint(latitude, longitude),4326)


select * from temp_weighted_points limit 10



SELECT DISTINCT s1.latitude, s1.longitude, s1.geom, s1.id
FROM temp_weighted_points AS s1 
JOIN temp_weighted_points AS s2 ON ST_DWithin(s1.geom, s2.geom,1) 
WHERE (s1.id != s2.id) and s1.weight >= 2200