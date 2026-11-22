package org.firstinspires.ftc.teamcode.baygo.biobuzz;

import com.qualcomm.robotcore.hardware.Gamepad;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import java.util.Locale;

public class BaygoAutoManager {

    public enum Alliance { RED, BLUE }
    public enum StartSide { LEFT, RIGHT }
    public enum AutoStrategy { HIGH_GOAL_PARK, MULTI_SAMPLE_PARK, PARK_ONLY }

    private Alliance alliance = Alliance.RED;
    private StartSide startSide = StartSide.LEFT;
    private AutoStrategy strategy = AutoStrategy.HIGH_GOAL_PARK;
    private double startDelaySeconds = 0.0;

    private boolean prevDpadUp = false;
    private boolean prevDpadDown = false;
    private boolean prevDpadLeft = false;
    private boolean prevDpadRight = false;
    private boolean prevBtnX = false;
    private boolean prevBtnY = false;

    public void updateInitControls(Gamepad gamepad, Telemetry telemetry) {
        if (gamepad == null) return;

        if (gamepad.x && !prevBtnX) {
            alliance = (alliance == Alliance.RED) ? Alliance.BLUE : Alliance.RED;
        }
        prevBtnX = gamepad.x;

        if (gamepad.y && !prevBtnY) {
            startSide = (startSide == StartSide.LEFT) ? StartSide.RIGHT : StartSide.LEFT;
        }
        prevBtnY = gamepad.y;

        if (gamepad.dpad_up && !prevDpadUp) {
            cycleStrategy(1);
        } else if (gamepad.dpad_down && !prevDpadDown) {
            cycleStrategy(-1);
        }
        prevDpadUp = gamepad.dpad_up;
        prevDpadDown = gamepad.dpad_down;

        if (gamepad.dpad_right && !prevDpadRight) {
            startDelaySeconds = Math.min(10.0, startDelaySeconds + 1.0);
        } else if (gamepad.dpad_left && !prevDpadLeft) {
            startDelaySeconds = Math.max(0.0, startDelaySeconds - 1.0);
        }
        prevDpadRight = gamepad.dpad_right;
        prevDpadLeft = gamepad.dpad_left;

        if (telemetry != null) {
            telemetry.clearAll();
            telemetry.addLine("=== BAYGO Autonomous Route Chooser ===");
            telemetry.addData(" [X] Alliance", alliance);
            telemetry.addData(" [Y] Starting Side", startSide);
            telemetry.addData(" [D-Pad UP/DN] Strategy", strategy);
            telemetry.addData(" [D-Pad L/R] Start Delay", String.format(Locale.US, "%.0fs", startDelaySeconds));
            telemetry.addLine("Ready for match start.");
            telemetry.update();
        }
    }

    private void cycleStrategy(int dir) {
        AutoStrategy[] vals = AutoStrategy.values();
        int nextIdx = (strategy.ordinal() + dir + vals.length) % vals.length;
        strategy = vals[nextIdx];
    }

    public Alliance getAlliance() { return alliance; }
    public StartSide getStartSide() { return startSide; }
    public AutoStrategy getStrategy() { return strategy; }
    public double getStartDelaySeconds() { return startDelaySeconds; }
}
