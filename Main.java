import java.text.NumberFormat;
import java.util.*;

public class Main {
    private static final Scanner in = new Scanner(System.in);
    private static final Locale LOCALE_AR = Locale.forLanguageTag("es-AR");
    private static final NumberFormat ARS = NumberFormat.getCurrencyInstance(LOCALE_AR);

    public static void main(String[] args) {
        // Restaurante y menú de pizzas
        Restaurant restaurant = new Restaurant("Pizzeria");
        restaurant.addMenuItem("Muzzarella", 5200.0);
        restaurant.addMenuItem("Fugazzeta", 5400.0);
        restaurant.addMenuItem("Napolitana", 5900.0);
        restaurant.addMenuItem("Calabresa", 6100.0);

        // Repartidores
        List<Driver> drivers = new ArrayList<>();
        drivers.add(new Driver("Carla"));
        drivers.add(new Driver("Luis"));
        drivers.add(new Driver("Sofía"));

        // Cliente y pedido
        Customer customer = pedirCliente();
        Order order = new Order(restaurant, customer);

        System.out.println(">>> MENU CON ENVIAR v1 <<<");

        while (true) {
            System.out.println("\n=========== " + restaurant.getName() + " ===========");
            System.out.println("Cliente: " + customer.getName() + " (" + customer.getAddress() + ")");
            System.out.println("Estado del pedido: " + order.getStatus() + " | Total: " + ARS.format(order.getTotal()));
            System.out.println("------------------------------------------");
            System.out.println("1) Ver pizzas y agregar al pedido");
            System.out.println("2) Ver pedido actual");
            System.out.println("3) Programar envío / Enviar orden");   // <-- NUEVA OPCIÓN
            System.out.println("4) Asignar repartidor");
            System.out.println("5) Marcar pedido como ENTREGADO");
            System.out.println("6) Cancelar pedido");
            System.out.println("7) Cambiar cliente (nuevo pedido)");
            System.out.println("0) Salir");
            System.out.print("Opción: ");

            String op = in.nextLine().trim();
            try {
                switch (op) {
                    case "1" -> seleccionarPizzas(restaurant, order);
                    case "2" -> System.out.println(order);
                    case "3" -> enviarOrden(order);             // <-- HANDLER NUEVO
                    case "4" -> asignarDriver(order, drivers);
                    case "5" -> marcarEntregado(order);
                    case "6" -> cancelarPedido(order);
                    case "7" -> {
                        customer = pedirCliente();
                        order = new Order(restaurant, customer);
                        System.out.println("Nuevo pedido creado para " + customer.getName() + ".");
                    }
                    case "0" -> {
                        System.out.println("¡Gracias! Hasta luego.");
                        return;
                    }
                    default -> System.out.println("Opción inválida.");
                }
            } catch (Exception e) {
                System.out.println("⚠️ " + e.getMessage());
            }
        }
    }

    // ===== Utilidades de interacción =====

    private static Customer pedirCliente() {
        System.out.println("\n=== Datos del cliente ===");
        System.out.print("Nombre: ");
        String nombre = in.nextLine().trim();
        while (nombre.isBlank()) {
            System.out.print("Nombre no puede estar vacío. Ingrese nuevamente: ");
            nombre = in.nextLine().trim();
        }
        System.out.print("Dirección: ");
        String dir = in.nextLine().trim();
        while (dir.isBlank()) {
            System.out.print("Dirección no puede estar vacía. Ingrese nuevamente: ");
            dir = in.nextLine().trim();
        }
        return new Customer(nombre, dir);
    }

    private static void seleccionarPizzas(Restaurant r, Order o) {
        while (true) {
            System.out.println("\n--- Menú de " + r.getName() + " ---");
            List<String> items = new ArrayList<>(r.getMenu().keySet());
            for (int i = 0; i < items.size(); i++) {
                String item = items.get(i);
                System.out.printf("%d) %-15s %s%n", i + 1, item, ARS.format(r.getPrice(item)));
            }
            System.out.println("0) Volver");
            System.out.print("Elija una pizza por número: ");

            int opcion = leerEntero();
            if (opcion == 0) return;
            if (opcion < 1 || opcion > items.size()) {
                System.out.println("⚠️ Opción inválida.");
                continue;
            }

            String pizzaElegida = items.get(opcion - 1);
            System.out.print("¿Cantidad de " + pizzaElegida + "?: ");
            int cantidad = leerEntero();
            if (cantidad <= 0) {
                System.out.println("⚠️ La cantidad debe ser positiva.");
                continue;
            }

            o.addItem(pizzaElegida, cantidad);
            System.out.println("✔️ Agregado: " + cantidad + " x " + pizzaElegida +
                               " | Total parcial: " + ARS.format(o.getTotal()));
        }
    }

    /** Nueva acción: cambia el estado a EN CAMINO (requiere repartidor asignado). */
    private static void enviarOrden(Order o) {
        if (o.getStatus() == Order.Status.CANCELADO || o.getStatus() == Order.Status.ENTREGADO) {
            System.out.println("El pedido está finalizado. No se puede enviar.");
            return;
        }
        if (o.getStatus() == Order.Status.EN_CAMINO) {
            System.out.println("El pedido ya está EN CAMINO.");
            return;
        }
        if (o.getDriver() == null) {
            System.out.println("Primero asigne un repartidor (opción 4).");
            return;
        }
        o.startDelivery();
        System.out.println("🚚 Tu pedido fue enviado y ya está EN CAMINO.");
    }

    private static void asignarDriver(Order o, List<Driver> drivers) {
        if (o.getStatus() == Order.Status.CANCELADO || o.getStatus() == Order.Status.ENTREGADO) {
            System.out.println("El pedido está finalizado. No se puede asignar repartidor.");
            return;
        }
        if (o.getDriver() != null) {
            System.out.println("El pedido ya tiene repartidor: " + o.getDriver().getName());
            return;
        }
        List<Driver> libres = new ArrayList<>();
        for (Driver d : drivers) if (d.isAvailable()) libres.add(d);

        if (libres.isEmpty()) {
            System.out.println("No hay repartidores disponibles.");
            return;
        }

        System.out.println("\n--- Repartidores disponibles ---");
        for (int i = 0; i < libres.size(); i++) {
            System.out.printf("%d) %s%n", i + 1, libres.get(i).getName());
        }
        System.out.print("Elija un repartidor: ");
        int op = leerEntero();
        if (op < 1 || op > libres.size()) {
            System.out.println("⚠️ Opción inválida.");
            return;
        }

        Driver elegido = libres.get(op - 1);
        o.assignDriver(elegido);
        System.out.println("El repartidor " + elegido.getName() + " va a llevar tu pedido.");
        System.out.println("Estado del pedido: " + o.getStatus());
    }

    private static void marcarEntregado(Order o) {
        o.markDelivered();
        System.out.println("✅ Pedido ENTREGADO. Total final: " + ARS.format(o.getTotal()));
        if (o.getDriver() != null) {
            System.out.println("Repartidor " + o.getDriver().getName() + " quedó disponible nuevamente.");
        }
    }

    private static void cancelarPedido(Order o) {
        o.cancel();
        System.out.println("⛔ Pedido CANCELADO.");
        if (o.getDriver() != null) {
            System.out.println("Repartidor " + o.getDriver().getName() + " quedó disponible nuevamente.");
        }
    }

    private static int leerEntero() {
        while (true) {
            String s = in.nextLine().trim();
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.print("Ingrese un número válido: ");
            }
        }
    }
}
