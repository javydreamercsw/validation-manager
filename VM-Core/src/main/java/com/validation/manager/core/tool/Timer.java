package com.validation.manager.core.tool;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class Timer {

    private long start, end;

    public Timer() {
        reset();
    }

    public void stop() {
        end = System.nanoTime();
    }
    
    public void reset(){
        start = System.nanoTime();
    }

    public String elapsedTime() {
        long timeelapsed = (end - start);
        long milliseconds = timeelapsed / 1000;
        long seconds = (timeelapsed / 1000) % 60;
        long minutes = (timeelapsed / 60000) % 60;
        return "Time Elapsed: " + timeelapsed + ", Start Time:" + start
                + ", end Time: " + end + " (" + minutes + ":" + seconds 
                + ":" + milliseconds + " )";
    }
}
