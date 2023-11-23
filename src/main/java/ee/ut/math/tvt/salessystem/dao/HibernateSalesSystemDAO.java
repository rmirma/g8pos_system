package ee.ut.math.tvt.salessystem.dao;

import ee.ut.math.tvt.salessystem.dataobjects.HistoryItem;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HibernateSalesSystemDAO implements SalesSystemDAO {
    private final EntityManagerFactory emf;
    private final EntityManager em;

    public HibernateSalesSystemDAO() {
// if you get ConnectException / JDBCConnectionException then you
// probably forgot to start the database before starting the application
        emf = Persistence.createEntityManagerFactory("pos");
        em = emf.createEntityManager();
    }

    // TODO implement missing methods
    public void close() {
        em.close();
        emf.close();
    }

    @Override
    public List<StockItem> findStockItems() {
        return em.createQuery("from StockItem", StockItem.class).getResultList();
    }

    @Override
    public StockItem findStockItem(long id) {
        return em.find(StockItem.class, id);
    }

    @Override
    public void saveStockItem(StockItem stockItem) {
        em.merge(stockItem);
    }

    @Override
    public void saveSoldItem(SoldItem soldItem) {
        em.merge(soldItem);
    }

    @Override
    public void removeItem(StockItem StockItem) {
        em.remove(StockItem);
    }

    @Override
    public void beginTransaction() {
        em.getTransaction().begin();
    }

    @Override
    public void rollbackTransaction() {
        em.getTransaction().rollback();
    }

    @Override
    public void commitTransaction() {
        em.getTransaction().commit();
    }

    //History methods
    @Override
    public void saveHistoryItem(HistoryItem item) {
        em.merge(item);
    }
    @Override
    public List<HistoryItem> getHistoryList() {
        List<HistoryItem> items = em.createQuery("from HistoryItem", HistoryItem.class).getResultList();

        items.sort(Comparator.comparing(HistoryItem::getDate).thenComparing(HistoryItem::getTime)); //sorts items
        Collections.reverse(items);
        return items;
    }

    @Override
    public List<HistoryItem> getHistoryListLast10() {
        List<HistoryItem> items = em.createQuery("from HistoryItem", HistoryItem.class).getResultList();

        items.sort(Comparator.comparing(HistoryItem::getDate).thenComparing(HistoryItem::getTime)); //sorts items
        Collections.reverse(items);
        return items.size() > 10 ? items.subList(0,10) : items;

    }
    @Override
    public List<HistoryItem> getHistoryItemBetweenDates(LocalDate start, LocalDate end) {
        String jgl = "select e from HistoryItem e where e.date between :value1 and :value2";
        List<HistoryItem> items = em.createQuery(jgl, HistoryItem.class).setParameter("value1", start).setParameter("value2", end).getResultList();

        items.sort(Comparator.comparing(HistoryItem::getDate).thenComparing(HistoryItem::getTime)); //sorts items
        Collections.reverse(items);
        return items;
    }

    //TODO implement
    public List<SoldItem> findContentsOfPurchase(HistoryItem item){
        String jql = "select e from SoldItem e where e.history = :item";
        return em.createQuery(jql, SoldItem.class).setParameter("item", item).getResultList();
    }
}