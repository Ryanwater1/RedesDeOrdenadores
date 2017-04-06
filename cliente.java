

import java.io.*;
import java.net.*;
import java.util.*;

public class cliente {
    public static void main(String argv[]) {	
	System.out.print("Inicializando Cliente...");
	if (argv.length != 3) {//Si no recibe ningún parámetro dará error
	    System.out.println("\033[31m\t[FAIL]\u001B[0m");
	    System.err.println("Parámetros de entrada incorrectos");
	    System.exit(1);
	}      
	InetAddress address = null;
	int puerto=0;
        if(argv[2].equals("tcp")){
	try
	    {
		address = InetAddress.getByName(argv[0]);
		//System.out.println("Dirección ip del servidor: "+address);
	    }
	catch(UnknownHostException uhe)
	    {
		System.out.println("\033[31m\t[FAIL]\u001B[0m");
		System.err.println("Host no encontrado : " + uhe);
		System.exit(-1);
	    }
	try {// Puerto que hemos usado para el servidor
	    puerto = Integer.parseInt(argv[1]);
	    //System.out.println("Puerto servidor: "+puerto);
	} catch(NumberFormatException e) {
	    System.out.println("\033[31m\t[FAIL]\u001B[0m");
	    System.err.println("Number " + argv[0] + " invalid (" + e.getMessage() + ").");
	    System.exit(1);
	} 
       	Socket socket=null; //Se crea un objeto de tipo socket
	String mensaje=""; //Se declara una variable de tipo string
	//byte [] datos;
	DataOutputStream dos = null;
	DataInputStream dis = null;
	try {
	    //Se abre una excepción
	    //Obtiene el nombre de la dirección remota del socket
	    address=InetAddress.getByName(argv[0]);
	    socket = new Socket(address,puerto); //Se crea un objeto de tipo socket 
	    System.out.println("\033[32m\t[OK]\u001B[0m"); 
	    //Se declara un objeto de tipo DataOutputStream para mandar valores al servidor 
	    dos = new DataOutputStream(socket.getOutputStream());
	    dis = new DataInputStream(socket.getInputStream());
	    do {
		mensaje =""; //Se inicializa la variable de string mensaje a vacío
		//datos = new byte[1024];
		mensaje = dis.readUTF();//mas directo pero nos piden enviar bytes
		//dis.read(datos);
		//mensaje = new String(datos, "UTF-8");
		System.out.println(mensaje); //Imprime el contenido de mensaje  
		System.out.println("puerto remoto: "+socket.getRemoteSocketAddress());          
	    } while (0<1);
	}
	catch (Exception e) {
	    System.out.println("Desconexión remota: "+socket.getRemoteSocketAddress());
	    //System.err.println(e.getMessage());
	    // e.printStackTrace();
	}  finally{   
	    try {// releases all system resources from the streams
		socket.close();
	    } catch (IOException ex) {
		ex.printStackTrace();
	    } catch (NullPointerException ex) {//Servidor inaccesible
		System.out.println("\033[31m\t[FAIL]\u001B[0m");
	    }
	    System.exit(1);
	}
    }
    
    if(argv[2].equals("udp")){
            try {
      DatagramSocket socketUDP = new DatagramSocket();
      byte[] mensaje = "".getBytes();
      InetAddress hostServidor = InetAddress.getByName(argv[0]);
      int puertoServidor = 5060;

      // Construimos un datagrama para enviar el mensaje al servidor
      DatagramPacket peticion =
        new DatagramPacket(mensaje,"".length(), hostServidor,
                           puertoServidor);

      // Enviamos el datagrama
      socketUDP.send(peticion);

      // Construimos el DatagramPacket que contendrá la respuesta
      byte[] bufer = new byte[1000];
      DatagramPacket respuesta =
        new DatagramPacket(bufer, bufer.length);
      socketUDP.receive(respuesta);

      // Enviamos la respuesta del servidor a la salida estandar
      System.out.println("Respuesta: " + new String(respuesta.getData()));

      // Cerramos el socket
      socketUDP.close();

    } catch (SocketException e) {
      System.out.println("Socket: " + e.getMessage());
    } catch (IOException e) {
      System.out.println("IO: " + e.getMessage());
    }
    }
    }
}
