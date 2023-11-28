package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.Warehouse;
import ee.ut.math.tvt.salessystem.ui.SalesSystemUI;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class StockController implements Initializable {

    private static final Logger log = LogManager.getLogger(StockController.class);

    private final SalesSystemDAO dao;
    private final Warehouse warehouse;

    @FXML
    private Button addItem;
    @FXML
    private Button removeButton;
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
    @FXML
    private AnchorPane upperSplitPane;
    PurchaseController purchaseController;


    public StockController(SalesSystemDAO dao, Warehouse warehouse, PurchaseController purchaseController) {
        this.dao = dao;
        this.warehouse = warehouse;
        this.purchaseController = purchaseController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshStockItems();
        removeButton.disableProperty().bind(Bindings.isEmpty(warehouseTableView.getSelectionModel().getSelectedItems()));
        warehouseTableView.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                StockItem selectedItem = warehouseTableView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    fillInputsBySelectedStockItem(selectedItem);
                    barCodeField.setDisable(true);
                }
            }
        });
        barCodeField.focusedProperty().addListener((observable, oldPropertyValue, newPropertyValue) -> {
            if (!newPropertyValue) {
                StockItem stockItem = getStockItemByBarcode();
                fillInputsBySelectedStockItem(stockItem);
            }
        });
    }

    @FXML
    public void refreshButtonClicked() {
        warehouseTableView.getSelectionModel().clearSelection();
        refreshStockItems();
        barCodeField.setDisable(false);
        log.info("Stock items refreshed");
    }

    private void refreshStockItems() {
        List<StockItem> stockItems = dao.findStockItems();
        Iterator<StockItem> iterator = stockItems.iterator();
        while (iterator.hasNext()) {
            StockItem item = iterator.next();
            if (item.getQuantity() == 0) {
                warehouse.removeItem(item.getId());
                iterator.remove();
            }
        }
        warehouseTableView.setItems(FXCollections.observableList(stockItems));
        purchaseController.comboBox.getItems().clear();
        purchaseController.comboBox.setItems(FXCollections.observableList(stockItems));
        warehouseTableView.refresh();
    }


    @FXML
    protected void addProductButtonClicked() {
        try {
            barCodeField.setDisable(false);
            String name = nameField.getText();
            String desc = nameField.getText();
            double price = Double.parseDouble(priceField.getText());
            int quantity = Integer.parseInt(quantityField.getText());
            long id;
            if (Objects.equals(String.valueOf(barCodeField.getText()), "")) {
                id = generateUniqueId();
            } else id = Long.parseLong(barCodeField.getText());
            if (price < 0) SalesSystemUI.showAlert("Invalid price", "Price must be non-negative");
            else if (quantity < 0) SalesSystemUI.showAlert("Invalid quantity", "Quantity must be non-negative");
            else if (id < 0) SalesSystemUI.showAlert("Invalid id", "Id must be non-negative");
            else {
                warehouse.addItem(id, name, desc, price, quantity);
                refreshStockItems();
                resetProductField();
            }
        } catch (NumberFormatException e) {
            if (Objects.equals(e.getMessage(), "empty String")) {
                log.error("Product fields are empty: " + e.getMessage());
                SalesSystemUI.showAlert("Empty product fields", "Please fill in all product fields");
            }else{
            log.error("Failed to add product: " + e.getMessage());
            SalesSystemUI.showAlert("Invalid input", "Please enter numeric values for price, quantity, and id");
        }}
    }

    @FXML
    private void removeButtonClicked() {
        StockItem selectedItem = barCodeField.getText().isEmpty() ? warehouseTableView.getSelectionModel().getSelectedItem() : getStockItemByBarcode();
        assert selectedItem != null;
        warehouse.removeItem(selectedItem.getId());
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

    private void fillInputsBySelectedStockItem(StockItem stockItem) {
        if (stockItem != null) {
            barCodeField.setText(String.valueOf(stockItem.getId()));
            priceField.setText(String.valueOf(stockItem.getPrice()));
            nameField.setText(stockItem.getName());
            quantityField.setText(String.valueOf(stockItem.getQuantity()));
        }
    }
    private StockItem getStockItemByBarcode() {
        try {
            long code = Long.parseLong(barCodeField.getText());
            return dao.findStockItem(code);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}