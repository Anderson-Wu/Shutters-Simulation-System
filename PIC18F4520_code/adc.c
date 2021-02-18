#include <xc.h>

void ADC_Initialize(void) {
    TRISA = 0xff;		// Set as input port
    ADCON1 = 0x0D;  	// Ref vtg is VDD & Configure pin as analog pin AN0 AN! be input
    // ADCON2 = 0x92;  	
    ADCON2bits.ADFM = 1 ;          // Right Justifie 0~1024
    ADCON2bits.ACQT = 0b010; //acqu. time 010 = 4 TAD(4 * 0.7us) greater than 2.4us
    ADCON2bits.ADCS = 0b000; //clock 000
    ADRESH=0;  			// Flush ADC output Register
    ADRESL=0;  
}

int ADC_Read(int channel)
{
    int digital;
    
    ADCON0bits.CHS =  channel; // Select Channe
    ADCON0bits.GO = 1;
    ADCON0bits.ADON = 1;  //open adc
    
    while(ADCON0bits.GO_nDONE==1);

    digital = (ADRESH*256) | (ADRESL);
    //ADCON0bits.ADON = 0;  //close adc
    return(digital);
}