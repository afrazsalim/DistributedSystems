package rental.agency.session;

import rental.company.CarRentalCompany;
import rental.exception.ReservationException;
import rental.serializable.CarType;
import rental.serializable.Quote;
import rental.serializable.Reservation;
import rental.serializable.ReservationConstraints;

import java.rmi.RemoteException;
import java.util.*;

public class ReservationSessionImp extends Session implements ReservationSession {

    public ReservationSessionImp(CompanyProvider provider, String name) {
        super(provider, name);
    }

    @Override
    public void createQuote(ReservationConstraints constraints, String client)
            throws ReservationException {
        for (CarRentalCompany company : getProvider().getAllCompanies()) {
            try {
                quotes.add(company.createQuote(constraints, client));
                return;
            } catch (Exception ignored) {} // A particular company wasn't able to create a quote
        }
        throw new ReservationException("Couldn't create a quote for client named " + client + ".");
    }

    @Override
    public List<Quote> getCurrentQuotes() {
        return quotes; // Won't be accessible remotely, serialized copy
    }

    @Override
    public List<Reservation> confirmQuotes() throws RemoteException, ReservationException {
        List<Reservation> reservations = new ArrayList<>();
        try {
            for (Quote quote : quotes)
                reservations.add(getProvider().getCompany(quote.getRentalCompany()).confirmQuote(quote));
        }
        catch (Exception e) {
            for (Reservation reservation : reservations)
                getProvider().getCompany(reservation.getRentalCompany()).cancelReservation(reservation);
            throw new ReservationException("Couldn't confirm quotes.");
        }
        quotes.clear();
        return reservations;
    }

    private List<Quote> quotes = new ArrayList<>();

    @Override
    public Set<CarType> getAvailableCarTypes(Date start, Date end) throws RemoteException {
        Set<CarType> types = new HashSet<>();
        for (CarRentalCompany company : getProvider().getAllCompanies())
            types.addAll(company.getAvailableCarTypes(start, end));
        return types;
    }

    @Override
    public CarType getCheapestCarType(Date start, Date end, String region) throws RemoteException {
        CarType cheapestType = null;
        for (CarRentalCompany company : getProvider().getAllCompanies())
            if (company.operatesInRegion(region))
                for (CarType type : company.getAvailableCarTypes(start, end))
                    if (cheapestType == null)
                        cheapestType = type;
                    else if (type.getRentalPricePerDay() < cheapestType.getRentalPricePerDay())
                        cheapestType = type;
        return cheapestType;
    }

}
