package nl.yoerinijs.nb.security;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import nl.yoerinijs.nb.storage.KeyValueDB;

import static nl.yoerinijs.nb.security.AesCbcWithIntegrity.generateSalt;
import static nl.yoerinijs.nb.security.AesCbcWithIntegrity.saltString;

/**
 * A class that generates salts for encryption purposes
 */
public class SaltHandler {

    public static void setSalt(@NonNull Context context) throws Exception {
        if(null == KeyValueDB.getDerivedKeySalt(context) || null == KeyValueDB.getMasterKeySalt(context)) {
            KeyValueDB.setDerivedKeySalt(context, saltString(generateSalt()));
            KeyValueDB.setMasterKeySalt(context, saltString(generateSalt()));
        }
    }

    @Nullable
    static String getDerivedKeySalt(@NonNull Context context) throws Exception {
        return KeyValueDB.getDerivedKeySalt(context);
    }

    @Nullable
    static String getMasterKeySalt(@NonNull Context context) throws Exception {
        return KeyValueDB.getMasterKeySalt(context);
    }
}
