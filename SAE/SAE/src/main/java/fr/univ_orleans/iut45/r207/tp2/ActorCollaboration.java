package fr.univ_orleans.iut45.r207.tp2;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.nio.AttributeType;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
import com.google.gson.Gson;

public class ActorCollaboration {

    static class Film {
        String title;
        List<String> cast;
        // Les autres champs ne sont pas utilisés
    }

    public static Map<String, List<String>> getActorCollaborationMap(String filePath) throws Exception {
        List<Film> films = lireFilms(filePath);
        return faireMap(films);
    }

    // Méthode pour lire et nettoyer les données des films
    private static List<Film> lireFilms(String filePath) throws Exception {
        List<Film> films = new ArrayList<>();
        Gson gson = new Gson();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                Film film = gson.fromJson(line, Film.class);
                film.cast = nettoyerCast(film.cast);
                films.add(film);
            }
        }
        return films;
    }

    // Nettoyage des noms d'acteurs
    private static List<String> nettoyerCast(List<String> cast) {
        List<String> cleanedCast = new ArrayList<>();
        for (String actor : cast) {
            String cleaned = actor.replaceAll("\\[\\[|\\]\\]", "")
                                  .split("\\|")[0]
                                  .trim();
            cleanedCast.add(cleaned);
        }
        return cleanedCast;
    }

    // Crée la map de collaborations
    private static Map<String, List<String>> faireMap(List<Film> films) {
        Map<String, List<String>> collaborationMap = new HashMap<>();
        Set<String> tousLesActeurs = new HashSet<>();
        
        // Collecte tous les acteurs uniques
        for (Film film : films) {
            tousLesActeurs.addAll(film.cast);
        }

        // Pour chaque acteur, trouve ses collaborateurs
        for (String acteur : tousLesActeurs) {
            List<String> collaborateurs = trouvecollab(acteur, films);
            collaborationMap.put(acteur, collaborateurs);
        }

        return collaborationMap;
    }

    // Trouve les collaborateurs d'un acteur donné
    private static List<String> trouvecollab(String acteur, List<Film> films) {
        Set<String> collaborateurs = new HashSet<>();
        for (Film film : films) {
            if (film.cast.contains(acteur)) {
                for (String coActeur : film.cast) {
                    if (!coActeur.equals(acteur)) {
                        collaborateurs.add(coActeur);
                    }
                }
            }
        }
        return new ArrayList<>(collaborateurs);
    }
    public static Graph<String, DefaultEdge> getGraph(Map<String, List<String>> collaborations) {
        Graph<String, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
        
        // Ajouter les sommets (acteurs)
        for (String acteur : collaborations.keySet()) {
            graph.addVertex(acteur);
        }
        
        // Ajouter les arêtes (collaborations)
        for (Map.Entry<String, List<String>> entry : collaborations.entrySet()) {
            String acteur = entry.getKey();
            for (String collab : entry.getValue()) {
                graph.addEdge(acteur, collab);
            }
        }
        
        return graph;
    }


    public static void main(String[] args) throws Exception {
        Map<String, List<String>> collaborations = getActorCollaborationMap("data_100.txt");
        Graph<String, DefaultEdge> graph = getGraph(collaborations);

        DOTExporter<String, DefaultEdge> exporter = new DOTExporter<String, DefaultEdge>();
		exporter.setVertexAttributeProvider((x) -> Map.of("label", new DefaultAttribute<>(x, AttributeType.STRING)));
		exporter.exportGraph(graph, new FileWriter("graph.dot"));

        

    }
}
    
