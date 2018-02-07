package fileSystemSimulation;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Command c = new Command();

        while (true) {
            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine();

            String lowercaseCommand = command.toLowerCase();
            String[] splits = lowercaseCommand.split("\\s+");

            if (splits.length == 2 && splits[0].equals("delete")) {
                c.delete(splits[1]);
                c.display();
            } else if (splits.length == 3 && splits[0].equals("open")) {
                // open command
                c.open(splits[1], splits[2]);
                c.display();
                while (c.isWork()) {
                    Scanner scanner1 = new Scanner(System.in);
                    String command1 = scanner1.nextLine();
                    String lowercaseCommand1 = command1.toLowerCase();
                    String[] splits1 = lowercaseCommand1.split("\\s+", 3);
                    if (splits[1].equals("u")) {
                        if (splits1.length == 3 && splits1[0].equals("write")) {
                            c.write(splits1[1], splits1[2]);
                        } else if (splits1.length == 2
                                && splits1[0].equals("read")) {
                            c.read(splits1[1]);
                        } else if (splits1.length == 3
                                && splits1[0].equals("seek")) {
                            String[] s = splits1[2].split("\\s+");
                            if (s.length == 1) {
                                c.seek(splits1[1], splits1[2]);
                            } else {
                                System.out.println("invalid seek command");
                            }
                        } else if (splits1.length == 1
                                && splits1[0].equals("close")) {
                            c.close();
                            c.display();
                            break;
                        } else {
                            System.out.println("wrong command: "
                                    + "you can only do limited command now");
                        }
                        c.display();
                    } else if (splits[1].equals("i")) {
                        if (splits1.length == 2 && splits1[0].equals("read")) {
                            c.read(splits1[1]);
                        } else if (splits1.length == 3
                                && splits1[0].equals("seek")) {
                            String[] s = splits1[2].split("\\s+");
                            if (s.length == 1) {
                                c.seek(splits1[1], splits1[2]);
                            } else {
                                System.out.println("invalid seek command");
                            }
                        } else if (splits1.length == 1
                                && splits1[0].equals("close")) {
                            c.close();
                            c.display();
                            break;
                        } else {
                            System.out.println("wrong command:"
                                    + " you can only do limited command now");
                        }
                        c.display();
                    } else if (splits[1].equals("o")) {
                        if (splits1.length == 3 && splits1[0].equals("write")) {
                            c.write(splits1[1], splits1[2]);
                        } else if (splits1.length == 1
                                && splits1[0].equals("close")) {
                            c.close();
                            c.display();
                            break;
                        } else {
                            System.out.println("wrong command: "
                                    + "you can only do limited command now");
                        }
                        c.display();
                    }
                }
            } else if (splits.length == 3 && splits[0].equals("create")) {
                //create command
                c.create(splits[1], splits[2]);
                c.display();
                while (c.isWork()) {

                    Scanner scanner1 = new Scanner(System.in);
                    String command1 = scanner1.nextLine();

                    String lowercaseCommand1 = command1.toLowerCase();
                    String[] splits1 = lowercaseCommand1.split("\\s+", 3);

                    if (splits1.length == 3 && splits1[0].equals("write")) {
                        c.write(splits1[1], splits1[2]);
                    } else if (splits1.length == 1
                            && splits1[0].equals("close")) {
                        c.close();
                        c.display();
                        break;
                    } else {
                        System.out.println("Wrong command:"
                                + "you can only do limited command now");
                    }
                    c.display();
                }
            } else if (splits.length == 1 && splits[0].equals("exit")) {
                break;
            } else {
                System.out.println("Wrong command. "
                        + "Caution:you can only do delete,create, open command");
            }
        }
    }
}
