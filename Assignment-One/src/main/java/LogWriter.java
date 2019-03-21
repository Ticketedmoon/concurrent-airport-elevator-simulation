package main.java;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogWriter {

    private FileHandler fh;

    public LogWriter(){

        // Obtain logs from 3 logging classes.
        Logger airport_logs = Logger.getLogger(Airport.class.getName());
        Logger person_logs = Logger.getLogger(Person.class.getName());
        Logger elevator_logs = Logger.getLogger(Elevator.class.getName());

        try {
            // Reset file 'output.dat' in case of previous written fragments.
            PrintWriter writer = new PrintWriter("./output.dat");
            writer.print("");
            writer.close();

            // Write to file in desired format.
            fh = new FileHandler("output.dat", false);
            fh.setFormatter(new SimpleFormatter() {
                @Override
                public synchronized String format(LogRecord lr) {
                    return String.format("%1$tF %1$tT %2$-4s - %3$s %n", new Date(lr.getMillis()), lr.getLevel().getLocalizedName(), lr.getMessage());
                }

            });

            // Add logs to handler
            airport_logs.addHandler(fh);
            person_logs.addHandler(fh);
            elevator_logs.addHandler(fh);
        }catch (IOException e){
            e.printStackTrace();
        }
        finally {
            if(fh != null) {
                fh.flush();
            }
        }
    }
}
