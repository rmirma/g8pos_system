package ee.ut.math.tvt.salessystem.ui;


import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.HistoryItem;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class HistoryControllerCLI {

    private static final Logger log = LogManager.getLogger(ConsoleUI.class);
    private static SalesSystemDAO dao;

    public HistoryControllerCLI(SalesSystemDAO dao){
        HistoryControllerCLI.dao = dao;
    }

    public static void showAllHistory(){
        System.out.println("date\t\ttime\t\t\t\ttotal");
        for (HistoryItem item : dao.getHistoryList()) {
            System.out.println(item.getDate()+ "  " +
                    item.getTime() + "\t  " + item.getTotal());
        }
        log.info("showing all of purchase history in CLI");
    }

    public static void showLast10(){
        System.out.println("date\t\ttime\t\t\t\ttotal");

        List<HistoryItem> historyItems;
        if (dao.getHistoryList().size() < 10){
            historyItems = dao.getHistoryList();
            log.info("Last 10 purchases shown in CLI");
        }else{
            historyItems = dao.getHistoryList().subList(0,10);
            log.info("Last 10 purchases shown in CLI");
        }

        for (HistoryItem item : dao.getHistoryList()) {
            System.out.println(item.getDate()+ "  " +
                    item.getTime() + "\t  " + item.getTotal());
        }
        log.info("showing last 10 purchases made in CLI");
    }

    public static void usage(){
        System.out.println("Usage:");
        System.out.println("showall ->\t\t\tshows all the purchase history");
        System.out.println("showlast10 ->\t\tshows last 10 confirmed purchases");
        System.out.println("betweendates [Start Date] [End date] -> shows purchases between given dates." +
                        " date format: yy-mm-dd");
        System.out.println("checkcontents [Date] [time]  ->\t\t\tshows the contents of a purchase, get the " +
                "date and time by copying them from listings in history");
        System.out.println("q ->\t leave history view");
        System.out.println("help ->\t show help menu");
    }

    private static void showContents(String date, String time) {
        try{
            LocalDate dateToFind = LocalDate.parse(date);
            LocalTime timeToFind = LocalTime.parse(time);

            //find the purchase
            List<SoldItem> contents = null;
            for (HistoryItem item : dao.getHistoryList()) {
                if (item.getDate().equals(dateToFind) &&
                    item.getTime().equals(timeToFind)){
                    contents = item.getContents();
                    break;
                }
            }//for

            //print contents
            System.out.println("contents of purchase made at " + date + " " + time);
            System.out.println("id\tname\t\tprice\tquantity\tsum");
            if (contents != null){
                for (SoldItem content : contents) {
                    System.out.println(
                            content.getId().toString() +
                                '\t' + content.getName() +
                                    '\t' + content.getPrice() +
                                        "\t\t" + content.getQuantity() +
                                            "\t\t\t" + content.getSum());

                }
                System.out.println('\n');
                log.info("show contents of purchase made on " + date + " " + time);
            }

        }catch (DateTimeParseException e){
            log.error("wrong date and time format when trying to show contents of a purchase");
            System.out.println("wrong date and time, check format again");
        }
    }

    private static void showBetweenDates(String command1, String command2) {
        try {
            LocalDate start = LocalDate.parse(command1);
            LocalDate end = LocalDate.parse(command2);
            if (start.isBefore(end.plusDays(1)) &&   // +1 to be inclusive
                    start.isBefore(LocalDate.now().plusDays(1))) {

                System.out.println("purchases made between " + start + " - " + end);
                System.out.println("date\t\ttime\t\t\t\ttotal");

                for (HistoryItem item : dao.getHistoryList()) {
                    if (item.getDate().isAfter(start.minusDays(1)) && // +1 to be inclusive
                            item.getDate().isBefore(end.plusDays(1))) {
                        System.out.println(item.getDate()+ " " +
                                item.getTime() + '\t' + item.getTotal());
                    }
                }//for
                System.out.println('\t');
                log.info("History shown between " + start + " - " + end);
            } else {
                System.out.println("error with base conditions, check that dates are in order");
                log.error("wrong date range selected when viewing history between dates");
            }
        } catch (DateTimeParseException e) {
            System.out.println("wrong date format, check that format is \"yy-mm-dd\" ");
            log.error("DateTimeParseException when trying to view history between dates");
        }
    }

    public static boolean proccessHistoryCommand(String input, boolean historyUp){
        String[] commands = input.split(" ");
        switch (commands[0]){
            case "showall":
                showAllHistory();
                break;
            case "showlast10":
                showLast10();
                break;
            case "q":
                return false;
            case "checkcontents":
                if (commands.length == 3) showContents(commands[1],commands[2]);
                else System.out.println("wrong imput, chec that all the arguments are given");
                break;
            case "help":
                usage();
                break;
            case "betweendates":
                if (commands.length == 3) showBetweenDates(commands[1],commands[2]);
                else System.out.println("wrong imput, check that both dates are given");
                break;
            default: return true;

        }
        return true;
    }


}
