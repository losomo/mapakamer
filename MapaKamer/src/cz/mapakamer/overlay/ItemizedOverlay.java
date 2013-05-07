package cz.mapakamer.overlay;

import java.util.ArrayList;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.views.overlay.ItemizedIconOverlay;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
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
				return onSingleTapUpHelper(index, item);
			}

			@Override public boolean onItemLongPress(final int index, final ImageOverlayItem item) {
				return true;
			}
		}, resourceProxy);
	}

	public boolean onSingleTapUpHelper(int i, ImageOverlayItem item) {
		iView = new ImageView(mContext);
		iView.setImageDrawable(item.getImage());
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
}
