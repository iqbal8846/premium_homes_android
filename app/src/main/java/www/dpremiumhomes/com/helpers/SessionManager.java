package www.dpremiumhomes.com.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SessionManager {

    private static final String PREF_NAME = "app_session";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_SAVED_PROPERTIES = "saved_properties"; // store as comma-separated
    private static final String KEY_FLAT_DETAILS = "flat_details"; // store as JSON string

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    /** -------------------- LOGIN -------------------- **/
    public void saveUser(String id, String name, String email, String phone, String token, JSONObject userObj) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, id);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_TOKEN, token);

        // Save savedProperties
        Set<String> savedProperties = new HashSet<>();
        try {
            if (userObj.has("savedProperties")) {
                for (int i = 0; i < userObj.getJSONArray("savedProperties").length(); i++) {
                    savedProperties.add(userObj.getJSONArray("savedProperties").getString(i));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editor.putStringSet(KEY_SAVED_PROPERTIES, savedProperties);

        // Save flatDetails as JSON string
        try {
            if (userObj.has("flatDetails")) {
                JSONObject flatDetails = userObj.getJSONObject("flatDetails");
                editor.putString(KEY_FLAT_DETAILS, flatDetails.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        editor.apply();
    }

    /** -------------------- GETTERS -------------------- **/
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserId() {
        return pref.getString(KEY_USER_ID, "");
    }

    public String getName() {
        return pref.getString(KEY_NAME, "");
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL, "");
    }

    public String getPhone() {
        return pref.getString(KEY_PHONE, "");
    }

    public String getToken() {
        return pref.getString(KEY_TOKEN, "");
    }

    public Set<String> getSavedProperties() {
        return pref.getStringSet(KEY_SAVED_PROPERTIES, new HashSet<>());
    }

    public JSONObject getFlatDetails() {
        String jsonStr = pref.getString(KEY_FLAT_DETAILS, "{}");
        try {
            return new JSONObject(jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    /** -------------------- LOGOUT -------------------- **/
    public void logout() {
        editor.clear();
        editor.apply();
    }
}
