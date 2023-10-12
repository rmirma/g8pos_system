package ee.ut.math.tvt.salessystem.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Encapsulates everything that has to do with the history tab (the tab
 * labelled "History" in the menu).
 *
 */


public class HistoryController implements Initializable {
    private final SalesSystemDAO dao;


    //constructor
    public HistoryController(SalesSystemDAO dao) {
        this.dao = dao;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: implement
    }


    @FXML
    public void showShoppingCartContents(){

    }

}
