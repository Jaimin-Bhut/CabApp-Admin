package com.jb.dev.cabapp_admin.helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {
    private static Context context;
    private static ProgressDialog progressDialog;

    public Helper(Context context) {
        this.context = context;
    }

    public static void toast(Context context, String msg) {
        Toast.makeText(context, "" + msg, Toast.LENGTH_SHORT).show();
    }

    public static boolean validDriverEmail(@Nullable String email) {
        if (email.length() == 0) {
            return false;
        } else {
            return email.endsWith("driver.com");
        }
    }

    public static boolean validEmail(String editText) {
        if (editText.length() == 0) {
            return false;
        } else {
            String Email_Pattern = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                    "[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                    "A-Z]{2,7}$";
            Pattern pattern = Pattern.compile(Email_Pattern);
            Matcher matcher = pattern.matcher(editText);
            return matcher.matches();
        }
    }

    public static boolean validPhoneNumber(String editText) throws NumberFormatException {
        if (editText.length() < 10) {
            return false;
        } else {
            String postCodePattrn = "^[7-9]\\d{9}$";
            String postCodeInput = editText.trim();
            return postCodeInput.matches(postCodePattrn);
        }
    }

    public static boolean isValidText(String str) {
        if (str.length() < 3) {
            return false;
        } else {
            String expression = "^[a-zA-Z ]+$";
            CharSequence inputStr = str;
            Pattern pattern = Pattern.compile(expression);
            Matcher matcher = pattern.matcher(inputStr);
            return matcher.matches();
        }
    }

    public static boolean isValidCardNumber(String str) {
        if (str.length() < 19) {
            return false;
        } else {
            String expression = "^[0-9 ]+$";
            CharSequence ch = str;
            Pattern pattern = Pattern.compile(expression);
            Matcher matcher = pattern.matcher(ch);
            return matcher.matches();
        }
    }

    public static boolean isValidDigit(String str) {
        if (str.length() == 0) {
            return false;
        } else {
            String expression = "^[0-9 ]+$";
            CharSequence inputStr = str;
            Pattern pattern = Pattern.compile(expression);
            Matcher matcher = pattern.matcher(inputStr);
            return matcher.matches();
        }
    }

    public static boolean isValidPerCapacity(String str) {
        if (str.length() == 0) {
            return false;
        } else if (Integer.parseInt(str) > 9) {
            return false;
        } else {
            String expression = "^[0-9 ]+$";
            CharSequence inputStr = str;
            Pattern pattern = Pattern.compile(expression);
            Matcher matcher = pattern.matcher(inputStr);
            return matcher.matches();
        }
    }


    public static boolean isValidLaugage(String str) {
        if (str.length() == 0) {
            return false;
        } else {
            String expression = "^[0-9]+$";
            CharSequence inputStr = str;
            Pattern pattern = Pattern.compile(expression);
            Matcher matcher = pattern.matcher(inputStr);
            return matcher.matches();
        }
    }

    public static String insertPeriodically(String text, String insert, int period) {
        StringBuilder builder = new StringBuilder(text.length() + insert.length() * (text.length() / period) + 1);
        int index = 0;
        String prefix = "";
        while (index < text.length()) {
            builder.append(prefix);
            prefix = insert;
            builder.append(text.substring(index, Math.min(index + period, text.length())));
            index += period;
        }
        return builder.toString();
    }

    public static boolean isValidTextDigit(String str) {
        if (str.length() == 0) {
            return false;
        } else {
            String expression = "^[a-zA-Z0-9 ]+$";
            CharSequence inputStr = str;
            Pattern pattern = Pattern.compile(expression);
            Matcher matcher = pattern.matcher(inputStr);
            return matcher.matches();
        }
    }

    public static boolean isValidAddress(String str) {
        if (str.length() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public static void showProgressDialog(Context context) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
    }

    //-----------------password validation
    public static boolean validPassword(String editText)
            throws NumberFormatException {
        if (editText.length() < 8 || editText.trim().equals("")) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean validNumberPlate(String text) {
        if (text.length() < 10) {
            return false;
        } else {
            String expression = "^[GJ]{2}[0-9]{2}[A-Z]{2}[0-9]{4}+$";
            CharSequence inputStr = text;
            Pattern pattern = Pattern.compile(expression);
            Matcher matcher = pattern.matcher(inputStr);
            return matcher.matches();
        }
    }

    public static void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

}
