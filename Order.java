import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class Order {
    // Estados en español (impresos en español)
    public enum Status {
        PENDIENTE("PENDIENTE"),
        ASIGNADO("ASIGNADO"),
        EN_CAMINO("EN CAMINO"),
        ENTREGADO("ENTREGADO"),
        CANCELADO("CANCELADO");

        private final String etiqueta;
        Status(String etiqueta) { this.etiqueta = etiqueta; }
        @Override public String toString() { return etiqueta; }
    }

    private static final Locale LOCALE_AR = Locale.forLanguageTag("es-AR");
    private static final NumberFormat ARS = NumberFormat.getCurrencyInstance(LOCALE_AR);

    private final Restaurant restaurant;
    private final Customer customer;
    private final Map<String, Integer> items = new LinkedHashMap<>();
    private Driver driver;                  // null hasta asignar
    private Status status = Status.PENDIENTE;

    public Order(Restaurant restaurant, Customer customer) {
        if (restaurant == null || customer == null) throw new IllegalArgumentException("Datos inválidos");
        this.restaurant = restaurant;
        this.customer = customer;
    }

    public Status getStatus() { return status; }
    public Driver getDriver() { return driver; }

    public void addItem(String item, int qty) {
        if (item == null || item.isBlank()) throw new IllegalArgumentException("Ítem vacío");
        if (qty <= 0) throw new IllegalArgumentException("Cantidad inválida");
        Double price = restaurant.getPrice(item);
        if (price == null) throw new IllegalArgumentException("Ítem no existe en el menú");
        items.merge(item, qty, Integer::sum);
    }

    public double getTotal() {
        double total = 0.0;
        for (Map.Entry<String, Integer> e : items.entrySet()) {
            Double p = restaurant.getPrice(e.getKey());
            if (p != null) total += p * e.getValue();
        }
        return total;
    }

    public void assignDriver(Driver d) {
        if (d == null) throw new IllegalArgumentException("Driver inválido");
        if (!d.isAvailable()) throw new IllegalStateException("Driver no disponible");
        if (status == Status.CANCELADO || status == Status.ENTREGADO)
            throw new IllegalStateException("Pedido finalizado; no se puede asignar driver");
        this.driver = d;
        d.setAvailable(false);
        this.status = Status.ASIGNADO;
    }

    /** Pone el pedido EN CAMINO. Requiere driver asignado. */
    public void startDelivery() {
        if (status == Status.CANCELADO || status == Status.ENTREGADO)
            throw new IllegalStateException("Pedido finalizado; no se puede enviar");
        if (driver == null)
            throw new IllegalStateException("Primero asigne un repartidor.");
        this.status = Status.EN_CAMINO;
    }

    public void markDelivered() {
        if (status == Status.CANCELADO) throw new IllegalStateException("Pedido cancelado");
        this.status = Status.ENTREGADO;
        if (driver != null) driver.setAvailable(true);
    }

    public void cancel() {
        if (status == Status.ENTREGADO) throw new IllegalStateException("Pedido ya entregado");
        this.status = Status.CANCELADO;
        if (driver != null) driver.setAvailable(true);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n================= PEDIDO =================\n");
        sb.append("Cliente     : ").append(customer.getName())
          .append(" (").append(customer.getAddress()).append(")\n");
        sb.append("Restaurante : ").append(restaurant.getName()).append("\n");
        sb.append("Estado      : ").append(status).append("\n");
        sb.append("------------------------------------------\n");
        if (items.isEmpty()) {
            sb.append("(Sin ítems)\n");
        } else {
            for (Map.Entry<String,Integer> e : items.entrySet()) {
                String item = e.getKey();
                int cant = e.getValue();
                double precio = restaurant.getPrice(item);
                sb.append(String.format("• %-15s x%-3d  = %s%n",
                        item, cant, ARS.format(precio * cant)));
            }
        }
        sb.append("------------------------------------------\n");
        sb.append("Total       : ").append(ARS.format(getTotal())).append("\n");
        sb.append("Repartidor  : ").append(driver == null ? "(no asignado)" : driver.getName()).append("\n");
        sb.append("==========================================\n");
        return sb.toString();
    }
}
