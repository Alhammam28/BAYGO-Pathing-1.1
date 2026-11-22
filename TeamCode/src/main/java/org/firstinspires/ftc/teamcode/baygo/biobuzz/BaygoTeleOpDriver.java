package org.firstinspires.ftc.teamcode.baygo.biobuzz;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class BaygoTeleOpDriver {

    public enum Button {
        A, B, X, Y,
        LEFT_BUMPER, RIGHT_BUMPER,
        LEFT_TRIGGER, RIGHT_TRIGGER,
        DPAD_UP, DPAD_DOWN, DPAD_LEFT, DPAD_RIGHT
    }

    private final Follower follower;
    private final Map<String, Pose> presets = new HashMap<>();

    private Function<Gamepad, Boolean> snapCondition = gamepad -> gamepad.x;
    private Function<Gamepad, Boolean> autoAimCondition = gamepad -> gamepad.y;

    private boolean isPoseLocked = false;
    private boolean isAutoAiming = false;
    private boolean prevSnapPressed = false;
    private boolean prevAutoAimPressed = false;

    private Pose goalPosition = new Pose(72.0, 126.0, 0.0);

    public BaygoTeleOpDriver(Follower follower) {
        this.follower = follower;
        if (BaygoPoseStorage.hasAutoPose && follower != null) {
            follower.setPose(BaygoPoseStorage.currentPose);
        }
    }

    public BaygoTeleOpDriver setGoalPosition(double x, double y) {
        this.goalPosition = new Pose(x, y, 0.0);
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

    public BaygoTeleOpDriver setSnapCondition(Function<Gamepad, Boolean> condition) {
        this.snapCondition = condition;
        return this;
    }

    public BaygoTeleOpDriver setAutoAimCondition(Function<Gamepad, Boolean> condition) {
        this.autoAimCondition = condition;
        return this;
    }

    public void update(Gamepad gamepad) {
        Pose currentPose = follower.pose();

        boolean snapPressed = snapCondition != null && snapCondition.apply(gamepad);
        boolean autoAimPressed = autoAimCondition != null && autoAimCondition.apply(gamepad);

        if (snapPressed && !prevSnapPressed) {
            isPoseLocked = !isPoseLocked;
            if (isPoseLocked) {
                Pose lockPose = presets.getOrDefault("shooting", currentPose);
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
        Pose p = follower.pose();
        double dx = goalPosition.x() - p.x();
        double dy = goalPosition.y() - p.y();
        return Math.hypot(dx, dy);
    }

    public double getHeadingToGoal(Pose currentPose) {
        double dx = goalPosition.x() - currentPose.x();
        double dy = goalPosition.y() - currentPose.y();
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
