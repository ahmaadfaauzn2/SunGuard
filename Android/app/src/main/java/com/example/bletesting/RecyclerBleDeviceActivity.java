    package com.example.bletesting;


    import static android.content.ContentValues.TAG;

    import android.Manifest;
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
    import android.bluetooth.le.BluetoothLeScanner;
    import android.bluetooth.le.ScanCallback;
    import android.bluetooth.le.ScanResult;
    import android.content.Context;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.content.pm.PackageManager;
    import android.os.Bundle;
    import android.os.Handler;
    import android.os.Looper;
    import android.util.Log;
    import android.view.View;
    import android.widget.ImageView;
    import android.widget.ProgressBar;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.activity.result.ActivityResultLauncher;
    import androidx.activity.result.contract.ActivityResultContracts;
    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.annotation.RequiresApi;
    import androidx.appcompat.app.ActionBar;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.app.ActivityCompat;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import java.util.List;
    import java.util.UUID;

    public class RecyclerBleDeviceActivity extends AppCompatActivity implements BleRecyclerAdapter.ItemClickListener {

        private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
        private BleRecyclerAdapter recyclerAdapter;
        private BluetoothAdapter bleAdapter;
        public static final UUID BAROMETRIC_SERVICE = UUID.fromString(GattAttributes.BAROMETRIC_SERVICE);
        public static final UUID TEMPERATURE_UUID = UUID.fromString(GattAttributes.TEMPERATURE_MEASUREMENT);
        public static final UUID ALTITUDE_UUID = UUID.fromString(GattAttributes.ALTITUDE_MEASUREMENT);

        public static final UUID PRESSURE_UUID = UUID.fromString(GattAttributes.PRESSURE_MEASUREMENT);

        public static final UUID UV_INDEX_UUID = UUID.fromString(GattAttributes.UV_INDEX_MEASUREMENT);

        public static final UUID TIME_STAMP_UUID = UUID.fromString(GattAttributes.TIME_STAMP_MEASUREMENT);

        private BluetoothGattCharacteristic altitudeCharacteristic;
        private BluetoothGattCharacteristic temperatureCharacteristic;
        private BluetoothGattCharacteristic pressureCharacteristic;

        private BluetoothGattCharacteristic uvIndexCharacteristic;

        private BluetoothGattCharacteristic timeStampCharacteristic;
        private BluetoothLeScanner bleScanner;

        private boolean scanning;
        private Handler handler;
        private TextView scanView;
        private ProgressBar scanProgressBar;
        private ImageView reconnectView;
        private ImageView stopView;
        private static final long SCAN_PERIOD = 10000;
        private static final int REQUEST_ENABLE_BT = 1;
        private ScanCallback leScanCallback;

        private ActivityResultLauncher<Intent> enableBtIntentLauncher;
        private static final int MAX_RETRY_COUNT = 3;
        private int retryCount = 0;
        private BluetoothDevice selectedDevice;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_ble_recycler_device);

            handler = new Handler(Looper.getMainLooper());

            initializeLayout();

            final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            bleAdapter = bluetoothManager.getAdapter();
            if (bleAdapter != null) {
                bleScanner = bleAdapter.getBluetoothLeScanner();
            }

            leScanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    runOnUiThread(() -> {
                        BluetoothDevice device = result.getDevice();
                        recyclerAdapter.addDevice(device);
                        recyclerAdapter.notifyDataSetChanged();
                    });
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    runOnUiThread(() -> {
                        for (ScanResult result : results) {
                            BluetoothDevice device = result.getDevice();
                            recyclerAdapter.addDevice(device);
                        }
                        recyclerAdapter.notifyDataSetChanged();
                    });
                }

                @Override
                public void onScanFailed(int errorCode) {
                    runOnUiThread(() -> Toast.makeText(RecyclerBleDeviceActivity.this, "Scan failed with error: " + errorCode, Toast.LENGTH_SHORT).show());
                }
            };
        }
        private void saveTemperatureData(float temperature) {
            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.agm_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("temperature", String.valueOf(temperature));
            editor.apply();
        }


        @Override
        public void onItemClick(View view, int position) {
            onPause(); // Pause any ongoing operations.
            invalidateScanMenu(); // Refresh scan-related UI components.

            BluetoothDevice device = recyclerAdapter.getDevice(position);
            if (device == null) {
                Toast.makeText(this, "No device selected", Toast.LENGTH_SHORT).show();
                return; // Exit if no device is found.
            }

            selectedDevice = device;

            // Check Bluetooth connection permission before trying to connect.
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
            } else {
                connectToDevice(device);
            }

            // Save device information in SharedPreferences.
            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.ble_device_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor prefBleDeviceEditor = sharedPref.edit();
            prefBleDeviceEditor.putString("name", device.getName() == null ? "Unknown Device" : device.getName());
            prefBleDeviceEditor.putString("address", device.getAddress());
            prefBleDeviceEditor.apply();

            // Set result and finish
            Intent resultIntent = new Intent();
            resultIntent.putExtra("device_name", device.getName());
            resultIntent.putExtra("device_address", device.getAddress());
            setResult(Activity.RESULT_OK, resultIntent);
            finish();



            // Stop scanning before moving to the next activity.
            if (scanning) {
                bleScanner.stopScan(leScanCallback);
                scanning = false;
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == REQUEST_ENABLE_BT && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, try to connect again if a device was selected
                if (selectedDevice != null) {
                    connectToDevice(selectedDevice);
                }
            } else {
                Toast.makeText(this, "Bluetooth connection permission is required", Toast.LENGTH_SHORT).show();
            }
        }

        @SuppressLint("MissingPermission")
        private void connectToDevice(final BluetoothDevice device) {
            if (retryCount >= MAX_RETRY_COUNT) {
                Log.d("BLE", "Max retries reached. Giving up on connection.");
                return;
            }

            Log.d("BLE", "Attempting to connect. Retry count: " + retryCount);
            final BluetoothGatt bluetoothGatt = device.connectGatt(this, false, new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        gatt.discoverServices();
                    } else if (status == 133) {
                        retryCount++;
                        Log.d("BLE", "Connection failed with status 133. Retrying...");
                        handler.postDelayed(() -> {
                            gatt.close();
                            connectToDevice(device);
                        }, 2000 * retryCount);  // Increasing delay with each retry
                    } else {
                        Log.d("BLE", "Connection failed with status: " + status);
                        gatt.close();
                    }
                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        BluetoothGattService service = gatt.getService(BAROMETRIC_SERVICE);
                        if (service != null) {
                            temperatureCharacteristic = service.getCharacteristic(TEMPERATURE_UUID);
                            altitudeCharacteristic = service.getCharacteristic(ALTITUDE_UUID);
                            pressureCharacteristic = service.getCharacteristic(PRESSURE_UUID); // Add this line
                            uvIndexCharacteristic = service.getCharacteristic(UV_INDEX_UUID); // Add this line
//                            timeStampCharacteristic = service.getCharacteristic(TIME_STAMP_UUID); // Add this line


                            if (temperatureCharacteristic != null && altitudeCharacteristic != null && pressureCharacteristic != null) { // Update this line
                                gatt.setCharacteristicNotification(temperatureCharacteristic, true);
                                BluetoothGattDescriptor tempDescriptor = temperatureCharacteristic.getDescriptor(UUID.fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
                                tempDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                gatt.writeDescriptor(tempDescriptor);

                                gatt.setCharacteristicNotification(altitudeCharacteristic, true);
                                BluetoothGattDescriptor altDescriptor = altitudeCharacteristic.getDescriptor(UUID.fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
                                altDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                gatt.writeDescriptor(altDescriptor);

                                gatt.setCharacteristicNotification(pressureCharacteristic, true); // Add this line
                                BluetoothGattDescriptor pressureDescriptor = pressureCharacteristic.getDescriptor(UUID.fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG)); // Add this line
                                pressureDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE); // Add this line
                                gatt.writeDescriptor(pressureDescriptor); // Add this line

                                gatt.setCharacteristicNotification(uvIndexCharacteristic, true); // Add this line
                                BluetoothGattDescriptor uvIndexDescriptor = uvIndexCharacteristic.getDescriptor(UUID.fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG)); // Add this line
                                uvIndexDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE); // Add this line
                                gatt.writeDescriptor(uvIndexDescriptor); // Add this line
//

                                // Log for pressure
                                Log.d(TAG, "Pressure: " + pressureCharacteristic.getValue());

                            }
                        }
                    }
                }




            }, BluetoothDevice.TRANSPORT_LE);  // Specify LE transport

            if (bluetoothGatt == null) {
                Log.e("BLE", "Failed to create BluetoothGatt instance.");
            }
        }

        @Override
        protected void onResume() {
            super.onResume();
            recyclerAdapter.clear();
            scanBleDevice(true);
        }

        @Override
        protected void onPause() {
            super.onPause();
            scanBleDevice(false);
        }

        private void checkPermissionsAndScan() {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                }, REQUEST_BLUETOOTH_PERMISSIONS);
            } else {
                scanBleDevice(true);
            }
        }

        // Method to start or stop scanning for BLE devices.
        private void scanBleDevice(final boolean enable) {
            if (enable) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
                    scanning = true;
                    handler.postDelayed(() -> {
                        scanning = false;
                        if (bleScanner != null) {
                            bleScanner.stopScan(leScanCallback);
                        }
                        invalidateScanMenu();
                    }, SCAN_PERIOD);
                    if (bleScanner != null) {
                        bleScanner.startScan(leScanCallback);
                    }
                } else {
                    checkPermissionsAndScan();  // Request permissions if not granted
                }
            } else {
                scanning = false;
                if (bleScanner != null && ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
                    bleScanner.stopScan(leScanCallback);
                }
                invalidateScanMenu();
            }
        }

        // Refresh UI elements related to scanning.
        private void invalidateScanMenu() {
            scanProgressBar.setVisibility(View.GONE);
            stopView.setVisibility(View.GONE);
            scanView.setVisibility(View.VISIBLE);
        }

        @SuppressLint("WrongConstant")
        public void initializeLayout() {
            // Initialize the action bar
            this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setCustomView(R.layout.action_bar);

            // On-click listeners initialized
            final View actionView = getSupportActionBar().getCustomView();

            scanView = actionView.findViewById(R.id.scan);
            stopView = actionView.findViewById(R.id.stop);
            scanProgressBar = actionView.findViewById(R.id.scanInProgress);

            scanView.setVisibility(View.GONE);
            stopView.setVisibility(View.VISIBLE);
            scanProgressBar.setVisibility(View.VISIBLE);

            scanView.setOnClickListener(v -> {
                onResume();
                scanProgressBar.setVisibility(View.VISIBLE);
                scanView.setVisibility(View.GONE);
                stopView.setVisibility(View.VISIBLE);
            });

            stopView.setOnClickListener(v -> {
                onPause();
                invalidateScanMenu();
            });

            RecyclerView recyclerView = findViewById(R.id.bleRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerAdapter = new BleRecyclerAdapter(this);
            recyclerAdapter.setClickListener(this);
            recyclerView.setAdapter(recyclerAdapter);
        }
    }
