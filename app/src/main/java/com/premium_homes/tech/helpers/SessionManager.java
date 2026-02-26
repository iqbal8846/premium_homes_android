package com.premium_homes.tech.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class SessionManager {

    private static final String PREF_NAME = "app_session";
    private static final String TAG = "SESSION_MANAGER";

    // Keys
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_FLAT_DETAILS = "flat_details";
    private static final String KEY_USER_JSON = "user_json";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    /**
     * Save user data after successful login
     */
    public void saveUser(String id, String name, String email, String phone, String token, JSONObject userObj) {
        try {
            editor.putBoolean(KEY_IS_LOGGED_IN, true);
            editor.putString(KEY_USER_ID, id); // Store as String, but be aware it's a number in API
            editor.putString(KEY_NAME, name);
            editor.putString(KEY_EMAIL, email);
            editor.putString(KEY_PHONE, phone);
            editor.putString(KEY_TOKEN, token);

            // Save the entire user object for reference
            editor.putString(KEY_USER_JSON, userObj.toString());

            // Process and save flat details
            JSONObject flatDetails = extractFlatDetails(userObj);
            editor.putString(KEY_FLAT_DETAILS, flatDetails.toString());

            Log.d(TAG, "User saved successfully. Flats found: " + flatDetails.length());

            // Log the first flat details for verification
            if (flatDetails.length() > 0) {
                String firstKey = flatDetails.keys().next();
                Log.d(TAG, "Sample flat data: " + flatDetails.getJSONObject(firstKey).toString());
            }

        } catch (Exception e) {
            Log.e(TAG, "Error saving user: " + e.getMessage());
            e.printStackTrace();
        } finally {
            editor.apply();
        }
    }

    /**
     * Extract flat details from user object
     */
    private JSONObject extractFlatDetails(JSONObject userObj) {
        JSONObject flatDetails = new JSONObject();

        try {
            // Check for savedProperties array
            if (userObj.has("savedProperties")) {
                JSONArray flatsArray = userObj.getJSONArray("savedProperties");
                Log.d(TAG, "Found savedProperties array with length: " + flatsArray.length());

                for (int i = 0; i < flatsArray.length(); i++) {
                    JSONObject flat = flatsArray.getJSONObject(i);

                    // Get ID - handle both String and Integer
                    String flatId;
                    if (flat.has("id")) {
                        Object idObj = flat.get("id");
                        flatId = String.valueOf(idObj); // Convert to String safely
                    } else {
                        Log.e(TAG, "Flat missing ID field");
                        continue;
                    }

                    // Ensure all expected fields exist with defaults
                    JSONObject processedFlat = ensureFlatFields(flat);
                    flatDetails.put(flatId, processedFlat);

                    Log.d(TAG, "Added flat ID: " + flatId + " - " + processedFlat.optString("name", "Unknown"));
                }
            } else {
                Log.d(TAG, "No savedProperties found in user object");
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error extracting flat details: " + e.getMessage());
            e.printStackTrace();
        }

        return flatDetails;
    }

    /**
     * Ensure all required flat fields exist with defaults
     */
    private JSONObject ensureFlatFields(JSONObject flat) {
        try {
            // List of fields that should exist with defaults if missing
            String[] requiredFields = {
                    "id", "name", "location", "image", "flatNo", "flatSize", "price",
                    "videoLink", "videoLink2", "moneyReceiptLink", "purchaseDetailsLink"
            };

            for (String field : requiredFields) {
                if (!flat.has(field)) {
                    flat.put(field, "");
                    Log.d(TAG, "Added missing field: " + field);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return flat;
    }

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Get user ID (returns as String, but was number in API)
     */
    public String getUserId() {
        return pref.getString(KEY_USER_ID, "");
    }

    /**
     * Get user ID as integer (useful for API calls)
     */
    public int getUserIdInt() {
        try {
            return Integer.parseInt(getUserId());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Get user name
     */
    public String getName() {
        return pref.getString(KEY_NAME, "");
    }

    /**
     * Get user email
     */
    public String getEmail() {
        return pref.getString(KEY_EMAIL, "");
    }

    /**
     * Get user phone
     */
    public String getPhone() {
        return pref.getString(KEY_PHONE, "");
    }

    /**
     * Get auth token
     */
    public String getToken() {
        return pref.getString(KEY_TOKEN, "");
    }

    /**
     * Get all flats as JSONObject
     */
    public JSONObject getFlatDetails() {
        String json = pref.getString(KEY_FLAT_DETAILS, "{}");
        try {
            JSONObject flats = new JSONObject(json);
            Log.d(TAG, "Retrieved " + flats.length() + " flats from session");
            return flats;
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing flat details: " + e.getMessage());
            return new JSONObject();
        }
    }

    /**
     * Get flat by ID
     */
    public JSONObject getFlatById(String flatId) {
        JSONObject allFlats = getFlatDetails();
        JSONObject flat = allFlats.optJSONObject(flatId);

        if (flat == null) {
            Log.d(TAG, "Flat not found with ID: " + flatId);
        } else {
            Log.d(TAG, "Retrieved flat: " + flatId);
        }

        return flat;
    }

    /**
     * Get first flat (useful for users with single flat)
     */
    public JSONObject getFirstFlat() {
        JSONObject flats = getFlatDetails();

        if (flats.length() == 0) {
            Log.d(TAG, "No flats found when trying to get first flat");
            return null;
        }

        try {
            Iterator<String> keys = flats.keys();
            if (keys.hasNext()) {
                String firstKey = keys.next();
                return flats.getJSONObject(firstKey);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting first flat: " + e.getMessage());
        }

        return null;
    }

    /**
     * Get count of flats
     */
    public int getFlatCount() {
        return getFlatDetails().length();
    }

    /**
     * Check if user has any flats
     */
    public boolean hasFlats() {
        return getFlatCount() > 0;
    }

    /**
     * Get complete user object as JSON
     */
    public JSONObject getUserJson() {
        String json = pref.getString(KEY_USER_JSON, "{}");
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing user JSON: " + e.getMessage());
            return new JSONObject();
        }
    }

    /**
     * Clear all session data (logout)
     */
    public void logout() {
        editor.clear();
        editor.apply();
        Log.d(TAG, "User logged out, session cleared");
    }

    /**
     * Clear only flat details (useful for refreshing data)
     */
    public void clearFlatDetails() {
        editor.remove(KEY_FLAT_DETAILS);
        editor.apply();
        Log.d(TAG, "Flat details cleared");
    }

    /**
     * Update flat details without changing user info
     */
    public void updateFlatDetails(JSONObject newFlatDetails) {
        editor.putString(KEY_FLAT_DETAILS, newFlatDetails.toString());
        editor.apply();
        Log.d(TAG, "Flat details updated. New count: " + newFlatDetails.length());
    }

    /**
     * Debug method to print all preferences
     */
    public void debugPrint() {
        Log.d(TAG, "=== SESSION DEBUG ===");
        Log.d(TAG, "isLoggedIn: " + isLoggedIn());
        //Log.d(TAG, "userId (String): " + getUserId());
        Log.d(TAG, "userId (int): " + getUserIdInt());
        Log.d(TAG, "name: " + getName());
        Log.d(TAG, "email: " + getEmail());
        Log.d(TAG, "phone: " + getPhone());
        Log.d(TAG, "token exists: " + !getToken().isEmpty());
        Log.d(TAG, "token: " + getToken().substring(0, Math.min(20, getToken().length())) + "...");
        Log.d(TAG, "flatDetails: " + pref.getString(KEY_FLAT_DETAILS, "{}"));
        Log.d(TAG, "flatCount: " + getFlatCount());

        // Print first flat details if exists
        JSONObject firstFlat = getFirstFlat();
        if (firstFlat != null) {
            Log.d(TAG, "First flat ID: " + firstFlat.optString("id"));
            Log.d(TAG, "First flat name: " + firstFlat.optString("name"));
        }
        Log.d(TAG, "===================");
    }
}