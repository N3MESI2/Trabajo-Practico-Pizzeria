public class Customer {
    private String name;
    private String address;

    public Customer(String name, String address) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Nombre inválido");
        if (address == null || address.isBlank()) throw new IllegalArgumentException("Dirección inválida");
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "Customer{name='" + name + "', address='" + address + "'}";
    }
}
