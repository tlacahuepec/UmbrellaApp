package com.foo.umbrella.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.foo.umbrella.R;
import com.foo.umbrella.UmbrellaApp;
import com.foo.umbrella.adapter.MyAdapter;
import com.foo.umbrella.data.ApiServicesProvider;
import com.foo.umbrella.data.api.WeatherService;
import com.foo.umbrella.data.model.CurrentObservation;
import com.foo.umbrella.data.model.WeatherData;
import com.foo.umbrella.ui.fragment.SettingsFragment;

import retrofit2.Response;
import retrofit2.adapter.rxjava.Result;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private TextView tvLocation;
    private TextView tvTemperature;
    private TextView tvConditions;

    // Recycler View items
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private MyAdapter mAdapter;
    private WeatherService weatherService;

    private String zipCodePref;
    private String tempUnitPref;
    private String[] pref_temp_unit_values;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Views
        tvLocation = ((TextView) findViewById(R.id.tv_location));
        tvTemperature = ((TextView) findViewById(R.id.tv_temperature));
        tvConditions = ((TextView) findViewById(R.id.tv_conditions));

        // Setting up Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        // Remove default title text
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Shared Pref
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        zipCodePref = sharedPref.getString(SettingsFragment.KEY_PREF_ZIP_CODE, "");
        tempUnitPref = sharedPref.getString(SettingsFragment.KEY_PREF_TEMP_UNIT, "");
        pref_temp_unit_values = getResources().getStringArray(R.array.pref_temp_unit_values);

        // Setting Up Recycler View
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_hourly_forecast);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(new String[1]);
        mRecyclerView.setAdapter(mAdapter);

        weatherService = ((UmbrellaApp) getApplicationContext()).getApiServicesProvider().getWeatherService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String zipCode = getString(R.string.default_zip_code);
        if (zipCodePref != null && !zipCodePref.isEmpty()) {
            zipCode = zipCodePref;
        }
        weatherService.forecastForZipObservable(zipCode).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Observer<Result<WeatherData>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("Response Error: %s", e.getMessage());
                    }

                    @Override
                    public void onNext(Result<WeatherData> weatherDataResult) {
                        if (weatherDataResult != null) {
                            Response<WeatherData> response = weatherDataResult.response();
                            if (response.isSuccessful()) {
                                WeatherData weatherData = response.body();
                                CurrentObservation currentObservation = weatherData.getCurrentObservation();
                                tvLocation.setText(currentObservation.getDisplayLocation().getFullName());
                                int temp = 0;
                                if (tempUnitPref != null && !tempUnitPref.isEmpty()) {
                                    if (tempUnitPref.equals(pref_temp_unit_values[0])) {
                                        temp = Float.valueOf(currentObservation.getTempFahrenheit()).intValue();
                                    } else {
                                        temp = Float.valueOf(currentObservation.getTempCelsius()).intValue();
                                    }
                                }
                                tvTemperature.setText(getString(R.string.temp_degrees, temp));
                                tvConditions.setText(currentObservation.getWeatherDescription());
                            } else if (response.errorBody() != null) {
                                Timber.e("Response Error: %s", response.message());
                            }
                        } else {
                            Timber.d("No Data Available");
                        }
                    }
                });
    }

    /**
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                // User action no recognized
                return super.onOptionsItemSelected(item);

        }
    }

    /**
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return super.onCreateOptionsMenu(menu);
    }


}
