package com.blancosys.mobilyzerapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import com.mobilyzer.MeasurementResult;
import com.mobilyzer.UpdateIntent;
import com.mobilyzer.util.Logger;
import com.mobilyzer.util.MeasurementJsonConvertor;

import org.json.JSONException;
import org.json.JSONObject;

public class MeasurementBroadcastReceiver extends BroadcastReceiver {
    public MeasurementBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Broadcast Receiver", intent.getAction());
        Parcelable[] results=intent.getParcelableArrayExtra(UpdateIntent.RESULT_PAYLOAD);
        for (Object obj : results) {
            try {
                MeasurementResult result = (MeasurementResult) obj;
                result.getMeasurementDesc().parameters = null;
                JSONObject jsonobj = MeasurementJsonConvertor.encodeToJson(result);
                MeasurementHandler.loadJSONObjectInDB(jsonobj);
            } catch (JSONException e) {
                Logger.e("Error converting results to json format", e);
            }
        }
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
