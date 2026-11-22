package org.firstinspires.ftc.teamcode.baygo.biobuzz;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.interpolator.Interpolator;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.api.Paths.line;

public class BaygoPathExample {

    public static final Pose START_POSE = new Pose(36.0, 18.0, Math.toRadians(0));
    public static final Pose SHOOT_POSE = new Pose(36.0, 48.0, Math.toRadians(45));
    public static final Pose PICKUP_POSE = new Pose(72.0, 72.0, Math.toRadians(90));
    public static final Pose GOAL_POSE = new Pose(72.0, 126.0, Math.toRadians(90));

    public static Path getDriveToShootPath() {
        return line(START_POSE, SHOOT_POSE).constant(Math.toRadians(45));
    }

    public static Path getCurveToPickupPath() {
        Pose controlPoint = new Pose(60.0, 30.0);
        return curve(SHOOT_POSE, controlPoint, PICKUP_POSE).tangent();
    }

    public static void runAutonomousSequence(Follower follower) {
        if (follower == null) return;

        follower.setPose(START_POSE);

        Path path1 = line(START_POSE, SHOOT_POSE).constant(Math.toRadians(45));

        Pose controlPoint = new Pose(60.0, 30.0);
        Path path2 = curve(SHOOT_POSE, controlPoint, PICKUP_POSE)
                .heading(Interpolator.piecewise()
                        .until(0.5, Interpolator.tangent)
                        .until(1.0, Interpolator.constant(Math.toRadians(90))));

        follower.follow(path1);
    }
}
