package cz.mapakamer.activity;


import cz.mapakamer.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;


public class SplashActivity extends Activity {
	
	protected boolean active = true;
	protected int splashTime = 3000; // ms time to display the splash
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); 
        startSplash();           
    }
	
	public void startSplash() {    	
    	// thread for displaying the Splash
        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                	super.run();
                    int waited = 0;
                    while(active && (waited < splashTime)) {
                        sleep(100);
                        if(active) {
                            waited += 100;
                        }
                    }
                } catch(InterruptedException e) {
                    // do nothing
                } finally {                  
                	startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                	finish();
                }
            }
        };
        splashTread.start();
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            active = false;
        }
        return true;
    }
    
    @Override
    public void onBackPressed() {
    	active = false;
    	super.onBackPressed();
    }

}
