
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketException;

public class ServidorDelegadoUDP extends Thread{
	public int id;
	public DatagramPacket mensaje;
	public byte[] buffer = new byte[50000];
	public MulticastSocket MulsocketServUDP;

	/**
	 * Metodo constructor que crea un servidor multicast dado la peticion de los clientes
	 */
	public ServidorDelegadoUDP(DatagramPacket paquete, int id) {
		this.id=id;
		this.mensaje=paquete;
		try {
			MulsocketServUDP=new MulticastSocket();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void run(){
		System.out.println("Mensaje del cliente recibido");
		//Convierto lo recibido
		String mensajeRecibido = new String(mensaje.getData());
		//total+","+arch +","+descarga
		String[] ms=mensajeRecibido.split(",");

		//Especifica el archivo
		String archivo=ms[1];
		String siDescarga=ms[2];
		
		if(siDescarga.contains("i")){
			System.out.println("Entro");			
			try {
				InetAddress direccion = InetAddress.getByName("224.0.0.1");
				int puerto = 4000;
				System.out.println("Para los clientes el puerto es: "+ puerto+" y la ip multicast es: "+direccion);
				File file = new File(archivo);
				FileInputStream fis;
				System.out.println(file.getName());
				fis = new FileInputStream(file);
				int contador=0;
				System.out.println(fis.read(buffer));
				int totDat=0;
				while((contador = fis.read(buffer)) != -1){
					DatagramPacket archivoAEnviar = new DatagramPacket(buffer,contador,direccion,puerto);
				    MulsocketServUDP.send(archivoAEnviar);
				    totDat++;
				}
				
				System.out.println("Archivo de "+totDat+" datagramas enviado con exito para el Multicast");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		else {
			System.out.println("No se descargo nada en el cliente:  " + id);
		}
	}

}
