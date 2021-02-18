#include <xc.h>

void INT1_Initialize (void)
{
    INTCON2bits.INTEDG1= 1;
    INTCON3bits.INT1IP = 1;
    INTCON3bits.INT1E = 1;
    INTCON3bits.INT1F = 0;
}

