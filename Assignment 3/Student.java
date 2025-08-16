
import java.io.Serializable;

/**
 * Student - a data class to be used in Assignment01, Assignment02, Assignment03
 * and assignment04
 *
 * @author <a href="mailto:James.Ronholm@CanadoreCollege.ca">James Ronholm</a>
 * @version Nov 2021 original Oct 2020
 */
public class Student implements Serializable {

    private static int nextNum = 0;
    private final static String numPrefix = "A";
    private String fname, lname, program;
    private double marks[] = new double[6];
    private String studentID;

    public Student() {
        this("first name", "last name", "undeclared", new double[6]);
    }

    public Student(String fname, String lname, String program, double[] marks) {
        studentID = String.format("%s%08d", numPrefix, ++nextNum);
        this.fname = fname;
        this.lname = lname;
        this.program = program;
        this.marks = marks; // TODO add array validation
    }

    public Student(String id, String fname, String lname, String program, double[] marks) {
        this.studentID = id;
        this.fname = fname;
        this.lname = lname;
        this.program = program;
        this.marks = marks; // TODO add array validation
    }

    /**
     * Get the student's first name
     *
     * @return
     */
    public String getFname() {
        return fname;
    }

    /**
     * Set a new firstname for the student
     *
     * @param fname
     */
    public void setFname(String fname) {
        this.fname = fname;
    }

    /**
     * Get the student's name
     *
     * @return
     */
    public String getLname() {
        return lname;
    }

    /**
     * Set a new lastname for the student
     *
     * @param lname
     */
    public void setLname(String lname) {
        this.lname = lname;
    }

    /**
     * Get the student's marks
     *
     * @return
     */
    public double[] getMarks() {
        return marks;
    }

    /**
     * Set new marks for the student
     *
     * @param marks
     */
    public void setMarks(double[] marks) {
        // TODO add array validation
        this.marks = marks;
    }

    /**
     * Get the student's marks
     *
     * @param markID the specific mark to get
     * @throws InvalidMarkException if the markID isn't between 0 and 5
     * @return the mark specified by the ID
     */
    public double getMark(int markID) throws InvalidMarkException {
        if (markID < 0 || markID > 5) {
            throw new InvalidMarkException("Invalid Student mark ID: " + markID);
        }

        return marks[markID];
    }

    /**
     * Set new marks for the student
     *
     * @param markID the specific mark to adjust
     * @param mark the new mark
     * @throws InvalidMarkException if the mark isn't between 0 and 5
     */
    public void setMark(int markID, double mark) throws InvalidMarkException {
        if (markID < 0 || markID > 5) {
            throw new InvalidMarkException("Invalid Student mark ID: " + markID);
        } else if (mark < 0 || mark > 100) {
            throw new InvalidMarkException("Invalid Student mark: " + mark);
        }
        this.marks[markID] = mark;
    }

    /**
     * Get the student's student number
     *
     * @return
     */
    public String getStudentID() {
        return studentID;
    }

    /**
     * Set a new studentID
     *
     * @param studentID
     */
    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    /**
     * Get the student's current program
     *
     * @return the program name
     */
    public String getProgram() {
        return program;
    }

    /**
     * Set a new program name for the student
     *
     * @param program the name for the new program
     */
    public void setProgram(String program) {
        this.program = program;
    }

    /**
     * Get the numeric portion of the next student number
     *
     * @return the numeric portion of the next student number
     */
    public static int getNextNum() {
        return Student.nextNum;
    }

    /**
     * Set the numeric portion for the next student number
     *
     * @param nextNum the numeric portion for the next student number
     */
    public static void setNextNum(int nextNum) {
        Student.nextNum = nextNum;
    }

    ///// End Student Class /////////////////////////////////////
    /**
     * a custom Exception for InvalidMarks
     */
    private class InvalidMarkException extends RuntimeException {

        public InvalidMarkException(String msg) {
            super(msg);
        }
    }

}
