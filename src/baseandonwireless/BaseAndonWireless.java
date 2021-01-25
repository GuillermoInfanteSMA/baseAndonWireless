/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package baseandonwireless;

import com.pi4j.io.serial.Baud;
import com.pi4j.io.serial.DataBits;
import com.pi4j.io.serial.FlowControl;
import com.pi4j.io.serial.Parity;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialConfig;
import com.pi4j.io.serial.SerialFactory;
import com.pi4j.io.serial.StopBits;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 *
 * @author Guillermo Infante
 */
public class BaseAndonWireless {
    
    private Serial serial;
    private OutputStream out;
    private static final byte[] MESSAGE_HEADER = {0x41, 0x42, 0x44, 0x00, 0x00, 0x07};
    private static final byte[] MESSAGE_FOOTER = {0x00, 0x00};
    public static final byte [][] MESSAGE_BODY = {{0x07, 0x07, 0x07, 0x07, 0x07}, {0x01, 0x01, 0x01, 0x01, 0x01}, {0x04, 0x04, 0x04, 0x04, 0x04}, {0x07, 0x01, 0x04, 0x01, 0x07}, {0x04, 0x01, 0x07, 0x01, 0x04}};
    private static final byte[] ALL_TURNOFF = {0x00, 0x00, 0x00, 0x00, 0x00};
    private static final int HEADER_LENGTH = 6;
    private static final int BODY_LENGTH = 5;
    private static final int FOOTER_LENGTH = 2;
    private static final int MESSAGE_LENGTH = 13;
    
    public void Connect(){
        try{
            System.out.println("Conectando...");
            this.serial = SerialFactory.createInstance();
            SerialConfig serialConfig = new SerialConfig();
            serialConfig.device("/dev/ttyAMA0")
                    .baud(Baud._9600)
                    .dataBits(DataBits._8)
                    .parity(Parity.NONE)
                    .stopBits(StopBits._1)
                    .flowControl(FlowControl.NONE);
            serial.open(serialConfig);
            Thread.sleep(2000);
            System.out.println("Conectado con éxito a /dev/ttyAMA0");
        }catch(IOException ioe){ System.err.println("Error al conectase a /dev/ttyAMA0");
        }catch(InterruptedException ie){ System.err.println("Error en Hilo (Sleep) dentro de conexión"); }
    } 

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        BaseAndonWireless baw = new BaseAndonWireless();
        baw.Connect();
    }
    
}
