/* 
 * Copyright 2017 Javier A. Ortiz Bultron javier.ortiz.78@gmail.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.validation.manager.core.tool;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class Timer {

    private long start, end;

    public Timer() {
        reset();
    }

    public void stop() {
        end = System.nanoTime();
    }

    public final void reset() {
        start = System.nanoTime();
    }

    public String elapsedTime() {
        long timeelapsed = (end - start);
        long milliseconds = timeelapsed / 1000;
        long seconds = (timeelapsed / 1000) % 60;
        long minutes = (timeelapsed / 60000) % 60;
        return " (" + minutes + ":" + seconds
                + ":" + milliseconds + ")";
    }
}
