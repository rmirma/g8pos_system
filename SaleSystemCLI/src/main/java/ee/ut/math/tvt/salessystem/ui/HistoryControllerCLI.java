package ee.ut.math.tvt.salessystem.ui;


import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.HistoryItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class HistoryControllerCLI {

    private static final Logger log = LogManager.getLogger(ConsoleUI.class);
    private static SalesSystemDAO dao;

    public HistoryControllerCLI(SalesSystemDAO dao){
        HistoryControllerCLI.dao = dao;
    }

    public static boolean showAllHistory(){
        System.out.println("date\t\ttime\t\t\t\ttotal");
        for (HistoryItem item : dao.getHistoryList()) {
            System.out.println(item.getDate()+ " " +
                    item.getTime() + '\t' + item.getTotal());
        }
        return true;
    }

    public static boolean showLast10(){
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
            System.out.println(item.getDate()+ " " +
                    item.getTime() + '\t' + item.getTotal());
        }
        return true;
    }

    public static boolean usage(){
        System.out.println("-------------------------");
        System.out.println("Usage:");
        System.out.println("showall\t\t shows all the purchase history");
        System.out.println("showlast10\t\tshows last 10 confirmed purchases");
        System.out.println("betweendates [Start Date] [End date]\tshows purchases between given dates." +
                        " date format: yy-mm-dd");
        System.out.println("q\t\t leave history view");
        System.out.println("help\t\tshow help menu");
        System.out.println("-------------------------");
        return true;
    }

    private static boolean showBetweenDates(String command1, String command2) {
        try {
            LocalDate start = LocalDate.parse(command1);
            LocalDate end = LocalDate.parse(command2);
            if (start != null &&
                    end != null &&
                    start.isBefore(end.plusDays(1)) &&   // +1 to be inclusive
                    start.isBefore(LocalDate.now().plusDays(1))) {

                System.out.println("date\t\ttime\t\t\t\ttotal");

                for (HistoryItem item : dao.getHistoryList()) {
                    if (item.getDate().isAfter(start.minusDays(1)) && // +1 to be inclusive
                            item.getDate().isBefore(end.plusDays(1))) {
                        System.out.println(item.getDate()+ " " +
                                item.getTime() + '\t' + item.getTotal());
                    }
                }//for

                log.info("History shown between " + start + " - " + end);
            } else {
                System.out.println("error with base conditions, check that dates are in order");
                log.error("wrong date range selected when viewing history between dates");
            }
        } catch (DateTimeParseException e) {
            System.out.println("wrong date format, check that format is \"yy-mm-dd\" ");
            log.error("DateTimeParseException when trying to view history between dates");
        }
        return true;
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
            case "help":
                usage();
                break;
            case "betweendates":
                if (commands.length == 3) showBetweenDates(commands[1],commands[2]);
                else System.out.println("wrong imput, check that both dates are given");
            default: return true;

        }
        return true;
    }

}
