package rental.agency.session;

import java.util.Date;

public class Session {

    Session(CompanyProvider provider, String name) {
        this.provider = provider;
        this.name = name;
        activationDate = new Date();
    }

    /* Provider */

    CompanyProvider getProvider() {
        return provider;
    }

    private CompanyProvider provider;

    /* Date */

    void activate() {
        // System.out.println("activation"); // For testing
        activationDate = new Date();
    }

    Date getActivationDate() {
        return activationDate;
    }

    private Date activationDate;

    /* Name */

    public String getName() {
        return this.name;
    }

    private String name;

}
