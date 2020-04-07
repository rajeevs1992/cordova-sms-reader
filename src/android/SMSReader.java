package io.github.rajeevs1992.smsreader;

import android.Manifest;
import android.content.pm.PackageManager;
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

    private CallbackContext CallbackContext;
    private String[] SMSPermissions = { Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS,
            Manifest.permission.SEND_SMS };

    private String[] getStringArrayFromJSONArray(JSONArray array) throws JSONException {
        if (array == null) {
            return new String[] {};
        }
        String[] stringArray = new String[array.length()];
        for (int i = 0; i < stringArray.length; i++) {
            stringArray[i] = array.getString(i);
        }
        return stringArray;
    }

    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) {
        Log.v("SMSReader", "Called action " + action);
        try {
            ArrayList<SMS> sms = new ArrayList<SMS>();
            if (action == 'permission') {
                this.ensurePermissions(this.getStringArrayFromJSONArray(data.getJSONArray(0)), callbackContext);
                return true;
            } else {
                String folderType = data.getString(0);
                Long since = data.getLong(1);
                String[] searchstrings = this.getStringArrayFromJSONArray(data.getJSONArray(2));
                String[] senderids = this.getStringArrayFromJSONArray(data.getJSONArray(3));
                switch (action) {
                    case "all": {
                        sms = this.fetchSMS(folderType, since, new String[]{}, new String[]{});
                    }
                    break;
                    case "filterbody": {
                        if (searchstrings.length > 0) {
                            sms = this.fetchSMS(folderType, since, searchstrings, new String[] {});
                        }
                    }
                    break;
                    case "filtersenders": {
                        if (senderids.length > 0) {
                            sms = this.fetchSMS(folderType, since, new String[] {}, senderids);
                        }
                    }
                    break;
                    case "filterbodyorsenders": {
                        if (searchstrings.length + senderids.length > 0) {
                            sms = this.fetchSMS(folderType, since, searchstrings, senderids);
                        }
                    }
                    default: {
                        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.INVALID_ACTION));
                        return false;
                    }
                    break;
                }
            }
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, this.convertToJSONArray(sms)));
            return true;
        } catch (JSONException e) {
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.JSON_EXCEPTION, e.getMessage()));
            return false;
        }
    }

    private String[] resolvePermissions(String[] permissionCodes) {
        String[] permissions = new String[permissionCodes.length];
        for (int i = 0; i < permissionCodes.length; i++) {
            switch (permissionCodes[i]) {
            case "read": {
                permissions[i] = this.SMSPermissions[0];
            }
                break;
            case "receive": {
                permissions[i] = this.SMSPermissions[1];
            }
                break;
            case "send": {
                permissions[i] = this.SMSPermissions[2];
            }
                break;
            }
        }
        return permissions;
    }

    private void ensurePermissions(String[] permissionCodes, CallbackContext callbackContext) {
        boolean hasPermission = true;
        String[] permissions = this.resolvePermissions(permissionCodes);
        for (String permission : permissions) {
            if (!cordova.hasPermission(permission)) {
                hasPermission = false;
                break;
            }
        }
        if (hasPermission) {
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, "PERMISSION_GRANTED"));
            return;
        } else {
            this.CallbackContext = callbackContext;
            cordova.requestPermissions(this, 1, permissions);
        }
    }

    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults)
            throws JSONException {
        for (int r : grantResults) {
            if (r == PackageManager.PERMISSION_DENIED) {
                this.CallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "PERMISSION_DENIED"));
                return;
            }
        }
        this.CallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, "PERMISSION_GRANTED"));
        this.CallbackContext = null;
    }

    private JSONArray convertToJSONArray(ArrayList<SMS> sms) throws JSONException {
        JSONArray smsResult = new JSONArray();
        int resultLength = sms.size();
        for (int i = 0; i < resultLength; i++) {
            smsResult.put(sms.get(i).writeJSON());
        }
        return smsResult;
    }

    private ArrayList<SMS> fetchSMS(long since, String folderType, String[] searchText, String[] senderids) {
        ArrayList<SMS> lstSms = new ArrayList<SMS>();

        Uri message = Uri.parse("content://sms/" + folderType);
        ContentResolver contentResolver = cordova.getActivity().getContentResolver();

        Cursor cursor = contentResolver.query(message, null, null, null, null);
        int totalSMS = cursor.getCount();
        if (cursor.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {
                SMS sms = new SMS(cursor);
                if (sms.applyFilters(since, searchText, senderids)) {
                    lstSms.add(sms);
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        return lstSms;
    }
}
