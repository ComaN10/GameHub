
# **GameHub**

GameHub is a mobile application developed for Android that allows users to search and open installed games or apps using voice commands. Additionally, it provides the functionality to search for locations using Google Maps through voice queries.



## **Features**

- **Voice Search for Games:**
  - Recognize installed games or apps via voice command and open them directly.
  - If the app is not installed, redirect the user to the Google Play Store.

- **Voice Search for Locations:**
  - Search for places using voice commands and open them in Google Maps.

- **User-Friendly Navigation:**
  - Integrated with a navigation drawer to explore different categories of games.
  - A RecyclerView displays installed games in a categorized list.



## **Getting Started**

### **Prerequisites**

1. Android Studio installed on your machine.
2. Android SDK (API 26 and above recommended).
3. A physical or virtual Android device with:
   - Speech recognition support.
   - Google Maps app installed for location search functionality.

### **Installation**

1. Clone the repository to your local machine:
   ```bash
   git clone https://github.com/yourusername/GameHub.git



1.  Open the project in **Android Studio**.
2.  Sync the project with Gradle to install dependencies:
    -   Open Android Studio.
    -   Go to `File > Sync Project with Gradle Files`.

### **Configuration**

-   Ensure your Android device or emulator is connected.
-   Check the `AndroidManifest.xml` for required permissions:

    ```
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />

    ```

### **Deployment**

1.  Connect your Android device or start an emulator.
2.  In Android Studio, click on the **Run** button or use the shortcut:

    ```
    Shift + F10

    ```

3.  Select the target device for deployment.

* * * * *

**How to Use**
--------------

### **Voice Search for Games**

1.  Tap the **microphone button** on the main screen.
2.  Speak the name of the app or game you want to open (e.g., "Open Candy Crush").
    -   If the app is installed, it will launch immediately.
    -   If the app is not installed, you will be redirected to the Google Play Store with a search for the app.

### **Voice Search for Locations**

1.  Tap the **microphone button** on the main screen.
2.  Speak a query for a location (e.g., "Search Madeira Shopping on the map").
    -   The app will open Google Maps with the search location highlighted.

* * * * *

**Project Structure**
---------------------

```
GameHub/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/gamehub/
│   │   │   │   ├── MainActivity.java          # Main logic for voice search and navigation
│   │   │   │   ├── GameAdapter.java          # RecyclerView adapter for displaying games
│   │   │   ├── res/
│   │   │   │   ├── layout/                   # XML layouts for UI
│   │   │   │   ├── drawable/                 # Icons and images
│   │   │   │   ├── values/                   # Strings, styles, and themes
│   ├── build.gradle                          # Project dependencies and configurations
├── README.md                                 # This documentation

```

* * * * *

**Testing**
-----------

1.  Run the app on your device or emulator.
2.  Test the following scenarios:
    -   Voice command to open an installed game.
    -   Voice command for a game that is not installed.
    -   Voice command to search a location on Google Maps.

* * * * *

**Troubleshooting**
-------------------

-   **Voice Recognition Not Working:**

    -   Ensure your device supports speech recognition.
    -   Check the `RECORD_AUDIO` permission in the app settings.
-   **Google Maps Not Opening:**

    -   Ensure Google Maps is installed on the device.
    -   Verify the location query is correctly formatted.

* * * * *

**Contributing**
----------------

Contributions are welcome! To contribute:

1.  Fork the repository.
2.  Create a new branch for your feature or bugfix:

    ```
    git checkout -b feature-name

    ```

3.  Commit and push your changes:

    ```
    git commit -m "Description of changes"
    git push origin feature-name

    ```

4.  Open a Pull Request.

* * * * *

**License**
-----------

This project is licensed under the [MIT License]

* * * * *

**Contact**
-----------


-   **GitHub:** [ComaN](https://github.com/ComaN10)


-   **GitHub:** [jopeodelas](https://github.com/jopeodelas)


