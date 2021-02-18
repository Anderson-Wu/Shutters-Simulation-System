#include <xc.h>

void CCP1_Initialize() {    
    TRISCbits.RC2 = 0;
    CCP1CON = 12;//00001100 set to PWM mode
}

int CCP1_Setdutybyanalog(int degree){
    int segment = degree/170;
    if (segment == 6)
        segment = 5;
    int duty = 75 - 6*segment; 
    CCPR1L =duty/4;
    CCP1CONbits.DC1B1 = (duty% 4)/2;
    CCP1CONbits.DC1B0 = duty%2;
    return segment;
}
void CCP1_Setdutybyuart(int segment){
    int duty = 75 - 6*segment; 
    CCPR1L =duty/4;
    CCP1CONbits.DC1B1 = (duty% 4)/2;
    CCP1CONbits.DC1B0 = duty%2;
}


