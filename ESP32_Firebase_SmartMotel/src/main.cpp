#include <Arduino.h>
#include <WiFi.h>
#include <Update.h>
#include <LittleFS.h>
#include <SD.h>
#include <Firebase_ESP_Client.h>
#include <addons/TokenHelper.h>
#include <addons/RTDBHelper.h>

unsigned long sendDataPrevMillis = 0;

// ESP32 nay la cua so nha 343-72, Phan Xich Long, Quận Phú Nhuận, và các setup liên quan đến thiết bị đã được thực hiện
String path_fan_1 = "Người thuê dịch vụ/user2/Smart Motel/343-72, Phan Xich Long, Quận Phú Nhuận/Control/";
String path_lamp_1 = "Người thuê dịch vụ/user2/Smart Motel/343-72, Phan Xich Long, Quận Phú Nhuận/Control/lamp 1";
volatile bool fan_1_state = false;
volatile bool lamp_1_state = false;

// Config wifi
const char* ssid = "QuocViet2";
const char* password = "22041995";

// Define Firebase variable
#define API_KEY   "AIzaSyCw-l2RzNXpuMtbBNUUoJUMNCo20YkqUMs"
#define RTDB_URL  "https://smart-motel-12139-default-rtdb.firebaseio.com/"
#define auth_email     "cesplasma@gmail.com"
#define auth_password  "123456"

FirebaseData    Firebase_DataObject;
FirebaseAuth    authentification;
FirebaseConfig  config;

String UserID;

// Connect wifi function
void WifiConnect(){
  WiFi.begin(ssid, password);
  Serial.println("Connecting to Wifi...");
  while (WiFi.status() != WL_CONNECTED)
  {
    Serial.print('.');
    delay(500);
  }
  Serial.println(WiFi.localIP());
  Serial.println();
}

// Firebase Config
void Firebase_Configuration(){
  // Truy cap vao project firebase
  config.api_key = API_KEY;
  // Dang nhap vao Authentification truoc thi moi su dung duoc realtime database
  authentification.user.email = auth_email;
  authentification.user.password = auth_password;
  // Vao duong dan tren RTDB de tien hanh thao tac tren do
  config.database_url = RTDB_URL;

  Firebase.reconnectWiFi(true);

  // set up de vao ham tokenStatusCallback khi co thay doi cua token
  config.token_status_callback = tokenStatusCallback;

  Firebase.begin(&config, &authentification);

  Serial.println("Getting User UID...");

  while ((authentification.token.uid) == "")
  {
    Serial.print('.');
    delay(500);
  }
  
  UserID = authentification.token.uid.c_str();

  Serial.print("User UID: ");
  Serial.println(UserID);
}

void setup() {
  Serial.begin(9600);
  WifiConnect();
  Firebase_Configuration();
  pinMode(16, OUTPUT);
  pinMode(17, OUTPUT);
}

void loop() {
  if(Firebase.ready() && (millis() - sendDataPrevMillis > 500 || sendDataPrevMillis == 0))
  {
    sendDataPrevMillis = millis();
    /*if(Firebase.RTDB.setBool(&Firebase_DataObject, path_fan_1, false)){
      Serial.println("PASSED");
    }
    else{
      Serial.println("FAILED");
    }

    if(Firebase.RTDB.setBool(&Firebase_DataObject, path_lamp_1, true)){
      Serial.println("PASSED");
    }
    else{
      Serial.println("FAILED");
    }*/


    Firebase.RTDB.getJSON(&Firebase_DataObject, path_fan_1);
    Serial.print("JSON Data:");
    Serial.println(Firebase_DataObject.jsonString()); // In chuỗi JSON ra Serial Monitor
  }
}
