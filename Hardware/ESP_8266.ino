#include <ESP8266WiFi.h>
#include <FirebaseESP8266.h>
#include <NTPClient.h>
#include <WiFiUdp.h>
#include <Wire.h>

// WiFi credentials
#define WIFI_SSID "A.A.A"
#define WIFI_PASSWORD "Salma2016@"


const long utcOffsetInSeconds = 7200;

// Firebase credentials
#define FIREBASE_HOST "https://rehablabdatabase-58a90-default-rtdb.europe-west1.firebasedatabase.app/"
#define FIREBASE_AUTH "ekRQOy2KW0BKSjc01JeugNTP5n4aAfhxcdGvRnV5"

// Define NTP Client to get time
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org", utcOffsetInSeconds);

// Firebase objects
FirebaseData firebaseData;
FirebaseConfig config;
FirebaseJson json;
FirebaseAuth auth;

// Database main path (to be updated in setup with the user UID)
String databasePath;
// Database child nodes
String dataPath = "/data";

String parentPath;

// Add WiFi connection timeout
const unsigned long WIFI_TIMEOUT = 20000; // 20 seconds timeout

const unsigned long FIREBASE_INTERVAL = 1000; // Send data every 1 second

unsigned long epoch = 0;
unsigned long lastFirebaseUpdate;
// Constants for calculations
const int SECONDS_IN_A_MINUTE = 60;
const int SECONDS_IN_AN_HOUR = 3600;
const int SECONDS_IN_A_DAY = 86400;
int year;
int month;
int day;

int lastSecond;
int counter = 0;

// Days in months for non-leap and leap years
const int DAYS_IN_MONTHS[12] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
const int DAYS_IN_MONTHS_LEAP[12] = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

String readString = "";

unsigned int ntp_year, days_since_epoch, day_of_year;
void initWiFi() {
    WiFi.mode(WIFI_STA);
    WiFi.begin(WIFI_SSID, WIFI_PASSWORD);

    unsigned long startAttemptTime = millis();

    Serial.print("Connecting to WiFi");
    while (WiFi.status() != WL_CONNECTED && 
           millis() - startAttemptTime < WIFI_TIMEOUT) {
        Serial.print(".");
        delay(500);
    }

    if (WiFi.status() != WL_CONNECTED) {
        Serial.println("\nFailed to connect to WiFi. Restarting...");
        ESP.restart();
    }

    timeClient.begin();
    Serial.println();
    Serial.print("Connected with IP: ");
    Serial.println(WiFi.localIP());
}

void initFirebase() {
    config.host = FIREBASE_HOST;
    config.signer.tokens.legacy_token = FIREBASE_AUTH;

    Firebase.begin(&config, &auth);
    Firebase.reconnectWiFi(true);

    // Set database read timeout to 1 minute
    Firebase.setReadTimeout(firebaseData, 1000 * 60);
    // Set database write timeout
    Firebase.setwriteSizeLimit(firebaseData, "tiny");

    Serial.println("Firebase initialized");
}

void sendToFirebase() {
    if (WiFi.status() != WL_CONNECTED) {
        Serial.println("WiFi disconnected. Attempting to reconnect...");
        initWiFi();
        return;
    }

    json.clear();
    json.add("timestamp", String(millis()));
    json.add("roll", 0);
    json.add("pitch", 2);
    json.add("yaw", 5);

    String path = "/gyro_data";

    if (Firebase.pushJSON(firebaseData, path, json)) {
        Serial.println("Data sent to Firebase successfully");
    } else {
        Serial.println("Failed to send data to Firebase");
        Serial.println("Reason: " + firebaseData.errorReason());

        // If the error is related to token expiration or auth, reinitialize Firebase
        if (firebaseData.errorReason().indexOf("auth") >= 0 || 
            firebaseData.errorReason().indexOf("token") >= 0) {
            Serial.println("Reinitializing Firebase connection...");
            initFirebase();
        }
    }
}

// Function to check if a year is a leap year
bool isLeapYear(int year) {
    return (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
}

// Function to convert epoch to year, month, and day
void epochToDate() {
    // Start from 1970-01-01 (epoch 0)
    year = 1970;

    // Calculate days since epoch
    unsigned long days = epoch / SECONDS_IN_A_DAY;

    // Determine the current year
    while (true) {
        int daysInYear = isLeapYear(year) ? 366 : 365;
        if (days < daysInYear) break;
        days -= daysInYear;
        year++;
    }

    // Determine the current month
    const int *daysInMonths = isLeapYear(year) ? DAYS_IN_MONTHS_LEAP : DAYS_IN_MONTHS;
    month = 0; // Start from January (index 0)
    while (days >= daysInMonths[month]) {
        days -= daysInMonths[month];
        month++;
    }
    month += 1; // Months are 1-based (1 = January)

    // The remaining days represent the day of the month
    day = days + 1; // Days are 1-based (1 = first day of the month)
}

void serialRead() { 
  while (Serial.available()) {
  if (Serial.available() >0) {
    char c = Serial.read();
    readString += c;}
  }
}

void setup() {
  // put your setup code here, to run once:
    Serial.begin(115200);

    Wire.setClock(400000);
    Wire.begin();

    delay(250);

    initWiFi();
    initFirebase();
    lastFirebaseUpdate = millis();
}

void loop() {

    readString = "";
    serialRead();
    if(readString.charAt(0) == ';'){
    timeClient.update();
    epoch = timeClient.getEpochTime();
    epochToDate();
    String formattedDate= String(day) + "-" + String(month) + "-" + String(year);
    String formattedTime = timeClient.getFormattedTime();
    while(!Firebase.ready()){

    }
    Serial.println(readString);
    Serial.println("test test");
    if(timeClient.getSeconds() == lastSecond){
      counter++;
    }
    else{
      lastSecond = timeClient.getSeconds();
    }
    //Firebase.setString(firebaseData, String("/") + String(formattedDate) + String("/") + String(formattedTime) + String("/"), String("YA RAB")); 
    Firebase.setString(firebaseData, String("/") + String(formattedDate) + String("/") + String(formattedTime) + String(":") + String(counter) + String("/"), String(readString.substring(1, readString.length() - 2))); 
    Serial.print("Recieved Input: ");
    Serial.println(readString);
    //delay(100);
    }
}
