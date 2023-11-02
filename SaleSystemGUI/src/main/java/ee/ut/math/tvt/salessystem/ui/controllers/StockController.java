package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.Warehouse;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
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


    public StockController(SalesSystemDAO dao, Warehouse warehouse) {
        this.dao = dao;
        this.warehouse = warehouse;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshStockItems();
        removeButton.setDisable(true);
        BooleanBinding disableRemoveButton = Bindings.isNull(warehouseTableView.getSelectionModel().selectedItemProperty());
        removeButton.disableProperty().bind(disableRemoveButton);
        addFocusListener(upperSplitPane);

        barCodeField.focusedProperty().addListener((observable, oldPropertyValue, newPropertyValue) -> {
            if (!newPropertyValue) {
                fillInputsBySelectedStockItem();
            }
        });

    }
    private void addFocusListener(Node node) {
        node.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { // If focus has moved to upperSplitPane or any of its children, clear the selection
                warehouseTableView.getSelectionModel().clearSelection();
                refreshStockItems();
            }
        });

        if (node instanceof Parent) {
            ((Parent) node).getChildrenUnmodifiable().forEach(this::addFocusListener);
        }
    }


    @FXML
    public void refreshButtonClicked() {
        refreshStockItems();
        warehouseTableView.getSelectionModel().clearSelection();
        log.info("Stock items refreshed");
    }

    private void refreshStockItems() {
        warehouseTableView.setItems(FXCollections.observableList(dao.findStockItems()));
        warehouseTableView.refresh();
    }

    @FXML
    protected void addProductButtonClicked() {
        try {
            String name = nameField.getText();
            String desc = nameField.getText();
            double price = Double.parseDouble(priceField.getText());
            int quantity = Integer.parseInt(quantityField.getText());
            long id;
            if (Objects.equals(String.valueOf(barCodeField.getText()), "")) {
                id = generateUniqueId();
            } else id = Long.parseLong(barCodeField.getText());
            warehouse.addItem(id, name, desc, price, quantity);
            refreshStockItems();
            resetProductField();
        } catch (NumberFormatException e) {
            log.error("Failed to add product: " + e.getMessage());
        }
    }

    @FXML
    private void removeButtonClicked() {
        StockItem selectedItem = warehouseTableView.getSelectionModel().getSelectedItem();
        warehouse.removeItem(selectedItem.getId());
        refreshStockItems();
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

    private void fillInputsBySelectedStockItem() {
        StockItem stockItem = getStockItemByBarcode();
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