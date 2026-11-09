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
// (Anthropic) to write these tests from our Deliverable 2 test plan. After
// using this tool, I reviewed and edited the content as needed to ensure its
// accuracy and take full responsibility for the content in relation to
// grading. I understand that I am responsible for being able to complete this
// work without the use of assistance.

package nextup.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Checks the model/view line in the source code itself (from our D2 test
 * plan): no file in nextup.model may import nextup.view or
 * nextup.controller, or print with System.out. If someone breaks the
 * boundary, this test names the file.
 *
 * It reads the .java files from src/nextup/model, so run it with the
 * project folder as the working directory (Eclipse does this by default).
 *
 * @author Aditya Banerjee (adityab7)
 * @version 2026.11.08
 */
public class ModelBoundaryTest extends student.TestCase {

    /**
     * Every model source file stays on the model side of the line.
     *
     * @throws IOException
     *             if a file cannot be read
     */
    public void testModelDoesNotKnowViewOrController() throws IOException {
        File folder = new File("src/nextup/model");
        File[] files = folder.listFiles();
        assertNotNull("Run from the project folder", files);
        int checked = 0;
        for (File file : files) {
            if (!file.getName().endsWith(".java")
                || file.getName().endsWith("Test.java")) {
                continue;
            }
            String code = new String(Files.readAllBytes(file.toPath()),
                "UTF-8");
            assertFalse(file.getName() + " imports the view",
                code.contains("import nextup.view"));
            assertFalse(file.getName() + " imports the controller",
                code.contains("import nextup.controller"));
            assertFalse(file.getName() + " prints",
                code.contains("System.out"));
            checked++;
        }
        assertTrue(checked >= 8);
    }
}
