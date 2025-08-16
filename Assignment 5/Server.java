
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server clack for Rock-Paper-Scissors game. Handles connection between two
 * players and passes messages between them. Also handles socket disconnection
 * @author Alexandre Paquette
 * @version December 15, 2022
 */
public class Server{
    private static Socket playerOne;
    private static Socket playerTwo;
    private static ServerSocket server;
    private static ObjectOutputStream playerOne_out, playerTwo_out;
    private static ObjectInputStream playerOne_in, playerTwo_in;
    /**
     * Main method for Rock-Paper-Scissors server
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        final int PORT = 14312;
        String playerOne_throw;
        String playerTwo_throw;
        while(true){
            try{
                System.out.println("server running");
                server = new ServerSocket(PORT);
                playerOne_throw = "";
                playerTwo_throw = "";
                
                System.out.println("waiting for player 1...");
                playerOne = server.accept();    //wait for player to connect
                System.out.println("player 1 connected");
                
                playerOne_out = new ObjectOutputStream(playerOne.getOutputStream());
                playerOne_out.flush();
                playerOne_in = new ObjectInputStream(playerOne.getInputStream());
                String playerOneName = playerOne_in.readUTF();
                
                System.out.println("waiting for player 2...");
                playerTwo = server.accept();    //wait for second player to connect
                System.out.println("player 2 connected");
                
                playerTwo_out = new ObjectOutputStream(playerTwo.getOutputStream());
                playerTwo_out.flush();
                playerTwo_in = new ObjectInputStream(playerTwo.getInputStream());
                
                String playerTwoName = playerTwo_in.readUTF();
                
                playerOne_out.writeUTF(playerTwoName);
                playerOne_out.flush();
                playerTwo_out.writeUTF(playerOneName);
                playerTwo_out.flush();
                
                //playing game
                while(!(playerOne_throw.equals("D") || playerTwo_throw.equals("D"))){
                    playerOne_throw = playerOne_in.readUTF();
                    playerTwo_throw = playerTwo_in.readUTF();

                    if(playerOne_throw.equals("D") || playerTwo_throw.equals("D")){
                        resetServer();
                    }else{
                        playerOne_out.writeUTF(playerTwo_throw);
                        playerOne_out.flush();
                        playerTwo_out.writeUTF(playerOne_throw);
                        playerTwo_out.flush();
                    }
                }
            }catch(IOException ioe){
                System.out.println("IOException caught");
                resetServer();
            }
        }
    }
    
    /**
     * Resets server after a disconnection is detected
     */
    public static void resetServer(){
        //attempt sending disconnect to players
        try{
            playerOne_out.writeUTF("D");
            playerOne_out.flush();
        }catch(IOException ioe){ }
        try{
            playerTwo_out.writeUTF("D");
            playerTwo_out.flush();
        }catch(IOException ioe){ }
        
        //disconnect sockets
        try{
            playerOne.close();
            System.out.println("Closed Player One Socket");
        }catch(IOException ioe) {}
        try{
            playerTwo.close();
            System.out.println("Closed Player Two Socket");
        }catch(IOException ioe){}
        try{
            server.close();
            System.out.println("Closed Server Socket");
        }catch(IOException ioe){ }
    }
}
