package cz.mapakamer.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import cz.mapakamer.R;

public class HomeActivity extends Activity {


	private LinearLayout ll_add;
	private LinearLayout ll_adds;
	private LinearLayout ll_show;
	private LinearLayout ll_about;
	private LinearLayout ll_donate;



	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        setButtonClickListener();
        
    }

	private void setButtonClickListener() {

		ll_add = (LinearLayout)findViewById(R.id.llAddCamera);
		ll_adds = (LinearLayout)findViewById(R.id.llAddCameras);
		ll_show = (LinearLayout)findViewById(R.id.llShowMap);
        ll_about = (LinearLayout)findViewById(R.id.llAbout);
        ll_donate = (LinearLayout)findViewById(R.id.llDonate);

		ll_add.setClickable(true);
		ll_adds.setClickable(true);
		ll_show.setClickable(true);
		ll_about.setClickable(true);
		ll_donate.setClickable(true);

        
		ll_add.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	    		addNewCamera();
	    	}
	    });

		ll_adds.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addSavedCameras();		
			}
		});
		
		ll_show.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	    		showMap();
	    	}
	    });

		ll_about.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	    		showAbout();
	    	}
	    });

		ll_donate.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	    		showDonate();
	    	}
	    });

	}

	public void addNewCamera() {
		Intent i = new Intent(this, NewCameraActivity.class);
		startActivity(i);
	}

	public void addSavedCameras() {
		Intent i = new Intent(this, AddCamerasActivity.class);
		startActivity(i);
	}
	
	public void showMap() {
		Intent i = new Intent(this, CameraMapActivity.class);
		startActivity(i);
	}

	public void showAbout() {
		Intent i = new Intent(this, AboutActivity.class);
		startActivity(i);
	}

	public void showDonate() {
		Intent i = new Intent(this, DonateActivity.class);
		startActivity(i);
	}

}