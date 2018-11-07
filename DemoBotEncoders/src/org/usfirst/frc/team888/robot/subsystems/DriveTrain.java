package org.usfirst.frc.team888.robot.subsystems;

import org.usfirst.frc.team888.robot.RobotMap;
import org.usfirst.frc.team888.robot.commands.DefaultMovement;

import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *	Reworking of Charlie's DriveTrain system. Encoders moved to their own subsystem.
 */

public class DriveTrain extends Subsystem {

    Victor rearLeft,
    		 frontLeft,
    		 rearRight,
    		 frontRight;
    
    Joystick leftStick,
    			rightStick;
    
    Encoder leftEncoder,
    			rightEncoder;

	public DriveTrain() {
    	rearLeft = new Victor(RobotMap.MOTOR_REAR_LEFT);
    	frontLeft = new Victor(RobotMap.MOTOR_FRONT_LEFT);
    	
    	rearRight = new Victor(RobotMap.MOTOR_REAR_RIGHT);
    	frontRight = new Victor(RobotMap.MOTOR_FRONT_RIGHT);
    	
    	leftStick = new Joystick(RobotMap.LEFT_JOYSTICK);
    	rightStick = new Joystick(RobotMap.RIGHT_JOYSTICK);
    	
    	//Configure encoders.
    	leftEncoder = new Encoder(2, 3, true, CounterBase.EncodingType.k4X);
    	rightEncoder = new Encoder(0, 1, false, CounterBase.EncodingType.k4X);
    }
    
    public void initDefaultCommand() {
        setDefaultCommand(new DefaultMovement());
    }
    
    /**
     * Sets the motor speeds.
     * @param leftSpeed A value between -1.0 and 1.0
     * @param rightSpeed A value between -1.0 and 1.0
     */
    public void move(double leftSpeed, double rightSpeed) {
    	rearLeft.set(leftStick.getRawAxis(1) * -1.0);
    	frontLeft.set(leftStick.getRawAxis(1) * -1.0);
    	
    	
    	rearRight.set(rightStick.getRawAxis(1) * 1.0);
    	frontRight.set(rightStick.getRawAxis(1) * 1.0);
    	
    }
	
    //-------------------------------------------------------------------------
    // This code is simply to lay out concepts of programming with these wheels
    // It is in no way functional and should not be considered as such
    //
    // currently, it is setup to have full omnidirectional movement using one
    // stick
    // the other stick could then be used for rotation
    // I have no clue if or how the two sticks will work in tandem
    // That is not my problem
    //
    // As a side note, the rotation for the moment is only from the center of
    // the robot. As far as I can tell, mecanums allow for rotation around any
    // point. I don't see the point in this, so I didn't program it, but let
    // it be known that it could be done
    // -signed, General Idiot
    //-------------------------------------------------------------------------
    public void moveMecanum() {
    	
    	// finds angle of joystick
    	double angle = Math.toDegrees(Math.atan(leftStick.getRawAxis(1) / leftStick.getRawAxis(0)));
    	// allows for all angles
    	if (leftStick.getRawAxis(0) < 0.0) {
    		angle = 180 - angle;
    	}
    	// finds the magnitude of the joystick
    	double magnitude = Math.sqrt(Math.pow(leftStick.getRawAxis(0), 2) + Math.pow(leftStick.getRawAxis(0), 1));
    	
    	// Directional movement
    	frontLeft.set(Math.cos(angle - 45.0) * -1.0 * magnitude);
    	rearLeft.set(Math.sin(angle - 45.0) * -1.0 * magnitude);
    	
    	frontRight.set(Math.sin(angle - 45.0) * 1.0 * magnitude);
    	rearRight.set(Math.cos(angle - 45.0) * 1.0 * magnitude);
    	
    	// Rotational movement
    	if (rightStick.getRawAxis(1) != 0.0) {
    	frontLeft.set(rightStick.getRawAxis(1) * -1.0);
    	rearLeft.set(rightStick.getRawAxis(1) * -1.0);
    	
    	frontRight.set(rightStick.getRawAxis(1) * -1.0);
    	rearRight.set(rightStick.getRawAxis(1) * -1.0);
    	
    	}
    }
    
    /**
     *  Polls the TalonSRX's for the Encoder values.
     * @return Encoder values in {left, right} format.
     */
    public int[] getEncoderVals() {
    	
    	double leftCounts = leftEncoder.get();
		double rightCounts = rightEncoder.get();
    	SmartDashboard.putNumber("Left Encoder", leftCounts);
		SmartDashboard.putNumber("Right Encoder", rightCounts);
    	
    	int[] i = {
    			(int) leftCounts,
    			(int) rightCounts };
    	
    	return i;
    }
}

