package cz.mapakamer.activity;


import java.util.ArrayList;
import java.util.List;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import cz.mapakamer.R;
import cz.mapakamer.entity.Camera;
import cz.mapakamer.overlay.ImageOverlayItem;
import cz.mapakamer.overlay.ItemizedOverlay;
import cz.mapakamer.utils.GPSUtility;

public class CameraMapActivity extends Activity {
	
	private Location gpsLocation;
	private Location networkLocation;
	private LocationListener gpsLocListener;
	private LocationListener networkLocListener;
	private boolean gpsDialogAnswered;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	super.onCreate(savedInstanceState);

    	MapView mapView = new MapView(this, 256);
    	List<Overlay> mapOverlays = mapView.getOverlays();

        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(true);
 
        mapView.getController().setZoom(16);
        
        initLocation(mapView);
        setContentView(mapView);
                
        //TODO: create method getCamerasFromDB() which will put all cameras into List
        
        GeoPoint point = new GeoPoint(49.0, 14.5);
        GeoPoint point2 = new GeoPoint(49.1, 14.4);
        
        Camera camera1 = new Camera();
        camera1.setCoordinates(point);
        camera1.setDescription("krásná malá kamerka");
        
        Camera camera2 = new Camera();
        camera2.setCoordinates(point2);
        camera2.setDescription("větší a ne tak hezká kamerka");

        ArrayList<Camera> cameras = new ArrayList<Camera>();
        cameras.add(camera1);
        cameras.add(camera2);
        
        addMarkerToOverlay(cameras, mapView, mapOverlays);
    }
    
    
    //TODO: think about it. maybe it should be nice to have some small button retrieving current position with this method
	/*@Override
	public void onResume() {
		super.onResume();
		MapView mapView = new MapView(this, 256);
    	List<Overlay> mapOverlays = mapView.getOverlays();

        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(true);
 
        initLocation(mapView);
        setContentView(mapView);
                
        //TO DO: create method getCamerasFromDB() which will put all cameras into List
        
        GeoPoint point = new GeoPoint(49.0, 14.5);
        GeoPoint point2 = new GeoPoint(49.1, 14.4);
        
        Camera camera1 = new Camera();
        camera1.setCoordinates(point);
        camera1.setDescription("krásná malá kamerka");
        
        Camera camera2 = new Camera();
        camera2.setCoordinates(point2);
        camera2.setDescription("větší a ne tak hezká kamerka");

        ArrayList<Camera> cameras = new ArrayList<Camera>();
        cameras.add(camera1);
        cameras.add(camera2);
        
        addMarkerToOverlay(cameras, mapView, mapOverlays);
        //TO DO: override addMarkerOverlay(Point p, String t, String c, MapView mv, List<Overlay> mo) to addMarkerOverlay(List<List>, MapView mv, List<Overlay> mo) 
	    //addMarkerToOverlay(point, "s", "plakat", mapView, mapOverlays);
	    //addMarkerToOverlay(point2, "hh", "cctv skodi", mapView, mapOverlays);
	
	}*/
    
    public void addMarkerToOverlay(GeoPoint point, String title, String content, MapView mapView, List<Overlay> mapOverlays)
    {
        Drawable drawable = this.getResources().getDrawable(R.drawable.cctv);
        Drawable image = this.getResources().getDrawable(R.drawable.images);
        ItemizedOverlay itemizedOverlay = new ItemizedOverlay(drawable, this);
        
    	ImageOverlayItem overlayItem = new ImageOverlayItem(title, content, point, image);
	    itemizedOverlay.addItem(overlayItem);
	    mapView.getOverlays().add(itemizedOverlay.getOverlay());
    }
    
    public void addMarkerToOverlay(ArrayList<Camera> cameras, MapView mapView, List<Overlay> mapOverlays)
    {
    	Drawable drawable = this.getResources().getDrawable(R.drawable.cctv);
    	Drawable image = this.getResources().getDrawable(R.drawable.images);
    	ItemizedOverlay itemizedOverlay = new ItemizedOverlay(drawable, this);
    	
    	for (Camera camera : cameras)
    	{
    		double longitude = camera.getLongitude();
    		double latitude = camera.getLatitude();
    		String content = camera.getDescription();
    		
    		GeoPoint point = camera.getCoordinates();
    		GeoPoint point2 = new GeoPoint(longitude, latitude);

    		ImageOverlayItem overlayItem = new ImageOverlayItem(" ", content, point, image);
    		itemizedOverlay.addItem(overlayItem);
    		mapView.getOverlays().add(itemizedOverlay.getOverlay());
    	}
    	
    }

	private void initLocation(final MapView mapView) {
		gpsLocListener = new LocationListener() {
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
				if (!gpsDialogAnswered) {
					GPSUtility.checkGPS(CameraMapActivity.this);
					gpsDialogAnswered = true;
				}
			}

			public void onLocationChanged(Location location) {
				gpsLocation = location;
				updateLocation(mapView);
				setProgressBarIndeterminateVisibility(Boolean.FALSE);
			}
		};

		networkLocListener = new LocationListener() {
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}

			public void onLocationChanged(Location location) {
				networkLocation = location;
				updateLocation(mapView);
			}
		};

		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				gpsLocListener);
		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
				networkLocListener);

		setProgressBarIndeterminateVisibility(Boolean.TRUE);
	}
	
	public void updateLocation(MapView mapView) {
		Location location = gpsLocation;
		if (gpsLocation == null) {
			location = networkLocation;
		}

		GeoPoint center = new GeoPoint(location.getLatitude(), location.getLongitude());
		mapView.getController().setCenter(center);
		mapView.getController().setZoom(16);
	}
	
}
