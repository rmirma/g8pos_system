/**
 * HistoryItem is the object to hold the info of one singular purchase.
 * It holds the date, total price and the contents of the purchase.
 */

package ee.ut.math.tvt.salessystem.dataobjects;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
@Entity

@Table(name = "HISTORY_ITEM")

public class HistoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Transient
    public List<SoldItem> contents; //contens of the purchse
    @Column(name = "date")
    private LocalDate date;  //date of the transaction
    @Column(name = "time")
    private LocalTime time;  //time of the transaction
    @Column(name = "total")
    private Double total; //total price of the shopping cart

    public HistoryItem(List<SoldItem> contents, LocalDate date, LocalTime time, Double total) {
        this.contents = contents;
        this.date = date;
        this.time = time;
        this.total = total;
    }

    public HistoryItem() {

    }


    //Getters for HistoryItem
    public List<SoldItem> getContents() {
        return contents;
    }

    public LocalDate getDate() {
        return date;
    }

    public Double getTotal() {
        return total;
    }

    public LocalTime getTime(){return time;}
}
