import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.nio.InvalidMarkException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Marks Panel JPanel
 * @author Alexandre Paquette
 * Date: 09-25-2022
 * Last Edit: 09-25-2022
 * Description: JPanel containing 6 student marks
 */
public class MarksPanel extends JPanel {
    private JLabel label_Marks = new JLabel("Marks");
    private JTextField[] textField_Marks = new JTextField[6];

    /**
     * Constructor for MarksPanel Class
     */
    public MarksPanel() {
        this.setLayout(new BorderLayout());
        this.add(label_Marks, BorderLayout.NORTH);
        
        JPanel panel_MarksTextFields = new JPanel(new GridLayout(4,2));
        
        for(int x = 0; x < textField_Marks.length; x++){
            textField_Marks[x] = new JTextField(4);
            panel_MarksTextFields.add(textField_Marks[x]);
            textField_Marks[x].setEditable(false);
        }
        this.add(panel_MarksTextFields);
    }
    
    /**
     * Function to display marks of a double array
     * @param marks[] Double marks array containing student marks
     */
    void setMarks(double marks[]){
        for(int x = 0; x < marks.length; x++){
            textField_Marks[x].setText(String.valueOf(marks[x]));
        }
    }
    /**
     * Enables or disables text fields based on Boolean provided.
     * @param toggle Boolean to enable or disable Text Fields
     */
    void toggleTextFields(boolean toggle){
        for(JTextField field : textField_Marks){
            field.setEditable(toggle);
        }
    }
    
    /**
     * Empties text fields
     */
    void clearTextFields(){
        for(JTextField field : textField_Marks){
            field.setText("");
        }
    }
    
    /**
     * Returns array of marks based on values entered in text fields.
     * @return Array of Marks
     */
    double[] getMarks(){
        double marks[] = new double[6];
        for(int x = 0; x < 6; x++){
            //add data validation?
            try{
                marks[x] = (textField_Marks[x].getText().equals("")) ? 0: Double.parseDouble(textField_Marks[x].getText());
                if(marks[x] < 0)
                    marks[x] = 0;
                if(marks[x] > 100)
                    marks[x] = 100;
            }catch (Exception e){
                marks[x] = 0;
            }
        }
        return marks;
    }
}
