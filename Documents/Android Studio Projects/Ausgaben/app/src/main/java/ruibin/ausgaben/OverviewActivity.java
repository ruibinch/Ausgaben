package ruibin.ausgaben;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ruibin.ausgaben.database.DatabaseExpenses;

public class OverviewActivity extends AppCompatActivity {

    private DatabaseExpenses database;

    private int displayMonth;
    private int displayCountry;
    private int displayStartDate = 1;
    private int displayEndDate;
    private String displayCurrency = "EUR"; // default display is EUR

    // XML resources
    private TextView expenditureDisplay;
    private Spinner selectMonthDisplay;
    private Spinner startDateSpinner;
    private Spinner endDateSpinner;
    private Spinner selectCountryDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialisation methods
        openDatabase();
        initialiseWidgets();
        populateCountrySpinnerList();
        setMonthAndCountrySelection();
        setTextViews();

        // Populate data
        updateExpenseValuesDisplayed(); // default display currency is EUR

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

    /*
     * ====================== INITIALISATION METHODS ======================
     */

    private void openDatabase() {
        database = new DatabaseExpenses(this);
        database.open();
    }

    private void initialiseWidgets() {
        expenditureDisplay = (TextView) findViewById(R.id.expenditure);

        selectMonthDisplay = (Spinner) findViewById(R.id.spn_selectMonthDisplay);
        selectMonthDisplay.setOnItemSelectedListener(selectMonthListener);

        startDateSpinner = (Spinner) findViewById(R.id.spn_selectDateStartDisplay);
        startDateSpinner.setOnItemSelectedListener(selectStartDateListener);

        endDateSpinner = (Spinner) findViewById(R.id.spn_selectDateEndDisplay);
        endDateSpinner.setOnItemSelectedListener(selectEndDateListener);

        selectCountryDisplay = (Spinner) findViewById(R.id.spn_selectCountryDisplay);
        selectCountryDisplay.setOnItemSelectedListener(selectCountryListener);
    }

    // Populates the country Spinner with the list of existin countries in the DB
    private void populateCountrySpinnerList() {
        ArrayList<String> countriesList = database.getCountriesList();
        Collections.sort(countriesList); // Sorts in alphabetical order
        countriesList.add(0, "All");

        ArrayAdapter<String> selectCountryDisplayArray = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, countriesList);
        selectCountryDisplayArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectCountryDisplay.setAdapter(selectCountryDisplayArray);
    }

    // Sets the default display to the current month
    private void setMonthAndCountrySelection() {
        int displayMonth = getIntent().getIntExtra("month", -1);
        String displayCountry = getIntent().getStringExtra("country"); // only applicable when returning from DetailsActivity

        // Set the month Spinner to the correct selection
        if (displayMonth == -1) {
            Calendar cal = Calendar.getInstance();
            selectMonthDisplay.setSelection(cal.get(Calendar.MONTH) + 1); // set to current month
            setDateRange(cal.get(Calendar.MONTH) + 1);
        } else {
            selectMonthDisplay.setSelection(displayMonth);
            setDateRange(displayMonth);
        }

        // Set the country Spinner to the correct selection
        if (displayCountry != null) {
            selectCountryDisplay.setSelection(getCountrySpinnerIndex(displayCountry));
        }
    }

    // Sets the attributes for the TextViews
    private void setTextViews() {
        TextView textToggleFilters = (TextView) findViewById(R.id.text_totalExpenditure);
        textToggleFilters.setPaintFlags(textToggleFilters.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
    }

    /*
     * ====================== CLICK HANDLERS ======================
     */

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // Click handler for 'Display Month' spinner
    OnItemSelectedListener selectMonthListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            System.out.println("Month spinner position = " + position);
            displayMonth = position;
            updateExpenseValuesDisplayed();
            if (displayMonth == 0) { // if 'All Months' are selected, hide the start/end date spinners
                startDateSpinner.setVisibility(View.INVISIBLE);
                endDateSpinner.setVisibility(View.INVISIBLE);
            } else {
                startDateSpinner.setVisibility(View.VISIBLE);
                endDateSpinner.setVisibility(View.VISIBLE);
                setDateRange(displayMonth);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    // Click handler for start date spinner
    OnItemSelectedListener selectStartDateListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            displayStartDate = position + 1;
            updateExpenseValuesDisplayed();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    // Click handler for end date spinner
    OnItemSelectedListener selectEndDateListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            displayEndDate = position + 1;
            updateExpenseValuesDisplayed();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    // Click handler for 'Display Currency' spinner
    OnItemSelectedListener selectCountryListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            displayCountry = position;
            updateExpenseValuesDisplayed();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    // Click handler for the 'Total Expenditure' amount - clicking toggles the currency displayed
    public void onClickToggleCurrencyDisplay(View view) {
        if (displayCurrency.equals("EUR")) {
            displayCurrency = "SGD";
            updateExpenseValuesDisplayed();
        } else { // if display = "SGD"
            displayCurrency = "EUR";
            updateExpenseValuesDisplayed();
        }
    };

    // Click handler for 'View Expenses List' button
    public void onClickViewExpensesList(View view) {
        String selectedCountry = (String) selectCountryDisplay.getItemAtPosition(displayCountry);

        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("displayMonth", displayMonth);
        intent.putExtra("displayCountry", selectedCountry);
        intent.putExtra("displayStartDate", displayStartDate);
        intent.putExtra("displayEndDate", displayEndDate);
        startActivity(intent);
    }

    /*
     * ====================== HELPER METHODS ======================
     */

    // Sets the range of dates based on the display month
    private void setDateRange(int month) {
        Spinner startDateSpinner = (Spinner) findViewById(R.id.spn_selectDateStartDisplay);
        Spinner endDateSpinner = (Spinner) findViewById(R.id.spn_selectDateEndDisplay);

        String[] dates = new String[31];

        switch (month) {
            case 1 :
                dates = new String[]{ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
                        "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25",
                        "26", "27", "28", "29", "30", "31" };
                displayEndDate = 31;
                break;
            case 2 :
                dates = new String[]{ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
                        "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25",
                        "26", "27", "28" };
                displayEndDate = 28;
                break;
            case 3 :
                dates = new String[]{ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
                        "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25",
                        "26", "27", "28", "29", "30", "31" };
                displayEndDate = 31;
                break;
            case 4 :
                dates = new String[]{ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
                        "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25",
                        "26", "27", "28", "29", "30" };
                displayEndDate = 30;
                break;
            case 5 :
                dates = new String[]{ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
                        "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25",
                        "26", "27", "28", "29", "30", "31" };
                displayEndDate = 31;
                break;
            case 6 :
                dates = new String[]{ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
                        "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25",
                        "26", "27", "28", "29", "30" };
                displayEndDate = 30;
                break;
            case 7 :
                dates = new String[]{ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
                        "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25",
                        "26", "27", "28", "29", "30", "31" };
                displayEndDate = 31;
                break;
            case 8 :
                dates = new String[]{ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
                        "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25",
                        "26", "27", "28", "29", "30", "31" };
                displayEndDate = 31;
                break;
            case 9 :
                dates = new String[]{ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
                        "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25",
                        "26", "27", "28", "29", "30" };
                displayEndDate = 30;
                break;
            case 10 :
                dates = new String[]{ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
                        "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25",
                        "26", "27", "28", "29", "30", "31" };
                displayEndDate = 31;
                break;
            case 11 :
                dates = new String[]{ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
                        "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25",
                        "26", "27", "28", "29", "30" };
                displayEndDate = 30;
                break;
            case 12 :
                dates = new String[]{ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
                        "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25",
                        "26", "27", "28", "29", "30", "31" };
                displayEndDate = 31;
                break;
        }

        ArrayAdapter<String> dateArray = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dates);
        dateArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        startDateSpinner.setAdapter(dateArray);
        endDateSpinner.setAdapter(dateArray);
        startDateSpinner.setSelection(0); // set to first date
        endDateSpinner.setSelection(endDateSpinner.getCount() - 1); // set to last date
    }

    // Updates all expense values on the screen, in the correct display currency (total amount + individual categories)
    private void updateExpenseValuesDisplayed() {
        TextView accommodationExpenseDisplay = (TextView) findViewById(R.id.amount_categoryBreakdown_accommodation);
        TextView foodExpenseDisplay = (TextView) findViewById(R.id.amount_categoryBreakdown_food);
        TextView giftsExpenseDisplay = (TextView) findViewById(R.id.amount_categoryBreakdown_gifts);
        TextView leisureExpenseDisplay = (TextView) findViewById(R.id.amount_categoryBreakdown_leisure);
        TextView miscExpenseDisplay = (TextView) findViewById(R.id.amount_categoryBreakdown_misc);
        TextView shoppingExpenseDisplay = (TextView) findViewById(R.id.amount_categoryBreakdown_shopping);
        TextView travelExpenseDisplay = (TextView) findViewById(R.id.amount_categoryBreakdown_travel);
        BigDecimal accommodationExpense;
        BigDecimal foodExpense;
        BigDecimal giftsExpense;
        BigDecimal leisureExpense;
        BigDecimal miscExpense;
        BigDecimal shoppingExpense;
        BigDecimal travelExpense;

        if (displayCurrency.equals("EUR")) {
            accommodationExpenseDisplay.setText(R.string.str_euroSign);
            foodExpenseDisplay.setText(R.string.str_euroSign);
            giftsExpenseDisplay.setText(R.string.str_euroSign);
            leisureExpenseDisplay.setText(R.string.str_euroSign);
            miscExpenseDisplay.setText(R.string.str_euroSign);
            shoppingExpenseDisplay.setText(R.string.str_euroSign);
            travelExpenseDisplay.setText(R.string.str_euroSign);
            expenditureDisplay.setText(R.string.str_euroSign);
        } else { // display in SGD
            accommodationExpenseDisplay.setText(R.string.str_dollarSign);
            foodExpenseDisplay.setText(R.string.str_dollarSign);
            giftsExpenseDisplay.setText(R.string.str_dollarSign);
            leisureExpenseDisplay.setText(R.string.str_dollarSign);
            miscExpenseDisplay.setText(R.string.str_dollarSign);
            shoppingExpenseDisplay.setText(R.string.str_dollarSign);
            travelExpenseDisplay.setText(R.string.str_dollarSign);
            expenditureDisplay.setText(R.string.str_dollarSign);
        }
        
        String country = (String) selectCountryDisplay.getItemAtPosition(displayCountry);

        accommodationExpense = database.getCategoryExpenditure("accommodation", displayMonth, displayStartDate, displayEndDate, displayCurrency, country);
        foodExpense = database.getCategoryExpenditure("food", displayMonth, displayStartDate, displayEndDate, displayCurrency, country);
        giftsExpense = database.getCategoryExpenditure("gifts", displayMonth, displayStartDate, displayEndDate, displayCurrency, country);
        leisureExpense = database.getCategoryExpenditure("leisure", displayMonth, displayStartDate, displayEndDate, displayCurrency, country);
        miscExpense = database.getCategoryExpenditure("misc", displayMonth, displayStartDate, displayEndDate, displayCurrency, country);
        shoppingExpense = database.getCategoryExpenditure("shopping", displayMonth, displayStartDate, displayEndDate, displayCurrency, country);
        travelExpense = database.getCategoryExpenditure("travel", displayMonth, displayStartDate, displayEndDate, displayCurrency, country);

        accommodationExpenseDisplay.append(accommodationExpense.toString());
        foodExpenseDisplay.append(foodExpense.toString());
        giftsExpenseDisplay.append(giftsExpense.toString());
        leisureExpenseDisplay.append(leisureExpense.toString());
        miscExpenseDisplay.append(miscExpense.toString());
        shoppingExpenseDisplay.append(shoppingExpense.toString());
        travelExpenseDisplay.append(travelExpense.toString());

        BigDecimal totalExpenditure = accommodationExpense.add(foodExpense).add(giftsExpense).add(leisureExpense).
                add(miscExpense).add(shoppingExpense).add(travelExpense);
        expenditureDisplay.append(totalExpenditure.toString());

    }

    // Returns the Spinner index at which the specified country is stored
    private int getCountrySpinnerIndex(String displayCountry) {
        for (int i = 0; i < selectCountryDisplay.getCount(); i++) {
            if (selectCountryDisplay.getItemAtPosition(i).toString().equalsIgnoreCase(displayCountry)) {
                return i;
            }
        }
        return -1;
    }

}

