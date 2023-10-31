package ee.ut.math.tvt.salessystem.dao;

import ee.ut.math.tvt.salessystem.dataobjects.HistoryItem;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InMemorySalesSystemDAO implements SalesSystemDAO {

    private final List<StockItem> stockItemList;
    private final List<HistoryItem> historyList;   //contains all completed transactions and their data

    public InMemorySalesSystemDAO() {
        List<StockItem> items = new ArrayList<StockItem>();
        items.add(new StockItem(1L, "Lays chips", "Potato chips", 11.0, 5));
        items.add(new StockItem(2L, "Chupa-chups", "Sweets", 8.0, 8));
        items.add(new StockItem(3L, "Frankfurters", "Beer sausages", 15.0, 12));
        items.add(new StockItem(4L, "Free Beer", "Student's delight", 0.0, 100));

        this.stockItemList = items;
        this.historyList = new ArrayList<>();
    }

    @Override
    public List<StockItem> findStockItems() {
        return stockItemList;
    }


    @Override
    public void decreaseItemQuantites(List<SoldItem> items){
        for (SoldItem item : items) {
            StockItem itemToDecrease = findStockItem(item.getId());
            itemToDecrease.setQuantity(itemToDecrease.getQuantity()-item.getQuantity());
        }
    }

    @Override
    public StockItem findStockItem(long id) {
        for (StockItem item : stockItemList) {
            if (item.getId() == id)
                return item;
        }
        return null;
    }
    @Override
    public List<StockItem> findStockItem(String name) {
        List<StockItem> stockItems = new ArrayList<>();
        for (StockItem item : stockItemList) {
            if (Objects.equals(item.getName(), name)) {
                stockItems.add(item);
            }
        }
        return stockItems;
    }

    /**
     * Method returns The list of HistoryItems
     * @return list of historyItems
     */
    public List<HistoryItem>getHistoryList(){
        return historyList;
    }

    @Override
    public void saveHistoryItem(HistoryItem item) {
      historyList.add(0,item);
    }

    @Override
    public void saveStockItem(StockItem stockItem) {
        stockItemList.add(stockItem);
    }

    @Override
    public void beginTransaction() {
    }

    @Override
    public void rollbackTransaction() {
    }

    @Override
    public void commitTransaction() {
    }
}
