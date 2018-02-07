package fileSystemSimulation;

import static fileSystemSimulation.Command.FILE_SIZE;

public class FileBlock extends Sector {

    private char[] file;

    public FileBlock() {
        this.file = new char[FILE_SIZE];
        for (int i = 0; i < FILE_SIZE; i++) {
          file[i] = '\u0000';
     }       
    }

    public void setFile(char[] file) {
        this.file = file;
    }

    public char[] getFile() {
        return file;
    }
    

}
