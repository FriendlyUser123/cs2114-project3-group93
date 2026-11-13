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
// (Anthropic) to write this driver from our Deliverable 2 specification.
// After using this tool, I reviewed and edited the content as needed to
// ensure its accuracy and take full responsibility for the content in
// relation to grading. I understand that I am responsible for being able to
// complete this work without the use of assistance.

package nextup.demo;

import java.util.Random;
import nextup.model.LineEntry;
import nextup.model.LineObserver;
import nextup.model.LineRuleException;
import nextup.model.LineSnapshot;
import nextup.model.OfficeHoursLine;

/**
 * A tiny driver that runs the model with NO view, NO controller, and NO
 * Scanner: it walks the whole MVP path and a few bad inputs, printing each
 * snapshot. This is the "Model works" proof for Deliverable 3.
 *
 * Run: right-click ModelDemo.java, Run As, Java Application.
 *
 * It lives in its own package (nextup.demo) because it prints, and the
 * model package must never use System.out.
 *
 * @author Aditya Banerjee (adityab7)
 * @version 2026.11.13
 */
public class ModelDemo {

    private static long fakeNow = 0;

    /**
     * Private: only main() is used.
     */
    private ModelDemo() {
        // nothing
    }


    /**
     * Runs the demo.
     *
     * @param args
     *            not used
     */
    public static void main(String[] args) {
        OfficeHoursLine line = new OfficeHoursLine(() -> fakeNow,
            new Random(42));
        LineObserver printer = new LineObserver() {
            @Override
            public void update(LineSnapshot snapshot) {
                print(snapshot);
            }
        };
        line.addObserver(printer);

        System.out.println("> Ana, Ben, and Cal join");
        line.join("Ana", "Recursion base case?");
        int ben = line.join("Ben", "Generics?");
        line.join("Cal", "Big-O of remove?");

        System.out.println("> Bad input: \" ana \" joins again");
        tryRule(() -> line.join(" ana ", "again"));
        System.out.println("> Bad input: leave with ticket 57");
        tryRule(() -> line.leave(57));

        System.out.println("> Ben leaves early with his ticket " + ben);
        line.leave(ben);

        fakeNow += 2 * 60000;
        System.out.println("> 2 minutes later, TA calls next");
        line.callNext();
        System.out.println("> Bad input: call next while helping");
        tryRule(() -> line.callNext());

        System.out.println("> TA puts Ana back (front of line)");
        line.putBack();
        fakeNow += 60000;
        System.out.println("> 1 minute later, call Ana; help her 4 minutes");
        line.callNext();
        fakeNow += 4 * 60000;
        line.finishCurrent();
        System.out.println("> Dee joins: Cal is ~4 min away, Dee ~8 (the"
            + " wait-time estimate)");
        line.join("Dee", "Big-O?");
        System.out.println("> Help Cal for 6 minutes (average help is now 5)");
        line.callNext();
        fakeNow += 6 * 60000;
        line.finishCurrent();
        System.out.println("> Help Dee for 5 minutes");
        line.callNext();
        fakeNow += 5 * 60000;
        line.finishCurrent();

        System.out.println("> Bad input: call next on an empty line");
        tryRule(() -> line.callNext());
        System.out.println("> TA closes the line");
        line.closeLine();
        System.out.println("> Bad input: join after close");
        tryRule(() -> line.join("Eve", "Late?"));
    }


    /**
     * Runs an action that should break a rule, and prints the message.
     *
     * @param action
     *            the action
     */
    private static void tryRule(Runnable action) {
        try {
            action.run();
            System.out.println("  (no error?)");
        }
        catch (LineRuleException e) {
            System.out.println("  refused: " + e.getMessage());
        }
    }


    /**
     * Prints a snapshot.
     *
     * @param snapshot
     *            the snapshot
     */
    private static void print(LineSnapshot snapshot) {
        LineEntry helping = snapshot.getNowHelping();
        StringBuilder text = new StringBuilder("  now helping: ");
        text.append(helping == null ? "nobody" : helping.getName());
        text.append(" | waiting:");
        for (LineEntry entry : snapshot.getWaiting()) {
            text.append(" " + entry.getPosition() + "." + entry.getName()
                + "(" + entry.getMinutesWaited() + "m");
            if (entry.getEstimatedWaitMinutes() >= 0) {
                text.append(", est " + entry.getEstimatedWaitMinutes()
                    + "m");
            }
            text.append(")");
        }
        text.append(" | helped " + snapshot.getHelpedCount() + ", avg "
            + String.format("%.1f", snapshot.getAverageWaitMinutes())
            + " min");
        if (snapshot.isClosed()) {
            text.append(" | CLOSED");
        }
        System.out.println(text);
    }
}
