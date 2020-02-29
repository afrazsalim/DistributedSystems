package rental;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Car implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne(cascade=CascadeType.PERSIST)
    private CarType type;
    
    @OneToMany(cascade=CascadeType.ALL)
    @JoinColumn(name = "carID")
    private Set<Reservation> reservations;

    public Car() {}

    public Car(int uid, CarType type) {
    	this.id = uid;
        this.type = type;
        this.reservations = new HashSet<>();
    }

    public int getId() {
    	return id;
    }
    
    public CarType getType() {
        return type;
    }
	
    public void setType(CarType type) {
        this.type = type;
    }
    
    // Reservations

    public boolean isAvailable(Date start, Date end) {
        if (!start.before(end))
            throw new IllegalArgumentException("Illegal input period!");
        for (Reservation reservation : reservations) {
            if (reservation.getEndDate().before(start) || reservation.getStartDate().after(end))
                continue;
            return false;
        }
        return true;
    }
    
    public void addReservation(Reservation res) {
        reservations.add(res);
    }
    
    public void removeReservation(Reservation reservation) {
        reservations.remove(reservation); // Requires equals()
    }

    public Set<Reservation> getReservations() {
        return reservations;
    }
    
}