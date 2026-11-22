package org.firstinspires.ftc.teamcode.baygo.config;

import android.os.Environment;
import android.util.Log;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * BAYGO Configuration Manager.
 * JSON persistence engine saving parameters to Control Hub (/sdcard/FIRST/baygo_config.json).
 */
public class BaygoConfigManager {
    private static final String TAG = "BaygoConfigManager";
    public static final String FILE_PATH = Environment.getExternalStorageDirectory().getPath() + "/FIRST/baygo_config.json";

    public static double maxAchievableForwardVelocity = 70.0;
    public static double maxAchievableStrafeVelocity = 50.0;
    public static double naturalForwardDeceleration = 30.0;
    public static double naturalStrafeDeceleration = 25.0;

    public static double forwardTranslationalPrimaryKP = 0.05;
    public static double forwardTranslationalSecondaryKP = 0.02;
    public static double strafeTranslationalPrimaryKP = 0.06;
    public static double strafeTranslationalSecondaryKP = 0.025;

    public static double headingKP = 2.0;
    public static double headingBrakingLinear = 0.05;
    public static double headingBrakingQuadratic = 0.01;

    public static double forwardBrakingLinear = 0.04;
    public static double forwardBrakingQuadratic = 0.008;
    public static double strafeBrakingLinear = 0.05;
    public static double strafeBrakingQuadratic = 0.01;

    public static double coastKV = 0.012;
    public static double brakeKV = 0.010;

    public static double nominalVoltage = 12.0;
    public static boolean enableVoltageCompensation = true;

    public static String frontLeftMotor = "frontLeft";
    public static String frontRightMotor = "frontRight";
    public static String backLeftMotor = "backLeft";
    public static String backRightMotor = "backRight";

    public static String frontLeftDirection = "REVERSE";
    public static String frontRightDirection = "FORWARD";
    public static String backLeftDirection = "REVERSE";
    public static String backRightDirection = "FORWARD";

    public static boolean hasSavedConfig() {
        File file = new File(FILE_PATH);
        return file.exists() && file.length() > 0;
    }

    public static synchronized boolean saveConfig() {
        try {
            JSONObject json = new JSONObject();

            json.put("maxAchievableForwardVelocity", maxAchievableForwardVelocity);
            json.put("maxAchievableStrafeVelocity", maxAchievableStrafeVelocity);
            json.put("naturalForwardDeceleration", naturalForwardDeceleration);
            json.put("naturalStrafeDeceleration", naturalStrafeDeceleration);

            json.put("forwardTranslationalPrimaryKP", forwardTranslationalPrimaryKP);
            json.put("forwardTranslationalSecondaryKP", forwardTranslationalSecondaryKP);
            json.put("strafeTranslationalPrimaryKP", strafeTranslationalPrimaryKP);
            json.put("strafeTranslationalSecondaryKP", strafeTranslationalSecondaryKP);

            json.put("headingKP", headingKP);
            json.put("headingBrakingLinear", headingBrakingLinear);
            json.put("headingBrakingQuadratic", headingBrakingQuadratic);

            json.put("forwardBrakingLinear", forwardBrakingLinear);
            json.put("forwardBrakingQuadratic", forwardBrakingQuadratic);
            json.put("strafeBrakingLinear", strafeBrakingLinear);
            json.put("strafeBrakingQuadratic", strafeBrakingQuadratic);

            json.put("coastKV", coastKV);
            json.put("brakeKV", brakeKV);

            json.put("nominalVoltage", nominalVoltage);
            json.put("enableVoltageCompensation", enableVoltageCompensation);

            json.put("frontLeftMotor", frontLeftMotor);
            json.put("frontRightMotor", frontRightMotor);
            json.put("backLeftMotor", backLeftMotor);
            json.put("backRightMotor", backRightMotor);

            json.put("frontLeftDirection", frontLeftDirection);
            json.put("frontRightDirection", frontRightDirection);
            json.put("backLeftDirection", backLeftDirection);
            json.put("backRightDirection", backRightDirection);

            File file = new File(FILE_PATH);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(json.toString(4));
            }

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving BAYGO config", e);
            return false;
        }
    }

    public static synchronized boolean loadConfig() {
        if (!hasSavedConfig()) return false;

        try {
            File file = new File(FILE_PATH);
            StringBuilder sb = new StringBuilder();
            try (FileReader reader = new FileReader(file)) {
                char[] buf = new char[1024];
                int read;
                while ((read = reader.read(buf)) != -1) {
                    sb.append(buf, 0, read);
                }
            }

            JSONObject json = new JSONObject(sb.toString());

            maxAchievableForwardVelocity = json.optDouble("maxAchievableForwardVelocity", maxAchievableForwardVelocity);
            maxAchievableStrafeVelocity = json.optDouble("maxAchievableStrafeVelocity", maxAchievableStrafeVelocity);
            naturalForwardDeceleration = json.optDouble("naturalForwardDeceleration", naturalForwardDeceleration);
            naturalStrafeDeceleration = json.optDouble("naturalStrafeDeceleration", naturalStrafeDeceleration);

            forwardTranslationalPrimaryKP = json.optDouble("forwardTranslationalPrimaryKP", forwardTranslationalPrimaryKP);
            forwardTranslationalSecondaryKP = json.optDouble("forwardTranslationalSecondaryKP", forwardTranslationalSecondaryKP);
            strafeTranslationalPrimaryKP = json.optDouble("strafeTranslationalPrimaryKP", strafeTranslationalPrimaryKP);
            strafeTranslationalSecondaryKP = json.optDouble("strafeTranslationalSecondaryKP", strafeTranslationalSecondaryKP);

            headingKP = json.optDouble("headingKP", headingKP);
            headingBrakingLinear = json.optDouble("headingBrakingLinear", headingBrakingLinear);
            headingBrakingQuadratic = json.optDouble("headingBrakingQuadratic", headingBrakingQuadratic);

            forwardBrakingLinear = json.optDouble("forwardBrakingLinear", forwardBrakingLinear);
            forwardBrakingQuadratic = json.optDouble("forwardBrakingQuadratic", forwardBrakingQuadratic);
            strafeBrakingLinear = json.optDouble("strafeBrakingLinear", strafeBrakingLinear);
            strafeBrakingQuadratic = json.optDouble("strafeBrakingQuadratic", strafeBrakingQuadratic);

            coastKV = json.optDouble("coastKV", coastKV);
            brakeKV = json.optDouble("brakeKV", brakeKV);

            nominalVoltage = json.optDouble("nominalVoltage", nominalVoltage);
            enableVoltageCompensation = json.optBoolean("enableVoltageCompensation", enableVoltageCompensation);

            frontLeftMotor = json.optString("frontLeftMotor", frontLeftMotor);
            frontRightMotor = json.optString("frontRightMotor", frontRightMotor);
            backLeftMotor = json.optString("backLeftMotor", backLeftMotor);
            backRightMotor = json.optString("backRightMotor", backRightMotor);

            frontLeftDirection = json.optString("frontLeftDirection", frontLeftDirection);
            frontRightDirection = json.optString("frontRightDirection", frontRightDirection);
            backLeftDirection = json.optString("backLeftDirection", backLeftDirection);
            backRightDirection = json.optString("backRightDirection", backRightDirection);

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error loading BAYGO config", e);
            return false;
        }
    }
}
