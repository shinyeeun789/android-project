package com.inhatc.mobile_project.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.inhatc.mobile_project.R;

import java.io.IOException;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double latitude, longitude;
    private Geocoder geocoder;
    private List<Address> address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", 37.56);
        longitude = intent.getDoubleExtra("longitude", 126.97);     // 기본 값은 서울의 위도 경도로

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        //LatLng SEOUL = new LatLng(37.56, 126.97);

        LatLng place = new LatLng(latitude, longitude);

        try {
            geocoder = new Geocoder(getBaseContext());
            address = geocoder.getFromLocation(latitude, longitude, 1);
        } catch(IOException e) {
            e.printStackTrace();
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(place);
        markerOptions.title(address.get(0).getCountryName());
        markerOptions.snippet(address.get(0).getAddressLine(0));

        BitmapDrawable bitmapDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_marker);
        Bitmap bitmap = bitmapDraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
        mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 16));
    }
}