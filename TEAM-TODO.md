# Next Up: who does what for Deliverable 4

**We present on Wed Nov 18.** Canvas still lists Fri Nov 20, 1:25 PM. Same repo, graded live as an oral exam, and the whole group attends.

> ⚠️ **The 50% rule (from the D4 assignment):** "Group members not contributing substantively to at least one class or test file will receive a 50% deduction on their group's deliverable." Everyone below has a real test or class file to write and commit **under your own GitHub account**.

**Status:** every assigned part is merged, and all 70 tests pass.

**One item is open:** Al-Farouq signs the Honor Pledge in `StudentView`, `TaView`, `ViewTest`, and `ConsoleInputTest`. Only he can sign it.

---

## ✅ Aditya Banerjee: the change feature in the model. DONE

- [x] `OfficeHoursLine`: wait-time estimates (`totalHelpMillis`, `finishCurrent`, `estimateMinutes(int)`, `getSnapshot`), exactly the D3 prediction
- [x] `LineEntry`: `estimatedWaitMinutes` and `getEstimatedWaitMinutes()` (`-1` = no estimate yet, `0` = being helped)
- [x] 5 new tests in `OfficeHoursLineTest`, plus `ModelTypesTest` cases (`-2` throws)
- [x] `ModelDemo` prints the estimate: `1.Cal(7m, est 4m) 2.Dee(0m, est 8m)`
- [x] README: the D4 Reflection model rows, Next Steps, the timed demo draft, attribution, GenAI note
- [x] Reviewed everyone's work; final README pass on Wed Nov 18

**What you can use:** `entry.getEstimatedWaitMinutes()` on any `LineEntry` in a snapshot.

## ✅ Nicolas Marchese: the interleaved queue test and the Reflection. DONE

| File | What to do |
|---|---|
| `src/nextup/model/LinkedDequeTest.java` | Write `testInterleavedOrder`. The cases are listed in its comment, one assert per step. **This is the Exceeds box for "JUnit Tests."** |
| README → Reflection → "What design change would make the next feature cheaper" | One paragraph. **This is the Exceeds box for "Reflection."** There's a starting hint in the README. |
| README → "Not in the prediction" | Add anything that surprised you once the others are merged. |
| README → Attribution and GenAI note | Your `LinkedDequeTest` row and one GenAI sentence. |

**Done.**

## ✅ Ha Minh Nguyen: every bad input gets a test. DONE (13 tests green, controller unchanged)

| File | What to do |
|---|---|
| `src/nextup/controller/BadInputTest.java` (**new file, yours**) | 13 test methods, one per row of our D2 validation table plus the new estimate case. Each comment lists the exact inputs and messages, and the class comment has the 4-line pattern every test follows. **This is the Exceeds box for "Bad input survives."** |
| Header of that file | Your name, PID, and the true LLM statement. |
| README → "Bad input → test" table | Check every row against your tests and fix any test names that differ. |
| README → Reflection → `LineController` row | Run the `git diff` command written there. No output means the controller didn't change, which is the Exceeds box for "Change feature." **Don't edit `LineController` or `ActionResult` in D4.** |
| README → Attribution and GenAI note | Your `BadInputTest` row and one GenAI sentence. |

**Done.**

## ✅ Al-Farouq Mohamed: the views show the estimate and keep no copy. DONE (merged in PR #2)

| File | What to do |
|---|---|
| `src/nextup/view/StudentView.java` | (1) Print the estimate next to each name; (2) remove `latest` and draw inside `update()`. |
| `src/nextup/view/TaView.java` | The same two changes, with questions shown. |
| `src/nextup/view/ViewTest.java` | Write `testShowsEstimates` and `testLeaveAndPutBack` (cases in their comments). If you change the student menu to 1 Join, 2 Leave, 3 TA login, also change the existing scripts' `4` (TA login) to `3`. |
| README → Reflection | The `StudentView`/`TaView` row, "Did the change cross the model/view line?", and a views row in "What changed from our D3 plan". |
| README → Attribution and GenAI note | Your view rows and one GenAI sentence. |

**Why change "keeps a copy":** the Exceeds box for "View connected to the model" is "View keeps no copy of model state." Right now each view stores the last snapshot in `latest`.

**Rules (unchanged):** views import only `nextup.controller` and `LineObserver`, `LineSnapshot`, and `LineEntry` from the model. **Don't change `ConsoleInput` or `Runner`**, because our D3 prediction says they stay the same.

**Done.**

---

## Everyone: before we present

- [x] All tests green (right-click `src` → Run As → JUnit Test), and `Runner` runs the demo path with no crash
- [x] No `TODO(` left anywhere (Eclipse File Search)
- [ ] Your name, Honor Pledge, and LLM statement in every file you own (Al-Farouq: see above)
- [x] README: "What doesn't work yet", the stretch-goal decision (commit to or drop the Swing board), and who drives each part of the demo
- [ ] **Rehearse the 3-minute demo twice, timed.** The Exceeds box for Next Steps is that the demo fits in 3 minutes.
- [ ] **Walk through every class together.** The TA can ask anyone about anything.

## Don't break these (they're Exceeds boxes)

1. **Don't edit `LineController`, `ActionResult`, `LinkedDeque`, `LineSnapshot`, `ConsoleInput`, or `Runner`.** Our D3 prediction said they wouldn't change. One later exception: Aditya added the Swing board stretch goal to `Runner`; the estimate feature itself never touched it.
2. **Tests must target the model, not the UI.** `ViewTest` is extra; the graded tests are the model and queue tests.

## How to work in the repo

1. **Pull first:** `git pull`, or in Eclipse, right-click NextUp → Team → Pull.
2. **Commit as yourself.** Your name on the commit is what proves the 50% rule doesn't apply to you.
3. **Run from a terminal?** Build into `out`, never `bin` (see the README). A terminal build into `bin` breaks Eclipse.
