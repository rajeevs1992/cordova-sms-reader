package io.github.rajeevs1992.smsreader;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import android.net.Uri;
import android.content.ContentResolver;
import android.database.Cursor;
import org.apache.cordova.PluginResult;
import java.util.ArrayList;
import android.util.Log;

public class SMSReader extends CordovaPlugin {

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    private String[] getStringArrayFromJSONArray(JSONArray array) throws JSONException {
        String[] stringArray = new String[array.length()];
        for (int i = 0; i < stringArray.length; i++) {
            stringArray[i] = array.getString(i);
        }
        return stringArray;
    }

    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) {
        Log.v("SMSReader", "Called action " + action);
        try {
            ArrayList<SMS> sms;
            long since = data.getLong(0);
            switch (action) {
            case "all": {
                sms = this.fetchSMS(since, new String[] {}, new String[] {});
            }
                break;
            case "filtersenders": {
                String[] senderids = this.getStringArrayFromJSONArray(data.getJSONArray(1));
                sms = this.fetchSMS(since, senderids, new String[] {});
            }
                break;
            case "filterbody": {
                String[] searchstrings = this.getStringArrayFromJSONArray(data.getJSONArray(1));
                sms = this.fetchSMS(since, new String[] {}, searchstrings);
            }
                break;
            default: {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.INVALID_ACTION));
                return false;
            }
            }
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, this.convertToJSONArray(sms)));
            return true;
        } catch (JSONException e) {
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.JSON_EXCEPTION, e.getMessage()));
            return false;
        }
    }

    private JSONArray convertToJSONArray(ArrayList<SMS> sms) throws JSONException {
        JSONArray smsResult = new JSONArray();
        int resultLength = sms.size();
        for (int i = 0; i < resultLength; i++) {
            smsResult.put(sms.get(i).writeJSON());
        }
        return smsResult;
    }

    private ArrayList<SMS> fetchSMS(long since, String[] senderids, String[] searchText) {
        ArrayList<SMS> lstSms = new ArrayList<SMS>();

        Uri message = Uri.parse("content://sms/inbox");
        ContentResolver contentResolver = cordova.getActivity().getContentResolver();

        Cursor cursor = contentResolver.query(message, null, null, null, null);
        int totalSMS = cursor.getCount();
        if (cursor.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {
                SMS sms = new SMS(cursor);
                if (sms.applyFilters(since, senderids, searchText)) {
                    lstSms.add(sms);
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        return lstSms;
    }
}
