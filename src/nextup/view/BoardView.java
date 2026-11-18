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
// (Anthropic) to write this class for our Swing board stretch goal. After
// using this tool, I reviewed and edited the content as needed to ensure its
// accuracy and take full responsibility for the content in relation to
// grading. I understand that I am responsible for being able to complete this
// work without the use of assistance.

package nextup.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import nextup.model.LineEntry;
import nextup.model.LineObserver;
import nextup.model.LineSnapshot;

/**
 * The live "Now Helping" board: a Swing window meant to face the room. It
 * is a third LineObserver, so it redraws on every change to the line
 * without the model or controller knowing it exists. It only displays:
 * every action still happens in the terminal.
 *
 * Threads: the terminal keeps the main thread, and Swing components may be
 * touched only on Swing's event thread. So update hands the immutable
 * snapshot to that thread with invokeLater. The board keeps no snapshot.
 *
 * Like the student screen, it shows names but never questions.
 *
 * @author Aditya Banerjee (adityab7)
 * @version 2026.11.18
 */
public class BoardView implements LineObserver {

    private static final Color BACKGROUND = new Color(0x14, 0x1B, 0x2D);
    private static final Color PANEL = new Color(0x1F, 0x29, 0x42);
    private static final Color TEXT = new Color(0xF4, 0xF1, 0xEA);
    private static final Color MUTED = new Color(0xA9, 0xB3, 0xC7);
    private static final Color ACCENT = new Color(0xFF, 0x9E, 0x45);

    private final JPanel content;
    private final JLabel helpingLabel;
    private final JTextArea waitingArea;
    private final JLabel statsLabel;
    private final JLabel footerLabel;
    private JFrame frame;

    /**
     * Builds the board's contents. No window appears until open is called,
     * so the board can be tested without a screen.
     */
    public BoardView() {
        helpingLabel = label(Font.BOLD, 44, ACCENT);
        statsLabel = label(Font.PLAIN, 18, MUTED);
        footerLabel = label(Font.PLAIN, 14, MUTED);

        waitingArea = new JTextArea();
        waitingArea.setEditable(false);
        waitingArea.setFocusable(false);
        waitingArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 26));
        waitingArea.setForeground(TEXT);
        waitingArea.setBackground(PANEL);
        waitingArea.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JPanel top = new JPanel(new BorderLayout(0, 4));
        top.setOpaque(false);
        top.add(label("NOW HELPING"), BorderLayout.NORTH);
        top.add(helpingLabel, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(waitingArea);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel middle = new JPanel(new BorderLayout(0, 8));
        middle.setOpaque(false);
        middle.add(label("UP NEXT"), BorderLayout.NORTH);
        middle.add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(0, 4));
        bottom.setOpaque(false);
        bottom.add(statsLabel, BorderLayout.NORTH);
        bottom.add(footerLabel, BorderLayout.SOUTH);

        content = new JPanel(new BorderLayout(0, 20));
        content.setBackground(BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(24, 28, 20, 28));
        content.add(top, BorderLayout.NORTH);
        content.add(middle, BorderLayout.CENTER);
        content.add(bottom, BorderLayout.SOUTH);
    }


    /**
     * Opens the window. It never takes the keyboard, so typing keeps going
     * to the terminal.
     */
    public void open() {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Next Up");
            frame.setDefaultCloseOperation(
                WindowConstants.DISPOSE_ON_CLOSE);
            frame.setAutoRequestFocus(false);
            frame.setFocusableWindowState(false);
            frame.setContentPane(content);
            frame.setSize(new Dimension(760, 620));
            frame.setVisible(true);
        });
    }


    /**
     * Redraws the board from a new snapshot.
     *
     * @param snapshot
     *            the new line
     */
    @Override
    public void update(LineSnapshot snapshot) {
        SwingUtilities.invokeLater(() -> {
            helpingLabel.setText(helpingText(snapshot));
            waitingArea.setText(waitingText(snapshot));
            waitingArea.setCaretPosition(0);
            statsLabel.setText(statsText(snapshot));
        });
    }


    /**
     * Tells the user the terminal is done. An open Swing window keeps Java
     * running, so the program ends when this window is closed.
     */
    public void programEnded() {
        SwingUtilities.invokeLater(() -> {
            if (frame != null && frame.isDisplayable()) {
                footerLabel.setText("Close this window to exit.");
                System.out.println("Close the board window to exit.");
            }
        });
    }


    /**
     * Returns the headline text.
     *
     * @param snapshot
     *            the line
     * @return who is being helped, or the line's state if nobody is
     */
    static String helpingText(LineSnapshot snapshot) {
        if (snapshot.isClosed()) {
            return "Line closed";
        }
        LineEntry current = snapshot.getNowHelping();
        return current == null ? "No one yet" : current.getName();
    }


    /**
     * Returns the waiting list, one student per line.
     *
     * @param snapshot
     *            the line
     * @return the list, or a message if nobody is waiting
     */
    static String waitingText(LineSnapshot snapshot) {
        List<LineEntry> waiting = snapshot.getWaiting();
        if (waiting.isEmpty()) {
            return "No one is waiting.";
        }
        StringBuilder text = new StringBuilder();
        for (LineEntry entry : waiting) {
            if (text.length() > 0) {
                text.append('\n');
            }
            text.append(entry.getPosition()).append(". ")
                .append(entry.getName()).append(" (")
                .append(StudentView.estimateText(entry)).append(")");
        }
        return text.toString();
    }


    /**
     * Returns the line of totals at the bottom of the board.
     *
     * @param snapshot
     *            the line
     * @return the waiting count, helped count, and average wait
     */
    static String statsText(LineSnapshot snapshot) {
        return String.format("Waiting: %d    Helped: %d    Average wait: "
            + "%.1f min", snapshot.getWaiting().size(),
            snapshot.getHelpedCount(), snapshot.getAverageWaitMinutes());
    }


    /**
     * Returns the headline being shown. Call on the event thread.
     *
     * @return the headline
     */
    String shownHelping() {
        return helpingLabel.getText();
    }


    /**
     * Returns the waiting list being shown. Call on the event thread.
     *
     * @return the list text
     */
    String shownWaiting() {
        return waitingArea.getText();
    }


    /**
     * Returns the totals being shown. Call on the event thread.
     *
     * @return the totals text
     */
    String shownStats() {
        return statsLabel.getText();
    }


    /**
     * Checks that the headline treats a name as plain text.
     *
     * @return true if Swing will not render HTML in the headline
     */
    boolean headlineIgnoresHtml() {
        return Boolean.TRUE.equals(
            helpingLabel.getClientProperty("html.disable"));
    }


    /**
     * Makes a small heading label.
     *
     * @param text
     *            the heading
     * @return the label
     */
    private static JLabel label(String text) {
        JLabel heading = label(Font.BOLD, 14, MUTED);
        heading.setText(text);
        return heading;
    }


    /**
     * Makes an empty label. HTML is turned off because student names go
     * into labels, and Swing would otherwise render a name like
     * "&lt;html&gt;..." as markup.
     *
     * @param style
     *            the font style
     * @param size
     *            the font size
     * @param color
     *            the text color
     * @return the label
     */
    private static JLabel label(int style, int size, Color color) {
        JLabel made = new JLabel(" ");
        made.putClientProperty("html.disable", Boolean.TRUE);
        made.setFont(new Font(Font.SANS_SERIF, style, size));
        made.setForeground(color);
        return made;
    }
}
