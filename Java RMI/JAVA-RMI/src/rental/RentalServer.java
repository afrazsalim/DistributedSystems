package rental;

import rental.agency.CarRentalAgency;
import rental.agency.CarRentalAgencyImp;
import rental.company.CarRentalCompany;
import rental.company.CarRentalCompanyImp;
import rental.company.Car;
import rental.serializable.CarType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.util.*;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RentalServer {

	private final static int LOCAL = 0, REMOTE = 1;
	public static List<String> companyNames = Arrays.asList("Hertz", "Dockx");

	/* Setup */

	public static void main(String[] args) throws NumberFormatException {
		int localOrRemote = (args.length == 1 && args[0].equals("REMOTE")) ? REMOTE : LOCAL;
		if (localOrRemote == LOCAL)
			setupLocal(companyNames);
		else
			setupRemote(companyNames);
	}


	private static void setupLocal(List<String> companyNames) {
		System.setSecurityManager(null);
		try {
			Registry registry = LocateRegistry.createRegistry(10123);
			bindCompanies(registry, companyNames);
			bindAgency(registry);
			System.out.println("Server activated.");
		} catch (Exception e) {
			System.out.println("Failed to activate server.");
			e.printStackTrace();
		}
	}

	private static void setupRemote(List<String> companyNames) {
		try {
			// Naming.bind("//192.168.104.76:8080/company", stub);
			Registry registry = LocateRegistry.createRegistry(10123);
			bindCompanies(registry, companyNames);
			bindAgency(registry);
			System.out.println("Server activated.");
		} catch (Exception e) {
			System.out.println("Failed to activate server.");
			e.printStackTrace();
		}
	}

	private static void bindCompanies(Registry registry, List<String> companyNames)
			throws IOException, AlreadyBoundException {
		Map<String,CarRentalCompany> stubs = getCompanyStubs(companyNames);
		for (String companyName : companyNames)
			registry.bind(companyName, stubs.get(companyName));
	}

	private static Map<String,CarRentalCompany> getCompanyStubs(List<String> companyNames) throws IOException {
		Map<String,CarRentalCompany> stubs = new HashMap<>();
		for (String companyName : companyNames) {
			CrcData data = loadData(companyName + ".csv");
			CarRentalCompany company = new CarRentalCompanyImp(data.name, data.regions, data.cars);
			stubs.put(companyName, (CarRentalCompany)UnicastRemoteObject.exportObject(company, 10124));
		}
		return stubs;
	}

	private static void bindAgency(Registry registry)
			throws RemoteException, AlreadyBoundException {
		CarRentalAgency agency = new CarRentalAgencyImp(registry);
		CarRentalAgency stub = (CarRentalAgency)UnicastRemoteObject.exportObject(agency, 10124);
		registry.bind("agency", stub);
	}

	/* CSV Car Company Data Parsing */

	private static CrcData loadData(String datafile) throws NumberFormatException, IOException {

		CrcData out = new CrcData();
		int nextuid = 0;

		// Open file
		InputStream stream = MethodHandles.lookup().lookupClass().getClassLoader().getResourceAsStream(datafile);
		if (stream == null) {
			System.err.println("Could not find data file " + datafile);
		}

		assert stream != null;
		try (BufferedReader in = new BufferedReader(new InputStreamReader(stream))) {
			StringTokenizer csvReader;
			// While next line exists
			while (in.ready()) {
				String line = in.readLine();
				if (!line.startsWith("#")) { // Ignore comments
					if (line.startsWith("-")) {
						csvReader = new StringTokenizer(line.substring(1), ",");
						out.name = csvReader.nextToken();
						out.regions = Arrays.asList(csvReader.nextToken().split(":"));
					} else {
						// tokenize on ,
						csvReader = new StringTokenizer(line, ",");
						// create new car type from first 5 fields
						CarType type = new CarType(csvReader.nextToken(),
								Integer.parseInt(csvReader.nextToken()),
								Float.parseFloat(csvReader.nextToken()),
								Double.parseDouble(csvReader.nextToken()),
								Boolean.parseBoolean(csvReader.nextToken()));
						System.out.println(type);
						// create N new cars with given type, where N is the 5th field
						for (int i = Integer.parseInt(csvReader.nextToken()); i > 0; i--) {
							out.cars.add(new Car(nextuid++, type));
						}
					}
				}
			}
		}

		return out;

	}
	
	static class CrcData {
		List<Car> cars = new LinkedList<>();
		public String name;
		List<String> regions = new LinkedList<>();
	}

}
