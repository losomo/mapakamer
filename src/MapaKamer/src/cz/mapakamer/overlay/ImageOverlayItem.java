package cz.mapakamer.overlay;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import android.graphics.drawable.Drawable;

public class ImageOverlayItem extends OverlayItem{

	protected String aTitle;
	protected String aDescription;
	protected GeoPoint aPoint;
	protected Drawable aImage;
	
	public ImageOverlayItem(String title, String description, GeoPoint point, Drawable image) {
		
		super(title, description, point);
		aTitle = title;
		aDescription = description;
		aPoint = point;
		aImage = image;
	}
	public String getDescription(){return aDescription;}
	public Drawable getImage(){return aImage;}
	public String getTitle(){return aTitle;}
}
