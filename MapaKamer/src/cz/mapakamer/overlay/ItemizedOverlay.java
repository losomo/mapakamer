package cz.mapakamer.overlay;

import java.util.ArrayList;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import cz.mapakamer.R;

public class ItemizedOverlay {

	
	protected ItemizedIconOverlay<OverlayItem> mOverlay;
	protected Context mContext;
	protected Drawable mMarker;
	protected ImageView iView;

	public ItemizedOverlay(Drawable marker, Context context) {
		mContext = context;
		ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
		ResourceProxy resourceProxy = (ResourceProxy) new DefaultResourceProxyImpl(mContext);
		mMarker = marker;

		mOverlay = new ItemizedIconOverlay<OverlayItem>(items, mMarker,new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
			@Override public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
				return onSingleTapUpHelper(index, item);
			}

			@Override public boolean onItemLongPress(final int index, final OverlayItem item) {
				return true;
			}
		}, resourceProxy);

	}

	public boolean onSingleTapUpHelper(int i, OverlayItem item) {
		iView = new ImageView(mContext);
		iView.setImageResource(R.drawable.images);
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

	public void addItem(OverlayItem item){
		mOverlay.addItem(item);
	}

	public ItemizedIconOverlay<OverlayItem> getOverlay(){
		return mOverlay;
	}
}
