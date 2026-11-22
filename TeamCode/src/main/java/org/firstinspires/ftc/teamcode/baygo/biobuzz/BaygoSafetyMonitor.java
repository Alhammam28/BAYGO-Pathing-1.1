package org.firstinspires.ftc.teamcode.baygo.biobuzz;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.util.ElapsedTime;

public class BaygoSafetyMonitor {

    private final Follower follower;
    private final ElapsedTime stallTimer = new ElapsedTime();

    private double minVelocityThreshold = 0.5;
    private double maxStallTimeSeconds = 0.75;
    private boolean isStalled = false;

    public BaygoSafetyMonitor(Follower follower) {
        this.follower = follower;
    }

    public BaygoSafetyMonitor setStallThresholds(double minVelocity, double maxStallTime) {
        this.minVelocityThreshold = minVelocity;
        this.maxStallTimeSeconds = maxStallTime;
        return this;
    }

    public void update() {
        if (follower == null || !follower.isBusy()) {
            stallTimer.reset();
            isStalled = false;
            return;
        }

        double currentVelocity = 1.0;

        if (currentVelocity < minVelocityThreshold) {
            if (stallTimer.seconds() > maxStallTimeSeconds) {
                isStalled = true;
            }
        } else {
            stallTimer.reset();
            isStalled = false;
        }
    }

    public boolean isStalled() { return isStalled; }
}
