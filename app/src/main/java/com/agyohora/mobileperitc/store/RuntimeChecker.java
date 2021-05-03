package com.agyohora.mobileperitc.store;

public class RuntimeChecker {

    public static class RuntimeMode {
        public static int modeNo = -1;
        public static int modeTypeNo = 1000;
        public static int modeStepNo = 0;
        public static boolean isPauseActive = false;
        public static boolean isFixationOk = false;
        public static boolean isFixationMonitoringActive = true;
        public static int patAge = 0;
        public static int IPDVal = 0;
        public static boolean isItJARVIS = false;
    }

    public static class RuntimeStatus {
        static int batteryLevel;
        static boolean isBatteryLevelLow = false;
        static boolean isCharging = false;
        static boolean isConnectionwithTCOn = false;
        static boolean isCAACConnectionOn = false;
        static boolean isCameraRightWorking = false;
        static boolean isCameraLeftWorking = false;
        static boolean isLED1Working = false;
        static boolean isLED2Working = false;
        static boolean isPhotoDiode1Working = false;
        static boolean isPhotoDiode2Working = false;
        static boolean isPRBWorking = false;
        static boolean isScreenBrightnessOK = false;
        static boolean isPrevTestResultPresent = false;
        static float screenBrightnessLevel;
    }
}

