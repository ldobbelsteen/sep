package org.lukos.model.instances.util;

import org.lukos.model.rolesystem.util.GeneralPurposeHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

abstract class RoleDivisionReader extends GeneralPurposeHelper {

    /**
     * Reads the role division for a given number of players. Prerequisite: NROF_PLAYERS >= 12
     *
     * @param NROF_PLAYERS number of players to give roles to
     * @return array of numbers of roles per group.
     */
    public static int[] readDivision(int NROF_PLAYERS) {
        String directory = System.getProperty("user.dir");
        String configFile = directory + "/src/main/resources/Role_assignment_config.csv";
        configFile = configFile.replace("\\", "/");
        File config = new File(configFile);

        Scanner sc;
        try {
            sc = new Scanner(config);
        } catch (FileNotFoundException e) {
            // TODO: Handle a missing config file
            return new int[]{0};
        }

        // Assumption: line n+1 contains the division of (line n) + 1 players.

        // Skip first line with headers and start reading data
        sc.nextLine();
        String lineString = sc.nextLine();
        int lowestNumber = Integer.parseInt(lineString.split(",")[0]);

        // As it stands, 12 is the first number for which a role division is defined.
        // This setup allows for expansion of the config below 12.

        // Selects the correct row for NROF_PLAYERS
        int difference = NROF_PLAYERS - lowestNumber;
        while (difference > 0) {
            lineString = sc.nextLine();
            difference--;
        }

        String[] lineArray = lineString.split(",");
        int[] output = new int[lineArray.length];
        for (int i = 1; i < lineArray.length; i++) {
            output[i - 1] = Integer.parseInt(lineArray[i]);
        }

        sc.close();
        return output;
    }
}
