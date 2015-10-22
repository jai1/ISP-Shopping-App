package com.blancosys.mobilyzerapp;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.myjson.JsonArray;
import com.mobilyzer.MeasurementTask;
import com.mobilyzer.api.API;
import com.mobilyzer.exceptions.MeasurementError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.mobilyzer.MeasurementResult;
import com.mobilyzer.MeasurementTask;
import com.mobilyzer.UpdateIntent;
import com.mobilyzer.api.API;
import com.mobilyzer.exceptions.MeasurementError;
import com.mobilyzer.util.Logger;
import com.mobilyzer.util.MeasurementJsonConvertor;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.util.Log;

/**
 * Created by Dipesh on 17 Mar 15.
 */
public class MeasurementHandler {
    static API api ;
    static FragmentActivity activity;

    private static SQLiteDatabase db;

    public static MeasurementHandler getInstance(FragmentActivity a) {
        return new MeasurementHandler(a);
    }


    private MeasurementHandler(FragmentActivity a) {
        activity = a;
        api= API.getAPI(activity.getApplicationContext(), "speedMeter");
        db = a.openOrCreateDatabase("MobilyzerDB", Context.MODE_PRIVATE, null);
        String query="";
        query=  "CREATE TABLE IF NOT EXISTS ping (" +
                "ID INTEGER PRIMARY KEY  ," +
                "result Number(18,6), Latitude VARCHAR, Longitude VARCHAR, Carrier VARCHAR, " +
                "TimeStamp VARCHAR, Network_type VARCHAR);";
        db.execSQL(query);
        Log.d("Creation queries", query);
        query=  "CREATE TABLE IF NOT EXISTS dns_lookup (" +
                "ID INTEGER PRIMARY KEY  ," +
                "result Number(18,6), Latitude VARCHAR, Longitude VARCHAR, Carrier VARCHAR, " +
                "TimeStamp VARCHAR, Network_type VARCHAR);";
        db.execSQL(query);
        Log.d("Creation queries", query);
        query=  "CREATE TABLE IF NOT EXISTS TCP_Speed_Test (" +
                "ID INTEGER PRIMARY KEY  ," +
                "duration Number(8,6),total_tcp_speed_results Number(18,16), Direction VARCHAR, Latitude VARCHAR, Longitude VARCHAR, Carrier VARCHAR, " +
                "TimeStamp VARCHAR, Network_type VARCHAR);";
        db.execSQL(query);
        Log.d("Creation queries", query);
        printDBCount();
    }

    public static void runMeasurement(String measurementType)
    {
        String direction="Down";
        runMeasurement(measurementType,direction);

    }

    public static void runMeasurement(String measurementType, String direction) {
        Map<String, String> params = new HashMap<String, String>();
        API.TaskType taskType=API.TaskType.PING;
        try {
            //resultText.setText(measurementType + " Running ....");
            if (measurementType == "Trace Route") {
                taskType = API.TaskType.TRACEROUTE;
                params.put("target", "146.115.8.106");
                params.put("ping_exe", "ping");
            } else if (measurementType == "UDP Burst") {
                taskType = API.TaskType.UDPBURST;
                params.put("target", "mlab");
                params.put("direction", "Down");
                params.put("packet_size_byte", "100");
                params.put("packet_burst", "16");
                params.put("udp_interval","1");
            } else if (measurementType == "HTTP Test") {
                taskType = API.TaskType.HTTP;
                params.put("url", "www.irctc.co.in");
                params.put("method", "get");
            } else if (measurementType == "TCP Speed Test") {
                taskType = API.TaskType.TCPTHROUGHPUT;
                params.put("target", "mlab");
                params.put("direction", direction);
            } else if (measurementType == "Ping") {
                taskType = API.TaskType.PING;
                params.put("target", Constants.ping_website);
                params.put("ping_exe", "ping");
            } else if (measurementType == "DNS Look Up") {
                taskType = API.TaskType.DNSLOOKUP;
                params.put("target", Constants.ping_website);
            }
            MeasurementTask mt = api.createTask(taskType, null, null, 1, 1,
                    API.USER_PRIORITY, 1, params);
            api.submitTask(mt);
        } catch (MeasurementError measurementError) {
            measurementError.printStackTrace();
        }
    }

    public static void loadJSONObjectInDB(JSONObject jst) throws JSONException {
        String type = jst.getJSONObject("parameters").getString("type");
        Log.d("Type = ", "\"" + type + "\"");
        printDBCount();
        if (type.equals("ping")) {
            insertPingData(jst);
        }
        else if(type.equals("dns_lookup")){
            insertDNSData(jst);
        }
        else if(type.equals("tcpthroughput")){
            insertTCPThroughPutData(jst);
        }
        else
            Log.d("Error", "Type is incompatible " + "\"" + type + "\"");
        printDBCount();
    }

    public static void printDBCount()
    {   Cursor c=db.rawQuery("select count(*) from ping;", null);
        Cursor d=db.rawQuery("select count(*) from dns_lookup;", null);
        Cursor e=db.rawQuery("select count(*) from TCP_Speed_Test;", null);
        if(c.getCount()==0)
        {
            Log.d("error", "NO DATA");
        }else
        {
            c.moveToFirst();
            Log.d("Count Ping = ",Integer.toString(c.getInt(0)));
        }

        if(d.getCount()==0)
        {
            Log.d("error", "NO DATA");
        }else
        {
            d.moveToFirst();
            Log.d("Count DNS Lookup = ",Integer.toString(d.getInt(0)));
        }
        if(e.getCount()==0)
        {
            Log.d("error", "NO DATA");
        }else
        {
            e.moveToFirst();
            Log.d("Count TCP Speed Test = ",Integer.toString(e.getInt(0)));
        }

    }

    public static Cursor getData(String type, Map<String, String> addClauseMap)
    {
        Cursor e=null;
        String query="";

        if (type.equals("webbrowsing")) {
            query=  "select d.result as dr , p.result as pr, p.latitude as lat, p.longitude as lon, p.carrier\n" +
                    "from ping p join dns_lookup d \n" +
                    "on p.Latitude = d.Latitude\n" +
                    "and p.longitude = d.longitude\n" +
                    "and p.Carrier = d.Carrier\n" +
                    "and p.id = d.id;";
        }
        else if(type.equals("filedownload")){
            query=  "select duration, total_tcp_speed_results, Direction, latitude, longitude, carrier from TCP_Speed_Test;";
        }
        else if(type.equals("videostreaming")){
            query= "select duration, total_tcp_speed_results, Direction, latitude, longitude, carrier from TCP_Speed_Test;";
        }
        else if (type.equals("average_webbrowsing")) {
            query=  "select avg(result), latitude, longitude from " +
                    "(select result, latitude, longitude, carrier, timestamp, network_type from" +
                    "(select (d.result + p.result) as result, p.network_type as network_type," +
                    " p.latitude as latitude, p.longitude as longitude, p.carrier as carrier, " +
                    " p.timestamp as timestamp\n" +
                    "    from ping p join dns_lookup d\n" +
                    "    on p.Latitude = d.Latitude\n" +
                    "    and p.longitude = d.longitude\n" +
                    "    and p.Carrier = d.Carrier\n" +
                    "    and p.id = d.id" +
                    "    and p.network_type = d.network_type)" +
                    getAddWhereClause(addClauseMap) +
                    ")" +
                    "group by latitude, longitude" +
                    ";";
        }
        else if (type.equals("average_filedownload") || type.equals("average_videostreaming")) {
            query=  "select avg(result), latitude, longitude from " +
                    "(select total_tcp_speed_results as result, network_type as network_type, " +
                    "latitude as latitude, longitude as longitude, carrier as carrier, " +
                    "timestamp as timestamp from TCP_Speed_Test \n" +
                    getAddWhereClause(addClauseMap)  +
                    ")" +
                    "group by\n" +
                    "latitude, longitude\n" +
                    ";";
        }
        else if (type.equals("network_type"))
        {
            query=  "select distinct * from \n" +
                    "(select distinct  Network_type from ping\n" +
                    "union\n" +
                    "select distinct  Network_type from dns_lookup\n" +
                    "union\n" +
                    "select distinct  Network_type from tcp_speed_test);";
        }
        else if (type.equals("carrier"))
        {
            query=  "select distinct * from \n" +
                    "(select distinct Carrier from ping\n" +
                    "union\n" +
                    "select distinct Carrier from dns_lookup\n" +
                    "union\n" +
                    "select distinct Carrier from tcp_speed_test);";
        }
        else
            Log.d("Error", "Type is incompatible " + "\"" + type + "\"");

        e=db.rawQuery(query,null);
        Log.d("selection queries "+type, query);
        return e;
    }


    public static String getAddWhereClause(Map<String, String> addClauseMap) {
        String additionalWhereClause="";
        if(addClauseMap == null || addClauseMap.size() == 0 )
            return additionalWhereClause;
        additionalWhereClause+=" where \n";
        Iterator it = addClauseMap.entrySet().iterator();
        while (true) {
            Map.Entry pair = (Map.Entry)it.next();
            if(pair.getKey() == "date")
                additionalWhereClause += "timestamp >= datetime('now') - " + pair.getValue() + "\n";
            else
                additionalWhereClause += pair.getKey() + "=" + "\"" + pair.getValue() + "\"\n";
            if(it.hasNext())
                additionalWhereClause += "and ";
            else
                break;
        }
        return additionalWhereClause;
    }

    public static void insertDNSData(JSONObject jst) throws JSONException{
        double time_ms, latitude, longitude;
        String carrier, timestamp, network;
        time_ms = jst.getJSONObject("values").getDouble("time_ms");
        latitude = jst.getJSONObject("properties").getJSONObject("location").getDouble("latitude");
        longitude = jst.getJSONObject("properties").getJSONObject("location").getDouble("longitude");
        carrier = jst.getJSONObject("properties").getString("carrier");
        timestamp = jst.getJSONObject("properties").getString("timestamp");
        network = jst.getJSONObject("properties").getString("network_type");

        SQLiteDatabase db = activity.openOrCreateDatabase("MobilyzerDB", Context.MODE_PRIVATE, null);
        String dbString = "Insert into dns_lookup values ( null," + time_ms + ",\"" + latitude + "\",\"" +
                longitude + "\",\"" + carrier + "\",\"" + timestamp + "\",\"" +
                network + "\"); commit;";
        Log.d("dns_lookup queries", dbString);
        db.execSQL(dbString);
        //Log.d("dns_lookup queries", "Executed Successfully");
    }

    public static void insertTCPThroughPutData(JSONObject jst) throws JSONException{
        double duration, latitude, longitude;
        double total = 0;
        String carrier, timestamp, network;
        duration = jst.getJSONObject("values").getDouble("duration");
        //Log.d("JSON Class", jst.getJSONObject("values").getJSONObject("tcp_speed_results").getClass().toString());
        Log.d("JSON Object ", jst.getJSONObject("values").toString());
        String tcpSpeedResults = jst.getJSONObject("values").getString("tcp_speed_results");
        tcpSpeedResults=tcpSpeedResults.substring(1,tcpSpeedResults.length() - 1);
        Log.d("JSON Object ",tcpSpeedResults);
        String a[]=tcpSpeedResults.split(",");
        Log.d("JSON Object ",a.toString());
        if (a != null) {
            for (int i = 0; i < a.length; i++)
                total += Double.parseDouble(a[i]);
        }

        boolean dir_up = jst.getJSONObject("parameters").getBoolean("dir_up");
        String direction;
        if (dir_up)
            direction = "Up";
        else
            direction = "Down";
        latitude = jst.getJSONObject("properties").getJSONObject("location").getDouble("latitude");
        longitude = jst.getJSONObject("properties").getJSONObject("location").getDouble("longitude");
        carrier = jst.getJSONObject("properties").getString("carrier");
        timestamp = jst.getJSONObject("properties").getString("timestamp");
        network = jst.getJSONObject("properties").getString("network_type");

        SQLiteDatabase db = activity.openOrCreateDatabase("MobilyzerDB", Context.MODE_PRIVATE, null);

        String dbString = "Insert into TCP_Speed_Test values ( null, " + duration + "," + total + ",\"" + direction + "\",\"" + latitude + "\",\"" +
                longitude + "\",\"" + carrier + "\",\"" + timestamp + "\",\"" +
                network + "\"); commit;";
        Log.d("TCP_Speed_Test queries", dbString);
        db.execSQL(dbString);
        //Log.d("TCP_Speed_Test queries", "Executed Successfully");
    }

    public static void insertPingData(JSONObject jst) throws JSONException{
        double mean_rtt_ms, latitude, longitude;
        String carrier, timestamp, network;
        mean_rtt_ms = jst.getJSONObject("values").getDouble("mean_rtt_ms");
        latitude = jst.getJSONObject("properties").getJSONObject("location").getDouble("latitude");
        longitude = jst.getJSONObject("properties").getJSONObject("location").getDouble("longitude");
        carrier = jst.getJSONObject("properties").getString("carrier");
        timestamp = jst.getJSONObject("properties").getString("timestamp");
        network = jst.getJSONObject("properties").getString("network_type");
        SQLiteDatabase db = activity.openOrCreateDatabase("MobilyzerDB", Context.MODE_PRIVATE, null);
        String dbString = "Insert into ping values ( null," + mean_rtt_ms + ",\"" + latitude + "\",\"" +
                longitude + "\",\"" + carrier + "\",\"" + timestamp + "\",\"" +
                network + "\"); commit;";
        Log.d("Ping queries", dbString);
        db.execSQL(dbString);
        //Log.d("Ping queries", "Executed Successfully");

        getData("webbrowsing",null);
        getData("filedownload",null);
        getData("videostreaming",null);
        getData("average_webbrowsing",null);
        getData("average_filedownload",null);
        getData("average_videostreaming",null);
        getData("network_type",null);
        getData("carrier",null);
        Map<String,String>temp = new HashMap<String, String>();
        temp.put("carrier","Jai");
        getData("average_filedownload",temp);
        temp.put("network_type","WiFi");
        getData("average_videostreaming",temp);
        temp.put("date","30");
        getData("average_webbrowsing",temp);
    }
}
