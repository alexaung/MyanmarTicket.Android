package com.takemetomyanmar.myanmarticket.Authentication;

/**
 * Created by AMO on 2/13/2015.
 */
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class BaseActivity extends FragmentActivity {

    protected AuthService mAuthService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        AuthenticationApplication myApp = (AuthenticationApplication) getApplication();
        myApp.setCurrentActivity(this);
        mAuthService = myApp.getAuthService();
    }
}
