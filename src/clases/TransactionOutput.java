package clases;

import java.security.PublicKey;

public class TransactionOutput {
	
	public String id;
	public PublicKey reciepient; //Destinatario
	public float value; 			
	public String parentTransactionId; //El id donde la transaccion fue creada
	
	//Constructor
	public TransactionOutput(PublicKey reciepient, float value, String parentTransactionId) {
		this.reciepient = reciepient;
		this.value = value;
		this.parentTransactionId = parentTransactionId;
		this.id = StringUtil.applySha256(StringUtil.getStringFromKey(reciepient)+Float.toString(value)+parentTransactionId);
	}
	
	//Verifica si es tuyo
	public boolean isMine(PublicKey publicKey) {
		return (publicKey == reciepient);
	}
	
}