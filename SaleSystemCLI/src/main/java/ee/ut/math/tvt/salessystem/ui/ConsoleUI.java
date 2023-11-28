package ee.ut.math.tvt.salessystem.ui;

import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.HibernateSalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import ee.ut.math.tvt.salessystem.logic.Warehouse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

/**
 * A simple CLI (limited functionality).
 */
public class ConsoleUI {
    private static final Logger log = LogManager.getLogger(ConsoleUI.class);

    private final SalesSystemDAO dao;
    private final ShoppingCart cart;
    private final HistoryControllerCLI historyControllerCLI;
    private final Warehouse warehouse;

    public ConsoleUI(SalesSystemDAO dao) {
        this.dao = dao;
        cart = new ShoppingCart(dao);
        this.historyControllerCLI = new HistoryControllerCLI(this.dao);
        this.warehouse = new Warehouse(this.dao);
    }

    public static void main(String[] args) throws Exception {
        //SalesSystemDAO dao = new InMemorySalesSystemDAO();
        SalesSystemDAO dao = new HibernateSalesSystemDAO();
        ConsoleUI console = new ConsoleUI(dao);
        console.run();
    }

    /**
     * Run the sales system CLI.
     */
    public void run() throws IOException {
        log.info("Sales system CLI started.");
        System.out.println("===========================");
        System.out.println("=       Sales System      =");
        System.out.println("===========================");
        printUsage();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            waitForCommand(in);
        }
    }

    private void waitForCommand(BufferedReader in) throws IOException {
        System.out.print("> ");
        processCommand(in.readLine().trim().toLowerCase());
        System.out.println("Done. ");
    }

    private void showStock() {
        log.info("Showing stock");
        List<StockItem> stockItems = dao.findStockItems();
        System.out.println("-------------------------");
        for (StockItem si : stockItems) {
            if (si.getQuantity() <= 0) removeProductFromWarehouse(String.valueOf(si.getId()));
            else System.out.println(si.getId() + " " + si.getName() + " " + si.getPrice() + " Euro (" + si.getQuantity() + " items)");
        }
        if (stockItems.isEmpty()) {
            System.out.println("\tNothing");
        }
        System.out.println("-------------------------");
    }

    private void showCart() {
        log.info("Showing cart");
        System.out.println("-------------------------");
        for (SoldItem si : cart.getAll()) {
            System.out.println(si.getName() + " " + si.getPrice() + " Euro (" + si.getQuantity() + " items)");
            System.out.println("Sum: " + si.getSum() + " Euro");
        }
        if (cart.getAll().isEmpty()) {
            System.out.println("\tNothing");
        }
        System.out.println("-------------------------");
    }

    private void printUsage() {
        System.out.println("-------------------------");
        System.out.println("Usage:");
        System.out.println("h\t\t\tShow this help");
        System.out.println("w\t\t\tShow warehouse contents");
        System.out.println("n [name] [price] [amount]\tAdd new product to warehouse");
        System.out.println("rm IDX\t\tRemove product with index IDX from warehouse");
        System.out.println("c\t\t\tShow cart contents");
        System.out.println("a IDX NR \tAdd NR of stock item with index IDX to the cart");
        System.out.println("crm IDX NR \tRemove NR of item with index IDX from the cart");
        System.out.println("p\t\t\tPurchase the shopping cart");
        System.out.println("r\t\t\tReset the shopping cart");
        System.out.println("history\t\tcheck out history");
        System.out.println("t\t\t\tShow team info");
        System.out.println("-------------------------");
    }

    private void processCommand(String command) throws IOException {
        String[] c = command.split(" ");

        if (c[0].equals("h"))
            printUsage();
        else if (c[0].equals("q"))
            System.exit(0);
        else if (c[0].equals("w"))
            showStock();
        else if (c[0].equals("c"))
            showCart();
        else if (c[0].equals("history"))
            history();
        else if (c[0].equals("p"))
            cart.submitCurrentPurchase();
        else if (c[0].equals("r"))
            cart.cancelCurrentPurchase();
        else if (c[0].equals("t"))
            showTeam();
        else if (c[0].equals("rm")&& c.length == 2)
            removeProductFromWarehouse(c[1]);
        else if (c[0].equals("n") && c.length == 4)
            addProductToWarehouse(c[1], c[2], c[3]);
        else if (c[0].equals("a") && c.length == 3)
            addItemToCart(c);
        else if (c[0].equals("crm") && c.length == 3){
            removeItemFromCart(c);
        } else {
            System.out.println("unknown command");
        }
    }

    private void addItemToCart(String[] c) {
        try {
            long idx = Long.parseLong(c[1]);
            int amount = Integer.parseInt(c[2]);
            StockItem item = dao.findStockItem(idx);
            if (item != null) {
                cart.addItem(new SoldItem(item, Math.min(amount, item.getQuantity())));
                System.out.println("Item successfully added to the cart.");
                System.out.println("New total price is: " + cart.getTotalPrice());
            } else {
                System.out.println("no stock item with id " + idx + " found.");
            }
        } catch (SalesSystemException | NoSuchElementException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void removeItemFromCart(String[] c) {
        long idx = Long.parseLong(c[1]);
        int amount = Integer.parseInt(c[2]);
        StockItem item = dao.findStockItem(idx);

        try {
            if (item != null){
            cart.removeStockItem(item, amount);
            warehouse.addItem(idx, item.getName(), item.getDescription(), item.getPrice(), amount);
            System.out.println("Item successfully removed from the cart.");
            System.out.println("New total price is: " + cart.getTotalPrice());
            } else {
                System.out.println("no item in cart with id " + idx + " found.");
            }
        } catch (SalesSystemException | NoSuchElementException | IllegalArgumentException e) {
            log.error(e.getMessage());
        }

    }

    private void showTeam() throws IOException {
        log.info("Showing team info");
        System.out.println("===========================");
        System.out.println("=        Team info        =");
        System.out.println("===========================");
        String filePath = "application.properties";

        Properties pros = new Properties();
        try (InputStream ip = getClass().getClassLoader().getResourceAsStream(filePath)) {
            pros.load(ip);
            System.out.println("TEAM NAME: " + pros.get("teamName"));
            System.out.println("TEAM LEADER: " + pros.get("teamLeader"));
            System.out.println("TEAM MEMBERS: ");
            System.out.println(pros.get("teamMember1"));
            System.out.println(pros.get("teamMember2"));
            System.out.println(pros.get("teamMember3"));
            System.out.println(pros.get("teamMember4"));
            System.out.println("----------------------------------");
            log.debug("Team info successfully loaded");
            waitForCommand(new BufferedReader(new InputStreamReader(System.in)));
        } catch (IOException e) {
            System.out.println("Did not find the data. Check if the file exists and is reachable. (src/main/resources/application.properties)");
            waitForCommand(new BufferedReader(new InputStreamReader(System.in)));
        }
    }

    private void addProductToWarehouse(String name, String price, String amount) {
        try {
            long id = dao.findStockItems().stream().mapToLong(StockItem::getId).max().orElse(0) + 1;
            if (Double.parseDouble(price) < 0 || Integer.parseInt(amount) < 0) {
                throw new NumberFormatException("Price and amount must be positive");
            } else {
                warehouse.addItem(id, name, name, Double.parseDouble(price), Integer.parseInt(amount));
                System.out.println("Item added to stock:");
                System.out.println("-------------------------");
                System.out.println(name + " " + price + " Euro (" + amount + " items)");
                System.out.println("-------------------------");
            }
        } catch(NumberFormatException e){
            log.error(e.getMessage());
            System.out.println("Could not add item: " + e.getMessage());
        }

    }
    private void removeProductFromWarehouse(String id) {
        try {
            long barCode = Long.parseLong(id);
            if (dao.findStockItem(barCode) != null) {
                warehouse.removeItem(barCode);
            } else System.out.println("Could not find an item with id " + id);
        }catch (NumberFormatException e) {
            log.error(e.getMessage());
            System.out.println(id +" is not a valid id: " + e.getMessage());
        }
    }

    private void history() throws IOException {

        System.out.println("----------History---------");
        HistoryControllerCLI.usage();
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        boolean historyUp = true;
        while (historyUp) {
            System.out.print("> ");
            historyUp = HistoryControllerCLI.proccessHistoryCommand(input.readLine().trim(),historyUp);
            System.out.println("---------------------");
        }

    }
}