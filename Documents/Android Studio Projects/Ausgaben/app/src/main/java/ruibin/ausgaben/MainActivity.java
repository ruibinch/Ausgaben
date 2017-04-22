package ruibin.ausgaben;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ruibin.ausgaben.database.SQLiteHelper;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private SQLiteHelper dbHelper = new SQLiteHelper(this);

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.showOverflowMenu();

        initLocationClients();
        setLocationText();
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
     * ====================== TOOLBAR METHODS ======================
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_importCSV :
                importDatabase();
                return true;
            case R.id.action_exportCSV :
                exportDatabase();
                return true;
            default :
                return super.onOptionsItemSelected(item);
        }
    }

    /*
     * ====================== GOOGLE API IMPLEMENTATION METHODS ======================
     */

    @Override
    public void onConnected(Bundle connectionHint) {
        /*
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
        */
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
                Toast.makeText(this, "Resolving connection error", Toast.LENGTH_SHORT).show();
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

    // Initiates the Google Play Services API client
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

    public void onClickUpdateLocation(View view) {
        Toast.makeText(this, "Updating location", Toast.LENGTH_SHORT).show();
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
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    // TODO - include option to set defaults in ExpenseActivity
    // TODO - set list position in DetailsActivity after editing
    // TODO - long press on 1 filter to uncheck the rest
    // TODO - currency API
    // TODO - auto-back up to Google Drive

    /*
     * ====================== HELPER METHODS ======================
     */

    // Updates the Preferences and TextView with the new location
    private void updateWithNewLocation(Location location) {
        String cityName = "";
        String countryName = "";

        // Preferences API
        SharedPreferences mPrefsLocation = getSharedPreferences("location", MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mPrefsLocation.edit();
        mEditor.clear();

        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Geocoder gc = new Geocoder(this, Locale.getDefault());

            // Manual test of location
            //latitude = 41.993199;
            //longitude = 21.432557;

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
        SharedPreferences mPrefs = getSharedPreferences("location", MODE_PRIVATE); // Obtains the last-saved location
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

    /*
     * ====================== CSV HELPER METHODS ======================
     */

    // Imports a CSV file into the DB
    private void importDatabase() {
        try {
            InputStream inputStream = getAssets().open("expenses.csv");
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line = "";
            String tableName = "expenses";
            String columns = "date, name, category, amount, currency, forexrate, forexrate_eurtosgd, city, country";
            String sqlStart = "INSERT INTO " + tableName + " (" + columns + ") VALUES (";
            String sqlEnd = ");";

            SQLiteDatabase sqldb = dbHelper.getReadableDatabase();
            sqldb.beginTransaction();
            while ((line = br.readLine()) != null) {
                StringBuilder sb = new StringBuilder(sqlStart);
                String[] splitLine = line.split(",");

                // Converts the date from YYYYMMDD format to long
                Calendar cal = Calendar.getInstance();
                cal.set(Integer.parseInt(splitLine[0].substring(0,4)),
                        Integer.parseInt(splitLine[0].substring(4,6)) - 1,
                        Integer.parseInt(splitLine[0].substring(6,8)));

                sb.append(cal.getTimeInMillis() + ", ");
                sb.append("'" + splitLine[1] + "', ");
                sb.append("'" + splitLine[2] + "', ");
                sb.append(splitLine[3] + ", ");
                sb.append("'" + splitLine[4] + "', ");
                sb.append(splitLine[5] + ", ");
                sb.append(splitLine[6] + ", ");
                sb.append("'" + splitLine[7] + "', ");
                sb.append("'" + splitLine[8] + "'");
                sb.append(sqlEnd);
                System.out.println(sb.toString());
                sqldb.execSQL(sb.toString());
            }

            Toast.makeText(this, "Import successful", Toast.LENGTH_SHORT).show();
            sqldb.setTransactionSuccessful();
            sqldb.endTransaction();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "IOException encountered", Toast.LENGTH_SHORT).show();
        }

    }

    // Exports the DB to a CSV file
    private void exportDatabase() {
        verifyStoragePermissions(); // for API 23+

        SQLiteDatabase sqldb = dbHelper.getReadableDatabase();
        Cursor cursor;

        try {
            cursor = sqldb.rawQuery("SELECT * FROM expenses", null);
            int rowCount = 0;
            int colCount = 0;
            File directory = Environment.getExternalStorageDirectory();
            System.out.println("directory = " + directory);
            String fileName = "expenses.csv";
            File saveFile = new File(directory, fileName);
            FileWriter fw = new FileWriter(saveFile);

            BufferedWriter bw = new BufferedWriter(fw);
            rowCount = cursor.getCount();
            colCount = cursor.getColumnCount();
            System.out.println("rowCount = " + rowCount + ", colCount = " + colCount);

            if (rowCount > 0) {
                cursor.moveToFirst();

                for (int i = 0; i < colCount; i++) {
                    if (i != colCount - 1)
                        bw.write(cursor.getColumnName(i) + ", " );
                    else
                        bw.write(cursor.getColumnName(i));
                }
                bw.newLine();

                for (int i = 0; i < rowCount; i++ ) {
                    cursor.moveToPosition(i);
                    for (int j = 0; j < colCount; j++) {
                        if (j == 1) { // if this is the date column
                            Calendar cal = Calendar.getInstance();
                            cal.setTimeInMillis(cursor.getLong(j));
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                            bw.write(sdf.format(cal.getTime()) + ", ");
                        } else if (j != colCount - 1) {
                            bw.write(cursor.getString(j) + ", ");
                        } else {
                            if (cursor.getString(j) != null) // for the case where the image path is null
                                bw.write(cursor.getString(j));
                        }
                    }
                    bw.newLine();
                }

                bw.flush();
                bw.close();
                Toast.makeText(this, "Database exported: " + rowCount + " rows", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        } catch (IOException e) {
            if (sqldb.isOpen()) {
                sqldb.close();
            }
            e.printStackTrace();
            Toast.makeText(this, "IOException encountered", Toast.LENGTH_SHORT).show();
        }
    }

    private void verifyStoragePermissions() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }
}