package tests;

import java.util.ArrayList;
import com.google.gson.GsonBuilder;
import clases.*;

public class testProyecto {
	
	//Main
	public static ArrayList<Block> blockChain = new ArrayList<Block>(); 
	public static int difficulty = 3;

	public static void main(String[] args) {	
		
		//A�ade los bloques al ArrayList
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

}
