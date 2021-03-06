package com.ausgaben;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import com.ausgaben.misc.DecimalDigitsInputFilter;
import com.ausgaben.misc.IntentHelper;
import com.ausgaben.misc.Quintuple;
import com.ausgaben.database.DatabaseExpenses;
import com.ausgaben.database.Expense;
import com.ausgaben.exceptions.MissingNameException;

public class ExpenseActivity extends AppCompatActivity {

    private SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
    private Calendar mCal = Calendar.getInstance();
    private TextView mDateDisplay;

    private DatabaseExpenses database;

    // For editing purposes from DetailsActivity
    private boolean isEditExpense;
    private long editExpenseId;
    private int displayMonth;
    private String displayCountry;
    private int displayStartDate;
    private int displayEndDate;
    // Saved scroll position of the list in DetailsActivity
    private int scrollIndex;
    private int scrollOffset;

    // For camera
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private String currentPhotoPath; // To temporarily store the to-be image path
    private String imagePath; // To store the existing image path
    private Bitmap imageBitmap;
    private boolean isImageDeleted; // For editing expense purposes
    private boolean isNewImageTaken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        // Initialise toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialisation methods
        openDatabase();
        setDisplays();
        populateExpenseData();
        setCurrencyVisibility();
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

    private void openDatabase() {
        database = new DatabaseExpenses(this);
        database.open();
    }

    // Sets the correct displays for the elements
    private void setDisplays() {
        // Sets the date to today's date by default
        mDateDisplay = (TextView) findViewById(R.id.input_expenseDate);
        updateDateDisplay();

        // Sets the default category to 'Food'
        Spinner categorySpinner = (Spinner) findViewById(R.id.spn_expenseCategory);
        categorySpinner.setSelection(1);

        // Sets the input amount to 2 decimal places
        EditText editText = (EditText) findViewById(R.id.input_expenseAmt);
        editText.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(6,2)});

        // Initialise the default click handler for the Photo button
        ImageView imagePhoto = (ImageView) findViewById(R.id.img_photo);
        imagePhoto.setOnClickListener(onClickCamera);
    }

    // Populates the fields with data and sets elements visible, if applicable (i.e. editing an existing expense)
    private void populateExpenseData() {
         Intent intent = getIntent();
         if (intent.getLongExtra(getString(R.string.data_editExpenseId), -1) != -1) {
             isEditExpense = true;
             editExpenseId = intent.getLongExtra(getString(R.string.data_editExpenseId), -1);

             Date expenseDate = new Date(intent.getStringExtra(getString(R.string.data_date)));
             mCal.setTime(expenseDate);
             updateDateDisplay();

             EditText editName = (EditText) findViewById(R.id.input_expenseName);
             editName.setText(intent.getStringExtra(getString(R.string.data_name)));

             Spinner spinnerCategory = (Spinner) findViewById(R.id.spn_expenseCategory);
             setCategorySpinner(spinnerCategory, intent.getStringExtra(getString(R.string.data_category)));

             EditText editAmount = (EditText) findViewById(R.id.input_expenseAmt);
             editAmount.setText(intent.getStringExtra(getString(R.string.data_amount)));

             Spinner spinnerCurrency = (Spinner) findViewById(R.id.spn_currency);
             setCurrencySpinner(spinnerCurrency, intent.getStringExtra(getString(R.string.data_currency)));

             // Set 'Country' row to be visible
             TextView textCountry = (TextView) findViewById(R.id.text_expenseCountry);
             Spinner spinnerCountry = (Spinner) findViewById(R.id.spn_expenseCountry);
             textCountry.setVisibility(View.VISIBLE);
             spinnerCountry.setVisibility(View.VISIBLE);
             setCountrySpinner(spinnerCountry, intent.getStringExtra(getString(R.string.data_country)));

             // Set 'City' row to be visible
             TextView textCity = (TextView) findViewById(R.id.text_expenseCity);
             TextView textCityInfo = (TextView) findViewById(R.id.text_expenseCity_info);
             textCity.setVisibility(View.VISIBLE);
             textCityInfo.setVisibility(View.VISIBLE);
             textCityInfo.setText(intent.getStringExtra(getString(R.string.data_city)));

             // and then move 'Photo' row below 'City'
             TextView textPhoto = (TextView) findViewById(R.id.text_expensePhoto);
             ImageView imagePhoto = (ImageView) findViewById(R.id.img_photo);
             RelativeLayout.LayoutParams textPhotoParams = (RelativeLayout.LayoutParams) textPhoto.getLayoutParams();
             RelativeLayout.LayoutParams imagePhotoParams = (RelativeLayout.LayoutParams) imagePhoto.getLayoutParams();
             textPhotoParams.addRule(RelativeLayout.BELOW, R.id.text_expenseCity);
             imagePhotoParams.addRule(RelativeLayout.BELOW, R.id.text_expenseCity);

             // and then display the attached image
             imagePath = intent.getStringExtra(getString(R.string.data_imagepath)).trim();
             System.out.println("imagePath = " + imagePath);
             if (imagePath != null) {
                 if (!imagePath.equals("")) {
                     loadImageFromStorage(imagePhoto);
                     // if image is loaded, change the onclick listener
                     imagePhoto.setOnClickListener(onClickCameraWithExistingPhoto);
                 }
             }

             Button delButton = (Button) findViewById(R.id.btn_delete);
             delButton.setVisibility(View.VISIBLE);

             // Set month and country display settings
             displayMonth = intent.getIntExtra(getString(R.string.data_displayMonth), -1);
             displayCountry = intent.getStringExtra(getString(R.string.data_displayCountry));
             displayStartDate = intent.getIntExtra(getString(R.string.data_displayStartDate), -1);
             displayEndDate = intent.getIntExtra(getString(R.string.data_displayEndDate), -1);

             // Sets the saved scroll position from DetailsActivity
             scrollIndex = intent.getIntExtra(getString(R.string.data_scrollIndex), -1);
             scrollOffset = intent.getIntExtra(getString(R.string.data_scrollOffset), -1);
         }
    }

    // Sets the visibility of the currencies in the Spinner - only applicable when adding a new expense
    private void setCurrencyVisibility() {
        if (!isEditExpense) {
            ArrayList<String> currencyList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.arr_currency_entries)));
            SharedPreferences mPrefs = getSharedPreferences(getString(R.string.pref_toggleRates), MODE_PRIVATE);
            currencyList = filterHiddenCurrencies(currencyList, mPrefs);

            Spinner currencySpinner = (Spinner) findViewById(R.id.spn_currency);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, currencyList);
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

            currencySpinner.setAdapter(adapter);
            currencySpinner.setSelection(currencyList.indexOf(getString(R.string.currency_EUR))); // Sets the default currency to EUR
        }
    }

    /*
     * ====================== CLICK HANDLERS ======================
     */

    @Override
    public void onBackPressed() {
        if (!isEditExpense) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, DetailsActivity.class);
            IntentHelper intentHelper = new IntentHelper(intent);
            intentHelper.addDisplaySettings(displayMonth, displayCountry, displayStartDate, displayEndDate);
            intentHelper.addScrollSettings(scrollIndex, scrollOffset);
            intentHelper.addFilterSettings(
                getIntent().getBooleanExtra(getString(R.string.data_isAccommodationVisible), true),
                getIntent().getBooleanExtra(getString(R.string.data_isFoodVisible), true),
                getIntent().getBooleanExtra(getString(R.string.data_isGiftsVisible), true),
                getIntent().getBooleanExtra(getString(R.string.data_isLeisureVisible), true),
                getIntent().getBooleanExtra(getString(R.string.data_isMiscVisible), true),
                getIntent().getBooleanExtra(getString(R.string.data_isShoppingVisible), true),
                getIntent().getBooleanExtra(getString(R.string.data_isTravelVisible), true));

            startActivity(intent);
        }
    }

    public void onClickSetDate(View view) {
        DatePickerDialog.OnDateSetListener mDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                mCal.set(Calendar.YEAR, year);
                mCal.set(Calendar.MONTH, month);
                mCal.set(Calendar.DAY_OF_MONTH, day);
                updateDateDisplay();
            }
        };

        new DatePickerDialog(ExpenseActivity.this, mDateListener,
                mCal.get(Calendar.YEAR),
                mCal.get(Calendar.MONTH),
                mCal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private View.OnClickListener onClickCamera = new View.OnClickListener() {
        public void onClick(View view) {
            openCamera();
        }
    };

    private View.OnClickListener onClickCameraWithExistingPhoto = new View.OnClickListener() {
        public void onClick(View view) {
            CharSequence options[] = getResources().getStringArray(R.array.arr_photoClick_entries);

            AlertDialog.Builder builder = new AlertDialog.Builder(ExpenseActivity.this, R.style.AlertDialogTheme);
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int option) {
                   if (option == 0) { // "View photo"
                       Intent intent = new Intent(ExpenseActivity.this, ImageActivity.class);
                       intent.putExtra(getString(R.string.data_imagepath), imagePath);
                       startActivity(intent);
                   } else if (option == 1) { // "Take new photo"
                       openCamera();
                   } else if (option == 2) { // "Remove photo"
                       ImageView cameraBtn = (ImageView) findViewById(R.id.img_photo);
                       cameraBtn.setImageResource(R.drawable.ic_camera);
                       cameraBtn.setOnClickListener(onClickCamera);

                       isImageDeleted = true;
                   }
               }
            });

            builder.show();
        }
    };

    public void onClickCancel(View view) {
        onBackPressed();
    }

    public void onClickSave(View view) {
        try {
            Quintuple<Long, String, String, BigDecimal, String> quint = extractInputData(); // date, name, category, amount, currency

            // Forex rates Preferences
            SharedPreferences mPrefs = getSharedPreferences(getString(R.string.pref_forexRates), MODE_PRIVATE);
            Intent intent = new Intent(this, DetailsActivity.class);

            // Location Preferences
            SharedPreferences mPrefsLocation = getSharedPreferences(getString(R.string.pref_location), MODE_PRIVATE);
            String cityName = mPrefsLocation.getString(getString(R.string.pref_city), "");
            String countryName = mPrefsLocation.getString(getString(R.string.pref_country), "");

            // Saves the image to internal storage
            if (imageBitmap != null) {
                saveImageToInternalStorage(imageBitmap);
                isNewImageTaken = true;
            }

            if (!isEditExpense) { // Adding a new expense
                Expense newExpense = database.createExpense(quint.getFirst(), quint.getSecond(), quint.getThird(),
                        quint.getFourth(), quint.getFifth(), mPrefs, cityName, countryName, currentPhotoPath);
                setDisplayMonth(new Date(quint.getFirst())); // Sets the display month for DetailsActivity

                IntentHelper intentHelper = new IntentHelper(intent);
                intentHelper.addNewExpenseId(newExpense.getId());
                intentHelper.addSourceActivity(getString(R.string.str_expenseActivity));
                intentHelper.addDisplaySettings(displayMonth, getResources().getString(R.string.str_all), 1, 31);
                Toast.makeText(getApplicationContext(), "'" + quint.getSecond() + "' added in " + quint.getFifth(), Toast.LENGTH_SHORT).show();
            } else { // Editing an expense
                // If image had been removed, delete it from internal storage too
                if (isImageDeleted) {
                    deleteImageFromInternalStorage();
                    imagePath = null;
                }

                String country = extractInputCountry();
                if (isNewImageTaken) {
                    // Only update the image path, if a new image had been taken
                    // Caters for the corner case where 'Take new photo' is selected, but the camera request is cancelled
                    imagePath = currentPhotoPath;
                }

                IntentHelper intentHelper = new IntentHelper(intent);
                intentHelper.addScrollSettings(scrollIndex, scrollOffset);
                intentHelper.addFilterSettings(
                        getIntent().getBooleanExtra(getString(R.string.data_isAccommodationVisible), true),
                        getIntent().getBooleanExtra(getString(R.string.data_isFoodVisible), true),
                        getIntent().getBooleanExtra(getString(R.string.data_isGiftsVisible), true),
                        getIntent().getBooleanExtra(getString(R.string.data_isLeisureVisible), true),
                        getIntent().getBooleanExtra(getString(R.string.data_isMiscVisible), true),
                        getIntent().getBooleanExtra(getString(R.string.data_isShoppingVisible), true),
                        getIntent().getBooleanExtra(getString(R.string.data_isTravelVisible), true));

                boolean[] isEditsMade = database.editExpense(editExpenseId, quint.getFirst(), quint.getSecond(),
                        quint.getThird(), quint.getFourth(), quint.getFifth(), mPrefs, country, imagePath);
                intentHelper.addEditExpenseId(editExpenseId);
                intentHelper.addEditedInfo(isEditsMade[0], isEditsMade[1], isEditsMade[2], isEditsMade[3], isEditsMade[4], isEditsMade[5], isEditsMade[6]);
                intentHelper.addDisplaySettings(displayMonth, displayCountry, displayStartDate, displayEndDate);

                if (isEditsMade[0]) { // if date/month is edited, set display month to the new edited month and reset the start/end dates
                    setDisplayMonth(new Date(quint.getFirst()));
                    intentHelper.addDisplayMonthAndDatesSettings(displayMonth, 1, 31);
                }
                if (isEditsMade[5]) // if country is edited, set display country to all countries
                    intentHelper.addDisplayCountry("All");

                if (isEditsMade[0] || isEditsMade[1] || isEditsMade[2] || isEditsMade[3] || isEditsMade[4] || isEditsMade[5] || isEditsMade[6]) // if any edits were made
                    Toast.makeText(getApplicationContext(), "'" + quint.getSecond() + "' edited", Toast.LENGTH_SHORT).show();
            }

            startActivity(intent);
        } catch (NumberFormatException e) {
            Toast.makeText(getApplicationContext(), getString(R.string.msg_missingAmt), Toast.LENGTH_SHORT).show();
        } catch (MissingNameException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickDelete(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.msg_deleteExpenseConfirmation));

        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Delete the expense from the DB, as well as the attached image (if present) from internal storage
                String deletedExpenseName = database.deleteExpense(editExpenseId);
                if (imagePath != null) {
                    deleteImageFromInternalStorage();
                }

                Toast.makeText(getApplicationContext(), "'" + deletedExpenseName + "' deleted", Toast.LENGTH_SHORT).show();
                dialog.cancel();

                Intent intent = new Intent(ExpenseActivity.this, DetailsActivity.class);
                IntentHelper intentHelper = new IntentHelper(intent);
                intentHelper.addDisplaySettings(displayMonth, displayCountry, displayStartDate, displayEndDate);
                startActivity(intent);
            }
        });

        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /*
     *  ====================== CAMERA METHODS ======================
     */

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Toast.makeText(ExpenseActivity.this, getString(R.string.msg_exception_io), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            // Continue only if the file was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(ExpenseActivity.this, "com.ruibin.ausgaben.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    // Creates a file to save the image in
    private File createImageFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timestamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            if (storageDir.mkdirs()) {
                System.out.println("Directory created");
            } else {
                return null;
            }
        }

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        currentPhotoPath = image.getAbsolutePath();
        System.out.println("currentPhotoPath = " + currentPhotoPath);
        return image;
    }

    // Data obtained from the camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            File imageFile = new File(currentPhotoPath);
            imageBitmap = getBitmap(imageFile);
            imageBitmap = setImageRotation(imageBitmap);

            // Display the image as a thumbnail
            ImageView cameraBtn = (ImageView) findViewById(R.id.img_photo);
            cameraBtn.setImageBitmap(imageBitmap);
            cameraBtn.setOnClickListener(onClickCameraWithExistingPhoto); // change the onclick listener
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_CANCELED) {
            currentPhotoPath = null;
        }
    }

    // Get a bitmap from the file
    private Bitmap getBitmap(File imageFile) {
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile.getPath(), bounds);
        if ((bounds.outWidth == -1) || (bounds.outHeight == -1))
            return null;

        int originalSize = (bounds.outHeight > bounds.outWidth) ? bounds.outHeight : bounds.outWidth;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = originalSize / 400;
        return BitmapFactory.decodeFile(imageFile.getPath(), options);
    }

    // Sets the image to be displayed correctly in portrait/landscape
    private Bitmap setImageRotation(Bitmap imageBitmap) {
        try {
            ExifInterface exif = new ExifInterface(currentPhotoPath);
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            int rotationInDegrees = 0;
            if (rotation == ExifInterface.ORIENTATION_ROTATE_90) { rotationInDegrees = 90; }
            else if (rotation == ExifInterface.ORIENTATION_ROTATE_180) {  rotationInDegrees = 180; }
            else if (rotation == ExifInterface.ORIENTATION_ROTATE_270) {  rotationInDegrees = 270; }

            // Use the image's rotation as a reference point to rotate the image using a Matrix
            Matrix matrix = new Matrix();
            if (rotation != 0f) {
                matrix.preRotate(rotationInDegrees);
            }

            return Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.msg_exception_io), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return null;
        }
    }

    // TODO - debug
    private void addImageToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);

        Toast.makeText(this, "Image added to gallery", Toast.LENGTH_SHORT).show();
    }

    /*
     *  ====================== HELPER METHODS ======================
     */

    private void updateDateDisplay() {
        mDateDisplay.setText(sdf.format(mCal.getTime()));
    }

    private Quintuple<Long, String, String, BigDecimal, String> extractInputData() throws
            NumberFormatException, MissingNameException {
        // Get input data
        EditText input_name = (EditText) findViewById(R.id.input_expenseName);
        Spinner spn_category = (Spinner) findViewById(R.id.spn_expenseCategory);
        EditText input_amount = (EditText) findViewById(R.id.input_expenseAmt);
        Spinner spn_currency =  (Spinner) findViewById(R.id.spn_currency);

        // Extract input data
        long date = mCal.getTimeInMillis();
        String name = input_name.getText().toString().trim();
        String category = spn_category.getSelectedItem().toString().trim();
        BigDecimal amount = new BigDecimal(input_amount.getText().toString().trim());
        String currency = spn_currency.getSelectedItem().toString().trim();

        if (name.equals("")) {
            throw new MissingNameException(getString(R.string.msg_missingName));
        }

        return new Quintuple<>(date, name, category, amount, currency);
    }

    private String extractInputCountry() {
        Spinner spn_country = (Spinner) findViewById(R.id.spn_expenseCountry);
        return spn_country.getSelectedItem().toString().trim();
    }

    // Filters the list of currencies to be displayed in the Spinner
    private ArrayList<String> filterHiddenCurrencies(ArrayList<String> list, SharedPreferences mPrefs) {
        ArrayList<String> currencyList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.arr_currency_entries)));

        for (int i = 0; i < currencyList.size(); i++) {
            if (!mPrefs.getBoolean(currencyList.get(i), true)) { // if boolean is false, remove the currency
                list.remove(currencyList.get(i));
            }
        }

        return list;
    }

    // Sets the month to that of the expense being added/edited
    private void setDisplayMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        displayMonth = cal.get(Calendar.MONTH) + 1;
    }

    // Sets the category spinner to the category of the edited expense
    private void setCategorySpinner(Spinner spinner, String category) {
        switch (category) {
            case "Accommodation":
                spinner.setSelection(0);
                break;
            case "Food":
                spinner.setSelection(1);
                break;
            case "Gifts":
                spinner.setSelection(2);
                break;
            case "Leisure":
                spinner.setSelection(3);
                break;
            case "Misc":
                spinner.setSelection(4);
                break;
            case "Shopping":
                spinner.setSelection(5);
                break;
            case "Travel":
                spinner.setSelection(6);
                break;
        }
    }

    // Sets the currency spinner to the currency of the edited expense
    private void setCurrencySpinner(Spinner spinner, String currency) {
        switch (currency) {
            case "ALL":
                spinner.setSelection(0); break;
            case "BAM":
                spinner.setSelection(1); break;
            case "BGN":
                spinner.setSelection(2); break;
            case "BYN":
                spinner.setSelection(3); break;
            case "CHF":
                spinner.setSelection(4); break;
            case "CZK":
                spinner.setSelection(5); break;
            case "DKK":
                spinner.setSelection(6); break;
            case "EUR":
                spinner.setSelection(7); break;
            case "GBP":
                spinner.setSelection(8); break;
            case "HRK":
                spinner.setSelection(9); break;
            case "HUF":
                spinner.setSelection(10); break;
            case "MKD":
                spinner.setSelection(11); break;
            case "NOK":
                spinner.setSelection(12); break;
            case "PLN":
                spinner.setSelection(13); break;
            case "RON":
                spinner.setSelection(14); break;
            case "RSD":
                spinner.setSelection(15); break;
            case "SEK":
                spinner.setSelection(16); break;
            case "SGD":
                spinner.setSelection(17); break;
            case "TRY":
                spinner.setSelection(18); break;
        }
    }

    // Sets the country spinner to the country of the edited expense
    private void setCountrySpinner(Spinner spinner, String country) {
        switch (country) {
            case "Albania":
                spinner.setSelection(0); break;
            case "Austria":
                spinner.setSelection(1); break;
            case "Belarus":
                spinner.setSelection(2); break;
            case "Belgium":
                spinner.setSelection(3); break;
            case "Bosnia and Herzegovina":
                spinner.setSelection(4); break;
            case "Bulgaria":
                spinner.setSelection(5); break;
            case "Croatia":
                spinner.setSelection(6); break;
            case "Czech Republic":  case "Czechia":
                spinner.setSelection(7); break;
            case "Denmark":
                spinner.setSelection(8); break;
            case "Estonia":
                spinner.setSelection(9); break;
            case "Faroe Islands":
                spinner.setSelection(10); break;
            case "Finland":
                spinner.setSelection(11); break;
            case "France":
                spinner.setSelection(12); break;
            case "Germany":
                spinner.setSelection(13); break;
            case "Hungary":
                spinner.setSelection(14); break;
            case "Italy":
                spinner.setSelection(15); break;
            case "Latvia":
                spinner.setSelection(16); break;
            case "Liechtenstein":
                spinner.setSelection(17); break;
            case "Lithuania":
                spinner.setSelection(18); break;
            case "Luxembourg":
                spinner.setSelection(19); break;
            case "Macedonia (FYROM)":
                spinner.setSelection(20); break;
            case "Montenegro":
                spinner.setSelection(21); break;
            case "Netherlands":
                spinner.setSelection(22); break;
            case "Norway":
                spinner.setSelection(23); break;
            case "Poland":
                spinner.setSelection(24); break;
            case "Romania":
                spinner.setSelection(25); break;
            case "Serbia":
                spinner.setSelection(26); break;
            case "Singapore":
                spinner.setSelection(27); break;
            case "Slovenia":
                spinner.setSelection(28); break;
            case "Spain":
                spinner.setSelection(29); break;
            case "Sweden":
                spinner.setSelection(30); break;
            case "Switzerland":
                spinner.setSelection(31); break;
            case "Turkey":
                spinner.setSelection(32); break;
            case "United Kingdom":
                spinner.setSelection(33); break;
        }
    }

    /*
     *  ====================== STORAGE METHODS ======================
     */

    private void saveImageToInternalStorage(Bitmap imageBitmap) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(currentPhotoPath);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos); // Write image to OutputStream at 100% quality
            System.out.println("File saved to internal storage");
        } catch (FileNotFoundException e) {
            Toast.makeText(this, getString(R.string.msg_exception_fileNotFound), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                Toast.makeText(this, getString(R.string.msg_exception_io), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    private void deleteImageFromInternalStorage() {
        File file = new File(imagePath);
        if (file.delete())
            System.out.println("File at " + imagePath + " deleted");
    }

    private void loadImageFromStorage(ImageView imageView) {
        try {
            File file = new File(imagePath);
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            imageView.setImageBitmap(bitmap);
            System.out.println("Image loaded from " + imagePath);
        } catch (FileNotFoundException e) {
            Toast.makeText(this, getString(R.string.msg_exception_fileNotFound), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
