package com.ausgaben;

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

import com.ausgaben.database.Expense;

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
    private boolean isImagePathEdited;

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
                   boolean isDateEdited, boolean isNameEdited, boolean isCategoryEdited, boolean isAmountEdited,
                   boolean isCurrencyEdited, boolean isImagePathEdited) {
        super(context, R.layout.row_expenseslist, list);

        this.context = context;
        this.list = list;
        this.editExpenseId = editExpenseId;
        this.isDateEdited = isDateEdited;
        this.isNameEdited = isNameEdited;
        this.isCategoryEdited = isCategoryEdited;
        this.isAmountEdited = isAmountEdited;
        this.isCurrencyEdited = isCurrencyEdited;
        this.isImagePathEdited = isImagePathEdited;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        // Create inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Get rowView from inflater
        View rowView = inflater.inflate(R.layout.row_expenseslist, parent, false);

        // Get the Views from the RowView
        ImageView countryIconView = (ImageView) rowView.findViewById(R.id.list_countryIcon);
        ImageView categoryIconView = (ImageView) rowView.findViewById(R.id.list_categoryIcon);
        TextView dateView = (TextView) rowView.findViewById(R.id.list_date);
        TextView nameView = (TextView) rowView.findViewById(R.id.list_name);
        TextView amountView = (TextView) rowView.findViewById(R.id.list_amount);

        // Set the text for the TextViews
        setCountryIconView(countryIconView, list.get(pos).getCountry());
        setCategoryIconView(categoryIconView, list.get(pos).getCategory());
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
        } else if (editExpenseId != -1) { // if an expense was edited, highlight the specific information that was modified
            if (list.get(pos).getId() == editExpenseId) {
                if (isCategoryEdited)
                    categoryIconView.setColorFilter(colourDarkGreen);
                if (isDateEdited)
                    dateView.setTextColor(colourDarkGreen);
                if (isNameEdited)
                    nameView.setTextColor(colourDarkGreen);
                if (isAmountEdited || isCurrencyEdited)
                    amountView.setTextColor(colourDarkGreen);
                if (isImagePathEdited) {
                    categoryIconView.setColorFilter(colourDarkGreen);
                    dateView.setTextColor(colourDarkGreen);
                    nameView.setTextColor(colourDarkGreen);
                    amountView.setTextColor(colourDarkGreen);
                }
            }
        }

        return rowView;
    }

    /*
     * ====================== HELPER METHODS ======================
     */

    // Sets the correct country flag to be displayed
    private void setCountryIconView(ImageView iconView, String country) {
        switch (country) {
            case "Albania":
                iconView.setImageResource(R.drawable.flag_albania); break;
            case "Austria":
                iconView.setImageResource(R.drawable.flag_austria); break;
            case "Belarus":
                iconView.setImageResource(R.drawable.flag_belarus); break;
            case "Belgium":
                iconView.setImageResource(R.drawable.flag_belgium); break;
            case "Bosnia and Herzegovina":
                iconView.setImageResource(R.drawable.flag_bosnia); break;
            case "Bulgaria":
                iconView.setImageResource(R.drawable.flag_bulgaria); break;
            case "Croatia":
                iconView.setImageResource(R.drawable.flag_croatia); break;
            case "Czech Republic": case "Czechia":
                iconView.setImageResource(R.drawable.flag_czech); break;
            case "Denmark":
                iconView.setImageResource(R.drawable.flag_denmark); break;
            case "Estonia":
                iconView.setImageResource(R.drawable.flag_estonia); break;
            case "Faroe Islands":
                iconView.setImageResource(R.drawable.flag_faroe); break;
            case "Finland":
                iconView.setImageResource(R.drawable.flag_finland); break;
            case "France":
                iconView.setImageResource(R.drawable.flag_france); break;
            case "Germany":
                iconView.setImageResource(R.drawable.flag_germany); break;
            case "Hungary":
                iconView.setImageResource(R.drawable.flag_hungary); break;
            case "Italy":
                iconView.setImageResource(R.drawable.flag_italy); break;
            case "Latvia":
                iconView.setImageResource(R.drawable.flag_latvia); break;
            case "Liechtenstein":
                iconView.setImageResource(R.drawable.flag_liechtenstein); break;
            case "Lithuania":
                iconView.setImageResource(R.drawable.flag_lithuania); break;
            case "Luxembourg":
                iconView.setImageResource(R.drawable.flag_luxembourg); break;
            case "Macedonia (FYROM)":
                iconView.setImageResource(R.drawable.flag_macedonia); break;
            case "Netherlands":
                iconView.setImageResource(R.drawable.flag_netherlands); break;
            case "Norway":
                iconView.setImageResource(R.drawable.flag_norway); break;
            case "Poland":
                iconView.setImageResource(R.drawable.flag_poland); break;
            case "Romania":
                iconView.setImageResource(R.drawable.flag_romania); break;
            case "Serbia":
                iconView.setImageResource(R.drawable.flag_serbia); break;
            case "Singapore":
                iconView.setImageResource(R.drawable.flag_singapore); break;
            case "Slovenia":
                iconView.setImageResource(R.drawable.flag_slovenia); break;
            case "Spain":
                iconView.setImageResource(R.drawable.flag_spain); break;
            case "Sweden":
                iconView.setImageResource(R.drawable.flag_sweden); break;
            case "Switzerland":
                iconView.setImageResource(R.drawable.flag_switzerland); break;
            case "Turkey":
                iconView.setImageResource(R.drawable.flag_turkey); break;
            case "United Kingdom":
                iconView.setImageResource(R.drawable.flag_uk); break;
        }
    }

    // Sets the correct category icon to be displayed
    private void setCategoryIconView(ImageView iconView, String category) {
        switch (category) {
            case "Accommodation" :
                iconView.setImageResource(R.drawable.ic_accommodation); break;
            case "Food" :
                iconView.setImageResource(R.drawable.ic_food); break;
            case "Gifts" :
                iconView.setImageResource(R.drawable.ic_gifts); break;
            case "Leisure" :
                iconView.setImageResource(R.drawable.ic_leisure); break;
            case "Misc" :
                iconView.setImageResource(R.drawable.ic_misc); break;
            case "Shopping" :
                iconView.setImageResource(R.drawable.ic_shopping); break;
            case "Travel" :
                iconView.setImageResource(R.drawable.ic_travel); break;
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
