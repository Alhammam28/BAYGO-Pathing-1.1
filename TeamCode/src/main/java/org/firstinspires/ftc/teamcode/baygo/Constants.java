package org.firstinspires.ftc.teamcode.baygo;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.baygo.config.BaygoConfigManager;
import org.firstinspires.ftc.teamcode.baygo.config.BaygoVoltageCompensator;

/**
 * BAYGO Framework Constants & Configuration Factory.
 */
@SuppressWarnings("unused")
public class Constants {

    public static Follower create(HardwareMap hardwareMap) {
        if (BaygoConfigManager.hasSavedConfig()) {
            BaygoConfigManager.loadConfig();
        }

        double voltageMultiplier = BaygoVoltageCompensator.getMultiplier(hardwareMap, BaygoConfigManager.nominalVoltage);

        return null;
    }
}
