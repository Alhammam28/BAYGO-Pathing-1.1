package org.firstinspires.ftc.teamcode.baygo.biobuzz;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.baygo.Constants;

import java.util.Locale;

@TeleOp(name = "BAYGO: BIOBUZZ TeleOp Driver", group = "BAYGO")
public class BaygoTeleOpExample extends OpMode {

    private Follower follower;
    private BaygoTeleOpDriver teleOpDriver;

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);

        teleOpDriver = new BaygoTeleOpDriver(follower)
                .setGoalPosition(72.0, 126.0)
                .addPreset("shooting", 36.0, 48.0, 45.0)
                .setSnapButton(BaygoTeleOpDriver.Button.X)
                .setAutoAimButton(BaygoTeleOpDriver.Button.LEFT_TRIGGER);

        telemetry.addLine("=== BAYGO TeleOp Initialized ===");
        telemetry.addLine(" [X] Snap to Shooting Pose");
        telemetry.addLine(" [LT] Auto-Aim to High Goal");
    }

    @Override
    public void init_loop() {
        telemetry.addLine("BAYGO TeleOp Initialized. Ready for match start.");
    }

    @Override
    public void loop() {
        teleOpDriver.update(gamepad1);

        telemetry.addData("Distance to Goal", String.format(Locale.US, "%.1f in", teleOpDriver.getDistanceToGoal()));
        telemetry.addData("Pose Locked (X)", teleOpDriver.isPoseLocked());
        telemetry.addData("Auto-Aiming (LT)", teleOpDriver.isAutoAiming());
    }

    @Override
    public void stop() {
        if (follower != null) {
            BaygoPoseStorage.saveAutoPose(follower.pose());
        }
    }
}
