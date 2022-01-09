package com.garby.hypixelstats;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.hypixel.api.HypixelAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.Scanner;
import java.util.UUID;
public class stats {
    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
            String data;
        if (args.length != 0) {
            if (!args[0].equals("-debug")) {
            File myObj = new File("apikey.txt");
            Scanner myReader = new Scanner(myObj);
            data = myReader.nextLine().replaceAll("api key:","").replaceAll(" ","");
            myReader.close();}

            else data = args[1];
            stats.run(data);
            Thread.sleep(5000);
        }else {
            final String path = stats.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceFirst("/","");
            try{
                File myObj = new File("apikey.txt");
               if (!myObj.exists()) Runtime.getRuntime().exec("jar xf "+path+" apikey.txt");
                while (true) {
                if (myObj.exists()) {
                    Runtime.getRuntime().exec("cmd /c start cmd.exe /c \"java -jar "+path+" -cmd\"");
                    break;
                }else {
                    Thread.sleep(100);
                }}

                System.out.println("Successfully executed");


            }
            catch (Exception e) {
                System.err.println("Error: " + e.getMessage()+"\n");
                e.printStackTrace();
            }

        }
    }


    public static void run(String key) {
        //to debug this, use -debug <api key>

        try { //check if the api key is valid, if not, use catch statement and print error
            HypixelAPI api = new HypixelAPI(UUID.fromString(key));

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
                System.out.println("Goodbye!");
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

            /*
            NOTE: We are only using getPlayerByName() for the sake of convenience. In real projects,
            you should avoid using deprecated methods like this as they could potentially be removed
            in the future. Instead, you should get the player's UUID from the Mojang API and pass
            that into getPlayerByUuid()
            The .whenComplete(...) tells the CompletableFuture what code we want to run when the API
            responds. In our case, we want to print out the player's stats, so that's what we put
            between the two curly brackets [e.g "(response, error) -> {...}"]
             */
            //todo: use mojang api to get uuid instead of hypixel api
            api.getPlayerByName(line).whenComplete((response, error) -> {

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
                    final double finalKills = Integer.parseInt(getFieldsOrNA(new String[]{"stats", "Bedwars", "final_kills_bedwars"}, player));
                    final double finalDeaths = Integer.parseInt(getFieldsOrNA(new String[]{"stats", "Bedwars", "final_deaths_bedwars"}, player));
                    final double fkdr = finalKills / finalDeaths;
                    final double kills = Integer.parseInt(getFieldsOrNA(new String[]{"stats", "Bedwars", "kills_bedwars"}, player));
                    final double deaths = Integer.parseInt(getFieldsOrNA(new String[]{"stats", "Bedwars", "deaths_bedwars"}, player));
                    final double kdr = kills / deaths;
                    final double wins = Integer.parseInt(getFieldsOrNA(new String[]{"stats", "Bedwars", "wins_bedwars"}, player));
                    final double losses = Integer.parseInt(getFieldsOrNA(new String[]{"stats", "Bedwars", "losses_bedwars"}, player));
                    final double wl = wins / losses;
                    System.out.println("Username: " + getFieldOrNA("displayname", player)+"\n"+
                                        "Bedwars Stats: \n"+
                                        "Level: " + getFieldsOrNA(new String[]{"achievements", "bedwars_level"}, player)+"\n"+
                                        "games played: "+fd(wins+losses,0)+"\n"+
                                        "Beds Broken: " + fd(Integer.parseInt(getFieldsOrNA(new String[]{"stats", "Bedwars", "beds_broken_bedwars"}, player)),0)+"\n"+
                                        "Wins/loss: " + fd(wins,0) +"/"+fd(losses,0)+" or "+fd(wl,2)+"\n \n"+
                                        "Final Kills/deaths: " + fd(finalKills,0) +"/"+fd(finalDeaths,0)+" or "+fd(fkdr,2)+"\n \n"+
                                        "Kills/Deaths: "+fd(kills,0)+"/"+fd(deaths,0)+" or "+fd(kdr,2) +"\n"+
                                        "-----------------------------------------------------"
                    );


                } else {

                    // If we're here, it means that Hypixel has no info on this player
                    System.err.println("That player was not found");
                }
            });
        }
    }catch (Exception e) {System.err.println("Double check your api key\n");
        e.printStackTrace();
        System.out.println("\n check the newly created file apikey.txt"+
                           "\n for instructions on how to get your api key\n"+
                           "press enter to exit");
        Scanner sc = new Scanner(System.in);
        if (sc.hasNextLine()) {
            System.exit(2);
        }
        }
    }

    private static String fd(double d,int i) {
        if(i==2) return new DecimalFormat("#.##").format(d);
        else return new DecimalFormat("#").format(d);
    }
    private static String getFieldOrNA(String field, JsonObject json) {

        JsonElement value = json.get(field);
        if (value != null) {
            // If the field was found, return its value
            return value.getAsString();
        } else {
            // Otherwise, return "N/A"
            return "N/A";
        }
    }

    private static String getFieldsOrNA(String[] fields, JsonObject json) {
        JsonElement value = json.get(fields[0]);
        String returns="Error";
        if (value==null) {return "N/A";}
        //System.out.println(fields.length);

        for (int i = 1; i < fields.length; i++) {
            value = value.getAsJsonObject().get(fields[i]);
            if (i==fields.length-1) {returns = value.getAsString();}
        }
        return returns;
    }
}


