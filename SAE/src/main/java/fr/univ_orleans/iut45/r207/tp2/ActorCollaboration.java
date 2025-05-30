package fr.univ_orleans.iut45.r207.tp2;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.nio.AttributeType;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
import com.google.gson.Gson;

public class ActorCollaboration {

    static class Film {
        String title;
        Set<String> cast;
        // Les autres champs ne sont pas utilisés
    }

    public static Map<String, Set<String>> getActorCollaborationMap(String filePath) throws Exception {
        Set<Film> films = lireFilms(filePath);
        return faireMap(films);
    }

    // Méthode pour lire et nettoyer les données des films
    private static Set<Film> lireFilms(String filePath) throws Exception {
        Set<Film> films = new HashSet<>();
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
    private static Set<String> nettoyerCast(Set<String> cast) {
        Set<String> cleanedCast = new HashSet<>();
        for (String actor : cast) {
            String cleaned = actor.replaceAll("\\[\\[|\\]\\]", "")
                                  .split("\\|")[0]
                                  .trim();
            cleanedCast.add(cleaned);
        }
        return cleanedCast;
    }

    // Crée la map de collaborations
    private static Map<String, Set<String>> faireMap(Set<Film> films) {
        Map<String, Set<String>> collaborationMap = new HashMap<>();
        Set<String> tousLesActeurs = new HashSet<>();
        
        // Collecte tous les acteurs uniques
        for (Film film : films) {
            tousLesActeurs.addAll(film.cast);
        }

        // Pour chaque acteur, trouve ses collaborateurs
        for (String acteur : tousLesActeurs) {
            Set<String> collaborateurs = trouvecollab(acteur, films);
            collaborationMap.put(acteur, collaborateurs);
        }

        return collaborationMap;
    }

    // Trouve les collaborateurs d'un acteur donné
    private static Set<String> trouvecollab(String acteur, Set<Film> films) {
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
        return collaborateurs;
    }
    public static Graph<String, DefaultEdge> getGraph(Map<String, Set<String>> collaborations) {
        Graph<String, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
        
        // Ajouter les sommets (acteurs)
        for (String acteur : collaborations.keySet()) {
            graph.addVertex(acteur);
        }
        
        // Ajouter les arêtes (collaborations)
        for (Map.Entry<String, Set<String>> entry : collaborations.entrySet()) {
            String acteur = entry.getKey();
            for (String collab : entry.getValue()) {
                graph.addEdge(acteur, collab);
            }
        }
        Set<String> inactifs = new HashSet<>();
            for( String v : graph.vertexSet()){
                if(graph.degreeOf(v)<20)
                    inactifs.add(v);
            }
            graph.removeAllVertices(inactifs);
        return graph;
    }
    // Centralité d’un acteur : distance maximale à un autre acteur
    public static int centralité(Graph<String, DefaultEdge> graph, String actor) {
        DijkstraShortestPath<String, DefaultEdge> dijkstra = new DijkstraShortestPath<>(graph);
        int maxDistance = 0;

        for (String other : graph.vertexSet()) {
            if (!actor.equals(other)) {
                GraphPath<String, DefaultEdge> path = dijkstra.getPath(actor, other);
                if (path != null) {
                    maxDistance = Math.max(maxDistance, path.getLength());
                }
            }
        }

        return maxDistance;
    }

    // Trouve l’acteur au centre du graphe
    public static String getGraphCenter(Graph<String, DefaultEdge> graph) {
        String center = null;
        int minEccentricity = Integer.MAX_VALUE;

        for (String actor : graph.vertexSet()) {
            int ecc = centralité(graph, actor);
            if (ecc < minEccentricity) {
                minEccentricity = ecc;
                center = actor;
            }
        }

        System.out.println("Centralité minimale : " + minEccentricity);
        return center;
    }

    // Diamètre du graphe : distance maximale entre deux acteurs
    public static int getGraphDiameter(Graph<String, DefaultEdge> graph) {
        DijkstraShortestPath<String, DefaultEdge> dijkstra = new DijkstraShortestPath<>(graph);
        int diameter = 0;

        for (String a : graph.vertexSet()) {
            for (String b : graph.vertexSet()) {
                if (!a.equals(b)) {
                    GraphPath<String, DefaultEdge> path = dijkstra.getPath(a, b);
                    if (path != null) {
                        diameter = Math.max(diameter, path.getLength());
                    }
                }
            }
        }

        return diameter;
    }

    // Distance moyenne d’un acteur vers tous les autres
    public static double laDegreMoyen (Graph<String, DefaultEdge> graph, String actor) {
        DijkstraShortestPath<String, DefaultEdge> dijkstra = new DijkstraShortestPath<>(graph);
        double total = 0;
        int count = 0;

        for (String other : graph.vertexSet()) {
            if (!actor.equals(other)) {
                GraphPath<String, DefaultEdge> path = dijkstra.getPath(actor, other);
                if (path != null) {
                    total += path.getLength();
                    count++;
                }
            }
        }

        return count == 0 ? Double.POSITIVE_INFINITY : total / count;
    }

    // Trouve l’acteur avec la distance moyenne minimale
    public static String leMeilleurDegreMoyen(Graph<String, DefaultEdge> graph) {
        String best = null;
        double bestAvg = Double.POSITIVE_INFINITY;

        for (String actor : graph.vertexSet()) {
            double avg = laDegreMoyen (graph, actor);
            if (avg < bestAvg) {
                bestAvg = avg;
                best = actor;
            }
        }

        System.out.println("Meilleure moyenne : " + bestAvg);
        return best;
    }

    // Trouve les collaborateurs en commun entre deux acteurs (3.2)
    public static Set<String> collaborateursEnCommun(Graph<String, DefaultEdge> graph, String acteur1, String acteur2) {
    Set<String> voisinsActeur1 = new HashSet<>(Graphs.neighborListOf(graph, acteur1));
    Set<String> voisinsActeur2 = new HashSet<>(Graphs.neighborListOf(graph, acteur2));
    voisinsActeur1.retainAll(voisinsActeur2);
    return voisinsActeur1;
    }

    // Trouve les acteurs à une distance au plus k d'un acteur donné (3.3)
    public static Set<String> collaborateursProches(Graph<String, DefaultEdge> graph, String acteur, int k) {
        // Vérifie si l'acteur existe dans le graphe
        if (!graph.containsVertex(acteur)) {
            System.out.println(acteur + " est un illustre inconnu");
            return null;
        }

        // Initialisation des ensembles
        Set<String> collaborateurs = new HashSet<>();
        collaborateurs.add(acteur); // Ajoute l'acteur de départ

        for (int i = 1; i <= k; i++) {
            Set<String> collaborateursDirects = new HashSet<>();
            // Parcourt les collaborateurs actuels
            for (String collaborateur : collaborateurs) {
                // Parcourt les voisins de chaque collaborateur
                for (String voisin : Graphs.neighborListOf(graph, collaborateur)) {
                    if (!collaborateurs.contains(voisin)) {
                        collaborateursDirects.add(voisin);
                    }
                }
            }
            // Met à jour l'ensemble des collaborateurs
            collaborateurs.addAll(collaborateursDirects);
        }

        return collaborateurs;
    }

    public static String getCenterOfGroup(Graph<String, DefaultEdge> graph, Set<String> group) {
        String best = null;
        double bestAverage = -1;
        DijkstraShortestPath<String, DefaultEdge> dijkstra = new DijkstraShortestPath<>(graph);

        for (String candidate : graph.vertexSet()) {
            double total = 0;
            int count = 0;

            for (String member : group) {
                if (!candidate.equals(member)) {
                    GraphPath<String, DefaultEdge> path = dijkstra.getPath(candidate, member);
                    if (path != null) {
                        total += path.getLength();
                        count++;
                    }
                }
            }

            if (count > 0) {
                double average = total / count;
                if (bestAverage == -1 || average < bestAverage) {
                    bestAverage = average;
                    best = candidate;
                }
            }
        }

        return best;
    }

    public static void main(String[] args) throws Exception {
        Map<String, Set<String>> collaborations = getActorCollaborationMap("data_100.txt");
        Graph<String, DefaultEdge> graph = getGraph(collaborations);
        String center = getGraphCenter(graph);
        System.out.println("Centre du graphe : " + center);
        int diameter = getGraphDiameter(graph);
        System.out.println("Diamètre du graphe : " + diameter);
        String meilleurMoyenne = leMeilleurDegreMoyen(graph);
        System.out.println("Acteur avec meilleure moyenne de distances : " + meilleurMoyenne);
        DOTExporter<String, DefaultEdge> exporter = new DOTExporter<String, DefaultEdge>();
		exporter.setVertexAttributeProvider((x) -> Map.of("label", new DefaultAttribute<>(x, AttributeType.STRING)));
		exporter.exportGraph(graph, new FileWriter("graph.dot"));
        
        

    }
}
    
