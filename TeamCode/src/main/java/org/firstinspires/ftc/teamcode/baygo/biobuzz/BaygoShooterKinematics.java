package org.firstinspires.ftc.teamcode.baygo.biobuzz;

import com.pedropathing.math.Pose;

public class BaygoShooterKinematics {

    public static final double GRAVITY_INCHES_PER_SEC2 = 386.09;

    public static class ShooterTargetData {
        public double horizontalDistance;
        public double fieldAngleRad;
        public double turretRelativeAngleRad;
        public double requiredFlywheelVelocity;
        public double launchAngleRad;
    }

    public static Pose getShooterWorldPose(Pose robotPose, double offsetX, double offsetY) {
        double heading = robotPose.heading();
        double worldX = robotPose.x() + (offsetX * Math.cos(heading) - offsetY * Math.sin(heading));
        double worldY = robotPose.y() + (offsetX * Math.sin(heading) + offsetY * Math.cos(heading));
        return new Pose(worldX, worldY, heading);
    }

    public static ShooterTargetData calculateTargetData(Pose robotPose, Pose goalPose, double shooterOffsetX, double shooterOffsetY) {
        Pose shooterPose = getShooterWorldPose(robotPose, shooterOffsetX, shooterOffsetY);

        double dx = goalPose.x() - shooterPose.x();
        double dy = goalPose.y() - shooterPose.y();

        ShooterTargetData data = new ShooterTargetData();
        data.horizontalDistance = Math.hypot(dx, dy);
        data.fieldAngleRad = Math.atan2(dy, dx);

        double relativeAngle = data.fieldAngleRad - robotPose.heading();
        while (relativeAngle > Math.PI) relativeAngle -= 2 * Math.PI;
        while (relativeAngle < -Math.PI) relativeAngle += 2 * Math.PI;

        data.turretRelativeAngleRad = relativeAngle;
        return data;
    }

    public static double calculateRequiredVelocity(double distanceInches, double deltaZInches, double pitchAngleDeg) {
        double pitchRad = Math.toRadians(pitchAngleDeg);
        double cosPitch = Math.cos(pitchRad);
        double tanPitch = Math.tan(pitchRad);

        double denominator = 2.0 * cosPitch * cosPitch * (distanceInches * tanPitch - deltaZInches);
        if (denominator <= 0) return 0.0;

        double numerator = GRAVITY_INCHES_PER_SEC2 * distanceInches * distanceInches;
        return Math.sqrt(numerator / denominator);
    }

    public static double calculateRequiredHoodAngleDeg(double distanceInches, double deltaZInches, double velocityInchesPerSec) {
        double v2 = velocityInchesPerSec * velocityInchesPerSec;
        double g = GRAVITY_INCHES_PER_SEC2;

        double termUnderRoot = (v2 * v2) - g * (g * distanceInches * distanceInches + 2.0 * deltaZInches * v2);
        if (termUnderRoot < 0) return 45.0;

        double tanPitch = (v2 - Math.sqrt(termUnderRoot)) / (g * distanceInches);
        return Math.toDegrees(Math.atan(tanPitch));
    }
}
