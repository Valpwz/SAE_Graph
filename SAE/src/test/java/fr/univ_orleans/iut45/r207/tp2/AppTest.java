package fr.univ_orleans.iut45.r207.tp2;

import static org.junit.Assert.assertEquals;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.Set;
import java.util.HashSet;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.util.SupplierUtil;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    @Test
    public void testCollaborateursEnCommun() {
        Graph<String, DefaultEdge> graph = ActorCollaboration.buildGraph("data_100.txt");
        Set<String> result = ActorCollaboration.getCollaborateursCommuns(graph, "Al Pacino", "Robert De Niro");

        assertNotNull(result);
        assertTrue(result.contains("Diane Keaton")); // Exemple possible, à adapter selon les données
    }
    @Test
public void testCollaborateursProches() {
    Graph<String, DefaultEdge> graph = ActorCollaboration.buildGraph("data_100.txt");
    Set<String> proches = ActorCollaboration.getCollaborateursProches(graph, "Al Pacino", 2);

    assertNotNull(proches);
    assertTrue(proches.contains("Robert De Niro"));
    assertTrue(proches.contains("Diane Keaton")); // Possible proche à distance ≤ 2
}
@Test
public void testComputeEccentricity() {
    Graph<String, DefaultEdge> graph = ActorCollaboration.buildGraph("data_100.txt");
    int ecc = ActorCollaboration.computeEccentricity(graph, "Al Pacino");

    assertTrue(ecc >= 0);
}
@Test
public void testGraphCenter() {
    Graph<String, DefaultEdge> graph = ActorCollaboration.buildGraph("data_100.txt");
    String center = ActorCollaboration.getGraphCenter(graph);

    assertNotNull(center);
    assertTrue(graph.containsVertex(center));
}
@Test
public void testGraphDiameter() {
    Graph<String, DefaultEdge> graph = ActorCollaboration.buildGraph("data_100.txt");
    int diameter = ActorCollaboration.getGraphDiameter(graph);

    assertTrue(diameter <= 6); // Hypothèse à valider sur les données
}




}
