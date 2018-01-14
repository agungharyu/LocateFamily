package familia.user.locatefamily;

import android.annotation.TargetApi;
import android.icu.text.DecimalFormat;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.text.TextUtilsCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class TrackingActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private String email;

    DatabaseReference locations;
    Double lat,lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locations = FirebaseDatabase.getInstance().getReference("Locations");

        if(getIntent() != null){
            email = getIntent().getStringExtra("email");
            lat = getIntent().getDoubleExtra("lat",0);
            lng = getIntent().getDoubleExtra("lng",0);
        }
        if(!TextUtils.isEmpty(email)){
            loadLocationForThisUser(email);
        }
    }

    private void loadLocationForThisUser(String email) {
        Query user_location = locations.orderByChild("email").equalTo(email);

        user_location.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                    Tracking tracking = postSnapshot.getValue(Tracking.class);

                    LatLng friendLocaton = new LatLng(Double.parseDouble(tracking.getLat()),
                                                        Double.parseDouble(tracking.getLng()));

                    Location to = new Location("");
                    to.setLatitude(lat);
                    to.setLongitude(lng);

                    Location friend = new Location("");
                    friend.setLatitude(Double.parseDouble(tracking.getLat()));
                    friend.setLongitude(Double.parseDouble(tracking.getLng()));

                    //Toast.makeText(TrackingActivity.this, ""+tracking.getLat(), Toast.LENGTH_SHORT).show();

                    //distance(to,friend);

                    mMap.addMarker(new MarkerOptions().position(friendLocaton)
                                        .title(tracking.getEmail())
                                        .snippet("Distance "+String.format("%.2f",to.distanceTo(friend)/1000))
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(friendLocaton,12.0f));

                }

                LatLng current = new LatLng(lat,lng);
                mMap.addMarker(new MarkerOptions().position(current).title(FirebaseAuth.getInstance().getCurrentUser().getEmail()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private double distance(Location to, Location friend) {
        double theta = to.getLongitude() - friend.getLongitude();
        double dist = Math.sin(deg2rad(to.getLatitude()))
                     * Math.sin(deg2rad(friend.getLatitude()))
                     * Math.cos(deg2rad(to.getLatitude()))
                     * Math.cos(deg2rad(friend.getLatitude()))
                     * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        Toast.makeText(this, ""+dist, Toast.LENGTH_SHORT).show();
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        return(dist);

    }

    private double rad2deg(double rad) {
        return (rad*180.0/Math.PI);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI/180.0);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }
}
