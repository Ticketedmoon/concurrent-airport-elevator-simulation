package main.java;

public class Launcher {

    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s - %5$s%6$s%n");
        new LogWriter();

        Airport airport = new Airport(10);

        // If GUI parameter passed, start gui
        if(args.length > 0 && args[0].toLowerCase().equals("--gui")) {
            new Thread(() -> airport.initialize()).start();
            new Thread(() -> {
                Display display = new Display(airport);
                display.initialize();
            }).start();
        }
        // Otherwise, start normally via logging to the terminal
        else {
            airport.initialize();
        }
    }

}