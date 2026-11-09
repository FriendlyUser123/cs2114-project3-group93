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
 * TEST HELPER. A clock the test sets by hand, so "Ana waited 3 minutes"
 * takes no real time. Anyone's tests can use it, for example
 * LineControllerTest:
 *
 * <pre>
 * FakeTimeSource clock = new FakeTimeSource();
 * OfficeHoursLine line = new OfficeHoursLine(clock, new Random(42));
 * clock.setMillis(120000); // two minutes later
 * </pre>
 *
 * @author Aditya Banerjee (adityab7)
 * @version 2026.11.08
 */
public class FakeTimeSource implements TimeSource {

    private long millis;

    /**
     * Creates a clock that reads 0.
     */
    public FakeTimeSource() {
        millis = 0;
    }


    /**
     * Sets the time the clock will report.
     *
     * @param newMillis
     *            the time in milliseconds
     */
    public void setMillis(long newMillis) {
        millis = newMillis;
    }


    /**
     * Moves the clock forward.
     *
     * @param minutes
     *            whole minutes to add
     */
    public void advanceMinutes(int minutes) {
        millis += minutes * 60000L;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public long nowMillis() {
        return millis;
    }
}
