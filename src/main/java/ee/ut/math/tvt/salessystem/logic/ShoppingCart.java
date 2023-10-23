package ee.ut.math.tvt.salessystem.logic;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShoppingCart {

    private final SalesSystemDAO dao;
    private final List<SoldItem> items = new ArrayList<>();

    public ShoppingCart(SalesSystemDAO dao) {
        this.dao = dao;
    }

    /**
     * Add new SoldItem to table.
     */
    public void addItem(SoldItem item) {
        boolean found = false;
        long id;
        System.out.println(item);
        for (SoldItem soldItem: items) {
            id = item.getId();
            if(!Objects.equals(soldItem.getId(), id))
                continue;

            StockItem stockItem = dao.findStockItem(id);
            if(stockItem.getQuantity() == 0)
                throw new RuntimeException("Can't add more items than are in stock.");

            soldItem.setQuantity(soldItem.getQuantity()+1);
            found = true;
            break;
        }
        if(found)
            return;

        items.add(item);
        //log.debug("Added " + item.getName() + " quantity of " + item.getQuantity());
    }

    public List<SoldItem> getAll() {
        return items;
    }

    public void cancelCurrentPurchase() {
        items.clear();
    }

    public void submitCurrentPurchase() {
        // TODO decrease quantities of the warehouse stock
        //TODO change dao.saveSoldItem() so it would add a historyItem
        // instead of SoldItem (check HistroyItem and DAO)

        // note the use of transactions. InMemorySalesSystemDAO ignores transactions
        // but when you start using hibernate in lab5, then it will become relevant.
        // what is a transaction? https://stackoverflow.com/q/974596
        dao.beginTransaction();
        try {
            for (SoldItem item : items) {
                //dao.saveSoldItem(item);   TODO change so it would take in new HistoryItem()
            }
            dao.commitTransaction();
            items.clear();
        } catch (Exception e) {
            dao.rollbackTransaction();
            throw e;
        }
    }
}
