#include<xc.h>           // processor SFR definitions
#include<sys/attribs.h>  // __ISR macro
#include<math.h>
#include<stdio.h>
#include"ILI9163C.h"            //spi communication with LCD
#include"i2c_master_imu.h"    //i2c communication with IMU

// DEVCFG0
#pragma config DEBUG = OFF // no debugging
#pragma config JTAGEN = OFF // no jtag
#pragma config ICESEL = ICS_PGx1 // use PGED1 and PGEC1
#pragma config PWP = OFF // no write protect
#pragma config BWP = OFF // no boot write protect
#pragma config CP = OFF // no code protect

// DEVCFG1
#pragma config FNOSC = PRIPLL // use primary oscillator with pll
#pragma config FSOSCEN = OFF // turn off secondary oscillator
#pragma config IESO = OFF // no switching clocks
#pragma config POSCMOD = HS // high speed crystal mode
#pragma config OSCIOFNC = OFF // free up secondary osc pins
#pragma config FPBDIV = DIV_1 // divide CPU freq by 1 for peripheral bus clock
#pragma config FCKSM = CSDCMD // do not enable clock switch
#pragma config WDTPS = PS1 // slowest wdt
#pragma config WINDIS = OFF // no wdt window
#pragma config FWDTEN = OFF // wdt off by default
#pragma config FWDTWINSZ = WINSZ_25 // wdt window at 25%

// DEVCFG2 - get the CPU clock to 48MHz
#pragma config FPLLIDIV = DIV_2 // divide input clock to be in range 4-5MHz
#pragma config FPLLMUL = MUL_24 // multiply clock after FPLLIDIV
#pragma config FPLLODIV = DIV_2 // divide clock after FPLLMUL to get 48MHz
#pragma config UPLLIDIV = DIV_2 // divider for the 8MHz input clock, then multiply by 12 to get 48MHz for USB
#pragma config UPLLEN = ON // USB clock on

// DEVCFG3
#pragma config USERID = 0 // some 16bit userid, doesn't matter what
#pragma config PMDL1WAY = OFF // allow multiple reconfigurations
#pragma config IOL1WAY = OFF // allow multiple reconfigurations
#pragma config FUSBIDIO = ON // USB pins controlled by USB module
#pragma config FVBUSONIO = ON // USB BUSON controlled by USB module

int main() {

    __builtin_disable_interrupts();

    // set the CP0 CONFIG register to indicate that kseg0 is cacheable (0x3)
    __builtin_mtc0(_CP0_CONFIG, _CP0_CONFIG_SELECT, 0xa4210583);

    // 0 data RAM access wait states
    BMXCONbits.BMXWSDRM = 0x0;

    // enable multi vector interrupts
    INTCONbits.MVEC = 0x1;

    // disable JTAG to get pins back
    DDPCONbits.JTAGEN = 0;
    
    // initializations
    SPI1_init(); // Talk to LCD
    LCD_init();
    i2c_master_setup(); // Talk to IMU
    init_expander(); // Turn on accelerometer
    
    __builtin_enable_interrupts();
    char msg[100];
    int arrlen = 14;
    unsigned char data[arrlen];
    LCD_clearScreen(BLACK);
  
    sprintf(msg, "WHOAMI output: %d", get_expander(WHO_AM_I)); //0b01101001
    draw_string(msg, 20, 20, RED, BLACK); //register returns 105
    
    while(1) {
        i2c_read_multiple(SLAVE_ADDR, OUT_TEMP_L, data, arrlen);
        
        // parse read values
        signed short temp = (data[1] << 8) | data[0]; //16-bit short
        signed short gyroX = (data[3] << 8) | data[2];
        signed short gyroY = (data[5] << 8) | data[4];
        signed short gyroZ = (data[7] << 8) | data[6];
        signed short accelX = (data[9] << 8) | data[8];
        signed short accelY = (data[11] << 8) | data[10];
        signed short accelZ = (data[13] << 8) | data[12];
        
        //scaling length and height
        float xscale = accelX*0.000061*100; 
        float yscale = accelY*0.000061*100;
        
        sprintf(msg, "x = %f", xscale);
        draw_string(msg, 20, 100, RED, BLACK);
        sprintf(msg, "y = %f", yscale);
        draw_string(msg, 20, 110, RED, BLACK);
        
        draw_bar(60, 50, 50, 5, BLUE, WHITE, xscale, yscale); //xbar
        
        //5Hz loop
        _CP0_SET_COUNT(0);
        while (_CP0_GET_COUNT() < 48000000/2/5){
            ;
        }
        
    }// end infinite while
    
}// end main