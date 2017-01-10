package ruibin.ausgaben.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.math.BigDecimal;
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
            SQLiteHelper.COLUMN_AMOUNT
    };

    public DatabaseExpenses(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /*
     * ====================== DATABASE METHODS ======================
     */

    // Creates a new expense and adds it to the DB
    public Expense createExpense(long date, String name, String category, BigDecimal amount) {
        // Creating the set of values to be inserted into the DB
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_DATE, date);
        values.put(SQLiteHelper.COLUMN_NAME, name);
        values.put(SQLiteHelper.COLUMN_CATEGORY, category);
        values.put(SQLiteHelper.COLUMN_AMOUNT, amount.toPlainString());

        // Insert the data into the DB and obtaining the ID
        long insertId = database.insert(SQLiteHelper.TABLE_EXPENSES, null, values);

        // Read the recently-inserted data from the DB using a Cursor
        Cursor cursor = database.query(SQLiteHelper.TABLE_EXPENSES, allColumns, SQLiteHelper.COLUMN_ID + " = " + insertId,
                null, null, null, null);
        cursor.moveToFirst();
        Expense expense = getExpenseDetails(cursor);

        cursor.close();
        return expense;
    }

    // Edits an existing expense in the DB
    public boolean[] editExpense(long editId, long date, String name, String category, BigDecimal amount) {
        // Create the new set of values to replace the existing set in the DB
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_DATE, date);
        values.put(SQLiteHelper.COLUMN_NAME, name);
        values.put(SQLiteHelper.COLUMN_CATEGORY, category);
        values.put(SQLiteHelper.COLUMN_AMOUNT, amount.toPlainString());

        // Obtain the current row of values in the DB
        Cursor cursor = database.query(SQLiteHelper.TABLE_EXPENSES, allColumns, SQLiteHelper.COLUMN_ID + " = " + editId,
                null, null, null, null);
        cursor.moveToFirst();
        Expense expense = getExpenseDetails(cursor);
        boolean[] isEditsMade = compareDifferences(expense, values);

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

    // Obtains a list of expenses (in Expense objects) for the specified month from the DB
    public ArrayList<Expense> getExpensesList(int month) {
        ArrayList<Expense> expenseList = new ArrayList<Expense>();

        Cursor cursor = database.query(SQLiteHelper.TABLE_EXPENSES, allColumns,
                null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            if (month == 0 || expenseWithinMonth(cursor.getLong(1), month)) {
                Expense expense = getExpenseDetails(cursor);
                expenseList.add(expense);
            }
            cursor.moveToNext();
        }

        cursor.close();
        return expenseList;
    }

    // Obtains the total expenditure for all expenses occurring in the specified MONTH
    public BigDecimal getMonthExpenditure(int month) {
        Cursor cursor = database.query(SQLiteHelper.TABLE_EXPENSES,
                new String[] { SQLiteHelper.COLUMN_DATE, SQLiteHelper.COLUMN_AMOUNT }, // columns to return
                null, null, null, null, null, null);
        cursor.moveToFirst();

        BigDecimal monthExpenditure = new BigDecimal(0);
        while (!cursor.isAfterLast()) {
            if (month == 0 || expenseWithinMonth(cursor.getLong(0), month)) {
                BigDecimal expenditure = getExpenditureAmount(cursor.getString(1));
                monthExpenditure = monthExpenditure.add(expenditure);
            }
            cursor.moveToNext();
        }

        monthExpenditure = monthExpenditure.setScale(2);
        return monthExpenditure;
    }

    // Obtains the total expenditure for expenses of the specified CATEGORY in the specified MONTH
    public BigDecimal getCategoryExpenditure(String category, int month){
        Cursor cursor = database.query(SQLiteHelper.TABLE_EXPENSES,
                new String[] { SQLiteHelper.COLUMN_DATE, SQLiteHelper.COLUMN_CATEGORY, SQLiteHelper.COLUMN_AMOUNT }, // columns to return
                null, null, null, null, null, null);
        cursor.moveToFirst();

        BigDecimal categoryExpenditure = new BigDecimal(0);
        while (!cursor.isAfterLast()) {
            if (expenseWithinCategory(cursor, category) && (month == 0 || expenseWithinMonth(cursor.getLong(0), month))) {
                BigDecimal expenditure = getExpenditureAmount(cursor.getString(2));
                categoryExpenditure = categoryExpenditure.add(expenditure);
            }
            cursor.moveToNext();
        }

        categoryExpenditure = categoryExpenditure.setScale(2);
        return categoryExpenditure;
    }


    public void deleteAllExpenses() {
        database.delete(SQLiteHelper.TABLE_EXPENSES, null, null);
    }

    /*
     * ====================== HELPER METHODS ======================
     */

    // Checks if the expense occurs within the specified month
    private boolean expenseWithinMonth(long date, int month) {
        int expMonth = getMonth(date);
        return expMonth+1 == month;
    }

    // Checks if the expense is of a specified category
    private boolean expenseWithinCategory(Cursor cursor, String category) {
        String expCategory = cursor.getString(1);
        return expCategory.equalsIgnoreCase(category);
    }

    // Get all the details of an expense in an Expense object
    private Expense getExpenseDetails(Cursor cursor) {
        return new Expense(cursor.getLong(0), cursor.getLong(1), cursor.getString(2),
                cursor.getString(3), new BigDecimal(cursor.getString(4)));
    }

    // Get the expenditure amount in BigDecimal type
    private BigDecimal getExpenditureAmount(String amount) {
        return new BigDecimal(amount);
    }

    // Returns a boolean array indicating which fields were edited (date, name, category, amount)
    private boolean[] compareDifferences(Expense expense, ContentValues values) {
        boolean[] isEditsMade = new boolean[4];
        if (expense.getDate() != values.getAsLong(SQLiteHelper.COLUMN_DATE))
            isEditsMade[0] = true;
        if (!expense.getName().equals(values.getAsString(SQLiteHelper.COLUMN_NAME)))
            isEditsMade[1] = true;
        if (!expense.getCategory().equals(values.getAsString(SQLiteHelper.COLUMN_CATEGORY)))
            isEditsMade[2] = true;
        if (!expense.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP).equals(new BigDecimal(values.getAsString(SQLiteHelper.COLUMN_AMOUNT))))
            isEditsMade[3] = true;

        return isEditsMade;
    }

    /*
     * ====================== SECONDARY HELPER METHODS ======================
     */

    // Retrieves the month from the given date in long type
    private int getMonth(long date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(date));
        return cal.get(Calendar.MONTH);
    }

}
