package com.example.news_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import android.Manifest;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class HomeFragment extends Fragment {
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String openWeatherMapAPIKey = "d025fa3327443df9596f3bcad86ec75a";

    private String mParam1;
    private String mParam2;
//    private String city = "default_city";
//    private String state = "default_state";
    Boolean paused = false;



    private double latitude;
    private double longitude;
    LocationManager mLocationManager;
    String cityName = "";
    String stateName = "";
    String countryName;
    CardView weatherCard;
    TextView weathercard_text;
    TextView cityCard;
    TextView stateCard;
    TextView tempCard;
    TextView summaryCard;
    ImageView weatherImg;

    Boolean permissionGranted;

    TextView temp;


    RelativeLayout spinnerHome;
    private String response;


    RecyclerView recyclerView;
    ProgrammingAdapter2 programmingAdapter;
    ProgressBar progressBar;


    private SwipeRefreshLayout mSwipeRefreshLayout;



    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onPause(){
        super.onPause();
        paused = true;
    }

    @Override
    public void onResume(){
        super.onResume();
            if(checkPermissions()){

                if(cityName.length()>0 && stateName.length()>0){

                }
                else{
                    getLastLocation();
                }

            }



    }


    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment

        // Set the adapter
        programmingAdapter = new ProgrammingAdapter2(getActivity());


        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        spinnerHome = rootView.findViewById(R.id.spinner_home);
        progressBar = rootView.findViewById(R.id.progress_bar_homepage);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#2D42BD"), PorterDuff.Mode.MULTIPLY);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_homepage);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Setting horizontal lines
        recyclerView.addItemDecoration(new MugdhaDividerItemDecoration(recyclerView.getContext(),MugdhaDividerItemDecoration.VERTICAL_LIST));

        mSwipeRefreshLayout = rootView.findViewById(R.id.swiperefresh_items);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Code to make the refresh action for home page headlines
                new getHomePageHeadlines().execute();
            }
        });


        // Weather card

        //weatherCard = (CardView) rootView.findViewById(R.id.weather_card);
        //weathercard_text = (TextView)rootView.findViewById(R.id.weather_card_text);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        //temp = rootView.findViewById(R.id.temp_home_text);
        //cityCard = rootView.findViewById(R.id.city_card);
        //stateCard = rootView.findViewById(R.id.state_card);
        //tempCard = rootView.findViewById(R.id.temperature_card);
        //summaryCard = rootView.findViewById(R.id.summary_card);
        //weatherImg = rootView.findViewById(R.id.weather_img);

        getLastLocation();

        if(checkPermissions()){
            programmingAdapter.showWeatherCard();
        }
        else
        {
            programmingAdapter.hideWeatherCard();
        }
//        if(permissionGranted){
//            programmingAdapter.showWeatherCard();
//        }
//        else
//        {
//            programmingAdapter.hideWeatherCard();
//        }



        //weatherCard.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.cloudy_weather));



        //Weather card experiment
//        try{
//            Geocoder geocoder = new Geocoder(getActivity(),Locale.getDefault());
//            Toast.makeText(getActivity(), latitude + " " + longitude, Toast.LENGTH_LONG).show();
//            List<Address> addresses = geocoder.getFromLocation(latitude,longitude,1);
//            String cityName = addresses.get(0).getAddressLine(0);
//            String stateName = addresses.get(0).getAddressLine(1);
//            String countryName = addresses.get(0).getAddressLine(2);
//
//            String str  = "City : " + cityName + "\n State: " + stateName + "\n Country: " + countryName;
//            Toast.makeText(getActivity(), str, Toast.LENGTH_LONG).show();
//            temp.setText(str);
//        }
//        catch (Exception e)
//        {
//            temp.setText(e.toString());
//            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
//
//        }




        // end of weather card


        new getHomePageHeadlines().execute();
        //new weatherCard().execute();

        return rootView;


    }


    @Override
    public void onStart() {
        super.onStart();
        View view = getView();

        if (view != null) {
//            TextView textView = (TextView) view.findViewById(R.id.fragment_home_location);
//            textView.setText(latitude + "\n" + longitude+"\n" + city + "\n" + state);
        }
//        Toast.makeText(getActivity(), "On start method called",Toast.LENGTH_SHORT).show();
        try {
            SharedPreferences pref = getActivity().getSharedPreferences("Mugdha_hw9", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();

            String isChanged = pref.getString("changed", "");
//            Toast.makeText(getActivity(), isChanged,Toast.LENGTH_SHORT).show();
            if (isChanged.equals("true")) {
                String positionOfItem = pref.getString("itemPosition", "");
                //Toast.makeText(getActivity(), positionOfItem, Toast.LENGTH_SHORT).show();
                programmingAdapter.reloadHelper(positionOfItem);
            }

        } catch (Exception e) {
            // Toast.makeText(getActivity(), e.toString(),Toast.LENGTH_SHORT).show();
        }
    }


    private class weatherCard extends AsyncTask<Void, Void, String>{
        @Override
        protected void onPreExecute(){

        }

        @Override
        protected String doInBackground(Void... voids){


            try{
                String strUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&units=metric&appid=d025fa3327443df9596f3bcad86ec75a";
                URL url = new URL(strUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try{
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while( (line = bufferedReader.readLine())!=null){
                        stringBuilder.append(line);
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();

                }finally {
                    urlConnection.disconnect();
                }

            }catch (Exception e){
                //Do nothing
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response){


            try {
                JsonObject gson = new JsonParser().parse(response).getAsJsonObject();
                JSONObject jsonObject = new JSONObject(gson.toString());

                JSONObject mainObj = jsonObject.getJSONObject("main");
                String temperature = mainObj.getString("temp");

                JSONArray weather = jsonObject.getJSONArray("weather");
                JSONObject firstObj = weather.getJSONObject(0);
                String summary = firstObj.getString("main");

//                weatherCard.setBackgroundResource(R.drawable.cloudy_weather);
                //Now we have summery, temperature, city name
                String temp_text = "City = " + cityName + "\n Temperature: " + temperature + "\n Summary: " + summary;
                //weathercard_text.setText(temp_text);

                String newSummary = summary;
                summary = summary.toLowerCase();

//                if(summary.equals("clouds")){
//                    weatherImg.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.cloudy_weather));
//                    //weatherCard.setBackgroundResource(R.drawable.cloudy_weather);
//                }else if(summary.equals("clear")){
//                    weatherImg.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.clear_weather));
//                    //weatherCard.setBackgroundResource(R.drawable.clear_weather);
//                }else if(summary.equals("snow")){
//
//                    weatherImg.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.snowy_weather));
//                    //weatherCard.setBackgroundResource(R.drawable.snowy_weather);
//                }else if(summary.equals("rain") || summary.equals("drizzle")){
//
//                    weatherImg.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.rainy_weather));
//                    //weatherCard.setBackgroundResource(R.drawable.rainy_weather);
//                }else if(summary.equals("thunderstorm")){
//
//                    weatherImg.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.thunder_weather));
//                    //weatherCard.setBackgroundResource(R.drawable.thunder_weather);
//                }else{
//
//                    weatherImg.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.sunny_weather));
//                    //weatherCard.setBackgroundResource(R.drawable.sunny_weather);
//                }

//                cityCard.setText(cityName);
//                stateCard.setText(stateName);
//                tempCard.setText(temperature);
//                summaryCard.setText(newSummary);
                double temperature_double = Double.parseDouble(temperature);
                int temperature_int = (int) Math.round(temperature_double);

                temperature = String.valueOf(temperature_int) + "\u00B0 C";

                if(programmingAdapter!=null)
                {
                    programmingAdapter.setWeatherData(cityName,stateName,temperature,newSummary);
                }



            } catch (JSONException e) {
                //Do nothing for now
                e.printStackTrace();
                //weathercard_text.setText(e.toString());
            }

        }

    }


    private class getHomePageHeadlines extends AsyncTask<Void, Void, String>{

        @Override
        protected void onPreExecute(){
            if(!mSwipeRefreshLayout.isRefreshing()){
//                mSwipeRefreshLayout.setRefreshing(false);
               // progressBar.setVisibility(View.VISIBLE);
                spinnerHome.setVisibility(View.VISIBLE);
            }

        }

        @Override
        protected String doInBackground(Void... voids) {

            try{
                URL url = new URL("https://frontendinreact.wn.r.appspot.com/get_hw9_homepage_data");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try{
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while( (line = bufferedReader.readLine())!=null){
                        stringBuilder.append(line);
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();

                }finally {
                    urlConnection.disconnect();
                }

            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(String response){

            if(response!=null){
                //programmingAdapter = new ProgrammingAdapter2(response,getActivity());
                programmingAdapter.setDataSource(response,getActivity());
                recyclerView.setAdapter(programmingAdapter);

//                JsonObject gson = new JsonParser().parse(response).getAsJsonObject();
//                try{
//                    JSONObject jsonObject = new JSONObject(gson.toString());
//                    JSONArray arrayObject = jsonObject.getJSONArray("data");
//                    int numItems = arrayObject.length();
//
//                    for(int i = 1;i<numItems;i++){
//                        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL),i);
//
//                    }
//
//
//                }catch (Exception e){
//                    e.printStackTrace();
//                }




            }



//            if(response == null)
//            {
//                response = "ERROR";
//            }
//             temp.setText(response);

            //progressBar.setVisibility(View.GONE);
            spinnerHome.setVisibility(View.GONE);
            if(mSwipeRefreshLayout.isRefreshing()){
                mSwipeRefreshLayout.setRefreshing(false);
            }


        }
    }

    // For weather card
    private boolean checkPermissions(){
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    private void requestPermissions(){
        ActivityCompat.requestPermissions(
                getActivity(),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Granted. Start getting the location information

                getLastLocation();
                permissionGranted = true;
                //programmingAdapter.showWeatherCard();

            }
            else{

               // Permission not granted, hide the weather card
                permissionGranted = false;
                //programmingAdapter.hideWeatherCard();
            }
        }
    }

    private boolean isLocationEnabled(){
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            permissionGranted = true;
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                    // temp.setText(latitude + " | " + longitude);
                                    //TextView temp = getActivity().findViewById(R.id.temp_home_text);
                                    //temp.setText(latitude + " | " + longitude);
                                    try{
                                        Geocoder gcd = new Geocoder(getActivity(),Locale.getDefault());
                                        List<Address> addresses = gcd.getFromLocation(latitude,longitude,1);
                                         cityName = addresses.get(0).getLocality();
                                         stateName = addresses.get(0).getAdminArea();
                                         countryName = addresses.get(0).getCountryName();

                                         new weatherCard().execute();


                                    }catch (Exception e){
                                       // temp.setText(e.toString());
                                    }



                                }
                            }
                        }
                );
            } else {
                Toast.makeText(getActivity(), "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            //TextView temp = getActivity().findViewById(R.id.temp_home_text);
            //temp.setText(latitude + " | " + longitude);


        }
    };



}
