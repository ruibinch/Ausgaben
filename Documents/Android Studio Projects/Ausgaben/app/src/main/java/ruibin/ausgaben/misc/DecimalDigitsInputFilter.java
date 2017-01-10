package ruibin.ausgaben.misc;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ruibin on 1/1/2017.
 */

public class DecimalDigitsInputFilter implements InputFilter {

    Pattern mPattern;

    public DecimalDigitsInputFilter(int digitsBeforeDecimal, int digitsAfterDecimal) {
        mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeDecimal-1) + "}+((\\.[0-9]{0," +
                (digitsAfterDecimal-1) + "})?)||(\\.)?");
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dStart, int dEnd) {
        Matcher matcher = mPattern.matcher(dest);
        if (!matcher.matches())
            return "";
        return null;
    }
}
