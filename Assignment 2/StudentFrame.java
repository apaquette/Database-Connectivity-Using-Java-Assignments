import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Student Record JFramed
 * @author Alexandre Paquette
 * Date: 09-24-2022
 * Last Edit: 10-13-2022
 * Description: Frame containing student record including ID, 
 * Program, First Name, and Last Name, as well as marks.
 */
public class StudentFrame extends JFrame{
    private final HashMap<String, JPanel> panels = new HashMap<>();
    private HashMap<String, JTextField> textFields = new HashMap<>();
    private HashMap<String, JButton> buttons = new HashMap<>(); 
    private MarksPanel panel_Marks = new MarksPanel();
    private ArrayList<Student> students;
    private int index;
        
    /**
     * Constructor for StudentFrame Class
     * @param title Title to be displayed in Frame
     */
    public StudentFrame(String title){
        super(title);

        panels.put("Record", new JPanel(new GridLayout(0,1)));
        panels.put("Action Buttons", new JPanel(new GridLayout(0,5)));
        panels.put("Content", new JPanel(new BorderLayout()));

        for(String s : new String[] {"Load", "Edit", "Add", "Save", "Delete", "Prev", "Next"}){
            buttons.put(s, new JButton(s));
            if(!s.equals("Prev") && !s.equals("Next"))
                panels.get("Action Buttons").add(buttons.get(s));
        }

        for(String s : new String[] {"ID", "Program", "First Name", "Last Name"}){
            textFields.put(s, new JTextField(10));
            panels.put(s, new JPanel(new GridLayout(0,2)));
            panels.get("Record").add(panels.get(s));
        }
        
        panels.get("ID").add(new JLabel("ID: "));
        panels.get("ID").add(textFields.get("ID"));
        panels.get("Program").add(new JLabel("Program: "));
        panels.get("Program").add(textFields.get("Program"));
        panels.get("First Name").add(new JLabel("First Name: "));
        panels.get("First Name").add(textFields.get("First Name"));
        panels.get("Last Name").add(new JLabel("Last Name: "));
        panels.get("Last Name").add(textFields.get("Last Name"));
        panels.get("Content").add(panels.get("Record"), BorderLayout.CENTER);
        panels.get("Content").add(panels.get("Action Buttons"), BorderLayout.SOUTH);
        panels.get("Content").add(buttons.get("Next"), BorderLayout.EAST);
        panels.get("Content").add(buttons.get("Prev"), BorderLayout.WEST);
        
        //Add to JFrame
        this.getContentPane().add(panels.get("Content"), BorderLayout.CENTER);
        this.getContentPane().add(panel_Marks, BorderLayout.EAST);
        
        //Set Buttons and textfields
        toggleTextFields(false);
        toggleButtons(false);
        buttons.get("Load").setEnabled(true);
        buttons.get("Add").setEnabled(true);
        
        //Student list
        students = new ArrayList<>();
        
        //Action Listeners
        buttons.get("Add").addActionListener((ActionEvent e) -> {
            //Generate new student and set index to last item in collection
            students.add(new Student());
            index = students.size() - 1;
            
            //set Button and Textfields
            toggleButtons(false);
            buttons.get("Edit").setEnabled(true);
            buttons.get("Edit").setText("Done");
            toggleTextFields(true);
            clearTextFields();
            
            //Display Generated Student ID and set focus to Program text field
            textFields.get("ID").setText(students.get(index).getStudentID());
            textFields.get("Program").requestFocus();
        });
        buttons.get("Edit").addActionListener((ActionEvent e) -> {
            if(buttons.get("Edit").getText().equals("Done")){
                Student student = students.get(index);

                //set student fields
                student.setStudentID(textFields.get("ID").getText());
                student.setFname(textFields.get("First Name").getText());
                student.setLname(textFields.get("Last Name").getText());
                student.setProgram(textFields.get("Program").getText());
                
                //set student marks
                double marks[] = panel_Marks.getMarks();
                for(int x = 0; x < 6; x++)
                    student.setMark(x, marks[x]);

                reset();
                displayStudent(student);//show student record
            }else{
                toggleTextFields(true);
                toggleButtons(false);
                buttons.get("Edit").setEnabled(true);
                buttons.get("Edit").setText("Done");
            }
        });
        buttons.get("Delete").addActionListener((ActionEvent e) -> {
            students.remove(index);//remove student at current index
            if(!students.isEmpty()){//display first student if student(s) remain
                displayStudent(students.get(0));
                index = 0;
            }else{//reset if no students remain
                reset();
            }
        });
        buttons.get("Prev").addActionListener((ActionEvent e) -> { displayStudent(students.get(--index));});
        buttons.get("Next").addActionListener((ActionEvent e) -> { displayStudent(students.get(++index));});
    }
    
    /**
     * Display fields of a Student Object
     * @param s Student Object to be displayed
     */
    void displayStudent(Student s){
        //Assign student fields to appropriate text field
        textFields.get("ID").setText(s.getStudentID());
        textFields.get("Program").setText(s.getProgram());
        textFields.get("First Name").setText(s.getFname());
        textFields.get("Last Name").setText(s.getLname());
        panel_Marks.setMarks(s.getMarks());
        
        //Enable all buttons
        toggleButtons(true);

        if(s == students.get(students.size()-1))//disable next if last item in collection
            buttons.get("Next").setEnabled(false);
        if(s == students.get(0))//disable previous if first item in collection
            buttons.get("Prev").setEnabled(false);
    }
    
    /**
     * Enables or disables all buttons
     * @param toggle Boolean to enable or disable all buttons
     */
    private void toggleButtons(boolean toggle){
        for(HashMap.Entry<String, JButton> button : buttons.entrySet())
            button.getValue().setEnabled(toggle);
    }
    
    /**
     * Enable or disable all text fields
     * @param toggle Boolean to enable or disable all Text Fields
     */
    private void toggleTextFields(boolean toggle){
        for(HashMap.Entry<String, JTextField> field : textFields.entrySet())
            field.getValue().setEditable(toggle);
        
        panel_Marks.toggleTextFields(toggle);
    }
    
    /**
     * Clears text in text fields
     */
    private void clearTextFields(){
        for(HashMap.Entry<String, JTextField> field : textFields.entrySet())
            field.getValue().setText("");
        
        panel_Marks.clearTextFields();
    }
    
    /**
     * Resets all settings to default. Buttons are disabled except for Load and
     * Add. Text fields are disabled and text is cleared.
     */
    private void reset(){
        toggleButtons(false);
        buttons.get("Edit").setText("Edit");
        buttons.get("Load").setEnabled(true);
        buttons.get("Add").setEnabled(true);
        toggleTextFields(false);
        clearTextFields();
    }
}