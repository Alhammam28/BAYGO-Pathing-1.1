package org.firstinspires.ftc.teamcode.baygo.biobuzz;

import com.qualcomm.robotcore.hardware.Gamepad;

public class BaygoStatusSignals {

    public static void signalTargetLock(Gamepad gamepad) {
        if (gamepad != null) {
            gamepad.rumble(200);
        }
    }

    public static void signalActionComplete(Gamepad gamepad) {
        if (gamepad != null) {
            gamepad.rumbleBlips(2);
        }
    }

    public static void signalWarning(Gamepad gamepad) {
        if (gamepad != null) {
            gamepad.rumble(1000);
        }
    }
}
