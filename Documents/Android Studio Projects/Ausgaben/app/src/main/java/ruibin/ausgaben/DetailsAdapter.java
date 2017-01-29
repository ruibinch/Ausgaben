package ruibin.ausgaben;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ruibin.ausgaben.database.Expense;

/**
 * Created by Ruibin on 2/1/2017.
 */

class DetailsAdapter extends ArrayAdapter<Expense> {

    private SimpleDateFormat sdf = new SimpleDateFormat("dd MMM");

    private final Context context;
    private final ArrayList<Expense> list;

    private long newExpenseId = -1;

    private long editExpenseId = -1;
    private boolean isDateEdited;
    private boolean isNameEdited;
    private boolean isCategoryEdited;
    private boolean isAmountEdited;
    private boolean isCurrencyEdited;


    DetailsAdapter(Context context, ArrayList<Expense> list) {
        super(context, R.layout.row_expenseslist, list);

        this.context = context;
        this.list = list;
    }

    DetailsAdapter(Context context, ArrayList<Expense> list, long newExpenseId) {
        super(context, R.layout.row_expenseslist, list);

        this.context = context;
        this.list = list;
        this.newExpenseId = newExpenseId;
    }

    DetailsAdapter(Context context, ArrayList<Expense> list, long editExpenseId,
                   boolean isDateEdited, boolean isNameEdited, boolean isCategoryEdited, boolean isAmountEdited, boolean isCurrencyEdited) {
        super(context, R.layout.row_expenseslist, list);

        this.context = context;
        this.list = list;
        this.editExpenseId = editExpenseId;
        this.isDateEdited = isDateEdited;
        this.isNameEdited = isNameEdited;
        this.isCategoryEdited = isCategoryEdited;
        this.isAmountEdited = isAmountEdited;
        this.isCurrencyEdited = isCurrencyEdited;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        // Create inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Get rowView from inflater
        View rowView = inflater.inflate(R.layout.row_expenseslist, parent, false);

        // Get the Views from the RowView
        ImageView categoryIconView = (ImageView) rowView.findViewById(R.id.list_icon);
        TextView dateView = (TextView) rowView.findViewById(R.id.list_date);
        TextView nameView = (TextView) rowView.findViewById(R.id.list_name);
        TextView amountView = (TextView) rowView.findViewById(R.id.list_amount);

        // Set the text for the TextViews
        setIconView(categoryIconView, list.get(pos).getCategory());
        dateView.setText(sdf.format(new Date(list.get(pos).getDate())));
        nameView.setText(list.get(pos).getName());
        // Set the currency display - depending on the currency, the currency sign can be before or after the value
        String currency = list.get(pos).getCurrency();
        amountView.setText(list.get(pos).getAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        amountView.append(getCurrencySign(currency));


        // Highlight the added item or the edited details
        int colourDarkGreen = ContextCompat.getColor(parent.getContext(), R.color.colorDarkGreen);
        if (newExpenseId != -1) { // if a new expense was recently added, highlight it
            if (list.get(pos).getId() == newExpenseId) {
                categoryIconView.setColorFilter(colourDarkGreen);
                dateView.setTextColor(colourDarkGreen);
                nameView.setTextColor(colourDarkGreen);
                amountView.setTextColor(colourDarkGreen);
            }
        } else if (editExpenseId != -1) { // if an expense was edited, highlight the edited items
            if (list.get(pos).getId() == editExpenseId) {
                if (isCategoryEdited)
                    categoryIconView.setColorFilter(colourDarkGreen);
                if (isDateEdited)
                    dateView.setTextColor(colourDarkGreen);
                if (isNameEdited)
                    nameView.setTextColor(colourDarkGreen);
                if (isAmountEdited || isCurrencyEdited)
                    amountView.setTextColor(colourDarkGreen);
            }
        }

        return rowView;
    }

    /*
     * ====================== HELPER METHODS ======================
     */

    private void setIconView(ImageView iconView, String category) {
        switch (category) {
            case "Food" :
                iconView.setImageResource(R.drawable.ic_food);
                break;
            case "Gifts" :
                iconView.setImageResource(R.drawable.ic_gifts);
                break;
            case "Leisure" :
                iconView.setImageResource(R.drawable.ic_leisure);
                break;
            case "Misc" :
                iconView.setImageResource(R.drawable.ic_misc);
                break;
            case "Shopping" :
                iconView.setImageResource(R.drawable.ic_shopping);
                break;
            case "Travel" :
                iconView.setImageResource(R.drawable.ic_travel);
                break;
        }
    }

    private String getCurrencySign(String currency) {
        switch (currency) {
            case "ALL" :
                return " L";
            case "BAM" :
                return " KM";
            case "BGN" :
                return " лв";
            case "BYN" :
                return " Br";
            case "CHF" :
                return " CHF";
            case "CZK" :
                return " Kč";
            case "DKK" :
                return " kr";
            case "EUR" :
                return " €";
            case "GBP" :
                return " £";
            case "HRK" :
                return " kn";
            case "HUF" :
                return " Ft";
            case "MKD" :
                return " ден";
            case "NOK" :
                return " kr";
            case "PLN" :
                return " zł";
            case "RON" :
                return " lei";
            case "RSD" :
                return " din.";
            case "SEK" :
                return " kr";
            case "SGD" :
                return " S$";
            case "TRY" :
                return " ₺";
        }
        return "";
    }

}