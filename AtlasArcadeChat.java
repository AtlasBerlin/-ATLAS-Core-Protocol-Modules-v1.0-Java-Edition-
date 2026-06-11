// ====================================================================================
// AtlasArcadeChat
// Sicherer, gebrandeter C64-Retro-Gruppenchat für das Klassen-Inselnetzwerk 
// Von Nicky Leonora 
// ====================================================================================

// ====================================================================================
// EBENE 0 MPORT 
// ====================================================================================

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

// ====================================================================================
// EBENE 1 MASTER KLASSE
// ====================================================================================

public class AtlasArcadeChat 
{
    	private static List<PrintWriter> clientWriters = new ArrayList<>();
    	private static boolean istServer = false;
    	private static JTextPane chatTerminalPane;
    	private static JTextField inputField;
    	private static String lokaleIpFuerChat = "127.0.0.1";

// ====================================================================================
// EBENE 2: MAIN ENTRY & USER INTERFACE
// ====================================================================================

	public static void main(String[] args) 
	{
		starteChatInterface();
	}

	public static void starteChatInterface() 
	{
		java.io.File logoDatei = new java.io.File("atlas_logo.png");
		javax.swing.ImageIcon atlasIcon = logoDatei.exists() ? new javax.swing.ImageIcon("atlas_logo.png") : null;

		String nameEingabe = (String) JOptionPane.showInputDialog(null,
			"Gib deinen Chat-Namen ein:",
			"ATLAS ARCADE CHAT - PROFILE", JOptionPane.QUESTION_MESSAGE,
			atlasIcon, null, "");
		
		if (nameEingabe == null || nameEingabe.trim().isEmpty()) 
		{
			nameEingabe = "Peer_" + (int)(Math.random() * 900 + 100);
		}
		final String chatName = nameEingabe.trim();

		int auswahl = JOptionPane.showConfirmDialog(null,
			"Möchtest du den Chat-SERVER hosten?\n(Nein klicken, um als Client beizutreten)",
			"ATLAS CHAT MODE SELECTION", JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE, atlasIcon);

		String hostIp = "";
		int port = 50099;

		if (auswahl == JOptionPane.YES_OPTION) 
		{
			istServer = true;
			String ipEingabe = (String) JOptionPane.showInputDialog(null,
				"Gib deine lokale IPv4-Adresse ein (aus 'ipconfig' ablesen):",
				"ATLAS CHAT SERVER - IP CONFIG", JOptionPane.QUESTION_MESSAGE,
				atlasIcon, null, "192.168.");
			
			String portEingabe = (String) JOptionPane.showInputDialog(null,
				"Definiere den Server-Port für deine Klassenkameraden:",
				"ATLAS CHAT SERVER - PORT CONFIG", JOptionPane.QUESTION_MESSAGE,
				atlasIcon, null, "50099");

			if (ipEingabe == null || ipEingabe.trim().isEmpty() || portEingabe == null || portEingabe.trim().isEmpty()) return;
			hostIp = ipEingabe.trim();
			lokaleIpFuerChat = hostIp;
			port = Integer.parseInt(portEingabe.trim());
		} 
		else 
		{
			String verbindungEingabe = (String) JOptionPane.showInputDialog(null,
				"Gib [IP-Adresse-Host]:[Port] deines Klassenkameraden ein:",
				"ATLAS CHAT PEER INTERFACE", JOptionPane.QUESTION_MESSAGE,
				atlasIcon, null, "");

			if (verbindungEingabe == null || verbindungEingabe.trim().isEmpty()) return;

			if (verbindungEingabe.contains(":")) 
			{
				String[] teile = verbindungEingabe.split(":");
				hostIp = teile[0].trim();
				port = Integer.parseInt(teile[1].trim());
			} 
			else 
			{
				hostIp = verbindungEingabe.trim();
			}
			
			try 
			{
				lokaleIpFuerChat = InetAddress.getLocalHost().getHostAddress();
			}
			catch (Exception e) 
			{
				lokaleIpFuerChat = "169.254.X.X";
			}   
		}

		JFrame chatFrame = new JFrame("COMMODORE 64 - ATLAS ARCADE NETWORK CHAT");
		chatFrame.setSize(860, 800);
		chatFrame.getContentPane().setBackground(Color.BLACK);
		chatFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		chatTerminalPane = new JTextPane();
		chatTerminalPane.setEditable(false);
		chatTerminalPane.setBackground(Color.BLACK);
		chatTerminalPane.setFont(new Font("Consolas", Font.BOLD, 14));
		chatTerminalPane.setEditorKit(new javax.swing.text.html.HTMLEditorKit());

		JScrollPane scrollPane = new JScrollPane(chatTerminalPane);
		scrollPane.setBorder(null);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

		inputField = new JTextField();
		inputField.setBackground(Color.BLACK);
		inputField.setForeground(Color.GREEN);
		inputField.setCaretColor(Color.GREEN);
		inputField.setFont(new Font("Consolas", Font.BOLD, 14));
		inputField.setBorder(new javax.swing.border.LineBorder(Color.DARK_GRAY, 1));

		chatFrame.add(scrollPane, BorderLayout.CENTER);
		chatFrame.add(inputField, BorderLayout.SOUTH);
		chatFrame.setLocationRelativeTo(null);
		
		chatFrame.setVisible(true);
		inputField.requestFocusInWindow();

		printChatStyled("================================================================================\n", Color.WHITE);
		printChatStyled("                 ATLAS ARCADE CRYPTO CHATROOM PROTOKOLL\n", Color.YELLOW);
		printChatStyled("================================================================================\n", Color.WHITE);
		printChatStyled(" STATUS:       " + (istServer ? "SERVER-HOST ONLINE" : "CLIENT-PEER CONNECTED") + "\n", Color.GREEN);
		printChatStyled(" NUTZERNAME:   " + chatName + "\n", Color.CYAN);
		printChatStyled(" DEINE IP:     " + lokaleIpFuerChat + "\n", Color.CYAN);
		printChatStyled(" PORT-KANAL:   " + port + "\n", Color.CYAN);
		printChatStyled("================================================================================\n\n", Color.WHITE);

		final String finalHostIp = hostIp;
		final int finalPort = port;
		final String finalChatName = chatName;

		new Thread(() -> 
		{
			if (istServer) 
			{
				starteChatServer(finalHostIp, finalPort);
			} 
			else 
			{
				starteChatClient(finalHostIp, finalPort, finalChatName);
			}
		}).start();

// ENDE EBENE 2
	}

// ====================================================================================
// EBENE 3: TEXT-RENDERER & SERVER ENGINE
// ====================================================================================    

	public static void printChatStyled(String text, Color farbe) 
	{
		SwingUtilities.invokeLater(() -> 
		{
			String hex = String.format("#%02x%02x%02x", farbe.getRed(), farbe.getGreen(), farbe.getBlue());
			String html = "<span style='color:" + hex + "; font-family:Consolas; font-weight:bold;'>" + text.replace("\n", "<br>") + "</span>";
			
			try 
			{
				javax.swing.text.html.HTMLDocument doc = (javax.swing.text.html.HTMLDocument) chatTerminalPane.getDocument();
				doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()), html);
			} 
			catch (Exception e) {}
		});
	}

	private static void starteChatServer(String ip, int port) 
	{
		new Thread(() -> 
		{
			try (ServerSocket serverSocket = new ServerSocket(port, 50, InetAddress.getByName(ip))) 
			{
				printChatStyled("[SYSTEM] Server an " + ip + ":" + port + " gebunden. Warte auf Peers...\n", Color.YELLOW);
				
				inputField.addActionListener(e -> 
				{
					String roh = inputField.getText();
					String bereinigt = roh.replaceAll("[^a-zA-Z0-9A-ZäöüÄÖÜß\\s\\.,\\?!\\-]", "");

					if (!bereinigt.trim().isEmpty()) 
					{
						String msg = "[Host | " + lokaleIpFuerChat + "]: " + bereinigt;
						printChatStyled(msg + "\n", Color.GREEN);

						synchronized (clientWriters) 
						{
							for (PrintWriter writer : clientWriters) 
							{ 
								writer.println(msg); 
							}
						}
					}
					inputField.setText("");
				});

				while (true) 
				{
					Socket clientSocket = serverSocket.accept();
					PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
					
					
					synchronized (clientWriters) 
					{ 
						clientWriters.add(writer); 
					}
					new Thread(new ClientHandler(clientSocket)).start();
				}
			} 
			catch (Exception e) 
			{
				printChatStyled("[FEHLER] Socket-Bindung fehlgeschlagen: " + e.getMessage() + "\n", Color.RED);
			}
		}).start();
// ENDE EBENE 3
	}

// ====================================================================================
// EBENE 4: CLIENT ENGINE
// ====================================================================================
    	
	private static void starteChatClient(String ip, int port, String name) 
	{
		try 
		{
			Socket socket = new Socket(ip, port);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

			new Thread(() -> 
			{
				try 
				{
					String eingehend;
					while ((eingehend = in.readLine()) != null) 
					{
						printChatStyled(eingehend + "\n", Color.LIGHT_GRAY);
					}
				} 
				catch (Exception e) 
				{
					printChatStyled("[SYSTEM] Verbindung zum Chat-Server abgebrochen.\n", Color.RED);
				}

			}).start();

			inputField.addActionListener(e -> 
			{
				String roh = inputField.getText();
				String bereinigt = roh.replaceAll("[^a-zA-Z0-9A-ZäöüÄÖÜß\\s\\.,\\?!\\-]", "");

				if (!bereinigt.trim().isEmpty()) 
				{
					out.println("[" + name + " | " + lokaleIpFuerChat + "]: " + bereinigt);
				}
				inputField.setText("");
			});

		} 
		catch (Exception e) 
		{
			printChatStyled("[FEHLER] Verbindung fehlgeschlagen. Host-IP oder Firewall prüfen.\n", Color.RED);
		}
// ENDE EBENE 4
	}

// ====================================================================================
// EBENE 5: BROADCAST CLIENT HANDLER
// ====================================================================================
    
	private static class ClientHandler implements Runnable 
	{
		private Socket socket;
		public ClientHandler(Socket socket) 
		{
			this.socket = socket; 
		}

		public void run() 
		{
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"))) 
			{
				String nachricht;
				while ((nachricht = reader.readLine()) != null) 
				{
					AtlasArcadeChat.printChatStyled(nachricht + "\n", Color.LIGHT_GRAY);
					synchronized (clientWriters) 
					{
						for (PrintWriter writer : clientWriters) 
						{
							writer.println(nachricht);
						}
					}
				}
			} 
			catch (Exception e) {}
		}
	}

// ENDE MASTER KLASSE
}
