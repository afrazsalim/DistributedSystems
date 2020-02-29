package session;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

@Stateful
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class CarRentalSession implements CarRentalSessionRemote {

    @PersistenceContext
    EntityManager entityManager;
    
    @Resource
    SessionContext ctx;
    
    private String renter;
    private List<Quote> quotes = new LinkedList<>();
    
    @Override
    public Set<String> getAllRentalCompanies() {
        return new HashSet<>(entityManager.createNamedQuery("getAllRentalCompanies").getResultList());
    }
    
    @Override
    public List<CarType> getAvailableCarTypes(Date start, Date end) {
        return entityManager.createNamedQuery("getAvailableCarTypes").
                setParameter("start", start).
                setParameter("end", end).
                getResultList();
    }

    @Override
    public Quote createQuote(String name, ReservationConstraints constraints) throws ReservationException {
        for (String company : getAllRentalCompanies()) {
            try {
                CarRentalCompany rental = entityManager.find(CarRentalCompany.class, company);
                if (rental != null) {
                    Quote quote = rental.createQuote(constraints, renter);
                    quotes.add(quote);
                    return quote;
                }
            }
            catch (IllegalArgumentException | ReservationException ignored) {}
        }
        throw new ReservationException("Failed to create quote for renter named '" + renter + "'!");
    }

    @Override
    public List<Quote> getCurrentQuotes() {
        return quotes;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Reservation> confirmQuotes() throws ReservationException {
        List<Reservation> confirmedQuotes = new LinkedList<>();
        try {
            for (Quote quote : quotes) {
                CarRentalCompany rental = entityManager.find(CarRentalCompany.class, quote.getRentalCompany());
                Reservation reservation = rental.confirmQuote(quote);
                confirmedQuotes.add(reservation);
                entityManager.persist(reservation);
            }
        } catch (ReservationException e) {
            ctx.setRollbackOnly();
            throw new ReservationException("failed to confirm quotes for renter named '" + renter + "'!");
        }
        return confirmedQuotes;
    }

    @Override
    public void setRenterName(String name) {
        if (renter != null)
            throw new IllegalStateException("The renter's name is already set!");
        renter = name;
    }

    @Override
    public CarType getCheapestCarType(Date start, Date end, String region) {
        return (CarType)entityManager.createNamedQuery("getCheapestCarType").
                setParameter("start", start).
                setParameter("end", end).
                setParameter("region", region).
                setMaxResults(1).
                getSingleResult();
    }
    
}