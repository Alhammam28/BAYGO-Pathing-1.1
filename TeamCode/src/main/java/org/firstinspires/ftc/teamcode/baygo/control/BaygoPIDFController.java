package org.firstinspires.ftc.teamcode.baygo.control;

import com.qualcomm.robotcore.util.ElapsedTime;

@SuppressWarnings("unused")
public class BaygoPIDFController {

    private double kP;
    private double kI;
    private double kD;

    private double kS;
    private double kV;
    private double kA;

    private double iZone = Double.MAX_VALUE;
    private double maxIntegral = 1.0;
    private double derivativeLowPassCutoff = 0.8;

    private double errorSum = 0.0;
    private double lastError = 0.0;
    private double lastFilteredDerivative = 0.0;
    private double lastTargetVelocity = 0.0;

    private final ElapsedTime timer = new ElapsedTime();
    private boolean isFirstRun = true;

    public BaygoPIDFController(double kP, double kI, double kD, double kS, double kV, double kA) {
        setPIDF(kP, kI, kD, kS, kV, kA);
    }

    public BaygoPIDFController(double kP, double kI, double kD) {
        this(kP, kI, kD, 0.0, 0.0, 0.0);
    }

    public void setPIDF(double kP, double kI, double kD, double kS, double kV, double kA) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.kS = kS;
        this.kV = kV;
        this.kA = kA;
    }

    public void setIZone(double iZone) {
        this.iZone = Math.abs(iZone);
    }

    public void setMaxIntegral(double maxIntegral) {
        this.maxIntegral = Math.abs(maxIntegral);
    }

    public void setDerivativeLowPassCutoff(double cutoff) {
        this.derivativeLowPassCutoff = Math.max(0.0, Math.min(1.0, cutoff));
    }

    public void reset() {
        errorSum = 0.0;
        lastError = 0.0;
        lastFilteredDerivative = 0.0;
        lastTargetVelocity = 0.0;
        isFirstRun = true;
        timer.reset();
    }

    public double update(double currentPosition, double targetPosition, double targetVelocity) {
        double dt = timer.seconds();
        timer.reset();

        if (dt <= 0.0 || dt > 0.2) dt = 0.005;

        double error = targetPosition - currentPosition;
        double pTerm = kP * error;

        if (Math.abs(error) <= iZone) {
            errorSum += error * dt;
            if (kI > 0.0) {
                double maxErrorSum = maxIntegral / kI;
                errorSum = Math.max(-maxErrorSum, Math.min(maxErrorSum, errorSum));
            }
        } else {
            errorSum = 0.0;
        }
        double iTerm = kI * errorSum;

        double dTerm = 0.0;
        if (!isFirstRun && dt > 0) {
            double rawDerivative = (error - lastError) / dt;
            double filteredDerivative = (derivativeLowPassCutoff * lastFilteredDerivative) + ((1.0 - derivativeLowPassCutoff) * rawDerivative);
            lastFilteredDerivative = filteredDerivative;
            dTerm = kD * filteredDerivative;
        } else {
            isFirstRun = false;
        }
        lastError = error;

        double targetAcceleration = (targetVelocity - lastTargetVelocity) / dt;
        lastTargetVelocity = targetVelocity;

        double fTerm = (Math.signum(targetVelocity) * kS) + (kV * targetVelocity) + (kA * targetAcceleration);

        return Math.max(-1.0, Math.min(1.0, pTerm + iTerm + dTerm + fTerm));
    }

    public double update(double currentPosition, double targetPosition) {
        return update(currentPosition, targetPosition, 0.0);
    }

    public static BaygoPIDFController autoTuneFromSysId(double systemGainK, double timeConstantTau, double deadTimeL) {
        if (systemGainK <= 0 || timeConstantTau <= 0) {
            return new BaygoPIDFController(0.05, 0.0, 0.002, 0.05, 0.015, 0.002);
        }

        double safeL = Math.max(0.01, deadTimeL);
        double lambda = 3.0 * safeL;

        double calcKP = timeConstantTau / (systemGainK * (lambda + safeL));
        double calcKI = calcKP / (4.0 * timeConstantTau);
        double calcKD = calcKP * (safeL / 2.0);

        double calcKV = 1.0 / systemGainK;
        double calcKA = timeConstantTau / systemGainK;
        double calcKS = 0.05;

        BaygoPIDFController controller = new BaygoPIDFController(calcKP, calcKI, calcKD, calcKS, calcKV, calcKA);
        controller.setIZone(2.0);
        return controller;
    }

    public double getKP() { return kP; }
    public double getKI() { return kI; }
    public double getKD() { return kD; }
    public double getKS() { return kS; }
    public double getKV() { return kV; }
    public double getKA() { return kA; }
}
