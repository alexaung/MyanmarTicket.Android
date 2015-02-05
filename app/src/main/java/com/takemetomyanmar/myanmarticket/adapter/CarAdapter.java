package com.takemetomyanmar.myanmarticket.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.takemetomyanmar.myanmarticket.R;
import com.takemetomyanmar.myanmarticket.model.AirportTransfer.Car;

import java.util.ArrayList;

/**
 * Created by AlexAung on 2/4/2015.
 */
public class CarAdapter extends ArrayAdapter<Car> {
    /**
     * Adapter context
     */
    Context mContext;
    /**
     * Adapter View layout
     */
    int mLayoutResourceId;

    public CarAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);

        mContext = context;
        mLayoutResourceId = layoutResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //View row = convertView;

        final Car currentItem = getItem(position);

//        if (row == null) {
//            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
//            row = inflater.inflate(mLayoutResourceId, parent, false);
//        }
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(mLayoutResourceId, null);
        }

        //row.setTag(currentItem);

        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.list_image);
        TextView txtName = (TextView) convertView.findViewById(R.id.name);
        TextView txtDescription = (TextView) convertView.findViewById(R.id.description);
        TextView txtRates = (TextView) convertView.findViewById(R.id.rates);
        TextView txtSeatingCapacity = (TextView) convertView.findViewById(R.id.seating_capacity);
        TextView txtNoOfLuggage = (TextView) convertView.findViewById(R.id.no_of_luggage);

        int imageId = mContext.getResources().getIdentifier(currentItem.getImage(), "drawable", mContext.getPackageName());

        imgIcon.setImageResource(imageId);
        txtName.setText(currentItem.getName());
        txtDescription.setText(currentItem.getDescription());
        txtRates.setText(currentItem.getRates() + " USD");
        txtSeatingCapacity.setText(String.valueOf(currentItem.getSeatingCapacity()));
        txtNoOfLuggage.setText(String.valueOf(currentItem.getLuggage()));

        return convertView;
    }
}
