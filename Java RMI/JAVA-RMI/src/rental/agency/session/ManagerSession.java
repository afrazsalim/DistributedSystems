package rental.agency.session;

import rental.serializable.CarType;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

public interface ManagerSession extends Remote {

    void registerCompany(String companyName) throws RemoteException;
    void unregisterCompany(String companyName) throws RemoteException;

    int getNumberOfReservationsByRenter(String clientName) throws RemoteException;
    int getNumberOfReservationsForCarType(String carRentalName, String carType) throws RemoteException;

    Set<String> getBestClients() throws RemoteException;
    CarType getMostPopularCarTypeIn(String carRentalCompanyName, int year) throws RemoteException;

}
