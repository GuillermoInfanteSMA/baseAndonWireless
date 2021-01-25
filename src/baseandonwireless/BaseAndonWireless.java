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
    
    public BaseAndonWireless(){
        this.Connect();
        out = serial.getOutputStream();
        this.loop();
    }
    
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
    
    public void loop(){
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    int auxContador = 0;
                    int i = 0;
                    byte[] message;
                    while(auxContador != 10){
                        //out.write(MESSAGE_BODY[i]); //first attempt
                        message = Arrays.copyOf(MESSAGE_HEADER, HEADER_LENGTH + BODY_LENGTH);
                        System.arraycopy(MESSAGE_BODY[i], 0, message, HEADER_LENGTH, BODY_LENGTH);
                        message = Arrays.copyOf(message, MESSAGE_LENGTH);
                        System.arraycopy(MESSAGE_FOOTER, 0, message, HEADER_LENGTH + BODY_LENGTH, FOOTER_LENGTH);
                        out.write(message);
                        System.out.println("Cambiando de Color...");
                        Thread.sleep(5000);
                        i++;
                        auxContador++;
                        if(i == MESSAGE_BODY.length){
                            i=0;
                        }
                    }
                    message = Arrays.copyOf(MESSAGE_HEADER, HEADER_LENGTH + BODY_LENGTH);
                    System.arraycopy(ALL_TURNOFF, 0, message, HEADER_LENGTH, BODY_LENGTH);
                    message = Arrays.copyOf(message, MESSAGE_LENGTH);
                    System.arraycopy(MESSAGE_FOOTER, 0, message, HEADER_LENGTH + BODY_LENGTH, FOOTER_LENGTH);
                    System.out.println("Apangando Torreta...");
                    out.write(message);
                    Thread.sleep(5000);
                    System.out.println("HILO: Terminó mi proceso.");
                }catch( IOException ioe ) {System.err.println("Error al enviar mensaje: " + ioe);
                }catch( InterruptedException ie ){System.err.println("Error dentro de Thread: " + ie);}
            }
        };
        thread.start();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        new BaseAndonWireless();
    }
    
}
