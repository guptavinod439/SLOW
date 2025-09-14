package com.example.gupta.slow;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;


import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gupta.slow.Common.Common;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView signupbtn;
    private Button signinbtn,googlebtn;
    private ScrollView rootLayout;

   private   EditText edtId,edtPassword;

    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;
    private Button chooselanguage;



    private FirebaseAuth.AuthStateListener firebaseAuthlis;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog progressBar;
    private  static final int RC_SIGN_IN=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
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
        progressBar=new ProgressDialog(this);
        googlebtn=(Button)findViewById(R.id.sign_in_btn_google);
        googlebtn.setOnClickListener(this);
        chooselanguage=(Button)findViewById(R.id.Btn);


        chooselanguage.setOnClickListener(this);
        auth=FirebaseAuth.getInstance();
        db=FirebaseDatabase.getInstance();
        users=db.getReference(Common.user_rider_tbl);



        firebaseAuthlis=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser()!=null){
                    Intent i=new Intent(LoginActivity.this,NavigationDrawerActivity.class);
                    startActivity(i);
                    finish();
                }

            }
        };


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient=new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginActivity.this,"You got an error",Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
    }


    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(firebaseAuthlis);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                GoogleSignInAccount account=result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                finish();
                progressBar.dismiss();

            }else {

            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        Log.d("tag","signInWithCredentials:onComplete"+task.isSuccessful());
                        if(!task.isSuccessful()){
                            Log.d("tag","signInWithCredentials",task.getException());
                            Toast.makeText(LoginActivity.this,"Authentication failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

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
                                startActivity(new Intent(LoginActivity.this,NavigationDrawerActivity.class));

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
        }else if(v== googlebtn){
            Log.d("tag","googlebtnclicked");
            progressBar.setMessage("Please wait!!!");
            progressBar.show();

            signIn();

        }else if(v==chooselanguage){
            Log.d("tag","choose cliscked");
            showChangeLanguageDialog();



        }

    }



    private void showChangeLanguageDialog() {

        final String[] listItems = {"English", "हिंदी", "اবাংলা" };
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(LoginActivity.this);
        mBuilder.setTitle("Choose Language.......");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    setLocale("en");
                    recreate();
                }
                else if (i == 1) {
                    setLocale("hi");
                    recreate();
                }
                else if (i == 2) {
                    setLocale("bn");
                    recreate();
                }

                dialogInterface.dismiss();
            }

        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }


    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }
    public  void  loadLocale(){
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang","");
        setLocale(language);
    }
}
