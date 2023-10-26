package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.dataobjects.HistoryItem;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.net.URL;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Encapsulates everything that has to do with the history tab (the tab
 * labelled "History" in the menu).
 *
 */


public class HistoryController implements Initializable {
    private static final Logger log = LogManager.getLogger(PurchaseController.class);
    private SalesSystemDAO dao;

    @FXML
    private DatePicker startDate;

    @FXML
    private DatePicker endDate;

    @FXML
    private TableView<HistoryItem> HistoryView;

    @FXML
    private TableView<SoldItem> ShoppingCartView;



    //constructor
    public HistoryController(SalesSystemDAO dao) {
        this.dao = dao;

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: implement
        showAll();
    }

    @FXML
    public void showAll(){
        HistoryView.setItems(FXCollections.observableList(dao.getHistoryList()));
        HistoryView.refresh();
        log.info("Shows All purchases in History tab");
    }

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

    @FXML
    public void showBetweenDates(){
        //TODO
        try{
            LocalDate start = startDate.getValue();
            LocalDate end = endDate.getValue();

        }catch (Exception e){

        }

    }

    @FXML
    public void showShoppingCartContents(){
    List<SoldItem> shoppingCart = HistoryView.getSelectionModel().getSelectedItem().getContents();
    //TODO Logger shows it gets the item but for some reason it does not display it on GUI
    ShoppingCartView.setItems(FXCollections.observableList(shoppingCart));
    log.info("contents of purchase " + HistoryView.getSelectionModel().getSelectedItem().getDate() +
            " " + HistoryView.getSelectionModel().getSelectedItem().getTime() + " shown");
    ShoppingCartView.refresh();
        }
    }

