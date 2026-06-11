
// =============================================================================
// Projekt C64 RetroCryptoDuel ATLAS Acade Prottokoll Bare Matel Hard Code
// =============================================================================
// dieses Projek wurde komplet in der console und im Texteditor ertelt 
// es beinhatet eine Python Netzwerkbrücke zu Kommunikation im Lan 
// es besteht aus 22 Ebenen davon einen hauptklasse mit 21 Verschachtelungen 
// und beinhaltet eine KI spieler Logik für den CPU Gegener 
// es ist ein Runden Basierendes Karten Strategiespiel  
// das an Layer Eins Blockchains angehnt ist 
// von Nicky Leonora 
// =============================================================================

// =============================================================================
// Ebene 0 IMPORTS
// =============================================================================


import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import javax.swing.text.StyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.Collections; 
import java.util.List;


// ============================================================================
// EBENE 1: CORE ENGINE 
// ============================================================================
// Funktion: Bereitstellung der System-Pakete (AWT, Swing, MIDI, SQL).
// Architektur: Reine Vanilla-Architektur ohne externe Drittanbieter-Frameworks.
// ============================================================================

public class RetroCryptoDuel
{


// ============================================================================
// EBENE 2: REINHEITS-ZÜNDUNG & C64-MAIN-INTERFACE
// ============================================================================
// Funktion: Initialisierung der Fenster-Komponenten und des EDT-Threads.
// Taktung: Startet die hardwarenahe, zyklische Rechts-Links-Laufschrift-Engine.
// ============================================================================
	
	private static int tickerOffset = 0; // RAM-Anker für die horizontale Verschiebung
	private static Thread tickerThread = null; // Der asynchrone Animationstaktgeber

	
	public static void main(String[] args)
	{
		// Initialisiert die SQLite-Tabellen sofort beim App-Start
		initialisiereHighscoreDatenbank();

		JFrame frame = new JFrame("COMMODORE 64 - RETRO CRYPTO DUEL");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// RASTER-SICHERUNG: Großzügiger Rahmen, damit ALLES ohne Nachjustieren reinpasst
		frame.setSize(860, 800); 
		frame.getContentPane().setBackground(Color.BLACK);
		
		terminalPane = new JTextPane();
		terminalPane.setEditable(false);
		terminalPane.setBackground(Color.BLACK);
		terminalPane.setCaretColor(Color.BLACK);
		terminalPane.setFont(new Font("Consolas", Font.BOLD, 14));
		
		// --- DER ULTIMATIVE GRAFIK-FIX: VERBIETET JTEXTPANE JEDEN AUTOMATISCHEN ZEILENUMBRUCH ---
		terminalPane.setEditorKit(new javax.swing.text.html.HTMLEditorKit()); // Erzwingt strikte No-Wrap-Kontrolle
		
		JScrollPane scrollPane = new JScrollPane(terminalPane);
		scrollPane.setBorder(null);
		// Verhindert Scrollbalken-Flackern, erzwingt sauberes C64-Scaling
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		
		frame.add(scrollPane);
		
		initialisiereTastaturRouting(frame);
		
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		// ZÜNDUNG DER LAUFSCHRIFT-ENGINE
		starteKryptoBattleTicker();
		
		// DOZENTEN-FIX: Startet direkt in die Anleitung vor dem Menü
		zeigeAnleitungSpielfeld(); 
		terminalPane.requestFocusInWindow();
	}

	private static void starteKryptoBattleTicker()
	{
		if (tickerThread != null) return; // RAM-Schutz: Verhindert Thread-Klonung
		
		tickerThread = new Thread(() -> 
		{
			try 
			{
				while (imMenue) 
				{
					tickerOffset = (tickerOffset + 1) % 80;
					javax.swing.SwingUtilities.invokeLater(() -> 
					{
						if (imMenue) zeigeStartbildschirm();
					});
					Thread.sleep(120); 
				}
			} 
			catch (Exception e) {}
		});
		tickerThread.setDaemon(true);
		tickerThread.start();
	}

	public static void zeigeStartbildschirm()
	{
		terminalPane.setText(""); 
		Color c64Default = new Color(104, 194, 255);
		
		printStyled("================================================================================\n", Color.WHITE);

		// --- ATLAS DOUBLE-OUTLINE TRIFORCE PYRAMIDE (GEOMETRISCH REIN KALIBRIERT) ---
		printStyled("                                       /\\\n", Color.YELLOW);
		printStyled("                                      //\\\\\n", Color.YELLOW);
		printStyled("                                     //  \\\\\n", Color.YELLOW);
		printStyled("                                    //____\\\\\n", Color.YELLOW);
		printStyled("                                   //\\    /\\\\\n", Color.YELLOW);
		printStyled("                                  //  \\  /  \\\\\n", Color.YELLOW);
		printStyled("                                 //____\\/____\\\\\n", Color.YELLOW);
		printStyled("                                //  A.T.L.A.S \\\\\n", Color.YELLOW);
		printStyled("                               //______________\\\\    ATLAS ARCADE PROTOKOLL\n", Color.YELLOW);
		
		printStyled("================================================================================\n", Color.WHITE);
		
		String basisText = "   *** C R Y P T O   B A T T L E ***   ";
		StringBuilder tickerZeile = new StringBuilder();
		
		int startPos = (80 - tickerOffset) % 80;
		for (int i = 0; i < 80; i++) 
		{
			int zeichenIndex = (i - startPos + 80) % 80;
			if (zeichenIndex < basisText.length()) 
			{
				tickerZeile.append(basisText.charAt(zeichenIndex));
			} 
			else 
			{
				tickerZeile.append(" ");
			}
		}
		tickerZeile.append("\n");
		
		printStyled(tickerZeile.toString(), c64Default);
		printStyled("                            DEMO PROJEKT - IT BQ 146                              \n", c64Default);
		printStyled("================================================================================\n\n", Color.WHITE);
		
		if (aktuellerAuswahlSchritt == 1)
		{
			printStyled(" STEUERUNG: Pfeiltasten HOCH / RUNTER | ENTER ZUM BESTÄTIGEN                    \n", Color.YELLOW);
			printStyled("--------------------------------------------------------------------------------\n", Color.WHITE);
			printStyled(" SCHRITT 1: WÄHLE MATCH MODUS                                                   \n\n", Color.WHITE);
			if (ausgewaehlterModus == 1)
			{
				printStyled("  > [1 SPIELER] VS CPU-CORE TERMINATOR (IQ 130+)                                \n", Color.GREEN);
				printStyled("    [2 SPIELER] VS SPIELER 2 (Schach-Split-Modus)                               \n", Color.LIGHT_GRAY);
				printStyled("    [2 SPIELER ONLINE] P2P NETZWERK BRIDGE (PORT 666)                           \n", Color.LIGHT_GRAY);
			}
			else if (ausgewaehlterModus == 2)
			{
				printStyled("    [1 SPIELER] VS CPU-CORE TERMINATOR (IQ 130+)                                \n", Color.LIGHT_GRAY);
				printStyled("  > [2 SPIELER] VS SPIELER 2 (Schach-Split-Modus)                               \n", Color.GREEN);
				printStyled("    [2 SPIELER ONLINE] P2P NETZWERK BRIDGE (PORT 666)                           \n", Color.LIGHT_GRAY);
			}
			else
			{
				printStyled("    [1 SPIELER] VS CPU-CORE TERMINATOR (IQ 130+)                                \n", Color.LIGHT_GRAY);
				printStyled("    [2 SPIELER] VS SPIELER 2 (Schach-Split-Modus)                               \n", Color.LIGHT_GRAY);
				printStyled("  > [2 SPIELER ONLINE] P2P NETZWERK BRIDGE (PORT 666)                           \n", Color.GREEN);
			}
		}
		else
		{
			printStyled(" STEUERUNG: Pfeiltasten LINKS / RECHTS | ENTER ZUM BESTÄTIGEN                   \n", Color.YELLOW);
			printStyled("--------------------------------------------------------------------------------\n", Color.WHITE);
			printStyled(" SCHRITT 2: WÄHLE CPU SCHWIERIGKEITSGRAD (IQ)                                   \n\n", Color.WHITE);
			
			if (botIQLevel == 1)
			{
				printStyled("  > [ LEICHT (IQ 84) ] <      [ MITTEL (IQ 110) ]        [ HART (IQ 130+) ]     \n", Color.GREEN);
			}
			else if (botIQLevel == 2)
			{
				printStyled("    [ LEICHT (IQ 84) ]      > [ MITTEL (IQ 110) ] <      [ HART (IQ 130+) ]     \n", Color.GREEN);
			}
			else
			{
				printStyled("    [ LEICHT (IQ 84) ]        [ MITTEL (IQ 110) ]      > [ HART (IQ 130+) ] <   \n", Color.GREEN);
			}
			printStyled("\n (Leicht = Reiner Zufalls-Sektor || Hart = Berechnet alle Ticks im Voraus)    \n", Color.LIGHT_GRAY);
		}
		printStyled("\n================================================================================\n", Color.WHITE);
		printStyled(" P1/P2 Global Controls: Pfeiltasten (Select) | SPACE (Lock Asset / Next Turn)   \n", Color.CYAN);
		printStyled("================================================================================\n", Color.WHITE);
	
// Ende Ebene 2
	}

// ============================================================================
// EBENE 3: GLOBALER REGISTER-SPEICHER & RAM-ANKER
// ============================================================================
// Funktion: Zentrale Verwaltung aller State-Variablen, Indizes und Timer.
// Netzwerk: Hält die Sockets-Kanal-Switches und den P2P-Verbindungs-Lock.
// ============================================================================
	
	private static JTextPane terminalPane;
	private static int ausgewaehlterModus = 1; // 1 = vs CPU, 2 = vs Player 2, 3 = P2P Online
	private static int botIQLevel = 2;          // 1 = Easy (84), 2 = Medium (110), 3 = Hard (130+)
	private static int aktuellerAuswahlSchritt = 1; // 1 = Modus waehlen, 2 = IQ waehlen
	
	// DOZENTEN-FIX: Anleitung sperrt initial den Menü-Zugriff
	private static boolean zeigeAnleitung = true;
	private static boolean imMenue = false;
	private static boolean imOutro = false;
	private static boolean imPianoModus = false; // Sperre für den reinen Synth-Modus
	private static boolean soundAktiv = false;
	private static boolean abfrageNeustart = false; // Steuert die Y/N-Weiche am Ende
	
	private static Spieler spieler1;
	private static Spieler spieler2;
	
	// DOZENTEN-FIX: Der dynamische, abgesicherte RAM-Anker für deinen Namen
	private static String spielerNameP1 = "NICKY";
	
	private static int p1Index = 0;
	private static int p2Index = 0;
	
	private static Karte aktiveKarteP1 = null;
	private static Karte aktiveKarteP2 = null;
	private static boolean p1Bereit = false;
	private static boolean p2Bereit = false;
	private static boolean kampfLaeuft = false;
	
	private static int wobbleOffset = 0; 
	private static int spinZustand = 0;  

	// --- LOGISCHE SCHACH-UHR, ENGINE-SWITCHES & ARCADE-SCORES ---
	private static int aktivePartei = 1;         // 1 = Nicky/P1 am Zug, 2 = CPU/P2 am Zug
	private static boolean cpuHatInitiative = false; // Steuert den defensiven/offensiven KI-Vektor
	private static long zugStartZeit = 0;        // RAM-Anker für den System-Oszillator
	private static double p1GesamtZeit = 0.0;    // Akkumulierte Bedenkzeit für Spieler 1 (Sekunden)
	private static double p2GesamtZeit = 0.0;    // Akkumulierte Bedenkzeit für Spieler 2/CPU (Sekunden)
	private static int p1Score = 0;              // 90/10 Performance-Punkte Spieler 1
	private static int p2Score = 0;              // 90/10 Performance-Punkte Spieler 2 / CPU
	
	// P2P-NETZWERK BRIDGE SPEICHER-ANKER & LOCK-LATCH
	private static java.io.PrintWriter networkWriter = null;
	private static boolean netzwerkAktiv = false;
	private static boolean netzwerkWartetAufPartner = false; // SPERR-SCHLOSS FÜR HANDSHAKE
	
	// SYNTHESIZER-DEBOUNCE: Verriegelt gedrückte Tasten gegen RAM-Überlastung
	private static boolean[] synthTasteAktiv = new boolean[256];
	
// Ende Ebene 3


// ============================================================================
// EBENE 4: TEXT-PANEL TERMINAL INTERFACE-TREIBER
// ============================================================================
// Funktion: Kapselung des JTextPane-Zugriffs für das C64-Retro-Feeling.
// Logik: Steuert die Caret-Fixierung und erzwingt den Fokus auf die Tastatur.
// ============================================================================

	//  KARTEN KLASSE (DOUBLE-LINE WHITE CARD EDITION)
	static class Karte
	{
		String name;
		String ticker;
		double leben;
		double schlagkraft;
		double blockZeit;
		Color kartenFarbe;

		public Karte(String name, String ticker, double leben, double schlagkraft, double blockZeit, Color kartenFarbe)
		{
			this.name = name;
			this.ticker = ticker;
			this.leben = leben;
			this.schlagkraft = schlagkraft;
			this.blockZeit = blockZeit;
			this.kartenFarbe = kartenFarbe;
		}
		
		public Karte kopie()
		{
			return new Karte(this.name, this.ticker, this.leben, this.schlagkraft, this.blockZeit, this.kartenFarbe);  
		}
		
		// Doppelwandiges C64-Quartett-Layout (7 Zeilen tief)
		public String[] getFetteC64Grafik()
		{
			String[] lines = new String[7];
			lines[0] = "╔════════════╗";
			lines[1] = String.format("║ %-10s ║", ticker);
			lines[2] = String.format("║ HP:%-7.0f ║", leben);
			lines[3] = String.format("║ ATK:%-6.0f ║", schlagkraft);
			lines[4] = String.format("║ B:%-7.2f ║", blockZeit); // UPGRADE: 2 Nachkommastellen fest gelockt!
			lines[5] = "║            ║";
			lines[6] = "╚════════════╝";
			return lines;
		}
// Ende Ebene 4
	}

// ============================================================================
// EBENE 5: STYLED STREAMS (FARB-OSZILLATOR FÜR SCHRIFT & BG)
// ============================================================================
// Funktion: Zeichnet Text mit dedizierten Farb-Attributen (ANSI-Simulation).
// Ergänzung: Erlaubt harte Hintergrund-Inversionen für Kampfeffekte.
// ============================================================================


	// SPIELER KLASSE
	static class Spieler
	{
		String name;
		List<Karte> hauptDeck = new ArrayList<>(); 
		List<Karte> handKarten = new ArrayList<>(); 
		List<Karte> ablageStapel = new ArrayList<>(); 

		public Spieler(String name)
		{
			this.name = name;
		}
		
		public void handAuffuellen()
		{
			while (handKarten.size() < 5 && !hauptDeck.isEmpty())
			{
				handKarten.add(hauptDeck.remove(0));
			}
		}
// Ende Ebene 5
	} 

// ============================================================================
// EBENE 6: RETRO SOUND ENGINE (8-BIT HARDWARE MIDI-CHANNELS)
// ============================================================================
// Funktion: Paralleler Audio-Thread auf MIDI-Kanal 0, Program 81 (Square Lead).
// Fail-Safe: Thread-Cleaning bricht alte Oszillatoren bei Reset sofort ab.
// ============================================================================


	//  INTERNAL MIDI SOUND ENGINE (Realer 8-Bit Hardware Sound - Multi-Thread Safe)
	static class RetroSoundEngine
	{
		public static void starteHintergrundSound()
		{
			// RAM-REINIGUNG: Falls bereits ein Sound läuft, erst hart terminieren
			if (soundAktiv) 
			{
				soundAktiv = false;
				try 
				{ 
					Thread.sleep(150); 
				} 
				catch (Exception e) 
				{} // Wartezeit für sauberes Close
			}

			Thread soundThread = new Thread(() -> 
			{
				try
				{
					Synthesizer synth = MidiSystem.getSynthesizer();
					if (!synth.isOpen()) 
					{
						synth.open(); 
					}
					
					Soundbank defaultBank = synth.getDefaultSoundbank();
					if (defaultBank != null) 
					{
						synth.loadAllInstruments(defaultBank);
					}

					MidiChannel[] channels = synth.getChannels();
					if (channels == null || channels.length == 0) 
					{
						soundAktiv = false;
						return;
					}
					
					channels[0].programChange(81); 
					soundAktiv = true;

					// EPISCHE 64-NOTEN-SYNTHESIZER-KASKADE
					int[] melodie = 
					{
						67, 67, 72, 74, 75, 74, 72, 67, 67, 67, 72, 74, 75, 74, 72, 67,
						65, 65, 70, 72, 74, 72, 70, 65, 67, 67, 72, 74, 75, 74, 72, 67,
						67, 67, 72, 74, 75, 74, 72, 67, 67, 67, 72, 74, 75, 74, 72, 67,
						68, 68, 72, 74, 76, 74, 72, 68, 70, 70, 74, 76, 77, 76, 74, 70
					}; 
					
					int[] timing = 
					{
						300, 150, 150, 150, 300, 150, 150, 450, 300, 150, 150, 150, 300, 150, 150, 450,
						300, 150, 150, 150, 300, 150, 150, 450, 300, 150, 150, 150, 300, 150, 150, 450,
						300, 150, 150, 150, 300, 150, 150, 450, 300, 150, 150, 150, 300, 150, 150, 450,
						300, 150, 150, 150, 300, 150, 150, 450, 300, 150, 150, 150, 300, 150, 150, 450
					};

					int index = 0;

					while (soundAktiv && !imOutro)
					{
						int note = melodie[index];
						channels[0].noteOn(note, 85); 
						
						Thread.sleep(timing[index]); 
						
						channels[0].noteOff(note);    
						
						index = (index + 1) % melodie.length;
					}
					synth.close();
				}
				catch (Exception e)
				{
					soundAktiv = false;
				}
			});
			soundThread.setDaemon(true);
			soundThread.start();
		}

		public static void spieleNavigationsKlack()
		{
			try
			{
				Synthesizer synth = MidiSystem.getSynthesizer();
				if (!synth.isOpen()) synth.open();
				MidiChannel[] channels = synth.getChannels();
				if (channels != null && channels.length > 9)
				{
					channels[9].noteOn(39, 90); 
					new Thread(() -> 
					{
						try { Thread.sleep(30); channels[9].noteOff(39); } catch (Exception e) {}
					}).start();
				}
			}
			catch (Exception e) {}
		}

		public static void spieleAktionsPiep()
		{
			try
			{
				Synthesizer synth = MidiSystem.getSynthesizer();
				if (!synth.isOpen()) synth.open();
				MidiChannel[] channels = synth.getChannels();
				if (channels != null && channels.length > 0)
				{
					channels[0].noteOn(72, 85); 
					new Thread(() -> {
						try { Thread.sleep(50); channels[0].noteOff(72); } catch (Exception e) {}
					}).start();
				}
			}
			catch (Exception e) {}
		}

		public static void spieleRundenSiegDiDi()
		{
			try
			{
				Synthesizer synth = MidiSystem.getSynthesizer();
				if (!synth.isOpen()) synth.open();
				MidiChannel[] channels = synth.getChannels();
				if (channels != null && channels.length > 0)
				{
					channels[0].programChange(81);
					channels[0].noteOn(72, 90); Thread.sleep(80); channels[0].noteOff(72);
					channels[0].noteOn(76, 95); Thread.sleep(200); channels[0].noteOff(76);
				}
			}
			catch (Exception e) {}
		}

		public static void spieleRundenNiederlageMoep()
		{
			try
			{
				Synthesizer synth = MidiSystem.getSynthesizer();
				if (!synth.isOpen()) synth.open();
				MidiChannel[] channels = synth.getChannels();
				if (channels != null && channels.length > 0)
				{
					channels[0].programChange(81);
					channels[0].noteOn(48, 85); channels[0].noteOn(49, 85); 
					Thread.sleep(400);
					channels[0].noteOff(48); channels[0].noteOff(49);
				}
			}
			catch (Exception e) {}
		}
// Ende Ebene 6
	}

// ============================================================================
// EBENE 7: DATENSTRUKTUR "SPIELER" & DECK-REGISTER
// ============================================================================
// Funktion: Verwaltung von Handkarten, Hauptdeck und gewonnenen Ablagestapeln.
// Logik: Automatische Auffüll-Routine rückt Karten bündig ins Array nach.
// ============================================================================


	public static void printStyled(String text, Color fgColor)
	{
		StyledDocument doc = terminalPane.getStyledDocument();
		Style style = terminalPane.addStyle("ColorStyle", null);
		StyleConstants.setForeground(style, fgColor);
		try
		{
			doc.insertString(doc.getLength(), text, style);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// NEUER COGNITIVE ANKER: Punktgenauer Zeichen-Hintergrund ohne GUI-Crash
	public static void printStyledWithBg(String text, Color fgColor, Color bgColor)
	{
		StyledDocument doc = terminalPane.getStyledDocument();
		Style style = terminalPane.addStyle("BgColorStyle", null);
		StyleConstants.setForeground(style, fgColor);
		StyleConstants.setBackground(style, bgColor); // Maskiert nur die Zeichen!
		try
		{
			doc.insertString(doc.getLength(), text, style);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

// Ende Ebene 7
	}

// ============================================================================
// EBENE 8: STATISCHES MENÜ-RENDERING (DYNAMIC THREE-WAY MODUS SELECTION)
// ============================================================================
// Funktion: Zeichnet die Pyramide in doppelter Outline und innerem Triforce.
// UI-Weiche: Dynamischer Umschalter für 1-Spieler, PvP-Split und P2P-Online.
// ============================================================================


	public static void zeigeMenue()
	{
		terminalPane.setText(""); 
		Color c64Default = new Color(104, 194, 255);
		
		printStyled("================================================================================\n", Color.WHITE);
		
		// --- ATLAS DOUBLE-OUTLINE TRIFORCE PYRAMIDE (GEOMETRISCH REIN KALIBRIERT) ---
		printStyled("                                       /\\\n", Color.YELLOW);
		printStyled("                                      //\\\\\n", Color.YELLOW);
		printStyled("                                     //  \\\\\n", Color.YELLOW);
		printStyled("                                    //____\\\\\n", Color.YELLOW);
		printStyled("                                   //\\    /\\\\\n", Color.YELLOW);
		printStyled("                                  //  \\  /  \\\\\n", Color.YELLOW);
		printStyled("                                 //____\\/____\\\\\n", Color.YELLOW);
		printStyled("                                //  A.T.L.A.S \\\\\n", Color.YELLOW);
		printStyled("                               //______________\\\\    ATLAS ARCADE PROTOKOLL\n", Color.YELLOW);
		
		printStyled("================================================================================\n", Color.WHITE);
		printStyled("                                                                                 \n", c64Default);
		printStyled("                          DEMO PROJEKT - IT BQ 146                              \n", c64Default);
		printStyled("================================================================================\n\n", Color.WHITE);
		
		if (aktuellerAuswahlSchritt == 1)
		{
			printStyled(" STEUERUNG: Pfeiltasten HOCH / RUNTER | ENTER ZUM BESTÄTIGEN                    \n", Color.YELLOW);
			printStyled("--------------------------------------------------------------------------------\n", Color.WHITE);
			printStyled(" SCHRITT 1: WÄHLE MATCH MODUS                                                   \n\n", Color.WHITE);
			if (ausgewaehlterModus == 1)
			{
				printStyled("  > [1 SPIELER] VS CPU-CORE TERMINATOR (IQ 130+)                                \n", Color.GREEN);
				printStyled("    [2 SPIELER] VS SPIELER 2 (Schach-Split-Modus)                               \n", Color.LIGHT_GRAY);
				printStyled("    [2 SPIELER ONLINE] P2P NETZWERK BRIDGE (PORT 666)                           \n", Color.LIGHT_GRAY);
			}
			else if (ausgewaehlterModus == 2)
			{
				printStyled("    [1 SPIELER] VS CPU-CORE TERMINATOR (IQ 130+)                                \n", Color.LIGHT_GRAY);
				printStyled("  > [2 SPIELER] VS SPIELER 2 (Schach-Split-Modus)                               \n", Color.GREEN);
				printStyled("    [2 SPIELER ONLINE] P2P NETZWERK BRIDGE (PORT 666)                           \n", Color.LIGHT_GRAY);
			}
			else
			{
				printStyled("    [1 SPIELER] VS CPU-CORE TERMINATOR (IQ 130+)                                \n", Color.LIGHT_GRAY);
				printStyled("    [2 SPIELER] VS SPIELER 2 (Schach-Split-Modus)                               \n", Color.LIGHT_GRAY);
				printStyled("  > [2 SPIELER ONLINE] P2P NETZWERK BRIDGE (PORT 666)                           \n", Color.GREEN);
			}
		}
		else
		{
			printStyled(" STEUERUNG: Pfeiltasten LINKS / RECHTS | ENTER ZUM BESTÄTIGEN                   \n", Color.YELLOW);
			printStyled("--------------------------------------------------------------------------------\n", Color.WHITE);
			printStyled(" SCHRITT 2: WÄHLE CPU SCHWIERIGKEITSGRAD (IQ)                                   \n\n", Color.WHITE);
			
			if (botIQLevel == 1)
			{
				printStyled("  > [ LEICHT (IQ 84) ] <      [ MITTEL (IQ 110) ]        [ HART (IQ 130+) ]     \n", Color.GREEN);
			}
			else if (botIQLevel == 2)
			{
				printStyled("    [ LEICHT (IQ 84) ]      > [ MITTEL (IQ 110) ] <      [ HART (IQ 130+) ]     \n", Color.GREEN);
			}
			else
			{
				printStyled("    [ LEICHT (IQ 84) ]        [ MITTEL (IQ 110) ]      > [ HART (IQ 130+) ] <   \n", Color.GREEN);
			}
			printStyled("\n (Leicht = Reiner Zufalls-Sektor || Hart = Berechnet alle Ticks im Voraus)    \n", Color.LIGHT_GRAY);
		}
		printStyled("\n================================================================================\n", Color.WHITE);
		printStyled(" P1/P2 Global Controls: Pfeiltasten (Select) | SPACE (Lock Asset / Next Turn)   \n", Color.CYAN);
		printStyled("================================================================================\n", Color.WHITE);

// Ende Ebene 8
	}


// ============================================================================
// EBENE 9: MENÜ-REST (FOOTER STEUERUNGS-INDIKATOREN)
// ============================================================================
// Funktion: Ausgabe des dynamischen Steuerungs-Layouts je nach Match-Modus.
// Design: C64-konformes Farbschema (Weiß/Gelb/Cyan) im 80-Zeichen-Layout.
// ============================================================================


	// Fuss Zeile 
	private static void zeigeMenueRest(Color c64Default)
	{
		printStyled("                               PRÄSENTATION - IT BQ 146                   \n", c64Default);
		printStyled("=====================================================================================\n\n", Color.WHITE);
		
		if (aktuellerAuswahlSchritt == 1)
		{
			printStyled(" STEUERUNG: Pfeiltasten HOCH/RUNTER | ENTER ZUM BESTAETIGEN\n", Color.YELLOW);
			printStyled("----------------------------------------------------------------------\n\n", Color.WHITE);
			printStyled(" SCHRITT 1: WAEHLE MATCH MODUS\n\n", Color.WHITE);
			if (ausgewaehlterModus == 1)
			{
				printStyled(" > [1 SPIELER] VS CPU  (Simulation)\n", Color.GREEN);
				printStyled("   [2 SPIELER] VS SPIELER 2 (Split-Tastatur)\n", Color.GRAY);
				printStyled("   [2 SPIELER ONLINE] P2P NETZWERK BRIDGE (PORT 666)\n", Color.GRAY);
			}
			else if (ausgewaehlterModus == 2)
			{
				printStyled("   [1 SPIELER] VS CPU  (Simulation)\n", Color.GRAY);
				printStyled(" > [2 SPIELER] VS SPIELER 2 (Split-Tastatur)\n", Color.GREEN);
				printStyled("   [2 SPIELER ONLINE] P2P NETZWERK BRIDGE (PORT 666)\n", Color.GRAY);
			}
			else
			{
				printStyled("   [1 SPIELER] VS CPU  (Simulation)\n", Color.GRAY);
				printStyled("   [2 SPIELER] VS SPIELER 2 (Split-Tastatur)\n", Color.GRAY);
				printStyled(" > [2 SPIELER ONLINE] P2P NETZWERK BRIDGE (PORT 666)\n", Color.GREEN);
			}
		}
		else
		{
			printStyled(" STEUERUNG: Pfeiltasten LINKS/RECHTS | ENTER ZUM BESTAETIGEN\n", Color.YELLOW);
			printStyled("----------------------------------------------------------------------\n\n", Color.WHITE);
			printStyled(" SCHRITT 2: WAEHLE CPU SCHWIERIGKEITSGRAD (IQ)\n\n", Color.WHITE);
			
			if (botIQLevel == 1)
			{
				printStyled(" > [ LEICHT (IQ 84) ] <   [ MITTEL (IQ 110) ]     [ HART (IQ 130+) ]", Color.GREEN);
			}
			else if (botIQLevel == 2)
			{
				printStyled("   [ LEICHT (IQ 84) ]   > [ MITTEL (IQ 110) ] <   [ HARD (IQ 130+) ]", Color.GREEN);
			}
			else
			{
				printStyled("   [ LEICHT (IQ 84) ]     [ MITTEL (IQ 110) ]   > [ HARD (IQ 130+) ] <", Color.GREEN);
			}
			printStyled("\n\n (Leicht = Just for Fun | Hart = Berechnet alle Vektoren voraus!)", Color.GRAY);
		}
		printStyled("\n\n====================================================================================\n", Color.WHITE);
		printStyled(" P1 Controls: A/D (Select) | SPACE (Drop Card in Arena)\n", Color.CYAN);
		printStyled(" P2 Controls: Arrow Keys (Select) | ENTER (Drop Card in Arena)\n", Color.LIGHT_GRAY);
		printStyled("====================================================================================\n", Color.WHITE);
	
// Ende Ebene 9
	}

// ============================================================================
// EBENE 10: MATCH-KOMPONENTEN-INITIALISIERUNG & DECK-BALANCING
// ============================================================================
// Funktion: Lädt 40 Krypto-Themenkarten (Kategorie A-D) mit 2026er Hard-Floor.
// Taktung: Symmetrisches Spiegel-Verfahren eliminiert den Glücksfaktor zu 100%.
// ============================================================================

	// Initialisiert Decks mit allen 40 Krypto-Themenkarten (Hard-Floor Balancing 2026)
	public static void initialisiereMatchKomponenten()
	{
		// DOZENTEN-FIX: Zieht den dynamischen Namen und nutzt kurze Gegnerbezeichner für das C64-Grid
		spieler1 = new Spieler(spielerNameP1);
		spieler2 = new Spieler(ausgewaehlterModus == 1 ? "CPU" : "P2");

		List<Karte> kartenPool = new ArrayList<>();
		
		//1.1 KATEGORIE A: L1 GIGANTEN (MASSIVE TANKS - TRÄGE SCHWERGEWICHTE)
		kartenPool.add(new Karte("Bitcoin", "BTC", 9999.0, 99.0, 133.00, new Color(247, 147, 26)));     
		kartenPool.add(new Karte("Ethereum", "ETH", 3333.0, 92.0, 99.00, new Color(140, 124, 240)));    
		kartenPool.add(new Karte("BNB Chain", "BNB", 1111.0, 80.0, 30.00, new Color(243, 186, 47)));       
		kartenPool.add(new Karte("Cardano", "ADA", 999.0, 85.0, 20.00, new Color(34, 100, 240)));       
		kartenPool.add(new Karte("Ripple", "XRP", 1111.0, 78.0, 4.00, new Color(35, 125, 200)));         
		kartenPool.add(new Karte("Solana", "SOL", 2222.0, 75.0, 0.11, new Color(20, 241, 149))); // HARD-FLOOR SAFETY RIEGEL        
		kartenPool.add(new Karte("The Open Network", "TON", 677.0, 77.00, 0.09, new Color(0, 152, 234))); 
		kartenPool.add(new Karte("Avalanche", "AVAX", 333.0, 78.0, 1.00, new Color(232, 65, 66)));
		kartenPool.add(new Karte("Polkadot", "DOT", 555.0, 82.0, 6.00, new Color(230, 0, 122)));
		kartenPool.add(new Karte("Tron", "TRX", 420.0, 70.0, 3.00, new Color(255, 0, 41)));

		//1.2 KATEGORIE B: HIGH-PERFORMANCE L1S (BLITZSCHNELLE CHIPS)
		kartenPool.add(new Karte("Aptos", "APT", 333.0, 68.0, 0.10, new Color(255, 100, 100)));         
		kartenPool.add(new Karte("Sui", "SUI", 333.0, 70.0, 0.09, new Color(109, 186, 219)));           
		kartenPool.add(new Karte("Near Protocol", "NEAR", 888.0, 68.0, 1.10, new Color(15, 15, 15)));
		kartenPool.add(new Karte("Sei Network", "SEI", 222.0, 64.0, 0.10, new Color(153, 0, 0)));
		kartenPool.add(new Karte("Injective", "INJ", 222.0, 72.0, 0.12, new Color(0, 163, 255)));
		kartenPool.add(new Karte("Fantom", "FTM", 222.0, 65.0, 0.40, new Color(25, 118, 210)));
		kartenPool.add(new Karte("Hedera", "HBAR", 150.0, 72.0, 2.50, new Color(128, 128, 128)));
		kartenPool.add(new Karte("Algorand", "ALGO", 142.0, 76.0, 2.80, new Color(30, 30, 30)));
		kartenPool.add(new Karte("MultiversX", "EGLD", 111.0, 74.0, 6.00, new Color(30, 215, 96)));
		kartenPool.add(new Karte("Kadena", "KDA", 111.0, 75.0, 30.00, new Color(237, 20, 61)));

		// 1.3 KATEGORIE C: ETHEREUM LAYER 2S (SKALIERUNGS-ASSE - EXTREMER DURCHSATZ)
		kartenPool.add(new Karte("Arbitrum One", "ARB", 440.0, 82.0, 0.09, new Color(40, 160, 240)));
		kartenPool.add(new Karte("Optimism", "OP", 222.0, 80.0, 0.12, new Color(255, 4, 32)));
		kartenPool.add(new Karte("Base", "BASE", 1777.0, 85.0, 0.13, new Color(0, 82, 255)));            
		kartenPool.add(new Karte("Polygon", "POL", 222.0, 75.0, 0.15, new Color(130, 71, 229)));
		kartenPool.add(new Karte("Starknet", "STRK", 199.0, 70.0, 0.20, new Color(1, 26, 71)));
		kartenPool.add(new Karte("zkSync Era", "ZK", 188.0, 72.0, 0.10, new Color(240, 240, 240)));
		kartenPool.add(new Karte("Manta Pacific", "MANTA", 166.0, 60.0, 0.40, new Color(43, 142, 242)));
		kartenPool.add(new Karte("Linea", "LINEA", 188.0, 74.0, 0.15, new Color(40, 40, 40)));
		kartenPool.add(new Karte("Scroll", "SCROLL", 188.0, 78.0, 0.12, new Color(255, 222, 173)));
		kartenPool.add(new Karte("Blast", "BLAST", 202.0, 68.0, 0.11, new Color(252, 252, 3)));

		// 1.4 KATEGORIE D: COSMOS, ALT-CHAINS & EXOTEN (UNBERECHENBARE BASTIONEN)
		kartenPool.add(new Karte("Cosmos Hub", "ATOM", 269.0, 75.0, 5.00, new Color(46, 49, 71)));
		kartenPool.add(new Karte("Osmosis", "OSMO", 177.0, 58.0, 6.00, new Color(147, 51, 234)));
		kartenPool.add(new Karte("Celestia", "TIA", 211.0, 70.0, 12.00, new Color(125, 60, 255)));
		kartenPool.add(new Karte("Monero", "XMR", 2222.0, 90.0, 111.00, new Color(243, 91, 33))); 
		kartenPool.add(new Karte("Litecoin", "LTC", 3333.0, 82.0, 122.00, new Color(52, 93, 157))); 
		kartenPool.add(new Karte("Stellar", "XLM", 299.0, 72.0, 5.00, new Color(20, 20, 20)));
		kartenPool.add(new Karte("Kaspa", "KAS", 225.0, 70.0, 0.11, new Color(112, 224, 201))); 
		kartenPool.add(new Karte("Cronos", "CRO", 199.0, 68.0, 3.00, new Color(0, 45, 111)));
		kartenPool.add(new Karte("Filecoin", "FIL", 246.0, 75.0, 30.00, new Color(66, 197, 245)));
		kartenPool.add(new Karte("Internet Computer", "ICP", 377.0, 76.0, 1.00, new Color(241, 90, 36)));

		// --- STRATEGISCHES SPIEGEL-VERFAHREN (0% ZUFALLS-BIAS BEIM DECK-BALANCING) ---
		kartenPool.sort((k1, k2) -> 
		{
			double s1 = k1.schlagkraft + (k1.leben / 100.0) - (k1.blockZeit * 10.0);
			double s2 = k2.schlagkraft + (k2.leben / 100.0) - (k2.blockZeit * 10.0);
			return Double.compare(s2, s1);
		});

		for (int i = 0; i < kartenPool.size(); i += 2)
		{
			if (i + 1 < kartenPool.size())
			{
				spieler1.hauptDeck.add(kartenPool.get(i).kopie());
				spieler2.hauptDeck.add(kartenPool.get(i + 1).kopie());
			}
		}

		Collections.shuffle(spieler1.hauptDeck);
		Collections.shuffle(spieler2.hauptDeck);

		spieler1.handAuffuellen();
		spieler2.handAuffuellen();
		
		p1Index = 0; p2Index = 0;
		aktiveKarteP1 = null; aktiveKarteP2 = null;
		p1Bereit = false; p2Bereit = false;
		kampfLaeuft = false;
		aktivePartei = 1;
		cpuHatInitiative = false;
		p1GesamtZeit = 0.0; p2GesamtZeit = 0.0;
		p1Score = 0; p2Score = 0;
	
// Ende Ebene 10
	}


// ============================================================================
// EBENE 11: KI-LOGIK (DUAL-VEKTOR-ENGINE "SMART TERMINATOR")
// ============================================================================
// Funktion: IQ 84 (Zufalls-Riegel) vs. IQ 110/130+ (Prädiktive RAM-Simulation).
// Analyse: Berechnet Tick-Raten und Schlagschaden-Vektoren im Voraus.
// ============================================================================

	// KI-LOGIK: DUAL-VEKTOR-ENGINE (SMART-TERMINATOR MIT EASY-ZUFALLS-RIEGEL)
	private static int berechneCpuKartenZug() 
	{
		List<Karte> cpuHand = spieler2.handKarten;
		if (cpuHand.isEmpty()) 
		{
			return 0;
		}

		// --- LEVEL 1: ABSOLUTER EASY-MODUS (IQ 84) ---
		// HÄRTESTER ZUFALLS-RIEGEL: Schaltet jede mathematische Berechnung sofort ab!
		if (botIQLevel <= 1) 
		{
			// CPU nimmt blind eine Karte von der Hand zum reinen Durchklicken
			return (int) (Math.random() * cpuHand.size());
		}

		// --- COGNITIVE ANCHOR: VERTEIDIGUNGS-VEKTOR (Nickys Karte steht im Ring) ---
		if (aktiveKarteP1 != null)
		{
			int besterKonterIndex = 0;
			double hoechsterSimulationsWert = -999999.0;

			// Der Terminator scannt jede Handkarte und simuliert die Ticks im RAM
			for (int i = 0; i < cpuHand.size(); i++) 
			{
				Karte testCpu = cpuHand.get(i).kopie();
				Karte testNicky = aktiveKarteP1.kopie();

				double ticksCpu = Math.max(1.0, (testNicky.blockZeit / Math.max(0.01, testCpu.blockZeit)) * 4.5);
				double ticksNicky = Math.max(1.0, (testCpu.blockZeit / Math.max(0.01, testNicky.blockZeit)) * 4.5);

				while (testNicky.leben > 0 && testCpu.leben > 0)
				{
					int schlaegeCpu = Math.min(35, Math.max(1, (int)ticksCpu));
					for (int s = 0; s < schlaegeCpu && testNicky.leben > 0; s++) 
					{
						double dmg = Math.max(1.5, testCpu.schlagkraft - (testNicky.schlagkraft / 10.0)) / 4.0;
						testNicky.leben -= dmg;
					}
					
					if (testNicky.leben <= 0) 
					{
						break;
					}

					int schlaegeNicky = Math.min(35, Math.max(1, (int)ticksNicky));
					for (int s = 0; s < schlaegeNicky && testCpu.leben > 0; s++) 
					{
						double cpuBonus = (botIQLevel == 3) ? 1.15 : 1.0;
						double dmg = (Math.max(1.5, testNicky.schlagkraft - (testCpu.schlagkraft / 10.0)) / 4.0) * cpuBonus;
						testCpu.leben -= dmg;
					}
				}

				// --- STRATEGISCHE AUSWERTUNG DER ENGINE (NUR AB LEVEL 2) ---
				double simulationsWert = 0;
				
				if (testNicky.leben <= 0 && testCpu.leben > 0) 
				{
					simulationsWert = 100000.0 + testCpu.leben;
				} 
				else 
				{
					if (botIQLevel == 3) 
					{
						// Hardcore-Terminator schont dicke Tanks bei Niederlage (Opfer-Protokoll)
						simulationsWert = -testCpu.leben; 
					} 
					else 
					{
						// Medium-Modus maxet einfach stumpf den Schaden vor dem Tod
						simulationsWert = (aktiveKarteP1.leben - testNicky.leben);
					}
				}

				if (simulationsWert > hoechsterSimulationsWert) 
				{
					hoechsterSimulationsWert = simulationsWert;
					besterKonterIndex = i;
				}
			}
			return besterKonterIndex;
		}

		// --- COGNITIVE ANCHOR: ANGRIFFS-VEKTOR (CPU hat Initiative / Ring ist leer) ---
		else
		{
			int besterAngriffIndex = 0;
			double besterDruckQuotient = -1.0;

			// Der Mittlere/Harte Terminator eröffnet strategisch aggressiv
			for (int i = 0; i < cpuHand.size(); i++) 
			{
				Karte c = cpuHand.get(i);
				double druck = (c.schlagkraft * 2.0 + c.leben) / Math.max(0.1, c.blockZeit);
				
				if (druck > besterDruckQuotient) 
				{
					besterDruckQuotient = druck;
					besterAngriffIndex = i;
				}
			}
			return besterAngriffIndex;
		}
	
// Ende Ebene 11
	}

// ============================================================================
// EBENE 12: DYNAMISCHE SPIELFELD-VISUALISIERUNG & DECK-METRICS GRID ENGINE
// ============================================================================
// Funktion: Echtzeit-Anzeige der Schachuhr, Lebensbalken und Rundenscores.
// Logik: Gestauchte Zeichenketten eliminieren Zeilenüberläufe und Ecken-Verschiebung.
// ============================================================================
	

	public static void aktualisiereSpielfeldVisualisierung()
	{
		// --- COGNITIVE ANCHOR: NETZWERK LOCK DISPENSARY ---
		if (ausgewaehlterModus == 3 && netzwerkWartetAufPartner) 
		{
			terminalPane.setText("");
			Color c64Default = new Color(104, 194, 255);
			Color feldGrau = new Color(60, 60, 60);
			
			for(int i = 0; i < 5; i++) printStyled("\n", Color.BLACK);
			
			printStyledWithBg("┌──────────────────────────────────────────────────────────────────────────────┐\n", Color.WHITE, feldGrau);
			printStyledWithBg("│                     *** ATLAS P2P BRIDGE ACTIVE ***                          │\n", Color.YELLOW, feldGrau);
			printStyledWithBg("├──────────────────────────────────────────────────────────────────────────────┤\n", Color.WHITE, feldGrau);
			printStyledWithBg("│                                                                              │\n", Color.WHITE, feldGrau);
			if (networkWriter == null) {
				printStyledWithBg("│            INITIALISIERE LOKALE NETZWERK-SCHNITTSTELLE...                    │\n", c64Default, feldGrau);
			} else {
				printStyledWithBg("│            WARTE AUF HANDSHAKE VON SPIELER 2...                              │\n", Color.GREEN, feldGrau);
			}
			printStyledWithBg("│                                                                              │\n", Color.WHITE, feldGrau);
			printStyledWithBg("│            [ESC] ABBRECHEN & ZURÜCK ZUM HAUPTMENÜ                            │\n", Color.RED, feldGrau);
			printStyledWithBg("│                                                                              │\n", Color.WHITE, feldGrau);
			printStyledWithBg("└──────────────────────────────────────────────────────────────────────────────┘\n", Color.WHITE, feldGrau);
			return;
		}

		terminalPane.setText(""); 
		Color c64Default = new Color(104, 194, 255);
		Color aktivFarbe = Color.YELLOW;
		Color rouletteGruen = new Color(0, 140, 60);
		
		// --- MATHEMATISCHE DATA-EXTRAKTION SEKTOR 1: RESTLICHES HAUPTDECK (RESERVEN) ---
		double deckHpP1 = 0;
		for (Karte k : spieler1.hauptDeck) { if (k != null) deckHpP1 += k.leben; }
		
		double deckHpP2 = 0;
		for (Karte k : spieler2.hauptDeck) { if (k != null) deckHpP2 += k.leben; }

		// --- MATHEMATISCHE DATA-EXTRAKTION SEKTOR 2: HANDKARTEN (LIVE OPERATIV) ---
		double totalAtkP1 = 0, totalDefP1 = 0, totalHpP1 = 0;
		for (Karte k : spieler1.handKarten) {
			if (k != null) { totalAtkP1 += k.schlagkraft; totalDefP1 += k.blockZeit; totalHpP1 += k.leben; }
		}
		double totalAtkP2 = 0, totalDefP2 = 0, totalHpP2 = 0;
		for (Karte k : spieler2.handKarten) {
			if (k != null) { totalAtkP2 += k.schlagkraft; totalDefP2 += k.blockZeit; totalHpP2 += k.leben; }
		}

		// --- STRATEGISCHE RECHEL-METRIKEN (GEKÜRZT GEGEN TEXT-WRAP CHOKE) ---
		String statsP2 = String.format("   P2-METRICS ── RE-HP: %5.0f |    :-)    | HAND -> ATK:%3.0f DF:%3.1f HP:%4.0f\n", deckHpP2, totalAtkP2, totalDefP2, totalHpP2);
		String statsP1 = String.format("   P1-METRICS ── RE-HP: %5.0f |    :-)    | HAND -> ATK:%3.0f DF:%3.1f HP:%4.0f\n", deckHpP1, totalAtkP1, totalDefP1, totalHpP1);

		printStyled(statsP2, Color.GREEN);
		printStyled("================================================================================\n", Color.WHITE);
		
		// Visualisierungs-Weichen für die obere Partei (CPU / Spieler 2)
		Color headerP2Farbe = (aktivePartei == 2) ? aktivFarbe : c64Default;
		String statusP2 = (aktivePartei == 2) ? " [AM ZUG] " : "          ";
		String p2ZeitString = String.format("TIME: %.2fs | SCORE: %d", p2GesamtZeit, p2Score);

		printStyled(String.format(" [DECK: %d] %s  %s    [WIN: %d] | %s\n", 
			spieler2.hauptDeck.size(), statusP2, spieler2.name.toUpperCase() + " HAND", spieler2.ablageStapel.size(), p2ZeitString), headerP2Farbe);
		printStyled("================================================================================\n", Color.WHITE);
		
		// CPU-Handkarten vollkommen offen gerendert
		renderKartenReihe(spieler2.handKarten, p2Index, false); 

		// --- GEOMETRISCHER INTEGRITÄTS-FIX: Bündiges Schließen der Flanke ---
		printStyledWithBg("                                                                                \n", Color.BLACK, rouletteGruen);
		printStyledWithBg("                          *** C64-KAMPF-ARENA ***                               \n", Color.YELLOW, rouletteGruen);
		printStyledWithBg("                                                                                \n", Color.BLACK, rouletteGruen);
		   
		renderArenaZentrum();

		printStyled(statsP1, Color.GREEN);

		// Visualisierungs-Weichen für die untere Partei (Nicky / Spieler 1)
		Color headerP1Farbe = (aktivePartei == 1) ? aktivFarbe : c64Default;
		String statusP1 = (aktivePartei == 1) ? " [AM ZUG] " : "          ";
		String p1ZeitString = String.format("TIME: %.2fs | SCORE: %d", p1GesamtZeit, p1Score);

		printStyled("================================================================================\n", Color.WHITE);
		printStyled(String.format(" [DECK: %d] %s  %s            [WIN: %d] | %s\n", 
			spieler1.hauptDeck.size(), statusP1, spieler1.name.toUpperCase(), spieler1.ablageStapel.size(), p1ZeitString), headerP1Farbe);
		printStyled("================================================================================\n", Color.WHITE);
		
		renderKartenReihe(spieler1.handKarten, p1Index, false);
		
		printStyled("\n--------------------------------------------------------------------------------\n", Color.WHITE);
		printStyled(" STEUERUNG: Pfeiltasten LINKS / RECHTS = Auswaehlen | SPACE = Einloggen \n", Color.GREEN);
		printStyled("--------------------------------------------------------------------------------\n", Color.WHITE);


// Ende Ebene 12
	}


// ============================================================================
// EBENE 13: VERTIVAL WHITE-CARD RENDER-VECTOR (HANDKARTEN-GRID)
// ============================================================================
// Funktion: Zeichnet ASCII-Rahmen für verdeckte/offene Handkarten-Slots.
// Inversion: Schwarzer Text auf weißem Grund simuliert physische Karten.
// ============================================================================


	private static void renderKartenReihe(List<Karte> hand, int aktuellerIndex, boolean verdeckt)
	{
		Color feldGrau = new Color(60, 60, 60);
		Color lueckenGrau = Color.GRAY;

		// 1. SCHLEIFE: Oberer doppelter Rand
		for (int i = 0; i < 5; i++)
		{
			if (i < hand.size())
			{
				Karte k = hand.get(i);
				if (k == null)
				{
					printStyledWithBg("┌────────────┐  ", lueckenGrau, feldGrau);
				}
				else
				{
					Color f = (!verdeckt && i == aktuellerIndex) ? Color.YELLOW : k.kartenFarbe;
					printStyledWithBg("╔════════════╗  ", f, feldGrau);
				}
			}
		}
		printStyledWithBg("\n", Color.WHITE, Color.BLACK);

		// 2. SCHLEIFEN-KOMPLEX: Vertikaler White-Card-Kern (5 Dateneilen)
		for (int zeile = 1; zeile <= 5; zeile++)
		{
			for (int i = 0; i < 5; i++)
			{
				if (i < hand.size())
				{
					Karte k = hand.get(i);
					if (k == null)
					{
						printStyledWithBg("│", lueckenGrau, feldGrau);
						if (zeile == 3) 
						{
							printStyledWithBg("  [ LEER ]  ", Color.LIGHT_GRAY, feldGrau);
						}
						else 
						{
							printStyledWithBg("            ", feldGrau, feldGrau);
						}
						printStyledWithBg("│  ", lueckenGrau, feldGrau);
					}
					else
					{
						Color borderFarb = (!verdeckt && i == aktuellerIndex) ? Color.YELLOW : k.kartenFarbe;
						printStyledWithBg("║", borderFarb, feldGrau);
						
						if (verdeckt)
						{
							printStyledWithBg(" ░░░░░░░░░░ ", Color.LIGHT_GRAY, feldGrau);
						}
						else
						{
							String[] kartenGrafik = k.getFetteC64Grafik();
							String kern = kartenGrafik[zeile].replace("║", "").trim();
							// Hardcore-Inversion: Schwarze Schrift auf weißem Spielkarten-Grund!
							printStyledWithBg(String.format(" %-10s ", kern), Color.BLACK, Color.WHITE);
						}
						
						printStyledWithBg("║  ", borderFarb, feldGrau);
					}
				}
			}
			printStyledWithBg("\n", Color.WHITE, Color.BLACK);
		}

		// 3. SCHLEIFE: Unterer doppelter Rand
		for (int i = 0; i < 5; i++)
		{
			if (i < hand.size())
			{
				Karte k = hand.get(i);
				if (k == null)
				{
					printStyledWithBg("└────────────┘  ", lueckenGrau, feldGrau);
				}
				else
				{
					Color f = (!verdeckt && i == aktuellerIndex) ? Color.YELLOW : k.kartenFarbe;
					printStyledWithBg("╚════════════╝  ", f, feldGrau);
				}
			}
		}
		printStyledWithBg("\n", Color.WHITE, Color.BLACK);
		
		// 4. SCHLEIFE: Auswahl-Cursor
		if (!verdeckt)
		{
			for (int i = 0; i < hand.size(); i++)
			{
				if (i == aktuellerIndex)
				{
					printStyledWithBg("     [^]        ", Color.YELLOW, Color.BLACK);
				}
				else
				{
					printStyledWithBg("                ", Color.BLACK, Color.BLACK);
				}
			}
			printStyledWithBg("\n", Color.WHITE, Color.BLACK);
		}
// Ende Ebene 13
	}


// ============================================================================
// EBENE 14: ASYNCHRONER KAMPF-ALGORITHMUS (SURVIVAL-ENGINE WITH SYNC-LOCK)
// ============================================================================
// Funktion: Multi-Thread-Schlacht mit EDT-Wobble-Effekten (Schadens-Feedback).
// Logik: Transferiert tote Objekte und sendet BATTLE_END an den Remote-Peer.
// ============================================================================


	// ASYNCHRONER KAMPF-ALGORITHMUS (SURVIVAL-ENGINE MIT BI-DIREKTIONALEM TRANSFER-LOCK & SQL-HIGHSCORE)
	public static void triggerSchlachtAblauf() 
	{
		kampfLaeuft = true;
		
		Thread battleThread = new Thread(() -> 
		{
			try 
			{
				Thread.sleep(600);
				
				double ticksS1 = Math.max(1.0, (aktiveKarteP2.blockZeit / Math.max(0.01, aktiveKarteP1.blockZeit)) * 4.5);
				double ticksS2 = Math.max(1.0, (aktiveKarteP1.blockZeit / Math.max(0.01, aktiveKarteP2.blockZeit)) * 4.5);

				while (aktiveKarteP1.leben > 0 && aktiveKarteP2.leben > 0) 
				{
					// --- SPIELER 1 SCHLÄGE ---
					int schlaegeS1 = Math.min(35, Math.max(1, (int)ticksS1)); 
					for (int i = 0; i < schlaegeS1 && aktiveKarteP2.leben > 0; i++) 
					{
						double dmg = Math.max(1.5, aktiveKarteP1.schlagkraft - (aktiveKarteP2.schlagkraft / 10.0)) / 4.0;
						aktiveKarteP2.leben -= dmg;
						if (aktiveKarteP2.leben < 0) 
						{ 
							aktiveKarteP2.leben = 0; 
						}
						
						wobbleOffset = -2; 
						javax.swing.SwingUtilities.invokeLater(() -> aktualisiereSpielfeldVisualisierung()); 
						Thread.sleep(30);
						wobbleOffset = 2;  
						javax.swing.SwingUtilities.invokeLater(() -> aktualisiereSpielfeldVisualisierung()); 
						Thread.sleep(30);
						wobbleOffset = 0;  
						javax.swing.SwingUtilities.invokeLater(() -> aktualisiereSpielfeldVisualisierung()); 
						Thread.sleep(40);
					}

					if (aktiveKarteP2.leben <= 0) 
					{ 
						break; 
					}

					// --- CPU / SPIELER 2 SCHLÄGE ---
					int schlaegeS2 = Math.min(35, Math.max(1, (int)ticksS2));
					for (int i = 0; i < schlaegeS2 && aktiveKarteP1.leben > 0; i++) 
					{
						double cpuBonus = (botIQLevel == 3) ? 1.15 : 1.0;
						double dmg = (Math.max(1.5, aktiveKarteP2.schlagkraft - (aktiveKarteP1.schlagkraft / 10.0)) / 4.0) * cpuBonus;
						aktiveKarteP1.leben -= dmg;
						if (aktiveKarteP1.leben < 0) 
						{ 
							aktiveKarteP1.leben = 0; 
						}
						
						wobbleOffset = 2;  
						javax.swing.SwingUtilities.invokeLater(() -> aktualisiereSpielfeldVisualisierung()); 
						Thread.sleep(30);
						wobbleOffset = -2; 
						javax.swing.SwingUtilities.invokeLater(() -> aktualisiereSpielfeldVisualisierung()); 
						Thread.sleep(30);
						wobbleOffset = 0;  
						javax.swing.SwingUtilities.invokeLater(() -> aktualisiereSpielfeldVisualisierung()); 
						Thread.sleep(40);
					}
				}

				Thread.sleep(250);
				for (int s = 1; s <= 3; s++) 
				{
					spinZustand = s;
					javax.swing.SwingUtilities.invokeLater(() -> aktualisiereSpielfeldVisualisierung());
					Thread.sleep(60);
				}
				spinZustand = 0;

				// --- SURVIVAL EVALUATION & REFILL LOGIK (FIXED INDEX SHIFT) ---
				if (aktiveKarteP1.leben <= 0) 
				{
					// --- CPU / SPIELER 2 GEWINNT (P1 stirbt) ---
					RetroSoundEngine.spieleRundenNiederlageMoep();
					double zeitScore = Math.max(0.0, (20.0 - p2GesamtZeit) * 50.0);
					p2Score += (int)(9000.0 + Math.min(1000.0, zeitScore));
					
					// RELATIONALER SQL-SAVE-HOOK: Verankert die Punkte in der SQLite-Datenbank
					speichereHighscore(spieler2.name, p2Score, p2GesamtZeit);
					
					spieler2.ablageStapel.add(aktiveKarteP1);
					spieler1.handKarten.remove(null);
					
					aktiveKarteP1 = null; 
					p1Bereit = false;
					p2Bereit = true; // Karte bleibt im Ring
					aktivePartei = 1; 
					cpuHatInitiative = true;
				} 
				else 
				{
					// --- NICKY / SPIELER 1 GEWINNT (P2/CPU stirbt) ---
					RetroSoundEngine.spieleRundenSiegDiDi();
					double zeitScore = Math.max(0.0, (20.0 - p1GesamtZeit) * 50.0);
					p1Score += (int)(9000.0 + Math.min(1000.0, zeitScore));
					
					// RELATIONALER SQL-SAVE-HOOK: Verankert die Punkte in der SQLite-Datenbank
					speichereHighscore(spieler1.name, p1Score, p1GesamtZeit);
					
					spieler1.ablageStapel.add(aktiveKarteP2);
					spieler2.handKarten.remove(null);
					
					aktiveKarteP2 = null; 
					p2Bereit = false; 
					p1Bereit = true; // Karte bleibt im Ring
					aktivePartei = 2; 
					cpuHatInitiative = false;
				}

				p1Index = 0; p2Index = 0;
				kampfLaeuft = false;
				
				spieler1.handAuffuellen();
				spieler2.handAuffuellen();

				// NETZWERK-SYNC: Sende Match-Ergebnis an Remote-Peer für identischen State-Lock
				if (ausgewaehlterModus == 3 && networkWriter != null) 
				{
					networkWriter.println("{\"type\":\"BATTLE_END\",\"p1Score\":" + p1Score + ",\"p2Score\":" + p2Score + "}");
				}

				if ((spieler1.handKarten.isEmpty() && spieler1.hauptDeck.isEmpty()) || (spieler2.handKarten.isEmpty() && spieler2.hauptDeck.isEmpty())) 
				{
					javax.swing.SwingUtilities.invokeLater(() -> zeigeNeustartBildschirm());
					return;
				} 

				// AUTOMATISCHER CPU-KONTER-TRIGGER (WENN CPU VERLOREN HAT)
				if (ausgewaehlterModus == 1 && !cpuHatInitiative) 
				{
					javax.swing.SwingUtilities.invokeLater(() -> aktualisiereSpielfeldVisualisierung());
					
					new Thread(() -> 
					{
						try 
						{
							int bedenkzeit = (botIQLevel == 3) ? 550 : 350;
							Thread.sleep(bedenkzeit);
							
							if (!kampfLaeuft && !imOutro && aktivePartei == 2)
							{
								long cpuStart = System.currentTimeMillis();
								p2Index = berechneCpuKartenZug();
								
								aktiveKarteP2 = spieler2.handKarten.get(p2Index);
								spieler2.handKarten.set(p2Index, null); 
								p2Bereit = true;
								
								p2GesamtZeit += ((System.currentTimeMillis() - cpuStart) / 1000.0);
								
								RetroSoundEngine.spieleAktionsPiep();
								triggerSchlachtAblauf();
							}
						} 
						catch (Exception ex) {}
					}).start();
					return;
				}

				// AUTOMATISCHER CPU-ANGRIFF (WENN CPU GEWONNEN HAT)
				if (ausgewaehlterModus == 1 && cpuHatInitiative && !p2Bereit) 
				{
					long cpuStart = System.currentTimeMillis();
					p2Index = berechneCpuKartenZug();
					
					aktiveKarteP2 = spieler2.handKarten.get(p2Index);
					spieler2.handKarten.set(p2Index, null); 
					p2Bereit = true;
					
					p2GesamtZeit += ((System.currentTimeMillis() - cpuStart) / 1000.0);
					aktivePartei = 1; 
				}

				zugStartZeit = System.currentTimeMillis(); 
				javax.swing.SwingUtilities.invokeLater(() -> aktualisiereSpielfeldVisualisierung());
			} 	
			catch (Exception ex) 
			{
				ex.printStackTrace();
			}
		});
		
		battleThread.setDaemon(true);
		battleThread.start();
	
// Ende Ebene 14
	}

// ============================================================================
// EBENE 15: NEUSTART-ABFRAGE & PIANO SYNTHESIZER MENU MAPPING
// ============================================================================
// Funktion: Rendert Match-Ende (Y/N) und entsperrt die Hardware-Piano-Engine.
// Tastenzuweisung: Legt Chiptune-Frequenzen der C-Dur-Tonleiter auf Tasten 1-0.
// ============================================================================
	 
	
	public static void zeigeNeustartBildschirm()
	{
		abfrageNeustart = true;
		terminalPane.setText("");
		Color c64Default = new Color(104, 194, 255);
		Color feldGrau = new Color(60, 60, 60);
		
		for(int i = 0; i < 5; i++) 
		{
			printStyled("\n", Color.BLACK);
		}
		
		printStyledWithBg("┌──────────────────────────────────────────────────────────────────────────────┐\n", Color.WHITE, feldGrau);
		printStyledWithBg("│                        *** MATCH BEENDET ***                                 │\n", Color.YELLOW, feldGrau);
		printStyledWithBg("├──────────────────────────────────────────────────────────────────────────────┤\n", Color.WHITE, feldGrau);
		printStyledWithBg("│                                                                              │\n", Color.WHITE, feldGrau);
		printStyledWithBg("│            WOLLEN SIE DAS SPIEL NEU STARTEN?                                 │\n", c64Default, feldGrau);
		printStyledWithBg("│                                                                              │\n", Color.WHITE, feldGrau);
		printStyledWithBg("│            [Y] JA, ZURÜCK ZUM HAUPTMENÜ                                      │\n", Color.GREEN, feldGrau);
		printStyledWithBg("│            [N] NEIN, DIENST BEENDEN & SYNTHESIZER FREISCHALTEN               │\n", Color.RED, feldGrau);
		printStyledWithBg("│                                                                              │\n", Color.WHITE, feldGrau);
		printStyledWithBg("└──────────────────────────────────────────────────────────────────────────────┘\n", Color.WHITE, feldGrau);
	}

	public static void zeigePianoBildschirm()
	{
		terminalPane.setText("");
		Color neonGruen = Color.GREEN;
		Color feldGrau = new Color(60, 60, 60);
		
		for(int i = 0; i < 4; i++) 
		{
			printStyled("\n", Color.BLACK);
		}
		
		printStyledWithBg("┌──────────────────────────────────────────────────────────────────────────────┐\n", Color.WHITE, feldGrau);
		printStyledWithBg("│                 *** C64 LIVE-SYNTHESIZER OSCILLATOR ACTIVE ***               │\n", neonGruen, feldGrau);
		printStyledWithBg("├──────────────────────────────────────────────────────────────────────────────┤\n", Color.WHITE, feldGrau);
		printStyledWithBg("│                                                                              │\n", Color.WHITE, feldGrau);
		printStyledWithBg("│    DIE HARDWARE-PIANO ENGINE IST JETZT VOLLSTÄNDIG ENTSPERRT.                │\n", Color.WHITE, feldGrau);
		printStyledWithBg("│                                                                              │\n", Color.WHITE, feldGrau);
		printStyledWithBg("│    SPIELBARE RETRO-TASTEN:                                                   │\n", Color.YELLOW, feldGrau);
		printStyledWithBg("│                                                                              │\n", neonGruen, feldGrau);
		printStyledWithBg("│     [1]  [2]  [3]  [4]  [5]  [6]  [7]  [8]  [9]  [0]                         │\n", Color.YELLOW, feldGrau);
		printStyledWithBg("│      C    D    E    F    G    A    H    C2   D2   E2                         │\n", Color.CYAN, feldGrau);
		printStyledWithBg("│                                                                              │\n", Color.WHITE, feldGrau);
		printStyledWithBg("│    BEDIENUNG:                                                                │\n", Color.YELLOW, feldGrau);
		printStyledWithBg("│    Hauen Sie in die Tasten, um analoge Chiptune-Frequenzen zu erzeugen.      │\n", Color.LIGHT_GRAY, feldGrau);
		printStyledWithBg("│    Schliessen Sie das Spiel einfach oben rechts ueber das [X]-Symbol.        │\n", Color.LIGHT_GRAY, feldGrau);
		printStyledWithBg("└──────────────────────────────────────────────────────────────────────────────┘\n", Color.WHITE, feldGrau);
	
// Ende Ebene 15
	}

// ============================================================================
// EBENE 16: IN-GAME TASTATUR-STEUERUNG (REALTIME INPUT INTERCEPTOR)
// ============================================================================
// Funktion: Fängt LEFT/RIGHT/SPACE ab und blockiert unerlaubte Doppel-Eingaben.
// Netzwerk-Hook: Streamt Bewegungs- und Lock-Vektoren sofort als JSON-Zeilen.
// ============================================================================


	private static int aktiveZündungsTaste = -1; // Der physikalische Hardware-Anker

	public static java.awt.event.KeyListener holeTastaturSteuerung() 
	{
		// --- DER ULTIMATIVE NETZWERK- & PIANO-DISPATCHER ---
		// Wir klinken uns direkt in den globalen Hardware-Manager ein, um Windows-Auto-Repeat zu ZERSTÖREN!
		java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new java.awt.KeyEventDispatcher() 
		{
			@Override
			public boolean dispatchKeyEvent(java.awt.event.KeyEvent e) 
			{
				if (!imPianoModus) return false; // Wenn nicht im Synth-Modus, normal weiterleiten

				int code = e.getKeyCode();
				
				// HARDWARE-ABWEHR: Wenn Windows drückt, aber die Taste laut System schon gehalten wird -> VERNICHTEN
				if (e.getID() == java.awt.event.KeyEvent.KEY_PRESSED) 
				{
					if (code == aktiveZündungsTaste) 
					{
						e.consume(); // Löscht das künstliche Event komplett aus dem Windows-RAM
						return true; // Stoppt die Weiterleitung an das JTextPane vollständig!
					}
					
					// Erster echter Anschlag: Fixiere die Taste im Hardware-Register
					if (code >= java.awt.event.KeyEvent.VK_0 && code <= java.awt.event.KeyEvent.VK_9) 
					{
						aktiveZündungsTaste = code;
						
						// --- NATIVE INLINE MIDI SOUND SYSTEM (0% COMPILER INDEPENDENT) ---
						new Thread(() -> 
						{
							try 
							{
								javax.sound.midi.Synthesizer synth = javax.sound.midi.MidiSystem.getSynthesizer();
								synth.open();
								javax.sound.midi.MidiChannel[] channels = synth.getChannels();
								if (channels != null && channels.length > 0) 
								{
									// Berechnet die exakte C-Dur Tonleiter-Frequenz basierend auf der Taste
									int basisNote = 60; // Mittel-C
									int noteOffset = (code == java.awt.event.KeyEvent.VK_0) ? 9 : (code - java.awt.event.KeyEvent.VK_1);
									int finaleMidiNote = basisNote + noteOffset;
									
									channels[0].noteOn(finaleMidiNote, 90); // Note zünden (Lautstärke 90)
									Thread.sleep(180);                       // Tondauer halten
									channels[0].noteOff(finaleMidiNote);    // Note sauber abschalten
								}
								synth.close();
							} catch (Exception ex) {}
						}).start();
					}
					e.consume();
					return true;
				}
				
				// Erst wenn der Finger physisch hochgeht, wird das Register wieder gelöscht
				if (e.getID() == java.awt.event.KeyEvent.KEY_RELEASED) {
					if (code == aktiveZündungsTaste) 
					{
						aktiveZündungsTaste = -1; // Bereit für den nächsten Anschlag
					}
					e.consume();
					return true;
				}
				return false;
			}
		});

		return new java.awt.event.KeyAdapter() 
		{
			@Override
			public void keyPressed(java.awt.event.KeyEvent e) 
			{
				if (imPianoModus) return; // Piano wird komplett oben im Dispatcher abgefertigt!

				int code = e.getKeyCode();

				// --- NETZWERK BLOCKADE-RIEGEL FÜR ONLINE-SYNCHRONISATION ---
				if (ausgewaehlterModus == 3 && netzwerkWartetAufPartner)
				{
					if (code == java.awt.event.KeyEvent.VK_ESCAPE)
					{
						netzwerkAktiv = false;
						netzwerkWartetAufPartner = false;
						imMenue = true;
						if (pythonProcess != null) pythonProcess.destroy();
						RetroSoundEngine.spieleAktionsPiep();
						zeigeMenue();
					}
					return; 
				}

				if (kampfLaeuft || imMenue || imOutro) return;
				if (zugStartZeit == 0) zugStartZeit = System.currentTimeMillis();

				// --- NAVIGATION ---
				if (code == java.awt.event.KeyEvent.VK_LEFT)
				{
					if (aktivePartei == 1 && !p1Bereit && p1Index > 0)
					{
						p1Index--;
						if (ausgewaehlterModus == 3 && networkWriter != null)
						{
							networkWriter.println("{\"type\":\"MOVE\",\"player\":1,\"index\":" + p1Index + "}");
						}
						RetroSoundEngine.spieleNavigationsKlack();
						aktualisiereSpielfeldVisualisierung();
					}
					else if (aktivePartei == 2 && !p2Bereit && (ausgewaehlterModus == 2 || ausgewaehlterModus == 3) && p2Index > 0)
					{
						p2Index--;
						if (ausgewaehlterModus == 3 && networkWriter != null)
						{
							networkWriter.println("{\"type\":\"MOVE\",\"player\":2,\"index\":" + p2Index + "}");
						}
						RetroSoundEngine.spieleNavigationsKlack();
						aktualisiereSpielfeldVisualisierung();
					}
				}

				if (code == java.awt.event.KeyEvent.VK_RIGHT)
				{
					if (aktivePartei == 1 && !p1Bereit && p1Index < spieler1.handKarten.size() - 1)
					{
						p1Index++;
						if (ausgewaehlterModus == 3 && networkWriter != null)
						{
							networkWriter.println("{\"type\":\"MOVE\",\"player\":1,\"index\":" + p1Index + "}");
						}
						RetroSoundEngine.spieleNavigationsKlack();
						aktualisiereSpielfeldVisualisierung();
					}
					else if (aktivePartei == 2 && !p2Bereit && (ausgewaehlterModus == 2 || ausgewaehlterModus == 3) && p2Index < spieler2.handKarten.size() - 1)
					{
						p2Index++;
						if (ausgewaehlterModus == 3 && networkWriter != null)
						{
							networkWriter.println("{\"type\":\"MOVE\",\"player\":2,\"index\":" + p2Index + "}");
						}
						RetroSoundEngine.spieleNavigationsKlack();
						aktualisiereSpielfeldVisualisierung();
					}
				}

				// --- UNIVERSAL EINRAST-TRIGGER (LEERTASTE) ---
				if (code == java.awt.event.KeyEvent.VK_SPACE)
				{
					if (aktivePartei == 1)
					{
						if (!p1Bereit && spieler1.handKarten.size() > p1Index && spieler1.handKarten.get(p1Index) != null)
						{
							RetroSoundEngine.spieleAktionsPiep();
							aktiveKarteP1 = spieler1.handKarten.get(p1Index);
							spieler1.handKarten.set(p1Index, null);
							p1Bereit = true;
							if (ausgewaehlterModus == 3 && networkWriter != null)
							{
								networkWriter.println("{\"type\":\"LOCK\",\"player\":1,\"index\":" + p1Index + "}");
							}
						}
						
						if (p1Bereit)
						{
							long delta = System.currentTimeMillis() - zugStartZeit;
							p1GesamtZeit += (delta / 1000.0);
							aktivePartei = 2;
							zugStartZeit = System.currentTimeMillis();
							
							if (ausgewaehlterModus == 1)
							{
								if (!p2Bereit)
								{
									p2Index = berechneCpuKartenZug();
									aktiveKarteP2 = spieler2.handKarten.get(p2Index);
									spieler2.handKarten.set(p2Index, null);
									p2Bereit = true;
								}
								long cpuDelta = System.currentTimeMillis() - zugStartZeit;
								p2GesamtZeit += (cpuDelta / 1000.0);
								triggerSchlachtAblauf();
							} 
							else
							{
								if (p2Bereit) triggerSchlachtAblauf();
								else aktualisiereSpielfeldVisualisierung();
							}
						}
					}
					else if (aktivePartei == 2)
					{
						if (ausgewaehlterModus == 1)
						{
							if (!p2Bereit)
							{
								RetroSoundEngine.spieleAktionsPiep();
								p2Index = berechneCpuKartenZug();
								aktiveKarteP2 = spieler2.handKarten.get(p2Index);
								spieler2.handKarten.set(p2Index, null);
								p2Bereit = true;
							}
							long cpuDelta = System.currentTimeMillis() - zugStartZeit;
							p2GesamtZeit += (cpuDelta / 1000.0);
							triggerSchlachtAblauf();
						}
						else if (ausgewaehlterModus == 2 || ausgewaehlterModus == 3)
						{
							if (!p2Bereit && spieler2.handKarten.size() > p2Index && spieler2.handKarten.get(p2Index) != null)
							{
								RetroSoundEngine.spieleAktionsPiep();
								aktiveKarteP2 = spieler2.handKarten.get(p2Index);
								spieler2.handKarten.set(p2Index, null);
								p2Bereit = true;
								if (ausgewaehlterModus == 3 && networkWriter != null)
								{
									networkWriter.println("{\"type\":\"LOCK\",\"player\":2,\"index\":" + p2Index + "}");
								}
							}
							
							if (p2Bereit && p1Bereit)
							{
								long delta = System.currentTimeMillis() - zugStartZeit;
								p2GesamtZeit += (delta / 1000.0);
								triggerSchlachtAblauf();
							}
						}
					}
				}
			}
		};
// Ende Ebene 16
	}

// ============================================================================
// EBENE 17: MENÜ-NAVIGATION & CPU-IQ FORCE OVERRIDE
// ============================================================================
// Funktion: Steuert die zweistufige Selektion (Moduswahl und IQ-Härtung).
// System-Sync: Fixiert den gewählten KI-IQ nach der Deck-Initialisierung.
// ============================================================================

	// MENÜ-STEUERUNG C64 WITH THREE-WAY EXTRACTION & P2P CONNECTION LOCK
	public static java.awt.event.KeyListener holeMenueSteuerung() 
	{
		return new java.awt.event.KeyAdapter() 
		{
			@Override
			public void keyPressed(java.awt.event.KeyEvent e) 
			{
				if (!imMenue) return;

				// --- SCHRITT 1: MODUS WAHL (3 MODI ZYKLUS) ---
				if (aktuellerAuswahlSchritt == 1) 
				{
					if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN) 
					{
						ausgewaehlterModus = (ausgewaehlterModus % 3) + 1;
						RetroSoundEngine.spieleNavigationsKlack();
						zeigeMenue();
					}
					if (e.getKeyCode() == java.awt.event.KeyEvent.VK_UP) 
					{
						ausgewaehlterModus = (ausgewaehlterModus == 1) ? 3 : ausgewaehlterModus - 1;
						RetroSoundEngine.spieleNavigationsKlack();
						zeigeMenue();
					}
					
					if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) 
					{
						RetroSoundEngine.spieleAktionsPiep();
						
						// --- WHITE-HAT INPUT INTERCEPTOR PROZESS ---
						java.io.File logoDatei = new java.io.File("atlas_logo.png");
						javax.swing.ImageIcon atlasIcon = logoDatei.exists() ? new javax.swing.ImageIcon("atlas_logo.png") : null;

						String nameEingabe = (String) javax.swing.JOptionPane.showInputDialog(null, 
							"Spielername eingeben (Max. 6 Buchstaben, keine Zahlen/Sonderzeichen):", 
							"ATLAS SECURITY INITIALISIERUNG", javax.swing.JOptionPane.QUESTION_MESSAGE,
							atlasIcon, null, spielerNameP1);

						if (nameEingabe != null && !nameEingabe.trim().isEmpty()) 
						{
							// REGEX-WALL: Tilgt alle Zahlen und Zeichen, lässt nur Buchstaben durch
							String gefiltert = nameEingabe.replaceAll("[^a-zA-Z]", "");
							
							// HARD REGISTER BOUNDS: Schneidet den Vektor bei Überlänge gnadenlos ab
							if (gefiltert.length() > 6) 
							{
								gefiltert = gefiltert.substring(0, 6);
							}
							
							if (!gefiltert.isEmpty()) 
							{
								spielerNameP1 = gefiltert.toUpperCase(); // C64 Retro-Schriftbild erzwingen
							}
						}

						if (ausgewaehlterModus == 1) 
						{
							aktuellerAuswahlSchritt = 2;
							zeigeMenue();
						} 
						else if (ausgewaehlterModus == 2)
						{
							// Lokales PvP Modus zünden
							imMenue = false;
							initialisiereMatchKomponenten();
							RetroSoundEngine.starteHintergrundSound();
							aktualisiereSpielfeldVisualisierung();
						}
						else 
						{
							// Modus 3: Online P2P Modus zünden & HÄRTESTES WARTESCHLOSS AKTIVIEREN
							imMenue = false;
							netzwerkWartetAufPartner = true; 
							initialisiereMatchKomponenten();
							RetroSoundEngine.starteHintergrundSound();
							starteNetzwerkBridgeProtokoll(); 
							aktualisiereSpielfeldVisualisierung();
						}
					}
				}
				// --- SCHRITT 2: DIREKTER CPU-IQ HARD LOCK ---
				else if (aktuellerAuswahlSchritt == 2) 
				{
					if (e.getKeyCode() == java.awt.event.KeyEvent.VK_LEFT) 
					{
						if (botIQLevel > 1) 
						{
							botIQLevel--;
							RetroSoundEngine.spieleNavigationsKlack();
							zeigeMenue();
						}
					}
					if (e.getKeyCode() == java.awt.event.KeyEvent.VK_RIGHT) 
					{
						if (botIQLevel < 3) 
						{
							botIQLevel++;
							RetroSoundEngine.spieleNavigationsKlack();
							zeigeMenue();
						}
					}
					if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) 
					{
						RetroSoundEngine.spieleAktionsPiep();
						
						int finalerIQ = botIQLevel; 
						imMenue = false;
						
						initialisiereMatchKomponenten(); 
						botIQLevel = finalerIQ; 
						
						RetroSoundEngine.starteHintergrundSound(); 
						aktualisiereSpielfeldVisualisierung();
					}
				}
			}
		};
	
// Ende Ebene 17
	}

// ============================================================================
// HILFS-METRIK: C64-RETRO-ANLEITUNG RENDER-ENGINE
// ============================================================================
// Funktion: Zeigt das Regelwerk im 80-Zeichen-Raster vor dem Hauptmenü.
// ============================================================================
	public static void zeigeAnleitungSpielfeld() 
	{
		terminalPane.setText("");
		Color c64Default = new Color(104, 194, 255);
		Color gelb = Color.YELLOW;
		
		printStyled("================================================================================\n", Color.WHITE);
		printStyled("                 *** ATLAS ARCADE: CRYPTO-CHAIN DUEL ANLEITUNG ***             \n", gelb);
		printStyled("================================================================================\n\n", Color.WHITE);
		printStyled(" SCHLACHT-SYSTEM & STRATEGIE:\n", gelb);
		printStyled("  - Beide Spieler starten mit einem identisch ausbalancierten Krypto-Deck.\n", c64Default);
		printStyled("  - Ziel ist es, die gegnerischen Karten komplett zu liquidieren.\n\n", c64Default);
		printStyled(" MATHEMATISCHE CORE-ANALYSE (WICHTIG FÜR DEN SIEG!):\n", gelb);
		printStyled("  - HP  = Lebensenergie  ||  ATK = Reine Angriffskraft pro Schlag.\n", c64Default);
		printStyled("  - B   = Blockzeit. Je NIEDRIGER die Blockzeit, desto SCHNELLER die Kette!\n", c64Default);
		printStyled("  - Die Blockzeit bestimmt als Multiplikator, wie oft eine Karte zuschlaegt.\n", c64Default);
		printStyled("  - RECHNE IM KOPF: Eine niedrige Blockzeit (z.B. 0.09) erzeugt massiv viele\n", c64Default);
		printStyled("    Angriffs-Ticks und kann einen dicken Tank mit hoher ATK blitzschnell\n", c64Default);
		printStyled("    ueberholen. Vergleiche stets HP, ATK und B der Ketten vor dem Ausspielen!\n\n", c64Default);
		printStyled(" STEUERUNG IN DER ARENA:\n", gelb);
		printStyled("  - Pfeiltasten LINKS / RECHTS : Karte auf der Hand auswaehlen [^]\n", c64Default);
		printStyled("  - LEERTASTE (SPACE)          : Karte in den Ring werfen / Zug beenden\n\n", c64Default);
		printStyled(" COGNITIVE ENGINE:\n", gelb);
		printStyled("  - Die CPU berechnet auf Stufe 'HART' alle Ticks und Schadens-Vektoren voraus.\n", c64Default);
		printStyled("  - Rechnerischer Vorteil schlaegt hier puren Zufall zu 100%.\n\n", c64Default);
		printStyled("================================================================================\n", Color.WHITE);
		printStyled("               DRÜCKEN SIE [SPACE] UM DAS HAUPTMENÜ ZU ZÜNDEN                   \n", Color.GREEN);
		printStyled("================================================================================\n", Color.WHITE);
	}
// Ende Zusatz-Ebene
// ============================================================================
// EBENE 18: UNIVERSAL ROUTING-FIREWALL (KEY-ADAPTER DISPATCHER)
// ============================================================================
// Funktion: Überwacht alle Applikations-Phasen (Menü, Spiel, Outro, Piano).
// Reset: Löscht alle Kernmetriken und Scores bei Betätigung von Taste [Y].
// ============================================================================


	//SYSTEM-CONFERENCE & DYNAMISCHE TASTATUR-SCHALTUNG
	public static void initialisiereTastaturRouting(JFrame frame) 
	{
		java.awt.event.KeyListener menueSteuerung = holeMenueSteuerung();
		java.awt.event.KeyListener spielSteuerung = holeTastaturSteuerung();

		// UNKNACKBARE FIREWALL: Der Listener muss direkt auf das Textfeld lauschen!
		terminalPane.addKeyListener(new java.awt.event.KeyAdapter() 
		{
			@Override
			public void keyPressed(java.awt.event.KeyEvent e) 
			{
				// --- PHASE 0: ANLEITUNG WEGDRÜCKEN ---
				if (zeigeAnleitung) 
				{
					if (e.getKeyCode() == java.awt.event.KeyEvent.VK_SPACE) 
					{
						zeigeAnleitung = false;
						imMenue = true;
						RetroSoundEngine.spieleAktionsPiep();
						zeigeMenue();
					}
					return;
				}

				
				// --- PHASE 1: NEUSTART ABFRAGE (Y/N) ---
				if (abfrageNeustart)
				{
					if (e.getKeyCode() == java.awt.event.KeyEvent.VK_Y)
					{
						// Reset aller Kern-Metriken für das frische Hauptmenü
						imMenue = true;
						imOutro = false;
						imPianoModus = false;
						abfrageNeustart = false;
						aktuellerAuswahlSchritt = 1;
						ausgewaehlterModus = 1;
						botIQLevel = 2;
						aktiveKarteP1 = null;
						aktiveKarteP2 = null;
						p1Bereit = false;
						p2Bereit = false;
						p1GesamtZeit = 0.0;
						p2GesamtZeit = 0.0;
						p1Score = 0;
						p2Score = 0;
						RetroSoundEngine.spieleAktionsPiep();
						zeigeMenue();
					}
					else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_N)
					{
						RetroSoundEngine.spieleAktionsPiep();
						zeigeOutro();
					}
					return;
				}

				// --- PHASE 2: DAS SPIEL-OUTRO (ENTER-TRIGGER FÜR PIANO) ---
				if (imOutro && !imPianoModus)
				{
					if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER)
					{
						imPianoModus = true;
						RetroSoundEngine.spieleAktionsPiep();
						zeigePianoBildschirm();
					}
					return;
				}

				// --- PHASE 3: LIVE PIANO MODUS AKTIV ---
				if (imPianoModus)
				{
					if (e.getKeyCode() >= java.awt.event.KeyEvent.VK_0 && e.getKeyCode() <= java.awt.event.KeyEvent.VK_9) 
					{
						int numKey = e.getKeyCode() - java.awt.event.KeyEvent.VK_0;
						// Mappt Tasten 1-9 auf Index 0-8, Taste 0 (Keycode 48) wird Index 9
						int tastenIndex = (numKey == 0) ? 9 : numKey - 1;
						
						int[] pianoNotes = {60, 62, 64, 65, 67, 69, 71, 72, 74, 76}; // C-Dur Tonleiter
						
						try 
						{
							Synthesizer synth = MidiSystem.getSynthesizer();
							if (!synth.isOpen()) synth.open();
							MidiChannel[] channels = synth.getChannels();
							if (channels != null && channels.length > 0) 
							{
								// HARDWARE-FIX ERZWUNGEN: [0] fixiert!
								channels[0].programChange(81); // Bleibt auf knackigem C64 Lead-Square
								channels[0].noteOn(pianoNotes[tastenIndex], 95);
								new Thread(() -> {
									try { 
										Thread.sleep(200); 
										channels[0].noteOff(pianoNotes[tastenIndex]); 
									} catch (Exception ex) {}
								}).start();
							}
						} catch (Exception ex) {}
					}
					return;
				}

				// --- PHASE 4: STANDARD SPIELBETRIEB ---
				if (imMenue) 
				{
					menueSteuerung.keyPressed(e);
				} 
				else 
				{
					spielSteuerung.keyPressed(e);
				}
			}
		});
	
// Ende Ebene 18
	}


// ============================================================================
// EBENE 19: PARALLELES ARENA-GRID-RENDERING (KAMPF-ZENTRUM)
// ============================================================================
// Funktion: Zeichnet das grüne Roulette-Zentrum und das statische ◄VS►-Schloss.
// Blenden-Schutz: Textgleiche Maskierung macht leere Slots unsichtbar.
// ============================================================================


	private static void renderArenaZentrum()
	{
		// 1. DYNAMISCHES PHASEN-SCHLOSS FÜR SPIELER 1 (BLENDEN-EBENE)
		String[] p1Lines = new String[7];
		Color inhaltFgP1 = Color.BLACK; // Standardmäßig SCHWARZ -> Macht die Schrift im leeren Slot unsichtbar!
		Color inhaltBgP1 = Color.BLACK;
		Color p1Farbe = Color.DARK_GRAY;

		if (aktiveKarteP1 != null)
		{
			p1Lines = aktiveKarteP1.getFetteC64Grafik();
			p1Farbe = aktiveKarteP1.kartenFarbe;
			inhaltBgP1 = Color.WHITE;
			inhaltFgP1 = Color.BLACK;
		}
		else
		{
			// Prüfen, ob Spieler 1 komplett liquidiert ist
			if (spieler1.handKarten.isEmpty() && spieler1.hauptDeck.isEmpty())
			{
				p1Lines = new String[]{"┌────────────┐", "║  [ TOTAL ] ║", "║  LIQUID.   ║", "║  NETWORK   ║", "║  DRAINED   ║", "║            ║", "└────────────┘"};
				p1Farbe = Color.RED;
				inhaltBgP1 = Color.BLACK;
				inhaltFgP1 = Color.RED;
			}
			else
			{
				// Karte ist nur temporär besiegt -> Reine, unsichtbare Stealth-Blende
				p1Lines = new String[]{"┌────────────┐", "│            │", "│            │", "│            │", "│            │", "│            │", "└────────────┘"};
				p1Farbe = Color.DARK_GRAY;
				inhaltBgP1 = Color.BLACK;
				inhaltFgP1 = Color.BLACK; // Textfarbe matched Hintergrundfarbe exakt!
			}
		}

		// 2. DYNAMISCHES PHASEN-SCHLOSS FÜR CPU / SPIELER 2 (BLENDEN-EBENE)
		String[] cpuLines = new String[7];
		Color inhaltFgCpu = Color.BLACK;
		Color inhaltBgCpu = Color.BLACK;
		Color cpuFarbe = Color.DARK_GRAY;

		if (aktiveKarteP2 != null)
		{
			cpuLines = aktiveKarteP2.getFetteC64Grafik();
			cpuFarbe = aktiveKarteP2.kartenFarbe;
			inhaltBgCpu = Color.WHITE;
			inhaltFgCpu = Color.BLACK;
		}
		else
		{
			// Prüfen, ob CPU komplett liquidiert ist
			if (spieler2.handKarten.isEmpty() && spieler2.hauptDeck.isEmpty())
			{
				cpuLines = new String[]{"┌────────────┐", "║  [ TOTAL ] ║", "║  LIQUID.   ║", "║  NETWORK   ║", "║  DRAINED   ║", "║            ║", "└────────────┘"};
				cpuFarbe = Color.RED;
				inhaltBgCpu = Color.BLACK;
				inhaltFgCpu = Color.RED;
			}
			else
			{
				cpuLines = new String[]{"┌────────────┐", "│            │", "│            │", "│            │", "│            │", "│            │", "└────────────┘"};
				cpuFarbe = Color.DARK_GRAY;
				inhaltBgCpu = Color.BLACK;
				inhaltFgCpu = Color.BLACK;
			}
		}

		Color rouletteGruen = new Color(0, 140, 60);
		String wandLinksP1 = (aktiveKarteP1 != null) ? "║" : "│";
		String wandRechtsP1 = (aktiveKarteP1 != null) ? "║" : "│";
		String wandLinksCpu = (aktiveKarteP2 != null) ? "║" : "│";
		String wandRechtsCpu = (aktiveKarteP2 != null) ? "║" : "│";

		// 3. PARALLELES GRID-RENDERING (Exakt 80 Zeichen breit kalibriert)
		for (int i = 0; i < 7; i++)
		{
			printStyledWithBg("                     ", Color.BLACK, rouletteGruen);
			
			// --- ARENA SLOT LINKS: NICKY ---
			if (i > 0 && i < 6) 
			{
				printStyledWithBg(wandLinksP1, p1Farbe, rouletteGruen);
				String kern = p1Lines[i].replace("║", "").replace("│", "").trim();
				printStyledWithBg(String.format(" %-10s ", kern), inhaltFgP1, inhaltBgP1);
				printStyledWithBg(wandRechtsP1, p1Farbe, rouletteGruen);
			} 
			else 
			{
				String rand = p1Lines[i];
				if (aktiveKarteP1 == null) 
				{
					rand = rand.replace("╔", "┌").replace("═", "─").replace("╗", "┐").replace("╚", "└").replace("╝", "┘");
				}
				printStyledWithBg(rand, p1Farbe, rouletteGruen);
			}
			
			// Das stabilisierte VS-Kampfschloss
			printStyledWithBg("   ", Color.BLACK, rouletteGruen);
			if (i == 3) 
			{
				printStyledWithBg("◄VS►", Color.YELLOW, rouletteGruen);
			}
			else 
			{
				printStyledWithBg("    ", Color.BLACK, rouletteGruen);
			}
			printStyledWithBg("   ", Color.BLACK, rouletteGruen);
			
			// --- ARENA SLOT RECHTS: CPU ---
			if (i > 0 && i < 6) 
			{
				printStyledWithBg(wandLinksCpu, cpuFarbe, rouletteGruen);
				String kern = cpuLines[i].replace("║", "").replace("│", "").trim();
				printStyledWithBg(String.format(" %-10s ", kern), inhaltFgCpu, inhaltBgCpu);
				printStyledWithBg(wandRechtsCpu, cpuFarbe, rouletteGruen);
			} 
			else 
			{
				String rand = cpuLines[i];
				if (aktiveKarteP2 == null) 
				{
					rand = rand.replace("╔", "┌").replace("═", "─").replace("╗", "┐").replace("╚", "└").replace("╝", "┘");
				}
				printStyledWithBg(rand, cpuFarbe, rouletteGruen);
			}
			
			printStyledWithBg("                     \n", Color.BLACK, rouletteGruen);
		}
		
		printStyledWithBg("                                                                                \n", Color.BLACK, rouletteGruen);

// Ende Ebene 19
	}


//.....................ENDE.........................

// ============================================================================
// EBENE 20: FORENSISCHES OUTRO (GAME OVER CASKADE)
// ============================================================================
// Funktion: Flüssige Laufschrift-Verschiebung im synchronisierten 80ms-Takt.
// Statement: Stoppt Hintergrundmusik und gibt das finale Entwickler-Credo aus.
// ============================================================================


	private static void zeigeOutro() 
	{
		imOutro = true;
		soundAktiv = false; // Stoppt die Kampf-Hintergrundmusik endgültig
		abfrageNeustart = false;
		
		Color c64Default = new Color(104, 194, 255);
		Color gelb = Color.YELLOW;
		Color weiss = Color.WHITE;
		Color rot = Color.RED;

		// Forensische Laufschrift-Kaskade: Schiebt den Text flüssig nach unten
		new Thread(() -> 
		{
			try 
			{
				for (int vorschub = 0; vorschub < 8; vorschub++) 
				{
					final int aktuelleZeilen = vorschub;
					javax.swing.SwingUtilities.invokeLater(() -> 
					{
						terminalPane.setText("");
					
						// Vertikaler Abstandhalter
						for (int i = 0; i < aktuelleZeilen; i++) 
						{
							printStyled("\n", Color.BLACK);
						}
					
						// --- MASSIVER RAHMEN BLOCK (Exakt 80 Zeichen breit kalibriert) ---
						printStyled("┌──────────────────────────────────────────────────────────────────────────────┐\n", weiss);
						printStyled("│                        *** G A M E   O V E R ***                             │\n", rot);
						printStyled("├──────────────────────────────────────────────────────────────────────────────┤\n", weiss);
						printStyled("│                                                                              │\n", weiss);
						printStyled("│       ►►►             SPIELER WISSE EINS                   ◄◄◄               │\n", Color.GREEN);		
						printStyled("│                                                                              │\n", weiss);
						printStyled("│             Es gibt nur zwei Sorten von Programmierern:                      │\n", c64Default);
						printStyled("│           Die einen lieben Java, die anderen hassen Java.                    │\n", c64Default);
						printStyled("│                                                                              │\n", weiss);
						printStyled("│       Ich gehoere zu der Sorte, die Java hasst, musste aber ein              │\n", gelb);
						printStyled("│                   Programm mit Java programmieren.                           │\n", gelb);
						printStyled("│                                                                              │\n", weiss);
						printStyled("│       ►►►         E S   L E B E   P Y T H O N !            ◄◄◄               │\n", Color.GREEN);
						printStyled("│                                                                              │\n", weiss);
						printStyled("├──────────────────────────────────────────────────────────────────────────────┤\n", weiss);
						printStyled("│      DRÜCKEN SIE [ENTER] UM DEN 8-BIT HARDWARE SYNTHESIZER ZU STARTEN        │\n", Color.ORANGE);
						printStyled("└──────────────────────────────────────────────────────────────────────────────┘\n", weiss);
					});
					Thread.sleep(80); // Taktung für das geschmeidige Herabgleiten
				}
			} 
			catch (Exception ex) {}
		}).start();


// ENDE EBENE 20		
	}

// ============================================================================
// EBENE 21: UNSICHTBARE NETZWERK-BRIDGE PROZESS-STEUERUNG (TEIL 1/2)
// ============================================================================
// Funktion: Schreibt und startet die Python-P2P-Brücke im System-Hintergrund.
// Fallback: Nutzt 5 feste Ports, sonst manuelle über das Fenster eintragen .
// Matrix: 50066, 50067, 50068, 50069, 50070 (No-Admin garantiert)
// ============================================================================

	private static Process pythonProcess = null;

	public static void starteNetzwerkBridgeProtokoll() 
	{
		new Thread(() -> 
		{
			java.io.File tempPythonFile = new java.io.File(System.getProperty("java.io.tmpdir"), "atlas_bridge.py");
			try (java.io.PrintWriter fileWriter = new java.io.PrintWriter(new java.io.FileWriter(tempPythonFile))) 
			{
				fileWriter.println("import asyncio, sys");
				fileWriter.println("HOST, PORTS = '0.0.0.0', [50066, 50067, 50068, 50069, 50070]");
				fileWriter.println("class AtlasNetworkBridge:");
				fileWriter.println("    def __init__(self, mode, peer_ip=None, manueller_port=None):");
				fileWriter.println("        self.mode, self.peer_ip, self.manueller_port, self.peer_writer = mode, peer_ip, manueller_port, None");
				fileWriter.println("    async def handle_java_input(self):");
				fileWriter.println("        loop = asyncio.get_event_loop()");
				fileWriter.println("        reader = asyncio.StreamReader()");
				fileWriter.println("        protocol = asyncio.StreamReaderProtocol(reader)");
				fileWriter.println("        await loop.connect_read_pipe(lambda: protocol, sys.stdin)");
				fileWriter.println("        while True:");
				fileWriter.println("            line = await reader.readline()");
				fileWriter.println("            if not line: break");
				fileWriter.println("            raw = line.decode().strip()");
				fileWriter.println("            if raw and self.peer_writer and not self.peer_writer.is_closing():");
				fileWriter.println("                self.peer_writer.write(f'{raw}\\n'.encode()); await self.peer_writer.drain()");
				fileWriter.println("    async def handle_peer_connection(self, reader, writer):");
				fileWriter.println("        self.peer_writer = writer");
				fileWriter.println("        while True:");
				fileWriter.println("            data = await reader.readline()");
				fileWriter.println("            if not data: break");
				fileWriter.println("            sys.stdout.write(data.decode()); sys.stdout.flush()");
				fileWriter.println("    async def start_host(self):");
				fileWriter.println("        server = None; port = None");
				fileWriter.println("        if self.manueller_port:");
				fileWriter.println("            try: server = await asyncio.start_server(self.handle_peer_connection, HOST, self.manueller_port); port = self.manueller_port");
				fileWriter.println("            except Exception: pass");
				fileWriter.println("        if not server:");
				fileWriter.println("            for p in PORTS:");
				fileWriter.println("                try: server = await asyncio.start_server(self.handle_peer_connection, HOST, p); port = p; break");
				fileWriter.println("                except Exception: continue");
				fileWriter.println("        if not server:");
				fileWriter.println("            try: server = await asyncio.start_server(self.handle_peer_connection, HOST, 0); port = server.sockets[0].getsockname()[1]");
				fileWriter.println("            except Exception: sys.exit(1)");
				fileWriter.println("        sys.stdout.write(f'PORT_BINDING:{port}\\n'); sys.stdout.flush()");
				fileWriter.println("        asyncio.create_task(self.handle_java_input()); await server.serve_forever()");
				fileWriter.println("    async def start_peer(self):");
				fileWriter.println("        if self.manueller_port:");
				fileWriter.println("            try:");
				fileWriter.println("                reader, writer = await asyncio.open_connection(self.peer_ip, self.manueller_port)");
				fileWriter.println("                self.peer_writer = writer; sys.stdout.write(f'PORT_BINDING:{self.manueller_port}\\n'); sys.stdout.flush()");
				fileWriter.println("                asyncio.create_task(self.handle_peer_connection(reader, writer)); await self.handle_java_input(); return");
				fileWriter.println("            except Exception: pass");
				fileWriter.println("        for port in PORTS:");
				fileWriter.println("            try:");
				fileWriter.println("                reader, writer = await asyncio.open_connection(self.peer_ip, port)");
				fileWriter.println("                self.peer_writer = writer; sys.stdout.write(f'PORT_BINDING:{port}\\n'); sys.stdout.flush()");
				fileWriter.println("                asyncio.create_task(self.handle_peer_connection(reader, writer)); await self.handle_java_input(); return");
				fileWriter.println("            except Exception: continue");
				fileWriter.println("        sys.stdout.write('PORT_FAIL\\n'); sys.stdout.flush()");
				fileWriter.println("async def main():");
				fileWriter.println("    if len(sys.argv) < 2: sys.exit(1)");
				fileWriter.println("    mode = sys.argv[1]");
				fileWriter.println("    m_port = int(sys.argv[3]) if len(sys.argv) == 4 else None");
				fileWriter.println("    if mode == 'host': bridge = AtlasNetworkBridge(mode, manueller_port=m_port); await bridge.start_host()");
				fileWriter.println("    elif mode == 'peer' and len(sys.argv) >= 3:");
				fileWriter.println("        bridge = AtlasNetworkBridge(mode, sys.argv[2], manueller_port=m_port); await bridge.start_peer()");
				fileWriter.println("if __name__ == '__main__': asyncio.run(main())");
			} 
			catch (java.io.IOException e) 
			{
				netzwerkAktiv = false;
				return;
			}


// ============================================================================
// EBENE 21: UNSICHTBARE NETZWERK-BRIDGE PROZESS-STEUERUNG (TEIL 2/2)
// ============================================================================

			String pythonCmd = "python"; 
			String mode = "host"; 
			String zielIp = "";
			String manuellerPort = "";
			
			// Lädt das Atlas-Triforce für das GUI-Fenster
			java.io.File logoDatei = new java.io.File("atlas_logo.png");
			javax.swing.ImageIcon atlasIcon = logoDatei.exists() ? new javax.swing.ImageIcon("atlas_logo.png") : null;
			
			String eingabe = (String) javax.swing.JOptionPane.showInputDialog(null, 
				"Geben Sie die IP-Adresse des Gegners ein:\n(Leer lassen für HOST-Modus)", 
				"ATLAS NETZWERK-BRIDGE SETUP", javax.swing.JOptionPane.QUESTION_MESSAGE,
				atlasIcon, null, "");
			
			if (eingabe != null && !eingabe.trim().isEmpty()) 
			{
				mode = "peer";
				zielIp = eingabe.trim();
			}

			try 
			{
				ProcessBuilder pb;
				if (mode.equals("peer")) 
				{
					pb = new ProcessBuilder(pythonCmd, tempPythonFile.getAbsolutePath(), "peer", zielIp);
				} 
				else 
				{
					pb = new ProcessBuilder(pythonCmd, tempPythonFile.getAbsolutePath(), "host");
				}
				
				pythonProcess = pb.start();
				java.io.BufferedReader pythonReader = new java.io.BufferedReader(new java.io.InputStreamReader(pythonProcess.getInputStream(), "UTF-8"));
				int erkannterPort = -1;
				String pythonZeile;
				
				long startZeit = System.currentTimeMillis();
				while (System.currentTimeMillis() - startZeit < 2500) 
				{
					if (pythonReader.ready()) 
					{
						pythonZeile = pythonReader.readLine();
						if (pythonZeile != null && pythonZeile.startsWith("PORT_BINDING:")) 
						{
							erkannterPort = Integer.parseInt(pythonZeile.substring(13).trim());
							break;
						}
						if (pythonZeile != null && pythonZeile.equals("PORT_FAIL")) 
						{
							break;
						}
					} 
					else 
					{
						Thread.sleep(30);
					}
				}
				
				// Manuelles Pop-up bei Firewall-Blockade
				if (erkannterPort == -1) 
				{
					if (pythonProcess != null) pythonProcess.destroy();
					
					String portEingabe = (String) javax.swing.JOptionPane.showInputDialog(null, 
						"Standard-Ports belegt oder Host nicht gefunden.\nBitte Port manuell eingeben:", 
						"ATLAS FALLBACK-PORT PROTOKOLL", javax.swing.JOptionPane.WARNING_MESSAGE,
						atlasIcon, null, "");
					
					if (portEingabe != null && !portEingabe.trim().isEmpty()) 
					{
						manuellerPort = portEingabe.trim();
						if (mode.equals("peer")) 
						{
							pb = new ProcessBuilder(pythonCmd, tempPythonFile.getAbsolutePath(), "peer", zielIp, manuellerPort);
						} 
						else 
						{
							pb = new ProcessBuilder(pythonCmd, tempPythonFile.getAbsolutePath(), "host", manuellerPort);
						}
						pythonProcess = pb.start();
						pythonReader = new java.io.BufferedReader(new java.io.InputStreamReader(pythonProcess.getInputStream(), "UTF-8"));
						
						startZeit = System.currentTimeMillis();
						while (System.currentTimeMillis() - startZeit < 2000) 
						{
							if (pythonReader.ready()) 
							{
								pythonZeile = pythonReader.readLine();
								if (pythonZeile != null && pythonZeile.startsWith("PORT_BINDING:")) 
								{
									erkannterPort = Integer.parseInt(pythonZeile.substring(13).trim());
									break;
								}
							} 
							else 
							{
								Thread.sleep(30);
							}
						}
					}
				}
				
				if (erkannterPort == -1) 
				{
					netzwerkAktiv = false;
					return;
				}
				
				java.net.Socket socket = new java.net.Socket("127.0.0.1", erkannterPort);
				networkWriter = new java.io.PrintWriter(socket.getOutputStream(), true);
				java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(socket.getInputStream(), "UTF-8"));
				netzwerkAktiv = true;

				Runtime.getRuntime().addShutdownHook(new Thread(() -> 
				{
					if (pythonProcess != null) pythonProcess.destroy();
				}));

				if (mode.equals("peer") && networkWriter != null) 
				{
					networkWriter.println("{\"type\":\"READY\"}");
				}

				String zeile;
				while (netzwerkAktiv && (zeile = reader.readLine()) != null) 
				{
					final String daten = zeile;
					javax.swing.SwingUtilities.invokeLater(() -> 
					{
						if (daten.contains("\"type\":\"READY\"")) 
						{
							if (netzwerkWartetAufPartner) 
							{
								netzwerkWartetAufPartner = false; 
								if (networkWriter != null) 
								{
									networkWriter.println("{\"type\":\"READY_CONFIRM\"}");
								}
								zugStartZeit = System.currentTimeMillis(); 
								RetroSoundEngine.spieleAktionsPiep();
								aktualisiereSpielfeldVisualisierung();
							}
						}
						else if (daten.contains("\"type\":\"READY_CONFIRM\""))
						{
							if (netzwerkWartetAufPartner) 
							{
								netzwerkWartetAufPartner = false; 
								zugStartZeit = System.currentTimeMillis(); 
								RetroSoundEngine.spieleAktionsPiep();
								aktualisiereSpielfeldVisualisierung();
							}
						}
						else if (daten.contains("\"type\":\"MOVE\"")) 
						{
							int pl = daten.contains("\"player\":1") ? 1 : 2;
							int idxPos = daten.indexOf("\"index\":") + 8;
							int endPos = daten.indexOf("}", idxPos);
							int idx = Integer.parseInt(daten.substring(idxPos, endPos).trim());
							
							if (pl == 1) p1Index = idx; else p2Index = idx;
							RetroSoundEngine.spieleNavigationsKlack();
							aktualisiereSpielfeldVisualisierung();
						} 
						else if (daten.contains("\"type\":\"LOCK\"")) 
						{
							int pl = daten.contains("\"player\":1") ? 1 : 2;
							int idxPos = daten.indexOf("\"index\":") + 8;
							int endPos = daten.indexOf("}", idxPos);
							int idx = Integer.parseInt(daten.substring(idxPos, endPos).trim());
							
							if (pl == 1 && !p1Bereit) 
							{
								aktiveKarteP1 = spieler1.handKarten.get(idx);
								spieler1.handKarten.set(idx, null);
								p1Bereit = true;
								aktivePartei = 2; 
								zugStartZeit = System.currentTimeMillis(); 
							} 
							else if (pl == 2 && !p2Bereit) 
							{
								aktiveKarteP2 = spieler2.handKarten.get(idx);
								spieler2.handKarten.set(idx, null);
								p2Bereit = true;
								zugStartZeit = System.currentTimeMillis(); 
							}
							RetroSoundEngine.spieleAktionsPiep();
						}
					});
				}
			} 
			catch (Exception e) 
			{
				netzwerkAktiv = false;
			}

		}).start();

// ENDE EBENE 21
	}

	
// ============================================================================
// EBENE 22: RELATIONALE SQL-HIGHSCORE-ANBINDUNG (SQLITE CORE ENGINE)
// ============================================================================
// Funktion: Lizenzfreies, lokales SQLite-Schema mit Prepared Statements.
// Datenhaltung: Speichert Spieler, Score und Bedenkzeit mit Auto-Timestamp.
// ============================================================================

	private static java.sql.Connection dbConnection = null;

	public static void initialisiereHighscoreDatenbank() 
	{
		try 
		{
			// Bare-Metal Treiber-Laden für native, lizenzfreie SQLite-Anbindung
			Class.forName("org.sqlite.JDBC");
			dbConnection = java.sql.DriverManager.getConnection("jdbc:sqlite:highscores.db");
			
			java.sql.Statement stmt = dbConnection.createStatement();
			// Relationales Schema: Erstellt die Highscore-Tabelle mit Auto-Increment und Zeitstempel
			String sql = "CREATE TABLE IF NOT EXISTS highscores (" +
			             "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
			             "spieler_name TEXT NOT NULL, " +
			             "score INTEGER NOT NULL, " +
			             "bedenzeit DOUBLE NOT NULL, " +
			             "zeitstempel DATETIME DEFAULT CURRENT_TIMESTAMP" +
			             ");";
			stmt.execute(sql);
			stmt.close();
		}
		 catch (Exception e) 
		{
			// Spiel läuft bei Festplatten-Schreibblockaden im RAM-Modus weiter
		}
	}

	public static void speichereHighscore(String name, int score, double zeit) 
	{
		if (dbConnection == null) return;
		String sql = "INSERT INTO highscores (spieler_name, score, bedenzeit) VALUES (?, ?, ?);";
		try (java.sql.PreparedStatement pstmt = dbConnection.prepareStatement(sql)) 
		{
			pstmt.setString(1, name);
			pstmt.setInt(2, score);
			pstmt.setDouble(3, zeit);
			pstmt.executeUpdate();
		} 
		catch (Exception e) 
		{
			// Fehler abfangen ohne GUI-Crash
		}
	}

	public static void zeigeHighscoreBestenliste()
	{
		terminalPane.setText("");
		Color c64Default = new Color(104, 194, 255);
		Color gelb = Color.YELLOW;
		
		printStyled("================================================================================\n", Color.WHITE);
		printStyled("                      *** C64 ARCADE HIGHSCORE HALL OF FAME ***                 \n", gelb);
		printStyled("================================================================================\n\n", Color.WHITE);
		printStyled("  RANK  |  SPIELER NAME        |  SCORE      |  BEDENKZEIT   |  DATUM         \n", c64Default);
		printStyled("--------------------------------------------------------------------------------\n", Color.WHITE);
		
		if (dbConnection == null) 
		{
			printStyled("            DATENBANK-VERBINDUNG NICHT AKTIV (RAM-MODUS)\n", Color.RED);
			return;
		}

		try 
		{
			java.sql.Statement stmt = dbConnection.createStatement();
			// Holt die Top 10 sortiert nach der höchsten Punktzahl
			java.sql.ResultSet rs = stmt.executeQuery("SELECT spieler_name, score, bedenzeit, zeitstempel FROM highscores ORDER BY score DESC LIMIT 10;");
			
			int rang = 1;
			while (rs.next()) 
			{
				String name = rs.getString("spieler_name");
				int score = rs.getInt("score");
				double zeit = rs.getDouble("bedenzeit");
				String datum = rs.getString("zeitstempel").substring(0, 10); // Nur YYYY-MM-DD
				
				String zeile = String.format("  [%02d]   |  %-19s |  %06d     |  %6.2fs      |  %s\n", 
					rang, name.toUpperCase(), score, zeit, datum);
				
				printStyled(zeile, Color.GREEN);
				rang++;
			}
			
			if (rang == 1) 
			{
				printStyled("            NOCH KEINE EINTRÄGE IN DER DATENBANK VORHANDEN.                     \n", Color.LIGHT_GRAY);
			}
			
			rs.close();
			stmt.close();
		} 
		catch (Exception e) 
		{
			printStyled("            FEHLER BEIM LESEN DER SQL-DATENBANK.                                \n", Color.RED);
		}
		printStyled("\n================================================================================\n", Color.WHITE);
		printStyled(" DRÜCKEN SIE [SPACE] UM ZURÜCK ZUM HAUPTMENÜ ZU GELANGEN                        \n", Color.CYAN);
		printStyled("================================================================================\n", Color.WHITE);

// Ende Ebene 22
	}


// Ende Ebene 1 Master klasse 
}