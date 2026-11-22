package org.firstinspires.ftc.teamcode.baygo.biobuzz;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

import static com.pedropathing.api.Paths.curve;

public class BaygoWallAutoAlign {

    public static final Pose RED_WALL_CORNER = new Pose(9.0, 9.0, Math.toRadians(0));
    public static final Pose BLUE_WALL_CORNER = new Pose(135.0, 9.0, Math.toRadians(180));

    public static Path getWallDeparturePath(Follower follower, Pose wallCornerPose, Pose idealStartPose) {
        if (follower != null) {
            follower.setPose(wallCornerPose);
        }

        double controlX = (wallCornerPose.x() + idealStartPose.x()) / 2.0;
        double controlY = Math.max(wallCornerPose.y(), idealStartPose.y()) + 12.0;
        Pose controlPoint = new Pose(controlX, controlY);

        return curve(wallCornerPose, controlPoint, idealStartPose).tangent();
    }
}
