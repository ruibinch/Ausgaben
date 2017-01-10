package ruibin.ausgaben.database;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Created by Ruibin on 1/1/2017.
 */

public class Expense {

    private SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yy");

    private long id;
    private long date;
    private String name;
    private String category;
    private BigDecimal amount;

    public Expense() { }

    public Expense(long id, long date, String name, String category, BigDecimal amount) {
        this.id = id;
        this.date = date;
        this.name = name;
        this.category = category;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return (sdf.format(new Date(date)) + ", " + name + ", " + category + ", $" +
                amount.setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    // GETTERS

    public long getId() {
        return id;
    }

    public long getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    // SETTERS

    public void setId(long id) {
        this.id = id;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
