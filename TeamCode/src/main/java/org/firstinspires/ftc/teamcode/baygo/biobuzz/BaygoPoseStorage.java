package org.firstinspires.ftc.teamcode.baygo.biobuzz;

import com.pedropathing.math.Pose;

public class BaygoPoseStorage {
    public static Pose currentPose = new Pose(36.0, 18.0, 0.0);
    public static boolean hasAutoPose = false;

    public static void saveAutoPose(Pose pose) {
        if (pose != null) {
            currentPose = pose;
            hasAutoPose = true;
        }
    }

    public static void reset() {
        currentPose = new Pose(36.0, 18.0, 0.0);
        hasAutoPose = false;
    }
}
