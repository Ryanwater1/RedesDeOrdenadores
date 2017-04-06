

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import java.lang.*;

public class server {    
    public static void main(String argv[]) throws IOException {	
        ServerSocket ss;
        System.out.print("Inicializando servidor... ");
	//Si no recibe ningún parámetro dará error
	if (argv.length != 2) {
	    System.out.println("\033[31m\t[FAIL]\u001B[0m");
	    System.err.println("Parámetros de entrada incorrectos");
	    System.exit(1);
	}     
	int puerto=0;
	try {// Puerto que hemos usado para el servidor
	    puerto = Integer.parseInt(argv[0]);
	} catch(NumberFormatException e) {
	    System.out.println("\033[31m\t[FAIL]\u001B[0m");
	    System.err.println("Puerto incorrecto");
	    System.exit(1);
	}
	
	FileReader f = new FileReader(argv[1]);

	BufferedReader b = new BufferedReader(f);
	boolean vacio = false;
	String cadena;
	String[] aux = new String[255];
	int j=0;
	do {
		
		
		
		
		for (int i = 0; i <255; i++) {
			cadena = b.readLine();
			if (cadena == null) {
				vacio = true;
				break;
			}
			
			aux[i] = cadena;
			j++;

		}
	}while(vacio==false);
	
        try {
            ss = new ServerSocket(puerto);
            System.out.println("\033[32m\t[OK]\u001B[0m");
            int idSession = 0;
            //Creo hilo para UDP
            ((UDPHilo) new UDPHilo(puerto,aux,j)).start();
            while (true) {
                System.err.println("Inic2");
                Socket socket;
                socket = ss.accept();
                System.out.println("Nueva conexión entrante: "+socket);
                ((ServidorHilo) new ServidorHilo(socket, idSession,aux)).start();
                idSession++;
            }	    
        } catch (IOException ex) {
            Logger.getLogger(server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
class UDPHilo extends Thread{
    private DatagramSocket dsock;
    private byte[] receiveData = new byte[1024];
    private byte[] sendData = new byte[1024];
    private String[] archivo;
    int j;
    public UDPHilo (int puerto,String[] archivo,int j) throws SocketException{
        this.dsock= new DatagramSocket(puerto);
        this.archivo = archivo;
        this.j =j;
    }
    public void run() {
        System.out.println("Iniciado(1)");
        while(true){
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
            	Random rng = new Random();
                dsock.receive(receivePacket);
                System.out.println("RECEIVED");
                InetAddress IPAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();
                String envio = archivo[rng.nextInt(j)];
                sendData= envio.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                dsock.send(sendPacket);
            } catch (IOException ex) {
                Logger.getLogger(UDPHilo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
class ServidorHilo extends Thread {
   
    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;
    private String[] archivo;
    private int idSessio,i;
    public ServidorHilo(Socket socket, int id, String[] archivo) {
        this.socket = socket;
        this.idSessio = id;
        this.archivo = archivo;
        try {
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(ServidorHilo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void desconnectar() {
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ServidorHilo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void run() {	
    	
	do {	
	    java.util.Date utilDate = new java.util.Date(); //fecha actual
	   
	    try{
		
		dos.writeUTF(archivo[idSessio]);//mas directo pero nos piden enviar bytes	quitar getlocalport socket.getLocalPort()	
		//dos.write(datos);
		dos.flush();  
		
		Thread.sleep(1000);                 //1000 milliseconds is one second.
	    } catch (IOException ex) {
		System.out.println("Desconexión remota: "+socket.getRemoteSocketAddress());
		desconnectar();
		//Logger.getLogger(ServidorHilo.class.getName()).log(Level.SEVERE, null, ex);
		break;
	    } catch(InterruptedException ex){
		Thread.currentThread().interrupt();
	    }
	}
	while (0<1);
        desconnectar();
    }
}
