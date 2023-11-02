package ee.ut.math.tvt.salessystem.logic;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.HistoryItem;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShoppingCart {

    private final SalesSystemDAO dao;
    private final List<SoldItem> items = new ArrayList<>();
    private static final Logger log = LogManager.getLogger(ShoppingCart.class);


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

        // note the use of transactions. InMemorySalesSystemDAO ignores transactions
        // but when you start using hibernate in lab5, then it will become relevant.
        // what is a transaction? https://stackoverflow.com/q/974596
        dao.beginTransaction();
        try {

            //totalPrice -> price of the shopping cart
            Double totalPrice = 0.0;
            for (SoldItem item : items) {
                //TODO decrease stock items

                //calculates the total price of the order,used in HistoryItem
                totalPrice += item.getPrice()*item.getQuantity();
            }

            //new history item is created
            dao.saveHistoryItem(new HistoryItem(
                    new ArrayList<>(items),
                    LocalDate.now(),
                    LocalTime.now(),
                    totalPrice));
            log.info("new HistoryItem was created, time of creation: " + LocalDate.now() + " " + LocalTime.now());
            dao.commitTransaction();
            items.clear();
        } catch (Exception e) {
            dao.rollbackTransaction();
            throw e;
        }
    }
}
