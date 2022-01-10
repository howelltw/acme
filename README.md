# Acme Shipment Routing

### Build and Install instructions (requires java 11 or higher, Android SDK, and adb previously installed)
1. From a command prompt run "git clone https://github.com/howelltw/acme"
2. Run "cd acme"
3. Attach an Android device with USB debugging enabled
4. Run "adb devices" to verify you can see your device
5. Run "./gradlew installDebug" ("gradlew installDebug" on Windows)
6. On the Android device launch the "Acme Route" app
7. Use the option menu in the app to test the different algorithms.

(Optionally use Android Studio to build and launch)
