package org.firstinspires.ftc.teamcode.bot.components;

import com.disnodeteam.dogecommander.Subsystem;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake implements Subsystem {
    private HardwareMap hmp;
    private DcMotor left, right;
    private State state = State.STOP;

    public Intake(HardwareMap hmp) {
        this.hmp = hmp;
    }


    public enum State {
        /**
         * Defines ternary states for intake: INTAKE (IN), SPIT_OUT (OUT), STOP (STOP)
         * Allows for user-input analog power values as well
         */

        INTAKE(1.0),
        SPIT_OUT(-1.0),
        STOP(0.0);

        private final double power;

        State(double power) {
            this.power = power;
        }
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public void initHardware() {
        this.left = hmp.get(DcMotor.class, "intake_left");
        this.right = hmp.get(DcMotor.class, "intake_right");
    }

    @Override
    public void periodic() {
        left.setPower(state.power);
        right.setPower(state.power);
    }
}
