package com.takemetomyanmar.myanmarticket;

import android.app.Activity;
import android.app.ProgressDialog;

import com.microsoft.windowsazure.mobileservices.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;

/**
 * Created by aungmo on 20/2/15.
 */
public class ProgressFilter implements ServiceFilter {

    private Activity mActivity;
    private ProgressDialog mProgressDialog;
    public ProgressFilter(Activity activity){
        this.mActivity = activity;
        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setTitle("Processing");
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.setCancelable(false);
    }

    @Override
    public void handleRequest(ServiceFilterRequest request, NextServiceFilterCallback nextServiceFilterCallback,
                              final ServiceFilterResponseCallback responseCallback) {
        mActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (mProgressDialog != null)
                    mProgressDialog.show();
                //mProgressBar.setVisibility(ProgressBar.VISIBLE);
            }
        });

        nextServiceFilterCallback.onNext(request, new ServiceFilterResponseCallback() {

            @Override
            public void onResponse(ServiceFilterResponse response, Exception exception) {
                mActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (mProgressDialog != null)
                            mProgressDialog.dismiss();
                        //mProgressBar.setVisibility(ProgressBar.GONE);
                    }
                });

                if (responseCallback != null)  responseCallback.onResponse(response, exception);
            }
        });
    }
}
