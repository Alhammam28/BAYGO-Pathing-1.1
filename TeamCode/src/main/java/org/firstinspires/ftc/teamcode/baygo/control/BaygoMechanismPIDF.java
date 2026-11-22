package org.firstinspires.ftc.teamcode.baygo.control;

import com.qualcomm.robotcore.util.ElapsedTime;

public class BaygoMechanismPIDF {

    public enum MechanismType {
        SHOOTER_FLYWHEEL,
        ROTATIONAL_ARM,
        LINEAR_LIFT
    }

    private final MechanismType type;

    private double kP;
    private double kI;
    private double kD;

    private double kS;
    private double kV;
    private double kG;

    private double targetVelocity = 0.0;
    private double targetPosition = 0.0;

    private double errorSum = 0.0;
    private double lastError = 0.0;
    private double lastFilteredDerivative = 0.0;

    private boolean enableRecoveryBoost = true;
    private double recoveryThresholdRatio = 0.90;

    private final ElapsedTime timer = new ElapsedTime();
    private boolean isFirstRun = true;

    public BaygoMechanismPIDF(MechanismType type, double kP, double kI, double kD, double kF) {
        this.type = type;
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.kV = kF;
    }

    public BaygoMechanismPIDF setFeedforwards(double kS, double kV, double kA, double kG) {
        this.kS = kS;
        this.kV = kV;
        this.kG = kG;
        return this;
    }

    public void reset() {
        errorSum = 0.0;
        lastError = 0.0;
        lastFilteredDerivative = 0.0;
        isFirstRun = true;
        timer.reset();
    }

    public double updateShooterVelocity(double currentVelocity, double targetVelocity) {
        this.targetVelocity = targetVelocity;
        double dt = timer.seconds();
        timer.reset();

        if (dt <= 0.0 || dt > 0.2) dt = 0.005;

        double error = targetVelocity - currentVelocity;

        if (enableRecoveryBoost && targetVelocity > 0 && currentVelocity < (targetVelocity * recoveryThresholdRatio)) {
            return 1.0;
        }

        double pTerm = kP * error;
        errorSum += error * dt;
        double iTerm = kI * errorSum;

        double dTerm = 0.0;
        if (!isFirstRun && dt > 0) {
            double rawDerivative = (error - lastError) / dt;
            double filteredDerivative = (0.8 * lastFilteredDerivative) + (0.2 * rawDerivative);
            lastFilteredDerivative = filteredDerivative;
            dTerm = kD * filteredDerivative;
        } else {
            isFirstRun = false;
        }
        lastError = error;

        double fTerm = (Math.signum(targetVelocity) * kS) + (kV * targetVelocity);
        return Math.max(-1.0, Math.min(1.0, pTerm + iTerm + dTerm + fTerm));
    }

    public double updateMechanismPosition(double currentPosition, double targetPosition) {
        this.targetPosition = targetPosition;
        double dt = timer.seconds();
        timer.reset();

        if (dt <= 0.0 || dt > 0.2) dt = 0.005;

        double error = targetPosition - currentPosition;
        double pTerm = kP * error;

        errorSum += error * dt;
        double iTerm = kI * errorSum;

        double dTerm = 0.0;
        if (!isFirstRun && dt > 0) {
            double rawDerivative = (error - lastError) / dt;
            dTerm = kD * rawDerivative;
        } else {
            isFirstRun = false;
        }
        lastError = error;

        double gravityTerm = 0.0;
        if (type == MechanismType.ROTATIONAL_ARM) {
            gravityTerm = kG * Math.cos(Math.toRadians(currentPosition));
        } else if (type == MechanismType.LINEAR_LIFT) {
            gravityTerm = kG;
        }

        return Math.max(-1.0, Math.min(1.0, pTerm + iTerm + dTerm + gravityTerm));
    }

    public double getTargetVelocity() { return targetVelocity; }
    public double getTargetPosition() { return targetPosition; }
}
