package ruibin.ausgaben;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

import ruibin.ausgaben.database.DatabaseExpenses;
import ruibin.ausgaben.database.Expense;

public class DetailsActivity extends ListActivity {

    private DatabaseExpenses database;
    private ListView listView;
    private int displayMonth;
    private String displayCountry;

    private Animation animationFadeIn;
    private Animation animationFadeOut;

    // Declaration of Checkboxes - defaulted to true
    private boolean isFoodVisible = true;
    private boolean isGiftsVisible = true;
    private boolean isLeisureVisible = true;
    private boolean isMiscVisibile = true;
    private boolean isShoppingVisible = true;
    private boolean isTravelVisible = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Initialisation methods
        openDatabase();
        setMonthAndCountryDisplay();
        setTextViewsAttributes();
        initFadeAnimations();

        initListView();

    }

    /*
     * ====================== INITIALISATION METHODS ======================
     */

    private void openDatabase() {
        database = new DatabaseExpenses(this);
        database.open();
    }

    // Sets the month for which to display the expenses list
    private void setMonthAndCountryDisplay() {
        displayMonth = getIntent().getIntExtra("displayMonth", -1);
        displayCountry = getIntent().getStringExtra("displayCountry");
    }

    // Sets the attributes for the various TextViews
    private void setTextViewsAttributes() {
        TextView textToggleFilters = (TextView) findViewById(R.id.text_toggleFilters);
        textToggleFilters.setPaintFlags(textToggleFilters.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    // Initialises the fade-in and fade-out animations
    private void initFadeAnimations() {
        animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
        animationFadeOut = AnimationUtils.loadAnimation(this, R.anim.fadeout);
    }

    // Populates the list with the items to be displayed and highlights the added/edited items
    private void initListView() {
        // For highlighting a newly added expense
        long newExpenseId = getIntent().getLongExtra("newExpenseId", -1); // ID of the new expense added
        // For highlighting the edited details of an existing expense
        long editExpenseId = getIntent().getLongExtra("editExpenseId", -1);
        boolean isDateEdited = getIntent().getBooleanExtra("isDateEdited", false);
        boolean isNameEdited = getIntent().getBooleanExtra("isNameEdited", false);
        boolean isCategoryEdited = getIntent().getBooleanExtra("isCategoryEdited", false);
        boolean isAmountEdited = getIntent().getBooleanExtra("isAmountEdited", false);
        boolean isCurrencyEdited = getIntent().getBooleanExtra("isCurrencyEdited", false);
        boolean isImagePathEdited = getIntent().getBooleanExtra("isImagePathEdited", false);

        DetailsAdapter adapter;
        if (newExpenseId != -1) {
            adapter = new DetailsAdapter(this, displayData(), newExpenseId);
        } else if (editExpenseId != -1) {
            adapter = new DetailsAdapter(this, displayData(), editExpenseId, isDateEdited,
                    isNameEdited, isCategoryEdited, isAmountEdited, isCurrencyEdited, isImagePathEdited);
        } else {
            adapter = new DetailsAdapter(this, displayData());
        }

        setListAdapter(adapter);

        initListViewClickHandler();
    }

    /*
     * ====================== CLICK HANDLERS ======================
     */

    @Override
    public void onBackPressed() {
        if (getIntent().getStringExtra("source") != null) {
            Intent intent = new Intent(this, ExpenseActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, OverviewActivity.class);
            intent.putExtra("month", displayMonth);
            intent.putExtra("country", displayCountry);
            startActivity(intent);
        }
    }

    // Initialises the click handler for the ListView
    public void initListViewClickHandler() {
        listView = getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(DetailsActivity.this, ExpenseActivity.class);
            Expense expense = (Expense) parent.getItemAtPosition(position);
            Bundle bundle = new Bundle();

            // Inserting the expense details into the bundle
            bundle.putLong("id", expense.getId());
            bundle.putLong("date", expense.getDate());
            bundle.putString("name", expense.getName());
            bundle.putString("category", expense.getCategory());
            bundle.putString("amount", expense.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            bundle.putString("currency", expense.getCurrency());
            bundle.putString("country", expense.getCountry());
            bundle.putString("imagepath", expense.getImagePath());

                // Insert the display month/country settings into the bundle
            bundle.putInt("displayMonth", displayMonth);
            bundle.putString("displayCountry", displayCountry);

            intent.putExtras(bundle);
            startActivity(intent);
            }
        });
    }

    // Toggles the display of the category filters
    public void onClickToggleDisplayOfFilters(View view) {
        // Toggles the wording
        TextView textToggleFilters = (TextView) findViewById(R.id.text_toggleFilters);
        if (textToggleFilters.getText().equals(getResources().getString(R.string.str_showFilters))) {
            textToggleFilters.setText(R.string.str_hideFilters);
        } else {
            textToggleFilters.setText(R.string.str_showFilters);
        }

        // Toggles the display of the checkboxes and the list
        CheckBox cbFood = (CheckBox) findViewById(R.id.checkbox_food);
        CheckBox cbGifts = (CheckBox) findViewById(R.id.checkbox_gifts);
        CheckBox cbLeisure = (CheckBox) findViewById(R.id.checkbox_leisure);
        CheckBox cbMisc = (CheckBox) findViewById(R.id.checkbox_misc);
        CheckBox cbShopping = (CheckBox) findViewById(R.id.checkbox_shopping);
        CheckBox cbTravel = (CheckBox) findViewById(R.id.checkbox_travel);

        if (cbFood.getVisibility() == View.VISIBLE) {
            cbFood.startAnimation(animationFadeOut);
            cbGifts.startAnimation(animationFadeOut);
            cbLeisure.startAnimation(animationFadeOut);
            cbMisc.startAnimation(animationFadeOut);
            cbShopping.startAnimation(animationFadeOut);
            cbTravel.startAnimation(animationFadeOut);
            cbFood.setVisibility(View.GONE);
            cbGifts.setVisibility(View.GONE);
            cbLeisure.setVisibility(View.GONE);
            cbMisc.setVisibility(View.GONE);
            cbShopping.setVisibility(View.GONE);
            cbTravel.setVisibility(View.GONE);
            // shiftListPosition(0);
        } else {
            cbFood.startAnimation(animationFadeIn);
            cbGifts.startAnimation(animationFadeIn);
            cbLeisure.startAnimation(animationFadeIn);
            cbMisc.startAnimation(animationFadeIn);
            cbShopping.startAnimation(animationFadeIn);
            cbTravel.startAnimation(animationFadeIn);
            cbFood.setVisibility(View.VISIBLE);
            cbGifts.setVisibility(View.VISIBLE);
            cbLeisure.setVisibility(View.VISIBLE);
            cbMisc.setVisibility(View.VISIBLE);
            cbShopping.setVisibility(View.VISIBLE);
            cbTravel.setVisibility(View.VISIBLE);
            //shiftListPosition(250);
        }
    }

    // Toggles the list display accordingly based on the (un)selected filters
    public void onClickToggleFilters(View view) {
        switch (view.getId()) {
            case R.id.checkbox_food :
                isFoodVisible = !isFoodVisible;
                break;
            case R.id.checkbox_gifts :
                isGiftsVisible = !isGiftsVisible;
                break;
            case R.id.checkbox_leisure :
                isLeisureVisible = !isLeisureVisible;
                break;
            case R.id.checkbox_misc :
                isMiscVisibile = !isMiscVisibile;
                break;
            case R.id.checkbox_shopping :
                isShoppingVisible = !isShoppingVisible;
                break;
            case R.id.checkbox_travel :
                isTravelVisible = !isTravelVisible;
                break;
        }
        updateListView();
    }

    /*
     * ====================== HELPER METHODS ======================
     */

    // Updates the displayed list based on the category filters
    private void updateListView() {
        boolean[] filters = {isFoodVisible, isGiftsVisible, isLeisureVisible,
                isMiscVisibile, isShoppingVisible, isTravelVisible};

        DetailsAdapter adapter = new DetailsAdapter(this, displayData(filters));
        setListAdapter(adapter);
    }

    private ArrayList<Expense> displayData() {
        ArrayList<Expense> expenseList = database.getExpensesList(displayMonth, displayCountry);
        expenseList = sortMostRecentFirst(expenseList);
        return expenseList;
    }

    // Overloaded method to handle the category filters
    private ArrayList<Expense> displayData(boolean[] filters) {
        ArrayList<Expense> expenseList = database.getExpensesList(displayMonth, displayCountry);
        expenseList = sortMostRecentFirst(expenseList);

        if (!filters[0]) {
            expenseList = filterList(expenseList, "food");
        }
        if (!filters[1]) {
            expenseList = filterList(expenseList, "gifts");
        }
        if (!filters[2]) {
            expenseList = filterList(expenseList, "leisure");
        }
        if (!filters[3]) {
            expenseList = filterList(expenseList, "misc");
        }
        if (!filters[4]) {
            expenseList = filterList(expenseList, "shopping");
        }
        if (!filters[5]) {
            expenseList = filterList(expenseList, "travel");
        }

        return expenseList;
    }

    // Sorts the list, with the most recent date displayed on top
    // If 2 items are of the same date, the item that was added later is displayed above
    private ArrayList<Expense> sortMostRecentFirst(ArrayList<Expense> list) {
        Collections.sort(list, new Comparator<Expense>() {
           @Override
            public int compare(Expense ex1, Expense ex2) {
               Date ex1Date = new Date(ex1.getDate());
               Date ex2Date = new Date(ex2.getDate());

               if (!areDatesSimilar(ex1Date, ex2Date)) {
                   return ex2Date.compareTo(ex1Date);
               } else { // dates are same, then sort by newest first (i.e. larger ID first)
                   return Long.compare(ex2.getId(), ex1.getId());
               }
           }
        });

        return list;
    }

    /*
     * ====================== SECONDARY HELPER METHODS ======================
     */

    private ArrayList<Expense> filterList(ArrayList<Expense> list, String category) {
        Iterator<Expense> it = list.iterator();
        while (it.hasNext()) {
            if (it.next().getCategory().equalsIgnoreCase(category)) {
                it.remove();
            }
        }

        return list;
    }

    private boolean areDatesSimilar(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);

        return cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

}
