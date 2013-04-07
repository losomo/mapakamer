package cz.mapakamer.activity;


import cz.mapakamer.R;
import cz.mapakamer.app.MapaKamerApp;
import cz.mapakamer.entity.Camera;
import cz.mapakamer.utils.GPSUtility;
import cz.mapakamer.utils.ImageUtility;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class CameraDetailActivity extends Activity {

	private MapaKamerApp app;
	private int cameraId;
	protected Camera camera;
	 
    private TextView tv_desc;
    private TextView tv_location;
    private TextView tv_address;
    private ImageView iv_image;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle bundle = getIntent().getExtras();
        cameraId = bundle.getInt("camera");
        
        app = (MapaKamerApp) getApplication();
        
        camera = app.getCamera(cameraId);
                
        setContentView(R.layout.activity_camera_detail);
      
        tv_location = (TextView)findViewById(R.id.tvCameraLocation);
        tv_address = (TextView)findViewById(R.id.tvCameraLocationAddress);
        tv_desc = (TextView)findViewById(R.id.tvCameraDesc);
        iv_image = (ImageView)findViewById(R.id.ivCameraImage);
        
        fillFields();
    }
	
	private void fillFields() {
		tv_location.setText(GPSUtility.getGPSString(camera.getLatitude(), camera.getLongitude()));
		tv_address.setText(camera.getAddress());
		tv_desc.setText(camera.getDescription());
		if (camera.getImageBase64Encoded() != null && !camera.getImageBase64Encoded().equals("")) {
			iv_image.setImageBitmap(ImageUtility.decodeBase64(camera.getImageBase64Encoded()));
		}
		
		
	}
}
