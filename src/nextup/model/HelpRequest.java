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

/**
 * One student's place in line: their ticket, name, question, and when they
 * joined. It never changes after it is made. This is what the Queue
 * (LinkedDeque) holds.
 *
 * A HelpRequest never leaves the model. Views get LineEntry objects
 * instead, which have no ticket, so no view can show one student another
 * student's ticket.
 *
 * @author Aditya Banerjee (adityab7)
 * @version 2026.11.08
 */
public class HelpRequest {

    /** Smallest ticket number. */
    public static final int MIN_TICKET = 1000;

    /** Largest ticket number. */
    public static final int MAX_TICKET = 9999;

    private final int ticket;
    private final String name;
    private final String question;
    private final long joinedAtMillis;

    /**
     * Creates a help request. OfficeHoursLine cleans the text before calling
     * this, so the values are stored exactly as given.
     *
     * @param ticket
     *            the ticket number, 1000 to 9999
     * @param name
     *            the student's name; must not be null
     * @param question
     *            the student's question; must not be null
     * @param joinedAtMillis
     *            the time they joined, from the TimeSource
     * @throws IllegalArgumentException
     *             if a text value is null or the ticket is out of range
     */
    public HelpRequest(
        int ticket,
        String name,
        String question,
        long joinedAtMillis) {
        if (name == null || question == null) {
            throw new IllegalArgumentException(
                "Name and question must not be null.");
        }
        if (ticket < MIN_TICKET || ticket > MAX_TICKET) {
            throw new IllegalArgumentException(
                "Ticket must be 1000 to 9999.");
        }
        this.ticket = ticket;
        this.name = name;
        this.question = question;
        this.joinedAtMillis = joinedAtMillis;
    }


    /**
     * Returns the ticket number.
     *
     * @return the ticket number
     */
    public int getTicket() {
        return ticket;
    }


    /**
     * Returns the student's name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }


    /**
     * Returns the student's question.
     *
     * @return the question
     */
    public String getQuestion() {
        return question;
    }


    /**
     * Returns when the student joined. Put back keeps this, so a student
     * who is put back does not lose their wait time.
     *
     * @return the join time in milliseconds
     */
    public long getJoinedAtMillis() {
        return joinedAtMillis;
    }
}
