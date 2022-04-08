import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Scanner;

/**
 * Clase encargada de recibir la cantidad de clientes a conectar y de recibir solicitudes de cada uno de estos 
 * para asi mandar el paquete a todos los clientes mediante un Thread de un servidor Multicast.
 *
 */
public class ServidorUDP {
	public static void main(String[] args) throws IOException {
		Scanner lector = new Scanner(System.in);
		System.out.println("Digite el numero de clientes que desea que se conecten en simultaneo: ");
		int cantClientes = lector.nextInt();
		System.out.println("Bienvenido al servidor");
		DatagramSocket socketServUDP = new DatagramSocket(5000);
	    byte[] buffer = new byte[50000];
	    DatagramPacket paquete =new DatagramPacket(buffer,buffer.length);
	    int numeroServidor=0;
	    //Se espera a que todos manden la solicitud del paquete
	    while (numeroServidor<cantClientes) {
			//Espero a que un cliente mande el mensaje del archivo que desea
	    	socketServUDP.receive(paquete);
			System.out.println("Solicitud mandada por el cliente: "+ numeroServidor);
			numeroServidor++;		
		}
	    //Ejecuto un thread de un servidor Multicast para mandar el archivo
	    ServidorDelegadoUDP thread= new ServidorDelegadoUDP(paquete, numeroServidor);
		thread.start();
	    socketServUDP.close(); 

	}
}
