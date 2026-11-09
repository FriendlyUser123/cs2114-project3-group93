# Next Up: who does what for Deliverable 3

**Due: Fri Nov 13, 1:25 PM (start of lab).** We submit this repo's URL. The TA grades it live, so **everyone must be able to explain their own code.**

The whole program is already laid out: every class from our D2 spec exists with its signatures and Javadoc. **Your job is to replace every `TODO(YourName)` in your files.** Find yours with Eclipse **Search → File Search** for `TODO(Nicolas)`, `TODO(Ha Minh)`, or `TODO(Al-Farouq)`.

Every one of your methods currently throws `UnsupportedOperationException("TODO...")`, and every one of your tests calls `fail("TODO...")`. **You're done when none of those are left and all tests pass.**

---

## ✅ Aditya Banerjee: the line (model). DONE

- [x] `OfficeHoursLine`, `HelpRequest`, `LineSnapshot`, `LineEntry`, `LineObserver`, `TimeSource`, `LineRuleException`
- [x] Test helpers anyone can use: `FakeTimeSource` (a clock you set by hand) and `RecordingObserver` (counts updates)
- [x] `OfficeHoursLineTest`, `ModelTypesTest`, `ModelBoundaryTest` (28 tests, all pass once `LinkedDeque` works)
- [x] `nextup.demo.ModelDemo`: runs the model with no view (the "Model works" rubric row)
- [x] README: model part of **Reflection**, draft of **Next Steps**, attribution rows and GenAI note for the model
- [ ] Review everyone's pull request; final README pass on Thu Nov 12

## 🔲 Nicolas Marchese: the Queue. Needed by everyone, so first

| File | What to do |
|---|---|
| `src/nextup/model/LinkedDeque.java` | Fill in all 9 TODOs: constructor, `enqueue`, `dequeue`, `peek`, `isEmpty`, `size`, `addToFront`, `remove`, `toList`. Doubly linked with **head and tail sentinels** (same idea as Lab 10's DLList). Each TODO has the steps. |
| `src/nextup/model/LinkedDequeTest.java` | Replace each `fail(...)` with the cases written above it (from our D2 test plan). **This is the Exceeds box for "Model works"**: "core Queue methods already have passing JUnit tests." |
| Both files' headers | Your name, PID, and the true LLM statement. |
| README | Add your rows' "how" in the **Attribution** table, and one bullet under **Reflection → What changed** (did anything differ from the D2 signatures, and why?). |

**Target: Mon Nov 9.** When you're done, **`OfficeHoursLineTest` also turns green.** That's how you know the deque works with the real model.

## 🔲 Ha Minh Nguyen: the controller and first full run

| File | What to do |
|---|---|
| `src/nextup/controller/ActionResult.java` | Constructor, `ok`, `fail`, `isOk`, `getMessage`. |
| `src/nextup/controller/LineController.java` | All TODOs. The class comment has a worked example of the try/catch pattern for `join`; every other method follows it. It also lists the exact messages to use. |
| `src/nextup/controller/Runner.java` | `main` wires everything in 7 steps (listed in the TODO). |
| `src/nextup/controller/LineControllerTest.java` | Replace each `fail(...)`. Uses the **real** model with `FakeTimeSource` and `RecordingObserver`; no view. |
| README | Attribution "how" for your rows, a Reflection bullet for the controller, and **"What doesn't work yet"** after the first full run. |

**Target: Tue Nov 10.** Then on **Wed Nov 11**, run `Runner`, walk the whole MVP path plus every bad-input case in the D2 validation table, and post what broke in the group chat. That's the "Runs without crashing" rubric row, and Exceeds needs every bad-input case to survive.

## 🔲 Al-Farouq Mohamed: the views

| File | What to do |
|---|---|
| `src/nextup/view/ConsoleInput.java` | `readLine` and `readMenuChoice`. The steps are in the TODOs. |
| `src/nextup/view/StudentView.java` | `update` and `run` (student menu: 1 Join, 2 Leave, 3 Show, 4 TA login). **Names only, never questions.** |
| `src/nextup/view/TaView.java` | `update`, `setUpPin`, `run` (TA menu: 1 Call next, 2 Finish, 3 Put back, 4 Close, 5 Log out), and `show`. |
| `src/nextup/view/ConsoleInputTest.java` | Replace each `fail(...)` (case "abc", "9", "2" → 2 is from our D2 test plan). |
| `src/nextup/view/ViewTest.java` | Replace each `fail(...)`. The class comment has a full example that scripts the keyboard. |
| README | Attribution "how" for your rows, a Reflection bullet for the views, and the **"How to run the thin view"** steps once it works. |

**Target: Tue Nov 10.** The Exceeds box for "Thin view" is that **the view already goes through the controller**, so call `LineController` and never the model.

**View rules** (from our spec, and the TA will check them): views import only `nextup.controller` and `LineObserver` / `LineSnapshot` / `LineEntry` from the model. Never `OfficeHoursLine`, `LinkedDeque`, or `HelpRequest`. Views never check a line rule; they print `result.getMessage()`.

---

## Everyone: Thu Nov 12

- [ ] All tests green, and `Runner` runs the MVP path with no crash
- [ ] No `TODO(` left anywhere (Eclipse File Search)
- [ ] Your name and LLM statement in every file you own
- [ ] Each person's README parts are in (see above)
- [ ] **Walk through all three layers together**: each person explains their own classes, as practice for the oral exam
- [ ] Each person submits their own **D3 GenAI Transcript** on Canvas (individual, 3–5 sentences plus chat links)

## How to work in the repo

1. **Clone:** in Eclipse, File → Import → Git → Projects from Git → Clone URI. Paste the repo URL, then import the existing project `NextUp`. You need **CS2-Support** in your workspace (same as the labs).
2. **Branch:** `git checkout -b your-name-part`. Commit as you go.
3. **Pull request:** push the branch and open a PR to `main`. Someone else reads it and merges it, as our D2 spec says. Don't push straight to `main`.
4. **Signatures are locked.** If you need to change one, tell the group first. Other people's code calls it.

**Run the tests:** right-click `src`, then Run As → JUnit Test.
