PRAGMA foreign_keys = ON;

CREATE TABLE Date
(
    ID      INTEGER PRIMARY KEY AUTOINCREMENT,
    dateRep TEXT    NOT NULL UNIQUE,
    day     INTEGER NOT NULL,
    month   INTEGER NOT NULL,
    year    INTEGER NOT NULL
);

CREATE TABLE CountriesAndTerritories
(
    ID                      INTEGER PRIMARY KEY AUTOINCREMENT,
    geoId                   TEXT NOT NULL UNIQUE,
    countriesAndTerritories TEXT NOT NULL UNIQUE ,
    countryterritoryCode    TEXT NOT NULL UNIQUE
);

CREATE TABLE PopAndContinent
(
    geo_ID       INTEGER NOT NULL,
    popDate2020  INTEGER NOT NULL,
    continentExp TEXT    NOT NULL,
    FOREIGN KEY (geo_ID) references CountriesAndTerritories (ID)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE CasesAndDeaths
(
    data_ID INTEGER,
    geo_ID  INTEGER,
    cases   INTEGER,
    deaths  INTEGER,
    PRIMARY KEY (data_ID, geo_ID),
    FOREIGN KEY (data_ID) references Date (ID)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (geo_ID) REFERENCES CountriesAndTerritories (ID)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);