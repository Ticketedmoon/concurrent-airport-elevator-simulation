package main.java;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogWriter {

    private FileHandler fh;
    private Logger airport_logs = Logger.getLogger(Airport.class.getName());
    private Logger person_logs = Logger.getLogger(Person.class.getName());
    private Logger elevator_logs = Logger.getLogger(Elevator.class.getName());

    public LogWriter(){
        try {
            PrintWriter writer = new PrintWriter("./output.dat");
            writer.print("");
            writer.close();
            fh = new FileHandler("./output.dat", false);
            fh.setFormatter(new SimpleFormatter() {
                @Override
                public synchronized String format(LogRecord lr) {
                    return String.format("[%1$tF %1$tT] [%2$-4s] %3$s %n", new Date(lr.getMillis()), lr.getLevel().getLocalizedName(), lr.getMessage());
                }

            });
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
