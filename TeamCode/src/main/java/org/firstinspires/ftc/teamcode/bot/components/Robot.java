package org.firstinspires.ftc.teamcode.bot.components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.HOMAR.internal.controller.ErrorTimeThresholdFinishingAlgorithm;
import org.firstinspires.ftc.teamcode.HOMAR.internal.controller.FinishableIntegratedController;
import org.firstinspires.ftc.teamcode.HOMAR.internal.controller.PIDController;
import org.firstinspires.ftc.teamcode.HOMAR.internal.drivetrain.HeadingableMecanumDrivetrain;
import org.firstinspires.ftc.teamcode.HOMAR.internal.drivetrain.MecanumDrivetrain;
import org.firstinspires.ftc.teamcode.HOMAR.internal.sensor.IntegratingGyroscopeSensor;
import org.firstinspires.ftc.teamcode.SystemConfig;

public class Robot {
    //private FinishableIntegratedController controller;
    public MecanumDrivetrain drive;
    private Servo l_foundation, r_foundation;
    private boolean FOUNDATION_UP;
    private Intake intake;
    private CraneLift lift;
    private Camera camera;
    private int TIME_THRESHOLD = 1500;
    private boolean drive_sleeping = false; private double drive_sleep_duration = 0; private double drive_sleep_start = 0;
    private boolean lift_sleeping = false; private double lift_sleep_duration = 0; private double lift_sleep_start = 0;

    private final int TURN_90_TIME = 41;

    public static double  RIGHT_FOUNDATION_UP = 1,
            RIGHT_FOUNDATION_DOWN  = 0.24,
            LEFT_FOUNDATION_UP   = 0.1,
            LEFT_FOUNDATION_DOWN = 0.87;


    //  default 2, 0, 0, 12 for GoBilda
    public static float NEW_P = 6;
    public static float NEW_I = 0;
    public static float NEW_D = 0;
    public static float NEW_F = 12;
    public static float POS_P = 6;

    public static int speed_toggle_state = 0;
    public static double speed_toggle = 1;

    public Robot(HardwareMap hmp, Telemetry t) {
        // configure motors
        DcMotorEx frontLeft = hmp.get(DcMotorEx.class, "driveFrontLeft");
        DcMotorEx frontRight = hmp.get(DcMotorEx.class, "driveFrontRight");
        DcMotorEx backLeft = hmp.get(DcMotorEx.class, "driveBackLeft");
        DcMotorEx backRight = hmp.get(DcMotorEx.class, "driveBackRight");

        frontRight.setPositionPIDFCoefficients(POS_P);
        frontLeft.setPositionPIDFCoefficients(POS_P);
        backLeft.setPositionPIDFCoefficients(POS_P);
        backRight.setPositionPIDFCoefficients(POS_P);

        PIDFCoefficients pidfLeftFrontNew = new PIDFCoefficients(NEW_P, NEW_I, NEW_D, NEW_F);
        frontRight.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfLeftFrontNew);
        PIDFCoefficients pidfLeftRearNew = new PIDFCoefficients(NEW_P, NEW_I, NEW_D, NEW_F);
        frontLeft.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfLeftRearNew);
        PIDFCoefficients pidfRightFrontNew = new PIDFCoefficients(NEW_P, NEW_I, NEW_D, NEW_F);
        backLeft.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfRightFrontNew);
        PIDFCoefficients pidfRightRearNew = new PIDFCoefficients(NEW_P, NEW_I, NEW_D, NEW_F);
        backRight.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfRightRearNew);

        frontRight.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        frontLeft.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        frontRight.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        frontLeft.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

//        frontRight.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
//        frontLeft.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
//        backLeft.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
//        backRight.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);

        // config foundation servos

        l_foundation = hmp.get(Servo.class, SystemConfig.left_foundation_servo);
        r_foundation = hmp.get(Servo.class, SystemConfig.right_foundation_servo);
        FOUNDATION_UP = true;

        // configure foundation grabber servos

        intake = new Intake(hmp);

        // configure crane lift
        lift = new CraneLift(hmp, t);

        //camera = new Camera(hmp, t);

        // configure outerIntake motors
        drive = new MecanumDrivetrain(new DcMotorEx[]{frontLeft, frontRight, backLeft, backRight});
        /*gyro = hmp.get(BNO055IMUImpl.class, "gyro");

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();  //Figure out why the naive one doesn't have a public constructor
        gyro.initialize(parameters);
        while (!gyro.isGyroCalibrated());

        controller =    new FinishableIntegratedController(new IntegratingGyroscopeSensor(gyro),
                        new PIDController(1, 1, 1), // default values to 1, will tune later
                        new ErrorTimeThresholdFinishingAlgorithm(Math.PI/50, 1));

        drive = new HeadingableMecanumDrivetrain(new DcMotorEx[]{frontLeft, frontRight, backLeft, backRight},
                controller);*/
//        h_extend_full();
//        pause(600);
//        turnerin();
//        pause(600);
//        h_retract();
    }

    public Robot(HardwareMap hmp) {
        // configure motors
        DcMotorEx frontLeft = hmp.get(DcMotorEx.class, "driveFrontLeft");
        DcMotorEx frontRight = hmp.get(DcMotorEx.class, "driveFrontRight");
        DcMotorEx backLeft = hmp.get(DcMotorEx.class, "driveBackLeft");
        DcMotorEx backRight = hmp.get(DcMotorEx.class, "driveBackRight");

        frontRight.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        frontLeft.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        frontRight.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        frontLeft.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        drive = new HeadingableMecanumDrivetrain(new DcMotorEx[]{frontLeft, frontRight, backLeft, backRight}, null);

        // config foundation servos

        l_foundation = hmp.get(Servo.class, SystemConfig.left_foundation_servo);
        r_foundation = hmp.get(Servo.class, SystemConfig.right_foundation_servo);

        // configure foundation grabber servos

        intake = new Intake(hmp);

        // configure crane lift
        lift = new CraneLift(hmp, null);

        //camera = new Camera(hmp, null);

        /*gyro = hmp.get(BNO055IMUImpl.class, "gyro");

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();  //Figure out why the naive one doesn't have a public constructor
        gyro.initialize(parameters);
        while (!gyro.isGyroCalibrated());

        controller =    new FinishableIntegratedController(new IntegratingGyroscopeSensor(gyro),
                        new PIDController(1, 1, 1), // default values to 1, will tune later
                        new ErrorTimeThresholdFinishingAlgorithm(Math.PI/50, 1));

        drive = new HeadingableMecanumDrivetrain(new DcMotorEx[]{frontLeft, frontRight, backLeft, backRight},
                controller);*/
    }

    /**
     *
     * @return -1 for left, 0 for center, 1 for right, 2 for not found
     */
    final double CENTER_POS = -20.0;
    final double TOLERANCE = 1.1;
    public int get_skystone_pos() {
        double x = camera.scan_for_stone();
        if (x == 0) return 2;
        else if (x >= CENTER_POS-TOLERANCE && x <= CENTER_POS+TOLERANCE) return 0;
        else if (x <= CENTER_POS-TOLERANCE) return 1;
        return 2;
    }

    /**
     *
     * @param power
     * @param encoder_vals: encoder positions in this order: {front_r, front_l, rear_r, rear_l}
     */
    // make sure to stagger order of motor engagement so that one side of robot does not turn on / off before the other side
    public void encoder_drive(double power, int[] encoder_vals) {
        if (isDrive_sleeping()) return;

        for (int i = 0; i < 4; i ++) {
            drive.motors[i].setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
            //set dummy position before changing runmode
            drive.motors[i].setTargetPosition(0);
            drive.motors[i].setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            int new_pos = encoder_vals[i]*(1125 / ((42 / 35) * (32)) + drive.motors[i].getCurrentPosition());
            drive.motors[i].setTargetPosition(new_pos);
            drive.motors[i].setPower(power);
        }

//        drive.motors[0].setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
//        //set dummy position before changing runmode
//        drive.motors[0].setTargetPosition(0);
//        drive.motors[0].setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
//        int new_pos_0 = encoder_vals[0]*(1125 / ((42 / 35) * (32)) + drive.motors[0].getCurrentPosition());
//        drive.motors[0].setTargetPosition(new_pos_0);
//
//        drive.motors[2].setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
//        //set dummy position before changing runmode
//        drive.motors[2].setTargetPosition(0);
//        drive.motors[2].setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
//        int new_pos_2 = encoder_vals[2]*(1125 / ((42 / 35) * (32)) + drive.motors[2].getCurrentPosition());
//        drive.motors[2].setTargetPosition(new_pos_2);
//
//        drive.motors[1].setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
//        //set dummy position before changing runmode
//        drive.motors[1].setTargetPosition(0);
//        drive.motors[1].setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
//        int new_pos_1 = encoder_vals[1]*(1125 / ((42 / 35) * (32)) + drive.motors[1].getCurrentPosition());
//        drive.motors[1].setTargetPosition(new_pos_1);
//
//        drive.motors[3].setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
//        //set dummy position before changing runmode
//        drive.motors[3].setTargetPosition(0);
//        drive.motors[3].setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
//        int new_pos_3 = encoder_vals[3]*(1125 / ((42 / 35) * (32)) + drive.motors[3].getCurrentPosition());
//        drive.motors[3].setTargetPosition(new_pos_3);
//
//        //set power to motors in x shape
//        drive.motors[0].setPower(power);
//        drive.motors[1].setPower(power);
//        drive.motors[2].setPower(power);
//        drive.motors[3].setPower(power);


        double time = System.currentTimeMillis();

        while ((drive.motors[0].isBusy() || drive.motors[1].isBusy() || drive.motors[2].isBusy() || drive.motors[3].isBusy()) &&
                ((System.currentTimeMillis()-time <= TIME_THRESHOLD))) {
        }

        for (int i = 0; i < 4; i ++) {
            drive.motors[i].setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
            drive.motors[i].setPower(0);
            drive.motors[i].setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        }

//        drive.motors[0].setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
//        drive.motors[0].setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
//
//        drive.motors[1].setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
//        drive.motors[1].setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
//
//        drive.motors[2].setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
//        drive.motors[2].setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
//
//        drive.motors[3].setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
//        drive.motors[3].setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
//
//        //set power to motors in x shape
//        drive.motors[0].setPower(0);
//        drive.motors[1].setPower(0);
//        drive.motors[2].setPower(0);
//        drive.motors[3].setPower(0);
    }

    public void set_threshold(int val) {
        TIME_THRESHOLD = val;
    }

    public void intake() {
    intake.intake();
    }
    public void spit() {
    intake.spit();
    }
    public void stop_intake() {
         intake.stop();
    }
    public void servointake() { intake.servo_intake();}

    public void drive_forward(double speed, int distance) {
        encoder_drive(speed, new int[]{-distance, distance, -distance, distance});
    }
    public void drive_backward(double speed, int distance) {
        encoder_drive(speed, new int[]{distance, -distance, distance, -distance});
    }
    public void strafe_right(double speed, int distance) {
        encoder_drive(speed, new int[]{-distance, -distance, distance, distance});
    }
    public void strafe_left(double speed, int distance) {
        encoder_drive(speed, new int[]{distance, distance, -distance, -distance});
    }
    public void turn_90_ccw(double speed) {
        turn(speed, -90);
    }
    public void turn_90_cw(double speed) {
        turn(speed, 90);
    }
    public void turn(double speed, double angle) {
        int sign = (int) (angle/90 * TURN_90_TIME);
        encoder_drive(speed, new int[]{sign, sign, sign, sign});
    }

    public void raise_foundations() {
        l_foundation.setPosition(LEFT_FOUNDATION_UP);
        r_foundation.setPosition(RIGHT_FOUNDATION_UP);
    }
    public void lower_foundations() {
        l_foundation.setPosition(LEFT_FOUNDATION_DOWN);
        r_foundation.setPosition(RIGHT_FOUNDATION_DOWN);
    }
    public void toggle_foundation() {
        if (FOUNDATION_UP) lower_foundations();
        else raise_foundations();
        FOUNDATION_UP = !FOUNDATION_UP;
    }
    public void vlifttolevel() {if (!isLift_sleeping()) lift.lift_to_level();}
    public void vdisengagebylevel() {if (!isLift_sleeping()) lift.disengage_by_level();}
    public void vraise_lift_by_ticks(int ticks) { if (!isLift_sleeping()) lift.lift_by_ticks(ticks); }
    public void vretractlift() {if (!isLift_sleeping()) lift.vretract();}
    public void vhold() {if (!isLift_sleeping()) lift.vstop();}
    public void vslack() {if (!isLift_sleeping()) lift.slack();}
    public void vglideup() {if (!isLift_sleeping()) lift.v_glide_up();}
    public void vglidedown() {if (!isLift_sleeping()) lift.v_glide_down();}
    public void vgroundlevel() {if (!isLift_sleeping()) lift.lift_by_ticks(0);}
    public void updateheight(int change) {if (!isLift_sleeping()) lift.update_height(change);}
    public void updatestatus(boolean stationary) {lift.stationarystatus(stationary);}

    public void hextend_toggle() {
        if (!isLift_sleeping()) lift.htoggle();
    }
    public void h_grabber_pos() {if (!isLift_sleeping()) lift.h_grabber_bot();}
    public void h_extend() {if (!isLift_sleeping()) lift.hteleextend();}
    public void h_extend_full() {if (!isLift_sleeping()) lift.hmaxextend();}
    public void h_engage() {if (!isLift_sleeping()) lift.hengage();}
    public void h_auto_extend() {if (!isLift_sleeping()) lift.hautoextend();}
    public void h_retract() {if (!isLift_sleeping()) lift.hretract();}
    public void place_capstone () {if (!isLift_sleeping()) lift.toggle_capstone();}

    public void toggle_turner() {
        if (lift.H_FULLY_EXTENDED && !isLift_sleeping()) lift.toggle_rotator();
    }
    //TODO:ADD FOR MANUAL CONTROL?
    public void t_capstone_pos() {if (!isLift_sleeping()) lift.capstone_turn();}

    public void turnerout() {if (!isLift_sleeping()) lift.turner_out();}
    public void turnerin() {if (!isLift_sleeping()) lift.turner_in();}

    public void grab_stone() { if (!isLift_sleeping()) lift.grab_stone(); }
    public void drop_stone() { if (!isLift_sleeping()) lift.drop_stone(); }

    public void toggle_grabber() {
        lift.toggle_grabber();
    }

    public void toggle_speed(double vel) {
        speed_toggle=vel;
    }
    public void xbox_drive(double move_x, double move_y, double turn_x) {
        double course = Math.atan2(-move_y, move_x) - Math.PI/2;
        double velocity = Math.hypot(move_x, move_y);
        double rotation = -.85*turn_x;
        power_drive(course, velocity, rotation);
    }

    /**
     * Mechanum drive by power vector
     * @param course: angle repective to positive x-axis
     * @param velocity: magnitude
     * @param rotation: rotation speed around center of mass
     */
     public void power_drive(double course, double velocity, double rotation) {
         // TODO: add these conditions everywhere
         if (isDrive_sleeping()) return;
         drive.setCourse(course);
         drive.setVelocity(speed_toggle*velocity);
         drive.setRotation(speed_toggle*rotation);
     }

     public void stop() {
         power_drive(0, 0, 0);
     }

     public void pause(long time) {
         double t = System.currentTimeMillis();
         while (System.currentTimeMillis()-t < time);
     }

     public void pause_drive(long time) {
         drive_sleeping = true;
         drive_sleep_start = System.currentTimeMillis();
         drive_sleep_duration = time;
         drive.setVelocity(0);
     }

     public boolean isDrive_sleeping() {
         if (drive_sleeping){
             if (System.currentTimeMillis()-drive_sleep_start < drive_sleep_duration) {
                 return true;
             } else {
                 drive_sleeping = false;
                 return false;
             }
         } else {
             drive_sleeping = false;
             return false;
         }
     }

     public void pause_lift(long time) {
         lift_sleeping = true;
         lift_sleep_start = System.currentTimeMillis();
         lift_sleep_duration = time;
     }

    public boolean isLift_sleeping() {
        if (lift_sleeping){
            if (System.currentTimeMillis()-lift_sleep_start < lift_sleep_duration) {
                return true;
            } else {
                lift_sleeping = false;
                return false;
            }
        } else {
            drive_sleeping = false;
            return false;
        }
    }

     public void print_encoder_vals(Telemetry t) {
         t.addData("Front Left:", drive.motors[0].getCurrentPosition());
         t.addData("Front Right:", drive.motors[1].getCurrentPosition());
         t.addData("Back Left:", drive.motors[2].getCurrentPosition());
         t.addData("Back Right:", drive.motors[3].getCurrentPosition());
     }

     public void print_servo_vals(Telemetry t) {
         t.addData("Lift Level:", lift.height);
         t.addData("Stationary:", lift.IS_STATIONARY);
         t.addData("Turner Servo:", String.format("%.2f [%s]", lift.turner_pos(), lift.rotator_out ? "Rotated out" : "Not fully rotated"));
         t.addData("Horizontal Servo:", String.format("%.2f [%s]", lift.extender_pos(), lift.H_FULLY_EXTENDED ? "Fully extended" : "Not full extension"));
         t.addData("Grabber Servo:", String.format("%.2f [%s]", lift.grabber_pos(), lift.grabber_state==1 ? "Grabbing" : "Open"));
         t.addData("Left Lift:", lift.VLEFT_POS);
         t.addData("Right Lift:", lift.VRIGHT_POS);

     }

//    @Override
//    public void initHardware() {
//
//    }
//
//    @Override
//    public void periodic() {
//
//    }

    //public PIDTuner getPIDTuner(Gamepad pad, Telemetry t) { return new PIDTuner(drive, (PIDController) controller.algorithm, pad, t); }
}