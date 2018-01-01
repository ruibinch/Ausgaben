package com.ausgaben.misc;

import android.content.Intent;
import android.content.res.Resources;

import com.ausgaben.R;

/**
 * Created by Ruibin on 1/1/2018.
 */

public class IntentHelper {

    private Intent intent;
    private Resources res;

    public IntentHelper(Intent intent) {
        this.intent = intent;
        this.res = App.getContext().getResources();
    }

    public void addNewExpenseId(long newExpenseId) {
        intent.putExtra(res.getString(R.string.data_newExpenseId), newExpenseId);
    }

    public void addEditExpenseId(long editExpenseId) {
        intent.putExtra(res.getString(R.string.data_editExpenseId), editExpenseId);
    }

    public void addSourceActivity(String source) {
        intent.putExtra(res.getString(R.string.data_source), source);
    }

    public void addDisplaySettings(int displayMonth, String displayCountry, int displayStartDate, int displayEndDate) {
        addDisplayMonthAndDatesSettings(displayMonth, displayStartDate, displayEndDate);
        addDisplayCountry(displayCountry);
    }

    public void addDisplayMonthAndDatesSettings(int displayMonth, int displayStartDate, int displayEndDate) {
        intent.putExtra(res.getString(R.string.data_displayMonth), displayMonth);
        intent.putExtra(res.getString(R.string.data_displayStartDate), displayStartDate);
        intent.putExtra(res.getString(R.string.data_displayEndDate), displayEndDate);
    }

    public void addDisplayCountry(String displayCountry) {
        intent.putExtra(res.getString(R.string.data_displayCountry), displayCountry);
    }

    public void addExpenseDetails(long id, long date, String name, String category, String amount, String currency, String country, String city, String imagepath) {
        intent.putExtra(res.getString(R.string.data_id), id);
        intent.putExtra(res.getString(R.string.data_date), date);
        intent.putExtra(res.getString(R.string.data_name), name);
        intent.putExtra(res.getString(R.string.data_category), category);
        intent.putExtra(res.getString(R.string.data_amount), amount);
        intent.putExtra(res.getString(R.string.data_currency), currency);
        intent.putExtra(res.getString(R.string.data_country), country);
        intent.putExtra(res.getString(R.string.data_city), city);
        intent.putExtra(res.getString(R.string.data_imagepath), imagepath);
    }

    public void addScrollSettings(int scrollIndex, int scrollOffset) {
        intent.putExtra(res.getString(R.string.data_scrollIndex), scrollIndex);
        intent.putExtra(res.getString(R.string.data_scrollOffset), scrollOffset);
    }

    public void addEditedInfo(boolean isDateEdited, boolean isNameEdited, boolean isCategoryEdited, boolean isAmountEdited,
                              boolean isCurrencyEdited, boolean isCountryEdited, boolean isImagePathEdited) {
        intent.putExtra(res.getString(R.string.data_isDateEdited), isDateEdited);
        intent.putExtra(res.getString(R.string.data_isNameEdited), isNameEdited);
        intent.putExtra(res.getString(R.string.data_isCategoryEdited), isCategoryEdited);
        intent.putExtra(res.getString(R.string.data_isAmountEdited), isAmountEdited);
        intent.putExtra(res.getString(R.string.data_isCurrencyEdited), isCurrencyEdited);
        intent.putExtra(res.getString(R.string.data_isCountryEdited), isCountryEdited);
        intent.putExtra(res.getString(R.string.data_isImagePathEdited), isImagePathEdited);
    }

    public void addFilterSettings(boolean isAccommodationVisible, boolean isFoodVisible, boolean isGiftsVisible, boolean isLeisureVisible,
                                  boolean isMiscVisible, boolean isShoppingVisible, boolean isTravelVisible) {
        intent.putExtra(res.getString(R.string.data_isAccommodationVisible), isAccommodationVisible);
        intent.putExtra(res.getString(R.string.data_isFoodVisible), isFoodVisible);
        intent.putExtra(res.getString(R.string.data_isGiftsVisible), isGiftsVisible);
        intent.putExtra(res.getString(R.string.data_isLeisureVisible), isLeisureVisible);
        intent.putExtra(res.getString(R.string.data_isMiscVisible), isMiscVisible);
        intent.putExtra(res.getString(R.string.data_isShoppingVisible), isShoppingVisible);
        intent.putExtra(res.getString(R.string.data_isTravelVisible), isTravelVisible);
    }
}
