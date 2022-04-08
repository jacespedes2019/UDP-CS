import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Clase encargada de recibir la cantidad de clientes y el tipo de paquete que se desea para asi ejecutar
 * cada uno de los clientes como un Thread, mandandole los atributos por parametro.
 *
 */
public class ThreadsClientes {
	public static void main(String args[]) throws IOException {
		BufferedReader lector = new BufferedReader (new InputStreamReader (System.in));
		System.out.println("Hola, cuantos clientes desea poner (Poner los mismos del servidor): ");
		int cantClientes= Integer.parseInt(lector.readLine());
		System.out.println("Que archivo desea escoger: 1. 100MB 2. 250MB");
		int archivo= Integer.parseInt(lector.readLine());
		String arch="";
		if(archivo==1){
			arch="../100MB.txt";
		}
		else arch="../250MB.txt";
		ClienteUDP[] clientes=new ClienteUDP[cantClientes];

		for(int i=0; i<cantClientes;i++){
			clientes[i]=new ClienteUDP(i,arch, "Si",cantClientes);		
			clientes[i].start();
		}
	}
}
