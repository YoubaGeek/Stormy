package ghacompany.com.stormy;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Ayoub.
 */
public class MainActivity extends ActionBarActivity {

    public static  final  String TAG = MainActivity.class.getSimpleName();
    private CurrentWeather mCurrentWeather;
    private GPSTracker gps;
    private double latitude;
    private double longitude;
    private String _Location;

    @InjectView(R.id.timeId) TextView mTimeLabel;
    @InjectView(R.id.temperatureId) TextView mTemperatureLabel;
    @InjectView(R.id.humidityValue) TextView mHumidityValue;
    @InjectView(R.id.precipValue) TextView mPrecipValue;
    @InjectView(R.id.summaryId) TextView mSummaryLabel;
    @InjectView(R.id.iconId)ImageView mIconImageView;
    @InjectView(R.id.refreshId)ImageView mRefreshImageView;
    @InjectView(R.id.locationId)TextView mLocationName;
    @InjectView(R.id.progressBar)ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        mProgressBar.setVisibility(View.INVISIBLE);
        // create class object

        mRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getForecast(latitude, longitude);
            }
        });
        getForecast(latitude, longitude);

        Log.d(TAG, "Main UI Code");
    }

    private void getForecast(double latitude, double longitude) {

        gps = new GPSTracker(MainActivity.this);

        // check if GPS enabled
        if(gps.canGetLocation()){

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }else if (isNetworkAvailable() && !gps.canGetLocation()){
            gps.showSettingsAlert();
        }

        Geocoder gcd = new Geocoder(MainActivity.this, Locale.getDefault());
        List<Address> addresses = null;
        try {

            addresses = gcd.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0)
            _Location = addresses.get(0).getLocality();

        String API_KEY = "cf62265b79b7f29f6ab95b4fbd0eb507";
        String forecastUrl = "https://api.forecast.io/forecast/"+API_KEY+"/"+latitude+","+longitude;

        if(isNetworkAvailable()) {
            statusRefresh();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(forecastUrl)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateDisplay();
                            statusRefresh();
                        }
                    });
                    alertUserAboutError();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();
                        if (response.isSuccessful()) {
                            Log.v(TAG, jsonData);
                            mCurrentWeather = getCurrentDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                    statusRefresh();
                                }
                            });
                        } else {
                            alertUserAboutError();
                        }
                    }
                    catch (IOException | JSONException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    }
                }
            });
        }else{
            mLocationName.setText("Failed Network!");
            Toast.makeText(this, "failed network", Toast.LENGTH_LONG).show();
        }
    }

    private void statusRefresh() {
        if(mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageView.setVisibility(View.INVISIBLE);
        }
        else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImageView.setVisibility(View.VISIBLE);
        }
    }

    private void updateDisplay() {
        mTemperatureLabel.setText(mCurrentWeather.getTemperature() + "");
        mTimeLabel.setText("At " + mCurrentWeather.getFormattedTime() + " it will be");
        mHumidityValue.setText(mCurrentWeather.getHumidity() + "");
        mPrecipValue.setText(mCurrentWeather.getPrecipChance()  + "%");
        mSummaryLabel.setText(mCurrentWeather.getSummary());
        Drawable drawable = getResources().getDrawable(mCurrentWeather.getIconId());
        mIconImageView.setImageDrawable(drawable);
        mLocationName.setText(_Location);
    }

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException{


        //declare a jsonobject that contains the parent of the hierachy of the json data
        JSONObject data = new JSONObject(jsonData);
        //get timezone from the jsonobject and store it into a string variable
        String timeZone = data.getString("timezone");
        Log.i(TAG, timeZone);
        //now declare another jsonobject that will contain the json data of a specific jsonobject of the json feed
        JSONObject currently = data.getJSONObject("currently");
        //declare an instance of the CurrentWeather class
        CurrentWeather currentWeather = new CurrentWeather();
        //set variables with json data parsed
        currentWeather.setHumidity(currently.getDouble("humidity"));
        Log.i(TAG, "HUMIDITY: " +currently.getDouble("humidity"));
        currentWeather.setSummary(currently.getString("summary"));
        Log.i(TAG, "SUMMARY: " +currently.getString("summary"));
        currentWeather.setPrecipChance(currently.getInt("precipProbability"));
        Log.i(TAG, "PRECIPCHANCE: " +currently.getInt("precipProbability"));
        currentWeather.setTime(currently.getLong("time"));
        Log.i(TAG, "TIME: " +currently.getLong("time"));
        currentWeather.setIcon(currently.getString("icon"));
        Log.i(TAG, "ICON: " +currently.getString("icon"));
        currentWeather.setTemperature(currently.getInt("temperature"));
        Log.i(TAG, "TEMPERATURE: " +currently.getInt("temperature"));
        currentWeather.setTimeZone(timeZone);

        Log.d(TAG, "TEMPERATURE: " + currentWeather.getFormattedTime());

        return currentWeather;
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }
        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }
}
