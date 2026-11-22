# BAYGO 1.1 - FTC Autonomous & TeleOp Framework User Manual
**Official Documentation Guide for BIOBUZZ (2026-2027) Season**

---

## 🚀 Executive Summary

**BAYGO 1.1** is a high-performance, modular autonomous and TeleOp framework for FIRST Tech Challenge (FTC) robotics. It combines non-blocking event-driven architecture (`OpMode`), real-time battery voltage compensation, shooter projectile kinematics, gamepad auto-aiming, wall departure curves, and instant JSON parameter persistence.

---

## 📂 Package Architecture

```
TeamCode/src/main/java/org/firstinspires/ftc/teamcode/baygo/
├── Constants.java                       # Framework Initialization Factory
├── Tuning.java                          # Global Tuning Parameters
│
├── biobuzz/                             # Competition Season Modules
│   ├── BaygoShooterKinematics.java      # Shooter Kinematics, Distance, Turret Angle & RPM
│   ├── BaygoTeleOpDriver.java           # Auto-Aim & Gamepad Pose Lock Driver Assistant
│   ├── BaygoAutoManager.java            # Driver Station Strategy & Delay Selector
│   ├── BaygoWallAutoAlign.java          # Wall Corner Zeroing & Departure Curve
│   ├── BaygoPoseStorage.java            # Auto-to-TeleOp Seamless Pose Transfer
│   ├── BaygoActionTrigger.java          # Mid-Path Event Action Hooks
│   ├── BaygoSafetyMonitor.java          # Stall Protection & Anti-Collision System
│   ├── BaygoDodgeManager.java           # Dynamic Path Obstacle Bypass
│   ├── BaygoScoreTracker.java           # Real-Time Match Points Tracker
│   ├── BaygoStatusSignals.java          # Controller Rumble Signal Feedback
│   ├── BaygoTeleOpExample.java          # Standard OpMode TeleOp Implementation
│   └── BaygoPathExample.java            # Autonomous Path Trajectory Example
│
├── config/                              # Persistence & Battery Adaptations
│   ├── BaygoConfigManager.java          # Dynamic JSON Persistence (/sdcard/FIRST/baygo_config.json)
│   ├── BaygoPanelsManager.java          # Panels & Dashboard Integration Bridge
│   └── BaygoVoltageCompensator.java     # Real-Time Battery Voltage Compensation
│
└── control/                             # Advanced Control Algorithms
    ├── BaygoPIDFController.java         # Low-Pass Filtered & Anti-Windup PIDF
    ├── BaygoMechanismPIDF.java          # Flywheel Recovery & Arm/Lift Gravity Comp
    └── BaygoStepTester.java             # Interactive Step Response Tester
```

---

## 🎯 Key Competition Features

### 1. BIOBUZZ Shooter Kinematics (`BaygoShooterKinematics`)
Calculates the exact horizontal distance, required turret yaw angle, and flywheel RPM based on projectile motion:

$$v_0 = \sqrt{\frac{g \cdot d^2}{2 \cdot \cos^2(\phi) \cdot \left(d \cdot \tan(\phi) - \Delta Z\right)}}$$

```java
// Calculate target distance and turret angle with shooter offset
BaygoShooterKinematics.ShooterTargetData data = BaygoShooterKinematics.calculateTargetData(
        robotPose, goalPose, -4.0, 0.0 // 4 inch rear offset
);

double rpm = BaygoShooterKinematics.calculateRequiredVelocity(data.horizontalDistance, 24.0, 45.0);
```

### 2. TeleOp Auto-Pilot & Pose Snap (`BaygoTeleOpDriver`)
Hold target shooting coordinates or auto-aim heading to the high goal with a single button press:

```java
teleOpDriver = new BaygoTeleOpDriver(follower)
        .setGoalPosition(72.0, 126.0)
        .addPreset("shooting", 36.0, 48.0, 45.0)
        .setSnapButton(BaygoTeleOpDriver.Button.X)
        .setAutoAimButton(BaygoTeleOpDriver.Button.LEFT_TRIGGER);
```

### 3. Rapid Flywheel RPM Recovery (`BaygoMechanismPIDF`)
When a ball enters the flywheel, RPM drops momentarily. The recovery controller applies maximum boost ($100\%$ power) for a split second to rapidly recover target RPM before the next shot.

### 4. Wall Departure Arc (`BaygoWallAutoAlign`)
Aligns the robot against the physical field wall at match start to eliminate human placement error, then drives a curved Bézier departure arc to clear the wall without scraping.

### 5. Auto-to-TeleOp Pose Inheritance (`BaygoPoseStorage`)
Automatically preserves the autonomous end pose and restores it upon TeleOp initialization, preserving field-centric orientation without manual zeroing.

---

## ⚙️ Configuration & No-Recompile JSON Tuning

All PIDF parameters and drivetrain configurations are stored in `/sdcard/FIRST/baygo_config.json`. Parameters can be adjusted live on the Driver Station or Panels dashboard without re-compiling in Android Studio.

---

## 📜 License
BAYGO 1.1 is open-source software released under the BSD 3-Clause License.
