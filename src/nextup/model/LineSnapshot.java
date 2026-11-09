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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A read-only copy of the line at one moment. This is the only line data
 * that leaves the model: OfficeHoursLine sends one to every LineObserver
 * after each change. Changing it, or the list it was built from, cannot
 * change the line.
 *
 * @author Aditya Banerjee (adityab7)
 * @version 2026.11.08
 */
public class LineSnapshot {

    private final LineEntry nowHelping;
    private final List<LineEntry> waiting;
    private final int helpedCount;
    private final double averageWaitMinutes;
    private final boolean closed;

    /**
     * Creates a snapshot. The list is copied, so later changes to it do not
     * change this snapshot.
     *
     * @param nowHelping
     *            the student being helped, or null if nobody is
     * @param waiting
     *            the waiting students, front first; must not be null
     * @param helpedCount
     *            how many students have been finished this session
     * @param averageWaitMinutes
     *            average wait (join to called) of finished students
     * @param closed
     *            whether the TA has closed the line
     * @throws IllegalArgumentException
     *             if waiting is null
     */
    public LineSnapshot(
        LineEntry nowHelping,
        List<LineEntry> waiting,
        int helpedCount,
        double averageWaitMinutes,
        boolean closed) {
        if (waiting == null) {
            throw new IllegalArgumentException(
                "The waiting list must not be null.");
        }
        this.nowHelping = nowHelping;
        this.waiting = Collections.unmodifiableList(
            new ArrayList<LineEntry>(waiting));
        this.helpedCount = helpedCount;
        this.averageWaitMinutes = averageWaitMinutes;
        this.closed = closed;
    }


    /**
     * Returns the student being helped.
     *
     * @return the entry at position 0, or null if nobody is being helped
     */
    public LineEntry getNowHelping() {
        return nowHelping;
    }


    /**
     * Returns the waiting students, front first. The list cannot be
     * changed: add, remove, and clear throw
     * UnsupportedOperationException.
     *
     * @return the waiting students; empty, never null
     */
    public List<LineEntry> getWaiting() {
        return waiting;
    }


    /**
     * Returns how many students were finished this session.
     *
     * @return the helped count
     */
    public int getHelpedCount() {
        return helpedCount;
    }


    /**
     * Returns the average wait of finished students, in minutes.
     *
     * @return the average wait, or 0.0 if nobody has been helped
     */
    public double getAverageWaitMinutes() {
        return averageWaitMinutes;
    }


    /**
     * Returns whether the TA has closed the line.
     *
     * @return true if the session is over
     */
    public boolean isClosed() {
        return closed;
    }
}
