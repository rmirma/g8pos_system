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

import javax.swing.text.TableView;

public class ShoppingCart {

    private final SalesSystemDAO dao;
    private final List<SoldItem> items = new ArrayList<>();
    private double totalPrice = 0d;
    private static final Logger log = LogManager.getLogger(ShoppingCart.class);


    public ShoppingCart(SalesSystemDAO dao) {
        this.dao = dao;
    }

    /**
     * Add new SoldItem to table.
     */
    public void addItem(SoldItem item) {
        Optional<SoldItem> existingItem = contains(item);
        existingItem.ifPresentOrElse(
                (soldItem) -> {soldItem.setQuantity(soldItem.getQuantity()+item.getQuantity());},
                () -> {items.add(item);}
        );
        totalPrice += item.getPrice()*item.getQuantity();
    }

    /**
      * Gets the total price owed for the shopping cart
      */
    public double getTotalPrice(){
        return totalPrice;
    }

    /**
     * Remove a SoldItem from the table
     */
    public void removeItem(SoldItem item){
        items.remove(item);
        totalPrice -= item.getPrice()*item.getQuantity();
    }

    /**
     * Get a list of all the items in the shopping cart
     * @return list of SoldItems
     */
    public List<SoldItem> getAll() {
        return items;
    }

    /**
     * Checks if the shopping cart has an item
     * @param item SoldItem to be checked
     * @return optional of type SoldItem
     */
    public Optional<SoldItem> contains(SoldItem item){
        return items.stream().filter(soldItem -> soldItem.getId().equals(item.getId())).findFirst();
    }

    /**
     * Empties shopping cart
     */
    public void cancelCurrentPurchase() {
        items.clear();
    }

    /**
     * Submits the ongoing purchase, saving the data into the database
     */
    public void submitCurrentPurchase() {
        if(items.isEmpty())
            return;
        dao.beginTransaction();
        HistoryItem history = new HistoryItem(new ArrayList<>(), LocalDate.now(), LocalTime.now(), totalPrice);
        try {
            //totalPrice -> price of the shopping cart
            for (SoldItem item : items) {
                dao.saveSoldItem(item);
                StockItem itemToDecrease = dao.findStockItem(item.getId());
                itemToDecrease.setQuantity(itemToDecrease.getQuantity()-item.getQuantity());
                item.setHistoryItem(history);
                history.setSoldItem(item);
            }
            dao.saveHistoryItem(history);
            dao.commitTransaction();

            items.clear();
            totalPrice = 0d;
            log.info("new HistoryItem was created, time of creation: " + LocalDate.now() + " " + LocalTime.now());

        } catch (Exception e) {
            dao.rollbackTransaction();
            throw e;
        }
    }
}
