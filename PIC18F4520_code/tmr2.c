#include <xc.h>

void TMR2_Initialize()
{
    T2CON = 126;//1111110 prescaler 1 postscaler 16
    TMR2 = 0;
    PR2 = 155;//   20ms*500khz/4/16 - 1 = 155.25
}

