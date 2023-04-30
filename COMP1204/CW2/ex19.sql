SELECT dateRep,
       SUM(deaths) OVER (ORDER BY year, month, day) AS cumulativeDeaths,
       SUM(cases) OVER (ORDER BY year, month, day)  AS cumulativeCases
FROM CasesAndDeaths
         JOIN Date D on D.ID = CasesAndDeaths.data_ID
         JOIN CountriesAndTerritories CAT on CAT.ID = CasesAndDeaths.geo_ID
WHERE geoId = 'UK';
