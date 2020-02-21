package org.firstinspires.ftc.teamcode.HOMAR.internal.controller;

import org.firstinspires.ftc.teamcode.HOMAR.internal.sensor.Sensor;

//Created by Gabriel on 2018-01-02.

/**
 *
 */

public class FinishableIntegratedController extends IntegratedController {
    /**
     *
     */
    public FinishingAlgorithm finisher;

    /**
     * @param sensor
     * @param algorithm
     * @param finisher
     */
    public FinishableIntegratedController(Sensor sensor, PIDController algorithm, FinishingAlgorithm finisher) {
        super(sensor, algorithm);
        this.finisher = finisher;
    }

    /**
     * @param target
     */
    @Override
    public void setTarget(double target) {
        finisher.setTarget(target); //We need to set the finisher's target before super.setTarget updates the finisher. This mistake took a while to find.
        super.setTarget(target);
    }

    /**
     * @return
     */
    @Override
    public double update() {
        double sensorValue = super.update();
        finisher.input(sensorValue);
        return sensorValue;
    }

    /**
     * @return
     */
    public boolean finished() {
        return finisher.finished();
    }
}
