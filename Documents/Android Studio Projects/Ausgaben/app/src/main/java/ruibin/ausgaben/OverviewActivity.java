package ruibin.ausgaben;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.math.BigDecimal;
import java.util.Calendar;

import ruibin.ausgaben.database.DatabaseExpenses;

public class OverviewActivity extends Activity {

    private DatabaseExpenses database;
    private int displayMonth;
    private int displayCountry;
    private String displayCurrency = "EUR"; // default display is EUR

    // XML resources
    private TextView expenditureDisplay;
    private Spinner selectMonthDisplay;
    private Spinner selectCountryDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        // Initialisation methods
        openDatabase();
        initialiseWidgets();
        setMonthSelection();
        setTextViews();

        // Populate data
        updateExpenseValuesDisplayed(); // default display currency is EUR

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

        selectCountryDisplay = (Spinner) findViewById(R.id.spn_selectCountryDisplay);
        selectCountryDisplay.setOnItemSelectedListener(selectCountryListener);
    }

    // Sets the default display to the current month
    private void setMonthSelection() {
        int displayMonth = getIntent().getIntExtra("month", -1);
        if (displayMonth == -1) {
            Calendar cal = Calendar.getInstance();
            selectMonthDisplay.setSelection(cal.get(Calendar.MONTH) + 1);
        } else {
            selectMonthDisplay.setSelection(displayMonth);
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
            displayMonth = position;
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
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("month", displayMonth);
        startActivity(intent);
    }

    /*
     * ====================== HELPER METHODS ======================
     */

    // Updates all expense values on the screen, in the correct display currency (total amount + individual categories)
    private void updateExpenseValuesDisplayed() {
        TextView foodExpenseDisplay = (TextView) findViewById(R.id.amount_categoryBreakdown_food);
        TextView giftsExpenseDisplay = (TextView) findViewById(R.id.amount_categoryBreakdown_gifts);
        TextView leisureExpenseDisplay = (TextView) findViewById(R.id.amount_categoryBreakdown_leisure);
        TextView miscExpenseDisplay = (TextView) findViewById(R.id.amount_categoryBreakdown_misc);
        TextView shoppingExpenseDisplay = (TextView) findViewById(R.id.amount_categoryBreakdown_shopping);
        TextView travelExpenseDisplay = (TextView) findViewById(R.id.amount_categoryBreakdown_travel);
        BigDecimal foodExpense;
        BigDecimal giftsExpense;
        BigDecimal leisureExpense;
        BigDecimal miscExpense;
        BigDecimal shoppingExpense;
        BigDecimal travelExpense;

        if (displayCurrency.equals("EUR")) {
            foodExpenseDisplay.setText(R.string.str_euroSign);
            giftsExpenseDisplay.setText(R.string.str_euroSign);
            leisureExpenseDisplay.setText(R.string.str_euroSign);
            miscExpenseDisplay.setText(R.string.str_euroSign);
            shoppingExpenseDisplay.setText(R.string.str_euroSign);
            travelExpenseDisplay.setText(R.string.str_euroSign);
            expenditureDisplay.setText(R.string.str_euroSign);
        } else { // display in SGD
            foodExpenseDisplay.setText(R.string.str_dollarSign);
            giftsExpenseDisplay.setText(R.string.str_dollarSign);
            leisureExpenseDisplay.setText(R.string.str_dollarSign);
            miscExpenseDisplay.setText(R.string.str_dollarSign);
            shoppingExpenseDisplay.setText(R.string.str_dollarSign);
            travelExpenseDisplay.setText(R.string.str_dollarSign);
            expenditureDisplay.setText(R.string.str_dollarSign);
        }
        
        String country = (String) selectCountryDisplay.getItemAtPosition(displayCountry);

        leisureExpense = database.getCategoryExpenditure("leisure", displayMonth, displayCurrency, country);
        foodExpense = database.getCategoryExpenditure("food", displayMonth, displayCurrency, country);
        giftsExpense = database.getCategoryExpenditure("gifts", displayMonth, displayCurrency, country);
        miscExpense = database.getCategoryExpenditure("misc", displayMonth, displayCurrency, country);
        shoppingExpense = database.getCategoryExpenditure("shopping", displayMonth, displayCurrency, country);
        travelExpense = database.getCategoryExpenditure("travel", displayMonth, displayCurrency, country);

        foodExpenseDisplay.append(foodExpense.toString());
        giftsExpenseDisplay.append(giftsExpense.toString());
        leisureExpenseDisplay.append(leisureExpense.toString());
        miscExpenseDisplay.append(miscExpense.toString());
        shoppingExpenseDisplay.append(shoppingExpense.toString());
        travelExpenseDisplay.append(travelExpense.toString());

        BigDecimal totalExpenditure = leisureExpense.add(foodExpense).add(giftsExpense).add(miscExpense)
                .add(shoppingExpense).add(travelExpense);
        expenditureDisplay.append(totalExpenditure.toString());

    }

}
