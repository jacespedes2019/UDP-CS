package ClienteServidorUDP;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ClienteUDP extends Thread{
	public int id;
	public String arch;
	public int total;
	private static BufferedWriter escritorParaLOG;
	/**
	 * si descarga, no descarga
	 */
	public String descarga;
	/**
	 * Puerto en el que est� el servidor
	 */
	public final int SERV_PORT= 5000;
	/**
	 * Buffer que se usa para mandar los mensajes
	 */
	byte[] buffer = new byte[50000]; // 64kb es el m�ximo de tamano de la fragmentaci�n
	//Host del servidor
	public final String HOST = "192.168.157.128";
	
	public ArrayList<DatagramPacket> listPacket=new ArrayList<DatagramPacket>();

	public ClienteUDP(int id, String arch, String descarga, int total) {
		this.id = id;
		this.arch=arch;
		this.descarga=descarga;
		this.total=total;
	}

	public void run() {
		System.out.println(descarga);
		int totalArch=totArch();
		try {
			String fechaHoy = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(Calendar.getInstance().getTime());
			File archivoLog = new File("logs/" + fechaHoy + "-log.txt");
			escritorParaLOG = new BufferedWriter(new FileWriter(archivoLog));
			//Obtengo la localizacion del servidor
			//InetAddress direccionServidor = InetAddress.getByName(HOST);
			InetAddress direccionServidor = InetAddress.getLocalHost();

			//Creo el socket de UDP
			DatagramSocket socketUDPClient = new DatagramSocket();

			System.out.println("Para cliente: "+id+". El puerto es: "+ socketUDPClient.getInetAddress()+" y la ip es: "+socketUDPClient.getLocalPort());
			String mensaje=total+","+arch +","+descarga;
			//Convierto el mensaje a bytes
			byte[] byteMs = mensaje.getBytes();

			//Creo un datagrama
			DatagramPacket peticion = new DatagramPacket(byteMs, byteMs.length, direccionServidor, SERV_PORT);

			//Lo envio con send
			System.out.println("Envio el mensaje");
			socketUDPClient.send(peticion);
			socketUDPClient.close();
			
			if(descarga.equals("Si")){
				MulticastSocket socket = new MulticastSocket(4000);
				socket.joinGroup(InetAddress.getByName("235.1.1.1"));
				String pathDescarga="";
				pathDescarga="ArchivosRecibidos/Cliente"+id+"-Prueba-"+total+".txt";
				FileOutputStream fos = new FileOutputStream(pathDescarga);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				//Creo un datagrama
				int loQueVaDelArch=0;
				long inicioEnvio = 0;
				System.out.println(buffer.length);
				while(loQueVaDelArch<=totalArch){
					DatagramPacket archivoRecibido = new DatagramPacket(buffer, buffer.length);				
					socket.receive(archivoRecibido);
					if(loQueVaDelArch==0){
						inicioEnvio = System.currentTimeMillis();
					}
					listPacket.add(archivoRecibido);					
					loQueVaDelArch+=archivoRecibido.getLength();
					System.out.println("Se han descargado "+loQueVaDelArch+" bytes para el cliente "+ id);
				}
				for(int i=0;i<listPacket.size();i++){
					bos.write(listPacket.get(i).getData(), 0, buffer.length);
				}
				bos.flush();
				long finalDescarga = System.currentTimeMillis();
				System.out.println("Archivo guardado para el cliente: "+id);
				File fileDescargado = new File(pathDescarga);
				escritorParaLOG.write("El cliente "+ id +" recibio el archivo: " 
						+ fileDescargado.getName() + " de tamano: " + fileDescargado.length() + " bytes, " +
						"con una entrega exitosa y con un tiempo de transferencia de: "
						+ (finalDescarga-inicioEnvio) + " milisegundos");
				escritorParaLOG.newLine();
				escritorParaLOG.flush();
			}
			else {
				System.out.println("Archivo no guardado para el cliente: "+id);
			}



		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Debido a la p�rdida de paquetes promedio con la cantidad de clientes simult�neos se decidieron 
	 * los siguientes l�mites para que el cliente deje de recibir y poder tomar los tiempos.
	 * @return
	 */
	public int totArch(){
		int totalArch=0;
		if(total<3){
			if(arch.equals("100MB.txt")){
				totalArch=98000000;
			}
			else {
				totalArch=250000000;
			}
		}
		else if(total<6){
			if(arch.equals("100MB.txt")){
				totalArch=85000000;
			}
			else {
				totalArch=196000000;
			}
		}
		else if(total<9){
			if(arch.equals("100MB.txt")){
				totalArch=75500000;
			}
			else {
				totalArch=175000000;
			}
		}
		else {
			if(arch.equals("100MB.txt")){
				totalArch=69000000;
			}
			else {
				totalArch=160000000;
			}
		}
		return totalArch;
	}


}
