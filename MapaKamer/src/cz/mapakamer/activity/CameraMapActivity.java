package cz.mapakamer.activity;


import java.util.List;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import cz.mapakamer.R;
import cz.mapakamer.overlay.ItemizedOverlay;

public class CameraMapActivity extends Activity {
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	super.onCreate(savedInstanceState);

    	MapView mapView = new MapView(this, 256);
    	List<Overlay> mapOverlays = mapView.getOverlays();
    	
        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(true);
 
        mapView.getController().setZoom(10);
        mapView.getController().setCenter(new GeoPoint(49.0, 14.5));
        
        setContentView(mapView);
        
        
        GeoPoint point = new GeoPoint(49.0, 14.5);
        GeoPoint point2 = new GeoPoint(49.1, 14.4);
        
	    addMarkerToOverlay(point, "s", "plakat", mapView, mapOverlays);
	    addMarkerToOverlay(point2, "hh", "cctv skodi", mapView, mapOverlays);
    }
    
    public void addMarkerToOverlay(GeoPoint point, String title, String content, MapView mapView, List<Overlay> mapOverlays)
    {
        Drawable drawable = this.getResources().getDrawable(R.drawable.cctv);
        ItemizedOverlay itemizedoverlay = new ItemizedOverlay(drawable, this);
        
    	OverlayItem overlayitem = new OverlayItem(title, content, point);
	    itemizedoverlay.addItem(overlayitem);
	    mapView.getOverlays().add(itemizedoverlay.getOverlay());
    }

}
