import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.Warehouse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.inOrder;

@RunWith(MockitoJUnitRunner.class)
public class WarehouseTest{
    private void assertEqual(Class<? extends StockItem> aClass, Class<? extends StockItem> aClass1) {
    }

    private InMemorySalesSystemDAO dao;
    StockItem stockItem;

    @Mock
    SalesSystemDAO DAO;

    @InjectMocks
    Warehouse warehouse;

    @Before
    public void setUp() {
        dao = new InMemorySalesSystemDAO();
        stockItem = new StockItem(5L, "Test", "Test", 10.0, 50);
    }
    @Test
    public void testAddingItemBeginsAndCommitsTransaction(){
        warehouse.addItem(5L, "Test", "Test", 10.0, 50);
        InOrder inOrder = inOrder(DAO);
        inOrder.verify(DAO).beginTransaction();
        inOrder.verify(DAO).commitTransaction();
    }

    @Test
    public void testAddingNewItem() {
        dao.saveStockItem(stockItem);

        StockItem addedItem = dao.findStockItem(5L);

        assertEqual(stockItem.getClass(), addedItem.getClass());
        assertEquals(stockItem.getId(), addedItem.getId());
    }

    @Test
    public void testAddingExistingItem() {
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
            warehouse.addItem(5L, "Test", "Test", 10.0, -1);
        });
    }
    @Test
    public void testAddingItemWithNegativePrice(){
        assertThrows(IllegalArgumentException.class, () -> {
            warehouse.addItem(5L, "Test", "Test", -10.0, 1);
        });
    }
}