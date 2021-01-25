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
import java.util.Scanner;

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
    //Bytes de colores que soporta la torreta
    private static final byte turnOff = 0x00;
    private static final byte RED = 0x01;
    private static final byte AMBER = 0x02;
    private static final byte LEMON = 0x03;
    private static final byte GREEN = 0x04;
    private static final byte SKY_BLUE = 0x05;
    private static final byte BLUE = 0x06;
    private static final byte PURPLE = 0x07;
    private static final byte PINK = 0x08;
    private static final byte WHITE = 0x09;
    
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
    
    public byte [] seleccionColorSerie(){
        byte [] byteReturn = new byte [5];
        System.out.println("Ingrese número de 5 dígitos, cada digito corresponderá a un led de la torreta");
        System.out.println("0-Apagar\t1-Rojo\t2-Ambar\t3-Limón\t4-Verde\n"
                + "5-Azul Cielo\t6-Azul\t7-Morado\t8-Rosa\t9-Blanco");
        System.out.print("Serie: ");
        String resp = new Scanner(System.in).nextLine();
        for(int i = 0; i < 5; i++){
            switch(resp.charAt(i)){
                case '0':
                    byteReturn[i] = this.turnOff;
                    break;
                case '1':
                    byteReturn[i] = this.RED;
                    break;
                case '2':
                    byteReturn[i] = this.AMBER;
                    break;
                case '3':
                    byteReturn[i] = this.LEMON;
                    break;
                case '4':
                    byteReturn[i] = this.GREEN;
                    break;
                case '5':
                    byteReturn[i] = this.SKY_BLUE;
                    break;
                case '6':
                    byteReturn[i] = this.BLUE;
                    break;
                case '7':
                    byteReturn[i] = this.PURPLE;
                    break;
                case '8':
                    byteReturn[i] = this.PINK;
                    break;
                case '9':
                    byteReturn[i] = this.WHITE;
                    break;
                default:
                    byteReturn[i] = this.turnOff;
                    break;
            }
        }
        return byteReturn;
    } 
    
    public void loop(){
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    byte[] message;
                    message = Arrays.copyOf(MESSAGE_HEADER, HEADER_LENGTH + BODY_LENGTH);
                    System.arraycopy(seleccionColorSerie(), 0, message, HEADER_LENGTH, BODY_LENGTH);
                    message = Arrays.copyOf(message, MESSAGE_LENGTH);
                    System.arraycopy(MESSAGE_FOOTER, 0, message, HEADER_LENGTH + BODY_LENGTH, FOOTER_LENGTH);
                    out.write(message);
                    System.out.println("Cambiando de Color...");
                    Thread.sleep(5000);
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
