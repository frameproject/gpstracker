package com.example.linuxmag.helloworld;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.widget.EditText;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * <h4>Gestion des parametres</h4>
 *
 * @author fredericamps@gmail.com
 */
public class ManageSettings {


    public static Boolean isRecord;

    // nom du fichier shareprefs
    public static final String PREFS_PRIVATE = "data";
    private SharedPreferences prefsPrivate;


    /**
     *  Sauvegarde des donnees formulaire
     *
     * @param context
     * @param data
     */
    public void saveData(Context context, HashMap<String, String> data) {

        prefsPrivate = context.getSharedPreferences(PREFS_PRIVATE, Context.MODE_PRIVATE);

        SharedPreferences.Editor prefsPrivateEditor = prefsPrivate.edit();

        System.out.println("PASS = " + data.get("PASS"));
        System.out.println("TEL = " + data.get("TEL"));

        prefsPrivateEditor.putString("TEL", data.get("TEL").trim());
        prefsPrivateEditor.putString("PASS", encode(data.get("PASS").trim()));
        prefsPrivateEditor.putString("RECORD", data.get("RECORD").trim());

        prefsPrivateEditor.apply();
    }


    /**
     * Restaure les donnees shareprefs
     *
     * @param context
     * @param data
     */
    public void restoreData(Context context, HashMap<String, String> data) {
        SharedPreferences myPrefs = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        data.put("TEL", myPrefs.getString("TEL", null));
        data.put("PASS", myPrefs.getString("PASS", null));
        data.put("RECORD", myPrefs.getString("RECORD", null));
    }


    /**
     *  Encodage du mot de passe
     * @param msg
     * @return
     */
    public String encode(String msg) {
        // Sending side
        byte[] myPass = new byte[0];
        try {
            myPass = msg.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(myPass, Base64.DEFAULT);
    }

}

