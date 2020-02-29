package rental.agency;

import rental.agency.session.ManagerSession;
import rental.agency.session.ReservationSession;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CarRentalAgency extends Remote {

    ReservationSession createReservationSession(String clientName) throws RemoteException;
    ManagerSession createManagerSession(String managerName, String rentalName) throws RemoteException;
    void closeReservationSession(String clientName) throws RemoteException;
    void closeManagerSession(String managerName) throws RemoteException;

}
