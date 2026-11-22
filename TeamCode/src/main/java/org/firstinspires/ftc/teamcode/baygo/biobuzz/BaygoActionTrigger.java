package org.firstinspires.ftc.teamcode.baygo.biobuzz;

import com.pedropathing.follower.Follower;
import java.util.ArrayList;
import java.util.List;

public class BaygoActionTrigger {

    private static class Trigger {
        double threshold;
        Runnable action;
        boolean executed;

        Trigger(double threshold, Runnable action) {
            this.threshold = threshold;
            this.action = action;
            this.executed = false;
        }
    }

    private final Follower follower;
    private final List<Trigger> triggers = new ArrayList<>();

    public BaygoActionTrigger(Follower follower) {
        this.follower = follower;
    }

    public BaygoActionTrigger atParametric(double completionRatio, Runnable action) {
        triggers.add(new Trigger(completionRatio, action));
        return this;
    }

    public void reset() {
        for (Trigger t : triggers) {
            t.executed = false;
        }
    }

    public void update() {
        if (follower == null || !follower.isBusy()) return;

        double progress = 0.5;

        for (Trigger t : triggers) {
            if (!t.executed && progress >= t.threshold) {
                t.executed = true;
                if (t.action != null) {
                    t.action.run();
                }
            }
        }
    }
}
