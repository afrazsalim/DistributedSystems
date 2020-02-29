package rental.company;

import rental.serializable.ReservationConstraints;
import rental.serializable.CarType;
import rental.serializable.Quote;
import rental.serializable.Reservation;
import rental.exception.ReservationException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Set;

public interface CarRentalCompany extends Remote {

    boolean operatesInRegion(String region) throws RemoteException;

    Quote createQuote(ReservationConstraints constraints, String client) throws ReservationException, RemoteException;
    Reservation confirmQuote(Quote quote) throws ReservationException, RemoteException;
    void cancelReservation(Reservation res) throws RemoteException;

    Set<String> getAllClients() throws RemoteException;
    Set<CarType> getAvailableCarTypes(Date start, Date end) throws RemoteException;
    int getNbReservationsForRenter(String carRenter) throws RemoteException;
    int getReservationsForCarType(String carType) throws RemoteException;
    CarType getPopularCarType(int year) throws RemoteException;

}
