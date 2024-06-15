package com.example.mymessengerapp;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

public class AccountWarningFragment extends Fragment {

    MaterialButton setup_button;
    TextView title;
    LinearLayout home_selected, user_selected, chat_selected, noti_selected;
    public AccountWarningFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_warning, container, false);

        // load layout from XML
        setup_button = view.findViewById(R.id.setup_button);
        title = getActivity().findViewById(R.id.title);
        home_selected = getActivity().findViewById(R.id.home_selected);
        chat_selected = getActivity().findViewById(R.id.chat_selected);
        noti_selected = getActivity().findViewById(R.id.noti_selected);
        user_selected = getActivity().findViewById(R.id.user_selected);

        setup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_frame, new UserSettingFragment());
                transaction.commit();
                title.setText("User");
                home_selected.setBackground(null);
                noti_selected.setBackground(null);
                chat_selected.setBackground(null);
                user_selected.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.selected_nav_item));
            }
        });

        return view;
    }
}