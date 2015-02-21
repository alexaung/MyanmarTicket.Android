package com.takemetomyanmar.myanmarticket;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;
import com.takemetomyanmar.myanmarticket.Util.Validation;
import com.takemetomyanmar.myanmarticket.adapter.KeyValueArrayAdapter;
import com.takemetomyanmar.myanmarticket.model.AirportTransfer.Booking;
import com.takemetomyanmar.myanmarticket.model.AirportTransfer.Car;
import com.takemetomyanmar.myanmarticket.model.Authentication.Account;
import com.takemetomyanmar.myanmarticket.model.AirportTransfer.Personal;
import com.takemetomyanmar.myanmarticket.model.AirportTransfer.Transfer;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static java.util.UUID.randomUUID;

/**
 * Created by AMO on 2/6/2015.
 */
public class AirportPersonalFragment extends Fragment {

    public LinearLayout leadPassengerGroup;
    public TextView txtName;
    public TextView txtMobilePhone;
    public TextView txtEmail;

    CheckBox chkLeadPassenger;
    public Spinner spiPTitle;
    public EditText txtPName;
    public EditText txtPMobilePhone;
    public EditText txtPEmail;

    public String accountTitleCode;
    public String leadTitleCode;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private static final String ARG_TRANSFER_OBJECT = "Transfer";
    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;

    /**
     * Mobile Service URL.
     */
    private static String SERVICE_URL = "";
    /**
     * Mobile Service URL.
     */
    private static String SERVICE_KEY = "";
    /**
     * Mobile Service Client reference
     */
    private MobileServiceClient mClient;
    /**
     * Mobile Service Table used to access data
     */
    private MobileServiceTable<Account> mAccount;

    private Account mAccountObj;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static AirportPersonalFragment newInstance(int sectionNumber, Transfer transfer) {
        AirportPersonalFragment fragment = new AirportPersonalFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putSerializable(ARG_TRANSFER_OBJECT, transfer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_airportpersonal, container, false);

        registerViews(rootView);

        setControlValidator();

        return rootView;
    }


    private void registerViews(View rootView) {

        final LinearLayout leadPassengerGroup = (LinearLayout) rootView.findViewById(R.id.leadPassengerGroup);
        leadPassengerGroup.setVisibility(View.GONE);

        txtName = (TextView) rootView.findViewById(R.id.txtContactName);
        txtEmail = (TextView) rootView.findViewById(R.id.txtEmail);
        txtMobilePhone = (TextView) rootView.findViewById(R.id.txtMobilePhone);

        txtPName = (EditText) rootView.findViewById(R.id.txtPName);
        txtPMobilePhone = (EditText) rootView.findViewById(R.id.txtPMobilePhone);
        txtPEmail = (EditText) rootView.findViewById(R.id.txtPEmail);


        KeyValueArrayAdapter adapter = new KeyValueArrayAdapter(getActivity(),android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.setKeyValue(getResources().getStringArray(R.array.title_codes),
                getResources().getStringArray(R.array.title_names));

        spiPTitle.setAdapter(adapter);

        spiPTitle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view,
                                       final int position, final long id) {

                KeyValueArrayAdapter adapter = (KeyValueArrayAdapter) parent.getAdapter();

                leadTitleCode = adapter.getEntryValue(position);

                //Ln.d("Entry=" + adapter.getEntry(position));
                //Ln.d("EntryValue=" + adapter.getEntryValue(position));
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
            }

        });

        chkLeadPassenger = (CheckBox) rootView.findViewById (R.id.checkBox);

        chkLeadPassenger.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is chkIos checked?
                if (((CheckBox) v).isChecked()) {
                    //Case 1
                    leadPassengerGroup.setVisibility(View.GONE);
                }
                else
                    leadPassengerGroup.setVisibility(View.VISIBLE);

            }
        });

        final Button btnNext = (Button) rootView.findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                if ( checkValidation () )
                    submitForm();
                else
                    Toast.makeText(getActivity(), "Form contains error", Toast.LENGTH_LONG).show();

            }
        });


    }

    private void setAccountDetail() {
        SERVICE_URL = getString(R.string.mobile_service_url);
        SERVICE_KEY = getString(R.string.mobile_service_key);

        // Create the Mobile Service Client instance, using the provided
        // Mobile Service URL and key
        try {
            mClient = new MobileServiceClient(SERVICE_URL,
                    SERVICE_KEY,
                    getActivity()).withFilter(new ProgressFilter(getActivity()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // Get the Mobile Service Table instance to use
        mAccount = mClient.getTable(Account.class);

        // Get the items that weren't marked as completed and add them in the
        // adapter
        mAccount.where().field("Email").eq().execute(new TableQueryCallback<Account>() {

            public void onCompleted(List<Account> result, int count, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {

                    mAccountObj = result.get(0);
                    txtName.setText(mAccountObj.getName());
                    txtEmail.setText(mAccountObj.getEmail());
                    txtMobilePhone.setText(mAccountObj.getPhone());

                } else {
                    //createAndShowDialog(exception, "Error");
                }
            }
        });
    }

    private void setControlValidator(){

        txtPName.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Validation.hasText(txtPName);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        txtPMobilePhone.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Validation.hasText(txtPMobilePhone);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        txtPEmail.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Validation.isEmailAddress(txtPEmail, true);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

    }

    private void submitForm() {
        // update the main content by replacing fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, AirportDetailsFragment.newInstance(2, getBooking()))
                .addToBackStack("AirportDetailsFragment")
                .commit();
    }

    private boolean checkValidation() {
        boolean ret = true;

        if(!chkLeadPassenger.isChecked()) {
            if (!Validation.hasText(txtPName)) ret = false;
            if (!Validation.hasText(txtPMobilePhone)) ret = false;
            if (!Validation.isEmailAddress(txtPEmail, true)) ret = false;
        }


        return ret;
    }

    private Personal getLeadPassengerDetail(){
        Personal leadPassengerDetail = new Personal(randomUUID().toString(),
                txtPName.getText().toString(),
                txtPEmail.getText().toString(),
                txtPMobilePhone.getText().toString());

        return leadPassengerDetail;
    }

    private Booking getBooking(){
        Transfer transfer = (Transfer) getArguments().getSerializable(ARG_TRANSFER_OBJECT);

        Personal leadPassenger;
        if(chkLeadPassenger.isChecked())
            leadPassenger = new Personal(randomUUID().toString(), mAccountObj.getName(), mAccountObj.getEmail(), mAccountObj.getPhone());
        else
            leadPassenger = getLeadPassengerDetail();

        Booking booking = new Booking();
        booking.setBookingDate(new Date());
        ArrayList<Transfer> transfers = new ArrayList<Transfer>();

        transfers.add(transfer);
        booking.setTransfers(transfers);
        booking.setBookBy(mAccountObj);
        booking.setLeadPassenger(leadPassenger);

        return booking;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }
}
