package com.takemetomyanmar.myanmarticket;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;
import com.takemetomyanmar.myanmarticket.adapter.CarAdapter;
import com.takemetomyanmar.myanmarticket.model.AirportTransfer.Car;
import com.takemetomyanmar.myanmarticket.model.AirportTransfer.Transfer;

import java.net.MalformedURLException;
import java.util.List;

/**
 * Created by AlexAung on 2/4/2015.
 */
public class AirportVehicleFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private static final String ARG_TRANSFER_OBJECT = "Transfer";

    private static final String FRAGMENT_TITLE = "Airport Transfer";
    /**
     * Mobile Service URL.
     */
    private static String SERVICE_URL = "";
    /**
     * Mobile Service URL.
     */
    private static String SERVICE_KEY = "";
    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    /**
     * Mobile Service Client reference
     */
    private MobileServiceClient mClient;
    /**
     * Mobile Service Table used to access data
     */
    private MobileServiceTable<Car> mCarTable;
    /**
     * Adapter to sync the items list with the view
     */
    private CarAdapter mAdapter;
    /**
     * Progress spinner to use for table operations
     */
    private ProgressBar mProgressBar;
    private LinearLayout mVehicleListView;
    private ListView listViewCar;

    public AirportVehicleFragment() {
    }

    public static AirportVehicleFragment newInstance(int sectionNumber, Transfer transfer) {
        AirportVehicleFragment fragment = new AirportVehicleFragment();
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

        SERVICE_URL = getString(R.string.mobile_service_url);
        SERVICE_KEY = getString(R.string.mobile_service_key);

        mVehicleListView = (LinearLayout) inflater.inflate(
                R.layout.fragment_airportvehicle, container, false);

        mProgressBar = (ProgressBar) mVehicleListView.findViewById(R.id.loadingProgressBar);

        // Initialize the progress bar
        mProgressBar.setVisibility(ProgressBar.GONE);

        try {
            // Create the Mobile Service Client instance, using the provided
            // Mobile Service URL and key
            mClient = new MobileServiceClient(SERVICE_URL,
                    SERVICE_KEY,
                    getActivity()).withFilter(new ProgressFilter());

            // Get the Mobile Service Table instance to use
            mCarTable = mClient.getTable(Car.class);

            // Create an adapter to bind the items with the view
            mAdapter = new CarAdapter(getActivity(), R.layout.vehicle_list_item);
            listViewCar = (ListView) mVehicleListView.findViewById(R.id.vehicle_listView);
            listViewCar.setAdapter(mAdapter);

            // Load the items from the Mobile Service
            refreshItemsFromTable();

        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        }


        listViewCar.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final Car car = (Car) parent.getItemAtPosition(position);

                // Perform action on click
                Transfer transfer = (Transfer) getArguments().getSerializable(ARG_TRANSFER_OBJECT);
                transfer.setRate(car.getRates());
                transfer.setCar(car);
                transfer.setCar_Id(car.getId());
                // update the main content by replacing fragments
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, AirportPersonalFragment.newInstance(2, transfer))
                        .addToBackStack("AirportPersonalFragment")
                        .commit();
            }

        });

        return mVehicleListView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    /**
     * Refresh the list with the items in the Mobile Service Table
     */
    private void refreshItemsFromTable() {

        // Get the items that weren't marked as completed and add them in the
        // adapter
        mCarTable.where().field("AirportCode").eq("YGN").execute(new TableQueryCallback<Car>() {

            public void onCompleted(List<Car> result, int count, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    mAdapter.clear();

                    for (Car item : result) {
                        mAdapter.add(item);
                    }

                } else {
                    createAndShowDialog(exception, "Error");
                }
            }
        });
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

    private class ProgressFilter implements ServiceFilter {

        @Override
        public void handleRequest(ServiceFilterRequest request, NextServiceFilterCallback nextServiceFilterCallback,
                                  final ServiceFilterResponseCallback responseCallback) {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.VISIBLE);
                }
            });

            nextServiceFilterCallback.onNext(request, new ServiceFilterResponseCallback() {

                @Override
                public void onResponse(ServiceFilterResponse response, Exception exception) {
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.GONE);
                        }
                    });

                    if (responseCallback != null)  responseCallback.onResponse(response, exception);
                }
            });
        }
    }
}
