public class Driver {
    private final String name;
    private boolean available;

    public Driver(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name vacío");
        this.name = name;
        this.available = true;
    }

    /** Cambia disponibilidad */
    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isAvailable() { return available; }

    public String getName() { return name; }
}

