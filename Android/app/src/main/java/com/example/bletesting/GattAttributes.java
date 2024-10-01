package com.example.bletesting;

import java.util.HashMap;

public class GattAttributes {
    private static HashMap<String, String> attributes = new HashMap<>();

    public static String GENERIC_ACCESS = "00001800-0000-1000-8000-00805f9b34fb";
    public static String GENERIC_ATTRIBUTE = "00001801-0000-1000-8000-00805f9b34fb";
    public static String DEVICE_INFORMATION_SERVICE = "0000180a-0000-1000-8000-00805f9b34fb";
//    public static String UV_INDEX_SERVICE = "00000011-d501-4895-afbe-c80000823c94";
    public static String BAROMETRIC_SERVICE = "657501c3-c422-44bf-93d8-c66ea2a5e005";
//    public static String UV_INDEX_MEASUREMENT = "00000012-d501-4895-afbe-c80000823c94";
    public static String ALTITUDE_MEASUREMENT = "00000013-d501-4895-afbe-c80000823c95";
    public static String TEMPERATURE_MEASUREMENT = "00000011-d501-4895-afbe-c80000823c95";
    public static String PRESSURE_MEASUREMENT = "00000012-d501-4895-afbe-c80000823c95";
    public static String UV_INDEX_MEASUREMENT = "00000015-d501-4895-afbe-c80000823c95";

    public static String TIME_STAMP_MEASUREMENT = "00000016-d501-4895-afbe-c80000823c95";

//    public static String SERVICE_UUID = "657501c3-c422-44bf-93d8-c66ea2a5e005";
    public static String DEVICE_NAME = "00002a00-0000-1000-8000-00805F9B34FB";
    public static String PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS = "00002a04-0000-1000-8000-00805f9b34fb";
    public static String MODEL_NUMBER_STRING = "00002a24-0000-1000-8000-00805f9b34fb";
    public static String SERIAL_NUMBER_STRING = "00002a25-0000-1000-8000-00805f9b34fb";
    public static String FIRMWARE_REVISION_STRING = "00002a26-0000-1000-8000-00805f9b34fb";
    public static String HARDWARE_REVISION_STRING = "00002a27-0000-1000-8000-00805f9b34fb";
    public static String SOFTWARE_REVISION_STRING = "00002a28-0000-1000-8000-00805f9b34fb";
    public static String MANUFACTURER_NAME_STRING = "00002a29-0000-1000-8000-00805f9b34fb";
    public static String SERVICE_CHANGED = "00002a05-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    static {
        attributes.put(GENERIC_ACCESS, "Generic Access");
        attributes.put(GENERIC_ATTRIBUTE, "Generic Attribute");
        attributes.put(DEVICE_INFORMATION_SERVICE, "Device Information Service");

//        attributes.put(UV_INDEX_MEASUREMENT, "UV Index Measurement");
        // Sensors UUIDs
        attributes.put(BAROMETRIC_SERVICE, "Barometric Service");
        attributes.put(ALTITUDE_MEASUREMENT, "Altitude Measurement");
        attributes.put(TEMPERATURE_MEASUREMENT, "Temperature Measurement");
        attributes.put(PRESSURE_MEASUREMENT, "Pressure Measurement");
        attributes.put(UV_INDEX_MEASUREMENT, "UV Index Measurement");
//        attributes.put(TIME_STAMP_MEASUREMENT, "Time Stamp Measurement");


        // Device Information UUIDs
        attributes.put(DEVICE_NAME, "Device Name");
        attributes.put(PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS, "Peripheral Preferred Connection Parameters");
        attributes.put(MODEL_NUMBER_STRING, "Model Number String");
        attributes.put(SERIAL_NUMBER_STRING, "Serial Number String");
        attributes.put(FIRMWARE_REVISION_STRING, "Firmware Revision String");
        attributes.put(HARDWARE_REVISION_STRING, "Hardware Revision String");
        attributes.put(SOFTWARE_REVISION_STRING, "Software Revision String");
        attributes.put(MANUFACTURER_NAME_STRING, "Manufacturer Name String");
        attributes.put(SERVICE_CHANGED, "Service Changed");
//        attributes.put(SERVICE_UUID, "Service UUID");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
