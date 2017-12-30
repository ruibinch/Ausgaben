package com.ausgaben;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import java.util.ArrayList;
import java.util.Collections;

import com.ausgaben.database.DatabaseExpenses;

/**
 * Created by Ruibin on 23/4/2017.
 */

public class OverviewSettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    private DatabaseExpenses database;

    public static final String KEY_PREF_MONTH = "pref_month";
    public static final String KEY_PREF_STARTDATE = "pref_startDate";
    public static final String KEY_PREF_ENDDATE = "pref_endDate";
    public static final String KEY_PREF_COUNTRY = "pref_country";

    private ListPreference lpMonth;
    private ListPreference lpStartDate ;
    private ListPreference lpEndDate ;
    private ListPreference lpCountry;

    private String pref_month;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        openDatabase();
        initPreferencesData();
    }

    /*
     * ====================== INITIALISATION METHODS ======================
     */

    private void openDatabase() {
        database = new DatabaseExpenses(getActivity());
        database.open();
    }

    private void initPreferencesData() {
        lpMonth = (ListPreference) findPreference(KEY_PREF_MONTH);
        lpStartDate = (ListPreference) findPreference(KEY_PREF_STARTDATE);
        lpEndDate = (ListPreference) findPreference(KEY_PREF_ENDDATE);
        lpCountry = (ListPreference) findPreference(KEY_PREF_COUNTRY);

        // Obtain the previously saved month value
        // pref_month = lpMonth.getValue();

        //setDateRangeData(lpStartDate, pref_month);
        //setDateRangeData(lpEndDate, pref_month);
        setCountryData(lpCountry);
    }

    /*
     * ====================== CLICK HANDLERS ======================
     */

    @Override
    // If the pref month is changed, the start and end date ranges will change accordingly
    public boolean onPreferenceClick(Preference preference) {
        /*
        if ((preference instanceof ListPreference) && (preference.getKey().equals(KEY_PREF_MONTH))) {
            pref_month = ((ListPreference) preference).getValue();
            //setDateRangeData(lpStartDate, pref_month);
            //setDateRangeData(lpEndDate, pref_month);
            return true;
        }
        */

        return false;

    }

    /*
     * ====================== HELPER METHODS ======================
     */

    /*
    private void setDateRangeData(ListPreference lp, String month) {
        CharSequence[] entries = new CharSequence[]{};
        String lastValue = "";

        switch (month) {
            case "All" :
                break;
            case "Jan" :
                entries = new CharSequence[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
                        "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25",
                        "26", "27", "28", "29", "30", "31"};
                lastValue = "31";
                break;
            case "Feb" :
                entries = new CharSequence[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
                        "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25",
                        "26", "27", "28"};
                lastValue = "28";
                break;
            case "Mar" :
                entries = new CharSequence[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
                        "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25",
                        "26", "27", "28", "29", "30", "31"};
                lastValue = "31";
                break;
            case "Apr" :
                entries = new CharSequence[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
                        "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25",
                        "26", "27", "28", "29", "30"};
                lastValue = "30";
                break;
            case "May" :
                entries = new CharSequence[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
                        "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25",
                        "26", "27", "28", "29", "30", "31"};
                lastValue = "31";
                break;
            case "Jun" :
                entries = new CharSequence[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
                        "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25",
                        "26", "27", "28", "29", "30"};
                lastValue = "30";
                break;
            case "Jul" :
                entries = new CharSequence[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
                        "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25",
                        "26", "27", "28", "29", "30"};
                lastValue = "31";
                break;
            case "Aug" :
                entries = new CharSequence[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
                        "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25",
                        "26", "27", "28", "29", "30"};
                lastValue = "31";
                break;
            case "default" :
                break;
        }

        lp.setEntries(entries);
        lp.setEntryValues(entries);
        if (lp.getKey().equals(KEY_PREF_STARTDATE)) {
            lp.setDefaultValue("1");
        } else if (lp.getKey().equals(KEY_PREF_ENDDATE)) {
            lp.setDefaultValue(lastValue);
        }
    }
    */

    private void setCountryData(ListPreference lp) {
        ArrayList<String> countriesList = database.getCountriesList();
        Collections.sort(countriesList); // Sorts in alphabetical order
        countriesList.add(0, getResources().getString(R.string.str_all));

        CharSequence[] entries = countriesList.toArray(new CharSequence[countriesList.size()]);
        lp.setEntries(entries);
        lp.setEntryValues(entries);
        lp.setDefaultValue(getResources().getString(R.string.str_all));
    }
}
