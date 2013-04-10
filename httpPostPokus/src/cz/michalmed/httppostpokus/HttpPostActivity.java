package cz.michalmed.httppostpokus;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class HttpPostActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_http_post);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.http_post, menu);
		return true;
	}
	
	public void posliDoDb(View v){
		EditText editText = (EditText) findViewById(R.id.editText1);
		
		try {
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("jmeno", editText.getText().toString()));
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://geo102.fsv.cvut.cz/pin2/2013/b/pokus1.php");
			
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));	// Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        
	    } catch (ClientProtocolException ey) {
	        System.out.println("Chyba1");
	        ey.printStackTrace();
	        System.exit(1);
	    } catch (IOException ex) {
	    	System.out.println("Chyba2");
	    	ex.printStackTrace();
	        System.exit(1);
	    }
		
	}
}
