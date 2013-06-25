package cz.mapakamer.activity;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import cz.mapakamer.R;

public class AddCamerasActivity extends Activity {

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
				CheckBox chbox = new CheckBox(this);
				llLayout.addView(chbox);
				TextView text = new TextView(this);
				text.setText(name);
                text.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                llLayout.addView(text);
                llListOfCameras.addView(llLayout);
			}
		}
	}
}
