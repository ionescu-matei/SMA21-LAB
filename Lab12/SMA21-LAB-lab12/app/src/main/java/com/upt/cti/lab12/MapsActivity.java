package com.upt.cti.lab12;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.ui.IconGenerator;
import com.upt.cti.lab12.databinding.ActivityMapsBinding;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private final int REQ_PERMISSION = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        if (checkPermission())
            mMap.setMyLocationEnabled(true);
        else askPermission();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng facultate = new LatLng(45.747016, 21.227229);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(facultate));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));

        IconGenerator iconGenerator = new IconGenerator(this);
        iconGenerator.setColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
        iconGenerator.setTextAppearance(R.style.customTextStyle);
        mMap.addMarker(new MarkerOptions()
                .position(facultate)
                .icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon("Facultate"))));

        LatLng gara = new LatLng(45.750632, 21.207753);
        mMap.addMarker(new MarkerOptions()
                .position(gara)
                .icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon("Gara"))));
        List<LatLng> latLngs = new ArrayList<LatLng>();
        latLngs.add(facultate);
        latLngs.add(gara);
        drawPolyLineOnMap(latLngs, mMap);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getPosition().equals(facultate)) {
                    Toast.makeText(MapsActivity.this, "I'm studying",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MapsActivity.this, "Not 'facultate' marker",
                            Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });

        SphericalUtil.computeDistanceBetween(facultate, gara);
    }

    public void drawPolyLineOnMap(List<LatLng> list, GoogleMap googleMap) {
        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(android.R.color.holo_red_dark);
        polyOptions.width(8);
        polyOptions.addAll(list);
        googleMap.addPolyline(polyOptions);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : list) {
            builder.include(latLng);
        }
        builder.build();
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(
                this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPermission())
                        mMap.setMyLocationEnabled(true);
                } else {
                    // Permission denied
                }
                break;
            }
        }
    }
}