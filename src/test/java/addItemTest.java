import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class addItemTest {
    private void assertEqual(Class<? extends StockItem> aClass, Class<? extends StockItem> aClass1) {
    }

    private InMemorySalesSystemDAO dao;
    StockItem stockItem;

    @Before
    public void setUp() {
        dao = new InMemorySalesSystemDAO();
        stockItem = new StockItem(5L, "Test", "Test", 10.0, 50);
    }
    @Test
    public void testAddingItemBeginsAndCommitsTransaction(){
        // TODO:
        //  check that methods beginTransaction and commitTransaction
        //  are both called exactly once and that order
    }
    @Test
    public void testAddingNewItem() {
        dao.saveStockItem(stockItem);

        StockItem addedItem = dao.findStockItem(5L);

        assertNotNull(addedItem);
        assertEqual(stockItem.getClass(), addedItem.getClass());
        assertEquals(stockItem.getId(), addedItem.getId());
        assertEquals(stockItem.getName(), addedItem.getName());
        assertEquals(stockItem.getPrice(), addedItem.getPrice(), 0.001);
        assertEquals(stockItem.getQuantity(), addedItem.getQuantity());
    }

    @Test
    public void testAddingExistingItem() {
        //TODO: make it make sense

        dao.saveStockItem(stockItem);

        stockItem.setName("Water");
        stockItem.setPrice(20);
        stockItem.setQuantity(100);

        StockItem updatedItem = dao.findStockItem(5L);

        assertNotNull(updatedItem);
        assertEquals("Water", updatedItem.getName());
        assertEquals(20, updatedItem.getPrice(), 0.001);
        assertEquals(100, updatedItem.getQuantity());
    }
    @Test
    public void testAddingItemWithNegativeQuantity(){
        assertThrows(IllegalArgumentException.class, () -> {
            StockItem newItem = new StockItem(6L, "Test", "Test", 10.0, -1);
            dao.saveStockItem(newItem);
        });
    }
    @Test
    public void testAddingItemWithNegativePrice(){
        assertThrows(IllegalArgumentException.class, () -> {
            StockItem newItem = new StockItem(6L, "Test", "Test", -1, 50);
            dao.saveStockItem(newItem);
        });
    }
}