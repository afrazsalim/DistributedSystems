package rental.agency;

import rental.agency.session.*;
import rental.company.CarRentalCompany;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class CarRentalAgencyImp implements CarRentalAgency, CompanyProvider {

    public CarRentalAgencyImp(Registry registry) {
        this.registry = registry;
    }

    private Registry registry;

    @Override
    public void registerCompany(String companyName) throws Exception {
        rentalCompanies.put(companyName, (CarRentalCompany)registry.lookup(companyName));
    }

    @Override
    public void unregisterCompany(String companyName) {
        rentalCompanies.remove(companyName);
    }

    @Override
    public Collection<CarRentalCompany> getAllCompanies() {
        return rentalCompanies.values();
    }

    @Override
    public CarRentalCompany getCompany(String name) {
        return rentalCompanies.get(name);
    }

    private Map<String, CarRentalCompany> rentalCompanies = new HashMap<>();

    /* Sessions */

    @Override
    public ReservationSession createReservationSession(String clientName) throws RemoteException {
        if  (!renterSessions.containsKey(clientName)) {
            ReservationSessionImp session = new ReservationSessionImp(this, clientName);
            ReservationSession stub = (ReservationSession) UnicastRemoteObject.exportObject(session, 10124);
            renterSessions.put(clientName, stub);
            return stub;
        }
        return renterSessions.get(clientName);
    }

    @Override
    public ManagerSession createManagerSession(String managerName, String rentalName) throws RemoteException {
        if  (!managerSessions.containsKey(managerName)) {
            ManagerSessionImp session = new ManagerSessionImp(this, managerName, rentalName);
            ManagerSession stub = (ManagerSession)UnicastRemoteObject.exportObject(session, 10124);
            managerSessions.put(managerName, stub);
            return stub;
        }
        return managerSessions.get(managerName);
    }

    public void closeReservationSession(String name) {
        renterSessions.remove(name);
    }

    public void closeManagerSession(String name) {
        managerSessions.remove(name);
    }

    private Map<String, ReservationSession> renterSessions = new HashMap<>();
    private Map<String, ManagerSession> managerSessions = new HashMap<>();

}
