package finalproject.ss3v2.service;

import finalproject.ss3v2.domain.Utilities;
import finalproject.ss3v2.repository.UtilitiesRepository;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

@Service
public class CsvDataService {
    // Because getting the rest of the fields to complete the cost living profile data can take to long, an Excel file
    // is being created to provide that data, this .csv is being created with info from " the Bureau of Labor Statistics,
    // the Energy Information Administration, Energy.gov and AAA gas prices". The fields feed from
    // this search are: fuel cost, waste services cost, water services cost, transportation cost, natural gas cost and
    // internet cost.
    //https://www.forbes.com/home-improvement/living/monthly-utility-costs-by-state/

    private final UtilitiesRepository utilitiesRepository;

    public CsvDataService(UtilitiesRepository utilitiesRepository) {
        this.utilitiesRepository = utilitiesRepository;
    }

    public void createDbWithCsvData() {
        // This method will create the database with the data from the csv file
        String csvFile = "UtilitiesCostStatesManualFed.csv";
        String line = "";
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String headerLine = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(cvsSplitBy);
                Utilities utilities = new Utilities();
                utilities.setStateCode(data[0]);
                utilities.setStateName(data[1]);
                utilities.setGasRegularCost(Double.parseDouble(data[2]));
                utilities.setGasMidGradeCost(Double.parseDouble(data[3]));
                utilities.setGasPremiumCost(Double.parseDouble(data[4]));
                utilities.setGasDieselCost(Double.parseDouble(data[5]));
                utilities.setMonthlyWaterCost(Double.parseDouble(data[6]));
                utilities.setMonthlyInternetCost(Double.parseDouble(data[7]));
                utilities.setMonthlyNaturalGasCost(Double.parseDouble(data[8]));
                utilities.setMonthlyWasteCost(Double.parseDouble(data[9]));
                utilities.setPublicTransportationCost(Double.parseDouble(data[10]));
                utilitiesRepository.save(utilities);
            }
        } catch (IOException e) {
            e.printStackTrace();

        }

    }
}
