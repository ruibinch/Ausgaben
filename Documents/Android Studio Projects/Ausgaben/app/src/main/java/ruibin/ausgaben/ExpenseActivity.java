package ruibin.ausgaben;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ruibin.ausgaben.misc.DecimalDigitsInputFilter;
import ruibin.ausgaben.misc.Quadruple;
import ruibin.ausgaben.database.DatabaseExpenses;
import ruibin.ausgaben.database.Expense;
import ruibin.ausgaben.exceptions.MissingNameException;

public class ExpenseActivity extends Activity {

    private SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
    private Calendar mCal = Calendar.getInstance();
    private TextView mDateDisplay;

    private DatabaseExpenses database;

    // For editing purposes
    private boolean isEditExpense;
    private long editExpenseId;
    private int selectedMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        // Initalisation methods
        openDatabase();
        setDisplays();
        populateDataFromBundle();
        setDeleteButtonVisibility();

        // TODO - include different currencies and the corresponding forex rate
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
        // Set the spinner's default value to 'Food'
        Spinner spinner = (Spinner) findViewById(R.id.spn_expenseCategory);
        spinner.setSelection(1);

        // Sets the input amount to 2 decimal places
        EditText editText = (EditText) findViewById(R.id.input_expenseAmt);
        editText.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(6,2)});

        // Sets the date to today's date by default
        mDateDisplay = (TextView) findViewById(R.id.input_expenseDate);
        updateDateDisplay();
    }

    // Populates the fields with data if applicable (i.e. editing an existing expense)
    private void populateDataFromBundle() {
         Bundle bundle = getIntent().getExtras();
         if (bundle != null) {
             isEditExpense = true;
             editExpenseId = bundle.getLong("id");

             Date expenseDate = new Date(bundle.getLong("date"));
             mCal.setTime(expenseDate);
             updateDateDisplay();
             setSelectedMonth(expenseDate);

             EditText editName = (EditText) findViewById(R.id.input_expenseName);
             editName.setText(bundle.getString("name"));

             Spinner spinner = (Spinner) findViewById(R.id.spn_expenseCategory);
             switch (bundle.getString("category")) {
                 case "Entertainment" :
                     spinner.setSelection(0);
                     break;
                 case "Food" :
                     spinner.setSelection(1);
                     break;
                 case "Gifts" :
                     spinner.setSelection(2);
                     break;
                 case "Misc" :
                     spinner.setSelection(3);
                     break;
                 case "Shopping" :
                     spinner.setSelection(4);
                     break;
                 case "Travel" :
                     spinner.setSelection(5);
                     break;
             }

             EditText editAmount = (EditText) findViewById(R.id.input_expenseAmt);
             editAmount.setText(bundle.getString("amount"));
        }
    }

    // Sets the 'Delete' button to be visible if applicable
    private void setDeleteButtonVisibility() {
        if (isEditExpense) {
            Button delButton = (Button) findViewById(R.id.btn_delete);
            delButton.setVisibility(View.VISIBLE);
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
            intent.putExtra("month", selectedMonth);
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

    public void onClickCancel(View view) {
        if (!isEditExpense) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra("month", selectedMonth);
            startActivity(intent);
        }
    }

    public void onClickSave(View view) {
        try {
            Quadruple<Long, String, String, BigDecimal> quad = extractInputData();
            setSelectedMonth(new Date(quad.getFirst()));
            Intent intent = new Intent(this, DetailsActivity.class);

            if (!isEditExpense) {
                Expense newExpense = database.createExpense(quad.getFirst(), quad.getSecond(),
                        quad.getThird(), quad.getFourth());
                intent.putExtra("newExpenseId", newExpense.getId());
                intent.putExtra("source", "ExpenseActivity");
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_expenseAdded), Toast.LENGTH_SHORT).show();
            } else {
                boolean[] isEditsMade = database.editExpense(editExpenseId, quad.getFirst(), quad.getSecond(),
                        quad.getThird(), quad.getFourth());
                intent.putExtra("editExpenseId", editExpenseId);
                intent.putExtra("isDateEdited", isEditsMade[0]);
                intent.putExtra("isNameEdited", isEditsMade[1]);
                intent.putExtra("isCategoryEdited", isEditsMade[2]);
                intent.putExtra("isAmountEdited", isEditsMade[3]);
                Toast.makeText(getApplicationContext(), "Expense '" + quad.getSecond() + "' edited", Toast.LENGTH_SHORT).show();
            }

            intent.putExtra("month", selectedMonth); // To display the correct month in DetailsActivity
            startActivity(intent);
        } catch (NumberFormatException e) {
            Toast.makeText(getApplicationContext(), getString(R.string.message_missingAmt), Toast.LENGTH_SHORT).show();
        } catch (MissingNameException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickDelete(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.dialog_deleteConfirmation));

        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            String deletedExpenseName = database.deleteExpense(editExpenseId);
            Toast.makeText(getApplicationContext(), "Expense '" + deletedExpenseName + "' deleted", Toast.LENGTH_SHORT).show();
            dialog.cancel();

            Intent intent = new Intent(ExpenseActivity.this, DetailsActivity.class);
            intent.putExtra("month", selectedMonth);
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
     *  ====================== HELPER METHODS ======================
     */

    private void updateDateDisplay() {
        mDateDisplay.setText(sdf.format(mCal.getTime()));
    }

    private Quadruple<Long, String, String, BigDecimal> extractInputData() throws
            NumberFormatException, MissingNameException {
        // Get input data
        EditText input_name = (EditText) findViewById(R.id.input_expenseName);
        Spinner spn_category = (Spinner) findViewById(R.id.spn_expenseCategory);
        EditText input_amount = (EditText) findViewById(R.id.input_expenseAmt);

        // Extract input data
        long date = mCal.getTimeInMillis();
        String name = input_name.getText().toString().trim();
        String category = spn_category.getSelectedItem().toString().trim();
        BigDecimal amount = new BigDecimal(input_amount.getText().toString().trim());

        if (name.equals("")) {
            throw new MissingNameException(getString(R.string.message_missingName));
        }

        return new Quadruple<>(date, name, category, amount);
    }

    // Sets the month to that of the expense being added/edited
    private void setSelectedMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        selectedMonth = cal.get(Calendar.MONTH) + 1;
    }

}
