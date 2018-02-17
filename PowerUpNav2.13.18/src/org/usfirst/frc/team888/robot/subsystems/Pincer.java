package org.usfirst.frc.team888.robot.subsystems;

import org.usfirst.frc.team888.robot.Robot;
import org.usfirst.frc.team888.robot.RobotMap;
import org.usfirst.frc.team888.robot.commands.DefaultMovement;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Pincer extends Subsystem {

	TalonSRX pincerMotor;

	DoubleSolenoid pincerPiston;
	
	int pincerDesiredPosition;
	
	int pincerClicks;
	
	String positioning;
	
	boolean pincerOpen = true;

	
	public Pincer() {
		pincerMotor = new TalonSRX(RobotMap.PINCER_MOTOR);
		pincerMotor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
		pincerClicks = pincerMotor.getSelectedSensorPosition(0);
	}

	/*
	//Sets the position drivers want the pincer at
	public void setDesiredPincerPosition() {
		switch (Robot.oi.getGamepadPOV()) {
		case 0:
			pincerDesiredPosition = RobotMap.RESTING_POSITION;
			break;
		case 90:
			pincerDesiredPosition = RobotMap.HIGH_DROPOFF_POSITION;
			break;
		case 180:
			pincerDesiredPosition = RobotMap.LOW_DROPOFF_POSITION;
			break;
		case 270:
			pincerDesiredPosition = RobotMap.PICKUP_POSITION;
			break;
		default:
			//figure out what this is
			break;
		}
	}
	*/


	public void movePincer() {
		if (Robot.oi.getGamepadAxis(RobotMap.GP_L_Y_AXIS) > 0.1 || Robot.oi.getGamepadAxis(RobotMap.GP_L_Y_AXIS) < -0.1) {
			pincerMotor.set(ControlMode.PercentOutput,  Robot.oi.getGamepadAxis(RobotMap.GP_L_Y_AXIS) * 0.5);
		} else if (Robot.oi.getGamepadButton(RobotMap.A_BUTTON)) {
			pincerDesiredPosition = RobotMap.PICKUP_POSITION;
			setPincerPosition();
		} else if (Robot.oi.getGamepadButton(RobotMap.B_BUTTON)) {
			pincerDesiredPosition = RobotMap.HIGH_DROPOFF_POSITION;
			setPincerPosition();
		} else if (Robot.oi.getGamepadButton(RobotMap.X_BUTTON)) {
			pincerDesiredPosition = RobotMap.LOW_DROPOFF_POSITION;
			setPincerPosition();
		} else if (Robot.oi.getGamepadButton(RobotMap.Y_BUTTON)) {
			pincerDesiredPosition = RobotMap.RESTING_POSITION;
			setPincerPosition();
		}
	}
	
	//Move pincer to set positions through buttons
	public void setPincerPosition() {
		
		if (pincerClicks > (pincerDesiredPosition + 50)) {
			pincerMotor.set(ControlMode.PercentOutput, RobotMap.PINCER_MOTOR_SPEED);
		} else if (pincerClicks < (pincerDesiredPosition - 50)){
			pincerMotor.set(ControlMode.PercentOutput, -RobotMap.PINCER_MOTOR_SPEED);
		} else {
			pincerMotor.set(ControlMode.PercentOutput, 0);
		}
	}

	
	public void testPincer() {
		SmartDashboard.putNumber("Pincer Encoder", pincerClicks);
	}
	
	//Uses pistons to close pincer
	public void pince() {
		if(Robot.oi.getRightStickButton(1)) {
			if(pincerOpen) {
				pincerPiston.set(DoubleSolenoid.Value.kReverse);
			} else {
				pincerPiston.set(DoubleSolenoid.Value.kForward);
			}
		}
	}

	public void initDefaultCommand() {
		pincerMotor.setSelectedSensorPosition(0, 0, 0);
		setDefaultCommand(new DefaultMovement());
	}
}