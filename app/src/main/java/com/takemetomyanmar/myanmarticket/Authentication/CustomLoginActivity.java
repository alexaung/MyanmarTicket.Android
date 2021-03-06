package com.takemetomyanmar.myanmarticket.Authentication;

/**
 * Created by AMO on 2/13/2015.
 */
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableJsonOperationCallback;
import com.takemetomyanmar.myanmarticket.AirportFragment;
import com.takemetomyanmar.myanmarticket.HomeFragment;
import com.takemetomyanmar.myanmarticket.MainActivity;
import com.takemetomyanmar.myanmarticket.R;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CustomLoginActivity extends BaseActivity {

    private final String TAG = "CustomLoginActivity";
    private Button mBtnCancel;
    private Button mBtnLogin;
    private Button mBtnRegisterForAccount;
    private EditText mTxtUsername;
    private EditText mTxtPassword;
    private Activity mActivity;
    private int position;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_login);
        setTitle("Login");

        mActivity = this;
        // Initialize the progress bar
        //mProgressBar.setVisibility(ProgressBar.GONE);
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        Bundle extras = getIntent().getExtras();
        position = extras.getInt("position");
        //Get UI objects
        mBtnCancel = (Button) findViewById(R.id.btnCancel);
        mBtnLogin = (Button) findViewById(R.id.btnLogin);
        mBtnRegisterForAccount = (Button) findViewById(R.id.btnRegisterForAccount);
        mTxtUsername = (EditText) findViewById(R.id.txtUsername);
        mTxtPassword = (EditText) findViewById(R.id.txtPassword);

        //Add on click listeners
        mBtnCancel.setOnClickListener(cancelClickListener);
        mBtnLogin.setOnClickListener(loginClickListener);
        mBtnRegisterForAccount.setOnClickListener(registerClickListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.custom_login, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
    }

    View.OnClickListener cancelClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mActivity.finish();
        }
    };

    View.OnClickListener loginClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            mActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (progressDialog != null)
                        progressDialog.show();
                }
            });

            if (mTxtPassword.getText().toString().equals("") ||
                    mTxtUsername.getText().toString().equals("")) {
                //We're just logging this here, we should show something to the user
                Log.w(TAG, "Username or password not entered");
                mActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                    }
                });
                return;
            }
            mAuthService.login(mTxtUsername.getText().toString(), mTxtPassword.getText().toString(), new ApiJsonOperationCallback(){

                @Override
                public void onCompleted(JsonElement jsonElement, Exception exception, ServiceFilterResponse response) {
                    mActivity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (progressDialog != null)
                                progressDialog.dismiss();
                        }
                    });
                    if (exception == null) {
                        //If they've registered successfully, we'll save and set the userdata and then
                        //show the logged in activity
                        mAuthService.setUserAndSaveData(jsonElement);


                        //Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                        //mainIntent.putExtra("position", position);
                        //startActivity(mainIntent);
                        //((MainActivity) mActivity).onNavigationDrawerItemSelected(position);
                        finish();
                    } else {
                        createAndShowDialog(response.getContent(), "Error");
                        Log.e(TAG, "Error loggin in: " + exception.getMessage());
                    }
                }

            });

        }
    };
    View.OnClickListener registerClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent registerIntent = new Intent(getApplicationContext(), RegisterAccountActivity.class);
            registerIntent.putExtra("position", position);
            startActivity(registerIntent);
        }
    };


    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }
    /**
     * Creates a dialog and shows it
     *
     * @param message
     *            The dialog message
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }
}
