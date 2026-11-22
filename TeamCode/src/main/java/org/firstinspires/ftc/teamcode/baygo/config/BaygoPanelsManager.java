package org.firstinspires.ftc.teamcode.baygo.config;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import java.util.Locale;

public class BaygoPanelsManager {

    private static boolean isPanelsEnabled = true;

    public static void updatePanelsFieldOverlay(Follower follower) {
        if (!isPanelsEnabled || follower == null) return;
        BaygoConfigManager.loadConfig();
    }

    public static void sendPanelsTelemetry(Telemetry telemetry, Follower follower) {
        if (telemetry == null || follower == null) return;

        Pose pose = follower.pose();

        telemetry.addData("[BAYGO] Robot X", String.format(Locale.US, "%.1f in", pose.x()));
        telemetry.addData("[BAYGO] Robot Y", String.format(Locale.US, "%.1f in", pose.y()));
        telemetry.addData("[BAYGO] Heading", String.format(Locale.US, "%.1f°", Math.toDegrees(pose.heading())));
        telemetry.addData("[BAYGO] Battery Voltage", String.format(Locale.US, "%.2fV", BaygoConfigManager.nominalVoltage));
        telemetry.update();
    }

    public static void setPanelsEnabled(boolean enabled) {
        isPanelsEnabled = enabled;
    }
}
