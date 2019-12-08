package halla.icsw.d_day;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainFragment extends Fragment implements OnMapReadyCallback {

    View rootView;
    MapView mapView;
   public static LatLng markerpos;
    Marker marker = null;
    public  MainFragment(){

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){

    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        rootView =inflater.inflate(R.layout.content_main,container,false);
        mapView = rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        return rootView;
    }
    @Override
    public void onResume(){
        mapView.onResume();
        super.onResume();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory(){
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        MapsInitializer.initialize(this.getActivity());
        final LatLng latLng = new LatLng(37.302991,127.907488);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,14);
        googleMap.animateCamera(cameraUpdate);
        googleMap.getUiSettings().setZoomControlsEnabled(true);


        googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                if(marker !=null)  marker.remove();
                markerpos = new LatLng(googleMap.getCameraPosition().target.latitude,googleMap.getCameraPosition().target.longitude);
                MarkerOptions markerOptions = new MarkerOptions().position(markerpos);
                marker = googleMap.addMarker(markerOptions);
            }
        });


    }

}
