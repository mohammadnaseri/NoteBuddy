package nl.yoerinijs.nb.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.support.annotation.NonNull;

import java.security.NoSuchAlgorithmException;

import nl.yoerinijs.nb.security.EncryptionHandler;

/**
 * A class that holds all methods related to Android's Shared Preferences
 */
public class KeyValueDB {

    private static final String PREFS_NAME = "NoteBuddyPrefs";
    private static final String KEY_USERNAME = "username_key";
    private static final String KEY_DERIVED_SALT = "salt_derived_key";
    private static final String KEY_MASTER_SALT = "salt_master_key";
    private static final String KEY_SETUP = "setup_key";
    private static final String VALUE_SETUP = "setup_value";
    private static final String KEY_RANDOM_PASSWORD_STRING = "random_password_string_key";
    private static final String KEY_VERIFICATION_PASSWORD_HASH = "verification_password_hash_key";

    public KeyValueDB() {
        super();
    }

    /**
     * Method to store random password string
     * @param context
     * @param encryptedSample
     * @throws NoSuchAlgorithmException
     */
    public static void setRandomPasswordString(@NonNull Context context, @NonNull String encryptedSample) throws NoSuchAlgorithmException {
        setValue(context, KEY_RANDOM_PASSWORD_STRING, encryptedSample, false);
    }

    /**
     * Method to retrieve random password string
     * @param context
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String getRandomPasswordString(@NonNull Context context) throws NoSuchAlgorithmException {
        return getValue(context, KEY_RANDOM_PASSWORD_STRING);
    }

    /**
     * Method to set hash for password verification
     * @param context
     * @param verificationPasswordHash
     * @throws NoSuchAlgorithmException
     */
    public static void setVerificationPasswordHash(@NonNull Context context, @NonNull String verificationPasswordHash) throws NoSuchAlgorithmException {
        setValue(context, KEY_VERIFICATION_PASSWORD_HASH, verificationPasswordHash, false);
    }

    /**
     * Method to get hash for password verification
     * @param context
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String getVerificationPasswordHash(@NonNull Context context) throws NoSuchAlgorithmException {
        return getValue(context, KEY_VERIFICATION_PASSWORD_HASH);
    }

    /**
     * Method to set salt for derived key
     * @param context
     * @param salt
     * @throws NoSuchAlgorithmException
     */
    public static void setDerivedKeySalt(@NonNull Context context, @NonNull String salt) throws NoSuchAlgorithmException {
        setValue(context, KEY_DERIVED_SALT, salt, false);
    }

    /**
     * Method to retrieve salt for derived key
     * @param context
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String getDerivedKeySalt(@NonNull Context context) throws NoSuchAlgorithmException {
        return getValue(context, KEY_DERIVED_SALT);
    }

    /**
     * Method to set salt for master key
     * @param context
     * @param salt
     * @throws NoSuchAlgorithmException
     */
    public static void setMasterKeySalt(@NonNull Context context, @NonNull String salt) throws NoSuchAlgorithmException {
        setValue(context, KEY_MASTER_SALT, salt, false);
    }

    /**
     * Method to get salt for master key
     * @param context
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String getMasterKeySalt(@NonNull Context context) throws NoSuchAlgorithmException {
        return getValue(context, KEY_MASTER_SALT);
    }

    /**
     * Method to set setup status
     * @param context
     * @throws NoSuchAlgorithmException
     */
    public static void setSetup(@NonNull Context context) throws NoSuchAlgorithmException {
        setValue(context, KEY_SETUP, VALUE_SETUP, true);
    }

    /**
     * Method to get setup status
     * @param context
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String getSetup(@NonNull Context context) throws NoSuchAlgorithmException {
        return getValue(context, KEY_SETUP);
    }

    /**
     * Method to set username
     * @param context
     * @param value
     * @throws NoSuchAlgorithmException
     */
    public static void setUsername(@NonNull Context context, @NonNull String value) throws NoSuchAlgorithmException {
        setValue(context, KEY_USERNAME, value, false);
    }

    /**
     * Method to retrieve username
     * @param context
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String getUsername(@NonNull Context context) throws NoSuchAlgorithmException {
        return getValue(context, KEY_USERNAME);
    }

    /**
     * Get value method
     * @param context
     * @param key
     * @return
     * @throws NoSuchAlgorithmException
     */
    private static String getValue(@NonNull Context context, @NonNull String key) throws NoSuchAlgorithmException {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String text;
        if(isVersionNougatOrHigher()) {
            text = settings.getString(EncryptionHandler.hashString(key), null);
        } else {
            text = settings.getString(key, null);
        }
        return text;
    }

    /**
     * Set value method
     * @param context
     * @param key
     * @param value
     * @param secure
     * @throws NoSuchAlgorithmException
     */
    private static void setValue(@NonNull Context context, @NonNull String key, @NonNull String value, @NonNull Boolean secure) throws NoSuchAlgorithmException {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Editor editor = settings.edit();

        // Check if current Android version is Nougat or higher. If so, hash key and hash value, if needed.
        // Otherwise, use the raw key and value due to limitations by previous Android versions. Albeit this is not ideal, it is not really insecure due the fact
        // notes are encrypted by an unknown password provided by the user. Nevertheless, it is possible to see the current keys.
        // TODO: create a way so keys are always encrypted.
        if(isVersionNougatOrHigher()) {
            key = EncryptionHandler.hashString(key);
            value = secure ? EncryptionHandler.hashString(value) : value;
        }

        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Delete value method
     * @param context
     * @param key
     * @throws NoSuchAlgorithmException
     */
    private static void deleteValue(@NonNull Context context, @NonNull String key) throws NoSuchAlgorithmException {
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.remove(key);
        editor.apply();
    }

    /**
     * Clear Shared Preferences method
     * @param context
     */
    public static void clearSharedPreference(@NonNull Context context) {
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.clear();
        editor.apply();
    }

    /**
     * Returns true if the current Android version is Nougat or higher.
     * @return
     */
    private static boolean isVersionNougatOrHigher() {
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }
}