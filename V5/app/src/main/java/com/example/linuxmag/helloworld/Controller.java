package com.example.linuxmag.helloworld;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.HashMap;

/**
 * <h4>Controleur de MainActivity</h4>
 *
 * @author fredericamps@gmail.com
 */
public class Controller {

    Context myContext;
    String pass;
    String tel;
    Boolean modeInit = false;
    Boolean unlock = false;
    ManageSettings manageData;
    MainActivity myActivity;


    /** Constructeur
     *
     * @param myContext
     * @param myActivity
     */
    Controller(Context myContext, MainActivity myActivity) {
        this.myContext = myContext;
        this.myActivity = myActivity;

        manageData = new ManageSettings();

        // chargement des paramètres
        loadSettings();
    }


    /**
     * Retourne l'etat de record
     *
     * @return
     */
    public Boolean getRecord() {
        return ManageSettings.isRecord;
    }


    /**
     * Retourne le n° de tel
     *
     * @return
     */
    public String getTel() {
        return tel;
    }

    /**
     * Test le mot de passe
     *
     * @param pass
     * @return
     */
    public Boolean unlock(String pass) {
        // chargement des parametres
        ManageSettings loadMyData = new ManageSettings();
        HashMap<String, String> data = new HashMap<String, String>();
        loadMyData.restoreData(myContext, data);

        if (pass.trim().isEmpty() || pass == null) return false;

        if (loadMyData.encode(pass).equals(data.get("PASS"))) {
            unlock = true;
            return true;
        } else {
            unlock = false;
            return false;
        }
    }


    /**Stop du service
     *
     * @param pass
     * @return
     */
    public Boolean stopIsOK(String pass) {

        if(myActivity.isMyServiceRunning(ServiceLock.class) && unlock(pass))
            return true;
        else
            return false;
    }


    /**
     * Chargement des parametres de configuration
     */
    public void loadSettings() {
        // chargement des parametres de configuration
        ManageSettings loadMyData = new ManageSettings();
        HashMap<String, String> data = new HashMap<String, String>();

        loadMyData.restoreData(myContext, data);

        if (data.get("TEL") == null && data.get("PASS") == null) {
            modeInit = true;
            unlock = true;
        }

        pass = data.get("PASS");
        ManageSettings.isRecord = Boolean.parseBoolean(data.get("RECORD"));
        tel = data.get("TEL");
    }

    /**
     * Emmission d'un message Intent
     *
     * @param msg
     */
    public boolean broadCastMessage(String msg) {
        Log.d("MainActivity", "Broadcasting message");
        Intent intent = new Intent("lockMyPhone");
        intent.putExtra("message", msg);
        return LocalBroadcastManager.getInstance(myContext).sendBroadcast(intent);
    }


    /**
     * Sauvegarde des parametres
     *
     * @param tel
     * @param pass
     * @return
     */
    public boolean saveSettings(String tel, String pass) {
        HashMap<String, String> data = new HashMap<String, String>();

        if (tel == null || pass == null) return false;

        if (tel.trim().isEmpty() || pass.trim().isEmpty()) return false;

        data.put("TEL", tel);
        data.put("PASS", pass);

        if (ManageSettings.isRecord) {
            data.put("RECORD", "true");
        } else
            data.put("RECORD", "false");

        manageData.saveData(myContext, data);

        return true;
    }

    /**
     * Donne l'autorisation de demarrer le service
     *
     * @param pass
     * @param tel
     * @return
     */
    public boolean isStartOk(String tel, String pass) {

        if(tel==null || pass==null) return false;

        if (tel.trim().isEmpty() || pass.trim().isEmpty())  return false;

        // premiere utilisation du soft
        if (modeInit) {
            saveSettings(tel, pass);
            modeInit = false;

            if (!myActivity.isMyServiceRunning(ServiceLock.class)) {
                Intent i = new Intent(myContext, ServiceLock.class);
                myContext.startService(i);
                return true;
            }
            return false;
        }

        // debloque ?
        if (!unlock) {
            return false;
        }

        // ihm debloque, test passwd ?
        if (unlock(pass) && !myActivity.isMyServiceRunning(ServiceLock.class)) {
            saveSettings(tel, pass);
            Intent i = new Intent(myContext, ServiceLock.class);
            myContext.startService(i);
            return true;
        }

        return false;
    }

    /**
     * @param tel
     * @param pass
     * @return
     */
    public boolean onPauseOK(String tel, String pass) {

        if(tel==null || pass==null) return false;

        if (tel.trim().isEmpty() || pass.trim().isEmpty()) return false;

        if (unlock) {
            saveSettings(tel, pass);
            return true;
        }
        return false;
    }

}
