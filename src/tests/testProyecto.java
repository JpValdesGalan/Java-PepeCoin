package tests;

import clases.*;
import java.security.*;
import java.util.*;

import com.google.gson.GsonBuilder;

//Cambio de dollar a pepeCoin
//Pasar todo a un servidor rest

public class testProyecto {
	
	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	public static HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();
	
	public static int difficulty = 5;
	public static float minimumTransaction = 0.1f;
	public static Wallet walletA;
	public static Wallet walletB;
	public static Transaction genesisTransaction;
	
	public static Block currentBlock;

	//Main
	public static void main(String[] args) {
		
		//add our blocks to the blockchain ArrayList:
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); //Bouncey Security Provider
		
		//Creamos los wallets:
		walletA = new Wallet("Dodgeberto", "elonmusk");
		walletB = new Wallet("Elsapito", "belinda");		
		Wallet coinbase = new Wallet("Pepe", "pepecoin");
		
		setGenesisFirstTransaction(coinbase);
		
		blockTransaction(walletA, walletB, 40f);
		blockTransaction(walletA, walletB, 1000f);
		blockTransaction(walletA, walletB, 5f);
		blockTransaction(walletB, walletA, 20f);
		blockTransaction(walletB, walletA, 10f);
		blockTransaction(walletA, walletB, 15f);
		blockTransaction(walletA, walletB, 20f);
		
		isChainValid();
		
		printJsonBuilder();
		
	}
	
	public static Boolean isChainValid() {
		Block currentBlock; 
		Block previousBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		HashMap<String,TransactionOutput> tempUTXOs = new HashMap<String,TransactionOutput>(); //a temporary working list of unspent transactions at a given block state.
		tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
		
		//loop through blockchain to check hashes:
		for(int i=1; i < blockchain.size(); i++) {
			
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i-1);
			//compare registered hash and calculated hash:
			if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
				System.out.println("#Los Hashes actuales no coinciden");
				return false;
			}
			//compare previous hash and registered previous hash
			if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
				System.out.println("#Los Hashes previos no coinciden");
				return false;
			}
			//check if hash is solved
			if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
				System.out.println("#Este bloque no ha sido minado");
				return false;
			}
			
			//loop thru blockchains transactions:
			TransactionOutput tempOutput;
			for(int t=0; t <currentBlock.transactions.size(); t++) {
				Transaction currentTransaction = currentBlock.transactions.get(t);
				
				if(!currentTransaction.verifySignature()) {
					System.out.println("#Signature on Transaction(" + t + ") is Invalid");
					return false; 
				}
				if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
					System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
					return false; 
				}
				
				for(TransactionInput input: currentTransaction.inputs) {	
					tempOutput = tempUTXOs.get(input.transactionOutputId);
					
					if(tempOutput == null) {
						System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
						return false;
					}
					
					if(input.UTXO.value != tempOutput.value) {
						System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
						return false;
					}
					
					tempUTXOs.remove(input.transactionOutputId);
				}
				
				for(TransactionOutput output: currentTransaction.outputs) {
					tempUTXOs.put(output.id, output);
				}
				
				if( currentTransaction.outputs.get(0).reciepient != currentTransaction.reciepient) {
					System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
					return false;
				}
				if( currentTransaction.outputs.get(1).reciepient != currentTransaction.sender) {
					System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
					return false;
				}
				
			}
			
		}
		System.out.println("Blockchain valido");
		return true;
	}
	
	public static void addBlock(Block newBlock) {
		newBlock.mineBlock(difficulty);
		blockchain.add(newBlock);
	}
	
	//Crea primer bloque genesis
	public static void setGenesisFirstTransaction(Wallet coinbase) {
		genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
		genesisTransaction.generateSignature(coinbase.privateKey);	 //manually sign the genesis transaction	
		genesisTransaction.transactionId = "0"; //manually set the transaction id
		genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.reciepient, genesisTransaction.value, genesisTransaction.transactionId)); //manually add the Transactions Output
		UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); //its important to store our first transaction in the UTXOs list.
		System.out.println("Creating and Mining Genesis block... ");
		Block genesis = new Block("0");
		genesis.addTransaction(genesisTransaction);
		addBlock(genesis);
		currentBlock = genesis;
	}
	
	//Creamos Bloque de transaccion
	public static void blockTransaction(Wallet walletA, Wallet walletB, float value) {
		Block newBlock = new Block(currentBlock.hash);
		System.out.println("\n" + walletA.getUsername() + " balance is: " + walletA.getBalance());
		System.out.println("\n" + walletA.getUsername() + " esta enviando " + value + " pepeCoins a " + walletB.getUsername() + "...");
		newBlock.addTransaction(walletA.sendFunds(walletB.publicKey, value));
		addBlock(newBlock);
		System.out.println("\n" + walletA.getUsername() + " balance: " + walletA.getBalance());
		System.out.println(walletB.getUsername() + " balance: " + walletB.getBalance());
		currentBlock = newBlock;
		
	}
	
	public static void printJsonBuilder () {
		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);		
		System.out.println(blockchainJson);
	}
	
}
/*public class testProyecto {
	
	//Main
	public static ArrayList<Block> blockChain = new ArrayList<Block>(); 
	public static int difficulty = 6;

	public static void main(String[] args) {	
		
		//Añade los bloques al ArrayList
		blockChain.add(new Block("Primer Bloque", "1"));	
		System.out.println("Minando Primer Bloque...");
		blockChain.get(0).mineBlock(difficulty);
		
		blockChain.add(new Block("Segundo Bloque",blockChain.get(blockChain.size()-1).hash)); 
		System.out.println("Minando Segundo Bloque...");
		blockChain.get(1).mineBlock(difficulty);
		
		blockChain.add(new Block("Tercer Bloque",blockChain.get(blockChain.size()-1).hash));
		System.out.println("Minando Tercer Bloque...");
		blockChain.get(2).mineBlock(difficulty);
		
		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockChain);		
		System.out.println(blockchainJson);
		
	}
	
	//-------------------------------------------------------------------------------------------------------------------
	
	public static Boolean isChainValid() {
		Block currentBlock;
		Block previousBlock;
		
		//For para verificar hashes de la blockChain
		for (int i = 1; i < blockChain.size(); i++) {
			currentBlock = blockChain.get(i);
			previousBlock = blockChain.get(i-1);
			//Compara Hash registrado con el Hash calculado
			if (!currentBlock.hash.equals(currentBlock.calculateHash()) ){
				System.out.println("Current Hashes not equal");			
				return false;
			}
			//Compara el previousHash registrado con el previousHash
			if (!previousBlock.hash.equals(currentBlock.previousHash) ) {
				System.out.println("Previous Hashes not equal");
				return false;
			}
		}
		return true;
	}

}*/
