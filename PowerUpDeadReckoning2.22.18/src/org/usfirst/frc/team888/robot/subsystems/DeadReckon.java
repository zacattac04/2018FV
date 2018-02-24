package org.usfirst.frc.team888.robot.subsystems;

import org.usfirst.frc.team888.robot.RobotMap;
import org.usfirst.frc.team888.robot.subsystems.DriveTrain;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DeadReckon extends Subsystem {

	protected Timer timer;
	protected DriveTrain drive;

	protected double angle;
	protected double encoderLeftValue;
	protected double encoderRightValue;
	protected double lastEncoderLeft;
	protected double lastEncoderRight;
	protected double lastHeading;
	protected double changeInEncoderLeft;
	protected double changeInEncoderRight;
	protected double changeInHeading;
	protected double changeInDistance;
	protected double changeInX;
	protected double changeInY;
	protected double clickPosX;
	protected double clickPosY;
	protected double time;
	protected double lastTime;
	protected double timePassed;
	protected double speed;
	protected double posX;
	protected double posY;
	protected double heading;

	protected boolean calibrated;

	public DeadReckon(DriveTrain p_drive) {
		timer = new Timer();
		drive = p_drive;

		clickPosX = 0;
		clickPosY = 0;
		lastEncoderLeft = 0;
		lastEncoderRight = 0;
		heading = 0;
		posX = 0;
		posY = 0;

		reset();
	}

	public void updateTracker() {
		updateEncoderVals();

		changeInEncoderLeft = encoderLeftValue - lastEncoderLeft;
		changeInEncoderRight = encoderRightValue - lastEncoderRight;
		changeInDistance = (changeInEncoderLeft + changeInEncoderRight) / 2;
		changeInHeading = (changeInEncoderLeft - changeInEncoderRight) / RobotMap.WHEEL_BASE;
		angle = heading + (changeInHeading / 2);

		timePassed = time - lastTime;

		changeInX = changeInDistance * Math.sin(angle);
		changeInY = changeInDistance * Math.cos(angle);
		clickPosX += changeInX;
		clickPosY += changeInY;
		heading += changeInHeading;

		speed = ((Math.sqrt(Math.pow(changeInX, 2) + Math.pow(changeInY, 2)) / RobotMap.CLICKS_PER_INCH) / 12)
				/ (timePassed);

		posX = clickPosX / RobotMap.CLICKS_PER_INCH;
		posY = clickPosY / RobotMap.CLICKS_PER_INCH;
	}

	public void updateDashborad() {
		SmartDashboard.putNumber("X Position", posX);
		SmartDashboard.putNumber("Y Position", posY);
		SmartDashboard.putNumber("Heading", Math.toDegrees(heading));
		SmartDashboard.putNumber("Speed", speed);
		SmartDashboard.putNumber("DeltaTime", timePassed);
	}

	/**
	 * Updates n and n-1 encoder value variables.
	 */
	private void updateEncoderVals() {

		lastHeading = heading;
		lastTime = time;
		time = timer.get();

		int[] vals = drive.getEncoderVals();
		if (calibrated) {
			lastEncoderLeft = encoderLeftValue;
			lastEncoderRight = encoderRightValue;
		} else {
			lastEncoderLeft = vals[0];
			lastEncoderRight = vals[1];
			calibrated = true;
		}
		encoderLeftValue = vals[0];
		encoderRightValue = vals[1];
	}

	/**
	 * Resets tracking values to 0 or default.
	 */
	public void reset() {
		drive.resetEncoderPositions();
		
		encoderLeftValue = 0;
		encoderRightValue = 0;

		time = timer.get();
		
		calibrated = false;
	}

	public void startTimer() {
		timer.reset();
		timer.start();
	}

	/**
	 * Accessor to get the position logged by the encoders in an array.
	 * 
	 * @return Returns a double array in format {x, y}
	 */
	public double[] getPos() {
		double[] toReturn = {posX, posY};
		return toReturn;
	}

	/**
	 * @return Returns the heading (in radians between 0 and 2Pi) logged by the encoders.
	 */
	public double getHeading() {
		return heading;
	}

	/**
	 * @return Returns the data for the navigation class
	 */
	public double[] getNavLocationData() {
		double[] i = {changeInEncoderLeft, changeInEncoderRight, heading};
		return i;
	}

	/**
	 * Converts the angle measurement to a range of 0 - 2Pi.
	 * @param angle Angle measurement in radians.
	 * @return Angle measurement in radians from 0 - 2Pi.
	 */
	public static double absAngle(double angle) {
		angle %= 2 * Math.PI;
		if (angle < 0){
			angle += 2 * Math.PI;
		}
		return angle;
	}

	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		//setDefaultCommand(new MySpecialCommand());
	}
}