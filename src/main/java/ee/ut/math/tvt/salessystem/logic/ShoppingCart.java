package ee.ut.math.tvt.salessystem.logic;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
        items.add(item);
    }

    /**
     * Remove a SoldItem from the table
     */
    public void removeItem(SoldItem item){
        items.remove(item);
    }

    /**
     * Increases amount of existing SoldItem in table
     */
    public void increaseItemQuantity(SoldItem item){
        SoldItem existingItem = items.stream().filter(soldItem -> soldItem.getId().equals(item.getId())).toList().get(0);
        existingItem.setQuantity(existingItem.getQuantity()+item.getQuantity());
    }

    public List<SoldItem> getAll() {
        return items;
    }

    public boolean contains(SoldItem item){
        return items.stream().anyMatch(soldItem -> soldItem.getId().equals(item.getId()));
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
