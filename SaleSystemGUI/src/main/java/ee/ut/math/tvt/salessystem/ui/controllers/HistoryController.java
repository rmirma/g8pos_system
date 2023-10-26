package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.dataobjects.HistoryItem;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.ui.SalesSystemUI;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.swing.text.TabableView;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Encapsulates everything that has to do with the history tab (the tab
 * labelled "History" in the menu).
 *
 */


public class HistoryController implements Initializable {
    private SalesSystemDAO dao;
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
    }


    @FXML
    public void showShoppingCartContents(){
    List<SoldItem> shoppingCart = HistoryView.getSelectionModel().getSelectedItem().getContents();
    ShoppingCartView.setItems(FXCollections.observableList(shoppingCart));
    ShoppingCartView.refresh();
        }
    }

