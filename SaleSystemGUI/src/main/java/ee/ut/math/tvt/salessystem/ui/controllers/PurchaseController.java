package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import ee.ut.math.tvt.salessystem.ui.SalesSystemUI;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
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
    Callback<ListView<StockItem>, ListCell<StockItem>> factory;
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
    ComboBox<StockItem> comboBox;
    @FXML
    private TextField priceField;
    @FXML
    private Button addItemButton;
    @FXML
    private Button removeItemButton;
    @FXML
    private TableView<SoldItem> purchaseTableView;
    public PurchaseController(SalesSystemDAO dao, ShoppingCart shoppingCart, Callback<ListView<StockItem>, ListCell<StockItem>> factory) {
        this.dao = dao;
        this.shoppingCart = shoppingCart;
        this.factory = factory;

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // configure combobox to work with stockitems
        comboBox.setCellFactory(factory);
        comboBox.setButtonCell(factory.call(null));
        disableInputs();
        removeItemButton.disableProperty().bind(Bindings.isEmpty(purchaseTableView.getSelectionModel().getSelectedItems()));
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
            priceLabel.setText("0");
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
            newPurchase.setDisable(false);
            priceLabel.setText("0");
        } catch (SalesSystemException e) {
            log.error(e.getMessage(), e);
        }
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
     * Auto fills the input forms when selecting an item from the shopping cart
     */
    @FXML
    public void selectItemFromTable(){
        SoldItem soldItem = purchaseTableView.getSelectionModel().getSelectedItem();
        if(soldItem == null) // for if we sort the table
            return;
        barCodeField.setText(Long.toString(soldItem.getId()));
        priceField.setText(Double.toString(soldItem.getPrice()));
        quantityField.setText(Integer.toString(soldItem.getQuantity()));
        comboBox.setValue(soldItem.getStockItem());
    }

    /**
     * Gets the quantity typed in the respective field and
     * shows an error if the input is invalid(isn't a number, is less than 1)
     * @return the number in the form, if there's an error -1
     */
    private int getQuantityFromField(){
        try {
            int quantity = Integer.parseInt(quantityField.getText());
            if (quantity < 1) throw new SalesSystemException("Quantity cannot be less than 1");
            return quantity;
        }
        catch (NumberFormatException e) {
            SalesSystemUI.showAlert("Incorrect input in quantity field", "Quantity must be a number");
        } catch(SalesSystemException e){
            SalesSystemUI.showAlert("Incorrect input in quantity field", e.getMessage());
        }
        return -1;
    }

    /**
     * When adding an item to the shopping cart, this method checks if
     * there is enough of that item in stock
     * @param soldItem item to add
     * @param stockItem existing item
     * @return false if exceeds, true otherwise
     */
    private boolean quantityExceedsStock(SoldItem soldItem, StockItem stockItem){
        int existingQuantity = 0;
        int quantityToAdd = soldItem.getQuantity();
        Optional<SoldItem> existingItem = shoppingCart.contains(soldItem);

        if(existingItem.isPresent())
            existingQuantity = existingItem.get().getQuantity();

        if(quantityToAdd+existingQuantity > stockItem.getQuantity()){
            SalesSystemUI.showAlert("Purchase quantity exceeds warehouse quantity",
                    "Quantity to purchase: " + (quantityToAdd+existingQuantity)
                    + "\n    Quantity in shopping cart: "
                    + existingQuantity+"\n    Quantity to add: " + quantityToAdd
                    + "\nQuantity in warehouse:" + stockItem.getQuantity());
            return true;
        }
        return false;
    }

    /**
     * Add new item to the cart.
     */
    @FXML
    public void addItemEventHandler() {
        StockItem stockItem = getStockItemByBarcode();
        if (stockItem == null)
            return;

        int quantity = getQuantityFromField();
        if (quantity  == -1)
            return;

        SoldItem item = new SoldItem(stockItem, quantity);
        System.out.println(stockItem.getQuantity());
        if (quantityExceedsStock(item, stockItem))
            return;

        if(shoppingCart.contains(item).isEmpty())
            purchaseTableView.getItems().add(item);
        shoppingCart.addItem(item);

        priceLabel.setText(String.valueOf(shoppingCart.getTotalPrice()));
        purchaseTableView.refresh();

        log.info("Added item to shopping cart");
    }

    /**
     * Select and remove item from table view
     */
    @FXML
    public void removeItemEventHandler() {
        SoldItem selectedItem = purchaseTableView.getSelectionModel().getSelectedItem();
        shoppingCart.removeItem(selectedItem);
        purchaseTableView.getItems().remove(selectedItem);
        priceLabel.setText(String.valueOf(shoppingCart.getTotalPrice()));
        log.info("Removed item from shopping cart");
    }

    /**
     * Display items in drop-down menu
     */
    @FXML
    public void selectItemComboboxEventHandler(){
        StockItem product = comboBox.getValue();
        if(product==null)
            return;
        barCodeField.setText(Long.toString(product.getId()));
        priceField.setText(Double.toString(product.getPrice()));
    }

    /**
     * Disables the product component
     */
    private void disableProductField() {
        cancelPurchase.setDisable(true);
        submitPurchase.setDisable(true);
        addItemButton.setDisable(true);
        quantityField.setDisable(true);
        comboBox.setDisable(true);
    }

    /**
     * Enables the product component
     */
    private void enableProductField() {
        addItemButton.setDisable(false);
        quantityField.setDisable(false);
        comboBox.setDisable(false);
    }

    /**
     * Reset dialog fields.
     */
    private void resetProductField() {
        barCodeField.setText("");
        quantityField.setText("1");
        priceField.setText("");
        comboBox.getSelectionModel().clearSelection();
    }

    // switch UI to the state that allows to proceed with the purchase
    private void enableInputs() {
        resetProductField();
        enableProductField();
        cancelPurchase.setDisable(false);
        submitPurchase.setDisable(false);
        newPurchase.setDisable(true);
    }

    // switch UI to the state that allows to initiate new purchase
    private void disableInputs() {
        resetProductField();
        disableProductField();
        newPurchase.setDisable(false);
    }
}
