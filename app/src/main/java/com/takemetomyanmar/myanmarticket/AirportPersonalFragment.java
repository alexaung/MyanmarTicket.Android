package com.takemetomyanmar.myanmarticket;

import android.app.Activity;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.takemetomyanmar.myanmarticket.adapter.KeyValueArrayAdapter;
import com.takemetomyanmar.myanmarticket.model.AirportTransfer.Transfer;

/**
 * Created by AMO on 2/6/2015.
 */
public class AirportPersonalFragment extends Fragment {
    public Spinner spiTitle;
    public EditText txtFirstName;
    public EditText txtLastName;
    public EditText txtMobilePhone;
    public EditText txtEmail;
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
        args.putParcelable(ARG_TRANSFER_OBJECT, transfer);
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

        spiTitle = (Spinner) rootView.findViewById(R.id.spiTitle);
        txtFirstName = (EditText) rootView.findViewById(R.id.txtFirstName);
        txtFirstName = (EditText) rootView.findViewById(R.id.txtLastName);
        txtMobilePhone = (EditText) rootView.findViewById(R.id.txtMobilePhone);
        txtEmail = (EditText) rootView.findViewById(R.id.txtEmail);

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

                //Ln.d("Entry=" + adapter.getEntry(position));
                //Ln.d("EntryValue=" + adapter.getEntryValue(position));
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
            }

        });

        final Button btnNext = (Button) rootView.findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

                // update the main content by replacing fragments
                FragmentManager fragmentManager = getFragmentManager();
                Bundle bundle = new Bundle();
                Transfer transfer = new Transfer();
                bundle.putParcelable("Transfer", transfer);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, AirportDetailsFragment.newInstance(2, transfer))
                        .addToBackStack("AirportDetailsFragment")
                        .commit();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }
}
