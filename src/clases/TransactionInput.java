package clases;

public class TransactionInput {
	
	public String transactionOutputId; //Referencia al id del remitente
	public TransactionOutput UTXO; //Contains the Unspent transaction output
	
	public TransactionInput(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}

}
