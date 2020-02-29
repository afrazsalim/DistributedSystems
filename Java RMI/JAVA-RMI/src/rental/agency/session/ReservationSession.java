package rental.agency.session;

import rental.exception.ReservationException;
import rental.serializable.CarType;
import rental.serializable.Quote;
import rental.serializable.Reservation;
import rental.serializable.ReservationConstraints;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface ReservationSession extends Remote {

    void createQuote(ReservationConstraints constraints, String client) throws RemoteException, ReservationException;
    List<Quote> getCurrentQuotes() throws RemoteException;
    List<Reservation> confirmQuotes() throws RemoteException, ReservationException;

    Set<CarType> getAvailableCarTypes(Date start, Date end) throws RemoteException;
    CarType getCheapestCarType(Date start, Date end, String region) throws RemoteException;

}
