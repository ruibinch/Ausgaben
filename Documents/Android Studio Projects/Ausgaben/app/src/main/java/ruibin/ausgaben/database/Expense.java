package ruibin.ausgaben.database;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static android.util.LayoutDirection.LOCALE;

/**
 * Created by Ruibin on 1/1/2017.
 */

public class Expense {

    private SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yy");

    // Main info
    private long id;
    private long date;
    private String name;
    private String category;
    private BigDecimal amount;

    // Currency info
    private String currency;
    private double forexRate; // Forex rate of the currency at the time when the expense is saved in the DB
    private double forexRateEurToSgd; // Forex rate of EUR to SGD at the time when the expense is saved

    // Location info
    private String city;
    private String country;

    public Expense() { }

    public Expense(long id, long date, String name, String category, BigDecimal amount, String currency,
                   double forexRate, double forexRateEurToSgd, String city, String country) {
        this.id = id;
        this.date = date;
        this.name = name;
        this.category = category;
        this.amount = amount;
        this.currency = currency;
        this.forexRate = forexRate;
        this.forexRateEurToSgd = forexRateEurToSgd;
        this.city = city;
        this.country = country;
    }

    @Override
    public String toString() {
        return (sdf.format(new Date(date)) + ", " + name + ", " + category + ", " + amount.setScale(2, BigDecimal.ROUND_HALF_UP) +
                ", forexRates = (" + String.format(Locale.ENGLISH, "%,.2f", forexRate) + ", " + String.format(Locale.ENGLISH, "%,.2f", forexRateEurToSgd) + ")" +
                ", [" + city.toUpperCase() + ", " + country.toUpperCase() + "]");
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

    public String getCurrency() {
        return currency;
    }

    public double getForexRate() {
        return forexRate;
    }

    public double getForexRateEurToSgd() {
        return forexRateEurToSgd;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
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

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setForexRate(double forexRate) {
        this.forexRate = forexRate;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

}
