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

package com.garby.hypixelstats;

public class main {
    //opens different classes with different arguments
    public static void main(String[] args) {
        String defaultArgs = "-ui";

        if (args.length != 0) {
        }else args = new String[]{defaultArgs};
        switch(args[0]) {
            case "-ui": {
                ui.main(new String[]{"null"});
                break;}
            case "-discord": {
                discord.main(new String[]{"null"});
            }
            case "egg": {
                egg.main(new String[]{"null"});
            }

        }
    }
}
