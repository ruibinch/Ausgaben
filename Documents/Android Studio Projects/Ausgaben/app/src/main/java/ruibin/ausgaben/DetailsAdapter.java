package ruibin.ausgaben;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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


    DetailsAdapter(Context context, ArrayList<Expense> list) {
        super(context, R.layout.row, list);

        this.context = context;
        this.list = list;
    }

    DetailsAdapter(Context context, ArrayList<Expense> list, long newExpenseId) {
        super(context, R.layout.row, list);

        this.context = context;
        this.list = list;
        this.newExpenseId = newExpenseId;
    }

    DetailsAdapter(Context context, ArrayList<Expense> list, long editExpenseId,
                   boolean isDateEdited, boolean isNameEdited, boolean isCategoryEdited, boolean isAmountEdited) {
        super(context, R.layout.row, list);

        this.context = context;
        this.list = list;
        this.editExpenseId = editExpenseId;
        this.isDateEdited = isDateEdited;
        this.isNameEdited = isNameEdited;
        this.isCategoryEdited = isCategoryEdited;
        this.isAmountEdited = isAmountEdited;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        // Create inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Get rowView from inflater
        View rowView = inflater.inflate(R.layout.row, parent, false);

        // Get the Views from the RowView
        ImageView categoryIconView = (ImageView) rowView.findViewById(R.id.list_icon);
        TextView dateView = (TextView) rowView.findViewById(R.id.list_date);
        TextView nameView = (TextView) rowView.findViewById(R.id.list_name);
        TextView amountView = (TextView) rowView.findViewById(R.id.list_amount);

        // Set the text for the TextViews
        setIconView(categoryIconView, list.get(pos).getCategory());
        dateView.setText(sdf.format(new Date(list.get(pos).getDate())));
        nameView.setText(list.get(pos).getName());
        amountView.setText("$" + list.get(pos).getAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString());

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
                if (isAmountEdited)
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
            case "Entertainment" :
                iconView.setImageResource(R.drawable.ic_entertainment);
                break;
            case "Food" :
                iconView.setImageResource(R.drawable.ic_food);
                break;
            case "Gifts" :
                iconView.setImageResource(R.drawable.ic_gifts);
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

}
