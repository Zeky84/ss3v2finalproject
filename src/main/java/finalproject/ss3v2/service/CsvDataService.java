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
        if(utilitiesRepository.count()==0){// I was having an error because in deployment, to avoid overwriting the data in the database
            // I was using none, and it was creating a double record in the db, finding a double values of each state, with this "if condition", is working well, the same i used for
            //states and metro areas in the ApiServiceHudUser class
            // This method will create the database with the data from the csv file

            //In Java 7, the try-with-resources statement was introduced to simplify old buffered reader pattern.
            // The try-with-resources statement automatically handles resource management for any object that implements
            // the AutoCloseable interface (which includes Closeable like BufferedReader, FileReader, etc.), so no need to
            // explicitly close the resources.

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

    }else{
        System.out.println("The database is already populated with the utilities data");
    }


    }
}
