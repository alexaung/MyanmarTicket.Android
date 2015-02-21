package com.takemetomyanmar.myanmarticket;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.takemetomyanmar.myanmarticket.model.AirportTransfer.Booking;
import com.takemetomyanmar.myanmarticket.model.AirportTransfer.Car;
import com.takemetomyanmar.myanmarticket.model.AirportTransfer.Personal;
import com.takemetomyanmar.myanmarticket.model.AirportTransfer.Transfer;
import com.takemetomyanmar.myanmarticket.model.Authentication.Account;


import org.json.JSONException;
import org.json.JSONObject;


import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by AlexAung on 2/7/2015.
 */
public class AirportDetailsFragment extends Fragment {

    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;//Enter the correct environment here;
    private static final String CONFIG_CLIENT_ID = "Ac9jSMVSQ4O-L5qs8zWSG9AwqYtJFgLbvXILXNRuIHD2DwuMYb3PkisWfqwGXbABrFGOjIVm_5j5k0rS";//you need to register with PayPal and enter your client_ID here;

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            .acceptCreditCards(true)
            .languageOrLocale("EN")
            .rememberUser(true)
            .merchantName("Company name");



    private TextView txtService;
    private TextView txtPickUp;
    private TextView txtFlightNo;
    private TextView txtRoute;
    private TextView txtNoOfPassengers;
    private TextView txtNoOfLuggage;

    private TextView txtCarName;
    private TextView txtCarRates;
    private ImageView imgCarImage;
    private TextView txtCarDescription;
    private TextView txtSeatingCapacity;
    private TextView txtCarNoOfLuggage;

    private TextView txtContactName;
    private TextView txtEmail;
    private TextView txtMobilePhone;

    private TextView txtLeadName;
    private TextView txtLeadEmail;
    private TextView txtLeadMobilePhone;

    private Booking booking;

    private ProgressDialog mProgressBar;

    public AirportDetailsFragment(){}
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private static final String ARG_BOOKING_OBJECT = "Booking";
    /**
     * Mobile Service URL.
     */
    private static String SERVICE_URL = "";
    /**
     * Mobile Service URL.
     */
    private static String SERVICE_KEY = "";

    MobileServiceClient mClient;

    private double paymentTotal = 0.0;
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static AirportDetailsFragment newInstance(int sectionNumber, Booking booking) {
        AirportDetailsFragment fragment = new AirportDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putSerializable(ARG_BOOKING_OBJECT, booking);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_airportdetails, container, false);

        SERVICE_URL = getString(R.string.mobile_service_url);
        SERVICE_KEY = getString(R.string.mobile_service_key);
        //mProgressBar = (ProgressBar) rootView.findViewById(R.id.loadingProgressBar);

        // Initialize the progress bar
        //mProgressBar.setVisibility(ProgressBar.GONE);
//        mProgressBar = new ProgressDialog(getActivity());
//        mProgressBar.setTitle("Processing");
//        mProgressBar.setMessage("Please Wait...");
//        mProgressBar.setCancelable(false);


        txtService = (TextView) rootView.findViewById(R.id.txtService);
        txtPickUp = (TextView) rootView.findViewById(R.id.txtPickUp);
        txtFlightNo = (TextView) rootView.findViewById(R.id.txtFlightNo);
        txtRoute = (TextView) rootView.findViewById(R.id.txtRoute);
        txtNoOfPassengers = (TextView) rootView.findViewById(R.id.txtNoOfPassengers);
        txtNoOfLuggage = (TextView) rootView.findViewById(R.id.txtNoOfLuggage);

        txtCarName = (TextView) rootView.findViewById(R.id.txtCarName);
        txtCarRates = (TextView) rootView.findViewById(R.id.txtCarRates);
        imgCarImage = (ImageView) rootView.findViewById(R.id.imgCarImage);
        txtCarDescription = (TextView) rootView.findViewById(R.id.txtCarDescription);
        txtSeatingCapacity = (TextView) rootView.findViewById(R.id.txtSeatingCapacity);
        txtCarNoOfLuggage = (TextView) rootView.findViewById(R.id.txtCarNoOfLuggage);

        txtContactName = (TextView) rootView.findViewById(R.id.txtContactName);
        txtEmail = (TextView) rootView.findViewById(R.id.txtEmail);
        txtMobilePhone = (TextView) rootView.findViewById(R.id.txtMobilePhone);

        txtLeadName = (TextView) rootView.findViewById(R.id.txtLeadName);
        txtLeadEmail = (TextView) rootView.findViewById(R.id.txtLeadEmail);
        txtLeadMobilePhone = (TextView) rootView.findViewById(R.id.txtLeadMobilePhone);


        initPaymentService();

        setFormData();

        final Button btnNext = (Button) rootView.findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

                onBuyPressed(v);
            }
        });

        return rootView;
    }

    private void setFormData(){
        booking = (Booking) getArguments().getSerializable(ARG_BOOKING_OBJECT);

        Collection<Transfer> transfers = booking.getTransfers();
        Transfer transfer = (Transfer) transfers.toArray()[0];
        Car car = transfer.getCar();
        Account account = booking.getBookBy();
        Personal leadPassenger = booking.getLeadPassenger();

        String[] airportCodes = getResources().getStringArray(R.array.airport_codes);
        String[] airportName = getResources().getStringArray(R.array.airport_names);

        int position = Arrays.asList(airportCodes).indexOf(transfer.getFrom());

        txtService.setText(transfer.getService());

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");

        txtPickUp.setText(dateFormat.format(transfer.getTransferDate()));
        txtFlightNo.setText(transfer.getFlightNo());
        txtRoute.setText(airportName[position].toString() + " --> " + transfer.getTo());
        txtNoOfPassengers.setText(String.valueOf(transfer.getNoOfPassenger()));
        txtNoOfLuggage.setText(String.valueOf(transfer.getNoOfLuggage()));

        if(car != null) {
            txtCarName.setText(car.getName());
            txtCarRates.setText(String.format("%.2f", car.getRates()) + " USD");

            int imageId = getActivity().getResources().getIdentifier(car.getImage(), "drawable", getActivity().getPackageName());

            imgCarImage.setImageResource(imageId);
            txtCarDescription.setText(car.getDescription());
            txtSeatingCapacity.setText(String.valueOf(car.getSeatingCapacity()));
            txtCarNoOfLuggage.setText(String.valueOf(car.getLuggage()));
        }

        txtContactName.setText(account.getName());
        txtEmail.setText(account.getEmail());
        txtMobilePhone.setText(account.getPhone());

        if(leadPassenger != null) {
            txtLeadName.setText(leadPassenger.getName());
            txtLeadEmail.setText(leadPassenger.getEmail());
            txtLeadMobilePhone.setText(leadPassenger.getPhone());
        }
        else {
            txtLeadName.setText(account.getName());
            txtLeadEmail.setText(account.getEmail());
            txtLeadMobilePhone.setText(account.getPhone());
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    public void initPaymentService() {
        try {
            Intent intent = new Intent(getActivity(), PayPalService.class);
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

            getActivity().startService(intent);
        } catch (Exception e) {
            Log.i("PayPal Exception", e.getMessage());
        }
    }

    public void onBuyPressed(View pressed) {

        try{
            createBooking();
        }catch (Exception e){
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void createBooking(){

        Transfer transfer = booking.getTransfers().get(0);
        paymentTotal = transfer.getCar().getRates();
        transfer.setRate(paymentTotal);
        transfer.setCar(null);
        ArrayList<Transfer> transfers = new ArrayList<Transfer>();

        transfers.add(transfer);
        booking.setTransfers(transfers);
        booking.setAccount_Id(booking.getBookBy().getId());
        booking.setBookBy(null);

        try {
             mClient = new MobileServiceClient(SERVICE_URL, SERVICE_KEY
                    , getActivity()).withFilter(new ProgressFilter(getActivity()));

            MobileServiceTable<Booking> mBooking = mClient.getTable(Booking.class);


            mBooking.insert(booking, new TableOperationCallback<Booking>() {
                @Override
                public void onCompleted(Booking b, Exception e, ServiceFilterResponse serviceFilterResponse) {
                    if(e == null) {
                        try{
                            booking = b;
                            Transfer transfer = booking.getTransfers().get(0);
                            Car car = transfer.getCar();
                            String description = "Transfer - Arrival ( "+ car.getName()+" - up to " + car.getSeatingCapacity() + " pax + " + car.getLuggage() + " luggage )";
                            PayPalPayment thingToBuy = new PayPalPayment(new BigDecimal(transfer.getRate()),
                                    "USD", description, PayPalPayment.PAYMENT_INTENT_SALE);

                            Intent intent = new Intent(getActivity(), PaymentActivity.class);
                            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
                            startActivityForResult(intent, REQUEST_CODE_PAYMENT);


                        }catch (NumberFormatException ex){
                            Toast.makeText(getActivity(), "Age value cannot be empty. \n Please enter a valid age.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Log.e("AMS", "Exception", e);
                        createAndShowDialog(e, "Error");
                    }


                }
            });
        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data
                        .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {

                        Log.i("PayPal", confirm.toJSONObject().toString(4));

                        JSONObject jsonObj=new JSONObject(confirm.toJSONObject().toString());
                        String paymentId=jsonObj.getJSONObject("response").getString("id");

                        Toast.makeText(getActivity().getApplicationContext(), "PaymentConfirmation info received from PayPal",
                                Toast.LENGTH_LONG).show();
                        booking.setPaymentId(paymentId);
                        verifyMobilePayment();

                    } catch (JSONException e) {
                        Log.e("PayPal", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment was submitted. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth = data
                        .getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("FuturePaymentExample", auth.toJSONObject().toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("FuturePaymentExample", authorization_code);

                        sendAuthorizationToServer(auth);

                        Toast.makeText(getActivity().getApplicationContext(), "Future Payment code received from PayPal",
                                Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        Log.e("FuturePaymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("FuturePaymentExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("FuturePaymentExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        }
    }

    private void sendAuthorizationToServer(PayPalAuthorization authorization) {


    }

    private void verifyMobilePayment(){

        Gson gson = new Gson();
        String json = gson.toJson(booking);
        JsonElement element = gson.fromJson (json, JsonElement.class);
        JsonObject jsonObj = element.getAsJsonObject();

        String apiName = "PayPal";

        mClient.invokeApi(apiName, jsonObj, new ApiJsonOperationCallback(){

            @Override
            public void onCompleted(JsonElement jsonElement, Exception e, ServiceFilterResponse response) {
                if(e == null) {
                    try{

                        Toast.makeText(getActivity(), "Payment Verified.", Toast.LENGTH_LONG).show();


                    }catch (NumberFormatException ex){
                        Toast.makeText(getActivity(), "Payment Verification Fail.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.e("AMS", "Exception", e);
                    createAndShowDialog(e, "Error");
                }
            }
        });


    }

    @Override
    public void onDestroy() {
        // Stop service when done
        getActivity().stopService(new Intent(getActivity(), PayPalService.class));
        super.onDestroy();
    }

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

//    private class ProgressFilter implements ServiceFilter {
//
//        @Override
//        public void handleRequest(ServiceFilterRequest request, NextServiceFilterCallback nextServiceFilterCallback,
//                                  final ServiceFilterResponseCallback responseCallback) {
//            getActivity().runOnUiThread(new Runnable() {
//
//                @Override
//                public void run() {
//                    if (mProgressBar != null)
//                        mProgressBar.show();
//                        //mProgressBar.setVisibility(ProgressBar.VISIBLE);
//                }
//            });
//
//            nextServiceFilterCallback.onNext(request, new ServiceFilterResponseCallback() {
//
//                @Override
//                public void onResponse(ServiceFilterResponse response, Exception exception) {
//                    getActivity().runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            if (mProgressBar != null)
//                                mProgressBar.dismiss();
//                                //mProgressBar.setVisibility(ProgressBar.GONE);
//                        }
//                    });
//
//                    if (responseCallback != null)  responseCallback.onResponse(response, exception);
//                }
//            });
//        }
//    }

}
