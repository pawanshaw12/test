package com.example.test;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import java.util.Calendar;
public class Appointment extends Fragment {
    EditText doc_name,doc_email,dob,birth_weight,child_name;Button next;RadioGroup genderR;String gender;
    RadioButton radioButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.appointment, container, false);
        getActivity().setTitle("CHILD DETAILS");
        doc_name=view.findViewById(R.id.doc_name);doc_name.setEnabled(false);
        doc_email=view.findViewById(R.id.doc_email);doc_email.setEnabled(false);
        dob=view.findViewById(R.id.dob);birth_weight=view.findViewById(R.id.birth_weight);
        genderR = view.findViewById(R.id.gender);child_name=view.findViewById(R.id.name);

        int selected=genderR.getCheckedRadioButtonId();
        radioButton=view.findViewById(selected);
        gender=radioButton.getText().toString();
        next=view.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                ((RelativeLayout)getActivity().findViewById(R.id.log)).removeAllViews();
                Bundle bundle=new Bundle();
                bundle.putString("doc_name",doc_name.getText().toString().trim());bundle.putString("doc_email",doc_email.getText().toString().trim());
                bundle.putString("child_name",child_name.getText().toString().trim());bundle.putString("dob",dob.getText().toString().trim());
                bundle.putString("gender",gender);bundle.putString("birth_weight",birth_weight.getText().toString().trim());
                uploadChildCase upload=new uploadChildCase();
                upload.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.log, new uploadChildCase()).commit();
            }
        });
        return view;
    }
}
