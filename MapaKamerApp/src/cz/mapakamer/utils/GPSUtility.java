
package cz.mapakamer.utils;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import cz.mapakamer.R;

public class GPSUtility {

    public static String getAddressFromGps(Context context, double latitude, double longitude, int maxLines, String delimiter) throws IOException {
        String address = null;
        List<Address> addresses = null;
        try {
            addresses = new Geocoder(context).getFromLocation(latitude, longitude, 1);
        } catch (Exception e) {
        }
       
        if (addresses != null && addresses.size() > 0) {
            Address a = addresses.get(0);
            for(int i=0; i<a.getMaxAddressLineIndex() && i<maxLines; i++) {
                if(address==null)
                    address="";
                else
                    address+=delimiter;
                address+=a.getAddressLine(i);
            }
        }
        return address;
    }
    

    public static Location getLastGps(Context context) {

        LocationManager locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);

        Location location = null;

        for (int i = providers.size() - 1; i >= 0; i--) {
            location = locationManager.getLastKnownLocation(providers.get(i));
            if (location != null)
                break;
        }

        return location;
    }

    public static void checkGPS(Context ctx) {
        LocationManager locManager = (LocationManager) ctx
                .getSystemService(Context.LOCATION_SERVICE);
        if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        	createEnableGPSDialog(ctx);
        }
    }

    private static void createEnableGPSDialog(final Context ctx) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(R.string.gps_not_enabled)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            	showGpsSettings(ctx);
                            }
                        });
        builder.setNegativeButton(android.R.string.no, null);
        builder.show();
    }

    private static void showGpsSettings(Context ctx) {
        Intent gpsOptionsIntent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        ctx.startActivity(gpsOptionsIntent);
    }
    
    public static String getGPSString(double latitude, double longitude) {
        String strLat = coordinateToDMS(latitude);
        if (latitude < 0) {
            strLat += "S";
        }
        else {
            strLat += "N";
        }

        String strLon = coordinateToDMS(longitude);
        if (longitude < 0) {
            strLon += "W";
        }
        else {
            strLon += "E";
        }

        return strLat + "," + strLon;

    }

    private static String coordinateToDMS(double decimal) {
        decimal = Math.abs(decimal);

        int deg = (int) Math.floor(decimal);
        int minutes = (int) Math.floor((decimal * 60) % 60);
        double seconds = ((decimal * 3600) % 60);
        return deg + "°" + minutes + "'"
                + new DecimalFormat("#.###").format(seconds) + "\"";
    }

}
