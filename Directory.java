package fileSystemSimulation;

import static fileSystemSimulation.Command.NAME_SIZE;

public class Directory {

    private char type;
    private char[] name;
    private Sector link;// block number of first block of files.
    private int size;

    public Directory() {
        type = 'f';
        name = new char[NAME_SIZE];
        size = 0;
    }

    public void intialize() {
        type = 'f';
        name = new char[NAME_SIZE];
        size = 0;
    }

    public void setDirectory(char type, String name, Sector link) {
        this.type = type;
        this.name = new char[NAME_SIZE];
        System.arraycopy(name.toCharArray(), 0, this.name, 0, 
                Math.min(name.length(), NAME_SIZE));
        this.link = link;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public char[] getName() {
        return name;
    }

    public void setName(char[] name) {
        this.name = name;
    }

    public Sector getLink() {
        return link;
    }

    public void setLink(Sector link) {
        this.link = link;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean hasSameName(String n) {
        int i;
        for (i = 0; i < n.length(); i++) {
            if ((n.charAt(i) != name[i])) {
                return false;
            }
        }
        if (name[n.length()] != '\u0000') {
            return false;
        }
        return true;
    }
}
