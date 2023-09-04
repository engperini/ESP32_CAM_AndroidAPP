# ESP32_CAM_AndroidAPP
Android APP to use ESP32-CAM URL Stream Video

MyViewCam is an Android app that allows you to view video streams from an ESP32-CAM camera and capture photos on wifi.

![image](https://github.com/engperini/ESP32_CAM_AndroidAPP/assets/117356668/caf81827-7aa1-4490-a5e7-6a094d9fea2d)



## Features

- **Connect to ESP32-CAM**: Connect to your ESP32-CAM camera using a video streaming URL.

- **Live Video Streaming**: Watch the live video stream from the camera directly within the app.

- **Capture Photos**: Capture photos remotely from the ESP32-CAM camera and save them on your Android device.

- **Camera Switching**: Support for switching between multiple ESP32-CAM cameras.

## ESP32-CAM

**Connect to ESP32-CAM**: Connect to your ESP32-CAM cameraWebserver Arduino Example and enter with your Wifi Settings

  ![image](https://github.com/engperini/ESP32_CAM_AndroidAPP/assets/117356668/06bb2294-0190-411d-a7e3-229421ef61c4)

## Classes

### `MainActivity`

The `MainActivity` class serves as the main activity of the app. It contains the user interface and handles user interactions.

Key functions and components:

- **Buttons**: The app contains buttons for connecting to the camera, capturing photos, and accessing settings.

- **ImageView**: The live video stream from the camera is displayed in an `ImageView` component.

- **Switches**: Two switches (`switchCam1` and `switchCam2`) allow users to select between different ESP32-CAM cameras.

- **HTTP Connection**: The `connectButton` handles the connection to the camera's video stream using HTTP requests.

- **Camera Capture**: The `cameraButton` is responsible for capturing photos from the camera and saving them to the device.

- **Preferences**: The app uses SharedPreferences to store and retrieve camera URL preferences.

### `SettingsActivity`

The `SettingsActivity` class provides access to the app's settings.

Key functions and components:

- **PreferenceFragmentCompat**: This class handles the presentation of app settings to the user.

- **Navigation**: Users can access the settings from the main activity by tapping the "Settings" button.

## Setup

Before using the app, you need to configure the video streaming URLs for your ESP32-CAM cameras. 

## How to Use

1. Open the MyViewCam app on your Android device.

2. Tap the "Connect" button to connect to the camera.

3. Choose which ESP32-CAM camera you want to view using the "SwitchCam1" and "SwitchCam2" switches.

4. Tap the "Camera" button to  capture a photo from the selected camera.

5. Use the "Settings" button to access the app settings and set the camera URLs.

## License

This project is licensed under The MIT License - 2023 PeriniDev. See the [LICENSE.md](LICENSE.md) file for details.




