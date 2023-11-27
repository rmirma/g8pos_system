/**
 * HistoryItem is the object to hold the info of one singular purchase.
 * It holds the date,time, total price and the contents of the purchase.
 */

package ee.ut.math.tvt.salessystem.dataobjects;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
@Entity
@Table(name = "HISTORY")
public class HistoryItem {

    @Id
    @GeneratedValue()
    private long id;
    @OneToMany(mappedBy = "history")
    private List<SoldItem> contents; //contens of the purchse
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

    public HistoryItem(){}

    public void setContents(List<SoldItem> contents) {
        this.contents = contents;
    }

    //Getters for HistoryItem
    public List<SoldItem> getSoldItems() {
        return contents;
    }

    public LocalDate getDate() {
        return date;
    }

    public Double getTotal() {
        return total;
    }

    public LocalTime getTime(){return time;}

    public long getId() {
        return id;
    }

    public void setSoldItem(SoldItem item){this.contents.add(item);}
}
