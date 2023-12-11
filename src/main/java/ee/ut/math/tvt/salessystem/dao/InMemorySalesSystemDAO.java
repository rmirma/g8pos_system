package ee.ut.math.tvt.salessystem.dao;

import ee.ut.math.tvt.salessystem.dataobjects.HistoryItem;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import net.bytebuddy.asm.Advice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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
        List<SoldItem> soldItems = List.of(new SoldItem(stockItemList.get(0), 1), new SoldItem(stockItemList.get(1), 1), new SoldItem(stockItemList.get(2), 1));
        double total = 0;
        for (SoldItem soldItem : soldItems) {
            total += soldItem.getSum();
        }
        LocalDate date = LocalDate.of(2023, 12, 11);
        LocalTime time = LocalTime.now();
        historyList.add(new HistoryItem(soldItems, date, time, total));
        date = LocalDate.of(2023, 12, 2);
        time = LocalTime.of(15, 30, 25);
        historyList.add(new HistoryItem(soldItems, date, time, total));
    }

    @Override
    public List<StockItem> findStockItems() {
        return stockItemList;
    }

    @Override
    public StockItem findStockItem(long id) {
        for (StockItem item : stockItemList) {
            if (item.getId() == id)
                return item;
        }
        return null;
    }
    /**
     * Method returns The list of HistoryItems
     * @return list of historyItems
     */
    public List<HistoryItem>getHistoryList(){
        return historyList;
    }

    @Override
    public List<HistoryItem> getHistoryListLast10() {
        int i = 10;
        List<HistoryItem> last10 = new ArrayList<>();
        for (int j = 0; j < historyList.size(); j++,i--) {
            if(i==0)
                break;
            last10.add(historyList.get(j));
        }
        return last10;
    }

    @Override
    public List<HistoryItem> getHistoryItemBetweenDates(LocalDate start, LocalDate end) {
        List<HistoryItem> betweenDates = new ArrayList<>();
        for (HistoryItem historyItem : historyList) {
            // not before == equal or after, not after == equal or before
            if(!historyItem.getDate().isBefore(start) && !historyItem.getDate().isAfter(end))
                betweenDates.add(historyItem);
        }
        return betweenDates;
    }

    @Override
    public List<SoldItem> findContentsOfPurchase(HistoryItem item) {
        return item.getSoldItems();
    }

    @Override
    public List<SoldItem> findContentsOfPurchase(LocalDate date, LocalTime time) {
        for (HistoryItem historyItem : historyList) {
            if (historyItem.getDate().equals(date) && historyItem.getTime().equals(time))
                return historyItem.getSoldItems();
        }
        return null;
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
    public void saveSoldItem(SoldItem item) {}

    @Override
    public void removeItem(StockItem StockItem) {
        stockItemList.remove(StockItem);
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
