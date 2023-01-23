import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

class ColEdge
{
int u;
int v;
}

public class ReadGraph 
{
    public final static boolean DEBUG = false;
		
    public final static String COMMENT = "//";

    //! n is the number of vertices in the graph
    public static int n = -1;
        
    //! m is the number of edges in the graph
    public static int m = -1;

    ArrayList<ArrayList<ArrayList<Integer>>> finArr = new ArrayList<ArrayList<ArrayList<Integer>>>();
    
    public static int[][] readFile(String inputFile)

        {
            
        String inputfile = inputFile;
        
        boolean seen[] = null;
        

        
        //! e will contain the edges of the graph
        ColEdge e[] = null;
        
        try 	{ 
                FileReader fr = new FileReader(inputfile);
                BufferedReader br = new BufferedReader(fr);

                String record = new String();
                
                //! The first few lines of the file are allowed to be comments, staring with a // symbol.
                //! These comments are only allowed at the top of the file.
                
                //! -----------------------------------------
                while ((record = br.readLine()) != null)
                    {
                    if( record.startsWith("//") ) continue;
                    break; // Saw a line that did not start with a comment -- time to start reading the data in!
                    }

                if( record.startsWith("VERTICES = ") )
                    {
                    n = Integer.parseInt( record.substring(11) );					
                    if(DEBUG) System.out.println(COMMENT + " Number of vertices = "+n);
                    }

                seen = new boolean[n+1];	
                    
                record = br.readLine();
                
                if( record.startsWith("EDGES = ") )
                    {
                    m = Integer.parseInt( record.substring(8) );					
                    if(DEBUG) System.out.println(COMMENT + " Expected number of edges = "+m);
                    }

                e = new ColEdge[m];	
                                            
                for( int d=0; d<m; d++)
                    {
                    if(DEBUG) System.out.println(COMMENT + " Reading edge "+(d+1));
                    record = br.readLine();
                    String data[] = record.split(" ");
                    if( data.length != 2 )
                            {
                            System.out.println("Error! Malformed edge line: "+record);
                            System.exit(0);
                            }
                    e[d] = new ColEdge();
                    
                    e[d].u = Integer.parseInt(data[0]);
                    e[d].v = Integer.parseInt(data[1]);

                    seen[ e[d].u ] = true;
                    seen[ e[d].v ] = true;
                    
                    if(DEBUG) System.out.println(COMMENT + " Edge: "+ e[d].u +" "+e[d].v);
            
                    }
                                
                String surplus = br.readLine();
                if( surplus != null )
                    {
                    if( surplus.length() >= 2 ) if(DEBUG) System.out.println(COMMENT + " Warning: there appeared to be data in your file after the last edge: '"+surplus+"'");						
                    }
                
                br.close();

                }
        catch (IOException ex)
            { 
            // catch possible io errors from readLine()
            System.out.println("Error! Problem reading file "+inputfile);
            System.exit(0);
            }

        for( int x=1; x<=n; x++ )
            {
            if( seen[x] == false )
                {
                if(DEBUG) System.out.println(COMMENT + " Warning: vertex "+x+" didn't appear in any edge : it will be considered a disconnected vertex on its own.");
                }
            }


       
        //! At this point e[0] will be the first edge, with e[0].u referring to one endpoint and e[0].v to the other
        //! e[1] will be the second edge...
        //! (and so on)
        //! e[m-1] will be the last edge
        //! 
        //! there will be n vertices in the graph, numbered 1 to n

        int[][] eArr = new int[m][2];

        for (int i = 0; i < m; i++) {

            eArr[i][0] = e[i].u;

            eArr[i][1] = e[i].v;

        }



        return eArr; 
    }

    public static int getNumberOfVertices()
    {
        return n;
    }

    public static int getNumberOfEdges()
    {
        return m;
    }

}
