import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class addItemTest {
    private void assertEqual(Class<? extends StockItem> aClass, Class<? extends StockItem> aClass1) {
    }

    private InMemorySalesSystemDAO dao;
    private ShoppingCart cart;

    @Before
    public void setUp() {
        dao = new InMemorySalesSystemDAO();
        cart = new ShoppingCart(dao);
    }
    @Test
    public void testAddingNewItem() {
        StockItem newItem = new StockItem(5L, "Potato", "Potato", 1.0, 50);

        dao.saveStockItem(newItem);

        StockItem addedItem = dao.findStockItem(5L);

        assertNotNull(addedItem);
        assertEqual(newItem.getClass(), addedItem.getClass());
        assertEquals(newItem.getId(), addedItem.getId());
        assertEquals(newItem.getName(), addedItem.getName());
        assertEquals(newItem.getPrice(), addedItem.getPrice(), 0.001);
        assertEquals(newItem.getQuantity(), addedItem.getQuantity());
    }

    @Test
    public void testAddingExistingItem() {
        StockItem newItem = new StockItem(6L, "Test", "Test", 10.0, 50);

        dao.saveStockItem(newItem);

        newItem.setName("Water");
        newItem.setPrice(20);
        newItem.setQuantity(100);

        StockItem updatedItem = dao.findStockItem(6L);

        assertNotNull(updatedItem);
        assertEquals("Water", updatedItem.getName());
        assertEquals(20, updatedItem.getPrice(), 0.001);
        assertEquals(100, updatedItem.getQuantity());
    }

    @Test
    public void testAddingItemWithNegativeQuantity() {
        StockItem stockItem = new StockItem((long) 12, "Onion", "Onion", 10.0, 12);
        SoldItem soldItem = new SoldItem(stockItem, -1);
        cart.addItem(soldItem);
    }


}