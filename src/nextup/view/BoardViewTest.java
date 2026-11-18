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
// (Anthropic) to write these tests for our Swing board. After
// using this tool, I reviewed and edited the content as needed to ensure its
// accuracy and take full responsibility for the content in relation to
// grading. I understand that I am responsible for being able to complete this
// work without the use of assistance.

package nextup.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;
import javax.swing.SwingUtilities;
import nextup.model.FakeTimeSource;
import nextup.model.LineSnapshot;
import nextup.model.OfficeHoursLine;
import nextup.model.RecordingObserver;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the Swing board with a real line and a fake clock. No window is
 * opened, so these run without a screen.
 *
 * @author Aditya Banerjee (adityab7)
 * @version 2026.11.18
 */
public class BoardViewTest {

    private FakeTimeSource clock;
    private OfficeHoursLine line;
    private RecordingObserver observer;

    /**
     * Creates an open, empty line before each test.
     */
    @Before
    public void setUp() {
        clock = new FakeTimeSource();
        line = new OfficeHoursLine(clock, new Random(42));
        observer = new RecordingObserver();
        line.addObserver(observer);
    }


    /**
     * Returns the newest snapshot.
     *
     * @return the snapshot
     */
    private LineSnapshot latest() {
        return observer.getLast();
    }


    /**
     * Waits until Swing has run everything already handed to it.
     *
     * @throws Exception
     *             if the wait is interrupted
     */
    private void waitForSwing() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            // nothing: reaching this means earlier updates have run
        });
    }


    /**
     * An empty line says so instead of showing blanks.
     */
    @Test
    public void testEmptyLine() {
        assertEquals("No one yet", BoardView.helpingText(latest()));
        assertEquals("No one is waiting.", BoardView.waitingText(latest()));
        assertEquals("Waiting: 0    Helped: 0    Average wait: 0.0 min",
            BoardView.statsText(latest()));
    }


    /**
     * Before anyone is helped there is no estimate, and a student's
     * question never appears on the public board.
     */
    @Test
    public void testWaitingBeforeAnyEstimate() {
        line.join("Ana", "Private question");
        line.join("Ben", "Generics?");
        String text = BoardView.waitingText(latest());
        assertEquals("1. Ana (estimate after first student)\n"
            + "2. Ben (estimate after first student)", text);
        assertFalse(text.contains("Private question"));
    }


    /**
     * Sessions of 4 and 6 minutes average 5, so the board shows the current
     * student and estimates of 5 and 10 minutes.
     */
    @Test
    public void testHelpingAndEstimates() {
        for (String name : new String[] {"Ana", "Ben", "Cal", "Dee", "Eve"}) {
            line.join(name, "Q");
        }
        line.callNext();
        clock.advanceMinutes(4);
        line.finishCurrent();
        line.callNext();
        clock.advanceMinutes(6);
        line.finishCurrent();
        line.callNext();
        assertEquals("Cal", BoardView.helpingText(latest()));
        assertEquals("1. Dee (about 5 more)\n2. Eve (about 10 more)",
            BoardView.waitingText(latest()));
        assertTrue(BoardView.statsText(latest()).startsWith(
            "Waiting: 2    Helped: 2"));
    }


    /**
     * A closed line says so in the headline.
     */
    @Test
    public void testClosedLine() {
        line.closeLine();
        assertEquals("Line closed", BoardView.helpingText(latest()));
    }


    /**
     * As an observer, the board shows the line right away and follows each
     * change.
     *
     * @throws Exception
     *             if waiting for Swing is interrupted
     */
    @Test
    public void testUpdateRedrawsBoard() throws Exception {
        BoardView board = new BoardView();
        line.addObserver(board);
        waitForSwing();
        assertEquals("No one yet", board.shownHelping());

        line.join("Ana", "Q");
        waitForSwing();
        assertEquals("1. Ana (estimate after first student)",
            board.shownWaiting());
        assertTrue(board.shownStats().startsWith("Waiting: 1"));

        line.callNext();
        waitForSwing();
        assertEquals("Ana", board.shownHelping());
        assertEquals("No one is waiting.", board.shownWaiting());
    }


    /**
     * Bad input: a name written as HTML stays plain text on the board.
     *
     * @throws Exception
     *             if waiting for Swing is interrupted
     */
    @Test
    public void testHtmlNameIsPlainText() throws Exception {
        BoardView board = new BoardView();
        line.addObserver(board);
        line.join("<html><b>Ana", "Q");
        line.callNext();
        waitForSwing();
        assertEquals("<html><b>Ana", board.shownHelping());
        assertTrue(board.headlineIgnoresHtml());
    }
}
