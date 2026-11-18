// CS 2114 Project 3, Deliverable 4 | Al-Farouq Mohamed
// GenAI disclosure (D4): ChatGPT drafted the D4 estimate, Leave, and Put back
// tests and updated the existing scripts for the student menu.

package nextup.view;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
 * @author Al-Farouq Mohamed (alfarouqm18)
 * @version 2026.11.08
 */
public class ViewTest {

    @Test
    public void testMvpPath() {
        String output = runSession(
                "1234\n"                   // Set the TA PIN.
                        + "1\nAna\nRecursion?\n"   // Join as Ana.
                        + "3\n1234\n"              // Log in as TA.
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

        String taOutput = runSession(join + "3\n1234\n1\n5\n");
        assertTrue(taOutput.contains("Now helping: Ana (Recursion?)"));
    }

    @Test
    public void testBadInputSurvives() {
        String output = runSession(
                "1234\n"                   // Set the TA PIN.
                        + "abc\n"                  // Invalid menu choice.
                        + "1\n\nRecursion?\n"      // Join with a blank name.
                        + "y\nAna\nRecursion?\n"   // Retry with a valid name.
                        + "3\n9999\n"); // Wrong PIN, then input ends.

        assertTrue(output.contains("Enter a number 1 to 3."));
        assertTrue(output.contains("Try again? (y/n)"));
        assertTrue(output.contains("Your ticket is"));
        assertTrue(output.contains("Wrong PIN"));
    }

    @Test
    public void testShowsEstimates() {
        FakeTimeSource clock = new FakeTimeSource();
        OfficeHoursLine line = new OfficeHoursLine(clock, new Random(42));
        LineController controller = new LineController(line);
        String output = captureOutput(() -> {
            try (Scanner scanner = new Scanner("")) {
                ConsoleInput input = new ConsoleInput(scanner);
                attachViews(line, controller, input,
                    new TaView(controller, input));
                assertTrue(controller.setTaPin("1234").isOk());
                assertTrue(controller.join("Ana", "Recursion?").isOk());
                assertTrue(controller.join("Ben", "Generics?").isOk());
                assertTrue(controller.taLogin("1234").isOk());
                assertTrue(controller.callNext().isOk());
                clock.advanceMinutes(5);
                assertTrue(controller.finishCurrent().isOk());
                controller.taLogout();
                assertTrue(controller.join("Cal", "Lists?").isOk());
            }
        });

        assertTrue(output.contains(
            "1. Ana (0 min, estimate after first student)"));
        assertTrue(output.contains(
                "1. Ben - Generics? - 0 min (estimate after first student)"));
        assertTrue(output.contains(
            "1. Ben - Generics? - 5 min (about 5 min left)"));
        assertTrue(output.contains("1. Ben (5 min, about 5 min left)"));
        assertTrue(output.contains("2. Cal (0 min, about 10 min left)"));
    }

    @Test
    public void testLeaveAndPutBack() {
        int anaTicket = new Random(42).nextInt(9000) + 1000;
        String output = runSession("1234\n1\nAna\nRecursion?\n"
                + "1\nBen\nGenerics?\n"
                + "3\n1234\n1\n3\n5\n"   // Call, put back, log out.
                + "2\nabc\n2\n" + anaTicket + "\n");

        assertTrue(output.contains("You're #1. Your ticket is " + anaTicket));
        assertTrue(output.contains("Now helping: Ana"));
        assertTrue(output.contains("1. Ana - Recursion? - 0 min"));
        assertTrue(output.contains("Student put back at the front."));
        assertTrue(output.contains("Enter your ticket number, like 4821."));
        assertTrue(output.contains("You left the line."));

        String finalRedraw =
            output.substring(output.lastIndexOf("Student line"));
        assertTrue(finalRedraw.contains("1. Ben ("));
        assertFalse(finalRedraw.contains("Ana"));
    }

    private String runSession(String script) {
        OfficeHoursLine line = new OfficeHoursLine(
                new FakeTimeSource(), new Random(42));
        LineController controller = new LineController(line);
        return captureOutput(() -> {
            try (Scanner scanner = new Scanner(script)) {
                ConsoleInput input = new ConsoleInput(scanner);
                TaView ta = new TaView(controller, input);
                StudentView student = attachViews(line, controller, input, ta);
                ta.setUpPin();
                student.run();
            }
        });
    }

    private StudentView attachViews(
        OfficeHoursLine line, LineController controller, ConsoleInput input,
        TaView ta) {
        StudentView student = new StudentView(controller, input, ta);
        line.addObserver(ta);
        line.addObserver(student);
        return student;
    }

    private String captureOutput(Runnable session) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try (PrintStream capturedOut = new PrintStream(output)) {
            System.setOut(capturedOut);
            session.run();
        }
        finally {
            System.setOut(originalOut);
        }

        return output.toString();
    }
}
