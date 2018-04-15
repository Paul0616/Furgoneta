package ro.duoline.furgoneta.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Paul on 26/03/2018.
 */

public class SaveSharedPreferences {
    static SharedPreferences getSharedPreference(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    //############################ INFORMATII USER ######################
    public static void setUser(Context ctx, String email, int userId, String rol, String nume, String prenume){
        //in cazul in care userul e doctor atunci setez loocationId altfel pun zero
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.putString("USER_EMAIL", email);
        editor.putString("FIRST_NAME", prenume);
        editor.putString("LAST_NAME", nume);
        setUserId(ctx, userId);
        setRol(ctx, rol);
        editor.commit();
    }

    public static void setRol(Context ctx,  String rol){
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.putString("rol", rol);
        editor.commit();
    }
    public static String getRol(Context ctx){
        return getSharedPreference(ctx).getString("rol", "");
    }

    public static void setUserId(Context ctx, int id){
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.putInt("userID", id);
        editor.commit();
    }

    public static int getUserId(Context ctx){
        return getSharedPreference(ctx).getInt("userID", 0);
    }

    public static String getNume(Context ctx){
        return getSharedPreference(ctx).getString("LAST_NAME", "");
    }

    public static String getPrenume(Context ctx){
        return getSharedPreference(ctx).getString("FIRST_NAME", "");
    }

    public static String getFullname(Context ctx){

        return (getPrenume(ctx) + " " + getNume(ctx));
    }
    //#####################################################################

    public static void setDocumentType(Context ctx, String docType){
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.putString("documentType", docType);
        editor.commit();
    }
    public static String getDocumentType(Context ctx){
        return getSharedPreference(ctx).getString("documentType", "");
    }

    public static void setDocumentTypeID(Context ctx, int docTypeID){
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.putInt("documentTypeID", docTypeID);
        editor.commit();
    }
    public static int getDocumentTypeID(Context ctx){
        return getSharedPreference(ctx).getInt("documentTypeID", 0);
    }

    public static void setDocumentNo(Context ctx, int docNo){
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.putInt("documentNo", docNo);
        editor.commit();
    }
    public static int getDocumentNo(Context ctx){
        return getSharedPreference(ctx).getInt("documentNo", 0);
    }

    public static void setDocumentDate(Context ctx, String docDate){
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.putString("documentDate", docDate);
        editor.commit();
    }
    public static String getDocumentDate(Context ctx){
        return getSharedPreference(ctx).getString("documentDate", "");
    }

    public static void setCurrentLocation(Context ctx, String curLoc){
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.putString("currentLocation", curLoc);

        editor.commit();
    }
    public static String getCurrentLocation(Context ctx){
        return getSharedPreference(ctx).getString("currentLocation", "");
    }

    public static void setCurrentLocationId(Context ctx, int curLocId){
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.putInt("currentLocationId", curLocId);
        editor.commit();
    }
    public static int getCurrentLocationId(Context ctx){
        return getSharedPreference(ctx).getInt("currentLocationId",0);
    }

    public static void setAssociatedlocations(Context ctx, Set<String> set){
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.putStringSet("associatedLovcations", set);
        editor.commit();
    }

    public static Set<String> getAssociatedlocations(Context ctx){
        return getSharedPreference(ctx).getStringSet("associatedLovcations", new HashSet<String>());
    }
}
