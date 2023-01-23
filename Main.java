import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;

/** The main part of our program, where all the magic happens, multiple methods, all explained. 
* 
*/
public class Main
{

    

    /** vertices represent our graph, it is an arraylist of Node objects representing each vertex */
    public static ArrayList<Node> vertices = new ArrayList<>();

    
    /** verticesFull is used in our BK algorithm, representing all the possible maximal cliques in a graph */
    public static ArrayList<ArrayList<Node>> verticesFull = new ArrayList<>();

    public static void main(String[] args) 
    {

        /* we first specify the input file */

        String inputFile = args[0];

        System.out.println(inputFile);
        
        int[][] eArr = ReadGraph.readFile(inputFile);

        int numberOfVertices = ReadGraph.getNumberOfVertices();

        int numberOfEdges = ReadGraph.getNumberOfEdges();

        /* convert it to our finalArray from first phase, more explanation can be found in ArrayMethods and SolveGraph from first phase */
        ArrayList<ArrayList<ArrayList<Integer>>> finArr = ArrayMethods.inputArrayFinal(numberOfVertices, numberOfEdges, eArr);

        /* here we create our objects that represent the said graph, we decided to use objects for this phase as they are much easier and cleaner to work with */
        createNodesOOP(numberOfVertices, finArr);

        /* initializing variables */
        int lowerBound = 0;

        int upperBound = 0;

        int chromaticNumber = 0;

        /* here we see how big the graph is, if it is deemed too big, we use our random walks pruning method, as it is a lot faster, and without it, we woudln't be able to 
         * compute the bounds in time */ 
        if (eArr.length > 100000)
        {
            ArrayList<ArrayList<ArrayList<Integer>>> finArr2 = randomWalksAlgorithmForPruning(numberOfVertices, finArr);

            vertices.clear();

            /* recreate the objects without some, that were deemed unnecesarry */
            createNodesOOP(finArr2.size(), finArr2);

            lowerBound = BKalgorithm();

            upperBound = DSatur(vertices);

            vertices.clear();
            
            /* here we recreate the original graph, so that we can use our DFS to find bridges and then find connected components */
            createNodesOOP(numberOfVertices, finArr);
        }
        else 
        {
            upperBound = DSatur(vertices);

            lowerBound = BKalgorithm();
        }

        /* if our bounds match, we don't even need to run other parts of our code, as we've definitely found the exact chromatic number */
        if (lowerBound == upperBound)
        {
            chromaticNumber = lowerBound;
        }
        else
        {
            /* call the method to delete bridges and then to find connected components in the graph, so that they can be identified and their chromatic numbers
             * can be written inside the solutions arraylist
             */
            deleteBridges();

            ArrayList<ArrayList<Node>> allComponents = findConnectedComponents();

            ArrayList<Integer> solutions = new ArrayList<>();

            int numberOfComponents = 0;

            for (ArrayList<Node> component : allComponents) 
            {
                int chromaticNumberOfComponent = 0;

                /* if we can't determine the whether the component is a specialType we run our greedy brute force algorithm */
                boolean isSpecialType = false;
            
                if (isComplete(component))
                {
                    isSpecialType = true; 

                    //System.out.println("COMPLETE");

                    if ((component.size() % 2) == 0)
                    {
                        chromaticNumberOfComponent = component.size() - 1;

                        solutions.add(chromaticNumberOfComponent);
                    }
                    else 
                    {
                        chromaticNumberOfComponent = component.size();

                        solutions.add(chromaticNumberOfComponent);
                    }
                }
    
                if (isWheelGraph(component))
                {
                    isSpecialType = true; 

                    //System.out.println("WHEEL");

                    if ((component.size() % 2) == 0)
                    {
                        chromaticNumberOfComponent = 4;

                        solutions.add(chromaticNumberOfComponent);
                    }
                    else 
                    {
                        chromaticNumberOfComponent = 3;

                        solutions.add(chromaticNumberOfComponent);
                    }
                }
    
                if (isCycleGraph(component))
                {
                    isSpecialType = true; 

                    //System.out.println("CYCLE");

                    if ((component.size() % 2) == 0)
                    {
                        chromaticNumberOfComponent = 2;

                        solutions.add(chromaticNumberOfComponent);
                    }
                    else 
                    {
                        chromaticNumberOfComponent = 3;

                        solutions.add(chromaticNumberOfComponent);
                    }
                }
    
                if (isBipartite(component))
                {
                    isSpecialType = true; 

                    //System.out.println("BIPARTITE");

                    chromaticNumberOfComponent = 2;

                    solutions.add(chromaticNumberOfComponent);
                }
    
                if (isTree(component))
                {
                    isSpecialType = true; 

                    //System.out.println("TREE");

                    chromaticNumberOfComponent = 2;

                    solutions.add(chromaticNumberOfComponent);
                }

                /* if the component is deemed small enough we use our brute force algorithm */
                if (((isSpecialType == false) || (component.size() < 50)) && (numberOfComponents < 5))
                {
                    ArrayList<ArrayList<ArrayList<Integer>>> finArr3 = OOPtofinArr(component);

                    chromaticNumberOfComponent = SolveGraph.SolveGraphBruteForce(finArr3, finArr3.size());

                    solutions.add(chromaticNumberOfComponent);

                    numberOfComponents++;
                }
            }


            /* if we weren't able to find the exact chromatic number we say so here by outputing that it is -1*/
            if (solutions.size() == 0)
            {
                chromaticNumber = -1;
            }
            else 
            {
                chromaticNumber = Collections.max(solutions);  
            }
            
        }


        System.out.println("NEW BEST UPPER BOUND=" + upperBound);

        System.out.println("NEW BEST LOWER BOUND=" + lowerBound);

        /* same thing, if our chromatic number is somehow (which shouldn't happen) not in between our bounds, we output it is -1 as we weren't able to get it */
        if ((chromaticNumber < lowerBound) || (chromaticNumber > upperBound))
        {
            chromaticNumber = -1;
        }

        System.out.println("CHROMATIC NUMBER=" + chromaticNumber);

    }

    /**
     * The randomWalksPath method is used to generate a random path of nodes in a graph starting from a given vertex and continuing for a given number of walks.
     *
     * @param vertexStart Node object representing the starting vertex for the random walk
     * @param numberOfWalks int representing the number of times the walk should continue
     * @param pathTraveled ArrayList of Node objects representing the path traveled so far
     * @param WALKS int representing the number of walks to be taken
     * @return ArrayList of Node objects representing the path traveled
     */

    public static ArrayList<Node> randomWalksPath(Node vertexStart, int numberOfWalks, ArrayList<Node> pathTraveled, int WALKS)
    {

        if (numberOfWalks == WALKS)
        {
            return pathTraveled;
        } 
        else 
        {
            pathTraveled.add(vertexStart);

            numberOfWalks++;
            
            int randomContinuation = (int)((Math.random()*vertexStart.getNodesVertexIsConnectedTo().size()));

            ArrayList<Node> possibleMoves = vertexStart.getNodesVertexIsConnectedTo();

            if (possibleMoves.size() == 0)
            {
                return pathTraveled;
            }

            Node nextVertex = possibleMoves.get(randomContinuation);

            return randomWalksPath(nextVertex, numberOfWalks, pathTraveled, WALKS);
        }

    }


    /**
     * The bronKerboschPivoting method is used to find all the cliques of a graph using the Bron-Kerbosch algorithm with pivot.
     * It uses recursion to find all the cliques and stores them in a ArrayList of ArrayList of Nodes.
     *
     * @param currentClique ArrayList of Node objects representing the current clique being built
     * @param candidateSetOfVertices ArrayList of Node objects representing the set of candidate vertices for the current clique
     * @param excludingSetOfVertices ArrayList of Node objects representing the set of vertices that are excluded from the current clique
     */

    public static void bronKerboschPivoting(ArrayList<Node> currentClique, ArrayList<Node> candidateSetOfVertices, ArrayList<Node> excludingSetOfVertices)
    {

        if ((candidateSetOfVertices.size() + excludingSetOfVertices.size()) == 0)
        {
            verticesFull.add(currentClique);
        }
        else
        {

            // Getting the union of candidate and excluding vertices to choose a pivot vertex
            ArrayList<Node> unionOfCandidateAndExludedVertices = new ArrayList<Node>(candidateSetOfVertices);

            unionOfCandidateAndExludedVertices.addAll(excludingSetOfVertices);


            // Choosing a pivot vertex and getting it's neighbours
            int randomPivotVertexNumber = (int)(Math.random()*(unionOfCandidateAndExludedVertices.size()));

            Node pivotVertex = unionOfCandidateAndExludedVertices.get(randomPivotVertexNumber);

            ArrayList<Node> pivotVertexNeighbours = pivotVertex.getNodesVertexIsConnectedTo();


            // Getting the candidate vertices minus the neighbours of our pivot vertex
            ArrayList<Node> candidatesMinusPivotNeighbours = new ArrayList<Node>(candidateSetOfVertices);

            candidatesMinusPivotNeighbours.removeAll(pivotVertexNeighbours);


        
            for (Node node : (candidatesMinusPivotNeighbours))
            {
                ArrayList<Node> currentCliqueNew = new ArrayList<>(currentClique);

                currentCliqueNew.add(node);

                ArrayList<Node> candidateSetOfVerticesNew = new ArrayList<>(candidateSetOfVertices);

                ArrayList<Node> excludingSetOfVerticesNew = new ArrayList<>(excludingSetOfVertices);

                candidateSetOfVerticesNew.retainAll(node.getNodesVertexIsConnectedTo());

                excludingSetOfVerticesNew.retainAll(node.getNodesVertexIsConnectedTo());
    
                bronKerboschPivoting(currentCliqueNew, candidateSetOfVerticesNew, excludingSetOfVerticesNew);
    
                candidateSetOfVertices.remove(node);
    
                excludingSetOfVertices.add(node);
            }
        }
        
    }


    /**
     * The BKalgorithm method is used to find the maximum clique of a graph using the Bron-Kerbosch algorithm with pivot.
     * It uses the bronKerboschPivoting method to find all the cliques and finds the maximum clique by returning the size of the clique with the most number of vertices.
     *
     * @return an Integer representing the size of the maximum clique in the graph
     */

    public static int BKalgorithm()
    {
        ArrayList<Node> currentClique = new ArrayList<Node>();

        ArrayList<Node> candidateSetOfVertices = new ArrayList<>(vertices);

        ArrayList<Node> excludingSetOfVertices = new ArrayList<Node>();

        bronKerboschPivoting(currentClique, candidateSetOfVertices, excludingSetOfVertices);
        
        ArrayList<Integer> verticesAllCliques = new ArrayList<>();

        for (int i = 0; i < verticesFull.size(); i++)
        {
            ArrayList<Integer> verticesAndTheirNumbers = new ArrayList<>();

            for (int x = 0; x < verticesFull.get(i).size(); x++)
            {
                verticesAndTheirNumbers.add(verticesFull.get(i).get(x).vertexNumber);
            }

            verticesAllCliques.add(verticesAndTheirNumbers.size());

        }

        int result = Collections.max(verticesAllCliques);

        return result;
    }


    /**
     * The randomWalksAlgorithmForPruning method is used to prune a graph using random walks algorithm.
     * It takes in a number of vertices and an ArrayList of ArrayList of Integer objects representing the edges in the graph as input,
     * and performs random walks on the graph to find the most important vertices and edges.
     *
     * @param numberOfVertices an Integer represents the number of vertices in the graph
     * @param finArr an ArrayList of ArrayList of ArrayList of Integer objects representing the edges in the graph
     * @return ArrayList of ArrayList of ArrayList of Integer objects representing the edges in the pruned graph
     */

    public static ArrayList<ArrayList<ArrayList<Integer>>> randomWalksAlgorithmForPruning(int numberOfVertices, ArrayList<ArrayList<ArrayList<Integer>>> finArr)
    {
        LinkedHashMap<Integer, Integer> vertexAndTheNumberOfTimesItWasWalkedOn = new LinkedHashMap<>();

        for (int i = 0; i < numberOfVertices; i++)
        {
            vertexAndTheNumberOfTimesItWasWalkedOn.put(i+1, 0);
        }

        int numberOfWalks = vertices.size();

        for (int i = 0; i < numberOfWalks; i++)
        {
            ArrayList<Integer> verticesThatWereWalkedOn = new ArrayList<>();

            int randomNumber = (int)((Math.random()*vertices.size()));

            ArrayList<Node> pathTraveled = new ArrayList<>();

            pathTraveled = randomWalksPath(vertices.get(randomNumber), 0, pathTraveled, 500);

            for (int x = 0; x < pathTraveled.size(); x++)
            {
                verticesThatWereWalkedOn.add(pathTraveled.get(x).getVertexNumber());
            }
    
            for (int y = 0; y < verticesThatWereWalkedOn.size(); y++)
            {
                vertexAndTheNumberOfTimesItWasWalkedOn.compute(verticesThatWereWalkedOn.get(y), (key, val) -> val + 1);
            }
        }
        

        float sum = 0.0f;
        for (float f : vertexAndTheNumberOfTimesItWasWalkedOn.values()) 
        {
            sum += f;
        }

        ArrayList<Integer> verticesWalkedOnTheMost = new ArrayList<>();

        for (int i = 1; i < vertexAndTheNumberOfTimesItWasWalkedOn.size()+1; i++)
        {
            if (vertexAndTheNumberOfTimesItWasWalkedOn.get(i) > (sum/vertexAndTheNumberOfTimesItWasWalkedOn.size()))
            {
                verticesWalkedOnTheMost.add(i);
            }
        }

        ArrayList<ArrayList<ArrayList<Integer>>> finArr2 = new ArrayList<ArrayList<ArrayList<Integer>>>();

        for (int i = 0; i < finArr.size(); i++)
        {
            if (verticesWalkedOnTheMost.contains(finArr.get(i).get(0).get(0)))
            {
                finArr2.add(finArr.get(i));
            }

        }

        for (int i = 0; i < finArr2.size(); i++)
        {
            for (int x = 0; x < finArr2.get(i).get(1).size(); x++)
            {
                if (!(verticesWalkedOnTheMost.contains(finArr2.get(i).get(1).get(x))))
                {
                    finArr2.get(i).get(1).remove(x);
                }
            }

        }
        
        return finArr2;
    }

    /**
     * The createNodesOOP method is used to create a graph using Object Oriented Programming.
     * It takes in a number of vertices and an ArrayList of ArrayList of Integer objects representing the edges in the graph as input,
     * and creates Node objects for each vertex, adds them to the 'vertices' ArrayList and creates edges between them.
     *
     * @param numberOfVertices an Integer represents the number of vertices in the graph
     * @param finArr an ArrayList of ArrayList of ArrayList of Integer objects representing the edges in the graph
     */

    public static void createNodesOOP(int numberOfVertices, ArrayList<ArrayList<ArrayList<Integer>>> finArr)
    {
        HashMap<Integer, Node> verticesAndTheirNumbers = new HashMap<>();

        for (int i = 0; i < numberOfVertices; i++)
        {
            int vertexNumber = finArr.get(i).get(0).get(0);

            Node vertex = new Node(vertexNumber);

            vertices.add(vertex);

            verticesAndTheirNumbers.put(vertexNumber, vertex);
        }

        for (int i = 0; i < finArr.size(); i++)
        {
            ArrayList<ArrayList<Integer>> listOfCurrentConnections = finArr.get(i);

            int nodeToAddConnectionsTo = listOfCurrentConnections.get(0).get(0);

            ArrayList<Integer> listOfConnectedNodes = listOfCurrentConnections.get(1);

            for (int j = 0; j < listOfConnectedNodes.size(); j++)
            {
                int nodeToConnect = listOfCurrentConnections.get(1).get(j);

                verticesAndTheirNumbers.get(nodeToAddConnectionsTo).addAdjacentVertices(verticesAndTheirNumbers.get(nodeToConnect));
            }
        }

    }

    /**
     * The DFS method is a Depth First Search algorithm used to traverse a graph.
     * It takes a starting vertex and an ArrayList of Node objects representing a connected component as input,
     * and finds all vertices connected to the starting vertex by adding them to the connected component.
     *
     * @param vertex the starting vertex from which the graph will be traversed
     * @param connectedComponent an ArrayList of Node objects representing a connected component in the graph
     */

    public static void DFS(Node vertex, ArrayList<Node> connectedComponent)
    {
        vertex.setVertexVisitedStatusToTrue();

        connectedComponent.add(vertex);

        for (Node node : vertex.getNodesVertexIsConnectedTo()) 
        {
            if (node.getVertexVisitedStatus() == false)
            {
                DFS(node, connectedComponent);
            }
        }
    }

    /**
     * The findConnectedComponents method is used to find all connected components in a graph.
     * It uses a Depth First Search algorithm to traverse the graph and find all connected components.
     *
     * @return ArrayList of ArrayList of Node objects representing the connected components in the graph
     */

    public static ArrayList<ArrayList<Node>> findConnectedComponents()
    {
        changeAllNodesToUnvisited();

        ArrayList<ArrayList<Node>> allComponents = new ArrayList<>();

        for (Node node : vertices) 
        {
            ArrayList<Node> connectedComponent = new ArrayList<>();
            
            if (node.getVertexVisitedStatus() == false)
            {
                DFS(node, connectedComponent);   

                if (connectedComponent.size() > 1)
                {
                    allComponents.add(connectedComponent);
                }  
            
                connectedComponent = new ArrayList<>();
            }
        }

        return allComponents;
    }

    /**
     * The changeAllNodesToUnvisited method is used to change the visited status in all the nodes of the graph to false (make them unvisited)
     *
     */

    public static void changeAllNodesToUnvisited()
    {
        for (int i = 0; i < vertices.size(); i++)
        {
            vertices.get(i).setVertexVisitedStatusToFalse();
        }
    }

    /**
     * The deleteBridges method is used to delete bridges in the graph, calls the depthFirstSearch method to do the job 
     *
     * initializes all the important variables and objects that will be used by the depthFirstSearch method
     * calls depthFirstSearch on all unvisited nodes in the graph
     */


    public static void deleteBridges()
    {
        changeAllNodesToUnvisited();

        int[] d = new int[vertices.size()];

        int[] low = new int[vertices.size()];

        int[] parent = new int[vertices.size()];

        for (Node node : vertices) 
        {
            if (node.getVertexVisitedStatus() == false)
            {
                Integer time = 0;

                depthFirstSearch(node, d, low, parent, time);
            }
        }
    }

    /**
     * The depthFirstSearch method is used to find bridges in a given graph.
     *
     * @param vertex a Node object representing the current vertex we're looking at
     * @param d an int[] which we use to keep track of discovery times for the vertices
     * @param low an int[] that has the lowest time between two vertices
     * @param parent an int[] representing the parent and the child node
     * @param time an Integer representing the time discovery time of the vertex
     * deletes connection between two vertices if it deemed important to do so
     */

    public static void depthFirstSearch(Node vertex, int[] d, int[] low, int[] parent, Integer time) 
    {
        vertex.setVertexVisitedStatusToTrue();

        d[vertex.getVertexNumber()-1] = low[vertex.getVertexNumber() - 1] = ++time;

        for (Node neighbourNode : vertex.staticNodesVertexIsConnectedTo) 
        {
            if (neighbourNode.getVertexVisitedStatus() == false)
            {
                parent[neighbourNode.getVertexNumber() - 1] = vertex.getVertexNumber() - 1;

                time++;

                depthFirstSearch(neighbourNode, d, low, parent, time);

                low[vertex.getVertexNumber() - 1] = Math.min(low[vertex.getVertexNumber() - 1], low[neighbourNode.getVertexNumber() - 1]);

                if (low[neighbourNode.getVertexNumber() - 1] > d[vertex.getVertexNumber() - 1])
                {
                    deleteConnection(vertex, neighbourNode);
                }
            } 
            else if (parent[vertex.getVertexNumber() - 1] != (neighbourNode.getVertexNumber() - 1))
            {
                low[vertex.getVertexNumber() - 1] = Math.min(low[vertex.getVertexNumber() - 1], low[neighbourNode.getVertexNumber() - 1]);
            }
        }
    }

    /**
     * The deleteConnection method is used to delete a connection between two vertex objects.
     *
     * @param vertexA a Node object representing the vertex a connection will be removed from
     * @param vertexB a Node object representing the vertex a connection will be removed from
     */

    public static void deleteConnection(Node vertexA, Node vertexB)
    {
        vertexA.getNodesVertexIsConnectedTo().remove(vertexB);

        vertexB.getNodesVertexIsConnectedTo().remove(vertexA);
    }

    public static ArrayList<ArrayList<ArrayList<Integer>>> OOPtofinArr(ArrayList<Node> verticesInput)
    {
        ArrayList<ArrayList<ArrayList<Integer>>> finArr = new ArrayList<>();

        for (int i = 0; i < verticesInput.size(); i++)
        {
            ArrayList<ArrayList<Integer>> tempArr = new ArrayList<>();

            ArrayList<Integer> connectionsToTheVertex = new ArrayList<>();

            ArrayList<Integer> vertexNumber = new ArrayList<>();

            vertexNumber.add(verticesInput.get(i).getVertexNumber());

            for (int x = 0; x < verticesInput.get(i).getNodesVertexIsConnectedTo().size(); x++)
            {
                connectionsToTheVertex.add(verticesInput.get(i).getNodesVertexIsConnectedTo().get(x).getVertexNumber());
            }

            tempArr.add(vertexNumber);

            tempArr.add(connectionsToTheVertex);

            finArr.add(tempArr);
        }

        return finArr;
    }

    /**
     * The isComplete method is used to check if a given graph is complete or not.
     * A complete graph is a graph where each vertex is connected to all other vertices.
     *
     * @param verticesInput an ArrayList of Node objects representing the vertices in the graph
     * @return true if the graph is complete, false otherwise
     */

    public static boolean isComplete(ArrayList<Node> verticesInput)
    {
        changeAllNodesToUnvisited();

        int counter = 0;

        for (Node node : verticesInput) 
        {
            ArrayList<Node> connections = node.getNodesVertexIsConnectedTo();

            ArrayList<Node> verticesInputMinusNode = new ArrayList<>(verticesInput);

            verticesInputMinusNode.remove(node);

            if (connections.equals(verticesInputMinusNode))
            {
                counter+=1;
            }

            if (counter == verticesInput.size())
            {
                return true;
            }
        }

        return false;
    }
    
    /**
     * The isWheelGraph method takes an ArrayList<Node> object as input (representing the component) and returns true/false if the component is a wheel or not.
     * 
     * @param verticesInput the ArrayList<Node> input
     * @return true or false
     */

    public static boolean isWheelGraph(ArrayList<Node> verticesInput) 
    {
        changeAllNodesToUnvisited();

        int centralVertexCount = 0;

        Node centralVertex = null;

        for (Node vertex : verticesInput) 
        {
            if (vertex.getNodesVertexIsConnectedTo().size() == verticesInput.size() - 1) 
            {
                centralVertexCount++;

                centralVertex = vertex;
            }
        }
        if (centralVertexCount != 1) 
        {
            return false;
        }

        for (Node vertex : verticesInput) 
        {
            if (vertex != centralVertex) 
            {
                if (!vertex.getNodesVertexIsConnectedTo().contains(centralVertex)) 
                {
                    return false;
                }
                for (Node connectedVertex : vertex.getNodesVertexIsConnectedTo()) 
                {
                    if (connectedVertex != centralVertex && !vertex.getNodesVertexIsConnectedTo().contains(connectedVertex)) 
                    {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * The isCycleGraph method takes an ArrayList<Node> object as input (representing the component) and returns true/false if the component is a cycle or not.
     * 
     * @param verticesInput the ArrayList<Node> input
     * @return true or false
     */
    
    public static boolean isCycleGraph(ArrayList<Node> verticesInput) 
    {
        changeAllNodesToUnvisited();

        for (Node vertex : verticesInput) 
        {
            if (!vertex.getVertexVisitedStatus()) 
            {
                if (DFSforCycleGraph(vertex)) 
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * The DFSforCycleGraph method is used to check if a given graph contains a cycle.
     * It uses a Depth First Search algorithm to traverse the graph starting from a given vertex.
     *
     * @param vertex the starting vertex from which the graph will be traversed
     * @return true if the graph contains a cycle, false otherwise
     */

    public static boolean DFSforCycleGraph(Node vertex) 
    {
        vertex.setVertexVisitedStatusToTrue();

        for (Node neighbor : vertex.getNodesVertexIsConnectedTo()) 
        {
            if (!neighbor.getVertexVisitedStatus()) 
            {
                if (DFSforCycleGraph(neighbor)) 
                {
                    return true;
                }
            } else if (neighbor != vertex) 
            {
                return true;
            }
        }
        vertex.setVertexVisitedStatusToFalse();

        return false;
    }

    /**
     * The isBipartite method takes an ArrayList<Node> object as input (representing the component) and returns true/false if the component is a bipartite or not.
     * 
     * @param verticesInput the ArrayList<Node> input
     * @return true or false
     */

    public static boolean isBipartite(ArrayList<Node> verticesInput) 
    {
        changeAllNodesToUnvisited();

        for (Node vertex : verticesInput) 
        {
            if (!vertex.getVertexVisitedStatus()) 
            {
                if (!checkBipartite(vertex, true)) 
                {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean checkBipartite(Node vertex, boolean color) 
    {
        vertex.setVertexVisitedStatusToTrue();

        for (Node connectedVertex : vertex.getNodesVertexIsConnectedTo()) 
        {
            if (!connectedVertex.getVertexVisitedStatus()) 
            {
                if (!checkBipartite(connectedVertex, !color)) 
                {
                    return false;
                }
            } else if (connectedVertex.getVertexVisitedStatus() == color) 
            {
                return false;
            }
        }

        return true;
    }

    /**
     * The checkTree method takes an ArrayList<Node> object as input (representing the component) and returns true/false if the component is a tree or not.
     * 
     * @param verticesInput the ArrayList<Node> input
     * @return true or false
     */

    public static boolean isTree(ArrayList<Node> verticesInput) 
    {
        changeAllNodesToUnvisited();

        int visitedCount = 0;

        for (Node vertex : verticesInput) 
        {
            if (!vertex.getVertexVisitedStatus()) 
            {
                if (!checkTree(vertex)) 
                {
                    return false;
                }

                visitedCount++;
            }
        }

        return visitedCount == 1;
    }

    /**
     * The checkTree method takes an Node object as input and returns true if all neighbours of the vertex node are unvisited.
     * 
     * @param vertex the Node input
     * @return true or false
     */

    private static boolean checkTree(Node vertex) 
    {
        vertex.setVertexVisitedStatusToTrue();

        for (Node connectedVertex : vertex.getNodesVertexIsConnectedTo()) 
        {
            if (connectedVertex.getVertexVisitedStatus()) 
            {
                return false;
            }

            checkTree(connectedVertex);
        }

        return true;
    }

    /**
     * The DSatur method takes an ArrayList<Node> object as input and returns an integer denoting the highest colour used (the upper bound).
     * 
     * @param verticesInput the ArrayList<Node> object
     * @return the chromaticNumber (upper bound)
     */

    public static Node findNextVertex(ArrayList<Node> verticesInput)
    {
        Node nextVertex = verticesInput.get(0); 

        for (Node node : verticesInput) 
        {
            if (getSaturationDegree(node) > getSaturationDegree(nextVertex))
            {
                nextVertex = node;
            }
            else if (getSaturationDegree(node) == getSaturationDegree(nextVertex))
            {
                if (getUncolouredConnections(node) > getUncolouredConnections(nextVertex))
                {
                    nextVertex = node;
                } 
            }
        }

        return nextVertex;
    }

    /**
     * The DSatur method takes an ArrayList<Node> object as input and returns an integer denoting the highest colour used (the upper bound).
     * 
     * @param verticesInput the ArrayList<Node> object
     * @return the chromaticNumber (upper bound)
     */

    public static int DSatur(ArrayList<Node> verticesInput)
    {
        ArrayList<Node> verticesDynamic = new ArrayList<>(verticesInput);

        while (verticesDynamic.size() > 0)
        {
            Node node = findNextVertex(verticesDynamic);

            int newColour = findLowestMissingNumber(coloursOfAdjacentVertices(node));
    
            node.changeColour(newColour);

            verticesDynamic.remove(node);
        }

        ArrayList<Integer> coloursUsed = new ArrayList<>();
        
        for (Node node : verticesInput) 
        {
            if (!coloursUsed.contains(node.getColour()))
            {
                coloursUsed.add(node.getColour());
            }
        }

        int chromaticNumber = coloursUsed.size();

        return chromaticNumber;

    }

    /**
     * The coloursOfAdjacentVertices method takes an Node object as input and returns an arraylist of all colours of adjacent vertices.
     * 
     * @param node the Node object
     * @return the arraylist of all colours of adjacent vertices
     */

    public static ArrayList<Integer> coloursOfAdjacentVertices(Node vertex)
    {
        ArrayList<Integer> coloursOfAdjacentVertices = new ArrayList<>();

        for (Node neighbourNode : vertex.getNodesVertexIsConnectedTo()) 
        {
            if (neighbourNode != null)
            {
                if (!coloursOfAdjacentVertices.contains(neighbourNode.getColour()))
                {
                    coloursOfAdjacentVertices.add(neighbourNode.getColour());
                }
            }

        }

        return coloursOfAdjacentVertices;
    }

    /**
     * The findLowestMissingNumber method takes an ArrayList<Integer> object as input and returns the smallest number not in the input list.
     * 
     * @param list the ArrayList<Integer> object
     * @return the smallest number not in the input list
     */

    public static int findLowestMissingNumber(ArrayList<Integer> list) 
    {
        int min = 1;

        while (list.contains(min)) 
        {
            min++;
        }

        return min;
    }

    /**
     * The getSaturationDegree method takes an Node object as input and returns the number of coloured connections for our DSatur algorithm.
     * 
     * @param node the Node object
     * @return the number of coloured connections
     */

    public static int getSaturationDegree(Node node)
    {
        int saturationDegree = 0;

        ArrayList<Integer> coloursOfAdajacentVertices = coloursOfAdjacentVertices(node);

        for (int i = 0; i < coloursOfAdajacentVertices.size(); i++)
        {
            if (coloursOfAdajacentVertices.get(i) != 0)
            {
                saturationDegree++;
            }
        }

        return saturationDegree;
    }

    /**
     * The getUncolouredConnections method takes an Node object as input and returns the number of uncolored connections for our DSatur algorithm.
     * 
     * @param node the Node object
     * @return the number of uncolored connections
     */

    public static int getUncolouredConnections(Node node)
    {
        int uncolouredDegree = 0;

        ArrayList<Integer> coloursOfAdajacentVertices = coloursOfAdjacentVertices(node);

        for (int i = 0; i < coloursOfAdajacentVertices.size(); i++)
        {
            if (coloursOfAdajacentVertices.get(i) == 0)
            {
                uncolouredDegree++;
            }
        }

        return uncolouredDegree;
    }

}


  