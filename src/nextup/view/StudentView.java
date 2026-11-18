// CS 2114 Project 3, Deliverable 4 | Al-Farouq Mohamed
// GenAI disclosure (D4): ChatGPT drafted the D4 student observer rendering and menu updates.

package nextup.view;

import nextup.controller.ActionResult;
import nextup.controller.LineController;
import nextup.model.LineEntry;
import nextup.model.LineObserver;
import nextup.model.LineSnapshot;

public class StudentView implements LineObserver {

    private final LineController controller;
    private final ConsoleInput input;
    private final TaView taView;
    private boolean finished;

    /**
     * Creates the student view.
     *
     * @param controller
     *            where every action goes
     * @param input
     *            the shared input reader
     * @param taView
     *            the TA screen, run after a successful TA login
     */
    public StudentView(
        LineController controller,
        ConsoleInput input,
        TaView taView) {
        this.controller = controller;
        this.input = input;
        this.taView = taView;
    }


    /**
     * Renders the student screen while students have the keyboard.
     *
     * @param snapshot
     *            the new line
     */
    @Override
    public void update(LineSnapshot snapshot) {
        if (snapshot.isClosed()) {
            finished = true;
            System.out.println("Helped: " + snapshot.getHelpedCount());
            System.out.printf("Average wait: %.1f min%n",
                    snapshot.getAverageWaitMinutes());
            return;
        }
        if (controller.isTaMode()) {
            return;
        }

        System.out.println("\nStudent line");
        LineEntry current = snapshot.getNowHelping();
        if (current != null) {
            System.out.println("Now helping: " + current.getName());
        }
        for (LineEntry entry : snapshot.getWaiting()) {
            System.out.println(entry.getPosition() + ". " + entry.getName()
                    + " (" + entry.getMinutesWaited() + " min, "
                    + estimateText(entry) + ")");
        }
    }

    /**
     * Formats the estimate the model supplied for a waiting student.
     */
    static String estimateText(LineEntry entry) {
        int estimate = entry.getEstimatedWaitMinutes();
        if (estimate == -1) {
            return "estimate after first student";
        }
        return "about " + estimate + " more";
    }

    /**
     * The main menu loop. Runs until the line is closed or input ends.
     */
    public void run() {
        while (!finished) {
            System.out.println("\nStudent menu");
            System.out.println("1) Join the line  2) Leave the line  3) TA login");
            int choice = input.readMenuChoice("Choose: ", 3);

            switch (choice) {
                case -1:
                    return;

                case 1:
                    while (true) {
                        String name = input.readLine("Your name: ");
                        if (name == null) {
                            return;
                        }

                        String question = input.readLine("Your question: ");
                        if (question == null) {
                            return;
                        }

                        ActionResult result = controller.join(name, question);
                        System.out.println(result.getMessage());

                        if (result.isOk()) {
                            break;
                        }

                        String retry = input.readLine("Try again? (y/n) ");
                        if (retry == null) {
                            return;
                        }
                        if (!retry.trim().equalsIgnoreCase("y")) {
                            break;
                        }
                    }
                    break;

                case 2:
                    String ticket = input.readLine("Your ticket number: ");
                    if (ticket == null) {
                        return;
                    }
                    System.out.println(controller.leave(ticket).getMessage());
                    break;

                case 3:
                    String pin = input.readLine("TA PIN: ");
                    if (pin == null) {
                        return;
                    }

                    ActionResult login = controller.taLogin(pin);
                    if (login.isOk()) {
                        taView.run();
                    }
                    else {
                        System.out.println(login.getMessage());
                    }
                    break;
            }
        }
    }
}
