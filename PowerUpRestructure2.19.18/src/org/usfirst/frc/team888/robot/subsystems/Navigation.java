package org.usfirst.frc.team888.robot.subsystems;

import org.usfirst.frc.team888.robot.OI;
import org.usfirst.frc.team888.robot.RobotMap;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Navigation extends Subsystem {

	protected DriveTrain drive;
	protected DeadReckon location;
	protected OI oi;

	protected double maxOutput = 1.0;
	protected double leftSideAdjustment;
	protected double rightSideAdjustment;
	protected double desiredHeading;
	protected double leftBaseDriveOutput = 0.0;
	protected double rightBaseDriveOutput = 0.0;	
	protected double leftDriveOutput = 0.0;
	protected double rightDriveOutput = 0.0;

	protected double[] desiredLocation;

	protected boolean manualControl = true;

	protected int schedulerOffset = 0;

	protected boolean input = false;
	protected boolean lastInput = false;
	protected boolean output = false;
	protected boolean press = false;

	/*
 	protected boolean previousCameraButtonState = false;
 	protected byte[] ip = {10, 8, 88, 12};
 	protected InetAddress cameraAddress;

 	protected DatagramSocket sock;
 	protected DatagramPacket message;
	 */

	public Navigation(DriveTrain p_drive, DeadReckon p_location, OI p_oi) {
		drive = p_drive;
		location = p_location;
		oi = p_oi;

		/* try {
 			sock = new DatagramSocket(7777);
 			message = new DatagramPacket(byteCameraMessage, camera.length(), cameraAddress, 8888);
 			cameraAddress = InetAddress.getByAddress(ip);
 		} catch (Exception e) {

 		} */
	}

	public void navigationInit() {
		schedulerOffset = 0;

		drive.resetEncoderPositions();

		//send first message to pi to start camera feed
		/* try {
		 			sock.send(message);
		 		} catch (IOException e) {
		 			e.printStackTrace();
		 		} */
	}
	//send first message to pi to start camera feed
	public void navigationExecute() {
		location.updateTracker();
		updateGuidenceControl();
		updateMotion();

		if (schedulerOffset == 0) {
			//updateCamera();
		}

		schedulerOffset = (schedulerOffset + 1) % 50;
	}

	public void updateGuidenceControl() {
		desiredLocation = RobotMap.DESIRED_LOCATION;
	}

	/* 	public void updateCamera() {
 		if(oi.getRightStickButton(2) && !previousCameraButtonState) {
 			if(camera.equals("cameraFront")) {
 				camera = "backCamera";
 			} else {
 				camera = "frontCamera";
 			}

 			try {
 				sock.send(message);
 			} catch (IOException e) {
 				e.printStackTrace();
 			}
 			previousCameraButtonState = true;
 		} else if (!oi.getRightStickButton(2)) {
 			previousCameraButtonState = false;
 		}
 	} */

	/**
	 * Gets the encoder values and finds what adjustments need to be done
	 * @return An array containing the adjustments for the left and right sides in that order
	 */

	public void updateMotion() {
		if (!manualControl) {
			double[] adjustments = getAdjustments();
			drive.move(RobotMap.LEFT_AUTO_SPEED + adjustments[0], RobotMap.RIGHT_AUTO_SPEED + adjustments[1]);
		} else {
			if(oi.getTriggers()) {
				leftBaseDriveOutput = oi.getLeftStickAxis(RobotMap.L_Y_AXIS);
				rightBaseDriveOutput = oi.getRightStickAxis(RobotMap.R_Y_AXIS);
			} else {
				leftBaseDriveOutput = 0.7 * oi.getLeftStickAxis(RobotMap.L_Y_AXIS);
				rightBaseDriveOutput = 0.7 * oi.getRightStickAxis(RobotMap.R_Y_AXIS);
			}

			if(Math.abs(oi.getLeftStickAxis(RobotMap.L_Y_AXIS)) < 0.3 &&
					Math.abs(oi.getRightStickAxis(RobotMap.R_Y_AXIS)) < 0.3){
				leftBaseDriveOutput = 0.0;
				rightBaseDriveOutput = 0.0;
			}

			if (input == true && lastInput == false) {
				press = true;
			} else {
				press = false;
			}

			if (press) {
				output = !output;
			}

			lastInput = input;
			input = oi.getLeftStickButton(2) || oi.getRightStickButton(2);

			if(output) {
				leftDriveOutput = leftBaseDriveOutput;
				rightDriveOutput = rightBaseDriveOutput;
			} else {
				leftDriveOutput = -rightBaseDriveOutput;
				rightDriveOutput = -leftBaseDriveOutput;
			}

			SmartDashboard.putNumber("leftOutput", leftDriveOutput);
			SmartDashboard.putNumber("rightOutput", rightDriveOutput); 

			drive.move(leftDriveOutput, rightDriveOutput);
		}
	}

	public double[] getAdjustments() {	
		double[] navData = location.getNavLocationData();
		desiredHeading = 0; //calculateDesiredHeading();

		/**
		 * If the robot is moving in a positive direction...
		 */

		if ((navData[0] > 0) && (navData[1] > 0)) {

			/**
			 * If the left side is moving slower than right...
			 */

			if (DeadReckon.absAngle(navData[2] - desiredHeading) <
					DeadReckon.absAngle(desiredHeading - navData[2])) {

				/**
				 * If the speed plus the adjustment for the left side would be slower
				 * than the max speed add the adjustments to the left side.
				 * Otherwise, subtract the adjustments from the right side.
				 */

				if 	((RobotMap.LEFT_AUTO_SPEED + RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT)
						<= maxOutput) {			
					leftSideAdjustment = RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT;
					rightSideAdjustment = 0.0;
				} else {
					rightSideAdjustment = -RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT;
					leftSideAdjustment = 0.0;
				}

				/**
				 * If the right side is moving slower than left...
				 */		

			} else if (DeadReckon.absAngle(navData[2] - desiredHeading) >
			DeadReckon.absAngle(desiredHeading - navData[2])) {

				/**
				 * If the speed plus the adjustment for the right side would be slower
				 * than the max speed add the adjustments to the right side.
				 * Otherwise, subtract the adjustments from the left side.
				 */

				if 	((RobotMap.RIGHT_AUTO_SPEED + RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT) <= maxOutput) {			
					rightSideAdjustment = RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT;
					leftSideAdjustment = 0.0;
				} else {
					leftSideAdjustment = -RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT;
					rightSideAdjustment = 0.0;
				}

				/**
				 * If the robot is already moving straight add no adjustments
				 */

			} else {
				leftSideAdjustment = 0.0;
				rightSideAdjustment = 0.0;
			}	

			/**
			 * If the robot is moving in a negative direction...
			 */

		} else if((navData[0] < 0) && (navData[1] < 0)) {

			/**
			 * If the left side is moving slower than right...
			 */

			if (DeadReckon.absAngle(navData[2] - desiredHeading) >
			DeadReckon.absAngle(desiredHeading - navData[2])) {

				/**
				 * If the speed plus the adjustment for the left side would be slower
				 * than the max speed add the adjustments to the left side.
				 * Otherwise, subtract the adjustments from the right side.
				 */

				if ((RobotMap.LEFT_AUTO_SPEED - RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT) >= -maxOutput) {
					leftSideAdjustment = -RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT;
					rightSideAdjustment = 0.0;
				} else {
					rightSideAdjustment = RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT;
					leftSideAdjustment = 0.0;
				}

				/**
 			/* If the right side is moving slower than left...
				 */		

			} else if (DeadReckon.absAngle(navData[2] - desiredHeading) <
					DeadReckon.absAngle(desiredHeading - navData[2])) {

				/**
				 * If the speed plus the adjustment for the right side would be slower
				 * than the max speed add the adjustments to the right side.
				 * Otherwise, subtract the adjustments from the left side.
				 */

				if ((RobotMap.RIGHT_AUTO_SPEED - RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT) >= -maxOutput) {
					rightSideAdjustment = -RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT;
					leftSideAdjustment = 0.0;
				} else {
					leftSideAdjustment = RobotMap.DRIVE_STRAIGHT_ADJUSTMENT_AMOUNT;
					rightSideAdjustment = 0.0;
				}

				/**
 			/* If the robot is already moving straight add no adjustments
				 */	

			} else {
				leftSideAdjustment = 0.0;
				rightSideAdjustment = 0.0;
			}	

			/**
			 * If the robot is not moving or turning, add no adjustments.
			 */

		} else {
			leftSideAdjustment = 0.0;
			rightSideAdjustment = 0.0;
		}

		SmartDashboard.putNumber("Left Adjustments", leftSideAdjustment);
		SmartDashboard.putNumber("Right Adjustments", rightSideAdjustment);

		double[] adjustments = {
				leftSideAdjustment,
				rightSideAdjustment
		};

		return adjustments;		
	}

	public double calculateDesiredHeading() {
		double[] pos = location.getPos();
		double[] posToDesired = {0,0};

		for (int i = 0; i < pos.length; i++) {
			posToDesired[i] = pos[i] - desiredLocation[i];
		}

		desiredHeading = DeadReckon.absAngle(Math.atan2(posToDesired[0], posToDesired[1]));
		return desiredHeading;
	}

	/**
	 * @return The array with zeros for both adjustments
	 */

	public double[] reset() {
		double[] j = {0,0};
		return j;
	}

	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		//setDefaultCommand(new MySpecialCommand());
	}
}