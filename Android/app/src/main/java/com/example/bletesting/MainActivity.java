package com.example.bletesting;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.BLUETOOTH;
import static android.Manifest.permission.BLUETOOTH_ADMIN;

import static com.example.bletesting.RecyclerBleDeviceActivity.ALTITUDE_UUID;
import static com.example.bletesting.RecyclerBleDeviceActivity.TEMPERATURE_UUID;
import static com.example.bletesting.RecyclerBleDeviceActivity.PRESSURE_UUID;
import static com.example.bletesting.RecyclerBleDeviceActivity.UV_INDEX_UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.ekn.gruzer.gaugelibrary.ArcGauge;
import com.example.bletesting.ApiClient.ApiClient;
import com.example.bletesting.Fragment.UVIndexDataFragment;
import com.example.bletesting.Fragment.UVIndexPrediction;
import com.example.bletesting.Response.OpenWeatherResponse;
import com.example.bletesting.Response.UVIndexResponse;
import com.example.bletesting.Service.OpenWeatherService;
import com.example.bletesting.Service.SensorService;
import com.example.bletesting.Service.UVIndexService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    private static final String API_KEY = "f4f1a7372a6beff49da94f8249a3a339";
    private final static String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_ID = 4;
    private static final int REQUEST_LOCATION_PERMISSION = 5;
    private TextView uvStatusTextView;
    private TextView scanView;
    private ProgressBar scanProgressBar;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private ImageView reconnectView;
    private static final int REQUEST_BLUETOOTH_ADMIN_ID = 1;
    private static final int REQUEST_LOCATION_ID = 2;
    private static final int REQUEST_BLUETOOTH_ID = 3;
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";

    private TextView temp;

    private TextView seaLevel;

    private TextView pressure;

    private TextView maxUVView;

    private TextView windSpeedView;

    private String deviceName;
    TextView sunscreenTextView;

    private String deviceAddress;

    private SharedPreferences sharedPrefBLE;
    private ImageView ellipseView; // Add this line

    private ImageView MaxUVIndexStatus; // Add this line

    private boolean connected = false;
    private TextView altitudeView;
    private TextView pressureView;
    private TextView temperatureView;
    private ArcGauge uvIndexGauge;
    private Queue<BluetoothGattCharacteristic> characteristicQueue = new LinkedList<>();

    private TextView timeTextView;
    private Handler handler;
    private Runnable runnable;
    private ActivityResultLauncher<Intent> bleScannerLauncher;

    private FusedLocationProviderClient fusedLocationClient;
    private TextView locationTextView;
    // Global sensorData instance
    private SensorData sensorData = new SensorData(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, null, null, null);
    private FrameLayout frameLayout2, frameLayout4, frameLayout1;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uvIndexGauge = findViewById(R.id.uvIndexGauge);
        ellipseView = findViewById(R.id.ellipseView); // Add this line
        MaxUVIndexStatus = findViewById(R.id.MaxUVIndexStatus); // Add this line
        frameLayout2 = findViewById(R.id.frameLayout2);
        frameLayout4 = findViewById(R.id.frameLayout4);
        frameLayout1 = findViewById(R.id.frameLayout1);
        sunscreenTextView = findViewById(R.id.sunscreenText); // Ensure this ID matches your XML
//        sunscreenTextView.setText("Testing static update");


//        // Bottom navigation bar
//        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        sharedPrefBLE = getSharedPreferences(getString(R.string.ble_device_key), Context.MODE_PRIVATE);
        deviceName = sharedPrefBLE.getString("name", null);
        deviceAddress = sharedPrefBLE.getString("address", null);

        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationTextView = findViewById(R.id.location);

        // UV Index Gauge
        // Set minimum and maximum values
        uvIndexGauge.setMinValue(0);  // Set the minimum value for UV index
        uvIndexGauge.setMaxValue(11); // Set the maximum value for UV index

        bleCheck();
        locationCheck();
        checkPermissions();
        checkLocationPermission();

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        initializeLayout();
//        fetchWeatherData();
//        fetchUvIndex();

//



        if (hasPermissions(ACCESS_FINE_LOCATION)) {
            getUserLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }

        bleScannerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            deviceName = data.getStringExtra("device_name");
                            deviceAddress = data.getStringExtra("device_address");
                            connectToDevice(deviceAddress);
                        }
                    }
                }
        );



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            private int currentSelectedItemId = R.id.nav_home; // Default or current item

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                // Prevent reloading if the current item is reselected
                if (itemId == currentSelectedItemId) {
                    return false;
                }

                Fragment selectedFragment = null;

                if (itemId == R.id.nav_home) {
                    // Navigate home only if it's not already displayed
                    if (currentSelectedItemId != R.id.nav_home) {
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clears all other activities on top of MainActivity
                        startActivity(intent);
                        finish(); // Finish the current activity if it's not the home
                    }
                    currentSelectedItemId = itemId; // Update the current selected item
                    return true; // Since home is handled differently
                } else if (itemId == R.id.nav_uv_index_data) {
                    selectedFragment = new UVIndexDataFragment();
                    findViewById(R.id.frameLayout1).setVisibility(View.GONE);
                    findViewById(R.id.frameLayout2).setVisibility(View.GONE);
                    if (uvStatusTextView != null) {
                        uvStatusTextView.setVisibility(View.GONE);
                    }
                    currentSelectedItemId = itemId; // Update the current selected item

                } else if (itemId == R.id.nav_uv_prediction) {
                    selectedFragment = new UVIndexPrediction();
                    findViewById(R.id.frameLayout1).setVisibility(View.GONE);
                    findViewById(R.id.frameLayout2).setVisibility(View.GONE);

                    if (uvStatusTextView != null) {
                        uvStatusTextView.setVisibility(View.GONE);
                    }
                    currentSelectedItemId = itemId; // Update the current selected item


                }

                // Perform the fragment transaction if needed
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout4, selectedFragment)
                            .commit();
                    currentSelectedItemId = itemId; // Update the current selected item
                }

                return true;
            }
        });




        // Initialize the real-time clock
        timeTextView = findViewById(R.id.time);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                updateTime();
                handler.postDelayed(this, 1000); // Update every second
            }
        };
        handler.post(runnable); // Start the Runnable
    }


    // Sensor Data diubah menjadi Map<String, String> dataToSend
    private void sendSensorData(SensorData sensorData) {
        // Convert numeric values to String
        String temperature = String.valueOf(sensorData.getTemperature());
        String altitude = String.valueOf(sensorData.getAltitude());
        String pressure = String.valueOf(sensorData.getPressure());
        String uvIndex = sensorData.getUvIndexData().isEmpty() ? "0" : sensorData.getUvIndexData().get(sensorData.getUvIndexData().size() - 1);

        // Create a map of the data to send
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("temperature", temperature);
        dataToSend.put("altitude", altitude);
        dataToSend.put("pressure", pressure);
        dataToSend.put("uv_index", uvIndex);

        // Log the data being sent
        Log.i(TAG, "Sending Sensor Data: " + dataToSend);

        // Use Retrofit to send the data
        Retrofit retrofit = ApiClient.getClient("https://bison-amused-minnow.ngrok-free.app/api/sensordata/");
        SensorService service = retrofit.create(SensorService.class);
        Call<Void> call = service.sendSensorData(dataToSend);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "Sensor data sent successfully");
                } else {
                    Log.e(TAG, "Failed to send sensor data: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Failed to send sensor data", t);
            }
        });
    }

    private void fetchUvIndex(Location location) {
        Retrofit retrofit = ApiClient.getClient("https://currentuvindex.com/");
        UVIndexService uvIndexService = retrofit.create(UVIndexService.class);
//        Call<UVIndexResponse> call = uvIndexService.getCurrentUvIndex(-6.9419747, 107.7241992);
        Call<UVIndexResponse> call = uvIndexService.getCurrentUvIndex(location.getLatitude(), location.getLongitude());

        call.enqueue(new Callback<UVIndexResponse>() {
            @Override
            public void onResponse(Call<UVIndexResponse> call, Response<UVIndexResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UVIndexResponse uvIndexResponse = response.body();
                    double maxUVIndex = findMaxUvIndex(uvIndexResponse.getForecast());
                    double currentUvIndex = uvIndexResponse.getNow().getUvi();
                    updateUVIndexAPI(currentUvIndex);
                    updateMaxUvIndexTextView(maxUVIndex);
//                    populateUvForecast(uvIndexResponse.getForecast());
                } else {
                    Log.e(TAG, "Error fetching UV index: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<UVIndexResponse> call, Throwable t) {
                Log.e(TAG, "Failed to fetch UV index", t);
            }
        });
    }

    private double findMaxUvIndex(List<UVIndexResponse.UVForecast> forecasts) {
        double maxUv = 0.0;
        for (UVIndexResponse.UVForecast forecast : forecasts) {
            if (forecast.getUvi() > maxUv) {
                maxUv = forecast.getUvi();
            }
        }
        return maxUv;
    }

    private void updateMaxUvIndexTextView(double maxUv) {
        runOnUiThread(() -> {
            if (maxUVView != null) {
                maxUVView.setText(String.format(Locale.getDefault(), "%.1f", maxUv));
            }
            // Update the ellipse color based on the max UV index
            updateMaxEllipseColor((float) maxUv); // Add this line
        });
    }


    private void fetchWeatherData(Location location) {
        // This assumes you already have latitude and longitude, and API_KEY defined
//        double latitude = -6.9419747;  // Example latitude
//        double longitude = 107.7241992;  // Example longitude

        Retrofit retrofit = ApiClient.getClient("https://api.openweathermap.org/data/2.5/");
        OpenWeatherService service = retrofit.create(OpenWeatherService.class);
//        Call<OpenWeatherResponse> call = service.getWeather(latitude, longitude, API_KEY);
//        Call<OpenWeatherResponse> call = service.getWeather(latitude, longitude, API_KEY);
        Call<OpenWeatherResponse> call = service.getWeather(location.getLatitude(), location.getLongitude(), API_KEY);


        call.enqueue(new Callback<OpenWeatherResponse>() {
            @Override
            public void onResponse(Call<OpenWeatherResponse> call, Response<OpenWeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body());
                } else {
                    Log.e(TAG, "Failed to retrieve weather data: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<OpenWeatherResponse> call, Throwable t) {
                Log.e(TAG, "Weather data fetch failed: ", t);
            }
        });
    }





    @SuppressLint("MissingPermission")
    private void getUserLocation() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                updateLocationText(location);
                fetchWeatherData(location);
                fetchUvIndex(location);

            } else {
                // Handle location is null
                Log.e(TAG, "Location is null, retrying to fetch...");
                getUserLocation(); // Retry fetching location
            }
        });
    }

    private void updateUvProtectionAdvice(float uvIndex) {
        runOnUiThread(() -> {
            TextView sunscreenTextView = findViewById(R.id.sunscreenText);
            Log.d(TAG, "Updating UV protection advice for UV index: " + uvIndex);
            if (sunscreenTextView != null) {
                if (uvIndex <= 2) {
                    sunscreenTextView.setText("Low UV Index: SPF 15+ recommended");
                } else if (uvIndex <= 5) {
                    sunscreenTextView.setText("Moderate UV Index: SPF 30+ recommended");
                } else if (uvIndex <= 7) {
                    sunscreenTextView.setText("High UV Index: SPF 50+ recommended");
                } else if (uvIndex <= 10) {
                    sunscreenTextView.setText("Very High UV Index: Use SPF 50+ and seek shade");
                } else {
                    sunscreenTextView.setText("Extreme UV Index: Avoid sun exposure, use SPF 50+");
                }
            } else {
                Log.e(TAG, "sunscreenTextView is not initialized");
            }
        });
    }



    public void onUvIndexUpdated(float uvIndex) {
        runOnUiThread(() -> {
            updateUvProtectionAdvice(uvIndex);
        });
    }

    private void updateLocationText(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        // Use Geocoder to get address from latitude and longitude
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                String cityName = addresses.get(0).getLocality();
                locationTextView.setText(cityName);
            } else {
                locationTextView.setText("Location not found");
            }
        } catch (IOException e) {
            e.printStackTrace();
            locationTextView.setText("Location not found");
        }
    }
    private void updateTime() {
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        timeTextView.setText(currentTime);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable); // Stop the Runnable when the activity is destroyed
    }

    private void bleCheck() {
        if (ActivityCompat.checkSelfPermission(this, BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{BLUETOOTH}, REQUEST_BLUETOOTH_ID);
        }
        if (ActivityCompat.checkSelfPermission(this, BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{BLUETOOTH_ADMIN}, REQUEST_BLUETOOTH_ADMIN_ID);
        }
    }

    private void locationCheck() {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, REQUEST_LOCATION_ID);
        }
    }

    private void checkPermissions() {
        if (!hasPermissions(BLUETOOTH, BLUETOOTH_ADMIN, ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this, new String[]{BLUETOOTH, BLUETOOTH_ADMIN, ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_ID);
        }
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            getUserLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation();
            } else {
                // Permission denied, handle accordingly
            }
        }
    }

    private boolean hasPermissions(String... permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @SuppressLint("MissingPermission")
    private void connectToDevice(String address) {
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found. Unable to connect.");
            return;
        }
        bluetoothGatt = device.connectGatt(this, false, gattCallback, BluetoothDevice.TRANSPORT_LE);
    }
    @SuppressLint("MissingPermission")
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "Connected to GATT server.");
                Log.i(TAG, "Attempting to start service discovery:");
                bluetoothGatt.discoverServices();

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "Disconnected from GATT server.");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "Services discovered.");
                enableNotifications(gatt);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }



        // Handle the descriptor write callback to continue the chain
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "Descriptor write successful for " + descriptor.getCharacteristic().getUuid().toString());
            } else {
                Log.e(TAG, "Failed to write descriptor for " + descriptor.getCharacteristic().getUuid().toString());
            }
            enableNextNotification(gatt);  // Continue with the next characteristic in the queue
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            UUID characteristicUUID = characteristic.getUuid();
            Log.i(TAG, "Characteristic changed: " + characteristicUUID.toString());
            String value = characteristic.getStringValue(0);

            if (TEMPERATURE_UUID.equals(characteristicUUID)) {
                String temperature = characteristic.getStringValue(0);
//                SensorData sensorData = new SensorData(null, null, null, null, null, null, null, null);
                sensorData.getTemperatureData().add(value);
//                sendSensorData(sensorData);
                Log.i(TAG, "Temperature received: " + temperature);
                runOnUiThread(() -> updateTemperature(temperature));
            } else if (ALTITUDE_UUID.equals(characteristicUUID)) {
                String altitude = characteristic.getStringValue(0);
//                SensorData sensorData = new SensorData(null, null, null, null, null, null, null, null);
                sensorData.getAltitudeData().add(value);
//                sendSensorData(sensorData);
                Log.i(TAG, "Altitude received: " + altitude);
                runOnUiThread(() -> updateAltitude(altitude));
            } else if (PRESSURE_UUID.equals(characteristicUUID)) {
                String pressure = characteristic.getStringValue(0);
//                SensorData sensorData = new SensorData(null, null, null, null, null, null, null, null);
                sensorData.getPressureData().add(value);
//                sendSensorData(sensorData);
                Log.i(TAG, "Pressure received: " + pressure);
                runOnUiThread(() -> updatePressure(pressure));
            } else if (UV_INDEX_UUID.equals(characteristicUUID)) {
//                String uvIndex = characteristic.getStringValue(0);
////                SensorData sensorData = new SensorData(null, null, null, null, null, null, null, null);
//                sensorData.getUvIndexData().add(uvIndex);  // Add to collection for averaging
//                final float uvIndexValue = Float.parseFloat(uvIndex);  // Ensure this parsing does not fail
//                Log.d(TAG, "UV Index received: " + uvIndexValue);
//
//
////                sendSensorData(sensorData);
////                Log.i(TAG, "UV Index received: " + uvIndex);
//                runOnUiThread(() -> updateUVIndex(uvIndex));
                String uvIndex = characteristic.getStringValue(0);
                sensorData.getUvIndexData().add(uvIndex);
                try {
                    final float uvIndexValue = Float.parseFloat(uvIndex);  // Parse the UV Index value
                    Log.d(TAG, "UV Index received: " + uvIndexValue);
                    runOnUiThread(() -> {
                        updateUVIndex(uvIndex);  // Update the gauge
                        updateUVStatus(uvIndexValue);  // Update the UV status
                    });
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Failed to parse UV Index value: " + uvIndex, e);
                }


            } else {
                Log.i(TAG, "Received data from an unexpected characteristic: " + characteristicUUID);

            }
            // Determine if it's time to send data
            // Calculate the average UV index before sending
            if (shouldSendData()) {
                sensorData.setTemperature(sensorData.getTemperatureAvg());
                sensorData.setPressure(sensorData.getPressureAvg());
                sensorData.setAltitude(sensorData.getAltitudeAvg());
                sensorData.setUvIndex(sensorData.getUvIndexAvg());  // Ensure this is being calculated and set correctly
                sendSensorData(sensorData);
                resetSensorData(); // Reset data collections for next batch
            }

        }
    };

    // Check if the data collections have reached a certain size or time limit
    private boolean shouldSendData() {
        // Example condition: Check if all lists have at least 5 data points
        return sensorData.getTemperatureData().size() >= 5 &&
                sensorData.getPressureData().size() >= 5 &&
                sensorData.getAltitudeData().size() >= 5 &&
                sensorData.getUvIndexData().size() >= 5;
    }

    // Reset sensor data collections for new data
    private void resetSensorData() {
        sensorData.setTemperatureData(new ArrayList<>());
        sensorData.setPressureData(new ArrayList<>());
        sensorData.setAltitudeData(new ArrayList<>());
        sensorData.setUvIndexData(new ArrayList<>());
    }

    @SuppressLint("MissingPermission")
    private void enableNotifications(BluetoothGatt gatt) {
        BluetoothGattService service = gatt.getService(UUID.fromString(GattAttributes.BAROMETRIC_SERVICE));
        if (service == null) {
            Log.w(TAG, "Barometric service not found!");
            return;
        }

        // Queue the characteristics for enabling notifications
        queueCharacteristic(service, GattAttributes.ALTITUDE_MEASUREMENT);
        queueCharacteristic(service, GattAttributes.TEMPERATURE_MEASUREMENT);
        queueCharacteristic(service, GattAttributes.PRESSURE_MEASUREMENT);
        queueCharacteristic(service, GattAttributes.UV_INDEX_MEASUREMENT);

        // Start enabling notifications for the first characteristic in the queue
        enableNextNotification(gatt);
    }

    private void queueCharacteristic(BluetoothGattService service, String uuidString) {
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(uuidString));
        if (characteristic != null) {
            characteristicQueue.add(characteristic);
        } else {
            Log.w(TAG, "Characteristic not found: " + uuidString);
        }
    }

    @SuppressLint("MissingPermission")
    private void enableNextNotification(BluetoothGatt gatt) {
        if (!characteristicQueue.isEmpty()) {
            BluetoothGattCharacteristic characteristic = characteristicQueue.poll();
            if (characteristic != null) {
                gatt.setCharacteristicNotification(characteristic, true);
                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
                if (descriptor != null) {
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    gatt.writeDescriptor(descriptor);
                } else {
                    Log.w(TAG, "Descriptor not found for " + characteristic.getUuid().toString());
                    enableNextNotification(gatt);  // Continue with the next characteristic
                }
            }
        }
    }



    private void updateTemperature(String temperature) {
        runOnUiThread(() -> {
            if (temperatureView != null) {
                temperatureView.setText(temperature + " °C");
            } else {
                Log.w(TAG, "Temperature TextView is not initialized.");
            }
        });
    }

    private void updateAltitude(String altitude) {
        runOnUiThread(() -> {
            if (altitudeView != null) {
                altitudeView.setText(altitude + " m");
            } else {
                Log.w(TAG, "Altitude TextView is not initialized.");
            }
        });
    }

    private void updatePressure(String pressure) {
        runOnUiThread(() -> {
            if (pressureView != null) {
                pressureView.setText(pressure + " hPa");
            } else {
                Log.w(TAG, "Pressure TextView is not initialized.");
            }
        });
    }

//    private void updateUVStatus(float uvIndexValue) {
//        String uvStatus;
//        int color; // Add this line
//
//        if (uvIndexValue < 3) {
//            uvStatus = "Low UV";
//            color = Color.GREEN; // Add this line
//        } else if (uvIndexValue < 6) {
//            uvStatus = "Moderate UV";
//            color = Color.YELLOW; // Add this line
//
//        } else if (uvIndexValue < 8) {
//            uvStatus = "High UV";
//            color = Color.rgb(255, 165, 0); // Add this line for orange
//
//        } else if (uvIndexValue < 11) {
//            uvStatus = "Very High UV";
//            color = Color.RED; // Add this line
//
//        } else {
//            uvStatus = "Extreme UV";
//            color = Color.rgb(153, 50, 204); // Add this line for dark orchid
//
//        }
//        uvStatusTextView.setText(uvStatus);
//        updateUvProtectionAdvice(uvIndexValue); // Update the advice based on the new UV index value
//        // Update the ellipse color based on the UV status
//        if (ellipseView != null) {
//            ellipseView.setColorFilter(color); // Add this line
//        }
//
//    }
private void updateUVStatus(float uvIndexValue) {
    Log.d(TAG, "Preparing to update UV status for index: " + uvIndexValue);

    runOnUiThread(() -> {
        String uvStatus;
        int color;

        if (uvIndexValue < 3) {
            uvStatus = "Low UV";
            color = Color.GREEN;
        } else if (uvIndexValue < 6) {
            uvStatus = "Moderate UV";
            color = Color.YELLOW;
        } else if (uvIndexValue < 8) {
            uvStatus = "High UV";
            color = Color.rgb(255, 165, 0); // Orange
        } else if (uvIndexValue < 11) {
            uvStatus = "Very High UV";
            color = Color.RED;
        } else {
            uvStatus = "Extreme UV";
            color = Color.rgb(153, 50, 204); // Dark orchid
        }

        uvStatusTextView.setText(uvStatus);
        if (ellipseView != null) {
            ellipseView.setColorFilter(color);  // Update color filter
        }
    });
}



    private void updateMaxEllipseColor(float uvIndexValue) {
        runOnUiThread(() -> {
            int color;

            if (uvIndexValue < 3) {
                color = Color.GREEN;
            } else if (uvIndexValue < 6) {
                color = Color.YELLOW;
            } else if (uvIndexValue < 8) {
                color = Color.rgb(255, 165, 0); // Orange
            } else if (uvIndexValue < 11) {
                color = Color.RED;
            } else {
                color = Color.rgb(153, 50, 204); // Dark orchid
            }

            if (MaxUVIndexStatus != null) {
                MaxUVIndexStatus.setColorFilter(color);
            }
        });
    }


    private void updateGaugeColor(float uvIndexValue) {
        runOnUiThread(() -> {
            int color;
            if (uvIndexValue >= 8) {
                uvIndexGauge.setNeedleColor(Color.RED); // High UV index
                color = Color.RED;
            } else if (uvIndexValue >= 6) {
                uvIndexGauge.setNeedleColor(Color.YELLOW); // Moderate UV index
                color = Color.YELLOW;
            } else if (uvIndexValue >= 3) {
                uvIndexGauge.setNeedleColor(Color.rgb(255, 165, 0)); // Orange
                color = Color.rgb(255, 165, 0);
            } else {
                uvIndexGauge.setNeedleColor(Color.GREEN); // Low UV index
                color = Color.GREEN;
            }
        });
    }



    // Method for BLE String input from esp32
    private void updateUVIndex(String uvIndex) {
        runOnUiThread(() -> {
            if (uvIndexGauge != null) {
                try {
                    float uvIndexValue = Float.parseFloat(uvIndex);
                    uvIndexGauge.setValue(uvIndexValue); // Update gauge
                    updateUvProtectionAdvice(uvIndexValue);

//                     Change the gauge color based on the UV index value
                    updateUvProtectionAdvice(uvIndexValue); // Update the advice based on the new gauge value
                    onUvIndexUpdated(uvIndexValue); // Update the UV protection advice based on the new UV index value

                    // Change the gauge color based on the UV index value
                    updateGaugeColor(uvIndexValue);

                    uvIndexGauge.invalidate(); // Refresh the gauge to update the view
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Failed to parse UV index value", e);
                    uvIndexGauge.setValue(0); // Set to 0 or another default value
                    updateUVStatus(0);

                }
            }
        });
    }



    // Method from API response
    private void updateUVIndexAPI(double uvIndex) {
        runOnUiThread(() -> {
            if (uvIndexGauge != null) {
                float uvIndexValue = (float) uvIndex;
                uvIndexGauge.setValue(uvIndexValue);
                updateUVStatus(uvIndexValue);
                updateGaugeColor(uvIndexValue);
                uvIndexGauge.invalidate();
            }
        });
    }


    private void updateUI(OpenWeatherResponse response) {
        if (response.getMain() != null) {
            double tempInCelsius = response.getMain().getTemp() - 273.15;  // Convert from Kelvin to Celsius
            if (temperatureView != null) {
                temperatureView.setText(String.format(Locale.getDefault(), "%.1f°C", tempInCelsius)); // Format to 1 decimal place
            }
            if (pressureView != null) {
                pressureView.setText(response.getMain().getPressure() + " hPa");
            }
            if (altitudeView != null) {
                altitudeView.setText(response.getMain().getSeaLevel() + " m");
            }
            if (locationTextView != null) {
                locationTextView.setText(response.getName() + ", " + response.getSys().getCountry());
            }
        }

        if (response.getWind() != null && windSpeedView != null) {
            windSpeedView.setText(response.getWind().getSpeed() + " ms");
        }
    }



    public void openBleScanner() {
        Intent i = new Intent(this, RecyclerBleDeviceActivity.class);
        bleScannerLauncher.launch(i);
    }

    @SuppressLint("WrongConstant")
    public void initializeLayout() {
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        final View actionView = getSupportActionBar().getCustomView();

        scanView = actionView.findViewById(R.id.scan);
        scanProgressBar = actionView.findViewById(R.id.scanInProgress);
        reconnectView = actionView.findViewById(R.id.reconnect);

        altitudeView = findViewById(R.id.altitude);
        pressureView = findViewById(R.id.pressure);
        temperatureView = findViewById(R.id.temperature);
//        uvIndexView = findViewById(R.id.uvIndex);
        uvIndexGauge = findViewById(R.id.uvIndexGauge);
        uvStatusTextView = findViewById(R.id.uvIndexStatus);

        maxUVView = findViewById(R.id.MaxUVIndexValue);
        windSpeedView = findViewById(R.id.windspeedValue);

        scanView.setOnClickListener(v -> openBleScanner());

        reconnectView.setOnClickListener(view -> {
            scanProgressBar.setVisibility(View.VISIBLE);
            scanView.setVisibility(View.GONE);
            reconnectView.setVisibility(View.GONE);
        });
    }
}