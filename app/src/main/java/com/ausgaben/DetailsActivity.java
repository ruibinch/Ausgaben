package com.ausgaben;

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

import com.ausgaben.database.DatabaseExpenses;
import com.ausgaben.database.Expense;

public class DetailsActivity extends ListActivity {

    private DatabaseExpenses database;
    private ListView listView;

    // Display parameters from OverviewActivity
    private int displayMonth;
    private String displayCountry;
    private int displayStartDate;
    private int displayEndDate;

    // Details from ExpenseActivity, if DetailsActivity is being displayed after an expense is added/edited
    long newExpenseId;
    long editExpenseId;
    boolean isDateEdited;
    boolean isNameEdited;
    boolean isCategoryEdited;
    boolean isAmountEdited;
    boolean isCurrencyEdited;
    boolean isImagePathEdited;

    // Previous saved scroll position, if applicable
    private int scrollIndex;
    private int scrollOffset;

    private Animation animationFadeIn;
    private Animation animationFadeOut;

    // Declaration of Checkboxes
    private boolean isAccommodationVisible;
    private boolean isFoodVisible;
    private boolean isGiftsVisible;
    private boolean isLeisureVisible;
    private boolean isMiscVisible;
    private boolean isShoppingVisible;
    private boolean isTravelVisible;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Initialisation methods
        openDatabase();
        initFadeAnimations();
        setDisplayParameters();
        setTextViewsAttributes();

        // More initialisation methods, applicable only if the Intent originated from ExpenseActivity
        getListScrollPosition();
        getFilterSettings();
        getEditSettings();

        initListView();
    }

    /*
     * ====================== INITIALISATION METHODS ======================
     */

    private void openDatabase() {
        database = new DatabaseExpenses(this);
        database.open();
    }

    // Initialises the fade-in and fade-out animations
    private void initFadeAnimations() {
        animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
        animationFadeOut = AnimationUtils.loadAnimation(this, R.anim.fadeout);
    }

    private void setDisplayParameters() {
        displayMonth = getIntent().getIntExtra("displayMonth", -1);
        displayCountry = getIntent().getStringExtra("displayCountry");
        displayStartDate = getIntent().getIntExtra("displayStartDate", -1);
        displayEndDate = getIntent().getIntExtra("displayEndDate", -1);
    }

    // Sets the attributes for the 'Show Filters' TextView
    private void setTextViewsAttributes() {
        TextView textToggleFilters = (TextView) findViewById(R.id.text_toggleFilters);
        textToggleFilters.setPaintFlags(textToggleFilters.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    // Sets the saved scroll position of the ListView
    private void getListScrollPosition() {
        scrollIndex = getIntent().getIntExtra("scrollIndex", -1);
        scrollOffset = getIntent().getIntExtra("scrollOffset", -1);
    }

    // Sets the filter settings and the corresponding checkboxes
    private void getFilterSettings() {
        isAccommodationVisible = getIntent().getBooleanExtra("isAccommodationVisible", true);
        isFoodVisible = getIntent().getBooleanExtra("isFoodVisible", true);
        isGiftsVisible = getIntent().getBooleanExtra("isGiftsVisible", true);
        isLeisureVisible = getIntent().getBooleanExtra("isLeisureVisible", true);
        isMiscVisible = getIntent().getBooleanExtra("isMiscVisible", true);
        isShoppingVisible = getIntent().getBooleanExtra("isShoppingVisible", true);
        isTravelVisible = getIntent().getBooleanExtra("isTravelVisible", true);

        CheckBox cbAccommodation = (CheckBox) findViewById(R.id.checkbox_accommodation);
        CheckBox cbFood = (CheckBox) findViewById(R.id.checkbox_food);
        CheckBox cbGifts = (CheckBox) findViewById(R.id.checkbox_gifts);
        CheckBox cbLeisure = (CheckBox) findViewById(R.id.checkbox_leisure);
        CheckBox cbMisc = (CheckBox) findViewById(R.id.checkbox_misc);
        CheckBox cbShopping = (CheckBox) findViewById(R.id.checkbox_shopping);
        CheckBox cbTravel = (CheckBox) findViewById(R.id.checkbox_travel);

        if (isAccommodationVisible)
            cbAccommodation.setChecked(true);
        if (isFoodVisible)
            cbFood.setChecked(true);
        if (isGiftsVisible)
            cbGifts.setChecked(true);
        if (isLeisureVisible)
            cbLeisure.setChecked(true);
        if (isMiscVisible)
            cbMisc.setChecked(true);
        if (isShoppingVisible)
            cbShopping.setChecked(true);
        if (isTravelVisible)
            cbTravel.setChecked(true);

        // if any are false, then display the filter settings
        if (!isAccommodationVisible || !isFoodVisible || !isGiftsVisible || !isLeisureVisible ||
                !isMiscVisible || !isShoppingVisible || !isTravelVisible) {
            TextView textToggleFilters = (TextView) findViewById(R.id.text_toggleFilters);
            textToggleFilters.performClick();
        }
    }

    // Obtains the edit settings from the intent from ExpenseActivity, if applicable
    private void getEditSettings() {
        // For highlighting a newly added expense
        newExpenseId = getIntent().getLongExtra("newExpenseId", -1); // ID of the new expense added
        // For highlighting the edited details of an existing expense
        editExpenseId = getIntent().getLongExtra("editExpenseId", -1); // ID of the edied expense
        isDateEdited = getIntent().getBooleanExtra("isDateEdited", false);
        isNameEdited = getIntent().getBooleanExtra("isNameEdited", false);
        isCategoryEdited = getIntent().getBooleanExtra("isCategoryEdited", false);
        isAmountEdited = getIntent().getBooleanExtra("isAmountEdited", false);
        isCurrencyEdited = getIntent().getBooleanExtra("isCurrencyEdited", false);
        isImagePathEdited = getIntent().getBooleanExtra("isImagePathEdited", false);
    }

    // Initiates the ListView and sets it to the appropriate scroll position
    private void initListView() {
        updateListView();

        // Sets the list to the previously saved scroll position, if applicable
        if (scrollIndex != -1 && scrollOffset != -1) {
            listView = getListView();
            listView.setSelectionFromTop(scrollIndex, scrollOffset);
        }

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
            intent.putExtra("displayMonth", displayMonth);
            intent.putExtra("displayCountry", displayCountry);
            intent.putExtra("displayStartDate", displayStartDate);
            intent.putExtra("displayEndDate", displayEndDate);
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

            // Save the current scroll position of the list
            int scrollIndex = listView.getFirstVisiblePosition(); // index of the top-most visible item
            View v = listView.getChildAt(0);
            int scrollOffset = (v == null) ? 0 : (v.getTop() - listView.getPaddingTop());

            // Inserting the expense details into the bundle
            bundle.putLong("id", expense.getId());
            bundle.putLong("date", expense.getDate());
            bundle.putString("name", expense.getName());
            bundle.putString("category", expense.getCategory());
            bundle.putString("amount", expense.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            bundle.putString("currency", expense.getCurrency());
            bundle.putString("country", expense.getCountry());
            bundle.putString("imagepath", expense.getImagePath());

            // Insert the display month/country/date settings into the bundle
            bundle.putInt("displayMonth", displayMonth);
            bundle.putString("displayCountry", displayCountry);
            bundle.putInt("displayStartDate", displayStartDate);
            bundle.putInt("displayEndDate", displayEndDate);
                
            // Insert the filter settings into the bundle
            bundle.putBoolean("isAccommodationVisible", isAccommodationVisible);
            bundle.putBoolean("isFoodVisible", isFoodVisible);
            bundle.putBoolean("isGiftsVisible", isGiftsVisible);
            bundle.putBoolean("isLeisureVisible", isLeisureVisible);
            bundle.putBoolean("isMiscVisible", isMiscVisible);
            bundle.putBoolean("isShoppingVisible", isShoppingVisible);
            bundle.putBoolean("isTravelVisible", isTravelVisible);

            // Insert the scroll position details into the bundle
            bundle.putInt("scrollIndex", scrollIndex);
            bundle.putInt("scrollOffset", scrollOffset);

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

        // Toggles the display of the checkboxes
        CheckBox cbAccommodation = (CheckBox) findViewById(R.id.checkbox_accommodation);
        CheckBox cbFood = (CheckBox) findViewById(R.id.checkbox_food);
        CheckBox cbGifts = (CheckBox) findViewById(R.id.checkbox_gifts);
        CheckBox cbLeisure = (CheckBox) findViewById(R.id.checkbox_leisure);
        CheckBox cbMisc = (CheckBox) findViewById(R.id.checkbox_misc);
        CheckBox cbShopping = (CheckBox) findViewById(R.id.checkbox_shopping);
        CheckBox cbTravel = (CheckBox) findViewById(R.id.checkbox_travel);

        if (cbAccommodation.getVisibility() == View.VISIBLE) {
            cbAccommodation.startAnimation(animationFadeOut);
            cbFood.startAnimation(animationFadeOut);
            cbGifts.startAnimation(animationFadeOut);
            cbLeisure.startAnimation(animationFadeOut);
            cbMisc.startAnimation(animationFadeOut);
            cbShopping.startAnimation(animationFadeOut);
            cbTravel.startAnimation(animationFadeOut);
            cbAccommodation.setVisibility(View.GONE);
            cbFood.setVisibility(View.GONE);
            cbGifts.setVisibility(View.GONE);
            cbLeisure.setVisibility(View.GONE);
            cbMisc.setVisibility(View.GONE);
            cbShopping.setVisibility(View.GONE);
            cbTravel.setVisibility(View.GONE);
        } else {
            cbAccommodation.startAnimation(animationFadeIn);
            cbFood.startAnimation(animationFadeIn);
            cbGifts.startAnimation(animationFadeIn);
            cbLeisure.startAnimation(animationFadeIn);
            cbMisc.startAnimation(animationFadeIn);
            cbShopping.startAnimation(animationFadeIn);
            cbTravel.startAnimation(animationFadeIn);
            cbAccommodation.setVisibility(View.VISIBLE);
            cbFood.setVisibility(View.VISIBLE);
            cbGifts.setVisibility(View.VISIBLE);
            cbLeisure.setVisibility(View.VISIBLE);
            cbMisc.setVisibility(View.VISIBLE);
            cbShopping.setVisibility(View.VISIBLE);
            cbTravel.setVisibility(View.VISIBLE);
        }
    }

    // Toggles the list display accordingly based on the (un)selected filters
    public void onClickToggleFilters(View view) {
        switch (view.getId()) {
            case R.id.checkbox_accommodation :
                isAccommodationVisible = !isAccommodationVisible;
                break;
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
                isMiscVisible = !isMiscVisible;
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
    // Any added/edited expense will remain highlighted in green even if the filters are modified
    private void updateListView() {
        boolean[] filters = {isAccommodationVisible, isFoodVisible, isGiftsVisible, isLeisureVisible,
                isMiscVisible, isShoppingVisible, isTravelVisible};

        DetailsAdapter adapter;
        if (newExpenseId != -1) {
            adapter = new DetailsAdapter(this, displayData(filters), newExpenseId);
        } else if (editExpenseId != -1) {
            adapter = new DetailsAdapter(this, displayData(filters), editExpenseId, isDateEdited,
                    isNameEdited, isCategoryEdited, isAmountEdited, isCurrencyEdited, isImagePathEdited);
        } else {
            adapter = new DetailsAdapter(this, displayData(filters));
        }

        setListAdapter(adapter);
    }

    // Obtains the list of expenses to be displayed based on the category filters
    private ArrayList<Expense> displayData(boolean[] filters) {
        ArrayList<Expense> expenseList = database.getExpensesList(displayMonth, displayCountry, displayStartDate, displayEndDate);
        expenseList = sortMostRecentFirst(expenseList);

        if (!filters[0])
            expenseList = filterList(expenseList, "accommodation");
        if (!filters[1])
            expenseList = filterList(expenseList, "food");
        if (!filters[2])
            expenseList = filterList(expenseList, "gifts");
        if (!filters[3])
            expenseList = filterList(expenseList, "leisure");
        if (!filters[4])
            expenseList = filterList(expenseList, "misc");
        if (!filters[5])
            expenseList = filterList(expenseList, "shopping");
        if (!filters[6])
            expenseList = filterList(expenseList, "travel");

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
