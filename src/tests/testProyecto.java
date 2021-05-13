package tests;

import java.util.*;
import clases.*;
import tools.*;


public class testProyecto {

	public static List<Wallet> banco = new ArrayList<Wallet>();
	
	public static Block currentBlock;
	public static Wallet pepePurse;

	//Main
	public static void main(String[] args) {
		
		Bundle bundle = BlockChain.initialize(pepePurse);
		
		currentBlock = bundle.block;
		pepePurse = bundle.wallet;
		
		newUser("Dodgberto", "elonmusk");
		newUser("Elsapito", "belinda");
		newUser("Macumba","shakira");
		newUser("admin", "admin");
		
		int exitPrograma = 0;
		int exitMenu = 0;
		int opc;
		float cantidad;
		String username;
		String password;

		Wallet user;
		
		while(exitPrograma == 0) {
			
			
			System.out.println("\nSeleccione una opcion: \n1. Iniciar Sesion\n2. Registrarse\n3.Salir");
			opc = Leer.datoInt();
			switch(opc) {
			
			case 1:
				System.out.println("\nUsername: ");
				username = Leer.dato();
				System.out.println("Password: ");
				password = Leer.dato();
				user = getWallet(username);
				try {
					
					if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
						exitMenu = 0;
						//Nuevo menu
						do {
							System.out.println("\n1. Comprar PepeCoins\n2. Vender PepeCoins\n3. Transferir PepeCoins\n4. Ver Libreta de cuentas\n5. Modificar datos de usuario\n6. Eliminar Pepecuenta\n7. Salir");
							opc = Leer.datoInt();
							switch(opc) {
							case 1:
								user.getAccountBalance();
								System.out.println("Cuantos dolares quieres cambiar a Pepecoins: ");
								cantidad = Leer.datoFloat();
								System.out.println("Ingresa la password para confirmar: ");
								password = Leer.dato();
								buyPepecoin(user, password, cantidad);
								break;
							case 2:
								user.getAccountBalance();
								System.out.println("Cuantos pepeCoins quieres cambiar a dolares: ");
								cantidad = Leer.datoFloat();
								System.out.println("Ingresa la password para confirmar: ");
								password = Leer.dato();
								sellPepecoin(user, password, cantidad);
								break;
							case 3:
								user.getAccountBalance();
								System.out.println("PepeTransferencia a la PepeCuenta de: ");
								username = Leer.dato();
								System.out.println("Pepecoins a transferir: ");
								cantidad = Leer.datoFloat();
								System.out.println("Ingresa la password para confirmar: ");
								password = Leer.dato();
								blockTransaction(user, password, getWallet(username), cantidad);
								break;
							case 4:
								BlockChain.printJsonBuilder();
								BlockChain.isChainValid();
								break;
							case 5:
								System.out.println("\nIngresa tu password actual para confirmar: ");
								password = Leer.dato();
								if(user.getPassword().equals(password)) {
									System.out.println("Ingresar nuevo username: ");
									username = Leer.dato();
									System.out.println("Ingresar nueva password: ");
									password = Leer.dato();	
									user.setUsername(username);
									user.setUsername(password);
								} else {
									System.out.println("Contraseña incorrecta.");
									exitPrograma = 1;
								}
								break;
							case 6:
								System.out.println("\nEscribe la contraseña si estas seguro de borrar tu PepeCuenta: ");
								password = Leer.dato();
								deleteUser(user.getUsername(), password);
								exitMenu = 1;
								break;
							case 7:
								exitMenu = 1;
							default:
								break;
							}
						} while(exitMenu == 0);
					} else {
						System.out.println("Credenciales incorrectas.");
					}
					
				} catch (NullPointerException e) {
					System.out.println("Usuario Inexistente.\nAdios");
				}
				
				break;
			case 2:
				System.out.println("/nNew Username: ");
				username = Leer.dato();
				System.out.println("New Password: ");
				password = Leer.dato();
				newUser(username, password);
				System.out.println("Usuario " + username + " Creado!");
				break;
			case 3:
				System.out.println("\nGracias por utilizar Pepecoin!!!\nMuchos PepeSaludos");
				exitPrograma = 1;
			default:
				break;
			}
			
		}
		
	}
	
	public static void newUser(String username, String password) {
		banco.add(new Wallet(username, password));
		
	}
	
	public static void deleteUser(String username, String password) {
		sellPepecoin(getWallet(username), password, getWallet(username).getBalance());
		System.out.println("\nBYE BYE " + username + "-niichan");
		banco.remove(banco.indexOf(getWallet(username)));
	}
	
	public static Wallet getWallet(String username) {
		for(Wallet wallet: banco) {
			if(wallet.getUsername().equals(username)) {
				return wallet;
			}
		}
		return null;
	}
	
	//Creamos Bloque de transaccion
	public static void blockTransaction(Wallet walletA, String password, Wallet walletB, float value) {
		if(walletA.getPassword().equals(password)) {
			Block newBlock = new Block(currentBlock.hash);
			System.out.println("\n" + walletA.getUsername() + " balance is: " + walletA.getBalance());
			System.out.println("\n" + walletA.getUsername() + " esta enviando " + value + " pepeCoins a " + walletB.getUsername() + "...");
			newBlock.addTransaction(walletA.sendFunds(walletB.publicKey, value));
			BlockChain.addBlock(newBlock);
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
		if(walletA.getPassword().equals(password)) {
			Block newBlock = new Block(currentBlock.hash);
			System.out.println("\n" + walletA.getUsername() + " balance is: " + walletA.getBalance());
			System.out.println("\n" + walletA.getUsername() + " cambio " + dollar + "$ en pepeCoins(" + pepecoin + ").");
			try {
				if(newBlock.addTransaction(pepePurse.sendFunds(walletA.publicKey, pepecoin))) {
					BlockChain.addBlock(newBlock);
					System.out.println("\n" + walletA.getUsername() + " balance: " + walletA.getBalance());
					System.out.println(pepePurse.getUsername() + " balance: " + pepePurse.getBalance());
					currentBlock = newBlock;
					walletA.setDollars(walletA.getDollars() - dollar);
				} 	
			} catch (Exception e) {
				System.out.println("Aqui trono.");
			}
		} else {
			System.out.println("Validacion incorrecta. Revise su contraseña.");
		}
		
	}
	
	//Vender pepecoins con dollars
	public static void sellPepecoin(Wallet walletA, String password, float pepecoin) {
		float dollar = (float) (pepecoin / 4.20);
		if(walletA.getPassword().equals(password)) {
			Block newBlock = new Block(currentBlock.hash);
			System.out.println("\n" + walletA.getUsername() + " balance is: " + walletA.getBalance());
			System.out.println("\n" + walletA.getUsername() + " cambio " + pepecoin + " pepecoins a " + dollar + "$.");
			if(newBlock.addTransaction(walletA.sendFunds(pepePurse.publicKey, pepecoin))) {
				BlockChain.addBlock(newBlock);
				System.out.println("\n" + walletA.getUsername() + " balance: " + walletA.getBalance());
				System.out.println(pepePurse.getUsername() + " balance: " + pepePurse.getBalance());
				currentBlock = newBlock;
				walletA.setDollars(walletA.getDollars() + dollar);
			} 
		} else {
			System.out.println("Validacion incorrecta. Revise su contraseña.");
		}	
	}
	
}