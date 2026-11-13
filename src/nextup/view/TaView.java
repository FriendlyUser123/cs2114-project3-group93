
package nextup.view;

import nextup.controller.ActionResult;
import nextup.controller.LineController;
import nextup.model.LineObserver;
import nextup.model.LineSnapshot;
import nextup.model.LineEntry;

public class TaView implements LineObserver {

    private final LineController controller;
    private final ConsoleInput input;
    private LineSnapshot latest;

    /**
     * Creates the TA view.
     *
     * @param controller
     *            where every action goes
     * @param input
     *            the shared input reader
     */
    public TaView(LineController controller, ConsoleInput input) {
        this.controller = controller;
        this.input = input;
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
     * At startup, asks the TA to choose a PIN until
     * controller.setTaPin(...) returns ok. Stops if input ends.
     */
    public void setUpPin() {
        while (true) {
            String pin = input.readLine("TA, choose a 4-8 digit PIN: ");
            if (pin == null) {
                return;
            }

            ActionResult result = controller.setTaPin(pin);
            show(result);

            if (result.isOk()) {
                return;
            }
        }
    }


    /**
     * The TA menu loop. StudentView calls this right after a successful
     * taLogin. Returns when the TA logs out, closes the line, or input
     * ends.
     */
    public void run() {
        while (controller.isTaMode()) {
            System.out.println();

            LineEntry current = latest.getNowHelping();
            if (current != null) {
                System.out.println("Now helping: " + current.getName()
                        + " (" + current.getQuestion() + ") - waited "
                        + current.getMinutesWaited() + " min");
            }

            for (LineEntry entry : latest.getWaiting()) {
                System.out.println(entry.getPosition() + ". " + entry.getName()
                        + " - " + entry.getQuestion() + " - "
                        + entry.getMinutesWaited() + " min");
            }

            System.out.println("Helped: " + latest.getHelpedCount()
                    + ", average wait " + latest.getAverageWaitMinutes() + " min");
            System.out.println("1) Call next  2) Finish  3) Put back");
            System.out.println("4) Close line  5) Log out");

            int choice = input.readMenuChoice("Choose: ", 5);
            switch (choice) {
                case 1:
                    show(controller.callNext());
                    break;
                case 2:
                    show(controller.finishCurrent());
                    break;
                case 3:
                    show(controller.putBack());
                    break;
                case 4:
                    show(controller.closeLine());
                    break;
                case 5:
                case -1:
                    controller.taLogout();
                    return;
            }
        }
    }


    /**
     * Prints a result's message. (Small helper so each menu branch is one
     * line.)
     *
     * @param result
     *            the outcome of an action
     */
    private void show(ActionResult result) {
        System.out.println(result.getMessage());
    }

}
