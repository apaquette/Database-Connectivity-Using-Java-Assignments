import javax.swing.JFrame;

/**
 * StudentRecords application version 1 - just a GUI.
 * This version is intended to build the GUI. It hard codes one student object
 * to be displayed in the GUI. Future versions will extend the program to
 * retrieve students from a file.
 * @author <a href="mailto:James.Ronholm@CanadoreCollege.ca">James Ronholm</a>
 * @version Oct 4, 2020
 */
public class StudentRecords {

    /**
     * Executable method to start the program
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        StudentFrame app = new StudentFrame("Student Records");
        app.setSize(475, 150);
        app.setVisible(true);
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}