### ME 433: Advanced Mechatronics Course Projects
March 2017 - June 2017

## About
This repository contains scripts for various mechatronics projects completed for the ME 433 course at Northwestern.

## Contents
#### HW1:<br>
* pic32mx250f128b microcontroller breadboard circuit diagram and picture<br>
* Circuit validation code which toggles an LED on and off

#### HW2:<br>
* Custom pic32mx250f128b Eagle library
* pic32mx250f128b PCB schematic

#### HW3:<br>
![Board](https://github.com/stephanniec/stephanniec_ME433_2017/blob/master/imgs/pcb.png)
* PCB .brd Eagle file
* Compressed PCB Gerber files

#### HW4:<br>
* SPI communication with an MCP4902 DAC chip
* Code tells the DAC to output a 10Hz sine wave and 5Hz triangle wave

#### HW5:<br>
* I2C communication with an MCP23008 chip
* Code tells the chip to turn off a yellow LED when a button hooked up to GP7 is pressed

#### HW6:<br>
![LCD_Bar](https://github.com/stephanniec/stephanniec_ME433_2017/blob/master/imgs/lcd.png)
* TFT LCD display using SPI communication
* Code prints the image of a hedgehog and "Hello World!" to the screen, draws a progress bar which fills as a counter increases from 0-100, and outputs a live reading of the frames per second rate

#### HW7:<br>
![IMU](https://github.com/stephanniec/stephanniec_ME433_2017/blob/master/imgs/imu.png)
* LCD progress bar display of accelerometer xy readings from an LSM6DS33 chip

#### HW8:<br>
* Replica of HW7 using Harmony

#### HW9:<br>
* Accelerometer data display using PIC-USB communication

#### HW10:<br>
* Digital signal processing of IMU readouts
* Compared the performance of 3 different low pass filters: MAF, IIR, and FIR

#### HW11:<br>
* IMU mouse prototype
* Controls the trajectory of a cursor via PIC-USB communication

#### HW12:<br>
![Slider](https://github.com/stephanniec/stephanniec_ME433_2017/blob/master/imgs/android.png)
* Basic slider bar Android application written in JAVA

#### HW13:<br>
* Color identification Android application written in JAVA

#### HW14:<br>
![PICTalk](https://github.com/stephanniec/stephanniec_ME433_2017/blob/master/imgs/android_pic.png)
* Android phone and PIC microcontroller communication
* Phone sends the position of a slider bar to the chip
* Chip continuously streams increasing integer values
* When an input from the phone is received, the PIC will return the slider position as it outputs numbers

#### HW15:<br>
![Wheel1](https://github.com/stephanniec/stephanniec_ME433_2017/blob/master/imgs/wheel1_small.png)
![Wheel2](https://github.com/stephanniec/stephanniec_ME433_2017/blob/master/imgs/wheel2_small.png)
![BoxWalls](https://github.com/stephanniec/stephanniec_ME433_2017/blob/master/imgs/box_walls_small.png)
![Assemble](https://github.com/stephanniec/stephanniec_ME433_2017/blob/master/imgs/box_assembly_small.png)
* CAD designs of a wheel and a box with a living hinge built in OnShape

#### HW16:<br>
![Slider](https://github.com/stephanniec/stephanniec_ME433_2017/blob/master/imgs/3dwheel_small.png)
* Assembled 3D printed wheel prototypes
