#include <xc.h>

void INT0_Initialize (void)
{
    INTCON2bits.INTEDG0 = 1;
    INTCONbits.INT0E = 1;
    INTCONbits.INT0F = 0;
}

