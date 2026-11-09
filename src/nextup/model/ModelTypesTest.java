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

import java.util.ArrayList;
import java.util.List;

/**
 * Tests the small model types: HelpRequest, LineEntry, LineSnapshot,
 * LineRuleException, and the two test helpers (the "Small model types"
 * table of the D2 test plan).
 *
 * @author Aditya Banerjee (adityab7)
 * @version 2026.11.08
 */
public class ModelTypesTest extends student.TestCase {

    /**
     * Something that might throw.
     */
    private interface Action {
        /**
         * Runs the action.
         */
        void run();
    }

    /**
     * Checks that an action throws IllegalArgumentException.
     *
     * @param action
     *            the action
     */
    private void assertIllegal(Action action) {
        Exception thrown = null;
        try {
            action.run();
        }
        catch (Exception exception) {
            thrown = exception;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
    }


    /**
     * HelpRequest stores and returns its values.
     */
    public void testHelpRequest() {
        HelpRequest request = new HelpRequest(4821, "Ana", "Recursion?",
            1000);
        assertEquals(4821, request.getTicket());
        assertEquals("Ana", request.getName());
        assertEquals("Recursion?", request.getQuestion());
        assertEquals(1000, request.getJoinedAtMillis());
        assertEquals(1000, new HelpRequest(1000, "a", "b", 0).getTicket());
        assertEquals(9999, new HelpRequest(9999, "a", "b", 0).getTicket());
    }


    /**
     * HelpRequest rejects null text and out-of-range tickets.
     */
    public void testHelpRequestBadInput() {
        assertIllegal(() -> new HelpRequest(4821, null, "Q", 0));
        assertIllegal(() -> new HelpRequest(4821, "Ana", null, 0));
        assertIllegal(() -> new HelpRequest(999, "Ana", "Q", 0));
        assertIllegal(() -> new HelpRequest(10000, "Ana", "Q", 0));
    }


    /**
     * LineEntry stores and returns its values.
     */
    public void testLineEntry() {
        LineEntry entry = new LineEntry(1, "Ana", "Recursion?", 2);
        assertEquals(1, entry.getPosition());
        assertEquals("Ana", entry.getName());
        assertEquals("Recursion?", entry.getQuestion());
        assertEquals(2, entry.getMinutesWaited());
        assertEquals(0, new LineEntry(0, "a", "b", 0).getPosition());
    }


    /**
     * LineEntry rejects negative numbers and null text.
     */
    public void testLineEntryBadInput() {
        assertIllegal(() -> new LineEntry(-1, "Ana", "Q", 0));
        assertIllegal(() -> new LineEntry(1, "Ana", "Q", -1));
        assertIllegal(() -> new LineEntry(1, null, "Q", 0));
        assertIllegal(() -> new LineEntry(1, "Ana", null, 0));
    }


    /**
     * LineSnapshot returns its values and copies the list.
     */
    public void testLineSnapshot() {
        LineEntry helping = new LineEntry(0, "Ana", "Q", 1);
        List<LineEntry> list = new ArrayList<LineEntry>();
        list.add(new LineEntry(1, "Ben", "Q", 0));
        LineSnapshot snap = new LineSnapshot(helping, list, 3, 2.5, true);
        assertSame(helping, snap.getNowHelping());
        assertEquals(1, snap.getWaiting().size());
        assertEquals("Ben", snap.getWaiting().get(0).getName());
        assertEquals(3, snap.getHelpedCount());
        assertEquals(2.5, snap.getAverageWaitMinutes(), 0.001);
        assertTrue(snap.isClosed());

        list.clear();
        assertEquals(1, snap.getWaiting().size());

        LineSnapshot empty = new LineSnapshot(null, list, 0, 0.0, false);
        assertNull(empty.getNowHelping());
        assertFalse(empty.isClosed());
    }


    /**
     * LineSnapshot rejects a null list and cannot be changed.
     */
    public void testLineSnapshotBadInput() {
        assertIllegal(() -> new LineSnapshot(null, null, 0, 0.0, false));
        LineSnapshot snap = new LineSnapshot(null,
            new ArrayList<LineEntry>(), 0, 0.0, false);
        Exception thrown = null;
        try {
            snap.getWaiting().add(new LineEntry(1, "a", "b", 0));
        }
        catch (Exception exception) {
            thrown = exception;
        }
        assertTrue(thrown instanceof UnsupportedOperationException);
    }


    /**
     * LineRuleException carries its message and is unchecked.
     */
    public void testLineRuleException() {
        LineRuleException exception = new LineRuleException("x");
        assertEquals("x", exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }


    /**
     * The test helpers work as described.
     */
    public void testHelpers() {
        FakeTimeSource clock = new FakeTimeSource();
        assertEquals(0, clock.nowMillis());
        clock.advanceMinutes(2);
        assertEquals(120000, clock.nowMillis());
        clock.setMillis(5);
        assertEquals(5, clock.nowMillis());

        RecordingObserver observer = new RecordingObserver();
        assertEquals(0, observer.getUpdateCount());
        assertNull(observer.getLast());
        LineSnapshot snap = new LineSnapshot(null,
            new ArrayList<LineEntry>(), 0, 0.0, false);
        observer.update(snap);
        assertEquals(1, observer.getUpdateCount());
        assertSame(snap, observer.getLast());
    }
}
