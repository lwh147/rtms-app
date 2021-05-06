package com.lwh147.rtms.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.lwh147.rtms.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResidentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResidentFragment extends Fragment {

    public ResidentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */

    public static ResidentFragment newInstance() {

        return new ResidentFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_resident, container, false);
    }
}