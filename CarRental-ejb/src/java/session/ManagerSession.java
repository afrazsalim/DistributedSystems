package session;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import rental.Car;
import rental.CarRentalCompany;
import rental.CarType;

@Stateless
@RolesAllowed({"Manager"})
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ManagerSession implements ManagerSessionRemote {
    
    @PersistenceContext
    EntityManager entityManager;
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void registerCompany(String name, List<String> regions) {
        CarRentalCompany company = new CarRentalCompany(name, regions, new ArrayList<Car>());
        entityManager.persist(company);
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void registerCars(String name, List<Car> cars) {
        CarRentalCompany company = entityManager.find(CarRentalCompany.class, name);
        company.addCars(cars);
        entityManager.persist(company);
    }
    
    @Override
    public Set<CarType> getCarTypes(String company) {
        return new HashSet<>(entityManager.createNamedQuery("getCarTypes").
                setParameter("company", company).
                getResultList());
    }

    @Override
    public Set<Integer> getCarIds(String company, String type) {
        return new HashSet<>(entityManager.createNamedQuery("getCarIds").
                setParameter("company", company).
                setParameter("type", type).
                getResultList());
    }

    @Override
    public int getNumberOfReservations(String company, String type, int id) {
        return ((Number)entityManager.createNamedQuery("getNumberOfReservationsForId").
                setParameter("company", company).
                setParameter("type", type).
                setParameter("id", id).
                getSingleResult()).intValue();
    }

    @Override
    public int getNumberOfReservations(String company, String type) {
        return ((Number)entityManager.createNamedQuery("getNumberOfReservations").
                setParameter("company", company).
                setParameter("type", type).
                getSingleResult()).intValue();
    }

    @Override
    public int getNumberOfReservations(String clientName) {
        return ((Number)entityManager.createNamedQuery("getNumberOfReservationsForRenter").
                setParameter("renter", clientName).
                getSingleResult()).intValue();
    }

    @Override
    public Set<String> getBestClients() {
        return new HashSet<>(entityManager.createNamedQuery("getBestClients").getResultList());
    }

    
    @Override
    public CarType getMostPopularCarTypeIn(String carRentalCompanyName, int year) {
        try {
            DateFormat format = new SimpleDateFormat("d/M/y");
            Date start = format.parse("01/01/" + year);
            Date end = format.parse("31/12/" + year);
            return (CarType)entityManager.createNamedQuery("getMostPopularCarTypeIn").
                setParameter("company", carRentalCompanyName).
                setParameter("start", start).
                setParameter("end", end).
                setMaxResults(1).
                getSingleResult();
        }
        catch (ParseException ignored) {
            return null;
        }
    }

}