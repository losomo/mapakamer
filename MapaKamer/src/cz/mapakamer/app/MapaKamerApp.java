package cz.mapakamer.app;

import java.util.ArrayList;

import cz.mapakamer.entity.Camera;
import android.app.Application;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;


public class MapaKamerApp extends Application {

	protected SharedPreferences pref;
	private ArrayList<Camera> allCameras;
		
	
	@Override
    public void onCreate() {
        super.onCreate();        
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		
		allCameras = new ArrayList<Camera>();			
    }
	
	public void initSomeCameras(Location myLocation) {        	
    	//TODO / totalne zmenit na stahovani ze serveru/sql lite
    	Camera first = new Camera();
    	first.setId(1);
    	first.setLatitude(50.06237349443927);
    	first.setLongitude(14.444809198903386);
    	first.setDescription("popisek kamery ID 1");
    	first.setDistance(Camera.howFar(first.getLatitude(), first.getLongitude(), myLocation.getLatitude(), myLocation.getLongitude()));
    	allCameras.add(first);  
    	
    	Camera second = new Camera();
    	second.setId(2);
    	second.setLatitude(50.07479677603857);
    	second.setLongitude(14.480924606323242);
    	second.setDescription("popisek druhe kamery");
    	second.setDistance(Camera.howFar(second.getLatitude(), second.getLongitude(), myLocation.getLatitude(), myLocation.getLongitude()));
    	allCameras.add(second); 
    	
    	Camera third = new Camera();
    	third.setId(3);
    	third.setLatitude(50.0857849787494);
    	third.setLongitude(14.405213358113542);
    	third.setDescription("dalsi slidici kamera...");
    	third.setDistance(Camera.howFar(third.getLatitude(), third.getLongitude(), myLocation.getLatitude(), myLocation.getLongitude()));
    	allCameras.add(third); 
    }
	
	public ArrayList<Camera> getAllCameras() {
		return allCameras;
	}
	
	public Camera getCamera(int id) {
		return allCameras.get(id-1);
	}
	
	public void addNewCamera(Camera camera) {
		allCameras.add(camera);
	}
	
	public int getNextCameraID() {
		return allCameras.size()+1;
	}
	
	public int getAllCameraSize() {
		return allCameras.size();
	}
	
}

