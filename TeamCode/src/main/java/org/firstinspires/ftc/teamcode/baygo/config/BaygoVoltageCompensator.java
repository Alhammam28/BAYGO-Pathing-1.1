package org.firstinspires.ftc.teamcode.baygo.config;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;

@SuppressWarnings("unused")
public class BaygoVoltageCompensator {
    private static final double DEFAULT_NOMINAL_VOLTAGE = 12.0;

    public static double getMultiplier(HardwareMap hardwareMap, double nominalVoltage) {
        if (hardwareMap == null) return 1.0;

        double totalVoltage = 0.0;
        int sensorCount = 0;

        for (VoltageSensor sensor : hardwareMap.voltageSensor) {
            double v = sensor.getVoltage();
            if (v > 9.0) {
                totalVoltage += v;
                sensorCount++;
            }
        }

        if (sensorCount == 0 || totalVoltage <= 0.0) {
            return 1.0;
        }

        double avgVoltage = totalVoltage / sensorCount;
        double multiplier = nominalVoltage / avgVoltage;

        return Math.max(0.7, Math.min(1.4, multiplier));
    }

    public static double getMultiplier(HardwareMap hardwareMap) {
        return getMultiplier(hardwareMap, DEFAULT_NOMINAL_VOLTAGE);
    }
}
