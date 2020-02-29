package rental.company;

import rental.serializable.CarType;
import rental.serializable.Reservation;

import java.util.*;

public class Car {

    private int id;
    private CarType type;
    private List<Reservation> reservations;

    /* Constructor */
    
    public Car(int uid, CarType type) {
    	this.id = uid;
        this.type = type;
        this.reservations = new ArrayList<>();
    }

    /* ID */
    
    int getId() {
    	return id;
    }
    
    /* Car Type */
    
    public CarType getType() {
        return type;
    }

    /* Reservations */

    boolean isAvailable(Date start, Date end) {
        if (!start.before(end))
            throw new IllegalArgumentException("Illegal given period");
        for (Reservation reservation : reservations) {
            if (reservation.getEndDate().before(start) || reservation.getStartDate().after(end))
                continue;
            return false;
        }
        return true;
    }

    List<Reservation> getReservationsForRenter(String carRenter) {
        List<Reservation> result = new ArrayList<>();
        for (Reservation reservation : reservations)
            if (reservation.getCarRenter().equals(carRenter))
                result.add(reservation);
        return result;
    }

    Set<String> getRenters() {
        Set<String> renters = new HashSet<>();
        for (Reservation reservation : reservations)
            renters.add(reservation.getCarRenter());
        return renters;
    }

    int getNbReservations() {
        return reservations.size();
    }

    int getNbReservations(int year) {
        int total = 0;
        for (Reservation reservation : reservations) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(reservation.getStartDate());
            if (calendar.get(Calendar.YEAR) == year)
                total++;
        }
        return total;
    }

    void addReservation(Reservation res) {
        reservations.add(res);
    }
    
    void removeReservation(Reservation reservation) {
        reservations.remove(reservation); // equals method for Reservation is required
    }

    /* Description */

    @Override
    public String toString() {
        return String.format("Car (%s) of type %s (%d reservations)", getId(), getType(), reservations.size());
    }

}