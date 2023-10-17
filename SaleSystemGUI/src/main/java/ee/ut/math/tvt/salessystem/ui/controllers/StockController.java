package ee.ut.math.tvt.salessystem.ui.controllers;

import com.sun.javafx.collections.ObservableListWrapper;
import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class StockController implements Initializable {

    private final SalesSystemDAO dao;

    @FXML
    private Button addItem;
    @FXML
    private TextField barCodeField;
    @FXML
    private TextField quantityField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField priceField;
    @FXML
    private TableView<StockItem> warehouseTableView;

    public StockController(SalesSystemDAO dao) {
        this.dao = dao;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshStockItems();
        // TODO refresh view after adding new items
    }

    @FXML
    public void refreshButtonClicked() {
        refreshStockItems();
    }

    private void refreshStockItems() {
        warehouseTableView.setItems(FXCollections.observableList(dao.findStockItems()));
        warehouseTableView.refresh();
    }
    private void addItemToStock () {
        Long id;
        if ((Objects.equals(barCodeField.getText(), "")) || isBarcodeInUse(Long.parseLong(barCodeField.getText()))) {
            id = generateUniqueId();
        } else id = Long.parseLong(barCodeField.getText());

        String name = nameField.getText();
        String desc = nameField.getText();
        double price = Double.parseDouble(priceField.getText());
        int quantity = Integer.parseInt(quantityField.getText());
        dao.saveStockItem(new StockItem(id, name, desc, price, quantity));
    }


    @FXML
    protected void addProductButtonClicked(){
        addItemToStock();
        refreshStockItems();
        resetProductField();
    }
    private void resetProductField() {
        barCodeField.setText("");
        quantityField.setText("");
        nameField.setText("");
        priceField.setText("");
    }

    private long generateUniqueId() {
        return dao.findStockItems().stream().mapToLong(StockItem::getId).max().orElse(0) + 1;
    }

    private boolean isBarcodeInUse(Long barcode) {
        return dao.findStockItems().stream().anyMatch(item -> item.getId().equals(barcode));
    }
}