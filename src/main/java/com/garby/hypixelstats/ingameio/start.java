/*
 * Copyright 2022 Garbyexe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.garby.hypixelstats.ingameio;

import com.garby.hypixelstats.stats;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class start {
    public static void main(String[] args) {

        if (!System.getProperty("os.name").contains("Windows")) {

            System.out.println("This program is currently only compatible with Windows.");
            System.exit(0);
        }
        final String fileName = "bedwarsStats.exe";


        final String jarPath = stats.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceFirst("/", "");
        final String path = jarPath.substring(0, jarPath.lastIndexOf("/") + 1);

        System.out.println(path);
        System.out.println(jarPath);
        try {
            File hotkey = new File(fileName);
            if (!hotkey.exists()) {
                InputStream source = stats.class.getResourceAsStream("/" + fileName);
                assert source != null;
                Files.copy(source, Paths.get(fileName));
            }
            Runtime.getRuntime().exec(path + fileName + " " + jarPath);
            System.out.println("Successfully started!");


        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
    }

    public static void end() {
        System.out.println("Hello World!");
    }
}
