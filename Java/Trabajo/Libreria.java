package Trabajo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Libreria {
	public static String MostrarMenu(ArrayList<String> opciones) 
	{             
	String cadena = ""; 
	for (String info : opciones)
	  { 
	cadena = cadena + info + "\n"; 
	         }
	 return cadena; 
	 
	}
	public static boolean EsNumeroEntero(String dato) {
		 for (char c : dato.toCharArray()) {
		  if (!Character.isDigit(c)) {
		  return false;
		          }
		      }
		      return true;
		  }
		
		 
		 public static boolean EsNumeroDouble(String dato) {
		  boolean valido = false;
		  for (char c : dato.toCharArray()) {
		  if (!Character.isDigit(c)) {
		        if (c == '.' && !valido) {
		  valido = true;
		  } 
		 else {
		                 return false;
		               }
		  }
		  }
		  return valido;
		  }
		 
		 
		 
		 public static boolean EvaluarNumerico(String dato, int tipo)
		 {
		  boolean valido = false;
		  switch (tipo) {
		  case 1:
		  valido = EsNumeroEntero(dato);
		  break;
		 
		  case 2: 
		 valido = EsNumeroDouble(dato);
		  break;
		 
		 }
		  return valido;
		  }
		 
		 public static String Dialogo(String texto) throws IOException 
		{ 
		String cadena; 
		System.out.println(texto + " : "); 
		BufferedReader lectura = new BufferedReader(new InputStreamReader(System.in)); 
		cadena = lectura.readLine(); 
		return cadena; 
		}
		 
		 public static String Leer(String texto) throws IOException
		  { 
		 String cadena = ""; 
		 cadena = Dialogo(texto); 
		 if (cadena != null) 
		 { 
		 cadena = cadena.trim(); 
		 if (cadena.isEmpty())
		  cadena=null;
		  }
		  else
		  cadena = null; 
		
		  return cadena; 
		 }
		 
		 public static String DesplegarMenu(String Titulo1, ArrayList<String> menu) throws IOException 
		{ 
		String cadena; 
		       cadena=Titulo1 + "\n" + "\n"; 
		cadena=cadena+MostrarMenu(menu);
		 cadena = cadena +"\n Que opcion deseas "; 
		return  cadena = Dialogo(cadena);
		 }
		 
		 public static String RellenarEspacios(String dato, int tamano)
		  { 
		return String.format("%1$-" + tamano + "s", dato); 
		}
		 
		 public static String Fecha() {
		  Date fecha = new Date();
		  SimpleDateFormat formatodia = new SimpleDateFormat("dd-MM-yyyy");
		  return formatodia.format(fecha);
		  }
		 
		 
		 public static String IdTicketSiguiente(String idticket) {
		  String idticketnext = "";
		  int num = Integer.parseInt(idticket) + 1;
		  if (num < 10) 
		 { idticketnext = "00" + String.valueOf(num).trim(); }  
		 else if ((num > 9) && (num < 100)) 
		 { idticketnext = "0" + String.valueOf(num).trim(); 
		 }
		  else {
		  idticketnext = String.valueOf(num).trim(); 
		 }
		 
		 return idticketnext;
		 }
		 
		  // agregagrlo
		
		
		 public static int ObtenerUltimaPosicion(ArrayList matriz) {
		     
		int ultimaPosicion = -1;
		if(matriz!=null)
		ultimaPosicion = matriz.size()-1;
		return ultimaPosicion;
		}
		 
		  public static String MostrarObjetos(String[] vObjeto) { 
			  String codigo = RellenarEspacios( vObjeto[0], 5); 
			  String producto = RellenarEspacios( vObjeto[1], 30); 
			  String precio = RellenarEspacios( vObjeto[2], 10); 
			  String cantidad =RellenarEspacios( vObjeto[3], 10);
			   String cadena = codigo.concat(producto+precio+cantidad); 
			  return cadena; 
			  }
			  
			  
			   public static String MostrarLista(String[][] vproductos) 
			  { 
				   String salida = "";
				   for (int ciclo = 0; ciclo < 10; ciclo++) 
				   { 
					   String[] vproducto = {vproductos[ciclo][0], vproductos[ciclo][1], vproductos[ciclo][2], vproductos[ciclo][3]};
					   String cadena = MostrarProducto(vproducto); 
					   salida = salida.concat(cadena + "\n"); 
					   } 
			return salida; 
			 }
			private static String MostrarProducto(String[] vproducto) {
				// TODO Auto-generated method stub
				return null;
			}
}