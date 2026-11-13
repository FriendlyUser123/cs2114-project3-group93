
package nextup.view;

import nextup.controller.ActionResult;
import nextup.model.LineEntry;
import nextup.controller.LineController;
import nextup.model.LineObserver;
import nextup.model.LineSnapshot;

public class StudentView implements LineObserver {

    private final LineController controller;
    private final ConsoleInput input;
    private final TaView taView;
    // TODO(Al-Farouq, D4): two changes to this class (see TEAM-TODO.md).
    //
    // 1. Show the wait-time estimate next to each name:
    //    entry.getEstimatedWaitMinutes() is the minutes, or -1 when nobody
    //    has been helped yet. Print "1. Ana (3 min, about 5 more)" or
    //    "1. Ana (3 min, estimate after first student)".
    //
    // 2. D4 Exceeds for "View connected to the model" is "the view keeps
    //    no copy of model state", and the `latest` field below is a copy.
    //    Remove it: draw the line inside update(snapshot) instead of
    //    storing it. Only draw when controller.isTaMode() is false, or
    //    the student and TA screens both print on every change. When
    //    snapshot.isClosed(), print the final stats there and set a
    //    boolean so run() stops. "3) Show the line" is no longer needed
    //    (the line redraws itself after every change), so the menu
    //    becomes 1 Join, 2 Leave, 3 TA login. ViewTest's scripts then use
    //    "3" for TA login.
    private LineSnapshot latest;

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
        latest = null;
    }


    /**
     * Stores the newest snapshot. Called by the model.
     *
     * @param snapshot
     *            the new line
     */
    @Override
    public void update(LineSnapshot snapshot) {
        latest = snapshot;
    }


    /**
     * The main menu loop. Runs until the line is closed or input ends.
     */
    public void run() {
        while (!latest.isClosed()) {
            System.out.println();

            LineEntry nowHelping = latest.getNowHelping();
            if (nowHelping != null) {
                System.out.println("Now helping: " + nowHelping.getName());
            }

            for (LineEntry entry : latest.getWaiting()) {
                System.out.println(entry.getPosition() + ". " + entry.getName()
                        + " (" + entry.getMinutesWaited() + " min)");
            }

            System.out.println("1) Join the line  2) Leave the line");
            System.out.println("3) Show the line  4) TA login");
            int choice = input.readMenuChoice("Choose: ", 4);

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
                    // The next iteration redraws the line.
                    break;

                case 4:
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

        System.out.println("Helped: " + latest.getHelpedCount());
        System.out.println("Average wait: "
                + String.format("%.1f", latest.getAverageWaitMinutes())
                + " min");
    }
}
