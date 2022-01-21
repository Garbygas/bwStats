package com.garby.hypixelstats;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.hypixel.api.HypixelAPI;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Scanner;
import java.util.UUID;

public class stats {
    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                final String path = stats.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceFirst("/", "");
                Runtime.getRuntime().exec("cmd /c start cmd.exe /c \"java -jar " + path + " -w\"");

            } else {
                if (args[0].equals("-debug")) {
                    System.out.println("Debug mode enabled");
                    stats.run(args[1]);
                } else {
                    /* this fetches my api key from the file key.java
                     it looks like this:

                        package com.garby.hypixelstats;

                            public class key {
                                public static final String API_KEY = "<api key goes here>";
                            }
                     */
                    stats.run(key.API_KEY);


                }
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }


    public static void run(String key) {
        //to debug this, use -debug <api key>

        try { //check if the api key is valid, if not, use catch statement and print error
            HypixelAPI api = new HypixelAPI(UUID.fromString(key));
            //asks the user for a username to get stats for
            System.out.println("Type a username to view their bedwars stats, or type \"q\" to quit!");
            Scanner sc = new Scanner(System.in);
            while (sc.hasNextLine()) {

                // Get the line of text that was typed
                String line = sc.nextLine().replaceAll("\n", "");

                // Make sure some text was typed
                if (line.length() == 0) {
                    continue;
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
                    System.err.println("Invalid username. Please try again!");
                    continue;
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
                    continue;
                }
                Reader responseStreamScanner = new InputStreamReader(responseStream);
                JsonObject jsonObject = JsonParser.parseReader(responseStreamScanner).getAsJsonObject();
                String uuid = jsonObject.get("id").getAsString();

                //get the stats of the found uuid
                api.getPlayerByUuid(uuid).whenComplete((response, error) -> {

                    // Check if there was an API error
                    if (error != null) {
                        System.err.println("Error: " + error.getMessage());
                        return;
                    }

                    // Get the player object from the response. This object stores any available information about a given player
                    JsonObject player = response.getPlayer();

                    if (player != null) {

                    /*
                    The player was found; print some basic info about them
                    Not every player has all of these fields (for example, staff members don't have
                    "mostRecentGameType"), so I added a handy little method to check if a field
                    exists. If it doesn't, it returns "N/A" rather than throwing a
                    NullPointerException
                     */
                        final double finalKills = Integer.parseInt(getFields(new String[]{"stats", "Bedwars", "final_kills_bedwars"}, player));
                        final double finalDeaths = Integer.parseInt(getFields(new String[]{"stats", "Bedwars", "final_deaths_bedwars"}, player));
                        final double fkdr = finalKills / finalDeaths;
                        final double kills = Integer.parseInt(getFields(new String[]{"stats", "Bedwars", "kills_bedwars"}, player));
                        final double deaths = Integer.parseInt(getFields(new String[]{"stats", "Bedwars", "deaths_bedwars"}, player));
                        final double kdr = kills / deaths;
                        final double wins = Integer.parseInt(getFields(new String[]{"stats", "Bedwars", "wins_bedwars"}, player));
                        final double losses = Integer.parseInt(getFields(new String[]{"stats", "Bedwars", "losses_bedwars"}, player));
                        final double wl = wins / losses;
                        System.out.println("Username: " + getFields(new String[]{"displayname"}, player) + "\n" +
                                "Bedwars Stats: \n" +
                                "Level: " + getFields(new String[]{"achievements", "bedwars_level"}, player) + "\n" +
                                "games played: " + fd(wins + losses, 0) + "\n" +
                                "Beds Broken: " + fd(Integer.parseInt(getFields(new String[]{"stats", "Bedwars", "beds_broken_bedwars"}, player)), 0) + "\n" +
                                "Wins/loss: " + fd(wins, 0) + "/" + fd(losses, 0) + " or " + fd(wl, 2) + "\n \n" +
                                "Final Kills/deaths: " + fd(finalKills, 0) + "/" + fd(finalDeaths, 0) + " or " + fd(fkdr, 2) + "\n \n" +
                                "Kills/Deaths: " + fd(kills, 0) + "/" + fd(deaths, 0) + " or " + fd(kdr, 2) + "\n" +
                                "-----------------------------------------------------"
                        );


                    } else {

                        // If we're here, it means that Hypixel has no info on this player
                        System.err.println("That player was not found");
                    }
                });
            }
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage() + "\n");
            e.printStackTrace();
            Scanner sc = new Scanner(System.in);
            if (sc.hasNextLine()) {
                System.exit(2);
            }
        }
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


