package clases;

import java.util.ArrayList;
import java.util.Date;

public class Block {
	
	public String hash;
	public String previousHash;
	private String merkleRoot;	//Mensaje
	public ArrayList<Transaction> transactions = new ArrayList<Transaction>(); //El data sera un mensaje simple
	private long timeStamp;	//Milisegundos desde 1/1/1970
	private int nonce;
	
	//Constructor
	public Block (String previousHash) {
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		this.hash = calculateHash();
	}
	
	//Crea el nuevo Hash = HashAnterior + Tiempo + nonce + Data
	public String calculateHash() {
		String calculatedHash = StringUtil.applySha256(previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + merkleRoot);
		return calculatedHash;
	}
	
	public void mineBlock (int difficulty) {
		String target = new String(new char[difficulty]).replace('\0', '0');
		
		while (!hash.substring( 0, difficulty).equals(target)) {
			nonce ++;
			hash = calculateHash();
		}
		System.out.println("Bloque Minado!!! : " + hash);
	}
	
	//Add transactions to this block
	public boolean addTransaction(Transaction transaction) {
		//process transaction and check if valid, unless block is genesis block then ignore.
		if(transaction == null) return false;		
		if((!"0".equals(previousHash))) {
			if((transaction.processTransaction() != true)) {
				System.out.println("Transaccion declinada.");
				return false;
			}
		}

		transactions.add(transaction);
		System.out.println("Transaccion añadida al bloque.");
		return true;
	}
	
}