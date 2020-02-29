package rental.serializable;

import java.io.Serializable;

public class CarType implements Serializable {
    
    private String name;
    private int nbOfSeats;
    private boolean smokingAllowed;
    private double rentalPricePerDay;
    private float trunkSpace; // trunk space in liters
    
    /* Constructor */
    
    public CarType(String name, int nbOfSeats, float trunkSpace, double rentalPricePerDay, boolean smokingAllowed) {
        this.name = name;
        this.nbOfSeats = nbOfSeats;
        this.trunkSpace = trunkSpace;
        this.rentalPricePerDay = rentalPricePerDay;
        this.smokingAllowed = smokingAllowed;
    }

    public String getName() {
    	return name;
    }
    
    private int getNbOfSeats() {
        return nbOfSeats;
    }

    private boolean isSmokingAllowed() {
        return smokingAllowed;
    }

    public double getRentalPricePerDay() {
        return rentalPricePerDay;
    }

    private float getTrunkSpace() {
    	return trunkSpace;
    }
    
    /* Description */
    
    @Override
    public String toString() {
    	return String.format("Car type: %s \t[seats: %d, price: %.2f, smoking: %b, trunk: %.0fl]" , 
                getName(), getNbOfSeats(), getRentalPricePerDay(), isSmokingAllowed(), getTrunkSpace());
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CarType other = (CarType) obj;
		if (name == null) {
            return other.name == null;
		} else return name.equals(other.name);
    }

}