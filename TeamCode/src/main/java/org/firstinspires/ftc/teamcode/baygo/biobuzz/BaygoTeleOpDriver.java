package org.firstinspires.ftc.teamcode.baygo.biobuzz;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.HashMap;
import java.util.Map;

/**
 * BAYGO TeleOp Driver Assistant.
 * Provides target locking, coordinate snapping, and auto-aim heading correction.
 */
@SuppressWarnings("unused")
public class BaygoTeleOpDriver {

    public enum Button {
        A, B, X, Y,
        LEFT_BUMPER, RIGHT_BUMPER,
        LEFT_TRIGGER, RIGHT_TRIGGER,
        DPAD_UP, DPAD_DOWN, DPAD_LEFT, DPAD_RIGHT
    }

    public interface ButtonCondition {
        boolean isPressed(Gamepad gamepad);
    }

    private final Follower follower;
    private final Map<String, Pose> presets = new HashMap<>();

    private ButtonCondition snapCondition = gamepad -> gamepad != null && gamepad.x;
    private ButtonCondition autoAimCondition = gamepad -> gamepad != null && gamepad.y;

    private boolean isPoseLocked = false;
    private boolean isAutoAiming = false;
    private boolean prevSnapPressed = false;
    private boolean prevAutoAimPressed = false;

    private Pose targetGoalPosition = new Pose(72.0, 126.0, 0.0);

    public BaygoTeleOpDriver(Follower follower) {
        this.follower = follower;
        if (BaygoPoseStorage.hasAutoPose && follower != null) {
            follower.setPose(BaygoPoseStorage.currentPose);
        }
    }

    public BaygoTeleOpDriver setGoalPosition(double x, double y) {
        this.targetGoalPosition = new Pose(x, y, 0.0);
        return this;
    }

    public BaygoTeleOpDriver addPreset(String key, double x, double y, double headingDeg) {
        presets.put(key, new Pose(x, y, Math.toRadians(headingDeg)));
        return this;
    }

    public BaygoTeleOpDriver setSnapButton(Button button) {
        this.snapCondition = gamepad -> checkButton(gamepad, button);
        return this;
    }

    public BaygoTeleOpDriver setAutoAimButton(Button button) {
        this.autoAimCondition = gamepad -> checkButton(gamepad, button);
        return this;
    }

    public BaygoTeleOpDriver setSnapCondition(ButtonCondition condition) {
        this.snapCondition = condition;
        return this;
    }

    public BaygoTeleOpDriver setAutoAimCondition(ButtonCondition condition) {
        this.autoAimCondition = condition;
        return this;
    }

    public void update(Gamepad gamepad) {
        if (follower == null || gamepad == null) return;

        Pose currentPose = follower.pose();

        boolean snapPressed = snapCondition != null && snapCondition.isPressed(gamepad);
        boolean autoAimPressed = autoAimCondition != null && autoAimCondition.isPressed(gamepad);

        if (snapPressed && !prevSnapPressed) {
            isPoseLocked = !isPoseLocked;
            if (isPoseLocked) {
                Pose lockPose = presets.containsKey("shooting") ? presets.get("shooting") : currentPose;
                follower.hold(lockPose);
            }
        }
        prevSnapPressed = snapPressed;

        if (autoAimPressed && !prevAutoAimPressed) {
            isAutoAiming = !isAutoAiming;
        }
        prevAutoAimPressed = autoAimPressed;

        if (isPoseLocked) {
            follower.update();
        } else if (isAutoAiming) {
            double targetHeading = getHeadingToGoal(currentPose);
            double driveX = -gamepad.left_stick_y;
            double driveY = -gamepad.left_stick_x;
            double headingPower = getHeadingCorrectionPower(currentPose.heading(), targetHeading);

            DrivePowers powers = ManualDrive.fieldCentric(driveX, driveY, headingPower, currentPose.heading());
            ManualDrive.driveOrHold(follower, powers);
            follower.update();
        } else {
            double driveX = -gamepad.left_stick_y;
            double driveY = -gamepad.left_stick_x;
            double turn = -gamepad.right_stick_x;

            DrivePowers powers = ManualDrive.fieldCentric(driveX, driveY, turn, currentPose.heading());
            ManualDrive.driveOrHold(follower, powers);
            follower.update();
        }
    }

    private boolean checkButton(Gamepad gamepad, Button button) {
        if (gamepad == null || button == null) return false;
        switch (button) {
            case A: return gamepad.a;
            case B: return gamepad.b;
            case X: return gamepad.x;
            case Y: return gamepad.y;
            case LEFT_BUMPER: return gamepad.left_bumper;
            case RIGHT_BUMPER: return gamepad.right_bumper;
            case LEFT_TRIGGER: return gamepad.left_trigger > 0.5;
            case RIGHT_TRIGGER: return gamepad.right_trigger > 0.5;
            case DPAD_UP: return gamepad.dpad_up;
            case DPAD_DOWN: return gamepad.dpad_down;
            case DPAD_LEFT: return gamepad.dpad_left;
            case DPAD_RIGHT: return gamepad.dpad_right;
            default: return false;
        }
    }

    public double getDistanceToGoal() {
        if (follower == null) return 0.0;
        Pose p = follower.pose();
        double dx = targetGoalPosition.x() - p.x();
        double dy = targetGoalPosition.y() - p.y();
        return Math.hypot(dx, dy);
    }

    public double getHeadingToGoal(Pose currentPose) {
        if (currentPose == null) return 0.0;
        double dx = targetGoalPosition.x() - currentPose.x();
        double dy = targetGoalPosition.y() - currentPose.y();
        return Math.atan2(dy, dx);
    }

    private double getHeadingCorrectionPower(double currentHeading, double targetHeading) {
        double diff = targetHeading - currentHeading;
        while (diff > Math.PI) diff -= 2 * Math.PI;
        while (diff < -Math.PI) diff += 2 * Math.PI;
        return Math.max(-1.0, Math.min(1.0, diff * 1.5));
    }

    public boolean isPoseLocked() { return isPoseLocked; }
    public boolean isAutoAiming() { return isAutoAiming; }
}
