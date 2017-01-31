package ruibin.ausgaben;

import android.content.Intent;
import android.app.Activity;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private GoogleApiClient mGoogleApiClient;
    // private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initLocationClients();
        setRatesSettingsIfFirstStartup(); // for first-time startup
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    /*
     * ====================== GOOGLE API IMPLEMENTATION METHODS ======================
     */

    @Override
    public void onConnected(Bundle connectionHint) {
        try {
            Location location =  LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location == null) {
                //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                setLocationText();
            } else {
                updateWithNewLocation(location);
            }
        } catch (SecurityException e) {
            Toast.makeText(this, "Security exception", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int value) {
        Toast.makeText(this, "Connection suspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, 9000);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Connection failed", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * ====================== LOCATIONLISTENER IMPLEMENTATION METHODS ======================
     */

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(MainActivity.this, "Location changed: " + location.toString(), Toast.LENGTH_SHORT).show();
        updateWithNewLocation(location);
    }

    /*
     * ====================== INITIALISATION METHODS ======================
     */

    // Initiates the Google Play Services API client and LocationRequest
    private void initLocationClients() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        /*
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_LOW_POWER)
                .setInterval(10000 * 1000)
                .setFastestInterval(1000 * 1000);
         */
    }

    // Sets the forex rates manually on first startup when the Preferences are empty
    private void setRatesSettingsIfFirstStartup() {
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
    // TODO - Populate country Spinner based on input countries


    /*
     * ====================== HELPER METHODS ======================
     */

    // Updates the Preferences and TextView with the new location
    private void updateWithNewLocation(Location location) {
        String cityName = "";
        String countryName = "";

        // Preferences API
        SharedPreferences mPrefs = getSharedPreferences("location", MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.clear();

        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Geocoder gc = new Geocoder(this, Locale.getDefault());

            try {
                List<Address> addresses = gc.getFromLocation(latitude, longitude, 1);

                if (!addresses.isEmpty()) {
                    Address address = addresses.get(0);

                    // Get the city name
                    if (address.getLocality() != null) {
                        cityName = address.getLocality();
                    } else if (address.getSubLocality() != null) {
                        cityName = address.getSubLocality();
                    } else if (address.getAdminArea() != null) {
                        cityName = address.getAdminArea();
                    } else if (address.getSubAdminArea() != null) {
                        cityName = address.getSubAdminArea();
                    }

                    // Get the country name
                    countryName = address.getCountryName();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Save the updated location in the Preferences API
        mEditor.putString("city", cityName);
        mEditor.putString("country", countryName);
        mEditor.commit();

        setLocationText();
    }

    // Sets the TextView to the city name (if applicable) and country name
    private void setLocationText() {
        TextView locationText = (TextView) findViewById(R.id.text_location);
        SharedPreferences mPrefs = getSharedPreferences("location", MODE_PRIVATE);
        StringBuilder sb = new StringBuilder();

        String cityName = mPrefs.getString("city", "");
        String countryName = mPrefs.getString("country", "");

        if (!cityName.equals("")) {
            sb.append(cityName).append(", ");
        }
        sb.append(countryName);

        // Sets the location icon to be visible
        ImageView locationIcon = (ImageView) findViewById(R.id.icon_location);
        locationIcon.setVisibility(View.VISIBLE);
        locationIcon.getLayoutParams().width = 80;
        locationIcon.getLayoutParams().height = 80;

        locationText.setText(sb.toString());
    }
}