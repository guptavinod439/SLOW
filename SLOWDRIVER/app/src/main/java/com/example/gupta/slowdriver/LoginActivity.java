package com.example.gupta.slowdriver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gupta.slowdriver.Common.Common;
import com.example.gupta.slowdriver.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView signupbtn;
    private Button signinbtn;
    private ScrollView rootLayout;

   private   EditText edtId,edtPassword;

    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        signupbtn=(TextView)findViewById(R.id.signuptext);
        signupbtn.setOnClickListener(this);
        edtId=(EditText) findViewById(R.id.login_id);
        edtPassword=(EditText) findViewById(R.id.login_password);
        signinbtn=(Button)findViewById(R.id.sign_in_btn);
        signinbtn.setOnClickListener(this);
        rootLayout=(ScrollView) findViewById(R.id.rootLayout);
//        googlebtn=(Button)findViewById(R.id.sign_in_btn_google);
//        googlebtn.setOnClickListener(this);

//        progressBar=new ProgressDialog(this);

        auth=FirebaseAuth.getInstance();
        db=FirebaseDatabase.getInstance();
        users=db.getReference(Common.user_driver_tbl);







    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
//
    @Override
    public void onClick(View v) {
        if(v== signupbtn) {
            Log.d("tag", "signupbtnclicked");
            try {
                Intent i = new Intent(this, SignUpActivity.class);

                startActivity(i);
            } catch (Exception e) {
                Log.d("tag", e.getMessage());
            }

        }else if (v==signinbtn){

            Log.d("tag", "signInBtnClicked");
            try {
                if (TextUtils.isEmpty(edtId.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter email address", Snackbar.LENGTH_SHORT).show();
                    return;

                }

                if (edtPassword.getText().toString().length() < 6) {
                    Snackbar.make(rootLayout, "Please enter password", Snackbar.LENGTH_SHORT).show();
                    return;

                }

                auth.signInWithEmailAndPassword(edtId.getText().toString(),edtPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {

                                FirebaseDatabase.getInstance().getReference(Common.user_driver_tbl)
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                Common.currentUser = dataSnapshot.getValue(User.class);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });


                                startActivity(new Intent(LoginActivity.this,Welcome.class));



                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(rootLayout,"Sign not sucessfull"+e.getMessage(),Snackbar.LENGTH_SHORT).show();
                    }
                });


            } catch (Exception e) {
                Log.d("tag", e.getMessage());
            }
        }
//
    }
}
