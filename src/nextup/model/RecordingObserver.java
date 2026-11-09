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
// (Anthropic) to write this test helper from our Deliverable 2 test plan.
// After using this tool, I reviewed and edited the content as needed to
// ensure its accuracy and take full responsibility for the content in
// relation to grading. I understand that I am responsible for being able to
// complete this work without the use of assistance.

package nextup.model;

/**
 * TEST HELPER. A LineObserver that just counts updates and keeps the last
 * snapshot, so a test can check "exactly one update after a join" without
 * any view.
 *
 * @author Aditya Banerjee (adityab7)
 * @version 2026.11.08
 */
public class RecordingObserver implements LineObserver {

    private int updateCount;
    private LineSnapshot last;

    /**
     * Creates an observer that has seen nothing.
     */
    public RecordingObserver() {
        updateCount = 0;
        last = null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void update(LineSnapshot snapshot) {
        updateCount++;
        last = snapshot;
    }


    /**
     * Returns how many times update was called.
     *
     * @return the update count
     */
    public int getUpdateCount() {
        return updateCount;
    }


    /**
     * Returns the most recent snapshot.
     *
     * @return the last snapshot, or null if none yet
     */
    public LineSnapshot getLast() {
        return last;
    }
}
