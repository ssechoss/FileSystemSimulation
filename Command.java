package fileSystemSimulation;

import fileSystemSimulation.DirectoryBlock;

enum OpenType {
    input, output, update, close
}

public class Command {

    private Sector[] sector;
    private int nextFreeS;
    private int nextFreeBlock;
    private FileBlock file;
    private Sector currentSector;

    private int numOfFreeBlock = 99;
    private int numOfDirBlock = 1;
    private int numOfFileBlock = 0;

    public static final int NAME_SIZE = 9;
    public static final int DIR_SIZE = 31;
    public static final int SECTOR_SIZE = 100;
    public static final int FILE_SIZE = 504;

    private boolean work;

    private OpenType openType;
    private FileBlock openFile;
    private int pointer;
    private int size;

    private Directory fileDir;
    private String fileName = "";
    private int fileSize;

    public Command() {
        sector = new Sector[SECTOR_SIZE];
        sector[0] = new DirectoryBlock();
        nextFreeS = 1;
        pointer = 1;
        openType = OpenType.close;
        work = true;
    }

    public boolean isWork() {
        return work;
    }

    public void close() {
        openType = OpenType.close;
        openFile = null;
    }

    public void delete(String name) {
        String[] part = name.split("/");
        Sector currentSector = sector[0];
        int i;
        for (i = 0; i < part.length; i++) {
            Directory d = ((DirectoryBlock) currentSector).search(part[i]);
            if (d == null) {
                work = false;
                System.out.println(name + " does not exist");
                return;
            } else {
                currentSector = d.getLink();
                {

                    if (d.getType() == 'u' && i == part.length - 1) {
                        Sector next;
                        d.intialize();
                        while (currentSector != null) {
                            next = currentSector.getForward();
                            currentSector.initialize();
                            currentSector = null;
                            numOfFreeBlock += 1;
                            numOfFileBlock -= 1;
                            currentSector = next;
                        }
                    } else if (d.getType() == 'd' && i == part.length - 1) {
                        System.out.println("cannot delete a directory");
                    }
                }
            }
        }
    }

    public void create(String type, String name) {
        if (type.equals("u")) {
            createFile(name);
            //System.out.println("file " + name + " is created");
        } else if (type.equals("d")) {
            createDir(name);
            //System.out.println("directory " + name + " is created");
        } else {
            System.out.println("wrong create command");
            work = false;
        }
    }

    private void createFile(String name) {
        String[] part = name.split("/");
        Sector currentSector = sector[0];
        Sector previousSector;
        for (int i = 0; i < part.length; i++) {
            if (part[i].length() > NAME_SIZE) {
                System.out.println("invalid file name");
                work = false;
                return;
            }
        }
        for (int i = 0; i < part.length; i++) {
            Directory d = ((DirectoryBlock) currentSector).search(part[i]);
            if (d == null && i < part.length - 1) {// dir not exist

                currentSector = createNew('d', part[i], currentSector);

            } else if (d == null && i == part.length - 1) {
                currentSector = createNew('u', part[i], currentSector);

                openFile = (FileBlock) currentSector;
                openType = OpenType.output;
                pointer = 0;
                size = 0;
            } else if (d.getType() == 'd' && i < part.length - 1) {
                currentSector = d.getLink();
            } else if (d.getType() == 'u' && i == part.length - 1) {
                delete(name);
                create("u", name);
            } else {// new file's directory contain exist file
                System.out.println("wrong command:there is a name collision");
                work = false;
                return;
            }

        }

    }

    private void createDir(String name) {
        String[] part = name.split("/");
        Sector currentSector = sector[0];
        for (int i = 0; i < part.length; i++) {
            if (part[i].length() > NAME_SIZE) {

                System.out.println("invalid directory name");
                return;
            }
        }

        for (int i = 0; i < part.length; i++) {
            Directory d = ((DirectoryBlock) currentSector).search(part[i]);
            if (d == null) {// dir not exist              
                currentSector = createNew('d', part[i], currentSector);
            } else if (d.getType() == 'd') {
                currentSector = d.getLink();
            } else {// new file's directory contain exist file
                // work = false;
                System.out.println("wrong command:there is a name collision");
                return;
            }
        }
        work = false;
    }

    public Sector createNew(char type, String partName, Sector currentSector) {
        Sector s = nextAvilableBlk(type);
        work = true;
        Directory temp = ((DirectoryBlock) currentSector).add(type, partName, s);
        if (temp != null) {
            if (type == 'u') {
                fileDir = temp;
            }
            return s;
        } else {
            DirectoryBlock previous
                    = (DirectoryBlock) currentSector.getForward();
            DirectoryBlock newDir = (DirectoryBlock) nextAvilableBlk('d');
            //if current directory block is filled,build a new one
            newDir.setBack(previous);
            newDir.add(type, partName, s);
            previous.setForward(newDir);
            return s;
        }
    }

    private Sector nextAvilableBlk(char type) {
        int i;
        for (i = 0; i < SECTOR_SIZE && sector[nextFreeS] != null
                && !sector[nextFreeS].isFree(); i++) {
            nextFreeS++;
            nextFreeS %= 100;
        }
        if (i == SECTOR_SIZE - 1 && !sector[nextFreeS].isFree()) {
            System.out.println("disk is full ");
            return null;
        } else if (type == 'u') {
            sector[nextFreeS] = new FileBlock();
            numOfFileBlock += 1;
            numOfFreeBlock -= 1;
            sector[nextFreeS].setFree(false);
        } else if (type == 'd') {
            sector[nextFreeS] = new DirectoryBlock();
            sector[nextFreeS].setFree(false);
            numOfDirBlock += 1;
            numOfFreeBlock -= 1;
        }
        return sector[nextFreeS];
    }

    public void loop(Sector currentSector) {
        for (int i = 0; i < DIR_SIZE; i++) {
            if (currentSector instanceof DirectoryBlock) {
                Directory d = ((DirectoryBlock) currentSector).
                        sweep(((DirectoryBlock) currentSector).
                                getDirectory()[i]);
                if (d == null) {
                    String[] part = fileName.split("/");
                    if (part.length <= 2) {
                        fileName = "";
                    } else {
                        for (int j = 0; j < part.length - 1; j++) {
                            fileName = "/" + part[j];
                        }
                    }
                    return;
                } else if (d.getType() == 'u') {
                    fileName = fileName + "/" + String.valueOf(d.getName());
                    fileSize = d.getSize();
                    Sector s = d.getLink();
                    while (s.getForward() != null) {
                        fileSize += FILE_SIZE;
                        s = s.getForward();
                    }
                    System.out.println("fileName: "
                            + fileName.substring(1, fileName.length() - 1)
                            + " fileSize= " + fileSize);
                    String[] part = fileName.split("/");
                    if (part.length <= 2) {
                        fileName = "";
                    } else {
                        for (int j = 1; j < part.length - 1; j++) {
                            fileName = "/" + part[j];
                        }
                    }
                } else if (d.getType() == 'd') {
                    fileName = fileName + "/" + String.valueOf(d.getName());
                    loop(d.getLink());
                }
            }
        }
    }

    public void display() {
        System.out.println("------------------------Now exist files----------"
                + "---------------------");

        loop(sector[0]);

        System.out.println("------------------------Now block numbers---------"
                + "--------------------");
        System.out.println("**free block = " + numOfFreeBlock
                + ", file block = " + numOfFileBlock
                + ", directory block = " + numOfDirBlock);
        System.out.println("-----------------------------End------------------"
                + "--------------------");

    }

    public void open(String mode, String name) {
        if (mode.equals("i")) {
            openType = OpenType.input;

        } else if (mode.equals("u")) {
            openType = OpenType.update;

        } else if (mode.equals("o")) {
            openType = OpenType.output;

        } else {
            work = false;
            System.out.println("wrong open command");
        }
        String[] part = name.split("/");
        Sector currentSector = sector[0];

        int i;
        for (i = 0; i < part.length; i++) {
            Directory d = ((DirectoryBlock) currentSector).search(part[i]);
            if (d == null) {
                work = false;
                System.out.println(name + " does not exist");
                return;
            } else {
                currentSector = d.getLink();
                if (d.getType() == 'd' && i == part.length - 1) {
                    work = false;
                    System.out.println(name + " does not exist");
                    return;
                } else if (d.getType() == 'u' && i == part.length - 1) {
                    fileDir = d;
                    openFile = (FileBlock) d.getLink();
                    size = d.getSize();
                    while (openFile.getForward() != null) {
                        size += FILE_SIZE;
                        openFile = (FileBlock) openFile.getForward();
                    }//get the file's whole size;
                    position(size);
                }
                work = true;
            }
        }
    }

    public void position(int size) {
        if (openType == OpenType.input || openType == OpenType.update) {
            pointer = 0;
        } else if (openType == OpenType.output) {
            pointer = size;
        } else {
            System.out.println("closed file");
            return;
        }
    }

    public void read(String num) {
        int n = Integer.parseInt(num);
        String s = String.valueOf(openFile.getFile());
        int toRead;
        if (n >= size - pointer) {
            n = size - pointer;
            System.out.print("The end of the file is reached. Read: ");
        } else {
            System.out.print("Read: ");
        }
        while (n != 0) {
            if (n <= FILE_SIZE - (pointer % FILE_SIZE)) {
                System.out.println(s.substring(pointer % FILE_SIZE,
                        n + pointer % FILE_SIZE));
                pointer = pointer + n;
                n = 0;
            } else {
                toRead = FILE_SIZE - (pointer % FILE_SIZE);
                System.out.println(s.substring(pointer % FILE_SIZE, FILE_SIZE));
                n = n - toRead;
                pointer = pointer + toRead;
                openFile = (FileBlock) openFile.getForward();
            }
        }
    }

    public void write(String num, String data) {
        int n = Integer.parseInt(num);
        data = data.substring(1, data.length() - 1);
        if (data.length() < n) {
            data = String.format("%-" + n + "s", data);
            //append sufficient blank
        }

        char[] source = data.toCharArray();
        char[] arr = openFile.getFile();

        int size0 = fileDir.getSize();//bytes # in last file block

        do {
            if (n < FILE_SIZE - pointer % FILE_SIZE && n >= 0) {
                System.arraycopy(source, 0, openFile.getFile(),
                        pointer % FILE_SIZE, n);
                //write update data after pointer;
                pointer += n;
                size = Math.max(pointer, size);
                fileDir.setSize(size % FILE_SIZE);
                //whole file size;
                n = 0;
                System.out.print("FILE DATA: ");
                System.out.println(openFile.getFile());
            } else if (n > FILE_SIZE - pointer % FILE_SIZE) {
                size = Math.max(n + pointer, size);
                fileDir.setSize(size % FILE_SIZE);
                //write update data after pointer;
                int toWrite = FILE_SIZE - pointer % FILE_SIZE;
                System.arraycopy(source, 0, openFile.getFile(),
                        pointer % FILE_SIZE, toWrite);
                try {
                    Sector newFileSector;
                    if (openFile.getForward() != null) {
                        newFileSector = openFile.getForward();
                    } else {
                        newFileSector = nextAvilableBlk('u');
                    }
                    newFileSector.setBack(openFile);
                    openFile.setForward(newFileSector);
                    openFile = (FileBlock) newFileSector;
                    n = n - toWrite;

                    pointer = pointer + toWrite;
                } catch (Exception e) {

                    System.out.println("the disk if full");
                } finally {
                    pointer = (n + pointer) % FILE_SIZE;
                }
            } else {
                System.out.println("wrong write command");
                return;
            }
        } while (n > 0);       
    }

    public void seek(String base, String offset) {// offset must be a number
        int n = Integer.parseInt(offset);
        //n<0 means backward;n>0 means forward;

        if (base.equals("0")) {
            if (n > -pointer && n < 0) {// seek forward;
                if (n >= -pointer % FILE_SIZE && n < 0) {
                    pointer = pointer + n;
                } else {
                    n = n + pointer % FILE_SIZE;
                    pointer = pointer - pointer % FILE_SIZE;

                    while (n != 0) {
                        openFile = (FileBlock) openFile.getBack();//
                        if (n >= -FILE_SIZE) {
                            pointer = pointer + n;
                            n = 0;
                        } else {
                            pointer = pointer - FILE_SIZE;
                            n += FILE_SIZE;
                        }
                    }
                }
            } else if (n > 0 && n < size - pointer) {//seek backward;
                if (n <= FILE_SIZE - pointer % FILE_SIZE) {
                    pointer = pointer + n;
                } else {
                    n = n - FILE_SIZE + pointer % FILE_SIZE;
                    pointer = pointer + FILE_SIZE - pointer % FILE_SIZE;
                    while (n != 0) {
                        openFile = (FileBlock) openFile.getForward();
                        if (n <= FILE_SIZE) {
                            pointer += n;
                            n = 0;
                        } else {
                            pointer += FILE_SIZE;
                            n = n - FILE_SIZE;
                        }
                    }
                }
            } else if (n >= size - pointer) {
                while (openFile.getForward() != null) {
                    openFile = (FileBlock) openFile.getForward();
                }
                pointer = size;
                System.out.println("pointer already reach the end");
            } else if (n <= -pointer) {
                openFile = (FileBlock) fileDir.getLink();
                pointer = 0;
                System.out.println("pointer alread reach the beginning");
            }
            System.out.println("pointer=" + pointer);
        } else if (base.equals("-1")) {// seek from the beginning
            openFile = (FileBlock) fileDir.getLink();
            pointer = 0;
            if (n <= size && n > 0) {
                while (n != 0) {
                    if (n <= FILE_SIZE) {
                        pointer += n;
                        n = 0;
                    } else {
                        pointer += FILE_SIZE;
                        openFile = (FileBlock) openFile.getForward();
                        n = n - FILE_SIZE;
                    }
                }
            } else if (n > size) {
                while (openFile.getForward() != null) {
                    openFile = (FileBlock) openFile.getForward();
                }
                pointer = size;
                System.out.println("pointer already reach the end");
            } else if (n <= 0) {
                pointer = 0;
                System.out.println("pointer already reach the beginning");
            }
            System.out.println("pointer=" + pointer);
        } else if (base.equals("+1") || base.equals("1")) {//seek from the end;
            openFile = (FileBlock) fileDir.getLink();
            if (n >= 0) {
                while (openFile.getForward() != null) {
                    openFile = (FileBlock) openFile.getForward();
                }
                pointer = size;
                System.out.println("pointer already reach the end");
            } else if (n > -size && n < 0) {
                while (openFile.getForward() != null) {
                    openFile = (FileBlock) openFile.getForward();
                }
                pointer = size;
                if (n >= -fileDir.getSize() && n < 0) {
                    pointer = pointer + n;
                } else {
                    pointer = pointer - fileDir.getSize();
                    n = n + fileDir.getSize();
                    while (n != 0) {
                        openFile = (FileBlock) openFile.getBack();
                        if (n >= -FILE_SIZE) {
                            pointer = pointer + n;
                            n = 0;
                        } else {
                            pointer = pointer - FILE_SIZE;
                            n += FILE_SIZE;
                        }
                    }
                }
            } else if (n <= -size) {
                pointer = 0;
                System.out.println("pointer already reach the beginning");
            }
            System.out.println("pointer=" + pointer);
        } else {
            System.out.println("wrong seek command");
        }
    }
}
