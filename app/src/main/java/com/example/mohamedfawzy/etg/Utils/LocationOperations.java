package com.example.mohamedfawzy.etg.Utils;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;

/**
 * Created by E610 on 22/01/2017.
 */
public class LocationOperations extends AsyncTask<Void,Void,Void> implements GoogleApiClient.OnConnectionFailedListener{

    private GoogleApiClient googleApiClient;
    private Place currentPlace;
    private LocationResponse locationResponse;

    public LocationOperations(FragmentActivity activity  ){

        connectionSetUp(activity);
    }

    public void setOnLocationResponse(LocationResponse lr){
        locationResponse=lr;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        startConnection();
    }

    @Override
    protected Void doInBackground(Void... params) {

        getCurrentPlace();
        int i;
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);


        //locationResponse.onLoctionDetected(currentPlace);

        endConnection();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void connectionSetUp(FragmentActivity   activity){
        googleApiClient=new GoogleApiClient.Builder(activity).addApi(Places.PLACE_DETECTION_API)
                .addApi(Places.GEO_DATA_API).enableAutoManage(activity,this).build();
    }

    private void startConnection(){
        if(googleApiClient!=null)
            googleApiClient.connect();
    }

    private void endConnection(){
        if(googleApiClient!=null)
            googleApiClient.disconnect();
    }

    private void getCurrentPlace(){

        PendingResult<PlaceLikelihoodBuffer> result=Places.PlaceDetectionApi.getCurrentPlace(googleApiClient,null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(@NonNull PlaceLikelihoodBuffer placeLikelihoods) {

                currentPlace=placeLikelihoods.get(0).getPlace();
                locationResponse.onLoctionDetected(currentPlace);

            }
        });
    }
}
