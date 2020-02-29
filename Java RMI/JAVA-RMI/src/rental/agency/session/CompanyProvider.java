package rental.agency.session;

import rental.company.CarRentalCompany;

import java.util.Collection;

public interface CompanyProvider {

    void registerCompany(String companyName) throws Exception;
    void unregisterCompany(String companyName);

    Collection<CarRentalCompany> getAllCompanies();
    CarRentalCompany getCompany(String name);

}
