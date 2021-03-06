package com.example.test;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class annotateImage extends Fragment {
    RecyclerView recyclerView;ImageAdapter adapter;ParentImageAdapter adapter2;private ProgressDialog progressDialog;String child_name, parent_email, id, type,doc_email;
    ArrayList<String> filenames,tagList;FirebaseFirestore db;FirebaseUser user;Bundle bundle;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.annotate_image, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        bundle = getArguments();String title = "Annotate Images";
        child_name = bundle.getString("child_name");id = bundle.getString("id");type = bundle.getString("user");
        if(type.equals("p")){ title = "Annotated Images";doc_email = bundle.getString("doctor_email");parent_email = user.getEmail(); }
        else{ doc_email = user.getEmail();parent_email = bundle.getString("parent_email"); }
        getActivity().setTitle(title);setHasOptionsMenu(true);
        db = FirebaseFirestore.getInstance();filenames = new ArrayList<>();tagList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        progressDialog = new ProgressDialog(getActivity());progressDialog.setMessage("Please wait...");progressDialog.show();
        db.collection("Email").document("parent " + parent_email).collection("sent_appointments")
                .document(child_name + " " + doc_email).collection("Dates").document("" + id)
                .collection("Untag_images").get().addOnSuccessListener(queryDocumentSnapshots -> {
                    progressDialog.dismiss();
                    if(type.equals("d")){
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) filenames.add(doc.getString("filename"));

                        adapter = new ImageAdapter(getActivity(), filenames);recyclerView.setAdapter(adapter);
                    }
                    else if(type.equals("p")){
                        for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                            filenames.add(doc.getString("filename"));
                            tagList.add(doc.getString("tag"));
                        }
                        adapter2 = new ParentImageAdapter(getActivity(), filenames, tagList);recyclerView.setAdapter(adapter2);
                    }
                }).addOnFailureListener(e -> Toast.makeText(getActivity(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show());
        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();inflater.inflate(R.menu.menu_diagnosis, menu);super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem save = menu.findItem(R.id.action_save), download = menu.findItem(R.id.action_download);
        download.setVisible(false);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            if(type.equals("d")) {
                final CollectionReference path = db.collection("Email").document("parent " + parent_email)
                        .collection("sent_appointments").document(child_name + " " + user.getEmail())
                        .collection("Dates").document("" + id).collection("Untag_images");
                path.get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if(adapter.getTagCount()==adapter.getItemCount()) {
                        int i = 0;
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Map<String, Object> tag = new HashMap<>();
                            tag.put("tag", adapter.getItem(i).toUpperCase());i++;
                            String id = doc.getId();
                            path.document(id).set(tag, SetOptions.merge());
                        }
                        doctorDiagnosis dg = new doctorDiagnosis();dg.setArguments(bundle);
                        android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        ((RelativeLayout) getActivity().findViewById(R.id.log)).removeAllViews();
                        fragmentManager.beginTransaction().replace(R.id.log, dg).commit();
                    }
                    else{
                        int left = adapter.getItemCount() - adapter.getTagCount();
                        String msg = String.valueOf(left) + " tag left!";
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else{
                doctorDiagnosis dg = new doctorDiagnosis();dg.setArguments(bundle);
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                ((RelativeLayout) getActivity().findViewById(R.id.log)).removeAllViews();
                fragmentManager.beginTransaction().replace(R.id.log, dg).commit();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
