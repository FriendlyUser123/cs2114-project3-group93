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
// (Anthropic) to write this interface from our Deliverable 2 specification.
// After using this tool, I reviewed and edited the content as needed to
// ensure its accuracy and take full responsibility for the content in
// relation to grading. I understand that I am responsible for being able to
// complete this work without the use of assistance.

package nextup.model;

/**
 * Anything that wants to hear when the line changes. StudentView and TaView
 * implement this. The model only knows this interface, never a view class,
 * which is what lets the model run and be tested with no view at all
 * (the Observer pattern from the Week 10 Counter example).
 *
 * @author Aditya Banerjee (adityab7)
 * @version 2026.11.08
 */
public interface LineObserver {

    /**
     * Called by OfficeHoursLine once when the observer is added, and again
     * after every successful change to the line.
     *
     * @param snapshot
     *            a read-only copy of the line; never null
     */
    void update(LineSnapshot snapshot);
}
