import duke.tasks.Fixed;
import duke.tasks.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class FixedTest {

    @org.junit.jupiter.api.Test
    void testToString() {
        Task t = new Fixed("read sales report", "2 hours");
        assertEquals("[F][✗] read sales report (needs: 2 hours)", t.toString());
        assertNotEquals("[F][✓] read sales report (needs: 2 hours)", toString());
        t.markDone();
        assertEquals("[F][✓] read sales report (needs: 2 hours)", t.toString());
        assertNotEquals("[F][✗] read sales report (needs: 2 hours)", toString());
    }

    @org.junit.jupiter.api.Test
    void storeString() {
        Task t = new Fixed("read sales report", "2 hours");
        assertEquals("F | 0 | read sales report | 2 hours", t.storeString());
        assertNotEquals("F | 1 | read sales report | 2 hours", t.storeString());
        t.markDone();
        assertEquals("F | 1 | read sales report | 2 hours", t.storeString());
        assertNotEquals("F | 0 | read sales report | 2 hours", t.storeString());
    }
}