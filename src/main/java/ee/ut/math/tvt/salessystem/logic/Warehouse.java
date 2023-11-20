package ee.ut.math.tvt.salessystem.logic;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;

public class Warehouse {

    private final SalesSystemDAO dao;

    public Warehouse(SalesSystemDAO dao) {
        this.dao = dao;
    }

    public void addItem(long barCode, String name, String desc, double price, int quantity) {
        dao.beginTransaction();

        try {
            if (dao.findStockItem(barCode) != null) {
                StockItem item = dao.findStockItem(barCode);
                item.setName(name);
                if (item.getQuantity()+quantity <= 0) {
                    removeItem(barCode);
                }
                else {
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
        }
    }
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