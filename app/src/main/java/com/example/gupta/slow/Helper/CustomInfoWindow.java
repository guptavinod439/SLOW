package com.example.gupta.slow.Helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import  com.example.gupta.slow.R;

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {

    View myView;

    public CustomInfoWindow(Context context) {
        myView = LayoutInflater.from(context)
                .inflate(R.layout.custom_rider_info_window,null);
    }




    @Override
    public View getInfoWindow(Marker marker) {

        TextView txtPickupTitle = ((TextView)myView.findViewById(R.id.textPickupInfo));
        txtPickupTitle.setText(marker.getTitle());

        TextView txtPickupSnippet = ((TextView)myView.findViewById(R.id.textPickupSnippet));
        txtPickupSnippet.setText(marker.getSnippet());

        return myView;

    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
