package cz.mapakamer.overlay;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.views.overlay.ItemizedIconOverlay;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;
import cz.mapakamer.R;
import cz.mapakamer.overlay.ImageOverlayItem;

public class ItemizedOverlay {

	
	protected ItemizedIconOverlay<ImageOverlayItem> mOverlay;
	protected Context mContext;
	protected Drawable mMarker;
	protected ImageView iView;

	public ItemizedOverlay(Drawable marker, Context context) {
		mContext = context;
		ArrayList<ImageOverlayItem> items = new ArrayList<ImageOverlayItem>();
		ResourceProxy resourceProxy = (ResourceProxy) new DefaultResourceProxyImpl(mContext);
		mMarker = marker;
		
		mOverlay = new ItemizedIconOverlay<ImageOverlayItem>(items, mMarker,new ItemizedIconOverlay.OnItemGestureListener<ImageOverlayItem>() {
			@Override public boolean onItemSingleTapUp(final int index, final ImageOverlayItem item) {
				try {
					return onSingleTapUpHelper(index, item);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;
			}

			@Override public boolean onItemLongPress(final int index, final ImageOverlayItem item) {
				return true;
			}
		}, resourceProxy);
	}

	public boolean onSingleTapUpHelper(int i, ImageOverlayItem item) throws InterruptedException, ExecutionException {
		iView = new ImageView(mContext);
		ImageHttpPost ihp=new ImageHttpPost();
		ihp.execute(item.getTitle());
		iView.setImageBitmap(ihp.get());
		//item.getImage());
		//iView.setImageResource(R.drawable.images);
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		//dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		dialog.setView(iView);
		dialog.setNeutralButton(R.string.close_button,new DialogInterface.OnClickListener()
		{
			@Override
	        public void onClick(DialogInterface dialog, int which) 
	        {
	            dialog.dismiss();
	        }
		});
		dialog.show();
		return true;
	}

	public void addItem(ImageOverlayItem item){
		mOverlay.addItem(item);
	}

	public ItemizedIconOverlay<ImageOverlayItem> getOverlay(){
		return mOverlay;
	}
	
	private class ImageHttpPost extends AsyncTask<String,Void,Bitmap>{
		@Override
		protected Bitmap doInBackground(String...q) {
			// TODO Auto-generated method stub
			InputStream inputStream=null;
			try {
				
				HttpClient httpclient = new DefaultHttpClient();
				//HttpPost hp=new HttpPost("http://10.0.2.2:8080/Pin2_b13/GetFromDB");
				//HttpPost hp=new HttpPost("http://geo102.fsv.cvut.cz:8080/Pin213/GetFromDB");
				HttpPost hp=new HttpPost("http://www.mapakamer.cz/mobilniMK/mobilniMK/GetFromDB");
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		        nameValuePairs.add(new BasicNameValuePair("path", q[0]));
		        hp.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		        
		        HttpResponse res = httpclient.execute(hp);
		        HttpEntity bufHttpEntity = res.getEntity();
		        inputStream = bufHttpEntity.getContent();
		       // BufferedInputStream buf = new BufferedInputStream(inputStream);
		        Bitmap bit=BitmapFactory.decodeStream(inputStream);
	            //EditText et = (EditText) findViewById(R.id.editText1);
	     
	            //this.mujText=stringBuilder.toString();
	            //inputStream.close();
	            return bit;
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
		}}
}
