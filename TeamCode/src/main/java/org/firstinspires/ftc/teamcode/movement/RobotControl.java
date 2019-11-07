package org.firstinspires.ftc.teamcode.movement;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.SystemConfig;
public class RobotControl {
    SystemConfig sys = new SystemConfig();
    public MotorGroup top_left, top_right, bot_left, bot_right;
    public MotorGroup left_intake, right_intake;
    public Servo claws;

    private double MAX_SPEED = 5;

    public RobotControl(HardwareMap hmp) {
        top_left = new MotorGroup(hmp, sys.top_left);
        top_right = new MotorGroup(hmp, sys.top_right);
        bot_left = new MotorGroup(hmp, sys.bot_left);
        bot_right = new MotorGroup(hmp, sys.bot_right);
    }

    /**
     * Mechanum strafing
     * For linear motion i.e. NSEW movement set angle to 0
     * Note: motion is defined by the polar vector form (r, theta)
     * @param angle: radian angle rotation (theta component)
     * @param percent_rotation: percent max speed angular rotation
     * @param percent_power: percent max speed vector magnitude (r component)
     */
    public void strafe(double angle, double percent_rotation, double percent_power) {
        double r = percent_power*MAX_SPEED;
        double robotAngle = angle;
        double rightX = percent_rotation*MAX_SPEED; // TODO: make sure this isn't too much
        final double v1 = r * Math.cos(robotAngle) + rightX;l./
        final double v2 = r * Math.sin(robotAngle) - rightX;
        final double v3 = r * Math.sin(robotAngle) + rightX;
        final double v4 = r * Math.cos(robotAngle) - rightX;

        top_left.setPower(v1);
        top_right.setPower(v2);
        bot_left.setPower(v3);
        bot_right.setPower(v4);
    }

    /**
     * Failsafe function for N-S drive (in case the mechanum fails)
     * @param direction: 1 for forward, -1 for backwards
     * @param percent_power: percent full speed
     */
    public void linear_drive(int direction, double percent_power) {
        top_left .setPower(direction*percent_power*MAX_SPEED);
        top_right.setPower(-direction*percent_power*MAX_SPEED);
        bot_left .setPower(direction*percent_power*MAX_SPEED);
        bot_right.setPower(-direction*percent_power*MAX_SPEED);
    }

    /**
     * Full stop function
     */
    public void stop_drive() {
        top_left .setPower(0);
        top_right.setPower(0);
        bot_left .setPower(0);
        bot_right.setPower(0);
    }
}