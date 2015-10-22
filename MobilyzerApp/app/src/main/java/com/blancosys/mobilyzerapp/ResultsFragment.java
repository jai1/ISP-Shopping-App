package com.blancosys.mobilyzerapp;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResultsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ResultsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResultsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResultsFragment newInstance(String param1, String param2) {
        ResultsFragment fragment = new ResultsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ResultsFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View V = inflater.inflate(R.layout.fragment_results, container, false);

        // get the listview
        expListView = (ExpandableListView) V.findViewById(R.id.lvExp);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(V.getContext(), listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        // Listview Group click listener
        expListView.setOnGroupClickListener(new OnGroupClickListener() {

            //@Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(V.getContext(),
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(V.getContext(),
                        listDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();

            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                Toast.makeText(
                        V.getContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });


        return V;

    }



    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Web Browsing");
        listDataHeader.add("File Download");
        listDataHeader.add("Video Streaming");

        //MeasurementHandler.getInstance(getActivity());

        // Adding child data
        List<String> wb = new ArrayList<String>();
        Cursor wbc=MeasurementHandler.getData("webbrowsing",null);
        if(wbc!=null){
            if(wbc.moveToFirst()) {
                String data;
                do {
                    data = "DNS Lookup: " + Double.toString(wbc.getDouble(0)) + " ms" +
                            "\nPing: " + Double.toString(wbc.getDouble(1)) + " ms" +
                            "\nLatitude: " + wbc.getString(2) +
                            "\nLongitude: " + wbc.getString(3) +
                            "\nCarrier: " + wbc.getString(4);
                    wb.add(data);
                    Log.d("ResultFragment Data = ", data);
                } while (wbc.moveToNext());
            }
        }


        List<String> fd = new ArrayList<String>();
        Cursor fdc=MeasurementHandler.getData("filedownload",null);
        if(fdc!=null){
            if(fdc.moveToFirst()) {
                String data;
                do {
                    data = "Duration: " + fdc.getString(0) + " ms" +
                            "\nTotal Data: " + Double.toString(fdc.getDouble(1)) + " bytes" +
                            "\nDirection: " + fdc.getString(2) +
                            "\nLatitude: " + fdc.getString(3) +
                            "\nLongitude: " + fdc.getString(4) +
                            "\nCarrier: " + fdc.getString(5);
                    fd.add(data);
                    Log.d("ResultFragment Data = ", data);
                } while (fdc.moveToNext());
            }
        }

        List<String> vs = new ArrayList<String>();
        Cursor vsc=MeasurementHandler.getData("videostreaming",null);
        if(vsc!=null){
            if(vsc.moveToFirst()) {
                String data;
                do {
                    data = "Duration: " + vsc.getString(0) + " ms" +
                            "\nTotal Data: " + Double.toString(vsc.getDouble(1)) + " bytes" +
                            "\nDirection: " + vsc.getString(2) +
                            "\nLatitude: " + vsc.getString(3) +
                            "\nLongitude: " + vsc.getString(4) +
                            "\nCarrier: " + vsc.getString(5);
                    vs.add(data);
                    Log.d("ResultFragment Data = ", data);
                } while (vsc.moveToNext());
            }
        }

        listDataChild.put(listDataHeader.get(0), wb); // Header, Child data
        listDataChild.put(listDataHeader.get(1), fd);
        listDataChild.put(listDataHeader.get(2), vs);
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
