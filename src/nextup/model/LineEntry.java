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
 * One row of a LineSnapshot: what a view is allowed to show about one
 * student. It has no ticket field on purpose.
 *
 * @author Aditya Banerjee (adityab7)
 * @version 2026.11.13
 */
public class LineEntry {

    private final int position;
    private final String name;
    private final String question;
    private final int minutesWaited;
    private final int estimatedWaitMinutes;

    /**
     * Creates one row.
     *
     * @param position
     *            1 for the front of the line, 0 for the student being
     *            helped
     * @param name
     *            the student's name; must not be null
     * @param question
     *            the student's question; must not be null
     * @param minutesWaited
     *            whole minutes waited so far (for the student being
     *            helped, minutes waited before being called)
     * @param estimatedWaitMinutes
     *            estimated whole minutes until this student is called, 0
     *            for the student being helped, or -1 if there is no
     *            estimate yet (nobody has been helped)
     * @throws IllegalArgumentException
     *             if position or minutesWaited is negative, the estimate
     *             is below -1, or a text value is null
     */
    public LineEntry(
        int position,
        String name,
        String question,
        int minutesWaited,
        int estimatedWaitMinutes) {
        if (position < 0 || minutesWaited < 0) {
            throw new IllegalArgumentException(
                "Position and minutes must not be negative.");
        }
        if (estimatedWaitMinutes < -1) {
            throw new IllegalArgumentException(
                "The estimate must be -1 (none yet) or more.");
        }
        if (name == null || question == null) {
            throw new IllegalArgumentException(
                "Name and question must not be null.");
        }
        this.position = position;
        this.name = name;
        this.question = question;
        this.minutesWaited = minutesWaited;
        this.estimatedWaitMinutes = estimatedWaitMinutes;
    }


    /**
     * Returns the position: 1 is the front, 0 is being helped.
     *
     * @return the position
     */
    public int getPosition() {
        return position;
    }


    /**
     * Returns the student's name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }


    /**
     * Returns the student's question. StudentView should not show this;
     * TaView does.
     *
     * @return the question
     */
    public String getQuestion() {
        return question;
    }


    /**
     * Returns the whole minutes this student has waited.
     *
     * @return minutes waited
     */
    public int getMinutesWaited() {
        return minutesWaited;
    }


    /**
     * Returns the estimated whole minutes until this student is called.
     *
     * @return the estimate, 0 for the student being helped, or -1 if
     *         there is no estimate yet
     */
    public int getEstimatedWaitMinutes() {
        return estimatedWaitMinutes;
    }
}
