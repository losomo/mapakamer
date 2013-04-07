/***
 * Copyright (c) 2011 readyState Software Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package cz.mapakamer.map;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.maps.OverlayItem;
import com.readystatesoftware.mapviewballoons.BalloonOverlayView;
import com.readystatesoftware.mapviewballoons.R;

public class CameraMapBalloonOverlayView<Item extends OverlayItem> extends
        BalloonOverlayView<CameraMapOverlayItem> {

    private static float IMAGE_HEIGHT_DP = 65f;

    private static Integer mImgHeight;

    private TextView mTitle;
    private View mRootLayout;

    public CameraMapBalloonOverlayView(Context context, int balloonBottomOffset) {
        super(context, balloonBottomOffset);
    }

    @Override
    protected void setupView(Context context, final ViewGroup parent) {

        // inflate our custom layout into parent
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootLayout = inflater.inflate(cz.mapakamer.R.layout.camera_balloon_overlay, parent);

        // setup our fields
        mTitle = (TextView) mRootLayout.findViewById(R.id.balloon_item_title);

        // implement balloon close
        ImageView close = (ImageView) mRootLayout.findViewById(R.id.balloon_close);
        close.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                parent.setVisibility(GONE);
            }
        });

        setImgHeight(context);
    }

    private void setImgHeight(Context context) {
        if (mImgHeight == null) {
            mImgHeight = (int) dpToPix(context, IMAGE_HEIGHT_DP);
        }
    }

    public static float dpToPix(Context context, Float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    @Override
    protected void setBalloonData(CameraMapOverlayItem item, ViewGroup parent) {
        // map our custom item data to fields
        mTitle.setText(item.getTitle());
    }

}
