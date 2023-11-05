package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.dataobjects.HistoryItem;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.ui.SalesSystemUI;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

/**
 * Encapsulates everything that has to do with the history tabs controls (the tab
 * labelled "History" in the GUI menu).
 *
 */


public class HistoryController implements Initializable {
    private static final Logger log = LogManager.getLogger(PurchaseController.class);
    private final SalesSystemDAO dao;

    @FXML
    private DatePicker startDate;

    @FXML
    private DatePicker endDate;

    @FXML
    private TableView<HistoryItem> HistoryView;

    @FXML
    private TableView<SoldItem> ShoppingCartView;


    //ShoppingCaryView columns
    @FXML
    private TableColumn<SoldItem,Long> Id;
    @FXML
    private TableColumn<SoldItem,String> Name;
    @FXML
    private TableColumn<SoldItem,String> Price;
    @FXML
    private TableColumn<SoldItem,String> Quantity;
    @FXML
    private TableColumn<SoldItem,String> Sum;




    //for showing last viewd HistoryItem's shopping cart contents
    private List<HistoryItem> itemsBetweenDates = new ArrayList<>();


    /**
     * Creates new instance of a HistoryController. Must be tied to HistoryTab.fxml
     * @param dao > built in temporary database
     */
    public HistoryController(SalesSystemDAO dao) {
        this.dao = dao;

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showAll();
        //setting ShoppingCartView column values from SoldItem
        Id.setCellValueFactory(new PropertyValueFactory<>("id"));
        Name.setCellValueFactory(new PropertyValueFactory<>("name"));
        Price.setCellValueFactory(new PropertyValueFactory<>("price"));
        Quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
    }


    /**
     * Returns The last viewd HistoryItems shown between dates. If last
     * try to view HistoryItems between dates logged an error /threw error,
     * Then the return value will be empty ArrayList< HistoryItem >.
     */
    public List<HistoryItem> getItemsBetweenDates(){
        return itemsBetweenDates;
    }


    /////////////////////////////////FXML/GUI methods////////////////////////////////////////////////


    /**
     * Shows all the purchase history (HistoryItems) in the HistoryView section
     * of HistoryTab.fxml.
     */
    @FXML
    public void showAll(){
        HistoryView.setItems(FXCollections.observableList(dao.getHistoryList()));
        HistoryView.refresh();
        log.info("Shows All purchases in History tab");
    }


    /**
     * Shows last 10 purchases made (HistoryItems) in HistoryView section
     * of HistoryTab.fxml
     */
    @FXML
    public void showLast10(){
        if (dao.getHistoryList().size() < 10){
            HistoryView.setItems(FXCollections.observableList(dao.getHistoryList()));
            HistoryView.refresh();
            log.info("Last 10 purchases shown in History tab");
        }else{
            HistoryView.setItems(FXCollections.observableList(dao.getHistoryList().subList(0,10)));
            HistoryView.refresh();
            log.info("Last 10 purchases shown in History tab");
        }
    }


    /**
     * Shows all the purchases (HistoryItems) made between the dates (both inclusive).
     * Method takes start and end date from HistoryTab.fxml button values "startDate"
     * and "endDate". If any field is empty or start date is before end date, throws
     * error and logs error.
     */
    @FXML
    public void showBetweenDates(){
        itemsBetweenDates.clear();
        try{
            LocalDate start = startDate.getValue();
            LocalDate end = endDate.getValue();
            if (start != null &&
                    end != null &&
                        start.isBefore(end.plusDays(1)) &&   // +1 to be inclusive
                            start.isBefore(LocalDate.now().plusDays(1))){
                for (HistoryItem item : dao.getHistoryList()) {
                    if (item.getDate().isAfter(start.minusDays(1)) && // +1 to be inclusive
                        item.getDate().isBefore(end.plusDays(1))){
                        itemsBetweenDates.add(item);
                    }
                }
                HistoryView.setItems(FXCollections.observableList(itemsBetweenDates));
                HistoryView.refresh();
                log.info("History shown between " + start + " - " + end);
            }else{
                //shows error screen
                SalesSystemUI.alert.setContentText("Please check that the dates are selected \nand that they are in logical order");
                SalesSystemUI.alert.setTitle("ERROR with dates");
                SalesSystemUI.alert.show();  //will autoclose on close
                log.error("Error in loading showBetweenDates method in HistoryController.java");
            }
        }catch (Exception e) {
            //throw new SalesSystemException("error in loading items between dates, see showBetweenDates()");
            log.error("Error in loading showBetweenDates method in HistoryController.java");
        }
    }


    /**
     * Shows selected purchase (HistoryItems) shopping cart/purchase contents
     * in ShoppingCartView in HistoryTab.fxml.Gets HistoryItem from
     * HistroyView.getSelectionModel(). If error occurs logs error.
     */
    @FXML
    public void showShoppingCartContents(){
        try{
            List<SoldItem> shoppingCart = HistoryView.getSelectionModel().getSelectedItem().getContents();
            ShoppingCartView.setItems(FXCollections.observableList(shoppingCart));
            log.info("contents of purchase " + HistoryView.getSelectionModel().getSelectedItem().getDate() +
                    " " + HistoryView.getSelectionModel().getSelectedItem().getTime() + " shown");
            ShoppingCartView.refresh();
        }catch (Exception e){
            //throw new SalesSystemException("error with loading contenst from selected purchase");
            log.error("error loading shoppingCartContents, reffer to showShoppingCartContents() in HistoryController");
            }
        }
    }//HistoryController

