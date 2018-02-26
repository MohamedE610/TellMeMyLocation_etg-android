package com.example.mohamedfawzy.etg.LocationUtils;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;

import java.util.List;
import java.util.Locale;

/**
 * Created by E610 on 22/01/2017.
 */
public class LocationOperations implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient googleApiClient;

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    private Place currentPlace;
    private LocationResponse locationResponse;
    Context context;

    public LocationOperations(FragmentActivity activity) {

        connectionSetUp(activity);
        context = activity.getApplicationContext();

    }

    public void setOnLocationResponse(LocationResponse locationResponse) {

        this.locationResponse = locationResponse;
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    /********************    using enableAutoManage() in fragment   ************************/
/********************  stackoverflow.com/questions/30622906/using-enableautomanage-in-fragment  ************************/

    private void connectionSetUp(FragmentActivity activity) {
        googleApiClient = new GoogleApiClient.Builder(activity).addApi(Places.PLACE_DETECTION_API)
                .addApi(Places.GEO_DATA_API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).enableAutoManage(activity, this).build();
    }

    public void getCurrentPlace() {

        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(googleApiClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(@NonNull PlaceLikelihoodBuffer placeLikelihoods) {
                try {


                    if (placeLikelihoods.getStatus().isSuccess() && placeLikelihoods.getCount() > 0) {
                        currentPlace = placeLikelihoods.get(1).getPlace();
                        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(currentPlace.getLatLng().latitude, currentPlace.getLatLng().longitude, 1);
                        if (addresses.size() > 0) {
                            String area = addresses.get(0).getLocality();
                        }
                        locationResponse.onLoctionDetected(currentPlace);
                        //endConnection();
                    }
                } catch (Exception e) {}
            }
        });
    }

}
