package com.hafizzaturrahim.tambang.tracking;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hafizzaturrahim.tambang.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListTrackingFragment extends Fragment {


    public ListTrackingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_list_tracking, container, false);

        return v;
    }

}
