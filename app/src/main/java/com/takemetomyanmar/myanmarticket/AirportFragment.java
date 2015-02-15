package com.takemetomyanmar.myanmarticket;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.takemetomyanmar.myanmarticket.Authentication.AuthenticationApplication;
import com.takemetomyanmar.myanmarticket.Authentication.CustomLoginActivity;
import com.takemetomyanmar.myanmarticket.Util.Validation;
import com.takemetomyanmar.myanmarticket.adapter.KeyValueArrayAdapter;
import com.takemetomyanmar.myanmarticket.model.AirportTransfer.Transfer;
import com.takemetomyanmar.myanmarticket.Authentication.AuthService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by AlexAung on 2/1/2015.
 */
public class AirportFragment extends Fragment {

    public Calendar myCalendar;
    public Spinner airport;
    public EditText destination;
    public EditText txtFlightNo;
    public EditText pickup_date;
    public EditText pickup_time;
    public EditText no_of_passengers;
    public EditText no_of_luggage;
    public DatePickerDialog.OnDateSetListener dateSetListener;
    public TimePickerDialog.OnTimeSetListener timeSetListener;

    public String airportCode;

    public AirportFragment(){}
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static AirportFragment newInstance(int sectionNumber) {
        AirportFragment fragment = new AirportFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_airport, container, false);

        registerViews(rootView);

        setControlValidator();

        return rootView;
    }

    private void registerViews(View rootView) {

        airport = (Spinner) rootView.findViewById(R.id.airport);
        destination = (EditText) rootView.findViewById(R.id.destination);
        txtFlightNo = (EditText) rootView.findViewById(R.id.flightNo);

        pickup_date = (EditText) rootView.findViewById(R.id.pickup_date);
        pickup_time = (EditText) rootView.findViewById(R.id.pickup_time);
        no_of_passengers = (EditText) rootView.findViewById(R.id.no_of_passengers);
        no_of_luggage = (EditText) rootView.findViewById(R.id.no_of_luggage);

//        // initialize all your visual fields
//        if (savedInstanceState != null) {
//            airport.setSelection(savedInstanceState.getInt("airport", 0));
//            destination.setText(savedInstanceState.getString("destination"));
//            pickup_date.setText(savedInstanceState.getString("pickup_date"));
//            pickup_time.setText(savedInstanceState.getString("pickup_time"));
//            no_of_passengers.setText(savedInstanceState.getString("no_of_passengers"));
//            no_of_luggage.setText(savedInstanceState.getString("no_of_luggage"));
//
//            // do this for each of your text views
//        }

        KeyValueArrayAdapter adapter = new KeyValueArrayAdapter(getActivity(),android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.setKeyValue(getResources().getStringArray(R.array.airport_codes),
                getResources().getStringArray(R.array.airport_names));

        airport.setAdapter(adapter);

        airport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view,
                                       final int position, final long id) {

                KeyValueArrayAdapter adapter = (KeyValueArrayAdapter) parent.getAdapter();
                airportCode = adapter.getEntryValue(position);
                //Ln.d("Entry=" + adapter.getEntry(position));
                //Ln.d("EntryValue=" + adapter.getEntryValue(position));
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
            }

        });

        myCalendar = Calendar.getInstance();

        pickup_date.setInputType(InputType.TYPE_NULL);
        pickup_date.setFocusable(false);

        dateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateLabel();
            }

        };

        pickup_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getActivity(), dateSetListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        pickup_time.setInputType(InputType.TYPE_NULL);
        pickup_time.setFocusable(false);

        timeSetListener = new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalendar.set(Calendar.MINUTE, minute);
                updateTimeLabel();
            }

        };

        pickup_time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new TimePickerDialog(getActivity(), timeSetListener,
                        myCalendar.get(Calendar.HOUR_OF_DAY),
                        myCalendar.get(Calendar.MINUTE), true).show();
            }
        });


        final Button btnNext = (Button) rootView.findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                /*
                Validation class will check the error and display the error on respective fields
                but it won't resist the form submission, so we need to check again before submit
                 */
                if ( checkValidation () )
                    submitForm();
                else
                    Toast.makeText(getActivity(), "Form contains error", Toast.LENGTH_LONG).show();


            }
        });


    }

    private void setControlValidator(){
        // TextWatcher would let us check validation error on the fly
        destination.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Validation.hasText(destination);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        txtFlightNo.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Validation.hasText(txtFlightNo);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        pickup_date.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Validation.hasText(pickup_date);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        pickup_time.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Validation.hasText(pickup_time);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        no_of_passengers.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Validation.hasText(no_of_passengers);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        no_of_luggage.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Validation.hasText(no_of_luggage);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });
    }

    private void submitForm() {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        Transfer transfer = getTransfer();
        fragmentManager.beginTransaction()
                .replace(R.id.container, AirportVehicleFragment.newInstance(2, transfer))
                .addToBackStack("AirportVehicleFragment")
                .commit();
    }

    private boolean checkValidation() {
        boolean ret = true;

        if (!Validation.hasText(destination)) ret = false;
        if (!Validation.hasText(txtFlightNo)) ret = false;
        if (!Validation.hasText(pickup_date)) ret = false;
        if (!Validation.hasText(pickup_time)) ret = false;
        if (!Validation.hasText(no_of_passengers)) ret = false;
        if (!Validation.hasText(no_of_luggage)) ret = false;


        return ret;
    }

    private Transfer getTransfer(){

        String from = airportCode;
        String to = destination.getText().toString();
        String flightNo = txtFlightNo.getText().toString();
        Calendar cal = Calendar.getInstance();

        String dateString = pickup_date.getText() + " " + pickup_time.getText();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int noOfPassenger = Integer.parseInt( no_of_passengers.getText().toString() );
        int noOfLuggage = Integer.parseInt(no_of_luggage.getText().toString());

        Transfer transfer = new Transfer(java.util.UUID.randomUUID().toString(), "Arrival", null, flightNo, from, to, noOfPassenger, noOfLuggage, convertedDate, null);
        return transfer;
    }

    private void updateDateLabel() {

        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        pickup_date.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateTimeLabel() {

        String myFormat = "HH:mm"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        pickup_time.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        super.onSaveInstanceState(savedInstanceState);
//        savedInstanceState.putInt("airport", airport.getSelectedItemPosition());
//        savedInstanceState.putString("destination", destination.getText().toString());
//        savedInstanceState.putString("pickup_date", pickup_date.getText().toString());
//        savedInstanceState.putString("pickup_time", pickup_time.getText().toString());
//        savedInstanceState.putString("no_of_passengers", no_of_passengers.getText().toString());
//        savedInstanceState.putString("no_of_luggage", no_of_luggage.getText().toString());
//    }
}
