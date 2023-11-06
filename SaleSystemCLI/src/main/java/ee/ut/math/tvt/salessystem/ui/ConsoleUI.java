package ee.ut.math.tvt.salessystem.ui;

import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Scanner;

/**
 * A simple CLI (limited functionality).
 */
public class ConsoleUI {
    private static final Logger log = LogManager.getLogger(ConsoleUI.class);

    private final SalesSystemDAO dao;
    private final ShoppingCart cart;
    private final HistoryControllerCLI historyControllerCLI;

    public ConsoleUI(SalesSystemDAO dao) {
        this.dao = dao;
        cart = new ShoppingCart(dao);
        this.historyControllerCLI = new HistoryControllerCLI(this.dao);
    }

    public static void main(String[] args) throws Exception {
        SalesSystemDAO dao = new InMemorySalesSystemDAO();
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
            System.out.print("> ");
            processCommand(in.readLine().trim().toLowerCase());
            System.out.println("Done. ");
        }
    }

    private void showStock() {
        log.info("Showing stock");
        List<StockItem> stockItems = dao.findStockItems();
        System.out.println("-------------------------");
        for (StockItem si : stockItems) {
            System.out.println(si.getId() + " " + si.getName() + " " + si.getPrice() + "Euro (" + si.getQuantity() + " items)");
        }
        if (stockItems.size() == 0) {
            System.out.println("\tNothing");
        }
        System.out.println("-------------------------");
    }

    private void showCart() {
        log.info("Showing cart");
        System.out.println("-------------------------");
        for (SoldItem si : cart.getAll()) {
            System.out.println(si.getName() + " " + si.getPrice() + "Euro (" + si.getQuantity() + " items)");
        }
        if (cart.getAll().size() == 0) {
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
        System.out.println("p\t\t\tPurchase the shopping cart");
        System.out.println("r\t\t\tReset the shopping cart");
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
        else if (c[0].equals("history"))  //tmp
            history();
        else if (c[0].equals("p"))
            cart.submitCurrentPurchase();
        else if (c[0].equals("r"))
            cart.cancelCurrentPurchase();
        else if (c[0].equals("t"))
            showTeam();
        else if (c[0].equals("rm")&& c.length == 2)
            removeProduct(c[1]);
        else if (c[0].equals("n") && c.length == 4)
            addProductToWarehouse(c[1], c[2], c[3]);
        else if (c[0].equals("a") && c.length == 3) {
            try {
                long idx = Long.parseLong(c[1]);
                int amount = Integer.parseInt(c[2]);
                StockItem item = dao.findStockItem(idx);
                if (item != null) {
                    cart.addItem(new SoldItem(item, Math.min(amount, item.getQuantity())));
                } else {
                    System.out.println("no stock item with id " + idx);
                }
            } catch (SalesSystemException | NoSuchElementException e) {
                log.error(e.getMessage(), e);
            }
        } else {
            System.out.println("unknown command");
        }
    }

    private void showTeam() {
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
            Scanner sc = new Scanner(System.in);
            System.out.println("Would you like to continue? (y/n)");
            String input = sc.nextLine();
            if (input.equalsIgnoreCase("y")) {
                SalesSystemDAO dao = new InMemorySalesSystemDAO();
                ConsoleUI console = new ConsoleUI(dao);
                console.run();
            } else {
                System.exit(0);
            }
        } catch (IOException e) {
            System.out.println("Did not find the data");
            System.exit(1);
        }
    }

    private void addProductToWarehouse(String name, String price, String amount) {
        try {
            long id = dao.findStockItems().stream().mapToLong(StockItem::getId).max().orElse(0) + 1;
            if (Double.parseDouble(price) < 0 || Integer.parseInt(amount) < 0) {
                throw new NumberFormatException("Price and amount must be positive");
            } else {
                StockItem stockItem = new StockItem(id, name, name, Double.parseDouble(price), Integer.parseInt(amount));
                dao.saveStockItem(stockItem);
                System.out.println("Item added to stock:");
                System.out.println("-------------------------");
                System.out.println(name + " " + price + "Euro (" + amount + " items)");
                System.out.println("-------------------------");
            }
        } catch(NumberFormatException e){
            log.error(e.getMessage());
            System.out.println("Could not add item: " + e.getMessage());
        }

    }
    private void removeProduct (String id) {
        long barCode = Long.parseLong(id);
        if (dao.findStockItem(barCode) != null) {
            dao.findStockItems().remove(dao.findStockItem(barCode));
            System.out.println("Item removed!");
        } else System.out.println("Could not find an item with id " + id);
    }

    private void history() throws IOException {

        System.out.println("----------History---------");
        HistoryControllerCLI.usage();
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        boolean historyUp = true;
        while (historyUp) {
            System.out.print("> ");
            historyUp = HistoryControllerCLI.proccessHistoryCommand(input.readLine().trim(),historyUp);
            System.out.println("---------------------done temp");
        }

    }
}