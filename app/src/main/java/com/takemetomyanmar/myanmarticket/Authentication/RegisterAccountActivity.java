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

public class RegisterAccountActivity extends BaseActivity {

    private final String TAG = "RegisterAccountActivity";
    private Button btnRegister;
    private EditText mTxtUsername;
    private EditText mTxtPassword;
    private EditText mTxtConfirm;
    private EditText mTxtEmail;
    private EditText mTxtPhone;
    private Activity mActivity;
    private int position;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account);
        setTitle("Register");

        mActivity = this;
        // Initialize the progress bar
        //mProgressBar.setVisibility(ProgressBar.GONE);
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        Bundle extras = getIntent().getExtras();
        position = extras.getInt("position");

        //Get UI elements
        btnRegister = (Button) findViewById(R.id.btnRegister);
        mTxtUsername = (EditText) findViewById(R.id.txtRegisterUsername);
        mTxtPassword = (EditText) findViewById(R.id.txtRegisterPassword);
        mTxtConfirm = (EditText) findViewById(R.id.txtRegisterConfirm);
        mTxtEmail = (EditText) findViewById(R.id.txtRegisterEmail);
        mTxtPhone = (EditText) findViewById(R.id.txtRegisterPhone);

        //Set click listeners
        btnRegister.setOnClickListener(registerClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register_account, menu);
        return true;
    }

    View.OnClickListener registerClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            //We're just logging the validation errors, we should be showing something to the user
            if (mTxtUsername.getText().toString().equals("") ||
                    mTxtPassword.getText().toString().equals("") ||
                    mTxtConfirm.getText().toString().equals("") ||
                    mTxtEmail.getText().toString().equals("") ||
                    mTxtPhone.getText().toString().equals(""))
            {
                createAndShowDialog("You must enter all fields to register", "Error");
                Log.w(TAG, "You must enter all fields to register");
                return;
            } else if (!mTxtPassword.getText().toString().equals(mTxtConfirm.getText().toString())) {
                createAndShowDialog("The passwords you've entered don't match", "Error");
                Log.w(TAG, "The passwords you've entered don't match");
                return;
            } else {

                mActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (progressDialog != null)
                            progressDialog.show();
                    }
                });

                mAuthService.registerUser(mTxtUsername.getText().toString(),
                        mTxtPassword.getText().toString(),
                        mTxtConfirm.getText().toString(),
                        mTxtEmail.getText().toString(),
                        mTxtPhone.getText().toString(),
                        new ApiJsonOperationCallback() {
                            @Override
                            public void onCompleted(JsonElement jsonElement, Exception exception,
                                                    ServiceFilterResponse response) {

                                mActivity.runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        if (progressDialog != null)
                                            progressDialog.dismiss();
                                    }
                                });

                                if (exception == null) {
                                    //If that was successful, set and save the user data
                                    mAuthService.setUserAndSaveData(jsonElement);
                                    //Finish this activity and run the logged in activity
                                    Intent mainIntent = new Intent(mActivity, MainActivity.class);
                                    mainIntent.putExtra("position", position);
                                    startActivity(mainIntent);
                                    //((MainActivity) mActivity).onNavigationDrawerItemSelected(position);
                                } else {

                                    createAndShowDialog(response.getContent(), "Error");
                                    Toast.makeText(mActivity, response.getContent(),
                                            Toast.LENGTH_LONG).show();
                                    Log.e(TAG, "There was an error registering the user: " + exception.getMessage());
                                }
                            }
                        });
            }
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
