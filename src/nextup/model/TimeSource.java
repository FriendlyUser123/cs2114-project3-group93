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
 * Where the model gets the current time. Runner passes the real clock
 * (System::currentTimeMillis), and tests pass a FakeTimeSource they can move
 * forward by hand, so a test of "waited 2 minutes" does not take 2 minutes.
 *
 * @author Aditya Banerjee (adityab7)
 * @version 2026.11.08
 */
public interface TimeSource {

    /**
     * Returns the current time.
     *
     * @return the current time in milliseconds
     */
    long nowMillis();
}
