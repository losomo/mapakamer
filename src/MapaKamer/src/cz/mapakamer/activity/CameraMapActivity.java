package cz.mapakamer.activity;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
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
 
        mapView.getController().setZoom(10);
        mapView.getController().setCenter(new GeoPoint(50.0, 14.5));
        LoadCameras lc = new LoadCameras();
		lc.execute();
		ArrayList<Camera> cameras=new ArrayList<Camera>();
		try {
			cameras = lc.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		initLocation(mapView);
        setContentView(mapView);
        addMarkerToOverlay(cameras, mapView, mapOverlays);}
    	/*
        //TODO: create method getCamerasFromDB() which will put all cameras into List
        
        GeoPoint point = new GeoPoint(50.1042572, 14.3887436);
        GeoPoint point2 = new GeoPoint(50.0719661, 14.4090608);
        GeoPoint point3 = new GeoPoint(50.0802069, 14.3946717);
        
        Camera camera1 = new Camera();
        camera1.setCoordinates(point);
        camera1.setDescription("krásná malá kamerka");
        camera1.setImage(this.getResources().getDrawable(R.drawable.images));
        
        Camera camera2 = new Camera();
        camera2.setCoordinates(point2);
        camera2.setDescription("větší a ne tak hezká kamerka");
        camera2.setImage(this.getResources().getDrawable(R.drawable.cctv_tree));

        Camera camera3 = new Camera();
        camera3.setCoordinates(point3);
        camera3.setDescription("ještě jedna kamerka");
        camera3.setImage(this.getResources().getDrawable(R.drawable.cctv_sign));
        
        //ArrayList<Camera> cameras = new ArrayList<Camera>();
        //cameras.add(camera1);
        //cameras.add(camera2);
        //cameras.add(camera3);
        
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
    
    public void addMarkerToOverlay(GeoPoint point, String title, String content, Drawable image, MapView mapView, List<Overlay> mapOverlays)
    {
        Drawable drawable = this.getResources().getDrawable(R.drawable.cctv);
        ItemizedOverlay itemizedOverlay = new ItemizedOverlay(drawable, this);
        
    	ImageOverlayItem overlayItem = new ImageOverlayItem(title, content, point, image);
	    itemizedOverlay.addItem(overlayItem);
	    mapView.getOverlays().add(itemizedOverlay.getOverlay());
    }
    
    public void addMarkerToOverlay(ArrayList<Camera> cameras, MapView mapView, List<Overlay> mapOverlays)
    {
    	Drawable drawable = this.getResources().getDrawable(R.drawable.cctv);
    	ItemizedOverlay itemizedOverlay = new ItemizedOverlay(drawable, this);
    	
    	for (Camera camera : cameras)
    	{
    		String content = camera.getDescription();
        	String path= camera.getAddress();
    		Drawable image = camera.getImage();
    		GeoPoint point = camera.getCoordinates();
    		GeoPoint point2 = new GeoPoint(camera.getLongitude(), camera.getLatitude());

    		ImageOverlayItem overlayItem = new ImageOverlayItem(path, content, point, image);
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
	
	/*private ArrayList<Camera> allCameras(String kamery)
	{
		ArrayList<Camera> cameras = new ArrayList<Camera>();
		
		String[] kamera = kamery.split(";");
		String[] castKamery=null;
		for(int i=0;i<kamera.length;i++)
		{
			castKamery=kamera[i].split("::");
			Camera cam=new Camera();
			//cam.setId(Integer.parseInt(castKamery[0]));
			cam.setCoordinates(new GeoPoint(Double.parseDouble(castKamery[2]),Double.parseDouble(castKamery[1])));
			//cam.setDescription(castKamery[1]);
			cam.setAddress(castKamery[3]);
			cameras.add(cam);
		}
		return cameras;
	}*/
	
	private class LoadCameras extends AsyncTask<Void,Void,ArrayList<Camera>>{
			@Override
			protected ArrayList<Camera> doInBackground(Void...q) {
				// TODO Auto-generated method stub
				InputStream inputStream=null;
				try {
					
					HttpClient httpclient = new DefaultHttpClient();
					//HttpPost hp=new HttpPost("http://10.0.2.2:8080/Pin2_b13/GetFromDB");
					HttpPost hp=new HttpPost("http://geo102.fsv.cvut.cz:8080/Pin213/GetFromDB");
			        HttpResponse res = httpclient.execute(hp);
			        inputStream = res.getEntity().getContent();

		            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

		            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		            StringBuilder stringBuilder = new StringBuilder();

		            String bufferedStrChunk = null;
		            //EditText et = (EditText) findViewById(R.id.editText1);
		            while((bufferedStrChunk = bufferedReader.readLine()) != null){
		                stringBuilder.append(bufferedStrChunk);
		            }
		            //this.mujText=stringBuilder.toString();
		            String kamery= stringBuilder.toString();
		            ArrayList<Camera> cameras = new ArrayList<Camera>();
		    		
		    		String[] kamera = kamery.split(";");
		    		String[] castKamery=null;
		    		for(int i=0;i<kamera.length;i++)
		    		{
		    			castKamery=kamera[i].split("::");
		    			Camera cam=new Camera();
		    			//cam.setId(Integer.parseInt(castKamery[0]));
		    			cam.setCoordinates(new GeoPoint(Double.parseDouble(castKamery[2]),Double.parseDouble(castKamery[1])));
		    			//cam.setDescription(castKamery[1]);
		    			cam.setAddress(castKamery[3]);
		    			cameras.add(cam);
		    		}
		    		return cameras;
			    } catch (ClientProtocolException ey) {
			        System.out.println("Chyba1");
			        ey.printStackTrace();
			        System.exit(1);
			    }	catch (IOException ex) {
			    	System.out.println("Chyba2");
			    	ex.printStackTrace();
			        System.exit(1);
			    }
				finally{
					if(inputStream!=null)
						try {
							inputStream.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
				
				
				return null;
			}
		}
	
}
