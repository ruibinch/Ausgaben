package ruibin.ausgaben;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

import ruibin.ausgaben.database.DatabaseExpenses;

public class OverviewActivity extends Activity implements OnItemSelectedListener {

    private DatabaseExpenses database;
    private int selectedMonth; // Stores the currently selected month

    // XML resources
    private TextView expenditureDisplay;
    private Spinner selectMonth;

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
        updateExpensesCategoriesBreakdown();

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
        selectMonth = (Spinner) findViewById(R.id.spn_selectMonth);
        selectMonth.setOnItemSelectedListener(this);
    }

    // Sets the default display to the current month
    private void setMonthSelection() {
        int displayMonth = getIntent().getIntExtra("month", -1);
        if (displayMonth == -1) {
            Calendar cal = Calendar.getInstance();
            selectMonth.setSelection(cal.get(Calendar.MONTH) + 1);
        } else {
            selectMonth.setSelection(displayMonth);
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

    // OnItemSelectedListener implementation - Spinner to select month
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        selectedMonth = pos;
        expenditureDisplay.setText(R.string.str_dollarSign);
        expenditureDisplay.append(database.getMonthExpenditure(pos).toString());

        updateExpensesCategoriesBreakdown();
    }

    // OnItemSelectedListener implementation
    public void onNothingSelected(AdapterView<?> parent) {
    }

    // Click handler for 'View Expenses List' button
    public void onClickViewExpensesList(View view) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("month", selectedMonth);
        startActivity(intent);
    }

    /*
     * ====================== HELPER METHODS ======================
     */

    private void updateExpensesCategoriesBreakdown() {
        TextView amountEntertainment = (TextView) findViewById(R.id.amount_categoryBreakdown_entertainment);
        TextView amountFood = (TextView) findViewById(R.id.amount_categoryBreakdown_food);
        TextView amountGifts = (TextView) findViewById(R.id.amount_categoryBreakdown_gifts);
        TextView amountMisc = (TextView) findViewById(R.id.amount_categoryBreakdown_misc);
        TextView amountShopping = (TextView) findViewById(R.id.amount_categoryBreakdown_shopping);
        TextView amountTravel = (TextView) findViewById(R.id.amount_categoryBreakdown_travel);

        amountEntertainment.setText(R.string.str_dollarSign);
        amountFood.setText(R.string.str_dollarSign);
        amountGifts.setText(R.string.str_dollarSign);
        amountMisc.setText(R.string.str_dollarSign);
        amountShopping.setText(R.string.str_dollarSign);
        amountTravel.setText(R.string.str_dollarSign);

        amountEntertainment.append(database.getCategoryExpenditure("entertainment", selectedMonth).toString());
        amountFood.append(database.getCategoryExpenditure("food", selectedMonth).toString());
        amountGifts.append(database.getCategoryExpenditure("gifts", selectedMonth).toString());
        amountMisc.append(database.getCategoryExpenditure("misc", selectedMonth).toString());
        amountShopping.append(database.getCategoryExpenditure("shopping", selectedMonth).toString());
        amountTravel.append(database.getCategoryExpenditure("travel", selectedMonth).toString());
    }

}

