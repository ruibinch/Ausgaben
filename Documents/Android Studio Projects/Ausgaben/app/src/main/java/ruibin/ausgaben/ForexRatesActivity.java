package ruibin.ausgaben;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class ForexRatesActivity extends Activity {

    // Checkbox fields of forex rates display
    CheckBox toggleALL;
    CheckBox toggleBAM;
    CheckBox toggleBGN;
    CheckBox toggleBYN;
    CheckBox toggleCHF;
    CheckBox toggleCZK;
    CheckBox toggleDKK;
    CheckBox toggleGBP;
    CheckBox toggleHUF;
    CheckBox toggleHRK;
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
    EditText rateHUF;
    EditText rateHRK;
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

        initWidgets();
        loadToggleSettings();
        loadForexRates();
    }

    /*
     * ====================== INITIALISATION METHODS ======================
     */
    
    private void initWidgets() {
        toggleALL = (CheckBox) findViewById(R.id.toggle_currencyALL);
        toggleBAM = (CheckBox) findViewById(R.id.toggle_currencyBAM);
        toggleBGN = (CheckBox) findViewById(R.id.toggle_currencyBGN);
        toggleBYN = (CheckBox) findViewById(R.id.toggle_currencyBYN);
        toggleCHF = (CheckBox) findViewById(R.id.toggle_currencyCHF);
        toggleCZK = (CheckBox) findViewById(R.id.toggle_currencyCZK);
        toggleDKK = (CheckBox) findViewById(R.id.toggle_currencyDKK);
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
        SharedPreferences mPrefs = getSharedPreferences("toggleRates", MODE_PRIVATE);
        
        toggleALL.setChecked(mPrefs.getBoolean("ALL", true));
        toggleBAM.setChecked(mPrefs.getBoolean("BAM", true));
        toggleBGN.setChecked(mPrefs.getBoolean("BGN", true));
        toggleBYN.setChecked(mPrefs.getBoolean("BYN", true));
        toggleCHF.setChecked(mPrefs.getBoolean("CHF", true));
        toggleCZK.setChecked(mPrefs.getBoolean("CZK", true));
        toggleDKK.setChecked(mPrefs.getBoolean("DKK", true));
        toggleGBP.setChecked(mPrefs.getBoolean("GBP", true));
        toggleHUF.setChecked(mPrefs.getBoolean("HUF", true));
        toggleHRK.setChecked(mPrefs.getBoolean("HRK", true));
        toggleMKD.setChecked(mPrefs.getBoolean("MKD", true));
        toggleNOK.setChecked(mPrefs.getBoolean("NOK", true));
        togglePLN.setChecked(mPrefs.getBoolean("PLN", true));
        toggleRON.setChecked(mPrefs.getBoolean("RON", true));
        toggleRSD.setChecked(mPrefs.getBoolean("RSD", true));
        toggleSEK.setChecked(mPrefs.getBoolean("SEK", true));
        toggleSGD.setChecked(mPrefs.getBoolean("SGD", true));
        toggleTRY.setChecked(mPrefs.getBoolean("TRY", true));
    }
    
    // Loads the saved rates from the SharedPreferences
    private void loadForexRates() {
        SharedPreferences mPrefs = getSharedPreferences("forexRates", MODE_PRIVATE);

        rateALL.setText(mPrefs.getString("ALL", ""));
        rateBAM.setText(mPrefs.getString("BAM", ""));
        rateBGN.setText(mPrefs.getString("BGN", ""));
        rateBYN.setText(mPrefs.getString("BYN", ""));
        rateCHF.setText(mPrefs.getString("CHF", ""));
        rateCZK.setText(mPrefs.getString("CZK", ""));
        rateDKK.setText(mPrefs.getString("DKK", ""));
        rateGBP.setText(mPrefs.getString("GBP", ""));
        rateHUF.setText(mPrefs.getString("HUF", ""));
        rateHRK.setText(mPrefs.getString("HRK", ""));
        rateMKD.setText(mPrefs.getString("MKD", ""));
        rateNOK.setText(mPrefs.getString("NOK", ""));
        ratePLN.setText(mPrefs.getString("PLN", ""));
        rateRON.setText(mPrefs.getString("RON", ""));
        rateRSD.setText(mPrefs.getString("RSD", ""));
        rateSEK.setText(mPrefs.getString("SEK", ""));
        rateSGD.setText(mPrefs.getString("SGD", ""));
        rateTRY.setText(mPrefs.getString("TRY", ""));
    }

    /*
     * ====================== CLICK HANDLERS ======================
     */
    
    @Override
    public void onBackPressed() {
        updateToggleSettings();
        updateForexRates();
        Toast.makeText(this, R.string.message_changesSaved, Toast.LENGTH_SHORT).show();
        
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /*
     * ====================== HELPER METHODS ======================
     */

    // Saves the updated toggle settings of the currencies
    private void updateToggleSettings() {
        SharedPreferences mPrefs = getSharedPreferences("toggleRates", MODE_PRIVATE); // key = toggleRates
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.clear();

        mEditor.putBoolean("ALL", toggleALL.isChecked());
        mEditor.putBoolean("BAM", toggleBAM.isChecked());
        mEditor.putBoolean("BGN", toggleBGN.isChecked());
        mEditor.putBoolean("BYN", toggleBYN.isChecked());
        mEditor.putBoolean("CHF", toggleCHF.isChecked());
        mEditor.putBoolean("CZK", toggleCZK.isChecked());
        mEditor.putBoolean("DKK", toggleDKK.isChecked());
        mEditor.putBoolean("GBP", toggleGBP.isChecked());
        mEditor.putBoolean("HUF", toggleHUF.isChecked());
        mEditor.putBoolean("HRK", toggleHRK.isChecked());
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
        SharedPreferences mPrefs = getSharedPreferences("forexRates", MODE_PRIVATE); // key = forexRates
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.clear();
        
        mEditor.putString("ALL", rateALL.getText().toString().trim());
        mEditor.putString("BAM", rateBAM.getText().toString().trim());
        mEditor.putString("BGN", rateBGN.getText().toString().trim());
        mEditor.putString("BYN", rateBYN.getText().toString().trim());
        mEditor.putString("CHF", rateCHF.getText().toString().trim());
        mEditor.putString("CZK", rateCZK.getText().toString().trim());
        mEditor.putString("DKK", rateDKK.getText().toString().trim());
        mEditor.putString("GBP", rateGBP.getText().toString().trim());
        mEditor.putString("HUF", rateHUF.getText().toString().trim());
        mEditor.putString("HRK", rateHRK.getText().toString().trim());
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
}
