// CS 2114 Project 3 | Al-Farouq Mohamed
// GenAI disclosure: Al-Farouq Mohamed used ChatGPT (OpenAI) to help write
// these tests. The test names and cases were laid out by Aditya Banerjee with
// Claude (Anthropic). See the attribution table in README.md.

package nextup.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.regex.Pattern;
import org.junit.Test;

/**
 * Tests ConsoleInput with scripted input instead of the keyboard.
 *
 * @author Al-Farouq Mohamed (alfarouqm18)
 * @version 2026.11.12
 */
public class ConsoleInputTest {

    @Test
    public void testReadLine() {
        ConsoleInput input = new ConsoleInput(new Scanner("Ana\n\n"));

        assertEquals("Ana", input.readLine("Name: "));
        assertEquals("", input.readLine("Name: "));
        assertNull(input.readLine("Name: "));
    }

    @Test
    public void testReadMenuChoice() {
        ConsoleInput input = new ConsoleInput(new Scanner("abc\n9\n2\n"));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try (PrintStream capturedOut = new PrintStream(output)) {
            System.setOut(capturedOut);
            assertEquals(2, input.readMenuChoice("Choose: ", 4));
        }
        finally {
            System.setOut(originalOut);
        }

        String message = "Enter a number 1 to 4.";
        int errorCount = output.toString()
                .split(Pattern.quote(message), -1).length - 1;
        assertEquals(2, errorCount);
    }

    @Test
    public void testReadMenuChoiceEndOfInput() {
        ConsoleInput input = new ConsoleInput(new Scanner("abc\n"));

        assertEquals(-1, input.readMenuChoice("Choose: ", 4));
    }
}