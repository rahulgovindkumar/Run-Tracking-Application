package com.example.HW07_forumfirebase;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.HW07_forumfirebase.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.GeoPoint;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    POJOclasses.Route trip;

    final String TAG = "Demo";

    int LOCATION_REQUEST_CODE = 10001;

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
           if(locationResult==null){
               return;
           }
            Log.d(TAG, "onLocationResult: Before");
           for(Location location: locationResult.getLocations()){
               Log.d(TAG, "onLocationResult: "+ location.toString());
               Log.d(TAG, "onLocationResult:getLongitude "+ location.getLongitude());
               Log.d(TAG, "onLocationResult:getLatitude "+ location.getLatitude());
           }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(getIntent() != null && getIntent().getExtras() != null & getIntent().hasExtra(HistoryFragment.intentKey)) {
            trip = (POJOclasses.Route) getIntent().getSerializableExtra(HistoryFragment.intentKey);
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        double camLatmin = trip.points.get(0).getLatitude();
        double camLatmax = trip.points.get(0).getLatitude();
        double camLongmin = trip.points.get(0).getLongitude();
        double camLongmax = trip.points.get(0).getLongitude();

        for (GeoPoint p: trip.points) {
            mMap.addPolyline(new PolylineOptions()
                    .clickable(true)
                    .add(new LatLng(p.getLatitude(), p.getLongitude())));
            camLatmin = p.getLatitude() < camLatmin ? p.getLatitude() : camLatmin;
            camLongmin = p.getLongitude() < camLongmin ? p.getLongitude() : camLongmin;
            camLongmax = p.getLongitude() > camLongmax ? p.getLongitude() : camLongmax;
            camLatmax = p.getLatitude() > camLatmax ? p.getLatitude() : camLatmax;
        }



        LatLngBounds australiaBounds = new LatLngBounds(
                new LatLng(camLatmin, camLongmin), // SW bounds
                new LatLng(camLatmax, camLongmax)  // NE bounds
        );
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(australiaBounds, 0));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //getLastLocation();
            checkSettingAndStartLocationUpdates();
        } else {
            askLocationPermission();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        stopLocationUpdates();
    }

    private void checkSettingAndStartLocationUpdates(){

        LocationSettingsRequest request = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build();

        SettingsClient client = LocationServices.getSettingsClient(this);

        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);


        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                //Settings of device satisfied and start location update

                startLocationUpdates();



            }
        });

        locationSettingsResponseTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if(e instanceof ResolvableApiException){
                    ResolvableApiException apiExtension = (ResolvableApiException) e;
                    try {
                        apiExtension.startResolutionForResult(MapsActivity.this,1001);
                    } catch (IntentSender.SendIntentException sendIntentException) {
                        sendIntentException.printStackTrace();
                    }
                }

            }
        });



    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates(){

        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper());


    }

    private void stopLocationUpdates(){

        fusedLocationProviderClient.removeLocationUpdates(locationCallback);


    }


    private void getLastLocation() {


        @SuppressLint("MissingPermission") Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();

        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!= null){
                    //We have a location
                    Log.d(TAG, "onSuccess: "+location.toString());
                    Log.d(TAG, "onSuccess: "+location.getLatitude());
                    Log.d(TAG, "onSuccess: "+location.getLongitude());
                }else
                {
                    Log.d(TAG, "onSuccess: Location is NULL ");
                }

            }
        });

        locationTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: "+e.getLocalizedMessage());

            }
        });


        

    }

    private void  askLocationPermission(){

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                Log.d(TAG, "askLocationPermission: ");

                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);

            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);

            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permission Granted
               // getLastLocation();
                checkSettingAndStartLocationUpdates();
            } else {
                //Permission Not granted
            }
        }
    }
}