# ⚡ BAYGO 1.1 - Complete FTC Framework

[![FTC SDK Version](https://img.shields.io/badge/FTC%20SDK-v12.0.0-blue.svg)](https://github.com/FIRST-Tech-Challenge/FTCRobotController)
[![Season](https://img.shields.io/badge/Season-BIOBUZZ%202026--2027-orange.svg)]()
[![License](https://img.shields.io/badge/License-BSD%203--Clause-green.svg)](LICENSE)

**BAYGO 1.1** is a self-contained, high-performance FTC robotics framework built specifically for the **BIOBUZZ (2026-2027)** season. It combines non-blocking `OpMode` architecture, real-time battery voltage compensation, ballistics kinematics, auto-aim driver assistance, wall departure curves, and dynamic JSON persistence.

---

## 🚀 Key Framework Features

* **⚡ Non-Blocking OpMode Architecture:** Event-driven control loop design (`init()`, `init_loop()`, `loop()`, `stop()`).
* **🎯 BIOBUZZ Shooter Kinematics (`BaygoShooterKinematics`):** Real-time horizontal distance, turret yaw, and flywheel RPM calculation based on projectile ballistics.
* **🎮 TeleOp Auto-Pilot (`BaygoTeleOpDriver`):** Target goal auto-aiming and coordinate snapping bound to customizable gamepad buttons.
* **⚡ Real-Time Battery Compensation (`BaygoVoltageCompensator`):** Automatic voltage scaling across peak (14.4V) and depleted (10.0V) battery levels.
* **📁 Dynamic Persistence (`BaygoConfigManager`):** Instant load/save parameters without re-compiling (`/sdcard/FIRST/baygo_config.json`).
* **🧱 Wall Departure Curve (`BaygoWallAutoAlign`):** Zeros localizer against field wall corners and drives a curved Bézier departure arc to eliminate human placement error.
* **🔄 Seamless Match Transition (`BaygoPoseStorage`):** Preserves ending autonomous pose and transfers it automatically into TeleOp.
* **🎛️ Advanced Mechanism PIDF (`BaygoMechanismPIDF`):** Rapid RPM recovery boost for shooter flywheels and gravity compensation for rotational arms and linear lifts.
* **📊 Panels Integration Bridge (`BaygoPanelsManager`):** Built-in bridge connecting live field overlays and telemetries to web dashboards.

---

## 📂 Repository Layout

```
TeamCode/
└── src/main/java/org/firstinspires/ftc/teamcode/baygo/
    ├── Constants.java                       # Initialization Factory
    ├── Tuning.java                          # Global Tuning Definitions
    │
    ├── biobuzz/                             # Competition Season Modules
    │   ├── BaygoShooterKinematics.java      # Kinematics & Flywheel RPM Calculator
    │   ├── BaygoTeleOpDriver.java           # Auto-Aim & Pose Snap Driver Assistant
    │   ├── BaygoAutoManager.java            # Driver Station Strategy & Delay Selector
    │   ├── BaygoWallAutoAlign.java          # Wall Corner Zeroing & Departure Arc
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
