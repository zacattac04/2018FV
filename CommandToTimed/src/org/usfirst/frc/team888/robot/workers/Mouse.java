package org.usfirst.frc.team888.robot.workers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Mouse extends Thread {

	FileInputStream mouse;
	
	byte[] dat = new byte[3];
	
	int deltaX = 0;
	int deltaY = 0;
	int x = 0;
	int y = 0;
	int h = 0;
	
	public Mouse() {
		try {
			mouse = new FileInputStream("/dev/input/mouse1");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				mouse.read(dat);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			boolean yOverflow = (dat[0] & 0x80) != 0;
			boolean xOverflow = (dat[0] & 0x40) != 0;
			boolean ySignBit = (dat[0] & 0x20) != 0;
			boolean xSignBit = (dat[0] & 0x10) != 0;

			int xMovement = dat[1];
			int yMovement = dat[2];
			
			if (xSignBit) deltaX = xMovement - 255;
	        else deltaX = xMovement;
	        
			if (ySignBit) deltaY = yMovement - 255;
	        else deltaY = yMovement;

	        x += deltaX;
	        y += deltaY;

	        h = (int) Math.atan2(deltaX, deltaY);

	        if (h < 0.0) h += (Math.PI * 2.0);

	        h %= (Math.PI * 2.0);
	        
	        if (!(xOverflow && yOverflow)) {
	        	System.out.println("x: " + x + " y: " + " h: " + h);
	        }
	        else if (xOverflow && !yOverflow) {
	        	System.out.println("X OVERFLOW!");
	        }
	        else if (!xOverflow && yOverflow) {
	        	System.out.println("Y OVERFLOW!");
	        }
	        else {
	        	System.out.println("X AND Y OVERFLOW!!!");
	        }
		}
	}
}