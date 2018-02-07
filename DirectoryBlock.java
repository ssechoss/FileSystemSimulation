package fileSystemSimulation;

import static fileSystemSimulation.Command.DIR_SIZE;
import fileSystemSimulation.Directory;
import java.util.LinkedList;
import java.util.List;

public class DirectoryBlock extends Sector {

    private int free;
    private char filler;
    private Directory[] directory;

    public DirectoryBlock() {
        directory = new Directory[DIR_SIZE];
        for (int i = 0; i < DIR_SIZE; i++) {
            directory[i] = new Directory();
        }
    }

    public Directory[] getDirectory() {
        return directory;
    }

    public void setDirectory(Directory[] directory) {
        this.directory = directory;
    }

    public Directory search(String name) {

        for (int i = 0; i < DIR_SIZE; i++) {
            if (directory[i].hasSameName(name) && directory[i].getType() != 'f') {
                return directory[i];
            }
        }
        return null;
    }

    public Directory add(char type, String name, Sector link) {

        for (int i = 0; i < DIR_SIZE; i++) {
            if (directory[i].getType() == 'f') {
                directory[i].setDirectory(type, name, link);
                return directory[i];
            }
        }
        return null;
    }

    public Directory sweep(Directory d) {
        if (d.getType() != 'f') {
            return d;
        }
        return null;
    }

}
