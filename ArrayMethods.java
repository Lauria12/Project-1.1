import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;


/** The part where we store some methods to convert the input in a way so that we can then work with it. 
 * 
*/
public class ArrayMethods
{

	/**
	It creates an ArrayList called finArr, which will be used to store all the vertices and their connections.
	The method then loops through the 2D array of edges, starting with the first vertex, and looks for its connections in the edges provided.
	It creates a temporary ArrayList called tempArr to store all the vertices the "main vertex" (the one we start with) is connected to.
	Another temporary ArrayList called temp2Arr is used to put the vertex number ArrayList and the tempArr ArrayList together,
	so that we can then put it into the finArr.
	The method then checks the individual arrays of edges whether a vertex is in the first position (index 0), or in the second position (index 1).
	It then uses a Collection to remove any duplicates and adds the vertex number ArrayList and tempArr ArrayList to temp2Arr.
	Finally, it adds temp2Arr to finArr and returns finArr, which is the final ArrayList that contains all the vertices and their connections.
     * @param numberOfVertices int representing the number of vertices in our graph
     * @param numberOfEdges int representing the number of edges in our graph
     * @param eArr int[][] representing the edges in our graph
     * @return ArrayList<ArrayList<ArrayList<Integer>>> representing the vertices and their connections in anti OOP way (1st phase code :/ ))
	*/

    public static ArrayList<ArrayList<ArrayList<Integer>>> inputArrayFinal(int numberOfVertices, int numberOfEdges, int[][] eArr) 
		{
			// Number of vertices is n, number of edges is m.
			// Otherwise it is pretty self-explanatory
	
			int[][] inputArr = eArr;
	
			ArrayList<ArrayList<ArrayList<Integer>>> finArr = new ArrayList<ArrayList<ArrayList<Integer>>>();

			// Here we go through the 2d array with edges and, starting with the fisrt vertex
			// look for its connections in the edegs we were given, so our range is from 0
			// to the numberOfVertices so that we don't miss a single vertex
	
			for (int i = 0; i < numberOfVertices; i++) {

				// Arraylist tempArr is used to store all the vertices our 
				// "main vertex" (the one we start with) is connected to
	
				ArrayList<Integer> tempArr = new ArrayList<Integer>();

				// Arraylist vertex number denotes the number of the vertex,
				// it has to be an arraylist because we cannot put together
				// an integer and an arraylist of arraylists if that makes sense
				// (even though its just a single number)
	
				

				// temp2Arr is only used to make this work, so we put the vertex number arraylist
				// and the tempArr arraylist into a single temp2Arr so that we can then put it into the finArr
				// so that our algorithms can work with it
	
				ArrayList<ArrayList<Integer>> temp2Arr = new ArrayList<ArrayList<Integer>>();

				// This part just checks the individual arrays of edges whether a vertex
				// is in the first position (index 0), or in the second position (index 1)
				// (because we get it in a form of [1,2] or [2,1])
	
				for (int x = 0; x < numberOfEdges; x++) {
	
					if ((i+1) == inputArr[x][0]) {
						
						tempArr.add(inputArr[x][1]);
	
					} else if ((i+1) == inputArr[x][1]) {
	
						tempArr.add(inputArr[x][0]);
	
					}
	
				}

				// As described above, we put all the arraylists together
				// so that we end up with an arraylist that contains all the vertices
				// and all their connections
				// We do it this way because it seemed the most logical

                Collection<? extends Integer> set = new HashSet<>(tempArr);

                tempArr.clear();

                tempArr.addAll(set);

                ArrayList<Integer> vertexNumber = new ArrayList<Integer>();
				
				vertexNumber.add(i+1);
	
				temp2Arr.add(vertexNumber);
	
				temp2Arr.add(tempArr);
	
				finArr.add(temp2Arr);
	
			}

			// We return the final arraylist that our algorithm can work with
	
			return(finArr);
			
		}

        public static int getMaxValue(int[][] numbers) 
        {
            int maxValue = numbers[0][0];
            for (int j = 0; j < numbers.length; j++) 
            {
                for (int i = 0; i < numbers[j].length; i++) 
                {
                    if (numbers[j][i] > maxValue) 
                    {
                        maxValue = numbers[j][i];
                    }
                }
            }
            return maxValue;
        }


}

