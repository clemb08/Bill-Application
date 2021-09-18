package app.kenavo.billapplication.utils;

import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationFields {

    public static final Pattern VALID_ADDRESS =
            Pattern.compile("^[A-Z0-9._%+-]+,[A-Z0-9.-]$", Pattern.CASE_INSENSITIVE);
    public static final Pattern VALID_EMAIL_ADDRESS =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    public static  final Pattern VALID_PHONE_NUMBER =
            Pattern.compile("(\\+\\d{1,3}( )?)?+\\d{9}$");
    public static final String REQUIRED_FIELD = "You need to fill this field";
    public static final String BAD_ADDRESS = "Please, provide a valid address (85 rue Douai, 75009 Paris";
    public static final String BAD_EMAIL = "Please, provide a valid email address";
    public static final String BAD_PHONE = "Please, provide a valid phone number (+33 6XXXXXXXX)";

    public static boolean checkRequired(Map<TextField, String> map, Text text, TextField textField) {
        if(textField.getText() == null) {
            map.put(textField, REQUIRED_FIELD);
            text.setText(REQUIRED_FIELD);
            text.setVisible(true);
            return false;
        } else if(map.containsKey(textField)) {
            map.remove(textField, REQUIRED_FIELD);
            text.setVisible(false);
            return true;
        } else {
            return true;
        }
    }

    public static void checkAddress(Map<TextField, String> map, Text text, TextField textField) {
        if(textField.getText() != null) {
            Matcher matcher = VALID_ADDRESS.matcher(textField.getText());
            if (!matcher.matches()) {
                map.put(textField, BAD_ADDRESS);
                text.setText(BAD_ADDRESS);
                text.setVisible(true);
            } else if(map.containsKey(textField)) {
                map.remove(textField, BAD_ADDRESS);
                text.setVisible(false);
            }
        }
    }


    public static void checkEmail(Map<TextField, String> map, Text text, TextField textField) {
        System.out.println(textField.getText());
        if(textField.getText() != null) {
            Matcher matcher = VALID_EMAIL_ADDRESS.matcher(textField.getText());
            if (!matcher.matches()) {
                map.put(textField, BAD_EMAIL);
                text.setText(BAD_EMAIL);
                text.setVisible(true);
            } else if(map.containsKey(textField)) {
                map.remove(textField, BAD_EMAIL);
                text.setVisible(false);
            }
        }
    }

    public static void checkPhone(Map<TextField, String> map, Text text, TextField textField) {
        if(textField.getText() != null) {
            Matcher matcher = VALID_PHONE_NUMBER.matcher(textField.getText());
            if (!matcher.matches()) {
                map.put(textField, BAD_PHONE);
                text.setText(BAD_PHONE);
                text.setVisible(true);
            } else if(map.containsKey(textField)) {
                map.remove(textField, BAD_PHONE);
                text.setVisible(false);
            }
        }
    }


}
