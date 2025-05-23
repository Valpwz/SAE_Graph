package fr.univ_orleans.iut45.r207.tp2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import com.google.gson.Gson;

public class ActorCollaboration {

    static class Film {
        String title;
        List<String> cast;
        // Autres champs non utilisés
    }

    public static Map<String, Integer> getActorCollaborationMap(String filePath) throws Exception {
        Set<String> actors = new HashSet<>();
        Map<String, List<String>> lesact = new HashMap<>();
        List<String> collab = new ArrayList<>();
        Gson gson = new Gson();

        // Lecture du fichier et extraction des paires d'acteurs
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                Film film = gson.fromJson(line, Film.class);
                List<String> cleanedCast = new ArrayList<>();
                
                // Nettoyage des noms d'acteurs
                for (String actor : film.cast) {
                    String cleaned = actor
                        .replaceAll("\\[\\[|\\]\\]", "") // Enlève les [[ et ]]
                        .split("\\|")[0]                  // Garde la partie avant |
                        .trim();
                    for (String a : film.cast){
                        
                    }
                    
                }


                
            }      
    }

    public static void main(String[] args) throws Exception {
        Map<String, Integer> result = getActorCollaborationMap("data_100.txt");
        // Exemple d'affichage
        
    }
}
}