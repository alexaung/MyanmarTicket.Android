package com.takemetomyanmar.myanmarticket;

import android.app.Activity;
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

import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.takemetomyanmar.myanmarticket.adapter.KeyValueArrayAdapter;
import com.takemetomyanmar.myanmarticket.model.AirportTransfer.Booking;
import com.takemetomyanmar.myanmarticket.model.AirportTransfer.Car;
import com.takemetomyanmar.myanmarticket.model.AirportTransfer.Personal;
import com.takemetomyanmar.myanmarticket.model.AirportTransfer.Transfer;

import org.json.JSONException;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;

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

    public AirportDetailsFragment(){}
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private static final String ARG_BOOKING_OBJECT = "Booking";

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
        Booking booking = (Booking) getArguments().getSerializable(ARG_BOOKING_OBJECT);

        Transfer transfer = booking.getTransfer();
        Car car = transfer.getCar();
        Personal account = booking.getBookBy();
        Personal leadPassenger = booking.getLeadPassenger();

        String[] airportCodes = getResources().getStringArray(R.array.airport_codes);
        String[] airportName = getResources().getStringArray(R.array.airport_names);

        int position = Arrays.asList(airportCodes).indexOf(transfer.getFrom());

        txtService.setText(transfer.getService());

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");

        txtPickUp.setText(dateFormat.format(transfer.getDate()));
        txtFlightNo.setText(transfer.getFlightNo());
        txtRoute.setText(airportName[position].toString() + " --> " + transfer.getTo());
        txtNoOfPassengers.setText(String.valueOf(transfer.getNoOfPassenger()));
        txtNoOfLuggage.setText(String.valueOf(transfer.getNoOfLuggage()));

        txtCarName.setText(car.getName());
        txtCarRates.setText(String.format("%.2f", car.getRates()));

        int imageId = getActivity().getResources().getIdentifier(car.getImage(), "drawable", getActivity().getPackageName());

        imgCarImage.setImageResource(imageId);
        txtCarDescription.setText(car.getDescription());
        txtSeatingCapacity.setText(String.valueOf(car.getSeatingCapacity()));
        txtCarNoOfLuggage.setText(String.valueOf(car.getLuggage()));

        txtContactName.setText(account.getTitle() + " " + account.getFirstName() + " " + account.getLastName());
        txtEmail.setText(account.getEmail());
        txtMobilePhone.setText(account.getPhone());

        if(leadPassenger != null) {
            txtLeadName.setText(leadPassenger.getTitle() + " " + leadPassenger.getFirstName() + " " + leadPassenger.getLastName());
            txtLeadEmail.setText(leadPassenger.getEmail());
            txtLeadMobilePhone.setText(leadPassenger.getPhone());
        }
        else {
            txtLeadName.setText(account.getTitle() + " " + account.getFirstName() + " " + account.getLastName());
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

                PayPalPayment thingToBuy = new PayPalPayment(new BigDecimal(20.00),
                        "USD", "Transfer - Arrival (Kullager)", PayPalPayment.PAYMENT_INTENT_SALE);

                Intent intent = new Intent(getActivity(), PaymentActivity.class);
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
                startActivityForResult(intent, REQUEST_CODE_PAYMENT);

        }catch (NumberFormatException e){
            Toast.makeText(getActivity(), "Age value cannot be empty. \n Please enter a valid age.", Toast.LENGTH_LONG).show();
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
                        Log.i("paymentExample", confirm.toJSONObject().toString(4));

                        Toast.makeText(getActivity().getApplicationContext(), "PaymentConfirmation info received from PayPal",
                                Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
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

    @Override
    public void onDestroy() {
        // Stop service when done
        getActivity().stopService(new Intent(getActivity(), PayPalService.class));
        super.onDestroy();
    }
}
