//import duke.SpinBox;
//import org.junit.jupiter.api.Test;
//
//import java.io.File;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotEquals;
//
///**
// * Integration testing of SpinBox.
// */
//public class SpinBoxTest {
//    @Test
//    public void duke_createNewTask_NewTaskCreatedPermanently() {
//        File file = new File("data/dukeTest.txt");
//        file.delete();
//
//        //Set pseudo-GUI mode to allow us to manually input commands
//        SpinBox test = new SpinBox("data/dukeTest.txt", "data/fileTest.txt", false);
//
//        //Step 1: Check for empty list (successful creation of new file)
//        assertEquals(test.getResponse("list"),
//                "____________________________________________________________\n"
//                        + "Here are the tasks in your list:\n"
//                        + "____________________________________________________________");
//
//        //Step 2: Add a task
//        test.getResponse("todo test1");
//
//        //Step 3: Exit program
//        test.getResponse("bye");
//
//        //Step 4: Create a new instance of SpinBox
//        SpinBox test2 = new SpinBox("data/dukeTest.txt", "data/fileTest.txt", false);
//
//        //Step 5: Check data persists across SpinBox instances.
//        assertNotEquals(test.getResponse("list"),
//                "____________________________________________________________\n"
//                        + "Here are the tasks in your list:\n"
//                        + "____________________________________________________________");
//    }
//
//    @Test
//    public void duke_updateNonExistentTask_exceptionThrown() {
//        File file = new File("data/dukeTest.txt");
//        file.delete();
//
//        //Set pseudo-GUI mode to allow us to manually input commands
//        SpinBox test = new SpinBox("data/dukeTest.txt", "data/fileTest.txt", false);
//
//        //Step 1: Check for empty list (successful creation of new file)
//        assertEquals(test.getResponse("list"),
//                "____________________________________________________________\n"
//                        + "Here are the tasks in your list:\n"
//                        + "____________________________________________________________");
//
//        //Step 2: Add an item
//        test.getResponse("todo test1");
//
//        //Step 3: Update item
//        String testList = test.getResponse("list");
//
//        assertEquals(test.getResponse("done 2"),
//                "____________________________________________________________\n"
//                        + "Invalid Input\n\n"
//                        + "Invalid index entered. Type 'list' to see your list.\n"
//                        + "____________________________________________________________");
//
//        assertEquals(test.getResponse("done 0"),
//                "____________________________________________________________\n"
//                        + "Invalid Input\n\n"
//                        + "Invalid index entered. Type 'list' to see your list.\n"
//                        + "____________________________________________________________");
//
//        assertNotEquals(test.getResponse("done 1"),
//                "____________________________________________________________\n"
//                        + "Invalid Input\n\n"
//                        + "Invalid index entered. Type 'list' to see your list.\n"
//                        + "____________________________________________________________");
//    }
//
//    @Test
//    public void duke_updateExistingTask_ExistingTaskUpdatedPermanently() {
//        File file = new File("data/dukeTest.txt");
//        file.delete();
//
//        //Set pseudo-GUI mode to allow us to manually input commands
//        SpinBox test = new SpinBox("data/dukeTest.txt", "data/fileTest.txt", false);
//
//        //Step 1: Check for empty list (successful creation of new file)
//        assertEquals(test.getResponse("list"),
//                "____________________________________________________________\n"
//                        + "Here are the tasks in your list:\n"
//                        + "____________________________________________________________");
//
//        //Step 2: Add an item
//        test.getResponse("todo test1");
//
//        //Step 3: Update item
//        String testList = test.getResponse("list");
//        test.getResponse("done 1");
//
//        //Step 4: Exit program
//        test.getResponse("bye");
//
//        //Step 5: Create a new instance of SpinBox
//        SpinBox test2 = new SpinBox("data/dukeTest.txt", "data/fileTest.txt", false);
//        assertNotEquals(test2.getResponse("list"), testList);
//    }
//
//    @Test
//    public void duke_deleteNonExistentTask_exceptionThrown() {
//        File file = new File("data/dukeTest.txt");
//        file.delete();
//
//        //Set pseudo-GUI mode to allow us to manually input commands
//        SpinBox test = new SpinBox("data/dukeTest.txt", "data/fileTest.txt", false);
//
//        //Step 1: Check for empty list (successful creation of new file)
//        String testList = test.getResponse("list");
//        assertEquals(test.getResponse("list"),
//                "____________________________________________________________\n"
//                        + "Here are the tasks in your list:\n"
//                        + "____________________________________________________________");
//
//        //Step 2: Add an item
//        test.getResponse("todo test1");
//
//        //Step 3: Check item is added to list
//        assertNotEquals(test.getResponse("list"), testList);
//
//        //Step 4: Exit program
//        test.getResponse("bye");
//
//        //Step 5: Create a new instance of SpinBox
//        SpinBox test2 = new SpinBox("data/dukeTest.txt", "data/fileTest.txt", false);
//
//        //Step 6: Attempt to delete non-existent items
//        assertEquals(test2.getResponse("delete 0"),
//                "____________________________________________________________\n"
//                        + "Invalid Input\n\n"
//                        + "Invalid index entered. Type 'list' to see your list.\n"
//                        + "____________________________________________________________");
//
//        assertEquals(test2.getResponse("delete 2"),
//                "____________________________________________________________\n"
//                        + "Invalid Input\n\n"
//                        + "Invalid index entered. Type 'list' to see your list.\n"
//                        + "____________________________________________________________");
//
//        assertEquals(test2.getResponse("delete 0"),
//                "____________________________________________________________\n"
//                        + "Invalid Input\n\n"
//                        + "Invalid index entered. Type 'list' to see your list.\n"
//                        + "____________________________________________________________");
//
//        assertEquals(test2.getResponse("delete-multiple 1,2"),
//                "____________________________________________________________\n"
//                        + "Invalid Input\n\n"
//                        + "Invalid index entered. Type 'list' to see your list.\n"
//                        + "____________________________________________________________");
//
//        assertEquals(test2.getResponse("done-multiple 3,4"),
//                "____________________________________________________________\n"
//                        + "Invalid Input\n\n"
//                        + "Invalid index entered. Type 'list' to see your list.\n"
//                        + "____________________________________________________________");
//    }
//
//    @Test
//    public void duke_deleteExistingTask_ExistingTaskDeletedPermanently() {
//        File file = new File("data/dukeTest.txt");
//        file.delete();
//
//        //Set pseudo-GUI mode to allow us to manually input commands
//        SpinBox test = new SpinBox("data/dukeTest.txt", "data/fileTest.txt", false);
//
//        //Step 1: Check for empty list (successful creation of new file)
//        String testList = test.getResponse("list");
//        assertEquals(test.getResponse("list"),
//                "____________________________________________________________\n"
//                        + "Here are the tasks in your list:\n"
//                        + "____________________________________________________________");
//
//        //Step 2: Add an item
//        test.getResponse("todo test1");
//
//        //Step 3: Check item is added to list
//        assertNotEquals(test.getResponse("list"), testList);
//
//        //Step 4: Add another item
//        test.getResponse("todo test2");
//
//        //Step 5: Check item is added to list
//        assertNotEquals(test.getResponse("list"), testList);
//
//        //Step 6: Add another item
//        test.getResponse("todo test3");
//
//        //Step 7: Check item is added to list
//        assertNotEquals(test.getResponse("list"), testList);
//
//        //Step 8: Add another item
//        test.getResponse("todo test4");
//
//        //Step 9: Check item is added to list
//        assertNotEquals(test.getResponse("list"), testList);
//
//        //Step 10: Add another item
//        test.getResponse("todo test5");
//
//        //Step 11: Check item is added to list
//        assertNotEquals(test.getResponse("list"), testList);
//
//        //Step 12: Exit program
//        test.getResponse("bye");
//
//        //Step 13: Create a new instance of SpinBox
//        SpinBox test2 = new SpinBox("data/dukeTest.txt", "data/fileTest.txt", false);
//
//        //Step 14: Delete added item
//        test2.getResponse("delete 1");
//
//        //Step 15: Delete multiple added items
//        test2.getResponse("delete-multiple 1,2");
//
//        //Step 16: Mark multiple added items as done and delete these tasks
//        test2.getResponse("done-multiple 1,2");
//
//        // Exit program
//        test2.getResponse("bye");
//
//        //Step 17: Create a new instance of SpinBox
//        SpinBox test3 = new SpinBox("data/dukeTest.txt", "data/fileTest.txt", false);
//
//        //Step 18: Verify that list is empty
//        assertEquals(test3.getResponse("list"), testList);
//    }
//}