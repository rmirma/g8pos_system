package ee.ut.math.tvt.salessystem.logic;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;

import java.util.Objects;

public class Warehouse {

    private final SalesSystemDAO dao;

    public Warehouse(SalesSystemDAO dao) {
        this.dao = dao;
    }

    /**
     * Adds new StockItem to warehouse. If StockItem with the same id is already
     * in warehouse, then existing StockItem's data will be updated.
     * Handles transactions and rollbacks if necessary.
     * Checks if any of the fields have invalid values ad throws an exception if so.
     * @param barCode product barcode
     * @param name product name
     * @param desc product description
     * @param price product price
     * @param quantity product quantity
     */
    public void addItem(long barCode, String name, String desc, double price, int quantity) {

        //if any of the fields have invalid values, throw an exception
        if (Objects.equals(name, "")) throw new IllegalArgumentException("Name must not be empty");
        else if (price < 0) throw new IllegalArgumentException("Price must be non-negative");
        else if (quantity < 0) throw new IllegalArgumentException("Quantity must be non-negative");
        else if (barCode < 0) throw new IllegalArgumentException("Id must be non-negative");

        dao.beginTransaction();
        try {
            if (dao.findStockItem(barCode) != null) {
                    StockItem item = dao.findStockItem(barCode);
                    item.setName(name);
                    if (item.getQuantity() + quantity <= 0) {
                        removeItem(barCode);
                    } else {
                        item.setQuantity(item.getQuantity() + quantity);
                        item.setPrice(price);
                        dao.commitTransaction();
                    }

            } else {
                dao.saveStockItem(new StockItem(barCode, name, desc, price, quantity));
                dao.commitTransaction();
            }
        } catch (Exception e) {
            dao.rollbackTransaction();
            throw new IllegalArgumentException("Invalid input");
        }
    }

    /**
     * Removes StockItem from warehouse.
     * @param barCode product barcode
     */
    public void removeItem(long barCode) {
        dao.beginTransaction();
        try {
            StockItem selectedItem = dao.findStockItem(barCode);
            if (selectedItem != null) {
                dao.removeItem(selectedItem);
            }
            dao.commitTransaction();
            System.out.println("Item removed!");
        } catch (Exception e) {
            dao.rollbackTransaction();
            
        }
    }
}