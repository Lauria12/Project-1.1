import java.util.ArrayList;

/**

The Node class represents a vertex in a graph.
It includes various functionalities such as adding adjacent vertices,
getting the vertex number, getting the vertices that a vertex is connected to,
getting the visited status of a vertex, setting the visited status of a vertex,
getting the color of a vertex and changing the color of a vertex.
*/

public class Node 
{

    
    public int vertexNumber;

    public ArrayList<Node> nodesVertexIsConnectedTo = new ArrayList<>();

    private boolean visited = false;

    public ArrayList<Node> staticNodesVertexIsConnectedTo = new ArrayList<>();

    public int colour = 0;

    /**
    Constructor for the Node class.
    @param vertexNumberInput The vertex number of the node.
    */
    Node(int vertexNumberInput)
    {
        vertexNumber = vertexNumberInput;
    }

    
    /**
    Get the vertex number of the node.
    @return The vertex number of the node.
    */
    public int getVertexNumber() 
    {
        return vertexNumber;
    }

    /**
    Add a vertex as adjacent to the node.
    @param vertex The vertex to be added as adjacent.
    */
    public void addAdjacentVertices(Node vertex)
    {
        nodesVertexIsConnectedTo.add(vertex);

        staticNodesVertexIsConnectedTo.add(vertex);
    }

    /**
    Get the vertices that the node is connected to.
    @return An ArrayList of the vertices that the node is connected to.
    */
    public ArrayList<Node> getNodesVertexIsConnectedTo() 
    {
        return nodesVertexIsConnectedTo;
    }

    public boolean getVertexVisitedStatus()
    {
        return visited;
    }

    public void setVertexVisitedStatusToTrue()
    {
        visited = true;
    }

    public void setVertexVisitedStatusToFalse()
    {
        visited = false;
    }

    public int getColour() 
    {
        return colour;
    }

    public void changeColour(int colour)
    {
        this.colour = colour;
    }

    public void changeColourBy1Up()
    {
        colour += 1;
    }







}