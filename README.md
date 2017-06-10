### ME 433: Advanced Mechatronics Course Projects
March 2017 - June 2017

## About
This repository contains scripts for various mechatronics projects completed for the ME 433 course at Northwestern.

## Contents
#### HW1: Breadboard Prototyping<br>
* pic32mx250f128b microcontroller breadboard circuit diagram and picture<br>
* Circuit validation code which toggles an LED on and off

#### HW2: PCB Design Part I<br>
* Custom pic32mx250f128b Eagle library
* pic32mx250f128b PCB schematic

#### HW3: PCB Design Part II<br>
![Board](https://github.com/stephanniec/stephanniec_ME433_2017/blob/master/imgs/pcb.png)
* PCB .brd Eagle file
* Compressed PCB Gerber files

#### HW4: SPI Device Communication<br>
* SPI communication with an MCP4902 DAC chip
* Code tells the DAC to output a 10Hz sine wave and 5Hz triangle wave

#### HW5: I2C Device Communication<br>
* I2C communication with an MCP23008 chip
* Code tells the chip to turn off a yellow LED when a button hooked up to GP7 is pressed

#### HW6: LCD Display<br>
![LCD_Bar](https://github.com/stephanniec/stephanniec_ME433_2017/blob/master/imgs/lcd.png)
* TFT LCD display using SPI communication
* Code prints the image of a hedgehog and "Hello World!" to the screen, draws a progress bar which fills as a counter increases from 0-100, and outputs a live reading of the frames per second rate

#### HW7: Accelerometer Display<br>
![IMU](https://github.com/stephanniec/stephanniec_ME433_2017/blob/master/imgs/imu.png)
* LCD progress bar display of accelerometer xy readings from an LSM6DS33 chip

#### HW8: Working with Harmony<br>
* Replica of HW7 using Harmony

#### HW9: USB Device Communication<br>
* Accelerometer data display using PIC-USB communication

#### HW10: Digital Signal Processing<br>
* Digital signal processing of IMU readouts
* Compared the performance of 3 different low pass filters: MAF, IIR, and FIR

#### HW11: USB Mouse Prototype<br>
* IMU mouse prototype
* Controls the trajectory of a cursor via PIC-USB communication

#### HW12: Android Programing<br>
![Slider](https://github.com/stephanniec/stephanniec_ME433_2017/blob/master/imgs/android.png)
* Basic slider bar Android application written in JAVA

#### HW13: Android Color Recognition<br>
* Color identification Android application written in JAVA

#### HW14: USB Communication with an Android Phone<br>
![PICTalk](https://github.com/stephanniec/stephanniec_ME433_2017/blob/master/imgs/android_pic.png)
* Android phone and PIC microcontroller communication
* Phone sends the position of a slider bar to the chip
* Chip continuously streams increasing integer values
* When an input from the phone is received, the PIC will return the slider position as it outputs numbers

#### HW15: CAD<br>
![Wheel1](https://github.com/stephanniec/stephanniec_ME433_2017/blob/master/imgs/wheel1_small.png)
![Wheel2](https://github.com/stephanniec/stephanniec_ME433_2017/blob/master/imgs/wheel2_small.png)
![BoxWalls](https://github.com/stephanniec/stephanniec_ME433_2017/blob/master/imgs/box_walls_small.png)
![Assemble](https://github.com/stephanniec/stephanniec_ME433_2017/blob/master/imgs/box_assembly_small.png)
* CAD designs of a wheel and a box with a living hinge built in OnShape

#### HW16: 3D Printing<br>
![wheels](https://github.com/stephanniec/stephanniec_ME433_2017/blob/master/imgs/3dwheel_small.png)
* Assembled 3D printed wheel prototypes

#### HW17: Laser Cutting<br>
![box](https://github.com/stephanniec/stephanniec_ME433_2017/blob/master/imgs/living_hinge_box_small.png)
* Assembled living hinge box

#### HW18: Motor Control Application<br>
* Created a JAVA android application which can individually set the speed and direction of two DC motors
* PWM output is determined by the position of two slider bars
* Wheel direction is determined by the position of two switches

#### HW19: Path Center Identification<br>
* Android application uses the rear camera scan the environment
* The resulting Bitmap read is analyzed for areas where green meets red
* A horizontal line is drawn where the android believes the path starts and ends
* The center of mass of the line is denoted by a red dot

#### HW20: Line-following Robot
![robot](https://github.com/stephanniec/stephanniec_ME433_2017/blob/master/imgs/line_follow_robot.png)
* Consolidated motor control and path detection code
* Constructed a small vehicle to house the motors and circuitry
* Implemented proportional control to moderate the speed of the wheels
* Filtered the center of mass positions so only the average points of the Bitmap's last 120 rows are analyzed
* Applied a threshold pixel range which halts one wheel of the robot if the COM point is lost

Note: Servo motor code was commented out to let the car run at max speed without needing to divert power elsewhere
