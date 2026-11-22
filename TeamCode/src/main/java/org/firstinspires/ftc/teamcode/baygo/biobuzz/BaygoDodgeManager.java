package org.firstinspires.ftc.teamcode.baygo.biobuzz;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

import static com.pedropathing.api.Paths.curve;

public class BaygoDodgeManager {

    public static Path createBypassPath(Pose startPose, Pose endPose, double offsetInches) {
        double midX = (startPose.x() + endPose.x()) / 2.0;
        double midY = (startPose.y() + endPose.y()) / 2.0 + offsetInches;

        Pose bypassControlPoint = new Pose(midX, midY);
        return curve(startPose, bypassControlPoint, endPose).tangent();
    }

    public static void dodgeAndContinue(Follower follower, Pose targetPose, double dodgeOffset) {
        if (follower == null) return;
        Pose current = follower.pose();
        Path bypassPath = createBypassPath(current, targetPose, dodgeOffset);
        follower.follow(bypassPath);
    }
}
