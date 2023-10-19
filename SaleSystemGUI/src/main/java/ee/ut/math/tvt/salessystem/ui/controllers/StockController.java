package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
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
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class StockController implements Initializable {

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
        removeButton.setVisible(false);
        removeButton.visibleProperty().bind(Bindings.isNotNull(warehouseTableView.getSelectionModel().selectedItemProperty()));
        addFocusListener(upperSplitPane);
        refreshStockItems();
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
    @FXML
    private void removeButtonClicked() {
        StockItem selectedItem = warehouseTableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            dao.findStockItems().remove(selectedItem);
            refreshStockItems();
        }
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