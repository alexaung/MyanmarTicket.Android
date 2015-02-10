package com.takemetomyanmar.myanmarticket;

import android.app.Activity;
import android.net.Uri;
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
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.takemetomyanmar.myanmarticket.Util.Validation;
import com.takemetomyanmar.myanmarticket.adapter.KeyValueArrayAdapter;
import com.takemetomyanmar.myanmarticket.model.AirportTransfer.Booking;
import com.takemetomyanmar.myanmarticket.model.AirportTransfer.Personal;
import com.takemetomyanmar.myanmarticket.model.AirportTransfer.Transfer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import static java.util.UUID.randomUUID;

/**
 * Created by AMO on 2/6/2015.
 */
public class AirportPersonalFragment extends Fragment {

    public LinearLayout leadPassengerGroup;
    public Spinner spiTitle;
    public EditText txtFirstName;
    public EditText txtLastName;
    public EditText txtMobilePhone;
    public EditText txtEmail;

    CheckBox chkLeadPassenger;
    public Spinner spiPTitle;
    public EditText txtPFirstName;
    public EditText txtPLastName;
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

        spiTitle = (Spinner) rootView.findViewById(R.id.spiTitle);
        txtFirstName = (EditText) rootView.findViewById(R.id.txtFirstName);
        txtLastName = (EditText) rootView.findViewById(R.id.txtLastName);
        txtMobilePhone = (EditText) rootView.findViewById(R.id.txtMobilePhone);
        txtEmail = (EditText) rootView.findViewById(R.id.txtEmail);

        spiPTitle = (Spinner) rootView.findViewById(R.id.spiPTitle);
        txtPFirstName = (EditText) rootView.findViewById(R.id.txtPFirstName);
        txtPLastName = (EditText) rootView.findViewById(R.id.txtPLastName);
        txtPMobilePhone = (EditText) rootView.findViewById(R.id.txtPMobilePhone);
        txtPEmail = (EditText) rootView.findViewById(R.id.txtPEmail);


        KeyValueArrayAdapter adapter = new KeyValueArrayAdapter(getActivity(),android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.setKeyValue(getResources().getStringArray(R.array.title_codes),
                getResources().getStringArray(R.array.title_names));

        spiTitle.setAdapter(adapter);

        spiTitle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view,
                                       final int position, final long id) {

                KeyValueArrayAdapter adapter = (KeyValueArrayAdapter) parent.getAdapter();

                accountTitleCode = adapter.getEntryValue(position);

                //Ln.d("Entry=" + adapter.getEntry(position));
                //Ln.d("EntryValue=" + adapter.getEntryValue(position));
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
            }

        });

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

    private void setControlValidator(){
        // TextWatcher would let us check validation error on the fly
        txtFirstName.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Validation.hasText(txtFirstName);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        txtLastName.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Validation.hasText(txtLastName);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        txtMobilePhone.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Validation.hasText(txtMobilePhone);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        txtEmail.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Validation.isEmailAddress(txtPEmail, true);

            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        txtPFirstName.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Validation.hasText(txtPFirstName);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        txtPLastName.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Validation.hasText(txtPLastName);
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

        if (!Validation.hasText(txtFirstName)) ret = false;
        if (!Validation.hasText(txtLastName)) ret = false;
        if (!Validation.hasText(txtMobilePhone)) ret = false;
        if (!Validation.isEmailAddress(txtEmail, true)) ret = false;

        if(!chkLeadPassenger.isChecked()) {
            if (!Validation.hasText(txtPFirstName)) ret = false;
            if (!Validation.hasText(txtPLastName)) ret = false;
            if (!Validation.hasText(txtPMobilePhone)) ret = false;
            if (!Validation.isEmailAddress(txtPEmail, true)) ret = false;
        }


        return ret;
    }

    private Personal getAccountDetail(){
        Personal accountDetail = new Personal(randomUUID().toString(), accountTitleCode,
                txtFirstName.getText().toString(),
                txtLastName.getText().toString(),
                txtEmail.getText().toString(),
                txtMobilePhone.getText().toString());

        return accountDetail;
    }

    private Personal getLeadPassengerDetail(){
        Personal leadPassengerDetail = new Personal(randomUUID().toString(), leadTitleCode,
                txtPFirstName.getText().toString(),
                txtPLastName.getText().toString(),
                txtPEmail.getText().toString(),
                txtPMobilePhone.getText().toString());

        return leadPassengerDetail;
    }

    private Booking getBooking(){
        Transfer transfer = (Transfer) getArguments().getSerializable(ARG_TRANSFER_OBJECT);

        Personal account = getAccountDetail();
        Personal leadPassenger;
        if(chkLeadPassenger.isChecked())
            leadPassenger = account;
        else
            leadPassenger = getLeadPassengerDetail();

        Booking booking = new Booking();
        booking.setBookingDate(new Date());
        ArrayList<Transfer> transfers = new ArrayList<Transfer>();

        transfers.add(transfer);
        booking.setTransfers(transfers);
        booking.setBookBy(account);
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
