package ee.ut.math.tvt.salessystem.dataobjects;

/**
 * Stock item.
 */
public class StockItem {

    private Long id;
    private String name;
    private double price;
    private String description;
    private int quantity;

    public StockItem() {
    }

    public StockItem(Long id, String name, String desc, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.description = desc;
        if (price < 0 || quantity < 0) throw new IllegalArgumentException("Price and quantity must be non-negative");
        this.price = price;
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price < 0 ) throw new IllegalArgumentException("Price must be non-negative");
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0 ) throw new IllegalArgumentException("Quantity must be non-negative");
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return String.format("StockItem{id=%d, name='%s'}", id, name);
    }
}
