package ee.ut.math.tvt.salessystem.ui;

import ee.ut.math.tvt.salessystem.dao.HibernateSalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import ee.ut.math.tvt.salessystem.logic.Warehouse;
import ee.ut.math.tvt.salessystem.ui.controllers.HistoryController;
import ee.ut.math.tvt.salessystem.ui.controllers.PurchaseController;
import ee.ut.math.tvt.salessystem.ui.controllers.StockController;
import ee.ut.math.tvt.salessystem.ui.controllers.TeamController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Graphical user interface of the sales system.
 */
public class SalesSystemUI extends Application {
    public static Alert alert = new Alert(Alert.AlertType.ERROR,"");

    private static final Logger log = LogManager.getLogger(SalesSystemUI.class);

    private final SalesSystemDAO dao;
    private final ShoppingCart shoppingCart;

    public SalesSystemUI() {
        //dao = new InMemorySalesSystemDAO();
        dao = new HibernateSalesSystemDAO();
        shoppingCart = new ShoppingCart(dao);
        alert.setResizable(false);
        alert.setTitle("Input Error");    //can be changed on callout
        alert.setOnCloseRequest(event -> {
            alert.setContentText("placeholder for new content");
            alert.setTitle("placeholder for new title");
            System.out.println(alert.getContentText());  //for testing
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        log.info("javafx version: " + System.getProperty("javafx.runtime.version"));

        //For adding stockitems to the combobox of the purchase tab
        Callback<ListView<StockItem>, ListCell<StockItem>> factory = lv -> new ListCell<StockItem>() {
            @Override
            protected void updateItem(StockItem item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getName());
            }
        };

        Tab purchaseTab = new Tab();
        purchaseTab.setText("Point-of-sale");
        purchaseTab.setClosable(false);
        PurchaseController pc = new PurchaseController(dao, shoppingCart, factory);
        purchaseTab.setContent(loadControls("PurchaseTab.fxml", pc));
        log.info("Purchase Tab is loaded");

        Tab stockTab = new Tab();
        stockTab.setText("Warehouse");
        stockTab.setClosable(false);
        stockTab.setContent(loadControls("StockTab.fxml", new StockController(dao, new Warehouse(dao), pc)));
        log.info("Stock Tab is loaded");

        Tab historyTab = new Tab();
        historyTab.setText("History");
        historyTab.setClosable(false);
        historyTab.setContent(loadControls("HistoryTab.fxml", new HistoryController(dao)));
        log.info("History Tab is loaded");

        Tab teamTab = new Tab();
        teamTab.setText("Team");
        teamTab.setClosable(false);
        teamTab.setContent(loadControls("TeamTab.fxml", new TeamController()));
        log.info("Team Tab is loaded");

        Pane root = new Pane();
        Scene scene = new Scene(root, 600, 500);
        scene.getStylesheets().add(getClass().getResource("DefaultTheme.css").toExternalForm());

        BorderPane borderPane = new BorderPane();
        borderPane.prefHeightProperty().bind(scene.heightProperty());
        borderPane.prefWidthProperty().bind(scene.widthProperty());
        borderPane.setCenter(new TabPane(purchaseTab, stockTab, historyTab, teamTab));
        root.getChildren().add(borderPane);

        primaryStage.setTitle("Sales system");
        primaryStage.setScene(scene);
        System.out.println("reset");
        primaryStage.setResizable(false);
        primaryStage.show();
        log.info("Salesystem GUI started");
    }

    private Node loadControls(String fxml, Initializable controller) throws IOException {
        URL resource = getClass().getResource(fxml);
        if (resource == null) {
            log.error(fxml + " not found");
            throw new IllegalArgumentException(fxml + " not found");
        }
        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        fxmlLoader.setController(controller);
        return fxmlLoader.load();
    }

}


