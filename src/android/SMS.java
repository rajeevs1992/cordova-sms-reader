package io.github.rajeevs1992.smsreader;

import org.json.JSONObject;
import android.database.Cursor;
import org.json.JSONException;

public class SMS {
    public int id;
    public String address;
    public String body;
    public Boolean read;
    public long date;

    public SMS(Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
        this.address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
        this.body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
        this.read = cursor.getInt(cursor.getColumnIndexOrThrow("read")) == 1;
        this.date = cursor.getLong(cursor.getColumnIndexOrThrow("date"));
    }

    private boolean applySenderFilter(String[] senderids) {
        for (int i = 0; i < senderids.length; i++) {
            if (this.address.equals(senderids[i])) {
                return true;
            }
        }
        return false;
    }

    public boolean applyFilters(long sinceDate, String[] searchKeys, String[] senderids) {
        if (this.date <= sinceDate) {
            return false;
        }
        if (senderids.length + searchKeys.length == 0) {
            // Get all SMS.
            return true;
        }
        return this.applyBodySearchFilters(searchKeys) || this.applySenderFilter(senderids);
    }

    private boolean applyBodySearchFilters(String[] searchKeys) {
        for (int i = 0; i < searchKeys.length; i++) {
            if (this.body.toLowerCase().contains(searchKeys[i].toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public JSONObject writeJSON() throws JSONException {
        JSONObject sms = new JSONObject();
        sms.put("id", this.id);
        sms.put("address", this.address);
        sms.put("body", this.body);
        sms.put("read", this.read);
        sms.put("date", this.date);
        return sms;
    }
}
