package com.example.test;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;

public class showDoctor extends AppCompatActivity {
    TextView name,phone,email,degree,gender,specialization,exp_yrs,city,clinic,mci;
    CircularImageView docPic;RatingBar ratingBar;Button choose;
    FirebaseFirestore db;StorageReference imageref;String emailid="";
    public void updateDoctor(objectDoctor doctor){
        name.setText(doctor.getName());email.setText(doctor.getEmail());degree.setText(doctor.getDegree());phone.setText(doctor.getPhone());exp_yrs.setText(doctor.getExp_yrs());
        gender.setText(doctor.getGender());specialization.setText(doctor.getSpecialization());clinic.setText(doctor.getClinic());city.setText(doctor.getCity());mci.setText(doctor.getMci());
        ratingBar.setRating(Float.parseFloat(doctor.getRating()));
        imageref.child(emailid+".jpg").getDownloadUrl().addOnSuccessListener(uri -> Glide.with(getApplicationContext()).load(uri).into(docPic));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);setContentView(R.layout.showdoctor);
        name=findViewById(R.id.name);name.setEnabled(false);phone=findViewById(R.id.phone);phone.setEnabled(false);
        email=findViewById(R.id.email);email.setEnabled(false);degree=findViewById(R.id.degree);degree.setEnabled(false);
        gender =findViewById(R.id.gender);gender.setEnabled(false);clinic=findViewById(R.id.clinic);clinic.setEnabled(false);
        specialization=findViewById(R.id.specialization);specialization.setEnabled(false);exp_yrs=findViewById(R.id.exp_yrs);exp_yrs.setEnabled(false);
        city=findViewById(R.id.city);city.setEnabled(false);mci=findViewById(R.id.mci);mci.setEnabled(false);ratingBar=findViewById(R.id.ratingBar);ratingBar.setEnabled(false);
        docPic=findViewById(R.id.docPic);docPic.setEnabled(false);choose=findViewById(R.id.choose);

        final Intent intent=getIntent();
        emailid=intent.getStringExtra("email");

        imageref = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Email").document("doctor "+emailid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    objectDoctor doctor = document.toObject(objectDoctor.class);updateDoctor(doctor);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Document not found", Toast.LENGTH_SHORT).show();
                    Intent intent1 =new Intent(getApplicationContext(),login.class);startActivity(intent1);finish();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Document not found", Toast.LENGTH_SHORT).show();
                Intent intent1 =new Intent(getApplicationContext(),login.class);startActivity(intent1);finish();
            }
        });
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Bundle bundle=new Bundle();
                    bundle.putString("doc_name",name.getText().toString());
                    bundle.putString("doc_email",email.getText().toString());
                    Appointment appointment=new Appointment();
                    appointment.setArguments(bundle);

                    android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                    ((LinearLayout)findViewById(R.id.contentpage)).removeAllViews();
                    fragmentManager.beginTransaction().replace(R.id.contentpage, appointment).commit();

            }
        });
    }
}
