SELECT dateRep, cases, deaths
FROM CasesAndDeaths
         JOIN Date D on CasesAndDeaths.data_ID = D.ID
         JOIN CountriesAndTerritories CAT on CasesAndDeaths.geo_ID = CAT.ID
WHERE geoId = 'UK'
ORDER BY year, month, day;