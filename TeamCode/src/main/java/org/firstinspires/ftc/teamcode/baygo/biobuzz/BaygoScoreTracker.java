package org.firstinspires.ftc.teamcode.baygo.biobuzz;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class BaygoScoreTracker {

    private static int autoHighGoalsScored = 0;
    private static int teleOpHighGoalsScored = 0;

    public static final int HIGH_GOAL_POINTS_AUTO = 6;
    public static final int HIGH_GOAL_POINTS_TELEOP = 3;

    public static void recordAutoScore() { autoHighGoalsScored++; }
    public static void recordTeleOpScore() { teleOpHighGoalsScored++; }
    public static void reset() {
        autoHighGoalsScored = 0;
        teleOpHighGoalsScored = 0;
    }

    public static int getTotalScore() {
        return (autoHighGoalsScored * HIGH_GOAL_POINTS_AUTO) + (teleOpHighGoalsScored * HIGH_GOAL_POINTS_TELEOP);
    }

    public static void displayScore(Telemetry telemetry) {
        if (telemetry == null) return;
        telemetry.addData("[BAYGO Score] Auto Goals", autoHighGoalsScored);
        telemetry.addData("[BAYGO Score] TeleOp Goals", teleOpHighGoalsScored);
        telemetry.addData("[BAYGO Score] Total Points", getTotalScore());
    }
}
