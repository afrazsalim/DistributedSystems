package session;

import java.util.List;
import java.util.Set;
import javax.ejb.Remote;
import rental.Car;
import rental.CarType;

@Remote
public interface ManagerSessionRemote {
        
    public void registerCompany(String name, List<String> regions);
    public void registerCars(String name, List<Car> cars);
    
    public Set<CarType> getCarTypes(String company);
    public Set<Integer> getCarIds(String company,String type);
    
    public int getNumberOfReservations(String clientName);
    public int getNumberOfReservations(String company, String type, int carId);
    public int getNumberOfReservations(String company, String type);

    public Set<String> getBestClients();
    public CarType getMostPopularCarTypeIn(String carRentalCompanyName, int year);
      
}