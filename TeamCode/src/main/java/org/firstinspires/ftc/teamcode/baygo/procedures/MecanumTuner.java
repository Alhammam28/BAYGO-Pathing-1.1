package org.firstinspires.ftc.teamcode.baygo.procedures;

import com.pedropathing.tuning.autotune.*;
import com.pedropathing.tuning.autotune.Display.FourWheelBot.Wheel;

enum Direction {
    @DisplayName("Forward") FORWARD,
    @DisplayName("Reversed") REVERSE
}

public class MecanumTuner extends Procedure {
    public MecanumTuner() {
        super("BAYGO Mecanum Tuner", "A procedure to find the directions of mecanum wheels.");
    }

    @Override
    public void run() throws InterruptedException {
        Inputs motorNames = inputs("Mecanum Motor Names", "Enter the names in HardwareMap of your drivetrain motors.");
        Inputs.Field<String> frontLeftName = motorNames.s("Front Left Name");
        Inputs.Field<String> frontRightName = motorNames.s("Front Right Name");
        Inputs.Field<String> backLeftName = motorNames.s("Back Left Name");
        Inputs.Field<String> backRightName = motorNames.s("Back Right Name");
        awaitInputs(motorNames);

        confirmation("Motor Directions", "Each drivetrain motor will spin, one at a time. Select if it spun forward or reversed.");

        Direction frontLeftDirection = testMotor(Wheel.FRONT_LEFT, "Front Left", frontLeftName.get());
        Direction frontRightDirection = testMotor(Wheel.FRONT_RIGHT, "Front Right", frontRightName.get());
        Direction backLeftDirection = testMotor(Wheel.BACK_LEFT, "Back Left", backLeftName.get());
        Direction backRightDirection = testMotor(Wheel.BACK_RIGHT, "Back Right", backRightName.get());

        result("frontLeftName", frontLeftName.get());
        result("frontRightName", frontRightName.get());
        result("backLeftName", backLeftName.get());
        result("backRightName", backRightName.get());
        result("frontLeftDirection", frontLeftDirection);
        result("frontRightDirection", frontRightDirection);
        result("backLeftDirection", backLeftDirection);
        result("backRightDirection", backRightDirection);
    }

    private Direction testMotor(Wheel wheel, String displayName, String hardwareName) throws InterruptedException {
        final boolean[] correctMotor = new boolean[1];
        final Direction[] direction = new Direction[1];

        withDisplay(Display.fourWheelBot(wheel, false), () -> {
            try {
                runOpMode(new SpinMotor(displayName, hardwareName));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Inputs inputs = inputs(displayName, "Determine the " + displayName.toLowerCase() + " motor direction.");
            Inputs.Field<Boolean> correctMotorField = inputs.b("Did the " + displayName.toLowerCase() + " motor spin?").withDefault(true);
            Inputs.Field<Direction> directionField = inputs.e("Which way did the motor spin?", Direction.class);
            try {
                awaitInputs(inputs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            correctMotor[0] = correctMotorField.get();
            direction[0] = directionField.get();
        });

        if (!correctMotor[0])
            abort("The wrong motor spun. Check that your motors are plugged into the correct ports.");

        return direction[0];
    }
}

class SpinMotor extends TuningOpMode<Void> {
    private final String name;

    public SpinMotor(String displayName, String hardwareName) {
        super(displayName, "The " + displayName.toLowerCase() + " motor will spin.", true);
        this.name = hardwareName;
    }

    @Override
    protected Void runTuningOpMode() {
        com.qualcomm.robotcore.hardware.DcMotor motor = hardwareMap.dcMotor.get(name);
        waitForStart();
        motor.setPower(0.5);
        while (opModeIsActive()) {
        }
        motor.setPower(0);
        return null;
    }
}
