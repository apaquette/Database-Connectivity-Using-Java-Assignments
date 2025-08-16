
import javax.swing.JFrame;

/**
 * Main Rock-Paper-Scissors game that opens the GUI from ClientGUI
 * @author Alexandre Paquette
 * @version December 15, 2022
 */
public class RockPaperScissors {

    /**
     * Main method for Rock-Paper-Scissors game
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ClientGUI app = new ClientGUI("Rock | Paper | Scissors");
        app.setSize(475, 250);
        app.setVisible(true);
        app.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}
