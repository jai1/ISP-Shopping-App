package com.blancosys.mobilyzerapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.location.Location;
import android.location.LocationManager;
import android.content.Context;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.heatmaps.WeightedLatLng;

import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;




/**
 * A fragment that launches other parts of the demo application.
 */
public class MapFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    Context con;
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;
    //private HashMap<String, DataSet> mLists = new HashMap<String, DataSet>();

    public void refreshSpinners(Context c, Spinner networkTypeSpinner, Spinner carrierTypeSpinner, Spinner dateSpinner) {
        Log.d("refreshSpinner","Start");
        // Convert into small functions
        List<String> networkTypeArray = new ArrayList<String>();
        ArrayAdapter<String> NetworkSpinnerAdapter = new ArrayAdapter<String>(c , android.R.layout.simple_spinner_item, networkTypeArray) {
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                ((TextView) v).setTextSize(16);
                ((TextView) v).setTextColor(
                        getResources()
                                .getColorStateList(R.color.black));
                return v;
            }
        };
        //NetworkSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        networkTypeSpinner.setAdapter(NetworkSpinnerAdapter);
        NetworkSpinnerAdapter.add("All Networks");
        Cursor wbc=MeasurementHandler.getData("network_type",null);
        if(wbc!=null){
            if(wbc.moveToFirst()) {
                do {
                    NetworkSpinnerAdapter.add(wbc.getString(0));
                } while (wbc.moveToNext());
            }
        }
        NetworkSpinnerAdapter.notifyDataSetChanged();

        List<String> carrierTypeArray = new ArrayList<String>();
        ArrayAdapter<String> CarrierSpinnerAdapter = new ArrayAdapter<String>(c , android.R.layout.simple_spinner_item, carrierTypeArray) {
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                ((TextView) v).setTextSize(16);
                ((TextView) v).setTextColor(
                        getResources()
                                .getColorStateList(R.color.black));
                return v;
            }
        };
        carrierTypeSpinner.setAdapter(CarrierSpinnerAdapter);
        CarrierSpinnerAdapter.add("All Carriers");
        wbc=MeasurementHandler.getData("carrier",null);
        if(wbc!=null){
            if(wbc.moveToFirst()) {
                do {
                    CarrierSpinnerAdapter.add(wbc.getString(0));
                } while (wbc.moveToNext());
            }
        }
        CarrierSpinnerAdapter.notifyDataSetChanged();

        List<String> dateArray = new ArrayList<String>();
        ArrayAdapter<String> DateSpinnerAdapter = new ArrayAdapter<String>(c , android.R.layout.simple_spinner_item, dateArray) {
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                ((TextView) v).setTextSize(16);
                ((TextView) v).setTextColor(
                        getResources()
                                .getColorStateList(R.color.black));
                return v;
            }
        };
        dateSpinner.setAdapter(DateSpinnerAdapter);
        DateSpinnerAdapter.add("Everything");
        DateSpinnerAdapter.add("Last 1 Day");
        DateSpinnerAdapter.add("Last Week");
        DateSpinnerAdapter .add("Last Month");
        DateSpinnerAdapter.notifyDataSetChanged();

        dateSpinner.setOnItemSelectedListener(new dateSpinnerActivity());
        carrierTypeSpinner.setOnItemSelectedListener(new CarrierSpinnerActivity());
        networkTypeSpinner.setOnItemSelectedListener(new networkSpinnerActivity());
    }

    // Dealing with spinner choices
    public class networkSpinnerActivity implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            Log.d("networkSpinnerActivity", parent.getItemAtPosition(pos).toString()) ;
        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }
    }
    // Dealing with spinner choices
    public class dateSpinnerActivity implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            Log.d("dateSpinnerActivity", parent.getItemAtPosition(pos).toString()) ;
        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }
    }
    // Dealing with spinner choices
    public class CarrierSpinnerActivity implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            Log.d("CarrierSpinnerActivity", parent.getItemAtPosition(pos).toString()) ;
        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        con = container.getContext();
        // inflate and return the layout
        View v = inflater.inflate(R.layout.fragment_maps, container,
                false);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();
        // latitude and longitude
        googleMap.setMyLocationEnabled(true);
        Location myloc = getLocation();
        Spinner networkTypeSpinner = (Spinner)v.findViewById(R.id.networkTypeSpinner);
        Spinner carrierTypeSpinner = (Spinner)v.findViewById(R.id.carrierTypeSpinner);
        Spinner dateSpinner = (Spinner)v.findViewById(R.id.dateSpinner);
        refreshSpinners(getActivity().getApplicationContext(), networkTypeSpinner, carrierTypeSpinner, dateSpinner);

        /*
        try{
            Cursor wbc=MeasurementHandler.getData("average_webbrowsing");
            if(wbc!=null){
                if(wbc.moveToFirst()) {
                    do {
                        CircleOptions circleOptions = new CircleOptions()
                                .center(new LatLng(Double.parseDouble(wbc.getString(1)), Double.parseDouble(wbc.getString(2))))
                                .radius(100)
                                .fillColor(Color.BLUE)
                                .strokeColor(Color.BLUE);

                        Circle circle = googleMap.addCircle(circleOptions);

                    } while (wbc.moveToNext());
                }
            }
            Cursor fdc=MeasurementHandler.getData("average_filedownload");
            if(fdc!=null){
                if(fdc.moveToFirst()) {
                    do {
                        CircleOptions circleOptions = new CircleOptions()
                                .center(new LatLng(Double.parseDouble(fdc.getString(1)), Double.parseDouble(fdc.getString(2))))
                                .radius(100)
                                .fillColor(Color.GREEN)
                                .strokeColor(Color.GREEN);

                        Circle circle = googleMap.addCircle(circleOptions);

                    } while (fdc.moveToNext());
                }
            }

            Cursor vsc=MeasurementHandler.getData("average_videostreaming");
            if(vsc!=null){
                if(vsc.moveToFirst()) {
                    do {
                        CircleOptions circleOptions = new CircleOptions()
                                .center(new LatLng(Double.parseDouble(vsc.getString(1)), Double.parseDouble(vsc.getString(2))))
                                .radius(100)
                                .fillColor(Color.RED)
                                .strokeColor(Color.RED);

                        Circle circle = googleMap.addCircle(circleOptions);

                    } while (vsc.moveToNext());
                }
            }
        }catch(Exception e){
            Log.d("error",e.getLocalizedMessage());
        }*/


        if (myloc != null) {
            double latitude = myloc.getLatitude();
            double longitude = myloc.getLongitude();



            // create marker
            //MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Hello Maps");

            // Changing marker icon
            //marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

            // adding marker
            //googleMap.addMarker(marker);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(latitude, longitude)).zoom(12).build();
            googleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

            addHeatMap();
        }

        //initializeHeapMap();
        // Perform any camera updates here
        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public Location getLocation() {

        LocationManager locationManager;
        String context = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) con.getSystemService(context);

        String provider = LocationManager.GPS_PROVIDER;
        Location location = null;

        location = locationManager.getLastKnownLocation(provider);

        return location;
    }

    public void initializeHeapMap(){
        // Create the gradient.
        int[] colors = {
                Color.rgb(102, 225, 0), // green
                Color.rgb(255, 0, 0)    // red
        };

        float[] startPoints = {
                0.2f, 1f
        };

        Gradient gradient = new Gradient(colors, startPoints);

// Create the tile provider.
        mProvider = new HeatmapTileProvider.Builder()
                //.data(mList)
                .gradient(gradient)
                .build();

// Add the tile overlay to the map.
        mOverlay = googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }

    private void addHeatMap() {
        List<WeightedLatLng> list = null;

        int[] colors_wb = {
                Color.rgb(0, 0, 255), // blue
                Color.rgb(255, 0, 0)    // red
        };
        int[] colors_fd = {
                Color.rgb(0, 255, 0), // green
                Color.rgb(0, 0, 255)    // blue
        };
        int[] colors_vs = {
                Color.rgb(255, 0, 0),   // red
                Color.rgb(0, 255, 0) // green
        };

        float[] startPoints = {
                0.2f, 1f
        };

        Gradient gradient_wb = new Gradient(colors_wb, startPoints);
        Gradient gradient_fd = new Gradient(colors_fd, startPoints);
        Gradient gradient_vs = new Gradient(colors_vs, startPoints);

        // Get the data: latitude/longitude positions of police stations.
        try {
            list = getLatLng(MeasurementHandler.getData("average_webbrowsing",null));
            if(list!=null) {
                // Create a heat map tile provider, passing it the latlngs of the police stations.
                mProvider = new HeatmapTileProvider.Builder()
                        .weightedData(list)
                        .gradient(gradient_wb)
                        .build();
                // Add a tile overlay to the map, using the heat map tile provider.
                mOverlay = googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
            }
            list = getLatLng(MeasurementHandler.getData("average_filedownload",null));
            if(list!=null) {
                // Create a heat map tile provider, passing it the latlngs of the police stations.
                mProvider = new HeatmapTileProvider.Builder()
                        .weightedData(list)
                        .gradient(gradient_fd)
                        .build();
                // Add a tile overlay to the map, using the heat map tile provider.
                mOverlay = googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
            }
            list = getLatLng(MeasurementHandler.getData("average_videostreaming",null));
            if(list!=null) {
                // Create a heat map tile provider, passing it the latlngs of the police stations.
                mProvider = new HeatmapTileProvider.Builder()
                        .weightedData(list)
                        .gradient(gradient_vs)
                        .build();
                // Add a tile overlay to the map, using the heat map tile provider.
                mOverlay = googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Problem reading list of latitudes and longitudes.", Toast.LENGTH_LONG).show();
        }


    }


    private List<WeightedLatLng> getLatLng(Cursor vsc){
        List<WeightedLatLng> wlllist= new ArrayList<WeightedLatLng>();
        if(vsc!=null){
            if(vsc.moveToFirst()) {
                do {
                    Log.d("lllist","lllist");
                    LatLng ll=new LatLng(Double.parseDouble(vsc.getString(1)), Double.parseDouble(vsc.getString(2)));
                    wlllist.add(new WeightedLatLng(ll,Double.parseDouble(vsc.getString(0))));
                } while (vsc.moveToNext());
            }
        }
        return wlllist;
    }
}