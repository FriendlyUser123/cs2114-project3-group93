package nextup.view;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Random;
import java.util.Scanner;
import nextup.controller.LineController;
import nextup.model.FakeTimeSource;
import nextup.model.OfficeHoursLine;
import org.junit.Test;

/**
 * Tests the student and TA views with scripted console input.
 *
 * @author Al-Farouq Mohamed
 * @version 2026.11.08
 */
public class ViewTest {

    @Test
    public void testMvpPath() {
        String output = runSession(
                "1234\n"                   // Set the TA PIN.
                        + "1\nAna\nRecursion?\n"   // Join as Ana.
                        + "4\n1234\n"              // Log in as TA.
                        + "1\n2\n4\n");            // Call next, finish, close.

        assertTrue(output.contains("Your ticket is"));
        assertTrue(output.contains("Now helping: Ana"));
        assertTrue(output.contains("Helped: 1" + System.lineSeparator()));
        assertTrue(output.contains("Average wait:"));
    }

    @Test
    public void testStudentViewHidesQuestions() {
        String join = "1234\n1\nAna\nRecursion?\n";

        String studentOutput = runSession(join);
        assertTrue(studentOutput.contains("1. Ana"));
        assertFalse(studentOutput.contains("Recursion?"));

        String taOutput = runSession(join + "4\n1234\n5\n");
        assertTrue(taOutput.contains("Ana - Recursion?"));
    }

    @Test
    public void testBadInputSurvives() {
        String output = runSession(
                "1234\n"                   // Set the TA PIN.
                        + "abc\n"                  // Invalid menu choice.
                        + "1\n\nRecursion?\n"      // Join with a blank name.
                        + "y\nAna\nRecursion?\n"   // Retry with a valid name.
                        + "4\n9999\n");            // Wrong PIN, then input ends.

        assertTrue(output.contains("Enter a number 1 to 4."));
        assertTrue(output.contains("Try again? (y/n)"));
        assertTrue(output.contains("Your ticket is"));
        assertTrue(output.contains("Wrong PIN"));
    }

    /**
     * D4 (Al-Farouq): the wait-time estimate shows on both screens.
     *
     * Cases: before anyone is finished, the student screen shows the
     * "no estimate yet" text (estimate is -1). After the TA helps one
     * student, the next student's line shows "about N min". Use
     * FakeTimeSource: runSession builds the line with one, so keep a
     * reference to it and call advanceMinutes between "call next" and
     * "finish".
     */
    @Test
    public void testShowsEstimates() {
        // TODO(Al-Farouq, D4)
        fail("TODO(Al-Farouq, D4): estimate text on both screens");
    }

    /**
     * D4 (Al-Farouq): Leave and Put back through the menus.
     *
     * Cases: a student leaves with their real ticket (read it from the
     * "Your ticket is NNNN." line, or use Random(42), which always gives
     * the same tickets) and disappears from the line; leaving with "abc"
     * prints "Enter your ticket number, like 4821."; the TA calls Ana,
     * puts her back, and she is #1 again.
     */
    @Test
    public void testLeaveAndPutBack() {
        // TODO(Al-Farouq, D4)
        fail("TODO(Al-Farouq, D4): leave and put back");
    }

    private String runSession(String script) {
        OfficeHoursLine line = new OfficeHoursLine(
                new FakeTimeSource(), new Random(42));
        LineController controller = new LineController(line);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try (Scanner scanner = new Scanner(script);
             PrintStream capturedOut = new PrintStream(output)) {
            System.setOut(capturedOut);

            ConsoleInput input = new ConsoleInput(scanner);
            TaView ta = new TaView(controller, input);
            StudentView student = new StudentView(controller, input, ta);
            line.addObserver(ta);
            line.addObserver(student);

            ta.setUpPin();
            student.run();
        }
        finally {
            System.setOut(originalOut);
        }

        return output.toString();
    }
}