// ====================================================================================
// AtlasArcadePortscanner 
// Portscanner zum schnellen Scannen der Ports, um das Spiel einfach zu starten
// Von Nicky Leonora 
// ====================================================================================

// ====================================================================================
// Ebene 0: Import-Dateien 
// ====================================================================================

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

// ====================================================================================
// Ebene 1
// ====================================================================================

public class AtlasArcadePortscanner 
{
    public static void main(String[] args) 
    {
        // Dynamische Bibliothek für die offenen Ports
        List<Integer> offenePortsBibliothek = new ArrayList<>();
        
        // Scan-Bereich: ab Port 1024 
        int startPort = 1024;
        int maxPort = 65535;
        
        // Pfad für den Report zum Windows-Desktop
        String userHome = System.getProperty("user.home");
        File ergebnisDatei = new File(userHome + File.separator + "Desktop", "ergebnis.txt");

        // Iterativer Scan-Loop
        for (int port = startPort; port <= maxPort; port++) 
        {
            // Sobald 10 offene Ports in der Bibliothek registriert sind
            if (offenePortsBibliothek.size() >= 10) 
            {
                break;
            }

            try (ServerSocket testSocket = new ServerSocket(port)) 
            {
                // Socket-Bindung erfolgreich -> Port ist auf Betriebssystem-Ebene frei
                offenePortsBibliothek.add(port);
            } 
            catch (Exception e) 
            {
                // Port blockiert durch restriktive Firewall-Regeln oder Systemdienste -> Überspringen
            }
        }

        // Ausgabe-Datei vorbereiten
        StringBuilder report = new StringBuilder();
        report.append("==================================================\n");
        report.append("        ATLAS ARCADE PORTSCANNER REPORT          \n");
        report.append("==================================================\n\n");
        report.append("Gefundene freie unprivilegierte Ports (No-Admin):\n\n");

        if (offenePortsBibliothek.isEmpty()) 
        {
            report.append("WARNUNG: Keine freien Ports im unprivilegierten Bereich gefunden!\n");
            report.append("Überprüfe die Hardening-Regeln deines Inselsystems.\n");
        } 
        else 
        {
            for (int i = 0; i < offenePortsBibliothek.size(); i++) 
            {
                report.append(String.format("Option %02d -> PORT: %d\n", (i + 1), offenePortsBibliothek.get(i)));
            }
        }

        report.append("\n==================================================\n");
        report.append("Status: Scan bei 10 Treffern erfolgreich beendet.\n");
        report.append("Nutze diese Ports für das Kapschefsky-Netzwerkprotokoll.\n");
        report.append("==================================================\n");
        // Lädt das Atlas-Triforce für das Scanner-GUI-Fenster
        java.io.File logoDatei = new java.io.File("atlas_logo.png");
        javax.swing.ImageIcon atlasIcon = logoDatei.exists() ? new javax.swing.ImageIcon("atlas_logo.png") : null;

        // Schreiben auf den Desktop und Dialog-Ausgabe
        try (PrintWriter writer = new PrintWriter(new FileWriter(ergebnisDatei))) 
        {
            writer.print(report.toString());
            
            // Zeigt das Pop-up mit Logo an
            JOptionPane.showMessageDialog(null, 
                "Scan erfolgreich beendet! 10 freie Ports isoliert.\nDie Datei 'ergebnis.txt' liegt auf deinem Desktop.", 
                "ATLAS ARCADE PORTSCANNER", JOptionPane.INFORMATION_MESSAGE, 
                atlasIcon);
        } 
        catch (Exception e) 
        {
            JOptionPane.showMessageDialog(null, 
                "Kritischer Fehler beim Schreiben des Reports.", 
                "ATLAS CRITICAL ERROR", JOptionPane.ERROR_MESSAGE, 
                atlasIcon);
        }
    }

// ====================================================================================
// Ende Ebene 1 
// ====================================================================================
}
