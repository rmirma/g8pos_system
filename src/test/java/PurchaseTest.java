import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.HistoryItem;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalTime;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PurchaseTest {
    private InMemorySalesSystemDAO dao;
    private ShoppingCart shoppingCart;
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
    public void  testSubmittingCurrentPurchaseDecreasesStockItemQuantity(){
        shoppingCart.addItem(new SoldItem(viin, 3));
        shoppingCart.submitCurrentPurchase();
        assertEquals(dao.findStockItem(5).getQuantity(), 9, 0.0001);
    }

    //mockito
    @Test
    public void testSubmittingCurrentPurchaseBeginsAndCommitsTransaction(){
        InMemorySalesSystemDAO mockDao = mock(InMemorySalesSystemDAO.class);
        ShoppingCart mockShoppingCart = new ShoppingCart(mockDao);
        mockShoppingCart.submitCurrentPurchase();
        verify(mockDao).beginTransaction();
        verify(mockDao).commitTransaction();
    }

    @Test
    public void testSubmittingCurrentOrderCreatesHistoryItem(){
        shoppingCart.addItem(new SoldItem(kondoomid, 2));
        shoppingCart.addItem(new SoldItem(pitsa, 3));
        shoppingCart.submitCurrentPurchase();
        int lastIndexOfHistoryList = dao.getHistoryList().size()-1;
        HistoryItem historyItem = dao.getHistoryList().get(lastIndexOfHistoryList);
        boolean historyItemIsCorrect = true;
        SoldItem item1 = historyItem.getSoldItems().get(0);
        SoldItem item2 = historyItem.getSoldItems().get(1);
        if(item1.getId() != 6 | item1.getQuantity() != 2)
            historyItemIsCorrect = false;
        if(item2.getId() != 7 | item2.getQuantity() != 3)
            historyItemIsCorrect = false;
        assertTrue(historyItemIsCorrect);
    }

    @Test
    public void testSubmittingCurrentOrderSavesCorrectTime(){
        shoppingCart.addItem(new SoldItem(viin, 7));
        shoppingCart.submitCurrentPurchase();
        long currentTime = LocalTime.now().getSecond();
        HistoryItem historyItem = dao.getHistoryList().get(0);
        assertEquals(currentTime, historyItem.getTime().getSecond(), 1);
    }

    @Test
    public void testCancellingOrder(){
        shoppingCart.addItem(new SoldItem(viin, 2));
        shoppingCart.cancelCurrentPurchase();
        shoppingCart.addItem(new SoldItem(pitsa, 5));
        shoppingCart.submitCurrentPurchase();
        int lastIndexOfHistoryList = dao.getHistoryList().size()-1;
        SoldItem item = dao.getHistoryList().get(lastIndexOfHistoryList).getSoldItems().get(0);
        assertTrue(item.getId()==7&&item.getQuantity()==5);
    }

    @Test
    public void testCancellingOrderQuantitiesUnchanged(){
        shoppingCart.addItem(new SoldItem(kondoomid, 3));
        shoppingCart.cancelCurrentPurchase();
        assertEquals(20, kondoomid.getQuantity());
    }
}
