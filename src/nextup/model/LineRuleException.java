// Project 3 fall 2026
// Virginia Tech Honor Code Pledge:
//
// As a Hokie, I will conduct myself with honor and integrity at all times.
// I will not lie, cheat, or steal, nor will I accept the actions of those who
// do.
// -- Aditya Banerjee (adityab7)

// LLM Statement:
//
// During the preparation of this assignment, I, Aditya Banerjee, used Claude
// (Anthropic) to write this class from our Deliverable 2 specification.
// After using this tool, I reviewed and edited the content as needed to
// ensure its accuracy and take full responsibility for the content in
// relation to grading. I understand that I am responsible for being able to
// complete this work without the use of assistance.

package nextup.model;

/**
 * Thrown by OfficeHoursLine when an action breaks a rule of the line, such
 * as joining twice or calling next on an empty line. The message is written
 * for the user, so the controller can show it as is. When this is thrown,
 * the line has not changed.
 *
 * It is unchecked (a RuntimeException) so model methods do not need a
 * throws clause, the same way IllegalArgumentException works.
 *
 * @author Aditya Banerjee (adityab7)
 * @version 2026.11.08
 */
public class LineRuleException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception with the message to show the user.
     *
     * @param message
     *            what went wrong, for example "No one is waiting"
     */
    public LineRuleException(String message) {
        super(message);
    }
}
