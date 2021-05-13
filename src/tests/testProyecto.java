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
	public static List<Wallet> banco = new ArrayList<Wallet>();
	
	public static int difficulty = 5;
	public static float minimumTransaction = 0.1f;
	public static Wallet pepePurse;
	public static Transaction genesisTransaction;
	
	public static Block currentBlock;
	public static int exit = 0;

	//Main
	public static void main(String[] args) {
		
		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);
		
		initialize();
		newUser("Dodgberto", "elonmusk");
		newUser("Elsapito", "belinda");
		newUser("","");
		
		while(exit != 0) {
			
			
			
		}
		
		//buyPepecoin(getWallet("Dodgberto"), "elonmusk", 420);
		
		//sellPepecoin(getWallet("Dodgberto"), "elonmusk", 1764);
		
		//isChainValid();
		
	}
	
	public static void newUser(String username, String password) {
		banco.add(new Wallet(username, password));
		
	}
	
	public static Wallet getWallet(String username) {
		for(Wallet wallet: banco) {
			if(wallet.getUsername() == username) {
				return wallet;
			}
		}
		return null;
	}
	
	//Creamos Bloque de transaccion
	public static void blockTransaction(Wallet walletA, String password, Wallet walletB, float value) {
		if(password == walletA.getPassword()) {
			Block newBlock = new Block(currentBlock.hash);
			System.out.println("\n" + walletA.getUsername() + " balance is: " + walletA.getBalance());
			System.out.println("\n" + walletA.getUsername() + " esta enviando " + value + " pepeCoins a " + walletB.getUsername() + "...");
			newBlock.addTransaction(walletA.sendFunds(walletB.publicKey, value));
			addBlock(newBlock);
			System.out.println("\n" + walletA.getUsername() + " balance: " + walletA.getBalance());
			System.out.println(walletB.getUsername() + " balance: " + walletB.getBalance());
			currentBlock = newBlock;
		} else {
			System.out.println("Validacion incorrecta. Revise su contraseña.");
		}
		
	}
	
	//Comprar pepecoins con dollars
	public static void buyPepecoin(Wallet walletA, String password, float dollar) {
		float pepecoin = (float) (dollar * 4.20);
		if(password == walletA.getPassword()) {
			Block newBlock = new Block(currentBlock.hash);
			System.out.println("\n" + walletA.getUsername() + " balance is: " + walletA.getBalance());
			System.out.println("\n" + walletA.getUsername() + " cambio " + dollar + "$ en pepeCoins(" + pepecoin + ").");
			if(newBlock.addTransaction(pepePurse.sendFunds(walletA.publicKey, pepecoin))) {
				addBlock(newBlock);
				System.out.println("\n" + walletA.getUsername() + " balance: " + walletA.getBalance());
				System.out.println(pepePurse.getUsername() + " balance: " + pepePurse.getBalance());
				currentBlock = newBlock;
				walletA.dollars -= dollar;
			} 
		} else {
			System.out.println("Validacion incorrecta. Revise su contraseña.");
		}
		
	}
	
	//Comprar pepecoins con dollars
	public static void sellPepecoin(Wallet walletA, String password, float pepecoin) {
		float dollar = (float) (pepecoin / 4.20);
		if(password == walletA.getPassword()) {
			Block newBlock = new Block(currentBlock.hash);
			System.out.println("\n" + walletA.getUsername() + " balance is: " + walletA.getBalance());
			System.out.println("\n" + walletA.getUsername() + " cambio " + pepecoin + " pepecoins a " + dollar + "$.");
			if(newBlock.addTransaction(walletA.sendFunds(pepePurse.publicKey, pepecoin))) {
				addBlock(newBlock);
				System.out.println("\n" + walletA.getUsername() + " balance: " + walletA.getBalance());
				System.out.println(pepePurse.getUsername() + " balance: " + pepePurse.getBalance());
				currentBlock = newBlock;
				walletA.dollars += dollar;
			} 
		} else {
			System.out.println("Validacion incorrecta. Revise su contraseña.");
		}	
	}

	
	public static void printJsonBuilder () {
		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);		
		System.out.println(blockchainJson);
	}
	
	public static void initialize () {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); //Bouncey Security Provider
		
		Wallet coinbase = new Wallet("Pepe", "pepecoin");
		pepePurse = new Wallet("admin", "admin");
		genesisTransaction = new Transaction(coinbase.publicKey, pepePurse.publicKey, 10000f, null);
		genesisTransaction.generateSignature(coinbase.privateKey);	 //manually sign the genesis transaction	
		genesisTransaction.transactionId = "0"; //manually set the transaction id
		genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.reciepient, genesisTransaction.value, genesisTransaction.transactionId)); //manually add the Transactions Output
		UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); //its important to store our first transaction in the UTXOs list.
		System.out.println("Creando y minando bloque genesis...");
		Block genesis = new Block("0");
		genesis.addTransaction(genesisTransaction);
		addBlock(genesis);
		currentBlock = genesis;
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
					System.out.println("#Firma en la Toransaccion (" + t + ") es invalida.");
					return false; 
				}
				if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
					System.out.println("#Los valores no coinciden en la Transaccion (" + t + ")");
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
	
}