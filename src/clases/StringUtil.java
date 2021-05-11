package clases;

import java.security.MessageDigest;
import com.google.gson.GsonBuilder;

public class StringUtil {
	//Aplicamos el algoritmo Sha256 a un String y retornamos el resultado
	public static String applySha256(String input) {
		
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");	        
			//Aplicamos Sha256 a nuestra entrada 
			byte[] hash = digest.digest(input.getBytes("UTF-8"));	        
			StringBuffer hexString = new StringBuffer(); //Guarda nuestro hash en hexadecimal
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if(hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	//Convierte un objeto en json String
	public static String getJson (Object o) {
		return new GsonBuilder().setPrettyPrinting().create().toJson(o);
	}
	
	//Devuelvela dificultad del String comparado con el Hash
	public static String getDifficultyString (int difficulty) {
		return new String (new char[difficulty]).replace('\0', '0');
	}
}
