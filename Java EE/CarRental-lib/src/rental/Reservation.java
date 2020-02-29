package rental;

import javax.persistence.Entity;

@Entity
public class Reservation extends Quote {

    private int carID;

    public Reservation() {}
    
    public Reservation(Quote quote, int carID) {
    	super(  quote.getCarRenter(), 
                quote.getStartDate(), 
                quote.getEndDate(), 
                quote.getRentalCompany(), 
                quote.getCarType(), 
                quote.getRentalPrice());
        this.carID = carID;
    }
    
    // Car ID
    
    public int getCarId() {
    	return carID;
    }
    
    @Override
    public String toString() {
        return String.format("Reservation for %s from %s to %s at %s\n"
                + "Car type: %s\tCar: %s\n"
                + "Total price: %.2f", 
                getCarRenter(), getStartDate(), getEndDate(), getRentalCompany(), 
                getCarType(), getCarId(), 
                getRentalPrice());
    }
    
}