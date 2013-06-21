package cz.mapakamer.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import cz.mapakamer.R;
import cz.mapakamer.app.MapaKamerApp;
import cz.mapakamer.entity.Camera;
import cz.mapakamer.utils.GPSUtility;

public class NewCameraActivity extends Activity {

	public static final int CAPTURE_IMG = 0;

	private MapaKamerApp app;
	protected Camera camera;
	private Uri imageUri;
	private Bitmap imageBitmap;
	private Location gpsLocation;
	private Location networkLocation;
	private LocationListener gpsLocListener;
	private LocationListener networkLocListener;
	private boolean gpsDialogAnswered;

	private EditText et_desc;
	private EditText et_location;
	private EditText et_address;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_camera);

		app = (MapaKamerApp) getApplication();

		et_location = (EditText) findViewById(R.id.etCameraLocation);
		et_address = (EditText) findViewById(R.id.etCameraLocationAddress);
		et_desc = (EditText) findViewById(R.id.etCameraDesc);
		//et_location.setText("50.2");
		camera = new Camera();
		camera.setAuthor("Anonym");
		//imageBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.kamera_autodome_g4);
	}

	private void initLocation() {
		gpsLocListener = new LocationListener() {
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
				if (!gpsDialogAnswered) {
					GPSUtility.checkGPS(NewCameraActivity.this);
					gpsDialogAnswered = true;
				}
			}

			public void onLocationChanged(Location location) {
				gpsLocation = location;
				updateLocation();
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
				updateLocation();
			}
		};

		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				gpsLocListener);
		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
				networkLocListener);

		setProgressBarIndeterminateVisibility(Boolean.TRUE);
	}

	public void updateLocation() {
		Location location = gpsLocation;
		if (gpsLocation == null) {
			location = networkLocation;
		}

		try {
			et_address.setText(GPSUtility.getAddressFromGps(this,
					location.getLatitude(), location.getLongitude(), 1, ","));
			et_location.setText(GPSUtility.getGPSString(location.getLatitude(),
					location.getLongitude()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		camera.setLatitude(location.getLatitude());
		camera.setLongitude(location.getLongitude());
		camera.setAddress(et_address.getText().toString());
	}

	public void captureCamera(View view) {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

		File photo = new File(Environment.getExternalStorageDirectory(),
				"newCamera.jpg");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
		imageUri = Uri.fromFile(photo);
		startActivityForResult(intent, CAPTURE_IMG);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case CAPTURE_IMG:
			if (resultCode == Activity.RESULT_OK) {
				Uri selectedImage = imageUri;
				getContentResolver().notifyChange(selectedImage, null);
				ImageView imageView = (ImageView) findViewById(R.id.ivCameraImage);
				ContentResolver cr = getContentResolver();
				Bitmap bitmap;
				try {
					bitmap = android.provider.MediaStore.Images.Media
							.getBitmap(cr, selectedImage);
					imageView.setImageBitmap(bitmap);
					imageBitmap = bitmap;
				} catch (Exception e) {
					Toast.makeText(this,
							getResources().getString(R.string.fail_capture),
							Toast.LENGTH_SHORT).show();
					Log.e("Camera", e.toString());
				}
			}
		}
	}

	public void sendCamera(View view) {

		// TODO / pro prezentacni ucely.... predelat na sql lite/ server post
		// task
		
		processSendingTask task = new processSendingTask();
	    task.execute();

	}

	private Builder cameraSavedDialogPreperation() {
		Builder dialog;
		dialog = new AlertDialog.Builder(this);

		dialog.setMessage(getResources().getString(R.string.camera_saved));
		dialog.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finish();
					}
				});
		return dialog;
	}

	@Override
	public void onResume() {
		super.onResume();
		initLocation();
	}

	@Override
	public void onPause() {
		LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locManager.removeUpdates(gpsLocListener);
		locManager.removeUpdates(networkLocListener);
		super.onPause();
	}

	private class processSendingTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... tmp) {
			try {
				MultipartEntity entity=new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
				HttpContext localContext = new BasicHttpContext();
				entity.addPart("jmeno", new StringBody(et_desc.getText().toString()));
				//entity.addPart("lat",new StringBody(et_location.getText().toString()));
				entity.addPart("lat",new StringBody(Double.toString(camera.getLatitude())));
				entity.addPart("lon",new StringBody(Double.toString(camera.getLongitude())));
				//entity.addPart("lat",new StringBody("49.8"));
				//entity.addPart("lon",new StringBody("14.5"));
				HttpClient httpclient = new DefaultHttpClient();
//				HttpPost httppost = new HttpPost("http://10.0.2.2:8080/Pin2_b13/SaveToDB");
				HttpPost httppost=new HttpPost("http://geo102.fsv.cvut.cz:8080/Pin213/SaveToDB");
				ByteArrayOutputStream bos=new ByteArrayOutputStream();
				imageBitmap.compress(CompressFormat.JPEG, 75, bos);
				byte[] data=bos.toByteArray();
				entity.addPart("uploaded", new ByteArrayBody(data,"myImage.jpg"));
				httppost.setEntity(entity);	// Execute HTTP Post Request
		        httpclient.execute(httppost,localContext);
		        
		    } catch (ClientProtocolException ey) {
		        System.out.println("Chyba1");
		        ey.printStackTrace();
		        System.exit(1);
		    }	catch (IOException ex) {
		    	System.out.println("Chyba2");
		    	ex.printStackTrace();
		        System.exit(1);
		    }
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			NewCameraActivity.this.cameraSavedDialogPreperation().show();
		}
	}

}