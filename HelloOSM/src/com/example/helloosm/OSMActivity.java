package com.example.helloosm;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import android.app.Activity;
import android.os.Bundle;



public class OSMActivity extends Activity {

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	super.onCreate(savedInstanceState);

        MapView mapView = new MapView(this, 256);

        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(true);
 
        mapView.getController().setZoom(10);
        mapView.getController().setCenter(new GeoPoint(49.0, 14.5));
        
        mapView.(TileSourceFactory.CYCLEMAP);
        setContentView(mapView);
        
    }
}
