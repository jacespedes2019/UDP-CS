package ClienteServidorUDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Scanner;

public class ServidorUDP {
	static ServidorDelegadoUDP thread=null;
	public static void main(String[] args) throws IOException {
		Scanner lector = new Scanner(System.in);
		//puerto de nuestro servidor
		final int PUERTO = 5000;
		System.out.println("Digite el numero de clientes que desea que se conecten en simultaneo: ");
		int cantClientes = lector.nextInt();
		System.out.println("Bienvenido al servidor");
		DatagramSocket socketServUDP = new DatagramSocket(5000);
	    byte[] buffer = new byte[50000];
	    DatagramPacket paquete =new DatagramPacket(buffer,buffer.length);
	    int numeroServidor=0;
	    while (numeroServidor<cantClientes) {
			//Espero a que un cliente mande el mensaje del archivo que desea
	    	socketServUDP.receive(paquete);
			System.out.println("Solicitud mandada por el cliente: "+ numeroServidor);
			numeroServidor++;		
		}
	    thread= new ServidorDelegadoUDP(paquete, numeroServidor);
		thread.start();
	    socketServUDP.close(); 

	}
}
