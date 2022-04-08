
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

/**
 * Clase que representa cada cliente como Thread
 *
 */
public class ClienteUDP extends Thread{
	/**
	 * Atributos del cliente
	 */
	public int id;
	public String arch;
	public int total;
	private static BufferedWriter escritorParaLOG;
	/**
	 * si descarga, no descarga
	 */
	public String descarga;
	/**
	 * Puerto en el que esta el servidor
	 */
	public final int SERV_PORT= 5000;
	/**
	 * Buffer que se usa para mandar los mensajes
	 */
	byte[] buffer = new byte[50000]; // 64kb es el maximo de tamano de la fragmentacion
	//Host del servidor
	public final String HOST = "192.168.253.132";
	
	/**
	 * ArrayList que almacena todos los DatagramPacket descargados
	 */
	public ArrayList<DatagramPacket> listPacket=new ArrayList<DatagramPacket>();

	/**
	 * Metodo constructor del cliente
	 * @param id
	 * @param arch archivo que desea
	 * @param descarga
	 * @param total de clientes
	 */
	public ClienteUDP(int id, String arch, String descarga, int total) {
		this.id = id;
		this.arch=arch;
		this.descarga=descarga;
		this.total=total;
	}

	/**
	 * Este metodo se ejecuta al correr el Thread, el cual se encarga de realizar las funciones que debe hacer el cliente
	 * tal como enviar el mensaje para decidir el archivo mediante un socket UDP, realizar los logs de cada cliente y
	 * ademas accede al archivo deseado en cual esta en una direccion multicast dada por el servidor, descarga los datagram packets
	 * de los archivos, los guarda en un arraylist y finalmente los escribe en un archivo con un nombre dado.
	 */
	public void run() {
		System.out.println(descarga);
		int totalArch=totArch();
		//int totalArch=500000000;
		try {
			String fechaHoy = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(Calendar.getInstance().getTime());
			File archivoLog = new File("../logs/" + fechaHoy + "-log.txt");
			escritorParaLOG = new BufferedWriter(new FileWriter(archivoLog));
			//Obtengo la localizacion del servidor
			InetAddress direccionServidor = InetAddress.getByName(HOST);
			//InetAddress direccionServidor = InetAddress.getLocalHost();

			//Creo el socket de UDP
			DatagramSocket socketUDPClient = new DatagramSocket();

			System.out.println("Para cliente: "+id+". El puerto es: "+ socketUDPClient.getLocalPort());
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
				socket.joinGroup(InetAddress.getByName("224.0.0.1"));
				String pathDescarga="";
				pathDescarga="../ArchivosRecibidos/Cliente"+id+"-Prueba-"+total+".txt";
				FileOutputStream fos = new FileOutputStream(pathDescarga);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				//Creo un datagrama
				int loQueVaDelArch=0;
				long inicioEnvio = 0;		
				while(loQueVaDelArch<=totalArch){
					DatagramPacket archivoRecibido = new DatagramPacket(buffer, buffer.length);				
					socket.receive(archivoRecibido);
					if(loQueVaDelArch==0){
						inicioEnvio = System.currentTimeMillis();
					}
					listPacket.add(archivoRecibido);					
					loQueVaDelArch+=archivoRecibido.getLength();
					//System.out.println("Se han descargado "+loQueVaDelArch+" bytes para el cliente "+ id);
				}
				//Se escribe el archivo descargado
				for(int i=0;i<listPacket.size();i++){
					bos.write(listPacket.get(i).getData(), 0, buffer.length);
				}
				bos.flush();
				long finalDescarga = System.currentTimeMillis();
				System.out.println("Archivo guardado para el cliente: "+id);
				File fileDescargado = new File(pathDescarga);
				//Se guarda el archivo de texto con los logs
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
	 * Debido a la perdida de paquetes promedio con la cantidad de clientes simultaneos se decidieron 
	 * los siguientes lamites para que el cliente deje de recibir y poder tomar los tiempos.
	 * @return total del archivo a recibir
	 */
	public int totArch(){
		int totalArch=0;
		if(total<6){
			if(arch.contains("100MB.txt")){
				totalArch=103000000;
			}
			else {
				totalArch=260000000;
			}
		}
		else if(total<8){
			if(arch.contains("100MB.txt")){
				totalArch=102000000;
			}
			else {
				totalArch=255000000;
			}
		}
		else {
			if(arch.contains("100MB.txt")){
				totalArch=100000000;
			}
			else {
				totalArch=250000000;
			}
		}
		return totalArch;
	}


}
