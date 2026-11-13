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
// (Anthropic) to write these tests from our Deliverable 2 test plan. After
// using this tool, I reviewed and edited the content as needed to ensure its
// accuracy and take full responsibility for the content in relation to
// grading. I understand that I am responsible for being able to complete this
// work without the use of assistance.

package nextup.model;

import java.util.List;
import java.util.Random;

/**
 * Tests OfficeHoursLine with no view, no controller, and no Scanner: a fake
 * clock, a fixed Random(42), and a RecordingObserver. One test method per
 * row of the D2 test plan.
 *
 * @author Aditya Banerjee (adityab7)
 * @version 2026.11.13
 */
public class OfficeHoursLineTest extends student.TestCase {

    private FakeTimeSource clock;
    private OfficeHoursLine line;
    private RecordingObserver observer;

    /**
     * Something that might throw. Lets one helper check every exception.
     */
    private interface Action {
        /**
         * Runs the action.
         */
        void run();
    }

    /**
     * Creates a line at time 0 with one observer before each test.
     */
    public void setUp() {
        clock = new FakeTimeSource();
        line = new OfficeHoursLine(clock, new Random(42));
        observer = new RecordingObserver();
        line.addObserver(observer);
    }


    /**
     * Runs an action and returns what it threw.
     *
     * @param action
     *            the action
     * @return the exception, or null if nothing was thrown
     */
    private Exception thrownBy(Action action) {
        try {
            action.run();
        }
        catch (Exception exception) {
            return exception;
        }
        return null;
    }


    /**
     * Checks that an action throws LineRuleException with a message, sends
     * no update, and leaves the waiting count unchanged.
     *
     * @param action
     *            the action
     * @param message
     *            the expected message
     */
    private void assertRuleBroken(Action action, String message) {
        int updates = observer.getUpdateCount();
        int size = line.getSnapshot().getWaiting().size();
        Exception thrown = thrownBy(action);
        assertTrue(thrown instanceof LineRuleException);
        assertEquals(message, thrown.getMessage());
        assertEquals(updates, observer.getUpdateCount());
        assertEquals(size, line.getSnapshot().getWaiting().size());
    }


    /**
     * Returns a string of n copies of "a".
     *
     * @param n
     *            the length
     * @return the string
     */
    private String repeat(int n) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < n; i++) {
            builder.append('a');
        }
        return builder.toString();
    }


    /**
     * Constructor: new line is empty and open; null arguments rejected.
     */
    public void testConstructor() {
        LineSnapshot snap = line.getSnapshot();
        assertTrue(snap.getWaiting().isEmpty());
        assertNull(snap.getNowHelping());
        assertEquals(0, snap.getHelpedCount());
        assertFalse(snap.isClosed());

        assertTrue(thrownBy(() -> new OfficeHoursLine(null,
            new Random())) instanceof IllegalArgumentException);
        assertTrue(thrownBy(() -> new OfficeHoursLine(clock,
            null)) instanceof IllegalArgumentException);
    }


    /**
     * addObserver: one update right away, then one per change; null
     * rejected.
     */
    public void testAddObserver() {
        assertEquals(1, observer.getUpdateCount());
        assertNotNull(observer.getLast());
        line.join("Ana", "Recursion?");
        assertEquals(2, observer.getUpdateCount());

        assertTrue(thrownBy(() -> line.addObserver(
            null)) instanceof IllegalArgumentException);
    }


    /**
     * join: tickets are in range and different; positions go 1, 2.
     */
    public void testJoin() {
        int ana = line.join("Ana", "Recursion?");
        assertTrue(ana >= 1000 && ana <= 9999);
        assertEquals(1, line.positionOf(ana));
        assertEquals(2, observer.getUpdateCount());

        int ben = line.join("Ben", "Generics?");
        assertTrue(ben != ana);
        assertEquals(2, line.positionOf(ben));
        LineEntry first = observer.getLast().getWaiting().get(0);
        assertEquals("Ana", first.getName());
        assertEquals("Recursion?", first.getQuestion());
    }


    /**
     * join: every bad-input case from the scope is refused and changes
     * nothing.
     */
    public void testJoinBadInput() {
        assertRuleBroken(() -> line.join(" ", "Q"), "Name can't be blank.");
        assertRuleBroken(() -> line.join(null, "Q"), "Name can't be blank.");
        assertRuleBroken(() -> line.join("\n\t", "Q"),
            "Name can't be blank.");
        assertRuleBroken(() -> line.join("Ana", "  "),
            "Question can't be blank.");
        assertRuleBroken(() -> line.join(repeat(31), "Q"),
            "Names can be at most 30 characters.");
        assertRuleBroken(() -> line.join("Ana", repeat(121)),
            "Questions can be at most 120 characters.");

        // Exactly at the limits is fine.
        line.join(repeat(30), repeat(120));

        line.join("Ana", "Recursion?");
        assertRuleBroken(() -> line.join(" ana ", "Other"),
            "You're already in line at #2.");
    }


    /**
     * join: control characters are removed before checking.
     */
    public void testJoinCleansText() {
        line.join("A\tna", "Line\none");
        LineEntry entry = line.getSnapshot().getWaiting().get(0);
        assertEquals("Ana", entry.getName());
        assertEquals("Lineone", entry.getQuestion());
    }


    /**
     * join: the 51st student is refused; join after close is refused;
     * the student being helped cannot join again.
     */
    public void testJoinFullClosedHelping() {
        line.join("Ana", "Q");
        line.callNext();
        assertRuleBroken(() -> line.join("ANA", "Q"),
            "You're being helped right now.");
        line.finishCurrent();

        for (int i = 0; i < 50; i++) {
            line.join("Student " + i, "Q");
        }
        assertRuleBroken(() -> line.join("Late", "Q"), "The line is full.");

        OfficeHoursLine other = new OfficeHoursLine(clock, new Random(1));
        other.closeLine();
        Exception thrown = thrownBy(() -> other.join("Ana", "Q"));
        assertEquals("The line is closed.", thrown.getMessage());
    }


    /**
     * positionOf: moves up after a leave; unknown and being-helped tickets
     * give -1.
     */
    public void testPositionOf() {
        int ana = line.join("Ana", "Q");
        int ben = line.join("Ben", "Q");
        assertEquals(2, line.positionOf(ben));
        line.leave(ana);
        assertEquals(1, line.positionOf(ben));
        assertEquals(-1, line.positionOf(57));
        line.callNext();
        assertEquals(-1, line.positionOf(ben));
    }


    /**
     * leave: the middle student leaves and the back student moves up.
     */
    public void testLeave() {
        line.join("Ana", "Q");
        int ben = line.join("Ben", "Q");
        line.join("Cal", "Q");
        line.leave(ben);
        List<LineEntry> waiting = observer.getLast().getWaiting();
        assertEquals(2, waiting.size());
        assertEquals("Ana", waiting.get(0).getName());
        assertEquals("Cal", waiting.get(1).getName());
        assertEquals(2, waiting.get(1).getPosition());
    }


    /**
     * leave: an unknown ticket, and the same ticket twice, are refused.
     */
    public void testLeaveBadTicket() {
        assertRuleBroken(() -> line.leave(57), "No ticket #57 in line");
        int ana = line.join("Ana", "Q");
        line.leave(ana);
        assertRuleBroken(() -> line.leave(ana),
            "No ticket #" + ana + " in line");
    }


    /**
     * callNext: the front student moves to position 0.
     */
    public void testCallNext() {
        line.join("Ana", "Q");
        line.join("Ben", "Q");
        line.callNext();
        LineSnapshot snap = observer.getLast();
        assertEquals("Ana", snap.getNowHelping().getName());
        assertEquals(0, snap.getNowHelping().getPosition());
        assertEquals("Ben", snap.getWaiting().get(0).getName());
        assertEquals(1, snap.getWaiting().get(0).getPosition());
    }


    /**
     * callNext: empty line, already helping, and closed are refused.
     */
    public void testCallNextBadState() {
        assertRuleBroken(() -> line.callNext(), "No one is waiting");
        line.join("Ana", "Q");
        int ben = line.join("Ben", "Q");
        line.callNext();
        assertRuleBroken(() -> line.callNext(), "Finish with Ana first.");
        assertEquals("Ana", line.getSnapshot().getNowHelping().getName());

        line.finishCurrent();
        line.leave(ben);
        line.closeLine();
        assertRuleBroken(() -> line.callNext(), "The line is closed.");
    }


    /**
     * finishCurrent: stats are counted from join time to called time.
     */
    public void testFinishCurrent() {
        line.join("Ana", "Q");
        clock.setMillis(120000);
        line.callNext();
        clock.setMillis(500000); // time spent being helped does not count
        line.finishCurrent();
        LineSnapshot snap = observer.getLast();
        assertEquals(1, snap.getHelpedCount());
        assertEquals(2.0, snap.getAverageWaitMinutes(), 0.001);
        assertNull(snap.getNowHelping());
    }


    /**
     * finishCurrent and putBack with nobody being helped are refused.
     */
    public void testNobodyHelped() {
        assertRuleBroken(() -> line.finishCurrent(),
            "No one is being helped");
        assertRuleBroken(() -> line.putBack(), "No one is being helped");
        assertEquals(0, line.getSnapshot().getHelpedCount());
    }


    /**
     * putBack: the student goes back to the FRONT with the same ticket, and
     * the wait keeps counting from the original join.
     */
    public void testPutBack() {
        int ana = line.join("Ana", "Q");
        line.join("Ben", "Q");
        clock.setMillis(60000);
        line.callNext();
        line.putBack();
        List<LineEntry> waiting = observer.getLast().getWaiting();
        assertEquals("Ana", waiting.get(0).getName());
        assertEquals("Ben", waiting.get(1).getName());
        assertEquals(1, line.positionOf(ana));

        clock.setMillis(180000);
        line.callNext();
        line.finishCurrent();
        assertEquals(3.0, line.getSnapshot().getAverageWaitMinutes(), 0.001);
    }


    /**
     * putBack: allowed with 50 waiting (making 51), but a new join is not.
     */
    public void testPutBackPastCap() {
        for (int i = 0; i < 50; i++) {
            line.join("Student " + i, "Q");
        }
        line.callNext();
        line.join("Newcomer", "Q");
        assertEquals(50, line.getSnapshot().getWaiting().size());
        line.putBack();
        assertEquals(51, line.getSnapshot().getWaiting().size());
        assertRuleBroken(() -> line.join("Late", "Q"), "The line is full.");
    }


    /**
     * closeLine: final stats go out in the last update; join refused.
     */
    public void testCloseLine() {
        line.join("Ana", "Q");
        clock.setMillis(60000);
        line.callNext();
        line.finishCurrent();
        line.closeLine();
        LineSnapshot snap = observer.getLast();
        assertTrue(snap.isClosed());
        assertEquals(1, snap.getHelpedCount());
        assertEquals(1.0, snap.getAverageWaitMinutes(), 0.001);
        assertTrue(thrownBy(() -> line.join("Ben",
            "Q")) instanceof LineRuleException);
    }


    /**
     * closeLine: refused while helping and when already closed; average is
     * 0.0 with nobody helped.
     */
    public void testCloseLineBadState() {
        line.join("Ana", "Q");
        line.callNext();
        assertRuleBroken(() -> line.closeLine(), "Finish with Ana first.");
        assertFalse(line.getSnapshot().isClosed());
        line.putBack();

        line.closeLine();
        assertEquals(0.0, line.getSnapshot().getAverageWaitMinutes(), 0.001);
        assertRuleBroken(() -> line.closeLine(),
            "The line is already closed.");
    }


    /**
     * getSnapshot: minutes waited follow the clock; the list is read-only.
     */
    public void testGetSnapshot() {
        line.join("Ana", "Recursion?");
        clock.advanceMinutes(3);
        LineEntry entry = line.getSnapshot().getWaiting().get(0);
        assertEquals(1, entry.getPosition());
        assertEquals("Ana", entry.getName());
        assertEquals("Recursion?", entry.getQuestion());
        assertEquals(3, entry.getMinutesWaited());

        LineSnapshot snap = line.getSnapshot();
        assertTrue(thrownBy(() -> snap.getWaiting()
            .clear()) instanceof UnsupportedOperationException);
        assertEquals(1, line.getSnapshot().getWaiting().size());
    }


    /**
     * Tickets are never reused, even after everyone leaves.
     */
    public void testTicketsNotReused() {
        boolean[] seen = new boolean[10000];
        for (int round = 0; round < 40; round++) {
            for (int i = 0; i < 50; i++) {
                int ticket = line.join("S" + i, "Q");
                assertFalse(seen[ticket]);
                seen[ticket] = true;
            }
            for (int i = 0; i < 50; i++) {
                line.callNext();
                line.finishCurrent();
            }
        }
        assertEquals(2000, line.getSnapshot().getHelpedCount());
    }


    /**
     * Joins Ana, Ben, and Cal, then helps Ana for 4 minutes and Ben for
     * 6 minutes. Used by the estimate tests.
     */
    private void helpTwoFor4And6Minutes() {
        line.join("Ana", "Q");
        line.join("Ben", "Q");
        line.join("Cal", "Q");
        line.join("Dee", "Q");
        line.join("Eve", "Q");
        line.callNext();
        clock.advanceMinutes(4);
        line.finishCurrent();
        line.callNext();
        clock.advanceMinutes(6);
        line.finishCurrent();
    }


    /**
     * Estimates: before anyone has been helped there is nothing to base
     * one on, so every waiting student gets -1.
     */
    public void testEstimateBeforeAnyHelp() {
        line.join("Ana", "Q");
        line.join("Ben", "Q");
        clock.advanceMinutes(10);
        line.callNext(); // being helped, but not finished
        List<LineEntry> waiting = observer.getLast().getWaiting();
        assertEquals(-1, waiting.get(0).getEstimatedWaitMinutes());
        assertEquals(0, observer.getLast().getNowHelping()
            .getEstimatedWaitMinutes());
    }


    /**
     * Estimates: sessions of 4 and 6 minutes average 5, so positions 1, 2,
     * and 3 are about 5, 10, and 15 minutes away.
     */
    public void testEstimateAfterTwoSessions() {
        helpTwoFor4And6Minutes();
        List<LineEntry> waiting = observer.getLast().getWaiting();
        assertEquals(3, waiting.size());
        assertEquals(5, waiting.get(0).getEstimatedWaitMinutes());
        assertEquals(10, waiting.get(1).getEstimatedWaitMinutes());
        assertEquals(15, waiting.get(2).getEstimatedWaitMinutes());
    }


    /**
     * Estimates round UP to a whole minute: one 90-second session makes
     * the front of the line "2 minutes", not "1".
     */
    public void testEstimateRoundsUp() {
        line.join("Ana", "Q");
        line.join("Ben", "Q");
        line.callNext();
        clock.setMillis(90000);
        line.finishCurrent();
        assertEquals(2, observer.getLast().getWaiting().get(0)
            .getEstimatedWaitMinutes());
    }


    /**
     * Estimates: time spent with a student who is put back does not count
     * as a help session.
     */
    public void testEstimatePutBackDoesNotCount() {
        helpTwoFor4And6Minutes();
        line.callNext();
        clock.advanceMinutes(30); // a long session that does not finish
        line.putBack();
        List<LineEntry> waiting = observer.getLast().getWaiting();
        assertEquals(5, waiting.get(0).getEstimatedWaitMinutes());
        assertEquals(15, waiting.get(2).getEstimatedWaitMinutes());
    }


    /**
     * Estimates follow the line: when someone ahead leaves, everyone
     * behind moves up and their estimate drops.
     */
    public void testEstimateUpdatesAfterLeave() {
        line.join("Ana", "Q");
        line.join("Ben", "Q");
        line.callNext();
        clock.advanceMinutes(5);
        line.finishCurrent();
        int cal = line.join("Cal", "Q");
        line.join("Dee", "Q");
        List<LineEntry> before = observer.getLast().getWaiting();
        assertEquals("Dee", before.get(2).getName());
        assertEquals(15, before.get(2).getEstimatedWaitMinutes());
        line.leave(cal);
        List<LineEntry> after = observer.getLast().getWaiting();
        assertEquals("Dee", after.get(1).getName());
        assertEquals(10, after.get(1).getEstimatedWaitMinutes());
    }
}
