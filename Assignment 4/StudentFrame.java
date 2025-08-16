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
import javax.swing.JOptionPane;
import java.sql.*;


/**
 * Student Record JFramed
 * @author Alexandre Paquette
 * Date: 09-24-2022
 * Last Edit: 11-27-2022
 * Description: Frame containing student record including ID, 
 * Program, First Name, and Last Name, as well as marks.
 */
public class StudentFrame extends JFrame{
    //GUI variables
    private final HashMap<String, JPanel> panels = new HashMap<>();
    private HashMap<String, JTextField> textFields = new HashMap<>();
    private HashMap<String, JButton> buttons = new HashMap<>();
    private MarksPanel panel_Marks = new MarksPanel();
    
    //Data variables
    private final ArrayList<Student> students = new ArrayList<>();
    private int index;
    
    //Database variables
    private final String DB_TYPE = "jdbc:mariadb:";
    private final String DB_ADDR = "//localhost:3306/";
    private final String DB = "studentdata";
    private final String USER = "root";
    private final String PASSWD = "";
    private final String DB_URL = DB_TYPE + DB_ADDR + DB;
    
    //Misc vars
    private Boolean editing = false;
    private Boolean validRecord = true;
        
    /**
     * Constructor for StudentFrame Class
     * @param title Title to be displayed in Frame
     */
    public StudentFrame(String title){
        super(title);

        panels.put("Record", new JPanel(new GridLayout(0,1)));
        panels.put("Action Buttons", new JPanel(new GridLayout(0,3)));
        panels.put("Content", new JPanel(new BorderLayout()));

        for(String s : new String[] {"Edit", "Add", "Delete", "Prev", "Next"}){
            buttons.put(s, new JButton(s));
            if(!s.equals("Prev") && !s.equals("Next"))
                panels.get("Action Buttons").add(buttons.get(s));
        }

        for(String s : new String[] {"ID", "Program", "First Name", "Last Name"}){
            textFields.put(s, new JTextField(10));
            panels.put(s, new JPanel(new GridLayout(0,2)));
            panels.get("Record").add(panels.get(s));
        }
        

        JPanel panel = panels.get("ID");
        panel.add(new JLabel("ID: "));
        panel.add(textFields.get("ID"));
        
        panel = panels.get("Program");
        panel.add(new JLabel("Program: "));
        panel.add(textFields.get("Program"));

        panel = panels.get("First Name");
        panel.add(new JLabel("First Name: "));
        panel.add(textFields.get("First Name"));

        panel = panels.get("Last Name");
        panel.add(new JLabel("Last Name: "));
        panel.add(textFields.get("Last Name"));

        panel = panels.get("Content");
        panel.add(panels.get("Record"), BorderLayout.CENTER);
        panel.add(panels.get("Action Buttons"), BorderLayout.SOUTH);
        panel.add(buttons.get("Next"), BorderLayout.EAST);
        panel.add(buttons.get("Prev"), BorderLayout.WEST);
        
        //Add to JFrame
        this.getContentPane().add(panels.get("Content"), BorderLayout.CENTER);
        this.getContentPane().add(panel_Marks, BorderLayout.EAST);
        
        //Set Buttons and textfields
        toggleTextFields(false);
        toggleButtons(false);
        //buttons.get("Load").setEnabled(true);
        buttons.get("Add").setEnabled(true);
        
        loadFromDB();
        
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
                validRecord = isValidRecord(textFields.get("ID").getText());
                if(validRecord){
                    Student student = students.get(index);
                    String oldID = student.getStudentID();
                    
                    //set student fields
                    student.setStudentID(textFields.get("ID").getText());
                    student.setFname(textFields.get("First Name").getText());
                    student.setLname(textFields.get("Last Name").getText());
                    student.setProgram(textFields.get("Program").getText());

                    //set student marks
                    double marks[] = panel_Marks.getMarks();
                    for(int x = 0; x < 6; x++)
                        student.setMark(x, marks[x]);
                    
                    if(editing){//edit existing record
                        executeOnDB("DELETE FROM marks WHERE studentID = '"+oldID+"';");  //delete marks associated with student
                        executeOnDB("UPDATE students SET firstName = '"+student.getFname()+"', lastName = '"+student.getLname()
                                + "', program = '"+student.getProgram()+"', studentID = '"+student.getStudentID()+"' "
                                        + "WHERE studentID = '"+oldID+"';");//update student record

                        for (double mark : student.getMarks()){//re-insert marks
                            executeOnDB("INSERT INTO marks (mark, studentID) VALUES ("+mark+", '"+student.getStudentID()+"');");
                        }
                        editing = false;
                    }else{//insert new record
                        executeOnDB("INSERT INTO students (studentID, firstName, lastName, program) "
                        + "VALUES ('" + student.getStudentID() + "', '" 
                        + student.getFname() + "', '" + student.getLname() 
                        + "', '" + student.getProgram() + "');");
                        for (double mark : student.getMarks()){
                            executeOnDB("INSERT INTO marks (mark, studentID) VALUES ("+mark+", '"+student.getStudentID()+"');");
                        }
                        executeOnDB("INSERT INTO nextid (nextID)  VALUES ("+Student.getNextNum()+");"); //set nextID
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "No new records -> ignoring duplicate value in nextID","Duplicate ID", JOptionPane.WARNING_MESSAGE);
                    if(!editing){
                        Student.setNextNum(Student.getNextNum() - 1);
                        students.remove(index);
                        index = 0;
                    }
                    validRecord = true;
                    editing = false;
                }
                reset();
                displayStudent(students.get(index));//show student record
            }else{
                toggleTextFields(true);
                toggleButtons(false);
                buttons.get("Edit").setEnabled(true);
                buttons.get("Edit").setText("Done");
                editing = true;
            }
        });
        buttons.get("Delete").addActionListener((ActionEvent e) -> {
            executeOnDB("DELETE FROM marks WHERE studentID = '" + students.get(index).getStudentID() + "';");
            executeOnDB("DELETE FROM students WHERE studentID = '" + students.get(index).getStudentID() + "';");
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
        buttons.get("Add").setEnabled(true);
        toggleTextFields(false);
        clearTextFields();
    }
    
    /**
     * Load student data from set database if data exists.
     */
    private void loadFromDB(){
        try(    Connection conxn = DriverManager.getConnection(DB_URL, USER, PASSWD);
                Statement statement = conxn.createStatement()){
            ResultSet resultSet = statement.executeQuery("SELECT studentID, firstName, lastName, program FROM students");
            
            while(resultSet.next()){
                String fname = resultSet.getString("firstName");
                String lname = resultSet.getString("lastName");
                String program = resultSet.getString("program");
                String id = resultSet.getString("studentID");
                
                ResultSet markSet = statement.executeQuery("SELECT mark FROM marks WHERE studentID = '"+id+"';");
                
                //ArrayList<Double> marks = new ArrayList<>();
                double[] marks = new double[6];
                for(int i = 0; i < 6; i++){
                    markSet.next();
                    marks[i] = markSet.getDouble("mark");
                }
                
                students.add(new Student(id, fname, lname, program, marks));
            }
            
            if(!students.isEmpty()){
                resultSet = statement.executeQuery("SELECT nextID FROM nextid ORDER BY nextID DESC;");
                resultSet.next();
                Student.setNextNum((Integer)resultSet.getObject("nextID"));
                displayStudent(students.get(0));
            }
            conxn.close();
        }catch(SQLException sqle){
            JOptionPane.showMessageDialog(null, "Database failed!","Error", JOptionPane.ERROR_MESSAGE);
            sqle.printStackTrace();
        }
    }
    
    /**
     * Execute a given sql query on the set database
     * @param sql sql command to be executed
     */
    private void executeOnDB(String sql){
        try(    Connection conxn = DriverManager.getConnection(DB_URL, USER, PASSWD);
                Statement statement = conxn.createStatement()){
            statement.executeUpdate(sql);
            conxn.close();
        }catch(SQLException sqle){
            if(!sqle.getMessage().contains("Duplicate entry")){
                JOptionPane.showMessageDialog(null, "Database failed!","Error", JOptionPane.ERROR_MESSAGE);
                sqle.printStackTrace();
            }else{
                JOptionPane.showMessageDialog(null, "No new records -> ignoring duplicate value in nextID","Duplicate ID", JOptionPane.WARNING_MESSAGE);
                validRecord = false;
            }
        }
    }
    
    /**
     * Checks if a given id is duplicated for all other students other than the current active index
     * @param id id being checked for duplicates
     * @return returns false if duplicate id is found
     */
    private boolean isValidRecord(String id){
        for(int i = 0; i < students.size(); i++){
            if(i != index && students.get(i).getStudentID().equals(id)){
                return false;
            }
        }
        
        return true;
    }
}