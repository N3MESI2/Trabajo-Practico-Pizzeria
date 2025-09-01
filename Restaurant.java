import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Restaurant {
    private String name;
    private Map<String, Double> menu;

    public Restaurant(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Nombre inválido");
        this.name = name;
        this.menu = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void addMenuItem(String item, double price) {
        if (item == null || item.isBlank()) throw new IllegalArgumentException("Descripción vacía");
        if (price < 0) throw new IllegalArgumentException("Precio inválido");
        menu.put(item, price);
    }

    public Double getPrice(String item) {
        return menu.get(item);
    }

    public Map<String, Double> getMenu() {
        return Collections.unmodifiableMap(menu);
    }

    @Override
    public String toString() {
        return "Restaurant{name='" + name + "', menu=" + menu + "}";
    }
}
