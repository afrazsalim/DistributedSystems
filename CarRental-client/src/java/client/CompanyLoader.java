package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import rental.Car;
import rental.CarType;

public class CompanyLoader {
        
    public CrcData loadRental(String datafile) {
        try {
            return loadData(datafile);
        } catch (NumberFormatException ex) {
            Logger.getLogger(CompanyLoader.class.getName()).log(Level.SEVERE, "bad file", ex);
        } catch (IOException ex) {
            Logger.getLogger(CompanyLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public CrcData loadData(String datafile) throws NumberFormatException, IOException {

        CrcData out = new CrcData();
        StringTokenizer csvReader;
        int nextuid = 0;
       
        try ( //open file from jar
                BufferedReader in = new BufferedReader(new InputStreamReader(CompanyLoader.class.getClassLoader().getResourceAsStream(datafile)))) {
            while (in.ready()) {
                String line = in.readLine();
                
                if (line.startsWith("#")) {
                    // comment -> skip					
                } else if (line.startsWith("-")) {
                    csvReader = new StringTokenizer(line.substring(1), ",");
                    out.name = csvReader.nextToken();
                    out.regions = Arrays.asList(csvReader.nextToken().split(":"));
                } else {
                    csvReader = new StringTokenizer(line, ",");
                    //create new car type from first 5 fields
                    CarType type = new CarType(csvReader.nextToken(),
                            Integer.parseInt(csvReader.nextToken()),
                            Float.parseFloat(csvReader.nextToken()),
                            Double.parseDouble(csvReader.nextToken()),
                            Boolean.parseBoolean(csvReader.nextToken()));
                    //create N new cars with given type, where N is the 5th field
                    for (int i = Integer.parseInt(csvReader.nextToken()); i > 0; i--) {
                        out.cars.add(new Car(nextuid++, type));
                    }        
                }
            } 
        }

        return out;
    }
    
    public class CrcData {
        public List<Car> cars = new LinkedList<>();
        public String name;
        public List<String> regions =  new LinkedList<>();
    }
    
}
