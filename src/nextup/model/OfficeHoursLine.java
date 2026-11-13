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
// (Anthropic) to write this class from our Deliverable 2 specification.
// After using this tool, I reviewed and edited the content as needed to
// ensure its accuracy and take full responsibility for the content in
// relation to grading. I understand that I am responsible for being able to
// complete this work without the use of assistance.

package nextup.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The office-hours line and every rule about it. This is the heart of the
 * model: it decides who can join, who is helped next, and keeps the
 * session stats. It knows nothing about the terminal, the controller, or
 * any view. It tells the outside world about changes only through
 * LineObserver.
 *
 * The Queue lives here: waiting is a LinkedDeque of HelpRequest, front of
 * the line first. The student being helped is moved out of the deque into
 * nowHelping until they are finished or put back.
 *
 * Every method that breaks a rule throws LineRuleException and leaves the
 * line exactly as it was (no change, no observer update).
 *
 * @author Aditya Banerjee (adityab7)
 * @version 2026.11.13
 */
public class OfficeHoursLine {

    /** Longest allowed name, after cleaning. */
    public static final int MAX_NAME = 30;

    /** Longest allowed question, after cleaning. */
    public static final int MAX_QUESTION = 120;

    /** Most students who can join. Put back may go one past this. */
    public static final int MAX_WAITING = 50;

    private static final long MILLIS_PER_MINUTE = 60000;
    private static final int TICKET_COUNT =
        HelpRequest.MAX_TICKET - HelpRequest.MIN_TICKET + 1;

    private final TimeSource clock;
    private final Random random;
    private final LinkedDeque<HelpRequest> waiting;
    private final List<LineObserver> observers;
    private final boolean[] usedTickets;
    private int ticketsIssued;
    private HelpRequest nowHelping;
    private long calledAtMillis;
    private int helpedCount;
    private long totalWaitMillis;
    private long totalHelpMillis;
    private boolean closed;

    /**
     * Creates an open, empty line.
     *
     * @param clock
     *            where to get the time; tests pass a fake clock
     * @param random
     *            used to pick ticket numbers; tests pass new Random(42)
     * @throws IllegalArgumentException
     *             if either argument is null
     */
    public OfficeHoursLine(TimeSource clock, Random random) {
        if (clock == null || random == null) {
            throw new IllegalArgumentException(
                "Clock and random must not be null.");
        }
        this.clock = clock;
        this.random = random;
        waiting = new LinkedDeque<HelpRequest>();
        observers = new ArrayList<LineObserver>();
        usedTickets = new boolean[TICKET_COUNT];
        ticketsIssued = 0;
        nowHelping = null;
        calledAtMillis = 0;
        helpedCount = 0;
        totalWaitMillis = 0;
        totalHelpMillis = 0;
        closed = false;
    }


    /**
     * Registers an observer and immediately sends it the current snapshot,
     * so a view never has "no data yet."
     *
     * @param observer
     *            the observer to add
     * @throws IllegalArgumentException
     *             if observer is null
     */
    public void addObserver(LineObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException(
                "Observer must not be null.");
        }
        observers.add(observer);
        observer.update(getSnapshot());
    }


    /**
     * Adds a student to the back of the line. Both strings are cleaned
     * first: control characters (newlines, tabs, ...) are removed, then the
     * text is trimmed. Every rule is checked on the cleaned text.
     *
     * @param name
     *            the student's name
     * @param question
     *            the student's question
     * @return the new, random ticket number (1000 to 9999)
     * @throws LineRuleException
     *             if the line is closed or full, a value is blank or too
     *             long, or this name is already waiting or being helped
     */
    public int join(String name, String question) {
        if (closed) {
            throw new LineRuleException("The line is closed.");
        }
        String cleanName = cleanText(name);
        String cleanQuestion = cleanText(question);
        if (cleanName.isEmpty()) {
            throw new LineRuleException("Name can't be blank.");
        }
        if (cleanQuestion.isEmpty()) {
            throw new LineRuleException("Question can't be blank.");
        }
        if (cleanName.length() > MAX_NAME) {
            throw new LineRuleException(
                "Names can be at most " + MAX_NAME + " characters.");
        }
        if (cleanQuestion.length() > MAX_QUESTION) {
            throw new LineRuleException(
                "Questions can be at most " + MAX_QUESTION + " characters.");
        }
        if (nowHelping != null
            && nowHelping.getName().equalsIgnoreCase(cleanName)) {
            throw new LineRuleException("You're being helped right now.");
        }
        int existing = positionOfName(cleanName);
        if (existing != -1) {
            throw new LineRuleException(
                "You're already in line at #" + existing + ".");
        }
        if (waiting.size() >= MAX_WAITING) {
            throw new LineRuleException("The line is full.");
        }

        int ticket = newTicket();
        waiting.enqueue(new HelpRequest(ticket, cleanName, cleanQuestion,
            clock.nowMillis()));
        notifyObservers();
        return ticket;
    }


    /**
     * Returns where a ticket is in the waiting line.
     *
     * @param ticket
     *            the ticket to look for
     * @return the 1-based position, or -1 if that ticket is not waiting
     *         (including the student being helped)
     */
    public int positionOf(int ticket) {
        List<HelpRequest> line = waiting.toList();
        for (int i = 0; i < line.size(); i++) {
            if (line.get(i).getTicket() == ticket) {
                return i + 1;
            }
        }
        return -1;
    }


    /**
     * Removes a waiting student. Everyone behind them moves up one.
     *
     * @param ticket
     *            the student's ticket
     * @throws LineRuleException
     *             if no waiting student has that ticket
     */
    public void leave(int ticket) {
        HelpRequest found = null;
        for (HelpRequest request : waiting.toList()) {
            if (request.getTicket() == ticket) {
                found = request;
            }
        }
        if (found == null) {
            throw new LineRuleException("No ticket #" + ticket + " in line");
        }
        waiting.remove(found);
        notifyObservers();
    }


    /**
     * Moves the student at the front of the line to "now helping" and
     * records when they were called.
     *
     * @throws LineRuleException
     *             if the line is closed, someone is already being helped,
     *             or nobody is waiting
     */
    public void callNext() {
        if (closed) {
            throw new LineRuleException("The line is closed.");
        }
        if (nowHelping != null) {
            throw new LineRuleException(
                "Finish with " + nowHelping.getName() + " first.");
        }
        if (waiting.isEmpty()) {
            throw new LineRuleException("No one is waiting");
        }
        nowHelping = waiting.dequeue();
        calledAtMillis = clock.nowMillis();
        notifyObservers();
    }


    /**
     * Finishes with the student being helped: counts them as helped, adds
     * their wait (join to called) to the stats, and adds how long they were
     * helped (called to now) to the total that wait estimates use.
     *
     * @throws LineRuleException
     *             if nobody is being helped
     */
    public void finishCurrent() {
        requireSomeoneHelped();
        helpedCount++;
        totalWaitMillis += calledAtMillis - nowHelping.getJoinedAtMillis();
        totalHelpMillis += clock.nowMillis() - calledAtMillis;
        nowHelping = null;
        notifyObservers();
    }


    /**
     * Puts the student being helped back at the FRONT of the line, with
     * the same ticket and join time. This is the one case where FIFO would
     * be unfair, so it is allowed even with 50 waiting (making 51). The
     * time spent with them does not count toward wait estimates, because
     * the help session did not finish.
     *
     * @throws LineRuleException
     *             if nobody is being helped
     */
    public void putBack() {
        requireSomeoneHelped();
        waiting.addToFront(nowHelping);
        nowHelping = null;
        notifyObservers();
    }


    /**
     * Closes the line. Observers get one last snapshot with the final
     * stats and isClosed() true. After this, join and callNext are refused.
     *
     * @throws LineRuleException
     *             if the line is already closed or someone is still being
     *             helped
     */
    public void closeLine() {
        if (closed) {
            throw new LineRuleException("The line is already closed.");
        }
        if (nowHelping != null) {
            throw new LineRuleException(
                "Finish with " + nowHelping.getName() + " first.");
        }
        closed = true;
        notifyObservers();
    }


    /**
     * Builds a new read-only snapshot of the line and the stats.
     *
     * @return a snapshot; never null
     */
    public LineSnapshot getSnapshot() {
        long now = clock.nowMillis();
        LineEntry helping = null;
        if (nowHelping != null) {
            helping = toEntry(nowHelping, 0, calledAtMillis, 0);
        }
        List<LineEntry> entries = new ArrayList<LineEntry>();
        List<HelpRequest> line = waiting.toList();
        for (int i = 0; i < line.size(); i++) {
            entries.add(toEntry(line.get(i), i + 1, now,
                estimateMinutes(i + 1)));
        }
        double average = 0.0;
        if (helpedCount > 0) {
            average = totalWaitMillis / (double)helpedCount
                / MILLIS_PER_MINUTE;
        }
        return new LineSnapshot(helping, entries, helpedCount, average,
            closed);
    }


    // ----------------------------------------------------------
    // Private helpers
    // ----------------------------------------------------------

    /**
     * Removes control characters (newline, tab, ...) and trims.
     *
     * @param text
     *            raw text from the user, possibly null
     * @return the cleaned text; "" if text was null
     */
    private String cleanText(String text) {
        if (text == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (!Character.isISOControl(c)) {
                builder.append(c);
            }
        }
        return builder.toString().trim();
    }


    /**
     * Finds a waiting student by name, ignoring case.
     *
     * @param name
     *            a cleaned name
     * @return the 1-based position, or -1 if nobody waiting has that name
     */
    private int positionOfName(String name) {
        List<HelpRequest> line = waiting.toList();
        for (int i = 0; i < line.size(); i++) {
            if (line.get(i).getName().equalsIgnoreCase(name)) {
                return i + 1;
            }
        }
        return -1;
    }


    /**
     * Picks a random ticket that has not been used this session. A ticket
     * is never reused, so an old ticket can never remove someone new.
     *
     * @return the ticket number
     * @throws LineRuleException
     *             if all 9000 tickets have been used
     */
    private int newTicket() {
        if (ticketsIssued == TICKET_COUNT) {
            throw new LineRuleException("No tickets are left this session.");
        }
        int index = random.nextInt(TICKET_COUNT);
        while (usedTickets[index]) {
            index = random.nextInt(TICKET_COUNT);
        }
        usedTickets[index] = true;
        ticketsIssued++;
        return index + HelpRequest.MIN_TICKET;
    }


    /**
     * Throws if nobody is being helped.
     */
    private void requireSomeoneHelped() {
        if (nowHelping == null) {
            throw new LineRuleException("No one is being helped");
        }
    }


    /**
     * Turns a request into a view-safe row (no ticket).
     *
     * @param request
     *            the request
     * @param position
     *            its position (0 for being helped)
     * @param untilMillis
     *            the end of the wait to measure
     * @param estimate
     *            the estimated wait in minutes, 0 for the student being
     *            helped, or -1 if there is no estimate yet
     * @return the row
     */
    private LineEntry toEntry(
        HelpRequest request,
        int position,
        long untilMillis,
        int estimate) {
        long waited = untilMillis - request.getJoinedAtMillis();
        int minutes = (int)(Math.max(0, waited) / MILLIS_PER_MINUTE);
        return new LineEntry(position, request.getName(),
            request.getQuestion(), minutes, estimate);
    }


    /**
     * Estimates how many minutes until the student at a position is
     * called: their position times the average length of a finished help
     * session, rounded up to a whole minute. Put-backs never count as a
     * session, so one long put-back can't skew the estimate.
     *
     * Example: sessions of 4 and 6 minutes average 5, so position 3 is
     * about 15 minutes away.
     *
     * @param position
     *            1 for the front of the line
     * @return the estimate in whole minutes, or -1 if nobody has been
     *         helped yet (there is nothing to base it on)
     */
    private int estimateMinutes(int position) {
        if (helpedCount == 0) {
            return -1;
        }
        long millis = position * totalHelpMillis / helpedCount;
        return (int)((millis + MILLIS_PER_MINUTE - 1) / MILLIS_PER_MINUTE);
    }


    /**
     * Sends a fresh snapshot to every observer.
     */
    private void notifyObservers() {
        LineSnapshot snapshot = getSnapshot();
        for (LineObserver observer : observers) {
            observer.update(snapshot);
        }
    }
}
