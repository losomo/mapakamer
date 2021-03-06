package cz.mapakamer.activity;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.osmdroid.events.DelayedMapListener;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.util.BoundingBoxE6;
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
import android.util.Log;
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
	private boolean locationInitialized = false;
	private static final String TAG = "MyActivity";
	//private boolean firstTime = true;
	private BoundingBoxE6 currentBBox = new BoundingBoxE6(0,0,0,0);

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	super.onCreate(savedInstanceState);


        final MapView mapView = new MapView(this, 256);
        final List<Overlay> mapOverlays = mapView.getOverlays();
        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(true);
 
        mapView.getController().setZoom(10);
        mapView.getController().setCenter(new GeoPoint(50.0, 14.5));
        
		if (!locationInitialized)
		{
			initLocation(mapView, mapOverlays);
		}
        setContentView(mapView);
        setCurrentBBox(mapView);
        Log.v(TAG, "BBox nastaven při spuštění");
       
        
        mapView.setMapListener(new DelayedMapListener(new MapListener() {
			
			@Override
			public boolean onZoom(final ZoomEvent e) {
				setCurrentBBox(mapView);
				Log.v(TAG, "BBox nastaven onZoom");
				processLoadCameras(mapView, mapOverlays);
				return false;
			}
			
			@Override
			public boolean onScroll(final ScrollEvent e) {
				setCurrentBBox(mapView);
				Log.v(TAG, "BBox nastaven onScroll");
				processLoadCameras(mapView, mapOverlays);
				return false;
			}
		}, 100 ));
    }
    	

    public void processLoadCameras(MapView mapView, List<Overlay> mapOverlays){
    	/* Clear overlay
    	 * 
    	 */
    	mapOverlays.clear();

    	/* Load cameras to overlay if close enough
    	 * 
    	 */
    	if (currentBBox.getLatNorthE6() != 0 && mapView.getZoomLevel() > 14)
        {
        	
        	mapOverlays.clear();

        	LoadCameras lc = new LoadCameras();
        	lc.execute();

        	ArrayList<Camera> cameras = new ArrayList<Camera>();
        	try {
        		cameras = lc.get();
        		//Log.v(TAG, cameras.toString());
        	} catch (InterruptedException e) {
        		Log.e(TAG, "Failed to fill cameras array. InterruptedException");
        		e.printStackTrace();
        	} catch (ExecutionException e) {
        		Log.e(TAG, "Failed to fill cameras array. ExecutionException");
        		e.printStackTrace();
        	}
       		addMarkerToOverlay(cameras, mapView, mapOverlays);
		}
    }
    
    public void setCurrentBBox(MapView mapView)
    {
    	//currentBBox = mapView.getProjection().getBoundingBox();
    	currentBBox = mapView.getBoundingBox();
        Log.v(TAG, currentBBox.toString());

    }
    
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
    		//GeoPoint point2 = new GeoPoint(camera.getLongitude(), camera.getLatitude());

    		ImageOverlayItem overlayItem = new ImageOverlayItem(path, content, point, image);
    		itemizedOverlay.addItem(overlayItem);
    		mapView.getOverlays().add(itemizedOverlay.getOverlay());
    	}
    	Log.v(TAG, "pocet kamer v overlayi je " + Integer.toString(mapOverlays.size()));
    	
    }

	private void initLocation(final MapView mapView, final List<Overlay> mapOverlays) {
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
				if (!locationInitialized){
					updateLocation(mapView);
					locationInitialized = true;
					setProgressBarIndeterminateVisibility(Boolean.FALSE);
				}
				/*if (!firstTime){
					processLoadCameras(mapView, mapOverlays);
				}
				firstTime = false;*/
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
				if (!locationInitialized) 
				{
					updateLocation(mapView);
					locationInitialized = true;
				}
				/*if (!firstTime){
					processLoadCameras(mapView, mapOverlays);
				}
				firstTime = false;*/
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
		setCurrentBBox(mapView);
	}
		
	private class LoadCameras extends AsyncTask<Void,Void,ArrayList<Camera>>{
			@Override
			protected ArrayList<Camera> doInBackground(Void...q) {
				// TODO Auto-generated method stub
				InputStream inputStream=null;
				try {
					HttpClient httpclient = new DefaultHttpClient();
					//HttpPost hp=new HttpPost("http://10.0.2.2:8080/mobilniMK/GetFromDB");
					//HttpPost hp=new HttpPost("http://geo102.fsv.cvut.cz:8080/Pin213/GetFromDB");
					Log.v(TAG, "ještě tu jsem");
					HttpPost hp=new HttpPost("http://www.mapakamer.cz/mobilniMK/mobilniMK/GetFromDB");
					Log.v(TAG, "a tady?");
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			        nameValuePairs.add(new BasicNameValuePair("north", Integer.toString(currentBBox.getLatNorthE6())));
			        nameValuePairs.add(new BasicNameValuePair("south", Integer.toString(currentBBox.getLatSouthE6())));
			        nameValuePairs.add(new BasicNameValuePair("east", Integer.toString(currentBBox.getLonEastE6())));
			        nameValuePairs.add(new BasicNameValuePair("west", Integer.toString(currentBBox.getLonWestE6())));
			        
			   /*     nameValuePairs.add(new BasicNameValuePair("north", Integer.toString(50078927)));
			        nameValuePairs.add(new BasicNameValuePair("south", Integer.toString(50068434)));
			        nameValuePairs.add(new BasicNameValuePair("east", Integer.toString(14413461)));
			        nameValuePairs.add(new BasicNameValuePair("west", Integer.toString(14403162)));*/
					        
			        hp.setEntity(new UrlEncodedFormEntity(nameValuePairs));
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
		            Log.v(TAG, Integer.toString(stringBuilder.length()));
		            if (stringBuilder.length() != 0)
		            {
		            	String kamery= stringBuilder.toString();
		            	ArrayList<Camera> cameras = new ArrayList<Camera>();
		    		
		            	String[] kamera = kamery.split(";");
		            	String[] castKamery=null;
		            	for(int i=0;i<kamera.length;i++)
		            	{
		            		castKamery=kamera[i].split("::");
		    			
		            		Camera cam=new Camera();

		    			//cam.setId(Integer.parseInt(castKamery[0]));
		    			/*if (castKamery.length == 0) {
		    				Log.v(TAG, "vrací null");
		    				return null;}*/
		    			//TODO leze sem a nemá
		    			//else
		    			//{
		    				Log.v(TAG, "a přece sem leze");
		    				cam.setCoordinates(new GeoPoint(Double.parseDouble(castKamery[2]),Double.parseDouble(castKamery[1])));
		    				cam.setDescription(castKamery[0]);
		    				cam.setAddress(castKamery[3]);
		    				cameras.add(cam);
		    			//}
		            	}
		            	return cameras;
		            }
		            else
			          {		            	
		            	/*Ochcávka totálního stupně -- vyrobím kameru někde jinde (kde nebude vidět)
		            	 * 
		            	 */
		            	ArrayList<Camera> cameras = new ArrayList<Camera>();
		            	Camera cam = new Camera();
		            	cam.setCoordinates(new GeoPoint(90000000,0));
	    				cam.setDescription("Santa Claus is watching you!!!");
	    				cam.setAddress("North pole");
	    				cameras.add(cam);
	    				return cameras;
		               }
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
