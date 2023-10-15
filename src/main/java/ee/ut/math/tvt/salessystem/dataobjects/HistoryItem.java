/**
 * HistoryItem is the object to hold the info of one singular purchase.
 * It holds the date, total price and the contents of the purchase.
 */

package ee.ut.math.tvt.salessystem.dataobjects;

import java.util.Date;
import java.util.List;

public class HistoryItem {

    private final List<SoldItem> contents; //contens of the purchse
    private final Date date;  //date of the transaction
    private final Double total; //total price of the shopping cart

    public HistoryItem(List<SoldItem> contents, Date date, Double total) {
        this.contents = contents;
        this.date = date;
        this.total = total;
    }


    //Getters for HistoryItem
    public List<SoldItem> getContents() {
        return contents;
    }

    public Date getDate() {
        return date;
    }

    public Double getTotal() {
        return total;
    }
}
