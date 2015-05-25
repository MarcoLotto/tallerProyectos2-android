package com.example.marco.fiubados;

import android.location.Location;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;

import com.example.marco.fiubados.TabScreens.CallbackScreen;

import com.example.marco.fiubados.httpAsyncTasks.GetFriendsHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.ProfileEditHttpAsyncTask;
import com.example.marco.fiubados.httpAsyncTasks.ProfileInfoHttpAsyncTask;
import com.example.marco.fiubados.model.ProfileField;
import com.example.marco.fiubados.model.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;


public class MapActivity extends AppCompatActivity implements CallbackScreen, com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //aca voy a updatear el profile, para updatear el Location.... es lo que hay
    private static final String EDIT_PROFILE_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/users";
    public static final String SHOW_PROFILE_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/users";

    // Parametros que recibe este activity via extra info
    public static final String USER_ID_PARAMETER = "userIdParameter";

    //private static final int UPDATE_PROFILE_INFO_SERVICE_ID = 3;
    private final int SEARCH_PROFILE_INFO_SERVICE_ID = 1;
    private final int EDIT_PROFILE_INFO_SERVICE_ID = 2;

    private List<ProfileField> fields = new ArrayList<>();

    //aca voy a sacar los datos que necesito para ubicar en el mapa
    public static final String FRIENDS_SEARCH_ENDPOINT_URL = ContextManager.WS_SERVER_URL + "/api/friends";
    private static final int SEARCH_FRIENDS_SERVICE_ID = 0;

    private List<User> users;

    private User user;

    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //magia relacionada con el mapa

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMyLocationEnabled(true);

        this.buildGoogleApiClient();
        this.createLocationRequest();
        // La magia

        LatLng bsas = new LatLng(-34.60305, -58.43855);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(bsas, 11));

        this.users = new ArrayList<>();

        this.obtenerDatosDelPerfil();

        this.onFocus();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(100000);
        mLocationRequest.setFastestInterval(100000/2);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    @Override
    public void onFocus() {

        this.users.clear();

        // Vamos a hacer el pedido de amigos al web service

        //http://www.mocky.io/v2/55638a00ab3d5f9d0fda7799
        //GetFriendsHttpAsyncTask friendsHttpService = new GetFriendsHttpAsyncTask(this, this,
        //        this.SEARCH_FRIENDS_SERVICE_ID, "TODO");
        //friendsHttpService.execute(this.FRIENDS_SEARCH_ENDPOINT_URL);

        GetFriendsHttpAsyncTask friendsHttpService = new GetFriendsHttpAsyncTask(this, this,
                this.SEARCH_FRIENDS_SERVICE_ID, "TODO");
        friendsHttpService.execute("http://www.mocky.io/v2/55638a00ab3d5f9d0fda7799");
    }

    @Override
    public void onServiceCallback(List responseElements, int serviceId) {

        if(serviceId == this.SEARCH_PROFILE_INFO_SERVICE_ID){

            this.fields.clear();
            // TODO: Dependiendo de que se este editando, completar fields con diferente info
            this.fillFieldsListWithPersonalProfileData();
        }
        else if(serviceId == this.EDIT_PROFILE_INFO_SERVICE_ID){

        }
        else if(serviceId == this.SEARCH_FRIENDS_SERVICE_ID){

            this.guardarAmigosRecibidosPorWebService(responseElements);
            this.actualizarMapa();
        }
    }

    private void guardarAmigosRecibidosPorWebService(List responseElements){

        this.users = responseElements;
        Iterator<User> it = this.users.iterator();
        while(it.hasNext()){
            User userAux = it.next();
            if(responseElements.contains(userAux)){
                // Mi usuario y este usuario son amigos
                userAux.setFriendshipStatus(User.FRIENDSHIP_STATUS_FRIEND);
            }
        }
    }

    private void actualizarMapa(){
        map.clear();
        Iterator<User> iterator = this.users.iterator();
        while (iterator.hasNext()) {
            User friend = iterator.next();
            LatLng latLng = new LatLng(friend.getLocation().getLatitude(),friend.getLocation().getLongitude());
            map.addMarker(new MarkerOptions().title( friend.getFullName() )
                            .snippet( friend.getLastTimeUpdate() )
                            .position( latLng )
            );
        }
    }

    private void obtenerDatosDelPerfil() {
        // Primero conseguimos los datos del perfil
        Bundle params = getIntent().getExtras();
        String userOwnerId = params.getString(MapActivity.USER_ID_PARAMETER);
        this.user = new User(userOwnerId);
        ProfileInfoHttpAsyncTask profileInfoService = new ProfileInfoHttpAsyncTask(this, this, SEARCH_PROFILE_INFO_SERVICE_ID, this.user);
        profileInfoService.execute(MapActivity.SHOW_PROFILE_ENDPOINT_URL);
    }

    private void updatePosition() {

        this.fillFieldsListWithPersonalProfileData();
        // Llamamos al servicio de edición de perfil
        ProfileEditHttpAsyncTask profileEditService = new ProfileEditHttpAsyncTask(this, this, EDIT_PROFILE_INFO_SERVICE_ID, this.fields);
        profileEditService.execute(this.EDIT_PROFILE_ENDPOINT_URL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location mCurrentLocation) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
        String lastUpdateTime = sdf.format(c.getTime());
        map.animateCamera(CameraUpdateFactory.newLatLng(
                new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
        );

        user.setLocation(mCurrentLocation);
        user.setLastTimeUpdate(lastUpdateTime);
        //aca hay que enviar el update al servidor
        this.updatePosition();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void fillFieldsListWithPersonalProfileData() {
        this.fields.add(new ProfileField("firstName", this.user.getFirstName(), "Nombre"));
        this.fields.add(new ProfileField("lastName", this.user.getLastName(), "Apellido"));
        this.fields.add(new ProfileField("biography", this.user.getBiography(), "Biografia"));
        this.fields.add(new ProfileField("nationality", this.user.getNationality(), "Nacionalidad"));
        this.fields.add(new ProfileField("city", this.user.getCity(), "Ciudad"));
        this.fields.add(new ProfileField( "latitude", String.valueOf(this.user.getLocation().getLatitude()),"Latitud" ));
        this.fields.add(new ProfileField( "longitude", String.valueOf( this.user.getLocation().getLongitude()),"Longitud" ));
        this.fields.add(new ProfileField( "lastUpdateTime", String.valueOf( this.user.getLocation().getLongitude()),"UltimoTiempoDeUpdate" ));
    }
}

