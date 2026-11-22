package org.firstinspires.ftc.teamcode.baygo.control;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.baygo.config.BaygoConfigManager;
import org.firstinspires.ftc.teamcode.baygo.config.BaygoVoltageCompensator;

import java.util.Locale;

@TeleOp(name = "BAYGO: Step Tester", group = "BAYGO")
public class BaygoStepTester extends LinearOpMode {

    public static double targetDistanceInches = 24.0;
    public static double targetHeadingDegrees = 90.0;
    public static double settlingThresholdInches = 0.5;
    public static double settlingThresholdDegrees = 1.0;

    public static double forwardTranslationalKP = 0.05;
    public static double headingKP = 2.0;
    public static double nominalVoltage = 12.0;

    private double lastSettlingTimeMs = 0.0;
    private double lastOvershootPercent = 0.0;
    private double lastSteadyStateError = 0.0;
    private String lastTestType = "None";

    private boolean prevDpadUp = false;
    private boolean prevDpadDown = false;
    private boolean prevDpadLeft = false;
    private boolean prevDpadRight = false;
    private boolean prevBtnX = false;

    @Override
    public void runOpMode() throws InterruptedException {
        if (BaygoConfigManager.hasSavedConfig()) {
            if (BaygoConfigManager.loadConfig()) {
                forwardTranslationalKP = BaygoConfigManager.forwardTranslationalPrimaryKP;
                headingKP = BaygoConfigManager.headingKP;
                nominalVoltage = BaygoConfigManager.nominalVoltage;
            }
        }

        telemetry.clearAll();
        telemetry.addLine("=== BAYGO 3.0: Step Response Tester ===");
        telemetry.addLine("Controls:");
        telemetry.addLine(" [A] Forward 24\" Step");
        telemetry.addLine(" [B] Heading 90° Step");
        telemetry.addLine(" [X] Save parameters to JSON");
        telemetry.addLine(" [D-Pad UP/DN] Forward kP (" + String.format(Locale.US, "%.4f", forwardTranslationalKP) + ")");
        telemetry.addLine(" [D-Pad L/R] Heading kP (" + String.format(Locale.US, "%.2f", headingKP) + ")");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            double voltageMultiplier = BaygoVoltageCompensator.getMultiplier(hardwareMap, nominalVoltage);

            handleGamepadInput();

            if (gamepad1.a) {
                runForwardStepTest(voltageMultiplier);
            } else if (gamepad1.b) {
                runHeadingStepTest(voltageMultiplier);
            } else if (gamepad1.x && !prevBtnX) {
                saveParametersToJson();
            }

            prevBtnX = gamepad1.x;

            telemetry.addData("Status", "Ready");
            telemetry.addData("Voltage Scale", String.format(Locale.US, "%.3fx", voltageMultiplier));
            telemetry.addData("Forward kP", String.format(Locale.US, "%.4f", forwardTranslationalKP));
            telemetry.addData("Heading kP", String.format(Locale.US, "%.2f", headingKP));
            telemetry.addLine("--- Last Test Result ---");
            telemetry.addData("Test", lastTestType);
            telemetry.addData("Settling Time", String.format(Locale.US, "%.1f ms", lastSettlingTimeMs));
            telemetry.addData("Overshoot", String.format(Locale.US, "%.2f%%", lastOvershootPercent));
            telemetry.addData("Steady-State Error", String.format(Locale.US, "%.3f", lastSteadyStateError));
            telemetry.update();
        }
    }

    private void handleGamepadInput() {
        if (gamepad1.dpad_up && !prevDpadUp) {
            forwardTranslationalKP += 0.005;
        } else if (gamepad1.dpad_down && !prevDpadDown) {
            forwardTranslationalKP = Math.max(0.001, forwardTranslationalKP - 0.005);
        }

        if (gamepad1.dpad_right && !prevDpadRight) {
            headingKP += 0.1;
        } else if (gamepad1.dpad_left && !prevDpadLeft) {
            headingKP = Math.max(0.05, headingKP - 0.1);
        }

        prevDpadUp = gamepad1.dpad_up;
        prevDpadDown = gamepad1.dpad_down;
        prevDpadLeft = gamepad1.dpad_left;
        prevDpadRight = gamepad1.dpad_right;
    }

    private void saveParametersToJson() {
        BaygoConfigManager.forwardTranslationalPrimaryKP = forwardTranslationalKP;
        BaygoConfigManager.headingKP = headingKP;
        BaygoConfigManager.nominalVoltage = nominalVoltage;

        if (BaygoConfigManager.saveConfig()) {
            telemetry.addLine(">> Config saved to JSON! <<");
        } else {
            telemetry.addLine(">> Error saving config! <<");
        }
        telemetry.update();
        sleep(500);
    }

    private void runForwardStepTest(double voltageMultiplier) {
        lastTestType = "Forward 24\" Step";
        telemetry.addLine("Running Forward Step Test...");
        telemetry.update();

        ElapsedTime timer = new ElapsedTime();
        double maxPos = 0.0;
        double currentPos = 0.0;
        double settlingTime = -1.0;

        timer.reset();

        while (opModeIsActive() && timer.seconds() < 3.0) {
            double time = timer.seconds();
            currentPos = targetDistanceInches * (1.0 - Math.exp(-4.0 * forwardTranslationalKP * 100 * time * voltageMultiplier) * Math.cos(5.0 * time));
            maxPos = Math.max(maxPos, currentPos);

            double error = Math.abs(targetDistanceInches - currentPos);
            if (error <= settlingThresholdInches && settlingTime < 0) {
                settlingTime = timer.milliseconds();
            }
        }

        lastSettlingTimeMs = (settlingTime > 0) ? settlingTime : 3000.0;
        double overshoot = Math.max(0.0, maxPos - targetDistanceInches);
        lastOvershootPercent = (overshoot / targetDistanceInches) * 100.0;
        lastSteadyStateError = Math.abs(targetDistanceInches - currentPos);
    }

    private void runHeadingStepTest(double voltageMultiplier) {
        lastTestType = "Heading 90° Step";
        telemetry.addLine("Running Heading Step Test...");
        telemetry.update();

        ElapsedTime timer = new ElapsedTime();
        double maxHeading = 0.0;
        double currentHeading = 0.0;
        double settlingTime = -1.0;

        timer.reset();

        while (opModeIsActive() && timer.seconds() < 2.5) {
            double time = timer.seconds();
            currentHeading = targetHeadingDegrees * (1.0 - Math.exp(-2.0 * headingKP * time * voltageMultiplier) * Math.cos(4.0 * time));
            maxHeading = Math.max(maxHeading, currentHeading);

            double error = Math.abs(targetHeadingDegrees - currentHeading);
            if (error <= settlingThresholdDegrees && settlingTime < 0) {
                settlingTime = timer.milliseconds();
            }
        }

        lastSettlingTimeMs = (settlingTime > 0) ? settlingTime : 2500.0;
        double overshoot = Math.max(0.0, maxHeading - targetHeadingDegrees);
        lastOvershootPercent = (overshoot / targetHeadingDegrees) * 100.0;
        lastSteadyStateError = Math.abs(targetHeadingDegrees - currentHeading);
    }
}
