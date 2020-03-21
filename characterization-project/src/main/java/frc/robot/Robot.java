/**
 * This is a very simple robot program that can be used to send telemetry to
 * the data_logger script to characterize your drivetrain. If you wish to use
 * your actual robot code, you only need to implement the simple logic in the
 * autonomousPeriodic function and change the NetworkTables update rate
 */

package frc.robot;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends TimedRobot {
    private static double WHEEL_DIAMETER = 0.1524;
    private static double ENCODER_EDGES_PER_REV = 22300.444444444445;

    private static double G_mps2 = 9.80367; // from WolframAlpha, in Tucson, AZ

    private Joystick stick;
    private DifferentialDrive drive;

    private WPI_TalonFX[] leftMotors;
    private WPI_TalonFX[] rightMotors;

    private NetworkTableEntry autoSpeedEntry = NetworkTableInstance.getDefault().getEntry("/robot/autospeed");
    private NetworkTableEntry telemetryEntry = NetworkTableInstance.getDefault().getEntry("/robot/telemetry");
    private NetworkTableEntry rotateEntry = NetworkTableInstance.getDefault().getEntry("/robot/rotate");

    double priorAutospeed = 0;
    Number[] numberArray = new Number[10 + 5*4];

    AHRS navx;

    double encoderConstant;

    @Override
    public void robotInit() {
        if (!isReal()) SmartDashboard.putData(new SimEnabler());

        stick = new Joystick(0);


        leftMotors = new WPI_TalonFX[2];
        leftMotors[0] = new WPI_TalonFX(2);
        leftMotors[1] = new WPI_TalonFX(3);

        rightMotors = new WPI_TalonFX[3];
        rightMotors[0] = new WPI_TalonFX(1);
        rightMotors[1] = new WPI_TalonFX(4);

        for (int i = 0; i < 2; i++) {
            // differential drive takes care of inversions
            leftMotors[i].setInverted(false);
            leftMotors[i].setNeutralMode(NeutralMode.Brake);
            leftMotors[i].configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);
            leftMotors[i].setSelectedSensorPosition(0);
            // to ensure "raw"est velocity data possible so we can get a realistic noise description
            leftMotors[i].configVelocityMeasurementWindow(1);
            leftMotors[i].configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_1Ms);

            // differential drive takes care of inversions
            rightMotors[i].setInverted(false);
            rightMotors[i].setNeutralMode(NeutralMode.Brake);
            rightMotors[i].configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);
            rightMotors[i].setSelectedSensorPosition(0);
            // to ensure "raw"est velocity data possible so we can get a realistic noise description
            rightMotors[i].configVelocityMeasurementWindow(1);
            rightMotors[i].configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_1Ms);

            if (i != 0) {
                leftMotors[i].follow(leftMotors[0]);
                rightMotors[i].follow(rightMotors[0]);
            }
        }

        navx = new AHRS();

        drive = new DifferentialDrive(
            new SpeedControllerGroup(leftMotors[0], leftMotors[1]),
            new SpeedControllerGroup(rightMotors[0], rightMotors[1])
        );
        drive.setDeadband(0);

        encoderConstant = (1 / ENCODER_EDGES_PER_REV) * WHEEL_DIAMETER * Math.PI;
    }



    // gyro readings
    public double getYaw_rad() {
        return -1 * Math.toRadians(navx.getAngle());
    }

    public double getYawRate_raw_radps() {
        return -1 * Math.toRadians(navx.getRawGyroZ());
    }

    public double getYawRate_processed_radps() {
        return -1 * Math.toRadians(navx.getRate());
    }



    // accelerometer readings
    public double getAccX_raw_mps2() {
        return navx.getRawAccelX() * G_mps2;
    }

    public double getAccX_processed_mps2() {
        return navx.getWorldLinearAccelX() * G_mps2;
    }

    public double getAccY_raw_mps2() {
        return navx.getRawAccelY() * G_mps2;
    }

    public double getAccY_processed_mps2() {
        return navx.getWorldLinearAccelY() * G_mps2;
    }



    // encoder readings
    public double getLeftPosition_m(WPI_TalonFX motor) {
        return motor.getSelectedSensorPosition() * encoderConstant;
    }

    public double getLeftVelocity_mps(WPI_TalonFX motor) {
        return motor.getSelectedSensorVelocity() * encoderConstant * 10;
    }

    public double getRightPosition_m(WPI_TalonFX motor) {
        return -getLeftPosition_m(motor);
    }

    public double getRightVelocity_mps(WPI_TalonFX motor) {
        return -getLeftVelocity_mps(motor);
    }





    @Override
    public void disabledInit() {
        System.out.println("Robot disabled");
        drive.tankDrive(0, 0);
    }

    @Override
    public void disabledPeriodic() {}

    @Override
    public void robotPeriodic() {

    }

    @Override
    public void teleopInit() {
        System.out.println("Robot in operator control mode");
    }

    @Override
    public void teleopPeriodic() {
        drive.arcadeDrive(-stick.getY(), stick.getX());
    }

    @Override
    public void autonomousInit() {
        System.out.println("Robot in autonomous mode");
    }



    int index = 0;
    public void add(Number[] arr, double val) {
        arr[index] = val;
        index++;
    }

    /**
     * If you wish to just use your own robot program to use with the data logging
     * program, you only need to copy/paste the logic below into your code and
     * ensure it gets called periodically in autonomous mode
     *
     * Additionally, you need to set NetworkTables update rate to 10ms using the
     * setUpdateRate call.
     */
    @Override
    public void autonomousPeriodic() {
        // Retrieve values to send back before telling the motors to do something
        double now = Timer.getFPGATimestamp();
        double battery = RobotController.getBatteryVoltage();

        // Retrieve the commanded speed from NetworkTables
        double autospeed = autoSpeedEntry.getDouble(0);
        priorAutospeed = autospeed;

        // command motors to do things
        drive.tankDrive(
            (rotateEntry.getBoolean(false) ? -1 : 1) * autospeed, autospeed,
            false
        );

        index = 0;

        // send telemetry data array back to NT
        add(numberArray, now);
        add(numberArray, battery);
        add(numberArray, autospeed);

        add(numberArray, getYaw_rad());
        add(numberArray, getYawRate_raw_radps());
        add(numberArray, getYawRate_processed_radps());

        add(numberArray, getAccX_raw_mps2());
        add(numberArray, getAccX_processed_mps2());
        add(numberArray, getAccY_raw_mps2());
        add(numberArray, getAccY_processed_mps2());

        for (int i = 0; i < leftMotors.length; i++) {
            add(numberArray, getLeftPosition_m(leftMotors[i]));
            add(numberArray, getLeftVelocity_mps(leftMotors[i]));
            add(numberArray, leftMotors[i].getMotorOutputVoltage());
            add(numberArray, leftMotors[i].getStatorCurrent());
            add(numberArray, leftMotors[i].getSupplyCurrent());
        }

        for (int i = 0; i < rightMotors.length; i++) {
            add(numberArray, getRightPosition_m(rightMotors[i]));
            add(numberArray, getRightVelocity_mps(rightMotors[i]));
            add(numberArray, rightMotors[i].getMotorOutputVoltage());
            add(numberArray, rightMotors[i].getStatorCurrent());
            add(numberArray, rightMotors[i].getSupplyCurrent());
        }

        telemetryEntry.setNumberArray(numberArray);
    }
}