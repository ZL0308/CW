INSERT INTO Date (dateRep, day, month, year)
SELECT DISTINCT dateRep, day, month, year
FROM dataset;

INSERT INTO CountriesAndTerritories (geoId, countriesAndTerritories, countryterritoryCode)
SELECT DISTINCT geoId, countriesAndTerritories, countryterritoryCode
FROM dataset;


INSERT INTO PopAndContinent (geo_ID, popDate2020, continentExp)
SELECT DISTINCT C.ID, popData2020, continentExp
FROM dataset
         JOIN CountriesAndTerritories C ON dataset.geoId = C.geoId;

INSERT INTO CasesAndDeaths (data_ID, geo_ID, cases, deaths)
SELECT DISTINCT D.ID, C.ID, cases, deaths
FROM dataset
         JOIN Date D ON dataset.dateRep = D.dateRep
         JOIN CountriesAndTerritories C ON dataset.geoId = C.geoId;