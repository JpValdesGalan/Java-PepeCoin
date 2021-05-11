package clases;

import java.security.MessageDigest;
import java.util.Base64;
import java.security.*;

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
	
	//Applies ECDSA Signature and returns the result ( as bytes ).
	public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
		Signature dsa;
		byte[] output = new byte[0];
		try {
			dsa = Signature.getInstance("ECDSA", "BC");
			dsa.initSign(privateKey);
			byte[] strByte = input.getBytes();
			dsa.update(strByte);
			byte[] realSig = dsa.sign();
			output = realSig;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
			return output;
		}
	
		//Verifies a String signature 
		public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
			try {
				Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
				ecdsaVerify.initVerify(publicKey);
				ecdsaVerify.update(data.getBytes());
				return ecdsaVerify.verify(signature);
			}catch(Exception e) {
				throw new RuntimeException(e);
			}
		}

		public static String getStringFromKey(Key key) {
			return Base64.getEncoder().encodeToString(key.getEncoded());
		}
}
