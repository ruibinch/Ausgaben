package ruibin.ausgaben;

import android.content.Intent;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRatesSettingsIfFirst();
    }
    
    /*
     * ====================== INITIALISATION METHODS ======================
     */

    // Sets the forex rates manually on first startup when the Preferences are empty
    private void setRatesSettingsIfFirst() {
        SharedPreferences prefsToggleRates = getSharedPreferences("toggleRates", MODE_PRIVATE);
        SharedPreferences prefsForexRates = getSharedPreferences("forexRates", MODE_PRIVATE);

        if (!prefsToggleRates.contains("SGD")) {
            SharedPreferences.Editor mEditor = prefsToggleRates.edit();
            mEditor.clear();

            mEditor.putBoolean("ALL", true);
            mEditor.putBoolean("BAM", true);
            mEditor.putBoolean("BGN", true);
            mEditor.putBoolean("BYN", true);
            mEditor.putBoolean("CHF", true);
            mEditor.putBoolean("CZK", true);
            mEditor.putBoolean("DKK", true);
            mEditor.putBoolean("GBP", true);
            mEditor.putBoolean("HUF", true);
            mEditor.putBoolean("HRK", true);
            mEditor.putBoolean("MKD", true);
            mEditor.putBoolean("NOK", true);
            mEditor.putBoolean("PLN", true);
            mEditor.putBoolean("RON", true);
            mEditor.putBoolean("RSD", true);
            mEditor.putBoolean("SEK", true);
            mEditor.putBoolean("SGD", true);
            mEditor.putBoolean("TRY", true);

            mEditor.commit();
        }
        
        if (!prefsForexRates.contains("SGD")) {
            Toast.makeText(this, "First time startup - setting initial forex rates", Toast.LENGTH_SHORT).show();

            SharedPreferences.Editor mEditor = prefsForexRates.edit();
            mEditor.clear();

            mEditor.putString("ALL", "135.95");
            mEditor.putString("BAM", "1.95");
            mEditor.putString("BGN", "1.95");
            mEditor.putString("BYN", "2.08");
            mEditor.putString("CHF", "1.07");
            mEditor.putString("CZK", "27.02");
            mEditor.putString("DKK", "7.44");
            mEditor.putString("GBP", "0.85");
            mEditor.putString("HUF", "308.00");
            mEditor.putString("HRK", "7.51");
            mEditor.putString("MKD", "61.6");
            mEditor.putString("NOK", "9.08");
            mEditor.putString("PLN", "4.42");
            mEditor.putString("RON", "4.52");
            mEditor.putString("RSD", "123.00");
            mEditor.putString("SEK", "9.58");
            mEditor.putString("SGD", "1.51");
            mEditor.putString("TRY", "3.99");

            mEditor.commit();
        }
    }

    /*
     * ====================== CLICK HANDLERS ======================
     */

    public void onClickOverview(View view) {
        Intent intent = new Intent(this, OverviewActivity.class);
        startActivity(intent);
    }

    public void onClickAddExpense(View view) {
        Intent intent = new Intent(this, ExpenseActivity.class);
        startActivity(intent);
    }

    public void onClickSetForexRates(View view) {
        Intent intent = new Intent(this, ForexRatesActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    // TODO - import CSV / export as CSV
    // TODO - Include location
    
}
