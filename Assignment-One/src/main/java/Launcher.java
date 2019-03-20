package main.java;

public class Launcher {

    public static void main(String[] args) {
        Airport airport = new Airport(50, 1);

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