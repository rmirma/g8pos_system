package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import ee.ut.math.tvt.salessystem.ui.SalesSystemUI;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Encapsulates everything that has to do with the purchase tab (the tab
 * labelled "Point-of-sale" in the menu). Consists of the purchase menu,
 * current purchase dialog and shopping cart table.
 */
public class PurchaseController implements Initializable {

    private static final Logger log = LogManager.getLogger(PurchaseController.class);
    private final SalesSystemDAO dao;
    private final ShoppingCart shoppingCart;

    @FXML
    private Button newPurchase;
    @FXML
    private Button submitPurchase;
    @FXML
    private Button cancelPurchase;
    @FXML
    private TextField barCodeField;
    @FXML
    private TextField quantityField;
    @FXML
    private Label priceLabel;
    @FXML
    private ComboBox<String> nameSelector;
    @FXML
    private TextField priceField;
    @FXML
    private Button addItemButton;
    @FXML
    private Button removeItemButton;
    @FXML
    private TableView<SoldItem> purchaseTableView;
    public PurchaseController(SalesSystemDAO dao, ShoppingCart shoppingCart) {
        this.dao = dao;
        this.shoppingCart = shoppingCart;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Add items to combobox
        dao.findStockItems().stream().map(StockItem::getName).forEach(name -> nameSelector.getItems().add(name));
        removeItemButton.setVisible(false);
        removeItemButton.visibleProperty().bind(Bindings.isNotNull(purchaseTableView.getSelectionModel().selectedItemProperty()));
        cancelPurchase.setDisable(true);
        submitPurchase.setDisable(true);
        //purchaseTableView.setItems(FXCollections.observableList(shoppingCart.getAll()));
        disableProductField(true);
        this.priceField.setDisable(true);
        this.barCodeField.setDisable(true);

        this.barCodeField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (!newPropertyValue) {
                    fillInputsBySelectedStockItem();
                }
            }
        });
    }

    /** Event handler for the <code>new purchase</code> event. */
    @FXML
    protected void newPurchaseButtonClicked() {
        log.info("New sale process started");
        try {
            enableInputs();
        } catch (SalesSystemException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Event handler for the <code>cancel purchase</code> event.
     */
    @FXML
    protected void cancelPurchaseButtonClicked() {
        log.info("Sale cancelled");
        try {
            purchaseTableView.getItems().clear();
            shoppingCart.cancelCurrentPurchase();
            disableInputs();
        } catch (SalesSystemException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Event handler for the <code>submit purchase</code> event.
     */
    @FXML
    protected void submitPurchaseButtonClicked() {
        log.info("Sale complete");
        try {
            log.debug("Contents of the current basket:\n" + shoppingCart.getAll());
            purchaseTableView.getItems().clear();
            shoppingCart.submitCurrentPurchase();
            disableInputs();
        } catch (SalesSystemException e) {
            log.error(e.getMessage(), e);
        }
    }

    // switch UI to the state that allows to proceed with the purchase
    private void enableInputs() {
        resetProductField();
        disableProductField(false);
        cancelPurchase.setDisable(false);
        submitPurchase.setDisable(false);
        newPurchase.setDisable(true);
    }

    // switch UI to the state that allows to initiate new purchase
    private void disableInputs() {
        resetProductField();
        priceLabel.setText("0");
        quantityField.setText("");
        nameSelector.getSelectionModel().clearSelection();
        cancelPurchase.setDisable(true);
        submitPurchase.setDisable(true);
        newPurchase.setDisable(false);
        disableProductField(true);
    }

    private void clearInputs(){
        quantityField.setText("");
        priceField.setText("");
        barCodeField.setText("");
    }

    private void fillInputsBySelectedStockItem() {
        Optional<StockItem> stockItem = Optional.ofNullable(getStockItemByBarcode());
        stockItem.ifPresentOrElse(
                (item) -> {priceField.setText(String.valueOf(item.getPrice()));},
                () -> {resetProductField();}
        );
    }

    // Search the warehouse for a StockItem with the bar code entered
    // to the barCode textfield.
    private StockItem getStockItemByBarcode() {
        try {
            long code = Long.parseLong(barCodeField.getText());
            return dao.findStockItem(code);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Add new item to the cart.
     */
    @FXML
    public void addItemEventHandler() {
        // add chosen item to the shopping cart.
        StockItem stockItem = getStockItemByBarcode();
        if (stockItem != null) {
            int quantity;
            try {
                quantity = Integer.parseInt(quantityField.getText());
                if (stockItem.getQuantity() >= quantity){
                    SoldItem item = new SoldItem(stockItem, quantity);
                    if (!shoppingCart.contains(item))
                        purchaseTableView.getItems().add(item);
                    shoppingCart.addItem(item);

                    priceLabel.setText(String.valueOf(shoppingCart.getTotalPrice()));
                    purchaseTableView.refresh();
                }else throw new SalesSystemException();

            } catch (SalesSystemException e) {
                log.error(e);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("Not enough items in stock");
                alert.setContentText("We have only " + stockItem.getQuantity() + " " + stockItem.getName() + " in stock");
                alert.showAndWait();
            } catch (NumberFormatException e) {
                log.error(e);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("Invalid amount of item");
                alert.setContentText("Check the Amount field on the purchase");
                alert.showAndWait();
            }
        }else {
            log.error("Purchasable product was not selected");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Purchasable product was not selected");
            alert.setContentText("Check the Name field on the purchase");
            alert.showAndWait();
        }
    }

    @FXML
    public void removeItemEventHandler() {
        SoldItem selectedItem = purchaseTableView.getSelectionModel().getSelectedItem();
        shoppingCart.removeItem(selectedItem);
        purchaseTableView.getItems().remove(selectedItem);
        priceLabel.setText(String.valueOf(shoppingCart.getTotalPrice()));
    }

    /**
     * Display items in drop-down menu
     */
    @FXML
    public void selectItemEventHandler(){
        String productName = nameSelector.getValue();
        if(productName==null){
            clearInputs();
            return;
        }
        List<StockItem> stockItem = dao.findStockItem(productName);
        long id = stockItem.get(0).getId();
        double price = stockItem.get(0).getPrice();
        barCodeField.setText(Long.toString(id));
        priceField.setText(Double.toString(price));
    }

    /**
     * Sets whether or not the product component is enabled.
     */
    private void disableProductField(boolean disable) {
        this.addItemButton.setDisable(disable);
        this.quantityField.setDisable(disable);
        this.nameSelector.setDisable(disable);
    }

    /**
     * Reset dialog fields.
     */
    private void resetProductField() {
        barCodeField.setText("");
        quantityField.setText("1");
        priceField.setText("");
    }
}
