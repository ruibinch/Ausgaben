package com.ausgaben;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class ForexRatesActivity extends AppCompatActivity {

    SharedPreferences mPrefsToggleRates;
    SharedPreferences mPrefsForexRates;
    
    // For monitoring of any changes
    ArrayList<String> currencyList;
    ArrayList<Boolean> oldToggleSettingsList = new ArrayList<Boolean>();
    ArrayList<Boolean> newToggleSettingsList = new ArrayList<Boolean>();
    ArrayList<String> oldForexRatesList = new ArrayList<String>();
    ArrayList<String> newForexRatesList = new ArrayList<String>();
    
    // Checkbox fields of forex rates display
    CheckBox toggleALL;
    CheckBox toggleBAM;
    CheckBox toggleBGN;
    CheckBox toggleBYN;
    CheckBox toggleCHF;
    CheckBox toggleCZK;
    CheckBox toggleDKK;
    CheckBox toggleEUR;
    CheckBox toggleGBP;
    CheckBox toggleHRK;
    CheckBox toggleHUF;
    CheckBox toggleMKD;
    CheckBox toggleNOK;
    CheckBox togglePLN;
    CheckBox toggleRON;
    CheckBox toggleRSD;
    CheckBox toggleSEK;
    CheckBox toggleSGD;
    CheckBox toggleTRY;
    
    // Text fields of forex rates input
    EditText rateALL;
    EditText rateBAM;
    EditText rateBGN;
    EditText rateBYN;
    EditText rateCHF;
    EditText rateCZK;
    EditText rateDKK;
    EditText rateGBP;
    EditText rateHRK;
    EditText rateHUF;
    EditText rateMKD;
    EditText rateNOK;
    EditText ratePLN;
    EditText rateRON;
    EditText rateRSD;
    EditText rateSEK;
    EditText rateSGD;
    EditText rateTRY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forex_rates);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initWidgets();
        loadToggleSettings();
        loadForexRates();
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

    // Initialises the Preferences API and the CheckBox and TextView widgets
    private void initWidgets() {
        mPrefsToggleRates = getSharedPreferences(getString(R.string.pref_toggleRates), MODE_PRIVATE);
        mPrefsForexRates = getSharedPreferences(getString(R.string.pref_forexRates), MODE_PRIVATE);

        currencyList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.arr_currency_entries)));

        toggleALL = (CheckBox) findViewById(R.id.toggle_currencyALL);
        toggleBAM = (CheckBox) findViewById(R.id.toggle_currencyBAM);
        toggleBGN = (CheckBox) findViewById(R.id.toggle_currencyBGN);
        toggleBYN = (CheckBox) findViewById(R.id.toggle_currencyBYN);
        toggleCHF = (CheckBox) findViewById(R.id.toggle_currencyCHF);
        toggleCZK = (CheckBox) findViewById(R.id.toggle_currencyCZK);
        toggleDKK = (CheckBox) findViewById(R.id.toggle_currencyDKK);
        toggleEUR = (CheckBox) findViewById(R.id.toggle_currencyEUR);
        toggleGBP = (CheckBox) findViewById(R.id.toggle_currencyGBP);
        toggleHUF = (CheckBox) findViewById(R.id.toggle_currencyHUF);
        toggleHRK = (CheckBox) findViewById(R.id.toggle_currencyHRK);
        toggleMKD = (CheckBox) findViewById(R.id.toggle_currencyMKD);
        toggleNOK = (CheckBox) findViewById(R.id.toggle_currencyNOK);
        togglePLN = (CheckBox) findViewById(R.id.toggle_currencyPLN);
        toggleRON = (CheckBox) findViewById(R.id.toggle_currencyRON);
        toggleRSD = (CheckBox) findViewById(R.id.toggle_currencyRSD);
        toggleSEK = (CheckBox) findViewById(R.id.toggle_currencySEK);
        toggleSGD = (CheckBox) findViewById(R.id.toggle_currencySGD);
        toggleTRY = (CheckBox) findViewById(R.id.toggle_currencyTRY);
        
        rateALL = (EditText) findViewById(R.id.rate_currencyALL);
        rateBAM = (EditText) findViewById(R.id.rate_currencyBAM);
        rateBGN = (EditText) findViewById(R.id.rate_currencyBGN);
        rateBYN = (EditText) findViewById(R.id.rate_currencyBYN);
        rateCHF = (EditText) findViewById(R.id.rate_currencyCHF);
        rateCZK = (EditText) findViewById(R.id.rate_currencyCZK);
        rateDKK = (EditText) findViewById(R.id.rate_currencyDKK);
        rateGBP = (EditText) findViewById(R.id.rate_currencyGBP);
        rateHUF = (EditText) findViewById(R.id.rate_currencyHUF);
        rateHRK = (EditText) findViewById(R.id.rate_currencyHRK);
        rateMKD = (EditText) findViewById(R.id.rate_currencyMKD);
        rateNOK = (EditText) findViewById(R.id.rate_currencyNOK);
        ratePLN = (EditText) findViewById(R.id.rate_currencyPLN);
        rateRON = (EditText) findViewById(R.id.rate_currencyRON);
        rateRSD = (EditText) findViewById(R.id.rate_currencyRSD);
        rateSEK = (EditText) findViewById(R.id.rate_currencySEK);
        rateSGD = (EditText) findViewById(R.id.rate_currencySGD);
        rateTRY = (EditText) findViewById(R.id.rate_currencyTRY);
    }

    // Loads the toggle settings from the SharedPreferences
    private void loadToggleSettings() {
        for (int i = 0; i < currencyList.size(); i++) {
            //if (!currencyList.get(i).equals("EUR"))
            oldToggleSettingsList.add(mPrefsToggleRates.getBoolean(currencyList.get(i), true));
        }
        
        toggleALL.setChecked(mPrefsToggleRates.getBoolean(getString(R.string.currency_ALL), true));
        toggleBAM.setChecked(mPrefsToggleRates.getBoolean(getString(R.string.currency_BAM), true));
        toggleBGN.setChecked(mPrefsToggleRates.getBoolean(getString(R.string.currency_BGN), true));
        toggleBYN.setChecked(mPrefsToggleRates.getBoolean(getString(R.string.currency_BYN), true));
        toggleCHF.setChecked(mPrefsToggleRates.getBoolean(getString(R.string.currency_CHF), true));
        toggleCZK.setChecked(mPrefsToggleRates.getBoolean(getString(R.string.currency_CZK), true));
        toggleDKK.setChecked(mPrefsToggleRates.getBoolean(getString(R.string.currency_DKK), true));
        toggleEUR.setChecked(mPrefsToggleRates.getBoolean(getString(R.string.currency_EUR), true));
        toggleGBP.setChecked(mPrefsToggleRates.getBoolean(getString(R.string.currency_GBP), true));
        toggleHRK.setChecked(mPrefsToggleRates.getBoolean(getString(R.string.currency_HRK), true));
        toggleHUF.setChecked(mPrefsToggleRates.getBoolean(getString(R.string.currency_HUF), true));
        toggleMKD.setChecked(mPrefsToggleRates.getBoolean(getString(R.string.currency_MKD), true));
        toggleNOK.setChecked(mPrefsToggleRates.getBoolean(getString(R.string.currency_NOK), true));
        togglePLN.setChecked(mPrefsToggleRates.getBoolean(getString(R.string.currency_PLN), true));
        toggleRON.setChecked(mPrefsToggleRates.getBoolean(getString(R.string.currency_RON), true));
        toggleRSD.setChecked(mPrefsToggleRates.getBoolean(getString(R.string.currency_RSD), true));
        toggleSEK.setChecked(mPrefsToggleRates.getBoolean(getString(R.string.currency_SEK), true));
        toggleSGD.setChecked(mPrefsToggleRates.getBoolean(getString(R.string.currency_SGD), true));
        toggleTRY.setChecked(mPrefsToggleRates.getBoolean(getString(R.string.currency_TRY), true));
    }
    
    // Loads the saved rates from the SharedPreferences
    private void loadForexRates() {
        for (int i = 0; i < currencyList.size(); i++) {
            if (!currencyList.get(i).equals(getString(R.string.currency_EUR)))
                oldForexRatesList.add(mPrefsForexRates.getString(currencyList.get(i), ""));
        }
        
        rateALL.setText(mPrefsForexRates.getString(getString(R.string.currency_ALL), ""));
        rateBAM.setText(mPrefsForexRates.getString(getString(R.string.currency_BAM), ""));
        rateBGN.setText(mPrefsForexRates.getString(getString(R.string.currency_BGN), ""));
        rateBYN.setText(mPrefsForexRates.getString(getString(R.string.currency_BYN), ""));
        rateCHF.setText(mPrefsForexRates.getString(getString(R.string.currency_CHF), ""));
        rateCZK.setText(mPrefsForexRates.getString(getString(R.string.currency_CZK), ""));
        rateDKK.setText(mPrefsForexRates.getString(getString(R.string.currency_DKK), ""));
        rateGBP.setText(mPrefsForexRates.getString(getString(R.string.currency_GBP), ""));
        rateHRK.setText(mPrefsForexRates.getString(getString(R.string.currency_HRK), ""));
        rateHUF.setText(mPrefsForexRates.getString(getString(R.string.currency_HUF), ""));
        rateMKD.setText(mPrefsForexRates.getString(getString(R.string.currency_MKD), ""));
        rateNOK.setText(mPrefsForexRates.getString(getString(R.string.currency_NOK), ""));
        ratePLN.setText(mPrefsForexRates.getString(getString(R.string.currency_PLN), ""));
        rateRON.setText(mPrefsForexRates.getString(getString(R.string.currency_RON), ""));
        rateRSD.setText(mPrefsForexRates.getString(getString(R.string.currency_RSD), ""));
        rateSEK.setText(mPrefsForexRates.getString(getString(R.string.currency_SEK), ""));
        rateSGD.setText(mPrefsForexRates.getString(getString(R.string.currency_SGD), ""));
        rateTRY.setText(mPrefsForexRates.getString(getString(R.string.currency_TRY), ""));
    }

    /*
     * ====================== CLICK HANDLERS ======================
     */
    
    @Override
    public void onBackPressed() {
        updateToggleSettings();
        updateForexRates();
        checkForChangesMade();
        
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /*
     * ====================== HELPER METHODS ======================
     */

    // Saves the updated toggle settings of the currencies
    private void updateToggleSettings() {
        System.out.println("togglePLN = " + togglePLN.isChecked());

        newToggleSettingsList.add(toggleALL.isChecked());
        newToggleSettingsList.add(toggleBAM.isChecked());
        newToggleSettingsList.add(toggleBGN.isChecked());
        newToggleSettingsList.add(toggleBYN.isChecked());
        newToggleSettingsList.add(toggleCHF.isChecked());
        newToggleSettingsList.add(toggleCZK.isChecked());
        newToggleSettingsList.add(toggleDKK.isChecked());
        newToggleSettingsList.add(toggleEUR.isChecked());
        newToggleSettingsList.add(toggleGBP.isChecked());
        newToggleSettingsList.add(toggleHRK.isChecked());
        newToggleSettingsList.add(toggleHUF.isChecked());
        newToggleSettingsList.add(toggleMKD.isChecked());
        newToggleSettingsList.add(toggleNOK.isChecked());
        newToggleSettingsList.add(togglePLN.isChecked());
        newToggleSettingsList.add(toggleRON.isChecked());
        newToggleSettingsList.add(toggleRSD.isChecked());
        newToggleSettingsList.add(toggleSEK.isChecked());
        newToggleSettingsList.add(toggleSGD.isChecked());
        newToggleSettingsList.add(toggleTRY.isChecked());
        
        SharedPreferences.Editor mEditor = mPrefsToggleRates.edit();
        mEditor.clear();

        mEditor.putBoolean(getString(R.string.currency_ALL), toggleALL.isChecked());
        mEditor.putBoolean(getString(R.string.currency_BAM), toggleBAM.isChecked());
        mEditor.putBoolean(getString(R.string.currency_BGN), toggleBGN.isChecked());
        mEditor.putBoolean(getString(R.string.currency_BYN), toggleBYN.isChecked());
        mEditor.putBoolean(getString(R.string.currency_CHF), toggleCHF.isChecked());
        mEditor.putBoolean(getString(R.string.currency_CZK), toggleCZK.isChecked());
        mEditor.putBoolean(getString(R.string.currency_DKK), toggleDKK.isChecked());
        mEditor.putBoolean(getString(R.string.currency_EUR), toggleEUR.isChecked());
        mEditor.putBoolean(getString(R.string.currency_GBP), toggleGBP.isChecked());
        mEditor.putBoolean(getString(R.string.currency_HRK), toggleHRK.isChecked());
        mEditor.putBoolean(getString(R.string.currency_HUF), toggleHUF.isChecked());
        mEditor.putBoolean(getString(R.string.currency_MKD), toggleMKD.isChecked());
        mEditor.putBoolean(getString(R.string.currency_NOK), toggleNOK.isChecked());
        mEditor.putBoolean(getString(R.string.currency_PLN), togglePLN.isChecked());
        mEditor.putBoolean(getString(R.string.currency_RON), toggleRON.isChecked());
        mEditor.putBoolean(getString(R.string.currency_RSD), toggleRSD.isChecked());
        mEditor.putBoolean(getString(R.string.currency_SEK), toggleSEK.isChecked());
        mEditor.putBoolean(getString(R.string.currency_SGD), toggleSGD.isChecked());
        mEditor.putBoolean(getString(R.string.currency_TRY), toggleTRY.isChecked());

        mEditor.commit();
    }

    // Saves the updated forex rates
    private void updateForexRates() {
        newForexRatesList.add(rateALL.getText().toString().trim());
        newForexRatesList.add(rateBAM.getText().toString().trim());
        newForexRatesList.add(rateBGN.getText().toString().trim());
        newForexRatesList.add(rateBYN.getText().toString().trim());
        newForexRatesList.add(rateCHF.getText().toString().trim());
        newForexRatesList.add(rateCZK.getText().toString().trim());
        newForexRatesList.add(rateDKK.getText().toString().trim());
        newForexRatesList.add(rateGBP.getText().toString().trim());
        newForexRatesList.add(rateHRK.getText().toString().trim());
        newForexRatesList.add(rateHUF.getText().toString().trim());
        newForexRatesList.add(rateMKD.getText().toString().trim());
        newForexRatesList.add(rateNOK.getText().toString().trim());
        newForexRatesList.add(ratePLN.getText().toString().trim());
        newForexRatesList.add(rateRON.getText().toString().trim());
        newForexRatesList.add(rateRSD.getText().toString().trim());
        newForexRatesList.add(rateSEK.getText().toString().trim());
        newForexRatesList.add(rateSGD.getText().toString().trim());
        newForexRatesList.add(rateTRY.getText().toString().trim());
        
        SharedPreferences.Editor mEditor = mPrefsForexRates.edit();
        mEditor.clear();
        
        mEditor.putString(getString(R.string.currency_ALL), rateALL.getText().toString().trim());
        mEditor.putString(getString(R.string.currency_BAM), rateBAM.getText().toString().trim());
        mEditor.putString(getString(R.string.currency_BGN), rateBGN.getText().toString().trim());
        mEditor.putString(getString(R.string.currency_BYN), rateBYN.getText().toString().trim());
        mEditor.putString(getString(R.string.currency_CHF), rateCHF.getText().toString().trim());
        mEditor.putString(getString(R.string.currency_CZK), rateCZK.getText().toString().trim());
        mEditor.putString(getString(R.string.currency_DKK), rateDKK.getText().toString().trim());
        mEditor.putString(getString(R.string.currency_GBP), rateGBP.getText().toString().trim());
        mEditor.putString(getString(R.string.currency_HRK), rateHRK.getText().toString().trim());
        mEditor.putString(getString(R.string.currency_HUF), rateHUF.getText().toString().trim());
        mEditor.putString(getString(R.string.currency_MKD), rateMKD.getText().toString().trim());
        mEditor.putString(getString(R.string.currency_NOK), rateNOK.getText().toString().trim());
        mEditor.putString(getString(R.string.currency_PLN), ratePLN.getText().toString().trim());
        mEditor.putString(getString(R.string.currency_RON), rateRON.getText().toString().trim());
        mEditor.putString(getString(R.string.currency_RSD), rateRSD.getText().toString().trim());
        mEditor.putString(getString(R.string.currency_SEK), rateSEK.getText().toString().trim());
        mEditor.putString(getString(R.string.currency_SGD), rateSGD.getText().toString().trim());
        mEditor.putString(getString(R.string.currency_TRY), rateTRY.getText().toString().trim());

        mEditor.commit();
    }

    // Compares the old and new toggle settings/forex rates to check for differences, i.e. changes made
    private void checkForChangesMade() {
        boolean isChangesMade = false;

        for (int i = 0; i < currencyList.size()-1; i++) {
            System.out.println("i = " + i + ", old toggle settings = " + oldToggleSettingsList.get(i) + ", new toggle settings = " + newToggleSettingsList.get(i));
            System.out.println("i = " + i + ", old forex rates = " + oldForexRatesList.get(i) + ", new forex rates = " + newForexRatesList.get(i));

            if (oldToggleSettingsList.get(i) != newToggleSettingsList.get(i)) {
                isChangesMade = true;
                break;
            }
            if (!oldForexRatesList.get(i).equals(newForexRatesList.get(i))) {
                isChangesMade = true;
                break;
            }
        }

        if (isChangesMade) {
            Toast.makeText(this, R.string.msg_changesSaved, Toast.LENGTH_SHORT).show();
        }
    }
}
