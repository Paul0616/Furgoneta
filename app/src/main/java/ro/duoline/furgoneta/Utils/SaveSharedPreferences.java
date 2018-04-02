package ro.duoline.furgoneta.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
}
