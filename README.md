# BAYGO 1.1 - FTC Autonomous & TeleOp Framework

Welcome to the official repository for **BAYGO 1.1** integrated with the FIRST Tech Challenge (FTC) SDK for the BIOBUZZ (2026-2027) competition season.

---

## Overview

**BAYGO 1.1** is an advanced, high-performance autonomous and TeleOp framework for FTC robotics. It combines path following, real-time battery voltage compensation, event triggers, auto-aim shooting kinematics, and seamless auto-to-teleop pose inheritance into a modular architecture.

### Core Architecture & Features

* **Non-Blocking OpMode Architecture:** Event-driven control loop design (`init()`, `loop()`, `stop()`).
* **Auto-Aim Shooting Kinematics (`BaygoShooterKinematics`):** Real-time ballistics, turret yaw, and flywheel RPM calculation.
* **TeleOp Auto-Pilot (`BaygoTeleOpDriver`):** Gamepad-driven target locking and coordinate snapping.
* **Battery Voltage Scaling (`BaygoVoltageCompensator`):** Automatic voltage scaling across peak (14.4V) and depleted battery levels.
* **Dynamic JSON Persistence (`BaygoConfigManager`):** Instant load/save parameters without re-compiling (`/sdcard/FIRST/baygo_config.json`).
* **Wall Departure Arc (`BaygoWallAutoAlign`):** Smooth Bézier departure curve from field walls to eliminate placement error.
* **Auto-to-TeleOp Pose Transfer (`BaygoPoseStorage`):** Preserves field-centric orientation across match transitions.

---

## Project Structure

```
TeamCode/
└── src/main/java/org/firstinspires/ftc/teamcode/baygo/
    ├── Constants.java                       # Framework Initialization Factory
    ├── Tuning.java                          # Global Tuning Definitions
    │
    ├── biobuzz/                             # Competition Season Modules
    │   ├── BaygoShooterKinematics.java      # Shooter Kinematics & Flywheel RPM Calculator
    │   ├── BaygoTeleOpDriver.java           # Auto-Aim & Pose Snap Driver Assistant
    │   ├── BaygoAutoManager.java            # Driver Station Strategy & Delay Selector
    │   ├── BaygoWallAutoAlign.java          # Wall Corner Zeroing & Departure Curve
    │   ├── BaygoPoseStorage.java            # Auto-to-TeleOp Pose Inheritance
    │   ├── BaygoActionTrigger.java          # Mid-Path Event Action Hooks
    │   ├── BaygoSafetyMonitor.java          # Stall Protection & Anti-Collision System
    │   ├── BaygoDodgeManager.java           # Dynamic Path Obstacle Bypass
    │   ├── BaygoScoreTracker.java           # Real-Time Match Points Tracker
    │   ├── BaygoStatusSignals.java          # Controller Rumble Feedback Signals
    │   ├── BaygoTeleOpExample.java          # Standard OpMode TeleOp Implementation
    │   └── BaygoPathExample.java            # Autonomous Path Trajectory Example
    │
    ├── config/                              # Persistence & Battery Adaptations
    │   ├── BaygoConfigManager.java          # Dynamic JSON Persistence Manager
    │   ├── BaygoPanelsManager.java          # Panels & Dashboard Integration Bridge
    │   └── BaygoVoltageCompensator.java     # Real-Time Voltage Compensation
    │
    └── control/                             # Control Algorithms
        ├── BaygoPIDFController.java         # Low-Pass Filtered & Anti-Windup PIDF
        ├── BaygoMechanismPIDF.java          # Flywheel Recovery & Mechanism Gravity Comp
        └── BaygoStepTester.java             # Interactive Step Response Tester
```

---

## 💻 TeleOp Implementation Example

```java
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

        telemetry.addLine("=== BAYGO TeleOp Ready ===");
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
```

---

## 📥 Documentation & Manuals

The repository includes ready-to-print user guides and manuals in the root directory:
* **`BAYGO_1.1_Guide.md`**: Complete technical markdown manual.
* **`BAYGO_1.1_User_Guide.html`**: Printable A4 PDF user manual (`Ctrl + P` -> Save as PDF).

---

## 📜 License

BAYGO 1.1 is open-source software released under the [BSD 3-Clause License](LICENSE).
