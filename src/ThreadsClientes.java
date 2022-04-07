import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ThreadsClientes {
	public static void main(String args[]) throws IOException {
		BufferedReader lector = new BufferedReader (new InputStreamReader (System.in));
		System.out.println("Hola, cuántos clientes desea poner (Poner los mismos del servidor): ");
		int cantClientes= Integer.parseInt(lector.readLine());
		System.out.println("Que archivo desea escoger: 1. 100MB 2. 250MB");
		int archivo= Integer.parseInt(lector.readLine());
		String arch="";
		if(archivo==1){
			arch="100MB.txt";
		}
		else arch="250MB.txt";
		ClienteUDP[] clientes=new ClienteUDP[cantClientes];

		for(int i=0; i<cantClientes;i++){
			clientes[i]=new ClienteUDP(i,arch, "Si",cantClientes);		
			clientes[i].start();
		}
	}
}
