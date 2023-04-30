SELECT CAT.geoId,
       (SUM(CasesAndDeaths.deaths) * 100.0 / SUM(CasesAndDeaths.cases)) || '%' as deathsPercentage
FROM CasesAndDeaths
         JOIN CountriesAndTerritories CAT on CasesAndDeaths.geo_ID = CAT.ID
GROUP BY CAT.geoId
ORDER BY deathsPercentage DESC;