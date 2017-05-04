#include<xc.h>           // processor SFR definitions
#include<sys/attribs.h>  // __ISR macro
#include<math.h>
#include<stdio.h>
#include "ILI9163C.h"

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
    SPI1_init();
    LCD_init();
    
    __builtin_enable_interrupts();
    LCD_clearScreen(YELLOW);
    char hhog1[20];
    char hhog2[20];
    char hhog3[20];
    char hhog4[20];
    char message[20];
    int count = 0;
    float fps = 0;
    
    while(1) {
        _CP0_SET_COUNT(0);
        sprintf(hhog1, "   ..::::::::.");
        sprintf(hhog2,"  :::::::::::::");
        sprintf(hhog3," /. `:::::::::::");
        sprintf(hhog4,"o__,_||||||||||'");
        draw_string(hhog1,25,20,BLUE,YELLOW);
        draw_string(hhog2,25,30,BLUE,YELLOW);
        draw_string(hhog3,25,40,BLUE,YELLOW);
        draw_string(hhog4,25,50,BLUE,YELLOW);  
        
        sprintf(message, "Hello World! %3d", count);
        draw_string(message,28,90,BLACK,YELLOW);

        //draw_bar
        draw_bar(15,70,100,5,RED,WHITE, count);
        
        sprintf(message, "FPS: %.2f", fps);
        draw_string(message,28,100,BLACK,YELLOW);
        fps = 24000000./_CP0_GET_COUNT();
        
        while(_CP0_GET_COUNT()<48000000/2/5){//Loop at 5Hz
            //USB 2x slower than peripheral
            ;
        }
        count++;
        
        if(count == 101 || count == -101){
            count = 0;
        } 
        
    }// end infinite while
    
}// end main