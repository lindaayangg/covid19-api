package com.example.covid19api.service;

import com.example.covid19api.model.Country;
import com.example.covid19api.repository.CountryRepository;
import com.example.covid19api.utils.ReadCSV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class DataService {

    @Autowired
    private CountryService countryService;

    @Autowired
    private CountryRepository countryRepository;

    public void saveCountryData(String file) {
        List<String[]> data = ReadCSV.readCSVFile(file);
        for (int i = 1; i < data.size(); i++) {
            String[] row = data.get(i);
            Country countryResult = countryService.findCountry(row[3])
                                                  .orElseThrow(() -> new EntityNotFoundException("country " + row[7] + " is not found"));
            int confirmed = countryResult.confirmed;
            int deaths = countryResult.deaths;
            int recovered = countryResult.recovered;
            int active = countryResult.active;

            confirmed += Integer.parseInt(row[7]);
            deaths += Integer.parseInt(row[8]);
            recovered += Integer.parseInt(row[9]);
            active += Integer.parseInt(row[10]);

            countryResult.setConfirmed(confirmed);
            countryResult.setDeaths(deaths);
            countryResult.setRecovered(recovered);
            countryResult.setActive(active);
            countryRepository.save(countryResult);
        }
    }

    public void resetCountryData() {
        countryRepository.findAll()
                         .forEach(country -> {
                             country.setConfirmed(0);
                             country.setDeaths(0);
                             country.setRecovered(0);
                             country.setActive(0);
                             countryRepository.save(country);
                         });
    }
}
//
//    public LatestDataGlobal latestDataGlobal(String file) {
//        Collection<LatestDataByCountry> latestDataByCountry = latestDataByCountry(file).values();
//        int confirmed = 0;
//        int deaths = 0;
//        int recovered = 0;
//        for (LatestDataByCountry data : latestDataByCountry) {
//            confirmed += data.confirmed;
//            deaths += data.deaths;
//            recovered += data.recovered;
//        }
//        return new LatestDataGlobal(confirmed,
//                                    deaths,
//                                    recovered);
//    }
//
//    public TreeMap<String, LatestDataByCountry> latestDataByCountry(String file) {
//        String countryTable = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/UID_ISO_FIPS_LookUp_Table.csv";
//        TreeMap<String, LatestDataByCountry> dataByCountry = new TreeMap<>();
//        List<String[]> data = ReadCSV.readCSVFile(file);
//        for (int i = 1; i < data.size(); i++) {
//            String[] row = data.get(i);
//            if (!dataByCountry.containsKey(row[3])) {
//                TreeMap<String, Country> countryMapResult = countryService.groupProvincesToCountry(countryTable);
//                Coordinate countryCoordinate = countryMapResult.get(row[3]).countryCoordinate;
//                LatestDataByCountry latestDataByCountry = new LatestDataByCountry(row[3],
//                                                                                  countryCoordinate,
//                                                                                  Integer.parseInt(row[7]),
//                                                                                  Integer.parseInt(row[8]),
//                                                                                  Integer.parseInt(row[9]));
//                dataByCountry.put(row[3], latestDataByCountry);
//            } else {
//                dataByCountry.get(row[3]).confirmed += Integer.parseInt(row[7]);
//                dataByCountry.get(row[3]).deaths += Integer.parseInt(row[8]);
//                dataByCountry.get(row[3]).recovered += Integer.parseInt(row[9]);
//            }
//        }
//        return dataByCountry;
//    }
//
//    public TreeMap<String, LatestDataByCountryGrouped> latestDataWithLocationsGrouped(String file) {
//        String countryTable = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/UID_ISO_FIPS_LookUp_Table.csv";
//        TreeMap<String, LatestDataByCountryGrouped> dataByCountryGrouped = new TreeMap<>();
//        List<String[]> data = ReadCSV.readCSVFile(file);
//        for (int i = 1; i < data.size(); i++) {
//            String[] row = data.get(i);
//            if (!dataByCountryGrouped.containsKey(row[3])) {
//                TreeMap<String, Country> countryMapResult = countryService.groupProvincesToCountry(countryTable);
//                Coordinate countryCoordinate = countryMapResult.get(row[3]).countryCoordinate;
//
//                TreeMap<String, LatestDataByLocation> latestDataByLocationTreeMap = new TreeMap<>();
//                if (!row[2].equals("Recovered")) {
//                    if (!row[2].equals("")) {
//                        TreeMap<String, Location> locationMapResult = countryService.createLocations(countryTable);
//                        Coordinate locationCoordinate = locationMapResult.get(row[2]).coordinate;
//                        latestDataByLocationTreeMap.put(row[2], new LatestDataByLocation(row[2],
//                                                                                         locationCoordinate,
//                                                                                         Integer.parseInt(row[7]),
//                                                                                         Integer.parseInt(row[8]),
//                                                                                         Integer.parseInt(row[9])));
//                    }
//                }
//                LatestDataByCountryGrouped latestDataByCountryGrouped = new LatestDataByCountryGrouped(row[3],
//                                                                                                       countryCoordinate,
//                                                                                                       Integer.parseInt(row[7]),
//                                                                                                       Integer.parseInt(row[8]),
//                                                                                                       Integer.parseInt(row[9]),
//                                                                                                       Optional.of(
//                                                                                                               latestDataByLocationTreeMap));
//                dataByCountryGrouped.put(row[3], latestDataByCountryGrouped);
//            } else {
//                dataByCountryGrouped.get(row[3]).confirmed += Integer.parseInt(row[7]);
//                dataByCountryGrouped.get(row[3]).deaths += Integer.parseInt(row[8]);
//                dataByCountryGrouped.get(row[3]).recovered += Integer.parseInt(row[9]);
//
//                if (!row[2].equals("Recovered")) {
//                    if (!row[2].equals("")) {
//                        boolean provinceStateExistsInMap = dataByCountryGrouped.get(row[3]).latestDataByLocations
//                                .get()
//                                .containsKey(row[2]);
//
//                        if (!provinceStateExistsInMap) {
//                            TreeMap<String, Location> locationMapResult = countryService.createLocations(countryTable);
//                            Coordinate locationCoordinate = locationMapResult.get(row[2]).coordinate;
//                            dataByCountryGrouped.get(row[3]).latestDataByLocations
//                                    .get()
//                                    .put(row[2], new LatestDataByLocation(row[2],
//                                                                          locationCoordinate,
//                                                                          Integer.parseInt(row[7]),
//                                                                          Integer.parseInt(row[8]),
//                                                                          Integer.parseInt(row[9])));
//                        } else {
//                            dataByCountryGrouped.get(row[3]).latestDataByLocations.get().get(row[2]).confirmed += Integer.parseInt(row[7]);
//                            dataByCountryGrouped.get(row[3]).latestDataByLocations.get().get(row[2]).deaths += Integer.parseInt(row[8]);
//                            dataByCountryGrouped.get(row[3]).latestDataByLocations.get().get(row[2]).recovered += Integer.parseInt(row[9]);
//                        }
//                    }
//                }
//            }
//        }
//        return dataByCountryGrouped;
//    }
//
//    public List<LocationConfirmedData> confirmedDataByLocation(String file) {
//        List<LocationConfirmedData> locationConfirmedDataList = new ArrayList<>();
//        List<String[]> data = ReadCSV.readCSVFile(file);
//        for (int i = 1; i < data.size(); i++) {
//            String[] row = data.get(i);
//            if (!row[0].equals("Recovered")) {
//                int confirmed = Integer.parseInt(row[row.length - 1]);
//                LocationConfirmedData locationConfirmedData = new LocationConfirmedData(row[1],
//                                                                                        Optional.of(row[0]),
//                                                                                        row[2],
//                                                                                        row[3],
//                                                                                        confirmed);
//                locationConfirmedDataList.add(locationConfirmedData);
//            }
//        }
//        return locationConfirmedDataList;
//    }
//
//    public List<LocationDeathData> deathDataByLocation(String file) {
//        List<LocationDeathData> locationDeathDataList = new ArrayList<>();
//        List<String[]> data = ReadCSV.readCSVFile(file);
//        for (int i = 1; i < data.size(); i++) {
//            String[] row = data.get(i);
//            if (!row[0].equals("Recovered")) {
//                int deaths = Integer.parseInt(row[row.length - 1]);
//                LocationDeathData locationDeathData = new LocationDeathData(row[1],
//                                                                            Optional.of(row[0]),
//                                                                            row[2],
//                                                                            row[3],
//                                                                            deaths);
//                locationDeathDataList.add(locationDeathData);
//            }
//        }
//        return locationDeathDataList;
//    }
//
//    public List<LocationRecoveredData> recoveredDataByLocation(String file) {
//        List<LocationRecoveredData> locationRecoveredDataList = new ArrayList<>();
//        List<String[]> data = ReadCSV.readCSVFile(file);
//        for (int i = 1; i < data.size(); i++) {
//            String[] row = data.get(i);
//            int recovered = Integer.parseInt(row[row.length - 1]);
//            LocationRecoveredData locationRecoveredData = new LocationRecoveredData(row[1],
//                                                                                    Optional.of(row[0]),
//                                                                                    row[2],
//                                                                                    row[3],
//                                                                                    recovered);
//            locationRecoveredDataList.add(locationRecoveredData);
//        }
//        return locationRecoveredDataList;
//    }
//}
