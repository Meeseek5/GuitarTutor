package com.example.guitartutor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FingerPlacementFirstFragment extends Fragment {
    private Button mButtonOK;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_first_finger_placement, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mButtonOK = (Button)getView().findViewById(R.id.button_ok);
        mButtonOK.setOnClickListener(buttonOKOnclick);
    }

    private View.OnClickListener buttonOKOnclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((FingerPlacementActivity)getActivity()).replaceFragment();
        }
    };
}
