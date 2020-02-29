package client;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

import rental.RentalServer;
import rental.agency.CarRentalAgency;
import rental.agency.session.ManagerSession;
import rental.agency.session.ReservationSession;
import rental.exception.ReservationException;
import rental.serializable.*;

public class Client extends AbstractTestManagement<ReservationSession, ManagerSession> {

	private final static int LOCAL = 0;
	private final static int REMOTE = 1;

	public static void main(String[] args) {
		try {
			// The first argument passed to the `main` method (if present)
			//  indicates whether the application is run on the remote setup or not.
			int localOrRemote = (args.length == 1 && args[0].equals("REMOTE")) ? REMOTE : LOCAL;
			Client client = new Client("trips", localOrRemote);

			client.registerCompanies();
			client.run();
			client.unregisterCompanies();
		}
		catch (Exception e) {
			System.out.println("A client could not be launched.");
			e.printStackTrace();
		}
	}

	/* Constructor */

	public Client(String scriptFile, int localOrRemote) throws Exception {
		super(scriptFile);
		fetchRegistry(localOrRemote);
		agency = (CarRentalAgency)registry.lookup("agency");
	}

	private void registerCompanies() {
		try {
			for (String companyName : RentalServer.companyNames) {
				ManagerSession session = agency.createManagerSession("manager" + companyName, companyName);
				session.registerCompany(companyName);
				System.out.println("Registered company '" + companyName + "'.");
				agency.closeManagerSession("manager" + companyName);
			}
		}
		catch (Exception e) {
			System.out.println("Failed to register companies.");
		}
	}

	private void unregisterCompanies() {
		try {
			for (String companyName : RentalServer.companyNames) {
				ManagerSession session = agency.createManagerSession("manager" + companyName, companyName);
				session.unregisterCompany(companyName);
				System.out.println("Unregistered company '" + companyName + "'.");
				agency.closeManagerSession("manager" + companyName);
			}
		}
		catch (Exception e) {
			System.out.println("Failed to unregister companies.");
		}
	}

	private CarRentalAgency agency;

	/* Registry */

	private void fetchRegistry(int localOrRemote) throws RemoteException {
		if (localOrRemote == LOCAL)
			registry = LocateRegistry.getRegistry(10123);
		else
			registry = LocateRegistry.getRegistry("192.168.104.76", 10123);
	}

	private Registry registry;

	/* Booking */

	@Override
	protected ReservationSession getNewReservationSession(String clientName) throws RemoteException {
		return agency.createReservationSession(clientName);
	}

	@Override
	protected void addQuoteToSession(ReservationSession session, String name, Date start, Date end, String carType, String region)
			throws RemoteException, ReservationException {
		try {
			ReservationConstraints constraints = new ReservationConstraints(start, end, carType, region);
			session.createQuote(constraints, name);
			System.out.println("Quote created for client named '" + name + "'.");
		}
		catch (Exception e) {
			System.out.println("Failed to create quote!");
			throw e;
		}
	}

	@Override
	protected List<Reservation> confirmQuotes(ReservationSession session, String name)
			throws RemoteException, ReservationException {
		return session.confirmQuotes();
	}

	@Override
	protected void checkForAvailableCarTypes(ReservationSession session, Date start, Date end) throws RemoteException {
		System.out.println("Available car types :");
		session.getAvailableCarTypes(start, end).forEach(System.out::println);
	}

	@Override
	protected String getCheapestCarType(ReservationSession session, Date start, Date end, String region) throws Exception {
		return session.getCheapestCarType(start, end, region).getName();
	}

	/* Management */

	@Override
	protected ManagerSession getNewManagerSession(String managerName, String carRentalName)
			throws RemoteException {
		return agency.createManagerSession(managerName, carRentalName);
	}

	@Override
	protected int getNumberOfReservationsByRenter(ManagerSession session, String clientName) throws RemoteException {
		return session.getNumberOfReservationsByRenter(clientName);
	}

	@Override
	protected int getNumberOfReservationsForCarType(ManagerSession session, String carRentalName, String carType) throws RemoteException {
		return session.getNumberOfReservationsForCarType(carRentalName, carType);
	}

	@Override
	protected Set<String> getBestClients(ManagerSession session)
			throws Exception {
		return session.getBestClients();
	}

	@Override
	protected CarType getMostPopularCarTypeIn(ManagerSession session, String carRentalCompanyName, int year)
			throws Exception {
		return session.getMostPopularCarTypeIn(carRentalCompanyName, year);
	}

}