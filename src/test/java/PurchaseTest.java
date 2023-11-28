import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class PurchaseTest {
    private ShoppingCart shoppingCart;
    private InMemorySalesSystemDAO dao;
    StockItem viin;
    StockItem kondoomid;
    StockItem pitsa;
    @Before
    public void setUp(){
        dao = new InMemorySalesSystemDAO();
        shoppingCart = new ShoppingCart(dao);
        viin = new StockItem(5L, "Lauaviin", "Teeb lõbusaks", 2.20, 12);
        kondoomid = new StockItem(6L, "Kondoomid", "Igaks juhuks", 6.99, 20);
        pitsa = new StockItem(7L, "Pitsa", "Pepperoni pitsa", 10.50, 10);
        dao.saveStockItem(viin);
        dao.saveStockItem(kondoomid);
        dao.saveStockItem(pitsa);
    }

    @Test
    public void testAddingNewItem(){
        shoppingCart.addItem(new SoldItem(viin, 1));
        assertFalse(shoppingCart.getAll().isEmpty());
    }

    @Test
    public void testAddingExistingItem(){
        shoppingCart.addItem(new SoldItem(viin, 1));
        shoppingCart.addItem(new SoldItem(viin, 1));
        assertEquals(2, shoppingCart.getAll().get(0).getQuantity(), 0);
    }

    @Test
    public void testAddingItemWithNegativeQuantity(){
        assertThrows(SalesSystemException.class, () -> {
            shoppingCart.addItem(new SoldItem(pitsa, -2));
        });
    }

    @Test
    public void testAddingItemWithQuantityTooLarge(){
        assertThrows(SalesSystemException.class, () -> {
            shoppingCart.addItem(new SoldItem(pitsa, 100));
        });
    }

    @Test
    public void testAddingItemWithQuantitySumTooLarge(){
        shoppingCart.addItem(new SoldItem(pitsa, 10));
        assertThrows(SalesSystemException.class, () -> {
            shoppingCart.addItem(new SoldItem(pitsa, 1));
        });
    }

    @Test
    public void testTotalPriceOfShoppingCart(){
        double realPrice = 3*2.20+1*6.99+4*10.50;
        shoppingCart.addItem(new SoldItem(viin, 3));
        shoppingCart.addItem(new SoldItem(kondoomid, 1));
        shoppingCart.addItem(new SoldItem(pitsa, 4));
        assertEquals(shoppingCart.getTotalPrice(), realPrice, 0.0001);
    }

    @Test
    public void testWarehouseQuantityDecreasingOnPurchase(){
        shoppingCart.addItem(new SoldItem(viin, 3));
        shoppingCart.submitCurrentPurchase();
        assertEquals(dao.findStockItem(5).getQuantity(), 9, 0.0001);
    }
}
