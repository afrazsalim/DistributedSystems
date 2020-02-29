package rental.company;

import rental.serializable.ReservationConstraints;
import rental.exception.ReservationException;
import rental.serializable.CarType;
import rental.serializable.Quote;
import rental.serializable.Reservation;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CarRentalCompanyImp implements CarRentalCompany {

	private static Logger logger = Logger.getLogger(CarRentalCompanyImp.class.getName());
	
	private List<String> regions;
	private String name;
	private List<Car> cars;
	private Map<String, CarType> carTypes = new HashMap<>();

	public CarRentalCompanyImp(String name, List<String> regions, List<Car> cars) {
		logger.log(Level.INFO, "<{0}> Car Rental Company {0} starting up...", name);
		setName(name);
		this.cars = cars;
		setRegions(regions);
		for (Car car:cars)
			carTypes.put(car.getType().getName(), car.getType());
		logger.log(Level.INFO, this.toString());
	}

	/* Name */

	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

	/* Regions */

    private void setRegions(List<String> regions) {
        this.regions = regions;
    }
    
    private List<String> getRegions() {
        return this.regions;
    }
    
    public boolean operatesInRegion(String region) {
        return getRegions().contains(region);
    }

	/* Car Types */

	private CarType getCarType(String carTypeName) {
		if (carTypes.containsKey(carTypeName))
			return carTypes.get(carTypeName);
		throw new IllegalArgumentException("<" + carTypeName + "> No car type of name " + carTypeName);
	}
	
	private boolean isAvailable(String carTypeName, Date start, Date end) {
		logger.log(Level.INFO, "<{0}> Checking availability for car type {1}", new Object[]{name, carTypeName});
		if (carTypes.containsKey(carTypeName))
			return getAvailableCarTypes(start, end).contains(carTypes.get(carTypeName));
		else
			throw new IllegalArgumentException("<" + carTypeName + "> No car type of name " + carTypeName);
	}
	
	public Set<CarType> getAvailableCarTypes(Date start, Date end) {
		Set<CarType> availableCarTypes = new HashSet<>();
		for (Car car : cars)
			if (car.isAvailable(start, end))
				availableCarTypes.add(car.getType());
		return availableCarTypes;
	}

	/* Cars */
	
	private Car getCar(int uid) {
		for (Car car : cars) {
			if (car.getId() == uid)
				return car;
		}
		throw new IllegalArgumentException("<" + name + "> No car with uid " + uid);
	}
	
	private List<Car> getAvailableCars(String carType, Date start, Date end) {
		List<Car> availableCars = new LinkedList<>();
		for (Car car : cars)
			if (car.getType().getName().equals(carType) && car.isAvailable(start, end))
				availableCars.add(car);
		return availableCars;
	}

	/* Reservations */

	public Quote createQuote(ReservationConstraints constraints, String client)
			throws ReservationException {
		logger.log( Level.INFO, "<{0}> Creating tentative reservation for {1} with constraints {2}",
                    new Object[]{name, client, constraints.toString()});
		if (!operatesInRegion(constraints.getRegion())
            || !isAvailable(constraints.getCarType(),
            constraints.getStartDate(),
            constraints.getEndDate()))
			throw new ReservationException("<" + name + "> No cars available to satisfy the given constraints.");
		CarType type = getCarType(constraints.getCarType());
		double price = calculateRentalPrice(type.getRentalPricePerDay(),constraints.getStartDate(), constraints.getEndDate());
		return new Quote(client, constraints.getStartDate(), constraints.getEndDate(), getName(), constraints.getCarType(), price);
	}

	private double calculateRentalPrice(double rentalPricePerDay, Date start, Date end) {
		// Implementation can be subject to different pricing strategies
		return rentalPricePerDay * Math.ceil((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24D));
	}

	public synchronized Reservation confirmQuote(Quote quote) throws ReservationException {
		logger.log(Level.INFO, "<{0}> Reservation of {1}", new Object[]{name, quote.toString()});
		List<Car> availableCars = getAvailableCars(quote.getCarType(), quote.getStartDate(), quote.getEndDate());
		if (availableCars.isEmpty())
			throw new ReservationException("Reservation failed, all cars of type "
                    + quote.getCarType() + " are unavailable from "
                    + quote.getStartDate() + " to " + quote.getEndDate());
		Car car = availableCars.get((int)(Math.random() * availableCars.size()));
		Reservation res = new Reservation(quote, car.getId());
		car.addReservation(res);
		return res;
	}

	public void cancelReservation(Reservation res) {
		logger.log(Level.INFO, "<{0}> Cancelling reservation {1}", new Object[]{name, res.toString()});
		getCar(res.getCarId()).removeReservation(res);
	}

	public int getNbReservationsForRenter(String carRenter) {
		int total = 0;
		for (Car car : cars)
			total += car.getReservationsForRenter(carRenter).size();
		return total;
	}

	public int getReservationsForCarType(String carType) {
		int nbReservations = 0;
		for (Car car : cars)
			if (car.getType().getName().equals(carType))
				nbReservations += car.getNbReservations();
		return nbReservations;
	}

	public CarType getPopularCarType(int year) {
		int reservations = 0;
		CarType popularType = null;
		for (CarType type : carTypes.values()) {
			int total = 0;
			for (Car car : cars)
				if (car.getType().equals(type))
					total += car.getNbReservations(year);
			if (total > reservations) {
				reservations = total;
				popularType = type;
			}
		}
		return popularType;
	}

	public Set<String> getAllClients() {
		Set<String> clients = new HashSet<>();
		for (Car car : cars)
			clients.addAll(car.getRenters());
		return clients;
	}

	/* Description */

	@Override
	public String toString() {
		return String.format("<%s> CRC is active in regions %s and serving with %d car types",
				name,
				listToString(regions),
				carTypes.size());
	}
	
	private static String listToString(List<?> input) {
		StringBuilder out = new StringBuilder();
		for (int i=0 ; i < input.size() ; i++) {
			if (i == input.size()-1) {
				out.append(input.get(i).toString());
			} else {
				out.append(input.get(i).toString()).append(", ");
			}
		}
		return out.toString();
	}
	
}