package com.example.gupta.slow;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.example.gupta.slow.Common.Common;
import com.example.gupta.slow.Model.Rider;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dmax.dialog.SpotsDialog;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {


    private ScrollView rootLayout;
    private EditText signUpUserName, signUpEmail, signUpPhone, signUpPassword;
    private Button signup_up_Btn;
    private TextView sign_in_text;

    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        setContentView(R.layout.activity_sign_up);
        rootLayout = (ScrollView) findViewById(R.id.signuprootLayout);
        signUpUserName = (EditText) findViewById(R.id.sign_up_name);
        signUpEmail = (EditText) findViewById(R.id.sign_up_email);
        signUpPhone = (EditText) findViewById(R.id.sign_up_phone);
        signUpPassword = (EditText) findViewById(R.id.sign_up_password);
        signup_up_Btn = (Button) findViewById(R.id.sign_up_btn);
        sign_in_text = (TextView) findViewById(R.id.signintext);

        signup_up_Btn.setOnClickListener(this);


        auth=FirebaseAuth.getInstance();
        db=FirebaseDatabase.getInstance();
        users=db.getReference(Common.user_rider_tbl);
    }

    @Override
    public void onClick(View v) {
        if (v == sign_in_text) {
            Log.d("tag", "signintextclicked");
            try {
                Intent i = new Intent(this, LoginActivity.class);

                startActivity(i);
                finish();
            } catch (Exception e) {
                Log.d("tag", e.getMessage());
            }

        } else if (v == signup_up_Btn) {

            Log.d("tag", "signInBtnClicked");
            try {
                if (TextUtils.isEmpty(signUpUserName.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter email address", Snackbar.LENGTH_SHORT).show();
                    return;

                }
                if (TextUtils.isEmpty(signUpEmail.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter email address", Snackbar.LENGTH_SHORT).show();
                    return;

                }
                if (TextUtils.isEmpty(signUpEmail.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter email address", Snackbar.LENGTH_SHORT).show();
                    return;

                }
                if (signUpPassword.getText().toString().length() < 6) {
                    Snackbar.make(rootLayout, "Please enter password", Snackbar.LENGTH_SHORT).show();
                    return;

                }

                final AlertDialog waitingDialog=new SpotsDialog(SignUpActivity.this);
                auth.createUserWithEmailAndPassword(signUpEmail.getText().toString(),signUpPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        waitingDialog.dismiss();

                        Rider rider=new Rider();
                        rider.setEmail(signUpEmail.getText().toString());
                        rider.setName(signUpUserName.getText().toString());
                        rider.setPassword(signUpPassword.getText().toString());
                        rider.setPhone(signUpPhone.getText().toString());

                        users.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(rider).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Snackbar.make(rootLayout,"Registers sucessfull",Snackbar.LENGTH_SHORT).show();

                                startActivity(new Intent(SignUpActivity.this,NavigationDrawerActivity.class));

                                finish();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Snackbar.make(rootLayout,"Registers not sucessfull"+e.getMessage(),Snackbar.LENGTH_LONG).show();
                            }
                        })
                        ;


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        waitingDialog.dismiss();
                        Snackbar.make(rootLayout,"Registers not sucessfull"+e.getMessage(),Snackbar.LENGTH_SHORT).show();


                    }
                });


            } catch (Exception e) {
                Log.d("tag", e.getMessage());
            }
        }
    }


}
