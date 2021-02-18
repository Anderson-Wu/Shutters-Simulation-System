#include <xc.h>
 
void PIN_MANAGER_Initialize(){
    TRISBbits.TRISB0 = 1;
    TRISDbits.TRISD0  = 0;
    TRISDbits.TRISD1  = 0;
    TRISDbits.TRISD2  = 0;
}
void  PIN_MANAGER_Sethandlight(){
    PORTDbits.RD0 = 1;
    PORTDbits.RD1 = 0;
    PORTDbits.RD2 = 0;
}
void  PIN_MANAGER_Setbtlight(){
    PORTDbits.RD0 = 0;
    PORTDbits.RD1 = 1;
    PORTDbits.RD2 = 0;
}
void  PIN_MANAGER_Setlightlight(){
    PORTDbits.RD0 = 0;
    PORTDbits.RD1 = 0;
    PORTDbits.RD2 = 1;
}