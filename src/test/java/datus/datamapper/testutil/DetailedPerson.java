package datus.datamapper.testutil;

public class DetailedPerson {
    private final String uniqueId;
    private final String salutation;
    private final String firstName;
    private final String lastName;
    private final String address;
    private final String addressExtra;
    private final String city;
    private final String occupation;

    public DetailedPerson(String uniqueId, String salutation, String firstName, String lastName, String address, String addressExtra, String city, String occupation) {
        this.uniqueId = uniqueId;
        this.salutation = salutation;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.addressExtra = addressExtra;
        this.city = city;
        this.occupation = occupation;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getSalutation() {
        return salutation;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddress() {
        return address;
    }

    public String getAddressExtra() {
        return addressExtra;
    }

    public String getCity() {
        return city;
    }

    public String getOccupation() {
        return occupation;
    }
}