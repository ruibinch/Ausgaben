package com.ausgaben.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by Ruibin on 1/1/2017.
 */

public class DatabaseExpenses {

    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;

    private String[] allColumns = {
            SQLiteHelper.COLUMN_ID,
            SQLiteHelper.COLUMN_DATE,
            SQLiteHelper.COLUMN_NAME,
            SQLiteHelper.COLUMN_CATEGORY,
            SQLiteHelper.COLUMN_AMOUNT,
            SQLiteHelper.COLUMN_CURRENCY,
            SQLiteHelper.COLUMN_FOREXRATE,
            SQLiteHelper.COLUMN_FOREXRATE_EURTOSGD,
            SQLiteHelper.COLUMN_CITY,
            SQLiteHelper.COLUMN_COUNTRY,
            SQLiteHelper.COLUMN_IMAGEPATH
    };

    public DatabaseExpenses(Context context) {
        dbHelper = SQLiteHelper.getInstance(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
        //upgradeDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    private void upgradeDatabase() {
        dbHelper.onUpgrade(database, 6, 7);
        System.out.println("Database upgraded");
    }

    /*
     * ====================== DATABASE METHODS ======================
     */

    // Creates a new expense and adds it to the DB
    public Expense createExpense(long date, String name, String category, BigDecimal amount, String currency,
                                 SharedPreferences mPrefs, String city, String country, String imagePath) {
        // Creating the set of values to be inserted into the DB
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_DATE, date);
        values.put(SQLiteHelper.COLUMN_NAME, name);
        values.put(SQLiteHelper.COLUMN_CATEGORY, category);
        values.put(SQLiteHelper.COLUMN_AMOUNT, amount.toPlainString());
        values.put(SQLiteHelper.COLUMN_CURRENCY, currency);
        values.put(SQLiteHelper.COLUMN_FOREXRATE, getForexRate(mPrefs, currency));
        values.put(SQLiteHelper.COLUMN_FOREXRATE_EURTOSGD, getForexRate(mPrefs, "SGD"));
        values.put(SQLiteHelper.COLUMN_CITY, city);
        values.put(SQLiteHelper.COLUMN_COUNTRY, country);
        values.put(SQLiteHelper.COLUMN_IMAGEPATH, imagePath);

        // Insert the data into the DB and obtain the ID
        long insertId = database.insert(SQLiteHelper.TABLE_EXPENSES, null, values);

        // Read the recently-inserted data from the DB using a Cursor
        Cursor cursor = database.query(SQLiteHelper.TABLE_EXPENSES, allColumns, SQLiteHelper.COLUMN_ID + " = " + insertId,
                null, null, null, null);
        cursor.moveToFirst();
        Expense expense = getExpenseDetails(cursor);
        System.out.println(expense);

        cursor.close();
        return expense;
    }

    // Edits an existing expense in the DB
    public boolean[] editExpense(long editId, long date, String name, String category, BigDecimal amount,
                                 String currency, SharedPreferences mPrefs, String country, String imagePath) {
        // Obtain the current row of values in the DB
        Cursor cursor = database.query(SQLiteHelper.TABLE_EXPENSES, allColumns, SQLiteHelper.COLUMN_ID + " = " + editId,
                null, null, null, null);
        cursor.moveToFirst();
        Expense expense = getExpenseDetails(cursor);

        // Create the new set of values to replace the existing set in the DB
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_DATE, date);
        values.put(SQLiteHelper.COLUMN_NAME, name);
        values.put(SQLiteHelper.COLUMN_CATEGORY, category);
        values.put(SQLiteHelper.COLUMN_AMOUNT, amount.toPlainString());
        values.put(SQLiteHelper.COLUMN_CURRENCY, currency);
        values.put(SQLiteHelper.COLUMN_COUNTRY, country);
        values.put(SQLiteHelper.COLUMN_IMAGEPATH, imagePath);

        // Obtain what has been edited
        boolean[] isEditsMade = compareDifferences(expense, values);
        if (isEditsMade[4]) { // if there is a currency change, then change the forex rate
            values.put(SQLiteHelper.COLUMN_FOREXRATE, getForexRate(mPrefs, currency));
        }

        database.update(SQLiteHelper.TABLE_EXPENSES, values, SQLiteHelper.COLUMN_ID + " = " + editId, null);

        cursor.close();
        return isEditsMade;
    }

    // Deletes an expense from the DB
    // Returns the name of the expense deleted
    public String deleteExpense(long deleteId) {
        String[] columnToRetrieve = { SQLiteHelper.COLUMN_NAME };
        Cursor cursor = database.query(SQLiteHelper.TABLE_EXPENSES, columnToRetrieve,
                SQLiteHelper.COLUMN_ID + " = " + deleteId, null, null, null, null);
        cursor.moveToFirst();

        database.delete(SQLiteHelper.TABLE_EXPENSES, SQLiteHelper.COLUMN_ID + " = " + deleteId, null);
        String deletedExpenseName = cursor.getString(0);

        cursor.close();
        return deletedExpenseName;
    }

    // Obtains a list of expenses (in Expense objects) for the specified MONTH and the specified COUNTRY from the DB
    public ArrayList<Expense> getExpensesList(int month, String country, int startDate, int endDate) {
        ArrayList<Expense> expenseList = new ArrayList<Expense>();

        Cursor cursor = database.query(SQLiteHelper.TABLE_EXPENSES, allColumns,
                null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            if ((month == 0 || expenseWithinMonthAndDateRange(cursor.getLong(1), month, startDate, endDate)) &&
                    expenseWithinCountry(cursor.getString(9), country)) {
                Expense expense = getExpenseDetails(cursor);
                expenseList.add(expense);
            }
            cursor.moveToNext();
        }

        cursor.close();
        return expenseList;
    }

    // Obtains a list of the countries that are existing in the DB
    public ArrayList<String> getCountriesList() {
        ArrayList<String> countriesList = new ArrayList<String>();

        Cursor cursor = database.query(SQLiteHelper.TABLE_EXPENSES, new String[] { SQLiteHelper.COLUMN_COUNTRY },
                null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            String country = cursor.getString(0);
            if (!countriesList.contains(country)) {
                countriesList.add(country);
            }
            cursor.moveToNext();
        }

        cursor.close();
        return countriesList;
    }

    // Obtains the total expenditure for expenses of the specified CATEGORY in the specified MONTH (within the specified start and end dates)
    // in the correct display CURRENCY and made in the correct COUNTRY
    public BigDecimal getCategoryExpenditure(String category, int month, int startDate, int endDate, String displayCurrency, String country){
        Cursor cursor = database.query(SQLiteHelper.TABLE_EXPENSES,
                new String[] { SQLiteHelper.COLUMN_DATE, SQLiteHelper.COLUMN_CATEGORY, SQLiteHelper.COLUMN_AMOUNT,
                        SQLiteHelper.COLUMN_FOREXRATE, SQLiteHelper.COLUMN_FOREXRATE_EURTOSGD, SQLiteHelper.COLUMN_COUNTRY }, // columns to return
                null, null, null, null, null, null);
        cursor.moveToFirst();

        BigDecimal categoryExpenditure = new BigDecimal(0);
        while (!cursor.isAfterLast()) {
            if (expenseWithinCategory(cursor.getString(1), category) &&
                    (month == 0 || expenseWithinMonthAndDateRange(cursor.getLong(0), month, startDate, endDate)) &&
                    expenseWithinCountry(cursor.getString(5), country)) {
                BigDecimal expenditure = getExpenditureAmount(cursor.getString(2), cursor.getDouble(3)); // expenditure value here is in euros

                if (displayCurrency.equals("SGD"))
                    expenditure = convertExpenditureAmountToSgd(expenditure, cursor.getDouble(4)); // convert to SGD if need be
                categoryExpenditure = categoryExpenditure.add(expenditure);
            }
            cursor.moveToNext();
        }

        cursor.close();
        categoryExpenditure = categoryExpenditure.setScale(2, RoundingMode.HALF_UP);
        return categoryExpenditure;
    }


    public void deleteAllExpenses() {
        database.delete(SQLiteHelper.TABLE_EXPENSES, null, null);
    }

    /*
     * ====================== HELPER METHODS ======================
     */

    // Checks if the expense occurs within the specified month
    private boolean expenseWithinMonthAndDateRange(long expenseDate, int month, int startDate, int endDate) {
        int expMonth = getMonth(expenseDate);
        int expDate = getDate(expenseDate);

        return expMonth+1 == month && expDate >= startDate && expDate <= endDate;
    }

    // Checks if the expense is of a specified category
    private boolean expenseWithinCategory(String expenseCategory, String category) {
        return expenseCategory.equalsIgnoreCase(category);
    }

    // Checks if the expense is made in a specific country
    private boolean expenseWithinCountry(String expenseCountry, String country) {
        return (country.equals("All") || country.equalsIgnoreCase(expenseCountry));
    }

    // Get all the details of an expense in an Expense object
    private Expense getExpenseDetails(Cursor cursor) {
        return new Expense(cursor.getLong(0), cursor.getLong(1), cursor.getString(2), cursor.getString(3),
                new BigDecimal(cursor.getString(4)), cursor.getString(5), cursor.getLong(6), cursor.getLong(7),
                cursor.getString(8), cursor.getString(9), cursor.getString(10));
    }

    // Get the expenditure amount, converted into euros, rounded to 2 decimal places
    private BigDecimal getExpenditureAmount(String amt, double forexRate) {
        BigDecimal amount = new BigDecimal(amt);
        return amount.divide(new BigDecimal(forexRate), 2, RoundingMode.HALF_UP);
    }

    // Converts the expenditure amount from EUR to SGD - for display purposes
    private BigDecimal convertExpenditureAmountToSgd(BigDecimal amount, double forexRateEurToSgd) {
        return amount.multiply(new BigDecimal(forexRateEurToSgd));
    }

    // Returns a boolean array indicating which fields were edited (date, name, category, amount)
    private boolean[] compareDifferences(Expense expense, ContentValues values) {
        boolean[] isEditsMade = new boolean[7];
        if (expense.getDate() != values.getAsLong(SQLiteHelper.COLUMN_DATE))
            isEditsMade[0] = true;
        if (!expense.getName().equals(values.getAsString(SQLiteHelper.COLUMN_NAME)))
            isEditsMade[1] = true;
        if (!expense.getCategory().equals(values.getAsString(SQLiteHelper.COLUMN_CATEGORY)))
            isEditsMade[2] = true;
        if (!expense.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP).equals(new BigDecimal(values.getAsString(SQLiteHelper.COLUMN_AMOUNT))))
            isEditsMade[3] = true;
        if (!expense.getCurrency().equals(values.getAsString(SQLiteHelper.COLUMN_CURRENCY)))
            isEditsMade[4] = true;
        if (!expense.getCountry().equals(values.getAsString(SQLiteHelper.COLUMN_COUNTRY)))
            isEditsMade[5] = true;
        if ((expense.getImagePath() != null && values.getAsString(SQLiteHelper.COLUMN_IMAGEPATH) == null) ||
            (expense.getImagePath() == null && values.getAsString(SQLiteHelper.COLUMN_IMAGEPATH) != null)) {
            isEditsMade[6] = true;
        } else if (expense.getImagePath() != null && values.getAsString(SQLiteHelper.COLUMN_IMAGEPATH) != null) {
            if ((!expense.getImagePath().equals(values.getAsString(SQLiteHelper.COLUMN_IMAGEPATH))))
                isEditsMade[6] = true;
        }

        return isEditsMade;
    }

    // Returns the forex rate for the specified currency
    private double getForexRate(SharedPreferences mPrefs, String currency) {
        switch (currency) {
            case "ALL" :
                return Double.valueOf(mPrefs.getString("ALL", ""));
            case "BAM" :
                return Double.valueOf(mPrefs.getString("BAM", ""));
            case "BGN" :
                return Double.valueOf(mPrefs.getString("BGN", ""));
            case "BYN" :
                return Double.valueOf(mPrefs.getString("BYN", ""));
            case "CHF" :
                return Double.valueOf(mPrefs.getString("CHF", ""));
            case "CZK" :
                return Double.valueOf(mPrefs.getString("CZK", ""));
            case "DKK" :
                return Double.valueOf(mPrefs.getString("DKK", ""));
            case "GBP" :
                return Double.valueOf(mPrefs.getString("GBP", ""));
            case "HRK" :
                return Double.valueOf(mPrefs.getString("HRK", ""));
            case "HUF" :
                return Double.valueOf(mPrefs.getString("HUF", ""));
            case "MKD" :
                return Double.valueOf(mPrefs.getString("MKD", ""));
            case "NOK" :
                return Double.valueOf(mPrefs.getString("NOK", ""));
            case "PLN" :
                return Double.valueOf(mPrefs.getString("PLN", ""));
            case "RON" :
                return Double.valueOf(mPrefs.getString("RON", ""));
            case "RSD" :
                return Double.valueOf(mPrefs.getString("RSD", ""));
            case "SEK" :
                return Double.valueOf(mPrefs.getString("SEK", ""));
            case "SGD" :
                return Double.valueOf(mPrefs.getString("SGD", ""));
            case "TRY" :
                return Double.valueOf(mPrefs.getString("TRY", ""));
        }
        return 1; // EUR
    }

    /*
     * ====================== SECONDARY HELPER METHODS ======================
     */

    // Retrieves the month from the given date
    private int getMonth(long date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(date));
        return cal.get(Calendar.MONTH);
    }

    // Retrieves the month date from the given date
    private int getDate(long date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(date));
        return cal.get(Calendar.DATE);
    }

}
