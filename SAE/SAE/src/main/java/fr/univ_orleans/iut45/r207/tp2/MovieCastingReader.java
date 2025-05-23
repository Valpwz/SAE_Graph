package fr.univ_orleans.iut45.r207.tp2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieCastingReader {
    public static void main(String[] args) {
        // Créer une HashMap pour stocker les films et leur casting
        Map<String, List<String>> movieMap = new HashMap<>();

        // Chemin vers le fichier (à adapter selon votre cas)
        String filePath = "data_100.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Supposons que le format du fichier soit: "Film;Acteur1, Acteur2, ..."
                String[] parts = line.split(";", 2); // Split sur le premier ';'
                
                if (parts.length >= 2) {
                    String movieName = parts[0].trim();
                    String[] casting = parts[1].split(",");
                    
                    // Nettoyer et stocker les noms des acteurs
                    List<String> actors = new ArrayList<>();
                    for (String actor : casting) {
                        actors.add(actor.trim());
                    }
                    
                    // Ajouter à la HashMap
                    movieMap.put(movieName, actors);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Afficher le résultat pour vérification
        for (Map.Entry<String, List<String>> entry : movieMap.entrySet()) {
            System.out.println("Film: " + entry.getKey());
            System.out.println("Casting: " + entry.getValue() + "\n");
        }
    }
}