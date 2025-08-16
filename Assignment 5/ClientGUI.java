import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Main GUI for client handling connection to the server and main game
 * @author Alexandre Paquette
 * @version December 15, 2022
 */
public class ClientGUI extends JFrame {
    Container contentPane;
    CardLayout card = new CardLayout();
    
    //*****************************Connection Content***************************************
    Font connectFont = new Font(Font.SERIF, Font.ROMAN_BASELINE, 20);
    
    private final JPanel connect_top_panel = new JPanel(new GridLayout(0,2));
    private final JPanel connect_panel = new JPanel(new BorderLayout());
    
    private final JLabel HOST_LABEL = new JLabel("Server address: ");
    private final JLabel PORT_LABEL = new JLabel("Server port: ");
    private final JLabel PLAYER_LABEL = new JLabel("Player name: ");

    private JTextField host_textField = new JTextField("localhost");
    private JTextField port_textField = new JTextField("14312");
    private JTextField player_textField = new JTextField();
    
    Icon connect_icon = new ImageIcon(getClass().getResource("icons\\connect.png"));
    
    private final JButton connect_button = new JButton(connect_icon);
    
    //*****************************Game Content******************************************
    private int wins = 0;
    private int losses = 0;
    private int ties = 0;
    
    private String opponentName = "";
    
    private final JPanel game_panel = new JPanel(new BorderLayout());
    private final JPanel playButton_panel = new JPanel(new GridLayout(0,3));
    private final JPanel opponentName_panel = new JPanel(new GridLayout(2,0));
    private final JPanel centerMessage_panel = new JPanel(new BorderLayout());
    private final JPanel centerVS_panel = new JPanel(new GridLayout(0,3));
    
    Icon rock_icon = new ImageIcon(getClass().getResource("icons\\rock.png"));
    Icon paper_icon = new ImageIcon(getClass().getResource("icons\\paper.png"));
    Icon scissor_icon = new ImageIcon(getClass().getResource("icons\\scissor.png"));
    Icon disconnect_icon = new ImageIcon(getClass().getResource("icons\\disconnect.png"));
    
    private final JButton rock_button = new JButton(rock_icon);
    private final JButton paper_button = new JButton(paper_icon);
    private final JButton scissors_button = new JButton(scissor_icon);
    private final JButton disconnect_button = new JButton(disconnect_icon);
    
    private JLabel gameStatus = new JLabel("WINS: "+wins+" | LOSSES: "+losses+" | TIES: "+ties, SwingConstants.CENTER);
    private JLabel message_label = new JLabel("", SwingConstants.CENTER);
    private final JLabel opponent_label = new JLabel("Opponent: ", SwingConstants.CENTER);
    private final JLabel opponentName_label = new JLabel("", SwingConstants.CENTER);
    private JLabel palyerMove_label = new JLabel("", SwingConstants.CENTER);
    private JLabel opponentMove_label = new JLabel("", SwingConstants.CENTER);
    private JLabel vs_label = new JLabel ("", SwingConstants.CENTER);
    
    //Server Content
    private Socket conxn;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String server_msg = "";
    private String player_msg = "";
    
    /**
     * ClientGUI Constructor that builds the Graphical elements used for the game.
     * @param title
     */
    public ClientGUI(String title){
        super(title);
        contentPane = this.getContentPane();
        //Connect Panel Components
        HOST_LABEL.setFont(connectFont);
        PORT_LABEL.setFont(connectFont);
        PLAYER_LABEL.setFont(connectFont);
        
        host_textField.setFont(connectFont);
        port_textField.setFont(connectFont);
        player_textField.setFont(connectFont);
        
        connect_top_panel.add(HOST_LABEL);
        connect_top_panel.add(host_textField);
        connect_top_panel.add(PORT_LABEL);
        connect_top_panel.add(port_textField);
        connect_top_panel.add(PLAYER_LABEL);
        connect_top_panel.add(player_textField);
        connect_panel.add(connect_top_panel, BorderLayout.CENTER);
        connect_panel.add(connect_button, BorderLayout.SOUTH);
        
        //Game Panel Components
        playButton_panel.add(rock_button);
        playButton_panel.add(paper_button);
        playButton_panel.add(scissors_button);
        
        opponentName_panel.add(opponent_label);
        opponentName_panel.add(opponentName_label);
        
        centerVS_panel.add(palyerMove_label);
        centerVS_panel.add(vs_label);
        centerVS_panel.add(opponentMove_label);
        
        centerMessage_panel.add(message_label, BorderLayout.NORTH);
        centerMessage_panel.add(centerVS_panel, BorderLayout.CENTER);
        
        game_panel.add(playButton_panel, BorderLayout.SOUTH);
        game_panel.add(disconnect_button, BorderLayout.WEST);
        game_panel.add(opponentName_panel, BorderLayout.EAST);
        game_panel.add(gameStatus, BorderLayout.NORTH);
        game_panel.add(centerMessage_panel, BorderLayout.CENTER);
        
        contentPane.setLayout(card);
        contentPane.add(connect_panel);
        contentPane.add(game_panel);
        
        connect_button.addActionListener((ActionEvent e) -> {
            int PORT = Integer.parseInt(port_textField.getText());
            String HOST = host_textField.getText();
            String playerName = player_textField.getText();
            
            if(!(playerName.isBlank() || HOST.isBlank() || PORT <= 0)){
                wins = 0;
                losses = 0;
                ties = 0;
                gameStatus.setText("WINS: " + wins + " | LOSSES: " + losses + " | TIES: " + ties);
                palyerMove_label.setIcon(null);
                opponentMove_label.setIcon(null);
                vs_label.setText("");
                opponentName_label.setText("");
                
                try{
                    conxn = new Socket(HOST, PORT);
                    out = new ObjectOutputStream(conxn.getOutputStream());
                    out.flush();
                    in = new ObjectInputStream(conxn.getInputStream());
                    
                    out.writeUTF(playerName);
                    out.flush();

                    card.next(contentPane);
                    message_label.setText("Waiting for player 2...");
                    
                    //listening thread
                    new Thread(){
                        @Override
                        public void run(){
                            listenToServer();
                        }
                    }.start();
                }catch(IOException ioe){
                    JOptionPane.showMessageDialog(null, "Invalid Connection!","Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        rock_button.addActionListener((ActionEvent e) -> {
            sendServerMessage("R");
        });
        paper_button.addActionListener((ActionEvent e) -> {
            sendServerMessage("P");
        });
        scissors_button.addActionListener((ActionEvent e) -> {
            sendServerMessage("S");
        });
        disconnect_button.addActionListener((ActionEvent e) -> {
            disconnect();
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we){
                disconnect();
            }
            @Override
            public void windowClosed(WindowEvent we){
                System.exit(0);
            }
        });
    }
    /**
     * Send a message to the server, then wait for the server to respond
     * @param msg message to be sent
     */
    private void sendServerMessage(String msg){
        player_msg = msg;
        try{
            out.writeUTF(msg);
            out.flush();
            //waitingMessage();
            message_label.setText("Waiting for " + opponentName);
            palyerMove_label.setIcon(null);
            opponentMove_label.setIcon(null);
            vs_label.setText("");
            new Thread(){//listening thread
                @Override
                public void run(){
                    listenToServer();
                }
            }.start();
        }catch(IOException ioe){ }
    }
    
    /**
     * Disconnect from the server by sending disconnect message and switching back
     * to the connection pane
     */
    private void disconnect(){
        card.first(contentPane);
        try{
            out.writeUTF("D");
            out.flush();
        }catch(Exception e){ }
    }
    
    /**
     * Check if the user wins by comparing player message and server message
     */
    private void checkWin(){
        if(player_msg.equals(server_msg)){//Tie
            message_label.setText("It's a Tie");
            ties++;
        }else if(//Win   
                    (player_msg.equals("P") && server_msg.equals("R")) || 
                    (player_msg.equals("R") && server_msg.equals("S")) || 
                    (player_msg.equals("S") && server_msg.equals("P"))){
            message_label.setText("You Win!!!");
            wins++;
        }else{//Loss
            message_label.setText("You Lose!!!");
            losses++;
        }
        gameStatus.setText("WINS: "+wins+" | LOSSES: "+losses+" | TIES: "+ties);
        palyerMove_label.setIcon(getIcon(player_msg));
        opponentMove_label.setIcon(getIcon(server_msg));
        vs_label.setText("VS");
        
    }
    
    /**
     * Listen for server message
     */
    private void listenToServer() {
        toggleButtons(false);
        try{
            server_msg = in.readUTF();
            switch(server_msg){
                case "R", "P", "S" -> {
                    checkWin();//playing
                }
                case "D" -> {
                    card.first(contentPane);//other player quit
                    JOptionPane.showMessageDialog(null, "Opponent left the game","Message", JOptionPane.INFORMATION_MESSAGE);
                }
                default -> {
                    //finish waiting for opponent
                    opponentName = server_msg;
                    opponentName_label.setText(opponentName);
                    message_label.setText("");
                }
            }
        }catch(IOException ioe){ }
        toggleButtons(true);
    }
    
    /**
     * Toggle buttons to be enabled or disabled
     * @param toggle Toggle bool for enabling or disabling
     */
    private void toggleButtons(Boolean toggle){
            disconnect_button.setEnabled(toggle);
            rock_button.setEnabled(toggle);
            paper_button.setEnabled(toggle);
            scissors_button.setEnabled(toggle);
    }
    
    /**
     * Returns Icon to be displayed based on player move
     * @param playerMove player move determining icon being returned
     * @return Icon based on playerMove provided
     */
    private Icon getIcon(String playerMove){
        return switch (playerMove) {
            case "R" -> rock_icon;
            case "P" -> paper_icon;
            case "S" -> scissor_icon;
            default -> new ImageIcon();
        };
    }
}
