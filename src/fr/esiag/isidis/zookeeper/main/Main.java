package fr.esiag.isidis.zookeeper.main;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import fr.esiag.isidis.zookeeper.client.ClientZk;
import fr.esiag.isidis.zookeeper.client.IClientZk;

/**
 * 
 * @author Yassine Diouri
 *
 */
public class Main {

	private static Scanner scan;
	private static boolean exit = false;

	private static String command;
	private static String action;
	private static String option;
	private static String path;
	private static String data;

	private static IClientZk clientZk;
	private static String hostport;

	public static void main(String[] args) throws IOException, InterruptedException {

		if (args.length > 1) {
			System.err.println("Just 1 argument [host:port]");
			System.exit(2);
		} // v�rifier s'il a entrer plus d'un argument en executant par le
			// terminal

		// r�cup�ration du host et port (sinon ceux par d�faut)
		if (args.length == 0)
			hostport = "localhost:2181";
		else
			hostport = args[0];

		// Cr�ation du Scanner pour r�cup�rer les commandes clients
		scan = new Scanner(System.in);

		// connection � Zookeeper
		clientZk = new ClientZk(hostport);
		// Lancement de la console
		System.out.println("\n----------------------------CONSOLE CLIENT ZOOKEEPER----------------------------\n");
		help(); // permet de savoir l'utilisation de la console

		while (!exit) {
			init();
			Thread.sleep(1000);
			System.out.print("enter command > ");
			command = scan.nextLine();
			// permet de traduire la commande saisie sur la console en action +
			// path + data
			readCommand();
			boolean r = false; // pour r�cup�rer les r�sultats de cr�ation + modification + suppression
			byte[] b = null; // pour les r�sultat de lectures

			switch (action) {

			case "create":
				// tester s'il a saisie le path et que le path commence par '/'
				// et qu'il n'existe pas parmis les paths de zookeeper
				if (path != "" && path.substring(0, 1).equals("/") && !clientZk.exists(path)) {
					if (option.equals("") || option.equals("-p")) { // cr�ation de la node en mode persistent (qui permet d'avoir des fils)
						if (data != "") {
							// cr�ation du ZNode
							r = clientZk.createZNode(path, data.getBytes());

							if (r) System.out.println("result command> Cr�ation r�ussie du path : '" + path + "' <= '" + data + "'");
							else System.err.println("result command> ERREUR de cr�ation de path");
						} else {
							// cr�ation du ZNode
							r = clientZk.createZNode(path, null);

							if (r) System.out.println("result command> Cr�ation r�ussie du path : '" + path + "'");
							else System.err.println("result command> ERREUR de cr�ation de path");
						}
					} else if(option.equals("-e")){ // cr�ation de la node en mode ephemere
						if (data != "") {
							// cr�ation du ZNode
							r = clientZk.createZNodeTemp(path, data.getBytes());

							if (r) System.out.println("result command> Cr�ation r�ussie du path : '" + path + "' <= '" + data + "'");
							else System.err.println("result command> ERREUR de cr�ation de path");
						} else {
							// cr�ation du ZNode
							r = clientZk.createZNodeTemp(path, null);

							if (r) System.out.println("result command> Cr�ation r�ussie du path : '" + path + "'");
							else System.err.println("result command> ERREUR de cr�ation de path");
						}
					}
				} else {
					System.err.println("result command> PATH EXISTANT ou non saisie, ou ne commence pas par '/'");
				}
				break;

			case "set":
				// tester s'il a saisie le path et que le path commence par '/'
				// et qu'il existe parmis les paths de zookeeper
				if (path != "" && path.substring(0, 1).equals("/") && clientZk.exists(path)) {
					if (data != "") {
						// modification de la donn�es du ZNode
						r = clientZk.setZNodeData(path, data.getBytes());

						if (r) System.out.println("result command> Modification de la donn�es sur : '" + path + "' <= '" + data + "'");
						else System.err.println("result command> ERREUR de modification de la donn�es du path '" + path + "' ou PATH INEXISANT");
					} else {
						System.err.println("result command> DATA non saisie");
					}
				} else {
					System.err.println("result command> PATH INNEXISTANT ou non saisie, ou ne commence pas par '/'");
				}
				break;

			case "get":
				if (path != "" && path.substring(0, 1).equals("/") && clientZk.exists(path)) {
					// r�cup�ration de la donn�es du ZNode
					b = clientZk.getZNodeData(path);
					if (b != null) {
						System.out.println("result command> path : '" + path + "' <= '" + new String(b) + "'");
					} else {
						System.out.println("result command> path : '" + path + "' <= 'null'");
					}
				} else {
					System.err.println("result command> PATH INNEXISTANT ou non saisie, ou ne commence pas par '/'");
				}
				break;

			case "delete":
				if (path != "" && path.substring(0, 1).equals("/") && clientZk.exists(path)) {
					// suppression du ZNode
					r = clientZk.deleteZNode(path);
					if (r) {
						System.out.println("result command> PATH supprim� avec succ�s");
					} else {
						System.err.println("result command> ERREUR d'acc�s au path : '" + path + "' ou PATH INNEXISTANT");
					}
				} else {
					System.err.println("result command> PATH INNEXISTANT ou non saisie, ou ne commence pas par '/'");
				}
				break;

			case "ls":
				if (path != "" && path.substring(0, 1).equals("/") && clientZk.exists(path)) {
					// suppression du ZNode
					List<String> ls = clientZk.listeChildrenZNode(path);
					if (ls.size() == 0) {
						System.out.println("result command> PATH '" + path + "' ne poss�de pas de fils ou il est ephem�re");
					} else {
						System.out.println("result command> CHILDREN de '" + path + "' : " + afficherList(ls));
					}
				} else {
					System.err.println("result command> PATH INNEXISTANT ou non saisie, ou ne commence pas par '/'");
				}
				break;

			case "help":
				help();
				break;

			case "quit":
				exit();
				System.out.println("EN QUITTANT LA CONSOLE TOUTES VOS DONNEES SERONT SUPPRIMEES");
				break;

			default:
				System.err.println("result command> COMMANDE INEXISTANTE");
			}
		}
	}

	private static String afficherList(List<String> ls) {
		String aff = "[";
		for (int i = 0; i < ls.size(); i++) {
			if (i != 0)
				aff += ", ";
			aff += ls.get(i);
		}
		aff += "]";
		return aff;
	}

	private static void help() {
		System.out.println("/**ACTION ==>");
		System.out.println("/**\tCREATE : cr�er un nouveau ZNode");
		System.out.println("/**\t\t Persistent : <create [-p] /path [data]> (default)");
		System.out.println("/**\t\t Ephemere   : <create [-e] /path [data]>");
		System.out.println("/**\tSET    : modifie la donn�es du ZNode");
		System.out.println("/**\t\t <set /path data>");
		System.out.println("/**\tGET    : r�cup�re la donn�es du ZNode");
		System.out.println("/**\t\t <set /path>");
		System.out.println("/**\tDELETE : supprime le ZNode");
		System.out.println("/**\t\t <delete /path>");
		System.out.println("/**\tLS     : Affiche les fils du ZNode");
		System.out.println("/**\t\t <ls /path>");
		System.out.println("/**\tHELP   : afficher assistant");
		System.out.println("/**\tQUIT   : quitter client ZooKeeper");
	}

	private static void readCommand() {
		String[] cmds = command.trim().split(" ");
		action = cmds[0].toLowerCase();
		
		if(cmds.length == 2) path = cmds[1];
		if(cmds.length == 3) {
			if(cmds[1].substring(0, 1).equals("-")) {
				option = cmds[1];
				path   = cmds[2];
			} else {
				path = cmds[1];
				data = cmds[2];
			}
		}
		if(cmds.length == 4) {
			option = cmds[1];
			path   = cmds[2];
			data   = cmds[3];
		}
	}

	private static void exit() {
		exit = true;
	}

	private static void init() {
		action = "";
		option = "";
		path   = "";
		data   = "";
	}
}
