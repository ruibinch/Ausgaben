package ruibin.ausgaben;

import android.app.Activity;
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
        mPrefsToggleRates = getSharedPreferences("toggleRates", MODE_PRIVATE);
        mPrefsForexRates = getSharedPreferences("forexRates", MODE_PRIVATE);

        currencyList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.spn_currency_entries)));

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
        
        toggleALL.setChecked(mPrefsToggleRates.getBoolean("ALL", true));
        toggleBAM.setChecked(mPrefsToggleRates.getBoolean("BAM", true));
        toggleBGN.setChecked(mPrefsToggleRates.getBoolean("BGN", true));
        toggleBYN.setChecked(mPrefsToggleRates.getBoolean("BYN", true));
        toggleCHF.setChecked(mPrefsToggleRates.getBoolean("CHF", true));
        toggleCZK.setChecked(mPrefsToggleRates.getBoolean("CZK", true));
        toggleDKK.setChecked(mPrefsToggleRates.getBoolean("DKK", true));
        toggleEUR.setChecked(mPrefsToggleRates.getBoolean("EUR", true));
        toggleGBP.setChecked(mPrefsToggleRates.getBoolean("GBP", true));
        toggleHRK.setChecked(mPrefsToggleRates.getBoolean("HRK", true));
        toggleHUF.setChecked(mPrefsToggleRates.getBoolean("HUF", true));
        toggleMKD.setChecked(mPrefsToggleRates.getBoolean("MKD", true));
        toggleNOK.setChecked(mPrefsToggleRates.getBoolean("NOK", true));
        togglePLN.setChecked(mPrefsToggleRates.getBoolean("PLN", true));
        toggleRON.setChecked(mPrefsToggleRates.getBoolean("RON", true));
        toggleRSD.setChecked(mPrefsToggleRates.getBoolean("RSD", true));
        toggleSEK.setChecked(mPrefsToggleRates.getBoolean("SEK", true));
        toggleSGD.setChecked(mPrefsToggleRates.getBoolean("SGD", true));
        toggleTRY.setChecked(mPrefsToggleRates.getBoolean("TRY", true));
    }
    
    // Loads the saved rates from the SharedPreferences
    private void loadForexRates() {
        for (int i = 0; i < currencyList.size(); i++) {
            if (!currencyList.get(i).equals("EUR"))
                oldForexRatesList.add(mPrefsForexRates.getString(currencyList.get(i), ""));
        }
        
        rateALL.setText(mPrefsForexRates.getString("ALL", ""));
        rateBAM.setText(mPrefsForexRates.getString("BAM", ""));
        rateBGN.setText(mPrefsForexRates.getString("BGN", ""));
        rateBYN.setText(mPrefsForexRates.getString("BYN", ""));
        rateCHF.setText(mPrefsForexRates.getString("CHF", ""));
        rateCZK.setText(mPrefsForexRates.getString("CZK", ""));
        rateDKK.setText(mPrefsForexRates.getString("DKK", ""));
        rateGBP.setText(mPrefsForexRates.getString("GBP", ""));
        rateHRK.setText(mPrefsForexRates.getString("HRK", ""));
        rateHUF.setText(mPrefsForexRates.getString("HUF", ""));
        rateMKD.setText(mPrefsForexRates.getString("MKD", ""));
        rateNOK.setText(mPrefsForexRates.getString("NOK", ""));
        ratePLN.setText(mPrefsForexRates.getString("PLN", ""));
        rateRON.setText(mPrefsForexRates.getString("RON", ""));
        rateRSD.setText(mPrefsForexRates.getString("RSD", ""));
        rateSEK.setText(mPrefsForexRates.getString("SEK", ""));
        rateSGD.setText(mPrefsForexRates.getString("SGD", ""));
        rateTRY.setText(mPrefsForexRates.getString("TRY", ""));
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

        mEditor.putBoolean("ALL", toggleALL.isChecked());
        mEditor.putBoolean("BAM", toggleBAM.isChecked());
        mEditor.putBoolean("BGN", toggleBGN.isChecked());
        mEditor.putBoolean("BYN", toggleBYN.isChecked());
        mEditor.putBoolean("CHF", toggleCHF.isChecked());
        mEditor.putBoolean("CZK", toggleCZK.isChecked());
        mEditor.putBoolean("DKK", toggleDKK.isChecked());
        mEditor.putBoolean("EUR", toggleEUR.isChecked());
        mEditor.putBoolean("GBP", toggleGBP.isChecked());
        mEditor.putBoolean("HRK", toggleHRK.isChecked());
        mEditor.putBoolean("HUF", toggleHUF.isChecked());
        mEditor.putBoolean("MKD", toggleMKD.isChecked());
        mEditor.putBoolean("NOK", toggleNOK.isChecked());
        mEditor.putBoolean("PLN", togglePLN.isChecked());
        mEditor.putBoolean("RON", toggleRON.isChecked());
        mEditor.putBoolean("RSD", toggleRSD.isChecked());
        mEditor.putBoolean("SEK", toggleSEK.isChecked());
        mEditor.putBoolean("SGD", toggleSGD.isChecked());
        mEditor.putBoolean("TRY", toggleTRY.isChecked());

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
        
        mEditor.putString("ALL", rateALL.getText().toString().trim());
        mEditor.putString("BAM", rateBAM.getText().toString().trim());
        mEditor.putString("BGN", rateBGN.getText().toString().trim());
        mEditor.putString("BYN", rateBYN.getText().toString().trim());
        mEditor.putString("CHF", rateCHF.getText().toString().trim());
        mEditor.putString("CZK", rateCZK.getText().toString().trim());
        mEditor.putString("DKK", rateDKK.getText().toString().trim());
        mEditor.putString("GBP", rateGBP.getText().toString().trim());
        mEditor.putString("HRK", rateHRK.getText().toString().trim());
        mEditor.putString("HUF", rateHUF.getText().toString().trim());
        mEditor.putString("MKD", rateMKD.getText().toString().trim());
        mEditor.putString("NOK", rateNOK.getText().toString().trim());
        mEditor.putString("PLN", ratePLN.getText().toString().trim());
        mEditor.putString("RON", rateRON.getText().toString().trim());
        mEditor.putString("RSD", rateRSD.getText().toString().trim());
        mEditor.putString("SEK", rateSEK.getText().toString().trim());
        mEditor.putString("SGD", rateSGD.getText().toString().trim());
        mEditor.putString("TRY", rateTRY.getText().toString().trim());

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
            Toast.makeText(this, R.string.message_changesSaved, Toast.LENGTH_SHORT).show();
        }
    }
}
