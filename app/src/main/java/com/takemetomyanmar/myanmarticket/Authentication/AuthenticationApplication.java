package com.takemetomyanmar.myanmarticket.Authentication;

/**
 * Created by AMO on 2/13/2015.
 */
import android.app.Activity;
import android.app.Application;

public class AuthenticationApplication extends Application {
    private AuthService mAuthService;
    private Activity mCurrentActivity;

    public AuthenticationApplication() {}

    public AuthService getAuthService() {
        if (mAuthService == null) {
            mAuthService = new AuthService(this);
        }
        return mAuthService;
    }

    public void setCurrentActivity(Activity activity) {
        mCurrentActivity = activity;
    }

    public Activity getCurrentActivity() {
        return mCurrentActivity;
    }
}
