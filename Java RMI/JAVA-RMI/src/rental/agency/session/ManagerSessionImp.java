package rental.agency.session;

import rental.company.CarRentalCompany;
import rental.serializable.CarType;

import java.rmi.RemoteException;
import java.util.*;

public class ManagerSessionImp extends Session implements ManagerSession {

    public ManagerSessionImp(CompanyProvider provider, String name, String rentalName) {
        super(provider, name);
        this.rentalName = rentalName;
    }

    private String rentalName;

    /* Company (Un)Registration */

    public void registerCompany(String companyName) throws RemoteException {
        try { getProvider().registerCompany(companyName); }
        catch (Exception e) { throw new RemoteException(); }
    }

    public void unregisterCompany(String companyName) {
        getProvider().unregisterCompany(companyName);
    }

    /* Lookup Methods */

    @Override
    public int getNumberOfReservationsByRenter(String clientName) throws RemoteException {
        return getProvider().getCompany(rentalName).getNbReservationsForRenter(clientName);
    }

    private int getAllNumberOfReservationsByRenter(String clientName) throws RemoteException {
        int total = 0;
        for (CarRentalCompany company : getProvider().getAllCompanies())
            total += company.getNbReservationsForRenter(clientName);
        return total;
    }

    @Override
    public int getNumberOfReservationsForCarType(String carRentalName, String carType) throws RemoteException {
        return getProvider().getCompany(carRentalName).getReservationsForCarType(carType);
    }

    @Override
    public Set<String> getBestClients() throws RemoteException {
        Set<String> clients = new HashSet<>(), bestClients = new HashSet<>();
        Collection<CarRentalCompany> companies = getProvider().getAllCompanies();
        for (CarRentalCompany company : companies) // Get all clients across companies
            clients.addAll(company.getAllClients());
        int maximum = 0;
        for (String client : clients) { // Get the clients that have the largest number of reservations
            int nbReservations = getAllNumberOfReservationsByRenter(client);
            if (nbReservations > maximum) {
                bestClients.clear();
                bestClients.add(client);
                maximum = nbReservations;
            }
            else if (nbReservations == maximum)
                bestClients.add(client);
        }
        return bestClients;
    }

    @Override
    public CarType getMostPopularCarTypeIn(String carRentalCompanyName, int year) throws RemoteException {
        return getProvider().getCompany(carRentalCompanyName).getPopularCarType(year);
    }

}
