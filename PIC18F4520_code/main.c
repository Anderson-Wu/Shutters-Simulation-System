#include "setting.h"
#include <stdlib.h>
#include "stdio.h"
#include "string.h"
#include <xc.h>
char str[20];
volatile int channel;

void main(void) {
    SYSTEM_Initialize() ;
    int value;
    int segment;
    char out[2];
    channel = 0;
    while(1) {
        if(GetLen()){
            strcpy(str,GetString());
            if(str[0]=='s'){
                switch(channel){
                    case 0:
                        UART_Write_Text("H");
                        break;
                    case 1:
                        UART_Write_Text("L");
                        break;            
                    case -1:
                        UART_Write_Text("B");
                        break;                          
                }          
            }
            else if(str[0] == 'v'){
                snprintf(out, 20,"%d",segment);
                UART_Write_Text(out);
            }
            else if(str[0]=='h'){//mode1
                channel = 0;
                value = ADC_Read(channel);
                segment = CCP1_Setdutybyanalog(value);
            }
            else if(str[0]=='b'){//mode1
                channel = -1;
            }
            else if(str[0]=='l'){//mode1
                channel = 1;
                value = ADC_Read(channel);
                segment = CCP1_Setdutybyanalog(value);
            }
            if(channel != -1){
                snprintf(out, 20,"%d",segment);
                UART_Write_Text(out);
            }
            ClearBuffer();
        }
        if(channel == 0){
            PIN_MANAGER_Sethandlight();
            value = ADC_Read(channel);
            segment = CCP1_Setdutybyanalog(value);
        }

        if(channel == 1){
            PIN_MANAGER_Setlightlight();
            value = ADC_Read(channel);
            segment = CCP1_Setdutybyanalog(value);
        }
        
        if(channel == -1){
            PIN_MANAGER_Setbtlight();
            while(1){
                if(channel!=-1)
                    break;
                if(GetLen()){
                    strcpy(str,GetString());
                    if(str[0] >=48 && str[0] < 54){
                        CCP1_Setdutybyuart(str[0]-48);
                        segment = str[0]-48;
                        ClearBuffer();
                       // UART_Write_Text(str[0]);
                    }
                    else{
                        break;
                    }
                }
            }
        }
    }
    return;
}




void __interrupt() isr(void)
{
     INTCONbits.INT0F = 0;
     if(channel == 0 || channel == -1){
         channel = 1;
     }
     else{
         channel = 0;
     }
     switch(channel){
        case 0:
            UART_Write_Text("H");
            break;
        case 1:
            UART_Write_Text("L");
            break;            
        case -1:
            UART_Write_Text("B");
            break;                          
    }
}