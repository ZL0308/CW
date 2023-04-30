SELECT CAT.geoId,
       (SUM(CasesAndDeaths.cases) * 100.0 / PAC.popDate2020) || '%'  as casesPercentage,
       (SUM(CasesAndDeaths.deaths) * 100.0 / PAC.popDate2020) || '%' as deathsPercentage
FROM CasesAndDeaths
         JOIN CountriesAndTerritories CAT on CasesAndDeaths.geo_ID = CAT.ID
         JOIN PopAndContinent PAC ON CAT.ID = PAC.geo_ID
GROUP BY CAT.geoId
ORDER BY CAT.geoId;