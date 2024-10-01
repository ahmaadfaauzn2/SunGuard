#include <Arduino.h>
#include <Wire.h>
// #include <Adafruit_MPU6050.h>
// #include <Adafruit_Sensor.h>
#include "MS5611.h"
#include <math.h>  // For altitude calculation
#include "BLEDevice.h"
#include "BLEServer.h"
#include "BLEUtils.h"
#include "BLE2902.h"
// #include <time.h>

// Define LED Pin
#define LED_PIN 5
#define LED_PIN_2 15
#define LED_PIN_3 14
#define LED_PIN_4 19
#define LED_PIN_5 18

// Define the Bluetooth Serial Name
const char *bleName = "ESP32 DOIT DEVKIT V1";

// Define the received text and the time of the last message
String receivedText = "";
unsigned long lastMessageTime = 0;

// Define the UUID for the BLE Service and the BLE Characteristic
#define SERVICE_UUID "657501c3-c422-44bf-93d8-c66ea2a5e005"
#define CHARACTERISTIC_UUID_RX "0e33be8e-7771-4aea-8140-113312f2c468"
#define CHARACTERISTIC_UUID_TX "166a3a0e-9f36-48a9-8627-f1f76f7ec075"
#define TEMPERATURE_UUID "00000011-d501-4895-afbe-c80000823c95"
#define PRESSURE_UUID "00000012-d501-4895-afbe-c80000823c95"
#define ALTITUDE_UUID "00000013-d501-4895-afbe-c80000823c95"
#define COMBINED_SENSOR_UUID "00000014-d501-4895-afbe-c80000823c95"
#define UV_INDEX_UUID "00000015-d501-4895-afbe-c80000823c95"
// #define TIME_STAMP_UUID "00000016-d501-4895-afbe-c80000823c95"


// Define the Bluetooth characteristic
BLECharacteristic *pCharacteristic;

// // MPU6050 Configuration  
// Adafruit_MPU6050 mpu;

// MS5611 Configuration
#define MS5611_ADDRESS 0x77
MS5611 ms5611(MS5611_ADDRESS);

// Default sea level pressure
#define DEFAULT_SEA_LEVEL_PRESSURE 1010

// Update interval
const unsigned long updateInterval = 1000; 
unsigned long lastUpdateTime = 0;

// Global variables for characteristic pointers
BLECharacteristic *pXAccChar, *pYAccChar, *pZAccChar, *pXGyroChar, *pYGyroChar, *pZGyroChar, *pAccTimeChar;

// Global variables for sensor data characteristics
BLECharacteristic *pTempChar;
BLECharacteristic *pPressChar;
BLECharacteristic *pAltChar;
BLECharacteristic *pUVChar;
BLECharacteristic *pCombinedChar;
BLECharacteristic *pTimeStampChar;



// Sensor UV
int sensorValue;
float sensorVoltage;

// Define the BLE server callbacks
class MyServerCallbacks : public BLEServerCallbacks {
  void onConnect(BLEServer *pServer) {
    Serial.println("Connected");
  }
  void onDisconnect(BLEServer *pServer) {
    Serial.println("Disconnected");
  }
};  

// Define the BLE characteristic callbacks
class MyCharacteristicCallbacks : public BLECharacteristicCallbacks {
  void onWrite(BLECharacteristic *pCharacteristic) {
    // When data is received, get the data and save it to receivedText, and record the time
    std::string value = pCharacteristic->getValue();
    receivedText = String(value.c_str());
    lastMessageTime = millis();
    Serial.print("Received: ");
    Serial.println(receivedText);
  }
};

void setupBLE() {
    BLEDevice::init(bleName); // Initialize the BLE device
    BLEServer *pServer = BLEDevice::createServer(); // Create the BLE server
    pServer->setCallbacks(new MyServerCallbacks()); // Set the BLE server callbacks

    BLEService *pService = pServer->createService(SERVICE_UUID);

    // Create characteristics for each sensor reading
    pTempChar = pService->createCharacteristic(
                  TEMPERATURE_UUID,
                  BLECharacteristic::PROPERTY_NOTIFY
                );
    pTempChar->addDescriptor(new BLE2902());

    pPressChar = pService->createCharacteristic(
                  PRESSURE_UUID,
                  BLECharacteristic::PROPERTY_NOTIFY
                );
    pPressChar->addDescriptor(new BLE2902());

    pAltChar = pService->createCharacteristic(
                ALTITUDE_UUID,
                BLECharacteristic::PROPERTY_NOTIFY
              );
    pAltChar->addDescriptor(new BLE2902());

    pUVChar = pService->createCharacteristic(
                UV_INDEX_UUID,
                BLECharacteristic::PROPERTY_NOTIFY
              );
    pUVChar->addDescriptor(new BLE2902());

    


    // Create a combined sensor data characteristic
    pCombinedChar = pService->createCharacteristic(
                      COMBINED_SENSOR_UUID,
                      BLECharacteristic::PROPERTY_NOTIFY
                    );
    pCombinedChar->addDescriptor(new BLE2902());


    pService->start(); // Start the BLE service
    pServer->getAdvertising()->start(); // Start advertising
    Serial.println("BLE setup complete, waiting for a client connection...");
}

void printLocalTime() {
  struct tm timeinfo;
  if(!getLocalTime(&timeinfo)){
    Serial.println("Failed to obtain time");
    return;
  }
  Serial.println(&timeinfo, "%A, %B %d %Y %H:%M:%S");
}

void setup() {
  Serial.begin(9600);
  pinMode(LED_PIN, OUTPUT);
  pinMode(LED_PIN_2, OUTPUT);
  pinMode(LED_PIN_3, OUTPUT);
  pinMode(LED_PIN_4, OUTPUT);
  pinMode(LED_PIN_5, OUTPUT);
  pinMode(36, INPUT); // Set GPIO 36 as an input
  setupBLE();
  while (!Serial); // Wait for Serial port to connect
  Serial.println("Starting...");
  Wire.begin(21,22); // Use default SDA and SCL pins

  // Initialize MS5611
  if (!ms5611.begin()) {
    Serial.println("Failed to initialize MS5611");
    while(1);
  } else {
    Serial.println("MS5611 initialized");
  }


}





void loop() {
  // Read GUVA-S12SD UV sensor
  int sensorValue = analogRead(36); // Read the analog input on GPIO 36
  float sensorVoltage = sensorValue * (3.3 / 4095.0); // Convert the reading to voltage
  float uvIndex = sensorVoltage / 0.1; // Convert the voltage to UV index (0.1V per UV index)
  String uvIndexStr = String(uvIndex, 2); // Convert to string with 2 decimal places

      // Serial.println("Sent UV Index data over BLE: " + uvDataString);
  // Send UV data over BLE
    if (pUVChar != nullptr) {
    pUVChar->setValue(uvIndexStr.c_str());
    pUVChar->notify();
    Serial.println("Sent UV Index data over BLE: " + uvIndexStr);
  } 

  // // Prepare UV data string
  //   String uvDataString = String(uvIndex);
  //   pUVChar->setValue(uvDataString.c_str());
  //   pUVChar->notify();

  Serial.print("Sensor Voltage = ");
  Serial.print(sensorVoltage);
  Serial.print(" V, UV Index: ");
  Serial.println(uvIndex);

  // Reset all LEDs
  digitalWrite(LED_PIN, LOW);
  digitalWrite(LED_PIN_2, LOW);
  digitalWrite(LED_PIN_3, LOW);
  digitalWrite(LED_PIN_4, LOW);
  digitalWrite(LED_PIN_5, LOW);

  // Determine which LEDs should be on based on UV Index
  if (uvIndex < 1) {
    // UV 0: 1 LED blinking
    digitalWrite(LED_PIN, (millis() / 500) % 2); // Toggle LED every 500ms
  } else if (uvIndex <= 2) {
    // UV 1-2: 1 LED on
    digitalWrite(LED_PIN, HIGH);
  } else if (uvIndex <= 5) {
    // UV 3-5: 2 LEDs on
    digitalWrite(LED_PIN, HIGH);
    digitalWrite(LED_PIN_2, HIGH);
  } else if (uvIndex <= 7) {    
    // UV 6-7: 3 LEDs on
    digitalWrite(LED_PIN, HIGH);
    digitalWrite(LED_PIN_2, HIGH);
    digitalWrite(LED_PIN_3, HIGH);
  } else if (uvIndex <= 10) {
    // UV 8-10: 4 LEDs on
    digitalWrite(LED_PIN, HIGH);
    digitalWrite(LED_PIN_2, HIGH);
    digitalWrite(LED_PIN_3, HIGH);
    digitalWrite(LED_PIN_4, HIGH);
  } else {
    // UV 11+: 5 LEDs on
    digitalWrite(LED_PIN, HIGH);
    digitalWrite(LED_PIN_2, HIGH);
    digitalWrite(LED_PIN_3, HIGH);
    digitalWrite(LED_PIN_4, HIGH);
    digitalWrite(LED_PIN_5, HIGH);
  }

  delay(1000); // Short delay to keep loop responsive




  unsigned long currentMillis = millis();

  if (currentMillis - lastUpdateTime >= updateInterval) {
    lastUpdateTime = currentMillis;


   // Read sensor data
    ms5611.read();
    float temperature = ms5611.getTemperature();
    float pressure = ms5611.getPressure();
    float altitude = 44330 * (1.0 - pow(pressure / DEFAULT_SEA_LEVEL_PRESSURE, 0.1903));

  
    
  //Prepare Pressure data string
    String pressDataString = String(pressure, 2);
    pPressChar->setValue(pressDataString.c_str());
    pPressChar->notify();
   

    // Prepare altitude data string 
    String altDataString = String(altitude, 2);
    pAltChar->setValue(altDataString.c_str());
    pAltChar->notify();

    // Send the combined data string over BLE using the temperature characteristic    
    String dataString = String(temperature, 2);
    pTempChar->setValue(dataString.c_str());
    pTempChar->notify();



    Serial.println("Sent temperature data over BLE: " + dataString);
    Serial.println("Sent altitude data over BLE: " + altDataString);
    Serial.println("Sent pressure data over BLE: " + pressDataString);
    // Serial.println("Sent UV Index data over BLE: " + uvDataString);


  

    // Print sensor data to serial monitor
    Serial.println("MS5611 Sensor Data:");
    Serial.print("Temperature: ");
    Serial.print(temperature);
    Serial.println(" °C");
    Serial.print("Pressure: ");
    Serial.print(pressure);
    Serial.println(" mbar");
    Serial.print("Altitude: ");
    Serial.print(altitude);
    Serial.println(" m");


  }
  delay(10); // Short delay to keep loop manageable
}


