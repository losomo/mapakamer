package cz.mapakamer.map;


import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import com.google.android.maps.MapView;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;
import com.readystatesoftware.mapviewballoons.BalloonOverlayView;
import cz.mapakamer.activity.CameraDetailActivity;
import cz.mapakamer.entity.Camera;

public class CameraMapOverlay extends BalloonItemizedOverlay<CameraMapOverlayItem> {

    private ArrayList<CameraMapOverlayItem> overlays = new ArrayList<CameraMapOverlayItem>();
    private Context context;
    
    public CameraMapOverlay(int resId, Context ctx, MapView mapView) {
        super(boundCenterBottom(ctx.getResources().getDrawable(resId)),
                mapView);
        setBalloonBottomOffset(ctx.getResources().getDrawable(resId).getMinimumHeight());
        this.context = ctx;
    }

    @Override
    protected CameraMapOverlayItem createItem(int i) {
        return overlays.get(i);
    }

    @Override
    public int size() {
        return overlays.size();
    }

    public void addOverlay(CameraMapOverlayItem overlay) {
    	overlays.add(overlay);
        populate();
    }

    @Override
    protected boolean onBalloonTap(int index, CameraMapOverlayItem item) {
        
    	Camera camera = overlays.get(index).getCamera();
        Intent intent = new Intent(context, CameraDetailActivity.class);
        intent.putExtra("camera", camera.getId());
        
        context.startActivity(intent);
        
    	return true;
    }

    @Override
    protected BalloonOverlayView<CameraMapOverlayItem> createBalloonOverlayView() {
        // use our custom balloon view with our custom overlay item type:
        return new CameraMapBalloonOverlayView<CameraMapOverlayItem>(getMapView()
                .getContext(), getBalloonBottomOffset());
    }
}

