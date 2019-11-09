package spinbox.containers.lists;

import spinbox.storage.Storage;
import spinbox.exceptions.CorruptedDataException;
import spinbox.exceptions.FileCreationException;
import spinbox.exceptions.DataReadWriteException;
import spinbox.entities.items.File;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FileList extends SpinBoxList<File> {
    private static final String FILE_LIST_FILE_NAME = "/files.txt";

    public FileList(String parentName) throws FileCreationException {
        super(parentName);
        localStorage = new Storage(DIRECTORY_NAME + this.getParentCode() + FILE_LIST_FILE_NAME);
    }

    /**
     * Order the files in alphabetical order.
     */
    static class FileComparator implements Comparator<File> {
        @Override
        public int compare(File a, File b) {
            return a.getName().compareToIgnoreCase(b.getName());
        }
    }

    public void sort() {
        list.sort(new FileComparator());
    }

    @Override
    public void loadData() throws DataReadWriteException, CorruptedDataException {
        List<String> savedData = localStorage.loadData();
        for (String datum : savedData) {
            File file = new File();
            file.fromStoredString(datum);
            this.addFromStorage(file);
        }
    }

    @Override
    public void saveData() throws DataReadWriteException {
        List<String> dataToSave = new ArrayList<>();
        for (File file: this.getList()) {
            dataToSave.add(file.storeString());
        }
        localStorage.saveData(dataToSave);
    }

    @Override
    public List<String> viewList() {
        List<String> output = new ArrayList<>();
        output.add("Here are the files in your module:");
        for (int i = 0; i < list.size(); i++) {
            output.add(((i + 1) + ". " + list.get(i).toString()));
        }
        return output;
    }

    @Override
    public List<String> containsKeyword(String keyword) {
        List<File> contains = new ArrayList<>();
        for (File file : this.getList()) {
            if (file.getName().toLowerCase().contains(keyword)) {
                contains.add(file);
            }
        }

        contains.sort(new FileComparator());

        List<String> output = new ArrayList<>();
        output.add("Here are the files that contain " + keyword
                + " in your module:");
        for (int i = 0; i < contains.size(); i++) {
            output.add(((i + 1) + ". " + contains.get(i).toString()));
        }

        return output;
    }
}
