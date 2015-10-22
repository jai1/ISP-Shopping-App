package com.blancosys.mobilyzerapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
//import android.app.Fragment;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.mobilyzer.MeasurementResult;
import com.mobilyzer.MeasurementTask;
import com.mobilyzer.UpdateIntent;
import com.mobilyzer.api.API;
import com.mobilyzer.exceptions.MeasurementError;
import com.mobilyzer.util.Logger;
import com.mobilyzer.util.MeasurementJsonConvertor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


import com.mobilyzer.MeasurementResult;
import com.mobilyzer.MeasurementTask;
import com.mobilyzer.UpdateIntent;
import com.mobilyzer.exceptions.MeasurementError;
import com.mobilyzer.measurements.PingTask;
import com.mobilyzer.api.API;
import com.mobilyzer.util.Logger;
import com.mobilyzer.util.MeasurementJsonConvertor;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MeasureFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MeasureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeasureFragment extends Fragment implements OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RadioGroup radioAppGroup;
    private RadioButton wbButton;
    private RadioButton fdButton;
    private RadioButton vsButton;
    private Button buttonMeasure;


    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MeasureFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MeasureFragment newInstance(String param1, String param2) {
        MeasureFragment fragment = new MeasureFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MeasureFragment() {
        // Required empty public constructor
    }


    /*BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("com.mobilyzer.USER_RESULT_ACTION.speedMeter".equals(action)) {
                Bundle bundle = intent.getExtras();
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
            }
        }
    };
*/



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        //IntentFilter filter = new IntentFilter("com.mobilyzer.USER_RESULT_ACTION.speedMeter");
        //getActivity().getApplicationContext().registerReceiver(intentReceiver, filter);
    }




    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.measurebutton:
                // get selected radio button from radioGroup
                //int selectedId = radioAppGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                //radioAppButton = (RadioButton) v.findViewById(selectedId);
                //Log.d("DIPPI", "Failed here");

                if (wbButton.isChecked()) {
                    Toast.makeText(v.getContext(), "Running Web Browsing Test...", Toast.LENGTH_SHORT).show();
                    MeasurementHandler.runMeasurement("Ping");
                    MeasurementHandler.runMeasurement("DNS Look Up");
                }
                else if (fdButton.isChecked()) {
                    Toast.makeText(v.getContext(), "Running File Download Test...", Toast.LENGTH_SHORT).show();
                    MeasurementHandler.runMeasurement("TCP Speed Test");
                    MeasurementHandler.runMeasurement("TCP Speed Test", "Up");
                }
                else if (vsButton.isChecked()) {
                    Toast.makeText(v.getContext(), "Running Video Streaming Test...", Toast.LENGTH_SHORT).show();
                    MeasurementHandler.runMeasurement("TCP Speed Test");
                    MeasurementHandler.runMeasurement("TCP Speed Test", "Up");
                }
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View V = inflater.inflate(R.layout.fragment_measure, container, false);
        //ImageView imageView = (ImageView)V.findViewById(R.id.my_image);

        radioAppGroup = (RadioGroup) V.findViewById(R.id.radioGroupApp);
        wbButton = (RadioButton) V.findViewById(R.id.wbbutton);
        fdButton = (RadioButton) V.findViewById(R.id.fdbutton);
        vsButton = (RadioButton) V.findViewById(R.id.vsbutton);
        buttonMeasure = (Button) V.findViewById(R.id.measurebutton);
        buttonMeasure.setOnClickListener(this);
        MeasurementHandler.getInstance(getActivity());
        //addListenerOnButton();

        // Inflate the layout for this fragment
        return V;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
