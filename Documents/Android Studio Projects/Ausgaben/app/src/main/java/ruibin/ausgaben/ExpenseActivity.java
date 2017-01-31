package ruibin.ausgaben;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import ruibin.ausgaben.misc.DecimalDigitsInputFilter;
import ruibin.ausgaben.misc.Quintuple;
import ruibin.ausgaben.database.DatabaseExpenses;
import ruibin.ausgaben.database.Expense;
import ruibin.ausgaben.exceptions.MissingNameException;

public class ExpenseActivity extends AppCompatActivity {

    private SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
    private Calendar mCal = Calendar.getInstance();
    private TextView mDateDisplay;

    private DatabaseExpenses database;

    // For editing purposes
    private boolean isEditExpense;
    private long editExpenseId;
    private int selectedMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        // Initalisation methods
        openDatabase();
        setDisplays();
        populateDataFromBundle();
        setCurrencyVisibility();
        setDeleteButtonVisibility();

    }

    /*
     * ====================== INITIALISATION METHODS ======================
     */

    private void openDatabase() {
        database = new DatabaseExpenses(this);
        database.open();
    }

    // Sets the correct displays for the elements
    private void setDisplays() {
        // Sets the date to today's date by default
        mDateDisplay = (TextView) findViewById(R.id.input_expenseDate);
        updateDateDisplay();

        // Sets the input amount to 2 decimal places
        EditText editText = (EditText) findViewById(R.id.input_expenseAmt);
        editText.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(6,2)});
    }

    // Populates the fields with data if applicable (i.e. editing an existing expense)
    private void populateDataFromBundle() {
         Bundle bundle = getIntent().getExtras();
         if (bundle != null) {
             isEditExpense = true;
             editExpenseId = bundle.getLong("id");

             Date expenseDate = new Date(bundle.getLong("date"));
             mCal.setTime(expenseDate);
             updateDateDisplay();
             setSelectedMonth(expenseDate);

             EditText editName = (EditText) findViewById(R.id.input_expenseName);
             editName.setText(bundle.getString("name"));

             Spinner categorySpinner = (Spinner) findViewById(R.id.spn_expenseCategory);
             setCategorySpinner(categorySpinner, bundle.getString("category"));

             EditText editAmount = (EditText) findViewById(R.id.input_expenseAmt);
             editAmount.setText(bundle.getString("amount"));

             Spinner currencySpinner = (Spinner) findViewById(R.id.spn_currency);
             setCurrencySpinner(currencySpinner, bundle.getString("currency"));
         }
    }

    // Sets the visibility of the currencies in the Spinner - only applicable when adding a new expense
    private void setCurrencyVisibility() {
        if (!isEditExpense) {
            ArrayList<String> currencyList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.spn_currency_entries)));
            SharedPreferences mPrefs = getSharedPreferences("toggleRates", MODE_PRIVATE);
            currencyList = filterHiddenCurrencies(currencyList, mPrefs);

            Spinner currencySpinner = (Spinner) findViewById(R.id.spn_currency);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, currencyList);
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

            currencySpinner.setAdapter(adapter);
            currencySpinner.setSelection(currencyList.indexOf("EUR")); // Sets the default currency to EUR
        }
    }

    // Sets the 'Delete' button to be visible if applicable
    private void setDeleteButtonVisibility() {
        if (isEditExpense) {
            Button delButton = (Button) findViewById(R.id.btn_delete);
            delButton.setVisibility(View.VISIBLE);
        }
    }

    /*
     * ====================== CLICK HANDLERS ======================
     */

    @Override
    public void onBackPressed() {
        if (!isEditExpense) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra("month", selectedMonth);
            startActivity(intent);
        }
    }

    public void onClickSetDate(View view) {
        DatePickerDialog.OnDateSetListener mDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                mCal.set(Calendar.YEAR, year);
                mCal.set(Calendar.MONTH, month);
                mCal.set(Calendar.DAY_OF_MONTH, day);
                updateDateDisplay();
            }
        };

        new DatePickerDialog(ExpenseActivity.this, mDateListener,
                mCal.get(Calendar.YEAR),
                mCal.get(Calendar.MONTH),
                mCal.get(Calendar.DAY_OF_MONTH)).show();

    }

    public void onClickCancel(View view) {
        if (!isEditExpense) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra("month", selectedMonth);
            startActivity(intent);
        }
    }

    public void onClickSave(View view) {
        try {
            Quintuple<Long, String, String, BigDecimal, String> quint = extractInputData(); // date, name, category, amount, currency
            setSelectedMonth(new Date(quint.getFirst()));

            // Forex rates Preferences
            SharedPreferences mPrefs = getSharedPreferences("forexRates", MODE_PRIVATE);
            Intent intent = new Intent(this, DetailsActivity.class);

            // Location Preferences
            SharedPreferences mPrefsCountry = getSharedPreferences("location", MODE_PRIVATE);
            String countryName = mPrefsCountry.getString("country", "");

            if (!isEditExpense) {
                Expense newExpense = database.createExpense(quint.getFirst(), quint.getSecond(),
                        quint.getThird(), quint.getFourth(), quint.getFifth(), mPrefs, countryName);
                intent.putExtra("newExpenseId", newExpense.getId());
                intent.putExtra("source", "ExpenseActivity");
                Toast.makeText(getApplicationContext(), "'" + quint.getSecond() + "' added in " + quint.getFifth(), Toast.LENGTH_SHORT).show();
            } else {
                boolean[] isEditsMade = database.editExpense(editExpenseId, quint.getFirst(), quint.getSecond(),
                        quint.getThird(), quint.getFourth(), quint.getFifth(), mPrefs);
                intent.putExtra("editExpenseId", editExpenseId);
                intent.putExtra("isDateEdited", isEditsMade[0]);
                intent.putExtra("isNameEdited", isEditsMade[1]);
                intent.putExtra("isCategoryEdited", isEditsMade[2]);
                intent.putExtra("isAmountEdited", isEditsMade[3]);
                intent.putExtra("isCurrencyEdited", isEditsMade[4]);

                if (isEditsMade[0] || isEditsMade[1] || isEditsMade[2] || isEditsMade[3] || isEditsMade[4]) // if any edits were made
                    Toast.makeText(getApplicationContext(), "'" + quint.getSecond() + "' edited", Toast.LENGTH_SHORT).show();
            }

            intent.putExtra("month", selectedMonth); // To display the correct month in DetailsActivity
            startActivity(intent);
        } catch (NumberFormatException e) {
            Toast.makeText(getApplicationContext(), getString(R.string.message_missingAmt), Toast.LENGTH_SHORT).show();
        } catch (MissingNameException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickDelete(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.dialog_deleteConfirmation));

        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            String deletedExpenseName = database.deleteExpense(editExpenseId);
            Toast.makeText(getApplicationContext(), "'" + deletedExpenseName + "' deleted", Toast.LENGTH_SHORT).show();
            dialog.cancel();

            Intent intent = new Intent(ExpenseActivity.this, DetailsActivity.class);
            intent.putExtra("month", selectedMonth);
            startActivity(intent);
            }
        });

        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /*
     *  ====================== HELPER METHODS ======================
     */

    private void updateDateDisplay() {
        mDateDisplay.setText(sdf.format(mCal.getTime()));
    }

    private Quintuple<Long, String, String, BigDecimal, String> extractInputData() throws
            NumberFormatException, MissingNameException {
        // Get input data
        EditText input_name = (EditText) findViewById(R.id.input_expenseName);
        Spinner spn_category = (Spinner) findViewById(R.id.spn_expenseCategory);
        EditText input_amount = (EditText) findViewById(R.id.input_expenseAmt);
        Spinner spn_currency =  (Spinner) findViewById(R.id.spn_currency);

        // Extract input data
        long date = mCal.getTimeInMillis();
        String name = input_name.getText().toString().trim();
        String category = spn_category.getSelectedItem().toString().trim();
        BigDecimal amount = new BigDecimal(input_amount.getText().toString().trim());
        String currency = spn_currency.getSelectedItem().toString().trim();

        if (name.equals("")) {
            throw new MissingNameException(getString(R.string.message_missingName));
        }

        return new Quintuple<>(date, name, category, amount, currency);
    }

    // Filters the list of currencies to be displayed in the Spinner
    private ArrayList<String> filterHiddenCurrencies(ArrayList<String> list, SharedPreferences mPrefs) {
        String[] currencies = { "ALL", "BAM", "BGN", "BYN", "CHF", "CZK", "DKK", "GBP", "HUF",
                                "HRK", "MKD", "NOK", "PLN", "RON", "RSD", "SEK", "SGD", "TRY"};

        for (int i = 0; i < currencies.length; i++) {
            if (!mPrefs.getBoolean(currencies[i], true)) { // if boolean is false, remove the currency
                list.remove(currencies[i]);
            }
        }

        return list;
    }

    // Sets the month to that of the expense being added/edited
    private void setSelectedMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        selectedMonth = cal.get(Calendar.MONTH) + 1;
    }

    // Sets the category spinner to the category of the edited expense
    private void setCategorySpinner(Spinner spinner, String category) {
        switch (category) {
            case "Food" :
                spinner.setSelection(0);
                break;
            case "Gifts" :
                spinner.setSelection(1);
                break;
            case "Leisure" :
                spinner.setSelection(2);
                break;
            case "Misc" :
                spinner.setSelection(3);
                break;
            case "Shopping" :
                spinner.setSelection(4);
                break;
            case "Travel" :
                spinner.setSelection(5);
                break;
        }
    }

    // Sets the currency spinner to the currency of the edited expense
    private void setCurrencySpinner(Spinner spinner, String currency) {
        switch (currency) {
            case "ALL" :
                spinner.setSelection(0);
                break;
            case "BAM" :
                spinner.setSelection(1);
                break;
            case "BGN" :
                spinner.setSelection(2);
                break;
            case "BYN" :
                spinner.setSelection(3);
                break;
            case "CHF" :
                spinner.setSelection(4);
                break;
            case "CZK" :
                spinner.setSelection(5);
                break;
            case "DKK" :
                spinner.setSelection(6);
                break;
            case "EUR" :
                spinner.setSelection(7);
                break;
            case "GBP" :
                spinner.setSelection(8);
                break;
            case "HRK" :
                spinner.setSelection(9);
                break;
            case "HUF" :
                spinner.setSelection(10);
                break;
            case "MKD" :
                spinner.setSelection(11);
                break;
            case "NOK" :
                spinner.setSelection(12);
                break;
            case "PLN" :
                spinner.setSelection(13);
                break;
            case "RON" :
                spinner.setSelection(14);
                break;
            case "RSD" :
                spinner.setSelection(15);
                break;
            case "SEK" :
                spinner.setSelection(16);
                break;
            case "SGD" :
                spinner.setSelection(17);
                break;
            case "TRY" :
                spinner.setSelection(18);
                break;
        }
    }
}
