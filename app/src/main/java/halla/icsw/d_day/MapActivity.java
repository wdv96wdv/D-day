package halla.icsw.d_day;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class MapActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                float latitude = (float)location.getLatitude();
                float longitude = (float)location.getLongitude();
                Toast.makeText(getApplicationContext(), "GPS수신", Toast.LENGTH_LONG).show();
                Uri uri = Uri.parse(String.format("geo:0,0?z=14,q=%f,%f", latitude, longitude));
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
//                status.setText("위도 : " + location.getLatitude() + "\n경도 :" + location.getLongitude() + "\n고도:" + location.getAltitude());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException e) {
        }
    }
}
