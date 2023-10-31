import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.HistoryItem;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import org.junit.Before;
import org.junit.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HistoryTabTest{


    private InMemorySalesSystemDAO dao = new InMemorySalesSystemDAO();
    private List<HistoryItem> testItems;
    private List<HistoryItem> historyItemList = dao.getHistoryList();


    /**
     * Sets up 5 history items with dates between 2.9.2023 - 25.10.2023
     */
    @Before
    public void setup(){
        StockItem newItem1 = new StockItem(5L, "Potato", "Potato", 2.0, 20);
        StockItem newItem2 = new StockItem(6L, "salad", "Potato", 1.0, 60);
        StockItem newItem3 = new StockItem(7L, "chips", "Potato", 3.0, 70);
        StockItem newItem4 = new StockItem(8L, "cider", "Potato", 5.0, 80);
        StockItem newItem5 = new StockItem(9L, "dish soap", "Potato", 7.0, 40);
        StockItem newItem6 = new StockItem(10L, "meat", "Potato", 11.0, 20);
        StockItem newItem7 = new StockItem(11L, "fish", "Potato", 12.0, 5);

        SoldItem newSold1 = new SoldItem(newItem1,3);
        SoldItem newSold2 = new SoldItem(newItem2,3);
        SoldItem newSold3 = new SoldItem(newItem3,3);
        SoldItem newSold4 = new SoldItem(newItem4,3);
        SoldItem newSold5 = new SoldItem(newItem5,3);
        SoldItem newSold6 = new SoldItem(newItem6,3);
        SoldItem newSold7 = new SoldItem(newItem7,3);
        List<SoldItem> list1 = Arrays.asList(newSold1,newSold3,newSold5);
        List<SoldItem> list2 = Arrays.asList(newSold2,newSold4,newSold6);
        List<SoldItem> list3 = Arrays.asList(newSold5,newSold3,newSold7);
        HistoryItem newHistory1 = new HistoryItem(list1,LocalDate.of(2023,10,20),LocalTime.now().plusMinutes(4),2.0);
        HistoryItem newHistory2 = new HistoryItem(list2,LocalDate.of(2023,9,2),LocalTime.now().plusMinutes(7),673.0);
        HistoryItem newHistory3 = new HistoryItem(list3,LocalDate.of(2023,10,21),LocalTime.now().plusMinutes(2),2022.0);
        HistoryItem newHistory4 = new HistoryItem(list1,LocalDate.of(2023,10,24),LocalTime.now().plusMinutes(3),2001.0);
        HistoryItem newHistory5 = new HistoryItem(list2,LocalDate.of(2023,10,25),LocalTime.now().plusMinutes(3),216.0);
        HistoryItem newHistory6 = new HistoryItem(list3,LocalDate.of(2023,10,24),LocalTime.now().plusMinutes(5),26.0);
        HistoryItem newHistory7 = new HistoryItem(list2,LocalDate.of(2023,10,25),LocalTime.now().plusMinutes(8),53.0);
        HistoryItem newHistory8 = new HistoryItem(list1,LocalDate.of(2023,10,23),LocalTime.now().plusMinutes(5),53.0);
        HistoryItem newHistory9 = new HistoryItem(list2,LocalDate.of(2023,10,11),LocalTime.now().plusMinutes(7),1.0);
        HistoryItem newHistory10 = new HistoryItem(list1,LocalDate.of(2023,10,30),LocalTime.now().plusMinutes(9),202.0);
        HistoryItem newHistory11 = new HistoryItem(list3,LocalDate.of(2023,10,22),LocalTime.now().plusMinutes(20),22.0);

        dao.saveHistoryItem(newHistory1);
        dao.saveHistoryItem(newHistory2);
        dao.saveHistoryItem(newHistory3);
        dao.saveHistoryItem(newHistory4);
        dao.saveHistoryItem(newHistory5);
        dao.saveHistoryItem(newHistory6);
        dao.saveHistoryItem(newHistory7);
        dao.saveHistoryItem(newHistory8);
        dao.saveHistoryItem(newHistory9);
        dao.saveHistoryItem(newHistory10);
        dao.saveHistoryItem(newHistory11);

        testItems = Arrays.asList(newHistory1,newHistory2,newHistory3,newHistory4,newHistory5,newHistory6,newHistory7,newHistory8,newHistory9,newHistory10,newHistory11);
        historyItemList.sort(Comparator.comparing(HistoryItem::getTime));
        testItems.sort(Comparator.comparing(HistoryItem::getTime));
        //TODO add FXMLoader to test the date method.

    }


     @Test
    public void testShowAll(){
        assertNotNull(dao.getHistoryList());
        assertNotNull(testItems);
        assertEquals(historyItemList,testItems);
    }

    @Test
    public void testShowLast10(){
        assertNotNull(historyItemList);
        assertNotNull(testItems);

        if (testItems.size()>10){
            assertEquals(historyItemList.subList(0,10),testItems.subList(0,10));
        }else {
            assertEquals(historyItemList,testItems);
        }
    }

    @Test public void testShowBetweenDates10(){
        //TODO
    }
}
