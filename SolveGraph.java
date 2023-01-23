import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;


/**
The class SolveGraph is our old class from phase1 that has the greedy algorithm to solve a graph and get the chromatic number.
*/
public class SolveGraph 
{

    /**
    The method greedyAlgorithm solves the problem of finding the chromatic number of a graph using a greedy algorithm approach.
    The method takes in two parameters, an ArrayList of ArrayLists of ArrayLists of Integers that represents the graph,
    and an integer that represents the number of vertices in the graph.
    The method first creates a Hashtable to store the vertex number as a string and its corresponding color as the value.
    It then assigns color 0 to all the vertices in the graph.
    The algorithm then starts with the first vertex, assigns color 1 to it, looks at its adjacent vertices,
    and checks their color. If the color is the same as the color of the current vertex, it increases the color by 1,
    and continues this process until all the vertices have a unique color among their adjacent vertices.
    The method then returns the highest color value found in the Hashtable, which represents the chromatic number of the graph.
    */

    public static int greedyAlgorithm(ArrayList<ArrayList<ArrayList<Integer>>> finArr, int numberOfVertices) {

        // At first we create a hashmap (python dictionary - thats how we got the idea),
        // to remember each vertex and its given colour, hashmap is much easier to access,
        // the only issue was that if the vertex was given as int, it would always sort the values,
        // that is why we pass the vertex as a string, so that the hashmap leaves it alone

        Hashtable<String, Integer> verticesAndTheirColours = new Hashtable<String, Integer>();

        // As mentioned above, we put the vertices
        // in and assign them a 0 colour,
        // meaning they don't have a colour yet

        for (int i = 0; i < numberOfVertices; i++) {

            String tempString = finArr.get(i).get(0).get(0).toString();

            verticesAndTheirColours.put(tempString, 0);

        }

        // This is the main algorithm, which works on a simple premise,
        // We start with the first vertex, assign colour 1 to it, look around,
        // ask other vertices their colour and if their colour is the same as
        // the vertex we are asking for, then we up the colour by one, continue asking again:

        for (int i = 0; i < finArr.size(); i++) {
            
            String currVertext = finArr.get(i).get(0).get(0).toString();

                // Here we get the vertices a vertex we are asking for is connected to
                // so that we can go through through each connection, and check the colour

                ArrayList<Integer> temporaryArray = new ArrayList<Integer>();

                for (int j = 0; j < finArr.get(i).get(1).size(); j++) {

                    temporaryArray.add(finArr.get(i).get(1).get(j));  

                    }

                // We sort the temporraryArray here, which might not be necessary, but
                // we decided to leave it there, so that we can understand the code

                temporaryArray.sort(null);

                // The algorithm starts by assigning the first  colour to the vertex we are currently at

                verticesAndTheirColours.compute(currVertext, (key, val) -> val + 1);

                // This forloop is here so that we run through the connections multiple times,
                // so that even if the order isn't "good", we will always get a right answer,
                // meaning the colour of the vertex we are asking for will be different 
                // (from all the other vertices that its adjecent to) and the lowest possible

                for (int m = 0; m < (temporaryArray.size()); m++) {

                    for (int k = 0; k < temporaryArray.size(); k++) {

                            // Here is the one and only if statement, asking whether the colour of the current vertex (using a hashmap)
                            // is the same as the colour of an adjecent vertex, if it is, we choose a different colour (that is one higher)
                            // this ensures that we always use the lowest colour possible
                        
                            if (verticesAndTheirColours.get(currVertext) == verticesAndTheirColours.get(temporaryArray.get(k).toString())
                            ) {

                                verticesAndTheirColours.compute(currVertext, (key, val) -> val + 1);

                            }
                        
                    } 
                    
                } 
            }

                // The last part here chooses the highest colour from our hashmap
                // that is the chromatic number, or at least the upper bound
                // as there can be no 2 vertices (that are adjecent) with the same colour

                int maxValueInMap=(Collections.max(verticesAndTheirColours.values()));
        
                return maxValueInMap;
            }

    /**
    *
    @param finArr arrayList of arrayLists of arrayLists of integers representing the graph, where each element of the outermost arrayList represents a vertex, 
    the first inner arrayList contains one element, the vertex number, and the second inner arrayList contains the vertices that the vertex is connected to
    @param numberOfVertices the number of vertices in the graph
    @return the smallest number of colors required to properly color the graph among a number of randomly generated possible solutions.
    */

    public static int SolveGraphBruteForce(ArrayList<ArrayList<ArrayList<Integer>>> finArr, int numberOfVertices)
    {

        int amountOfTests = 5000;

        int[] solutions = new int[amountOfTests];

        for (int i = 0; i < amountOfTests; i++)
        {
            Collections.shuffle(finArr);

            solutions[i] = greedyAlgorithm(finArr, numberOfVertices);
        }

        Arrays.sort(solutions);

        return solutions[0];

    }

}
