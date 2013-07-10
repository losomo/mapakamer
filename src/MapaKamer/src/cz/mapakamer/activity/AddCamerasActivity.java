package cz.mapakamer.activity;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import cz.mapakamer.R;
import cz.mapakamer.entity.Camera;

public class AddCamerasActivity extends Activity {

	private Bitmap imageBitmap;
	protected Camera cam;
	public ArrayList<Camera> cameras = new ArrayList<Camera>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_cameras);
		
		File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"MapaKamer");
		LinearLayout llListOfCameras = (LinearLayout) findViewById(R.id.listOfCameras);

		for (File f : directory.listFiles()) {
			if(f.isFile() && f.toString().endsWith(".txt"))
			{
				String name = f.getName();
				LinearLayout llLayout = new LinearLayout(this);
				TextView text = new TextView(this);
				text.setText(name);
                text.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                llLayout.addView(text);
                llListOfCameras.addView(llLayout);
			}
		}
	}
	
	private boolean getCameras(){
		File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"MapaKamer");
		
		for (File f : directory.listFiles()) {
			if(f.isFile() && f.toString().endsWith(".txt"))
			{
				Camera camera = new Camera();
				
				try {
				    BufferedReader br = new BufferedReader(new FileReader(f));
				    String line;

				    line = br.readLine();
				    String desc = line;
				    camera.setDescription(desc);
				    
				    line = br.readLine();
				    double latitude = Double.parseDouble(line);
				    camera.setLatitude(latitude);
				    
				    line = br.readLine();
				    double longitude = Double.parseDouble(line);
				    camera.setLongitude(longitude);
				    
				    line = br.readLine();
				    String picfilename = line;
				    camera.setImageBase64Encoded(picfilename);
				    
				    cameras.add(camera);
				}
				catch (IOException e) {
					System.out.println("InOut Exception");
			    	e.printStackTrace();
			        System.exit(1);
			        return false;
				}				
			}
		}
		return true;
	}
	
	private boolean addCameras(){
		
		for (Camera camera : cameras)
		{
			cam = new Camera();
			cam = camera;
			addCamera();
		}
		
		return true;
	}
	
	private void addCamera()
	{
		processSendingTask task = new processSendingTask();
	    task.execute();
	}
	
	private void deleteFiles()
	{
		File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"MapaKamer");

		for (File f : directory.listFiles())
		{
			f.delete();
		}
	}
	
	public void processLoadCameras(){
		if (getCameras())
		{
			addCameras();
			deleteFiles();
			AddCamerasActivity.this.cameraSavedDialogPreperation().show();
			
		}
		
	}
	
	private Builder cameraSavedDialogPreperation() {
		Builder dialog;
		dialog = new AlertDialog.Builder(this);

		dialog.setMessage(getResources().getString(R.string.cameras_saved));
		dialog.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finish();
					}
				});
		return dialog;
	}
	
	private class processSendingTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... tmp) {
			try {
				MultipartEntity entity=new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
				HttpContext localContext = new BasicHttpContext();
				entity.addPart("jmeno", new StringBody(cam.getDescription()));
				entity.addPart("lon",new StringBody(Double.toString(cam.getLatitude())));
				entity.addPart("lat",new StringBody(Double.toString(cam.getLongitude())));
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost=new HttpPost("http://www.mapakamer.cz/mobilniMK/mobilniMK/SaveToDB");
				ByteArrayOutputStream bos=new ByteArrayOutputStream();
				imageBitmap.compress(CompressFormat.JPEG, 50, bos);
				byte[] data=bos.toByteArray();
				entity.addPart("uploaded", new ByteArrayBody(data,"Mapa Kamer" + File.separator + cam.getImageBase64Encoded()));
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

	}
	
}
