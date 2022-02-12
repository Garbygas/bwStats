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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Scanner;

public class stats {
    public static Object[] main(String username, boolean debug) {
        try {
            return stats.lookup(username, debug);
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage() + "\n");
            e.printStackTrace();
            Scanner sc = new Scanner(System.in);
            if (sc.hasNextLine()) {
                System.exit(2);
            }
        }
        return null;
        //this fetches my api key from the file key.java
        //it looks like this:
             /*
                package com.garby.hypixelstats;

                public class key {
                    public static final String API_KEY = "<api key goes here>";
                }
              */


    }


    public static Object[] lookup(String line, boolean debug) throws IOException {
        String key = com.garby.hypixelstats.key.API_KEY;
        //to debug this, use -debug api_key


        // Make sure some text was typed
        if (line.length() == 0) {
            System.out.println("No username entered");
            return null;
        }

        // Quit if the input is "Q" or "q"
        if (line.equalsIgnoreCase("Q")) {
            System.exit(1);
        }

            /*
            Make sure the username is valid
            The regular expression (RegEx) used here checks for between 3 and 16 characters that are
            in the ranges A-Z, a-z, 0-9, or _ (underscores)
             */
        if (!line.matches("^\\w{3,16}$")) {
            System.err.println("Invalid username");
            return null;
        }
        //use mojang to get the uuid of the username
        URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + line);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        InputStream responseStream = connection.getInputStream();
        if (connection.getResponseCode() != 200) {
            System.err.println("Error. Please try again!");
            return null;
        }
        Reader responseStreamScanner = new InputStreamReader(responseStream);
        JsonObject jsonObject = JsonParser.parseReader(responseStreamScanner).getAsJsonObject();
        String uuid = jsonObject.get("id").getAsString();

        //get the stats of the found uuid
        url = new URL("https://api.hypixel.net/player?key=" + key + "&uuid=" + uuid);
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");

        responseStream = connection.getInputStream();
        if (connection.getResponseCode() != 200) {
            System.err.println("Error. Please try again!");
            return null;
        }
        responseStreamScanner = new InputStreamReader(responseStream);
        jsonObject = JsonParser.parseReader(responseStreamScanner).getAsJsonObject();
        JsonObject player = jsonObject.get("player").getAsJsonObject();
        JsonObject stats = player.get("stats").getAsJsonObject();


        // Get the player object from the response. This object stores any available information about a given player

        /*
        The player was found; print some basic info about them
        Not every player has all of these fields (for example, staff members don't have
        "mostRecentGameType"), so I added a handy little method to check if a field
        exists. If it doesn't, it returns "N/A" rather than throwing a
        NullPointerException
         */
        //^username ^level ^games played ^Wins ^Losses ^wlr ^fk ^fd ^fkdr ^k ^d  ^kdr
        final double finalKills = Integer.parseInt(getFields(new String[]{"Bedwars", "final_kills_bedwars"}, stats));
        final double finalDeaths = Integer.parseInt(getFields(new String[]{"Bedwars", "final_deaths_bedwars"}, stats));
        final double fkdr = finalKills / finalDeaths;
        final double kills = Integer.parseInt(getFields(new String[]{"Bedwars", "kills_bedwars"}, stats));
        final double deaths = Integer.parseInt(getFields(new String[]{"Bedwars", "deaths_bedwars"}, stats));
        final double kdr = kills / deaths;
        final double wins = Integer.parseInt(getFields(new String[]{"Bedwars", "wins_bedwars"}, stats));
        final double losses = Integer.parseInt(getFields(new String[]{"Bedwars", "losses_bedwars"}, stats));
        final double wl = wins / losses;

        System.out.println(
                "\nUsername: " + getFields(new String[]{"displayname"}, player) + "\n" +
                        "Bedwars Stats: \n" +
                        "Level: " + getFields(new String[]{"achievements", "bedwars_level"}, player) + "\n" +
                        "games played: " + fd(wins + losses, 0) + "\n" +
                        "Beds Broken: " + fd(Integer.parseInt(getFields(new String[]{"Bedwars", "beds_broken_bedwars"}, stats)), 0) + "\n" +
                        "Wins/loss: " + fd(wins, 0) + "/" + fd(losses, 0) + " or " + fd(wl, 2) + "\n \n" +
                        "Final Kills/deaths: " + fd(finalKills, 0) + "/" + fd(finalDeaths, 0) + " or " + fd(fkdr, 2) + "\n \n" +
                        "Kills/Deaths: " + fd(kills, 0) + "/" + fd(deaths, 0) + " or " + fd(kdr, 2) + "\n" +
                        "-----------------------------------------------------"
        );

        return new Object[]{getFields(new String[]{"displayname"}, player),
                getFields(new String[]{"achievements", "bedwars_level"}, player),
                fd(Integer.parseInt(getFields(new String[]{"Bedwars", "beds_broken_bedwars"}, stats)), 0),
                fd(wins + losses, 0),
                fd(wins, 0), fd(losses, 0), fd(wl, 2), fd(finalKills, 0), fd(finalDeaths, 0),
                fd(fkdr, 2), fd(kills, 0), fd(deaths, 0), fd(kdr, 2)};
    }

    private static String fd(double d, int i) {
        if (i == 2) return new DecimalFormat("#.##").format(d);
        else return new DecimalFormat("#").format(d);
    }

    private static String getFields(String[] fields, JsonObject json) {
        JsonElement value = json.get(fields[0]);
        String returns = "Error";
        if (value == null) {
            return "N/A";
        }
        if (fields.length == 1) {
            returns = value.toString();
        }
        for (int i = 1; i < fields.length; i++) {
            value = value.getAsJsonObject().get(fields[i]);
            if (i == fields.length - 1) {
                returns = value.getAsString();
            }
        }
        return returns;
    }

}


