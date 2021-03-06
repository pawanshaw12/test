package com.example.test;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.HashMap;
import java.util.Map;

public class doctorProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    EditText name,phone,degree,gender,specialization,city,clinic;
    CircularImageView docPic,navPic;MenuItem menuItem;TextView email,exp_yrs,mci;RatingBar ratingBar;
    public static final int RESULT_LOAD_IMAGE = 1;Uri selectedImage;TextView navName;FirebaseFirestore db;
    StorageReference imageref;String emailid="";FirebaseUser user;
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
    public void updateDoctor(objectDoctor doctor){
        name.setText(doctor.getName());email.setText(doctor.getEmail());degree.setText(doctor.getDegree());phone.setText(doctor.getPhone());exp_yrs.setText(doctor.getExp_yrs());
        gender.setText(doctor.getGender());specialization.setText(doctor.getSpecialization());clinic.setText(doctor.getClinic());city.setText(doctor.getCity());mci.setText(doctor.getMci());
        ratingBar.setRating(Float.parseFloat(doctor.getRating()));
        final NavigationView navigationView = findViewById(R.id.nav_view);navigationView.setNavigationItemSelectedListener(this);
        final View hView =  navigationView.getHeaderView(0);
        navName= hView.findViewById(R.id.navName);navName.setText(name.getText().toString());
        navPic=hView.findViewById(R.id.navPic);
        imageref.child(emailid+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).into(docPic);
                Glide.with(getApplicationContext()).load(uri).into(navPic);
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RESULT_LOAD_IMAGE && resultCode==RESULT_OK && data!=null ){
            selectedImage=data.getData();docPic.setImageURI(selectedImage);navPic.setImageDrawable(docPic.getDrawable());
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        changeStatusBarColor();
        setContentView(R.layout.activity_main1);

        Toolbar toolbar =findViewById(R.id.toolbar);setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);navigationView.setNavigationItemSelectedListener(this);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        name=findViewById(R.id.name);name.setEnabled(false);phone=findViewById(R.id.phone);phone.setEnabled(false);
        email=findViewById(R.id.email);email.setEnabled(false);degree=findViewById(R.id.degree);degree.setEnabled(false);
        gender =findViewById(R.id.gender);gender.setEnabled(false);clinic=findViewById(R.id.clinic);clinic.setEnabled(false);
        specialization=findViewById(R.id.specialization);specialization.setEnabled(false);exp_yrs=findViewById(R.id.exp_yrs);exp_yrs.setEnabled(false);
        city=findViewById(R.id.city);city.setEnabled(false);mci=findViewById(R.id.mci);mci.setEnabled(false);ratingBar=findViewById(R.id.ratingBar);ratingBar.setEnabled(false);
        docPic=findViewById(R.id.docPic);docPic.setEnabled(false);

        imageref = FirebaseStorage.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) emailid = user.getEmail();
        db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("Email").document("doctor "+emailid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        String uid=user.getUid();docRef.update("uid",uid);
                        objectDoctor doctor = document.toObject(objectDoctor.class);updateDoctor(doctor);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Document not found", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(getApplicationContext(),login.class);startActivity(intent);finish();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Document not found", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getApplicationContext(),login.class);startActivity(intent);finish();
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menuItem=menu.findItem(R.id.editProfile);menuItem.setVisible(true);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.editProfile) {
            if(item.getTitle()=="EDIT PROFILE"){
                item.setTitle("SAVE CHANGES");
                name.setEnabled(true);degree.setEnabled(true);phone.setEnabled(true);docPic.setEnabled(true);
                gender.setEnabled(true);mci.setEnabled(true);city.setEnabled(true);clinic.setEnabled(true);specialization.setEnabled(true);exp_yrs.setEnabled(true);
                docPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i,RESULT_LOAD_IMAGE);
                    }
                });
            }
            else{
                item.setTitle("EDIT PROFILE");
                name.setEnabled(false);degree.setEnabled(false);phone.setEnabled(false);docPic.setEnabled(false);
                gender.setEnabled(false);mci.setEnabled(false);city.setEnabled(false);clinic.setEnabled(false);specialization.setEnabled(false);exp_yrs.setEnabled(false);
                name.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                    @Override
                    public void afterTextChanged(Editable editable) {
                        NavigationView navigationView = findViewById(R.id.nav_view);
                        View hView =  navigationView.getHeaderView(0);
                        navName = hView.findViewById(R.id.navName);navName.setText(name.getText().toString());
                    }
                });
                db.collection("Email").document("doctor "+user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            db.collection("Email").document("doctor "+user.getEmail()).update("name",name.getText().toString().trim(),"specialization",specialization.getText().toString().trim(),"phone",phone.getText().toString().trim(),"city",city.getText().toString().trim(),"clinic",clinic.getText().toString().trim());
                            db.collection("Email").document("doctor "+user.getEmail()).update("degree",degree.getText().toString().trim(),"mci",mci.getText().toString().trim(),"exp_yrs",exp_yrs.getText().toString().trim(),"gender",gender.getText().toString().trim());
                            if(selectedImage!=null){
                            imageref = mStorageRef.child(email.getText().toString()+".jpg");
                            imageref.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                }
                            });
                            }
                        }

                    }
                });

            }
        }
        return super.onOptionsItemSelected(item);
    }
    //@SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        ((LinearLayout)findViewById(R.id.contentpage)).removeAllViews();
        if (id == R.id.viewprofile) {
            Intent i=new Intent(getApplicationContext(),doctorProfile.class);startActivity(i);finish();
        }
        else if (id == R.id.signout) {
            /*FirebaseAuth.getInstance().signOut();
            Intent intent=new Intent(getApplicationContext(),login.class);startActivity(intent);finish();*/
            Map<String,Object> tokenMapRemove = new HashMap<>();
            tokenMapRemove.put("token", FieldValue.delete());
            db.collection("Email").document("doctor "+user.getEmail()).update(tokenMapRemove)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            FirebaseAuth.getInstance().signOut();
                            Intent intent=new Intent(getApplicationContext(),login.class);startActivity(intent);finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("Error","cannot remove token");
                }
            });
        }
        else if(id==R.id.my_appointments){
            menuItem.setVisible(false);
            fragmentManager.beginTransaction().replace(R.id.contentpage, new appointmentList()).commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        if(intent!=null){
            String data = intent.getStringExtra("message");
            Log.i("data",data);
            if(data!=null){
                menuItem.setVisible(false);
                getSupportFragmentManager().beginTransaction().replace(R.id.contentpage, new appointmentList()).addToBackStack(null).commit();
            }
        }
    }
}
