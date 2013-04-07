package cz.mapakamer.map;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

import cz.mapakamer.entity.Camera;

public class CameraMapOverlayItem extends OverlayItem {

    private Camera camera;

    public CameraMapOverlayItem(Camera camera) {
        super(new GeoPoint(
                (int) (camera.getLatitude() * 1E6), (int) (camera
                        .getLongitude() * 1E6)), camera.getDescription(), "");
        this.camera = camera;
    }

    public Camera getCamera() {
        return camera;
    }

}
