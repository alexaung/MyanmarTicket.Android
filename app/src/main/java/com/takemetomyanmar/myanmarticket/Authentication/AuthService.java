package com.takemetomyanmar.myanmarticket.Authentication;

/**
 * Created by AMO on 2/13/2015.
 */
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.http.StatusLine;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceJsonTable;
import com.microsoft.windowsazure.mobileservices.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.TableJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableJsonQueryCallback;
import com.microsoft.windowsazure.mobileservices.UserAuthenticationCallback;
import com.takemetomyanmar.myanmarticket.R;

public class AuthService {

    private MobileServiceClient mClient;
    private MobileServiceJsonTable mTableAccounts;
    private MobileServiceJsonTable mTableAuthData;
    private MobileServiceJsonTable mTableBadAuth;
    private Context mContext;
    private final String TAG = "AuthService";
    private boolean mShouldRetryAuth;
    private boolean mIsCustomAuthProvider = false;
    private MobileServiceAuthenticationProvider mProvider;
    private String SERVICE_URL;
    private String SERVICE_KEY;
    public AuthService(Context context) {
        mContext = context;

        try {
            SERVICE_URL = context.getString(R.string.mobile_service_url);
            SERVICE_KEY = context.getString(R.string.mobile_service_key);
            mClient = new MobileServiceClient(SERVICE_URL,
                    SERVICE_KEY, mContext)
                    .withFilter(new MyServiceFilter());
            mTableAccounts = mClient.getTable("Accounts");
            mTableAuthData = mClient.getTable("AuthData");
            mTableBadAuth = mClient.getTable("BadAuth");
        } catch (MalformedURLException e) {
            Log.e(TAG, "There was an error creating the Mobile Service.  Verify the URL");
        }
    }

    public void setContext(Context context) {
        mClient.setContext(context);
    }

    public String getUserId() {
        return mClient.getCurrentUser().getUserId();
    }

    //Show the login dialog
    public void login(Context activityContext, MobileServiceAuthenticationProvider provider, UserAuthenticationCallback callback) {
        mProvider = provider;
        mClient.setContext(activityContext);
        mClient.login(provider, callback);
    }

    /**
     * Handles logging in with custom auth
     * @param username
     * @param password
     * @param callback
     */
    public void login(String username, String password, ApiJsonOperationCallback callback) {
        JsonObject customUser = new JsonObject();
        customUser.addProperty("email", username);
        customUser.addProperty("password", password);

        //mTableAccounts.insert(customUser, parameters, callback);
        mClient.invokeApi("CustomLogin", customUser, callback);
    }

    public void getAuthData(TableJsonQueryCallback callback) {
        mTableAuthData.where().execute(callback);
    }

    /**
     * Checks to see if we have userId and token stored on the device and sets them if so
     * @return
     */
    public boolean isUserAuthenticated() {
        SharedPreferences settings = mContext.getSharedPreferences("UserData", 0);
        if (settings != null) {
            String userId = settings.getString("userid", null);
            String token = settings.getString("token", null);
            if (userId != null && !userId.equals("")) {
                setUserData(userId, token);
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a nwe MobileServiceUser using a userId and token passed in.
     * Also sets the current provider
     * @param userId
     * @param token
     */
    public void setUserData(String userId, String token) {
        MobileServiceUser user = new MobileServiceUser(userId);
        user.setAuthenticationToken(token);
        mClient.setCurrentUser(user);

        //Check for custom provider
        String provider = userId.substring(0, userId.indexOf(":"));
        if (provider.equals("Custom")) {
            mProvider = null;
            mIsCustomAuthProvider = true;
        } else if (provider.equals("Facebook"))
            mProvider = MobileServiceAuthenticationProvider.Facebook;
        else if (provider.equals("Twitter"))
            mProvider = MobileServiceAuthenticationProvider.Twitter;
        else if (provider.equals("MicrosoftAccount"))
            mProvider = MobileServiceAuthenticationProvider.MicrosoftAccount;
        else if (provider.equals("Google"))
            mProvider = MobileServiceAuthenticationProvider.Google;
    }

    /***
     * Pulls the user ID and token out of a json object from the server
     * @param jsonElement
     */
    public void setUserAndSaveData(JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String userId = jsonObject.getAsJsonPrimitive("userId").getAsString();
        String token = jsonObject.getAsJsonPrimitive("token").getAsString();
        setUserData(userId, token);
        saveUserData();
    }

    /**
     * Saves userId and token to SharedPreferences.
     * NOTE:  This is not secure and is just used as a storage mechanism.  In reality, you would want to
     * come up with a more secure way of storing this information.
     */
    public void saveUserData() {
        SharedPreferences settings = mContext.getSharedPreferences("UserData", 0);
        SharedPreferences.Editor preferencesEditor = settings.edit();
        preferencesEditor.putString("userid", mClient.getCurrentUser().getUserId());
        preferencesEditor.putString("token", mClient.getCurrentUser().getAuthenticationToken());
        preferencesEditor.commit();
    }

    /**
     * Register the user if they're creating a custom auth account
     * @param username
     * @param password
     * @param confirm
     * @param email
     * @param callback
     */
    public void registerUser(String username, String password, String confirm,
                             String email, String phone,
                             ApiJsonOperationCallback callback) {
        JsonObject newUser = new JsonObject();
        newUser.addProperty("username", username);
        newUser.addProperty("password", password);
        newUser.addProperty("email", email);
        newUser.addProperty("phone", phone);

        //mTableAccounts.insert(newUser, callback);
        mClient.invokeApi("CustomRegistration", newUser, callback);
    }

    /**
     * Handles logging the user out including:
     * -deleting cookies so their login with a provider won't be cached in the web view
     * -removing the userdata from the shared preferences
     * -setting the current user object on the client to logged out
     * -optionally redirects to the login page if requested
     * @param shouldRedirectToLogin
     */
    public void logout(boolean shouldRedirectToLogin) {
        //Clear the cookies so they won't auto login to a provider again
        CookieSyncManager.createInstance(mContext);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        //Clear the user id and token from the shared preferences
        SharedPreferences settings = mContext.getSharedPreferences("UserData", 0);
        SharedPreferences.Editor preferencesEditor = settings.edit();
        preferencesEditor.clear();
        preferencesEditor.commit();
        //Clear the user and return to the auth activity
        mClient.logout();
        //Take the user back to the auth activity to relogin if requested
        if (shouldRedirectToLogin) {
            Intent logoutIntent = new Intent(mContext, AuthenticationActivity.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(logoutIntent);
        }
    }

    /**
     * Calls a method on the server that will auto trigger a 401 result
     * @param shouldRetry
     * @param callback
     */
    public void testForced401(boolean shouldRetry,
                              TableJsonOperationCallback callback) {
        JsonObject data = new JsonObject();
        data.addProperty("data", "data");
        mShouldRetryAuth = shouldRetry;
        mTableBadAuth.insert(data, callback);
    }

    /**
     * Custom ServiceFilter which facilitates retrys on 401s (if requested)
     */
    private class MyServiceFilter implements ServiceFilter {


        @Override
        public void handleRequest(final ServiceFilterRequest request, final NextServiceFilterCallback nextServiceFilterCallback,
                                  final ServiceFilterResponseCallback responseCallback) {

            nextServiceFilterCallback.onNext(request, new ServiceFilterResponseCallback() {
                @Override
                public void onResponse(ServiceFilterResponse response, Exception exception) {

                    if (exception != null) {
                        Log.e(TAG, "MyServiceFilter onResponse Exception: " + exception.getMessage());
                    }

                    StatusLine status = response.getStatus();
                    int statusCode = status.getStatusCode();
                    if (statusCode == 401) {
                        final CountDownLatch latch = new CountDownLatch(1);
                        //Log the user out but don't send them to the login page
                        logout(false);
                        //If we shouldn't retry (or they've used custom auth),
                        //we're going to kick them out for now
                        //If you're doing custom auth, you'd need to show your own
                        //custom auth popup to login with
                        if (mShouldRetryAuth && !mIsCustomAuthProvider) {
                            //Get the current activity for the context so we can show the login dialog
                            AuthenticationApplication myApp = (AuthenticationApplication) mContext;
                            Activity currentActivity = myApp.getCurrentActivity();
                            mClient.setContext(currentActivity);

                            currentActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mClient.login(mProvider, new UserAuthenticationCallback() {
                                        @Override
                                        public void onCompleted(MobileServiceUser user, Exception exception,
                                                                ServiceFilterResponse response) {
                                            if (exception == null) {
                                                //Save their updated user data locally
                                                saveUserData();
                                                //Update the requests X-ZUMO-AUTH header
                                                request.removeHeader("X-ZUMO-AUTH");
                                                request.addHeader("X-ZUMO-AUTH", mClient.getCurrentUser().getAuthenticationToken());

                                                //Add our BYPASS querystring parameter to the URL
                                                Uri.Builder uriBuilder = Uri.parse(request.getUrl()).buildUpon();
                                                uriBuilder.appendQueryParameter("bypass", "true");
                                                try {
                                                    request.setUrl(uriBuilder.build().toString());
                                                } catch (URISyntaxException e) {
                                                    Log.e(TAG, "Couldn't set request's new url: " + e.getMessage());
                                                    e.printStackTrace();
                                                }
                                                latch.countDown();

                                            } else {
                                                Log.e(TAG, "User did not login successfully after 401");
                                                //Kick user back to login screen
                                                logout(true);
                                            }

                                        }
                                    });
                                }
                            });
                            try {
                                latch.await();
                            } catch (InterruptedException e) {
                                Log.e(TAG, "Interrupted exception: " + e.getMessage());
                                return;
                            }

                            nextServiceFilterCallback.onNext(request, responseCallback);
                        } else {
                            //Log them out and proceed with the response
                            logout(true);
                            responseCallback.onResponse(response, exception);
                        }
                    } else {//
                        responseCallback.onResponse(response, exception);
                    }
                }
            });
        }
    }
}
