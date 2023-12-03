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
        // When something is selected in the table, enable the remove button
        removeButton.disableProperty().bind(Bindings.isEmpty(warehouseTableView.getSelectionModel().getSelectedItems()));

        // When something is selected in the table, fill the input fields
        warehouseTableView.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                StockItem selectedItem = warehouseTableView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    fillInputsBySelectedStockItem(selectedItem);
                    barCodeField.setDisable(true);
                }
            }
        });
        // When something is typed into the barcode field, fill the input fields with the corresponding stock item
        barCodeField.focusedProperty().addListener((observable, oldPropertyValue, newPropertyValue) -> {
            if (!newPropertyValue) {
                StockItem stockItem = getStockItemByBarcode();
                fillInputsBySelectedStockItem(stockItem);
            }
        });
    }

    /**
     * Event handler for the refresh button.
     */
    @FXML
    public void refreshButtonClicked() {
        warehouseTableView.getSelectionModel().clearSelection();
        refreshStockItems();
        resetProductField();
        barCodeField.setDisable(false);
        log.info("Stock items refreshed");
    }

    /**
     * Refreshes the stock item table with data from the dao.
     */
    private void refreshStockItems() {
        List<StockItem> stockItems = dao.findStockItems();
        Iterator<StockItem> iterator = stockItems.iterator();
        while (iterator.hasNext()) {
            // Removes items with quantity 0 from the table
            StockItem item = iterator.next();
            if (item.getQuantity() == 0) {
                warehouse.removeItem(item.getId());
                iterator.remove();
            }
        }
        // Refreshes the combobox in the purchase tab
        purchaseController.comboBox.setItems(FXCollections.observableList(dao.findStockItems()));

        // Refreshes the table
        warehouseTableView.setItems(FXCollections.observableList(stockItems));
        warehouseTableView.refresh();
    }


    /**
     * Event handler for the add product button.
     */
    @FXML
    protected void addProductButtonClicked() {
        try {
            barCodeField.setDisable(false);
            String name = nameField.getText();
            String desc = nameField.getText();
            double price = Double.parseDouble(priceField.getText());
            int quantity = Integer.parseInt(quantityField.getText());
            long id;

            // If the barcode field is empty, generate a unique id
            if (Objects.equals(String.valueOf(barCodeField.getText()), "")) {
                id = generateUniqueId();
            }
            // If the barcode field is not empty, use the value from the field, if a product with that id doesn't exist
            else id = Long.parseLong(barCodeField.getText());

            // delegates the task of adding a product to the warehouse class
            warehouse.addItem(id, name, desc, price, quantity);
            refreshStockItems();
            resetProductField();

        } catch (NumberFormatException e) {
            //if any of the fields are empty, show an alert
            if (Objects.equals(e.getMessage(), "empty String")) {
                log.error("Product fields are empty: " + e.getMessage());
                SalesSystemUI.showAlert("Empty product fields", "Please fill in all product fields");
            }else {
                //if any of the fields have invalid values (e.g. letters in a numeric field), show an alert
                log.error("Failed to add product: " + e.getMessage());
                SalesSystemUI.showAlert("Invalid input", "Please enter numeric values for price, quantity, and id");
            }
        } catch (IllegalArgumentException e) {

            //if any of the fields have invalid values(e.g negative price), show an alert
            log.error("Failed to add product: " + e.getMessage());
            SalesSystemUI.showAlert("Invalid input", e.getMessage());
        }
    }

    /**
     * Event handler for the remove product button.
     */
    @FXML
    private void removeButtonClicked() {
        barCodeField.setDisable(false);
        StockItem selectedItem = barCodeField.getText().isEmpty() ? warehouseTableView.getSelectionModel().getSelectedItem() : getStockItemByBarcode();
        assert selectedItem != null;
        warehouse.removeItem(selectedItem.getId());
        refreshStockItems();
        resetProductField();
    }

    /**
     * Resets the product input fields.
     */
    private void resetProductField() {
        // Clears all the input fields
        barCodeField.setText("");
        quantityField.setText("");
        nameField.setText("");
        priceField.setText("");
    }

    /**
     * Generates a unique id for a new product.
     * @return a unique id
     */
    private long generateUniqueId() {
        // Finds the highest id in the warehouse and assigns a new id that is 1 higher
        return dao.findStockItems().stream().mapToLong(StockItem::getId).max().orElse(0) + 1;
    }

    /**
     * Fills the input fields with the values of the selected stock item.
     * @param stockItem the selected stock item
     */
    private void fillInputsBySelectedStockItem(StockItem stockItem) {
        if (stockItem != null) {
            barCodeField.setText(String.valueOf(stockItem.getId()));
            priceField.setText(String.valueOf(stockItem.getPrice()));
            nameField.setText(stockItem.getName());
            quantityField.setText(String.valueOf(stockItem.getQuantity()));
        }
    }

    /**
     * Search the warehouse for a StockItem with the entered bar code.
     * @return the StockItem that matches the barcode, or null if no such item exists
     */
    private StockItem getStockItemByBarcode() {
        try {
            long code = Long.parseLong(barCodeField.getText());
            return dao.findStockItem(code);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}