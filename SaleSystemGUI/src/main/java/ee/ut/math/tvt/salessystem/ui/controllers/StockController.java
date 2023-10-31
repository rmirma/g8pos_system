package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.ui.SalesSystemUI;
import javafx.beans.binding.Bindings;
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

    private static final Logger log = LogManager.getLogger(SalesSystemUI.class);

    private final SalesSystemDAO dao;

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


    public StockController(SalesSystemDAO dao) {
        this.dao = dao;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshStockItems();
        removeButton.setVisible(false);
        removeButton.visibleProperty().bind(Bindings.isNotNull(warehouseTableView.getSelectionModel().selectedItemProperty()));
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
    public void addItemToStock () {
        dao.beginTransaction();
        try {
            Long id;
            if ((Objects.equals(barCodeField.getText(), "")) || isBarcodeInUse(Long.parseLong(barCodeField.getText()))) {
                id = generateUniqueId();
            } else id = Long.parseLong(barCodeField.getText());

            String name = nameField.getText();
            String desc = nameField.getText();
            double price = Double.parseDouble(priceField.getText());
            int quantity = Integer.parseInt(quantityField.getText());

            dao.saveStockItem(new StockItem(id, name, desc, price, quantity));
            log.info("Product '" + name + "' added to stock");
            dao.commitTransaction();
        } catch (Exception e) {
            dao.rollbackTransaction();
            throw new RuntimeException(e);

        }
    }

    @FXML
    protected void addProductButtonClicked(){
        StockItem stockItem = getStockItemByBarcode();
        if (stockItem != null){
            updateProduct();
        } else addItemToStock();
        refreshStockItems();
        resetProductField();
    }
    @FXML
    private void removeButtonClicked() {
        dao.beginTransaction();
        StockItem selectedItem = warehouseTableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            dao.findStockItems().remove(selectedItem);
            refreshStockItems();
            log.info("product '"+selectedItem.getName()+ "' removed");
            dao.commitTransaction();
        }else dao.rollbackTransaction();
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
    private void updateProduct(){
        dao.beginTransaction();
        StockItem stockItem = getStockItemByBarcode();
        if (stockItem != null){
            stockItem.setName(nameField.getText());
            stockItem.setPrice(Double.parseDouble(priceField.getText()));
            stockItem.setQuantity(Integer.parseInt(quantityField.getText()));
            log.info("product '"+stockItem.getName()+ "' updated");
            dao.commitTransaction();
        }else dao.rollbackTransaction();
    }
}