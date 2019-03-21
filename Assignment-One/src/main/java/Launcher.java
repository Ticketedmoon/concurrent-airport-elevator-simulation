package main.java;

// Launcher class - build and run with:
// 1. javac *.java
// 2. java Launcher
public class Launcher {

    public static void main(String[] args) {

        // Set Log format + Write log output to file & terminal.
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s - %5$s%6$s%n");
        new LogWriter();

        // NOTE: One can change parameter here to adjust how many person threads can spawn.
        Airport airport = new Airport(20);

        // If GUI parameter passed, disable GUI
        if(args.length > 0 && args[0].toLowerCase().equals("--disable-gui")) {
            airport.initialize();
        }
        // Otherwise, Start with GUI normally.
        else {
            new Thread(airport::initialize).start();
            new Thread(() -> {
                Display display = new Display(airport);
                display.initialize();
            }).start();
        }
    }

}