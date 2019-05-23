import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/*This main program is used to display various menu to the user and user can choose 6 different options 
for performing operations on Network Topology*/

public class LinkStateRouting
{

	//global variables
	static int[] tempDistance = new int [100]; 
	static int source, destination;
	static int flag=0;
	static int bestRouterNumber;
	static int deletedRouter;
	
	public static void main(String[] args) throws Exception 
	{

		int matrix[][]=null; 											// Matrix for storing input network topology matrix
		int choice;
		Scanner sc = new Scanner(System.in);
		do
		{
			System.out.println("\n CS542 Link State Routing Simulator");
			
			System.out.println("\n(1) Create a Network Topology");
			System.out.println("(2) Build a Connection Table");
			System.out.println("(3) Shortest path to destination router");
			System.out.println("(4) Modify Topology");
			System.out.println("(5) Best Router for broadcast");
		    System.out.println("(6) Exit");
	
		    System.out.println("Command:");
		    choice = sc.nextInt();
		
			switch (choice)
			{
			case 1:
				   System.out.println("Enter Input Topology Filename:");
    			   String filename = sc.next();
    			   matrix = readInputMatrix(filename);
    			   displayMatrix(matrix);
    			   break;
				
			case 2:
					System.out.println("Enter router number");
					int router = sc.nextInt();
					buildInterfaceTable(matrix,router);
				    break;
				   
			case 3:
				   shortestPath(matrix);
				   break;
				   
			case 4:
				modifyTopology(matrix);
				   break;
				   
			case 5:
				   bestRouter(matrix);
				   break;	   
			
			case 6:
				System.out.println("Exit CS542-04 2017 Fall project. Good Bye!");
				System.exit(0);
			default:System.out.println("Invalid choice. Please enter correct number");
			}
			
		}while(choice != 6 );			
		sc.close();
		
	}
	
	
	/*This function reads the input network topology matrix from the specified location. User is prompted
	 * to enter the input file name when he/she selects 1st option from the menu.
	 * This function handles reading the data and storing it in integer matrix
	 */
	
	public static int[][] readInputMatrix(String filename) throws Exception {
		
		int rowCount = 0, colCount = 0, temp=0;
		String rowText, row[]; 
		
		BufferedReader one = new BufferedReader(new FileReader(filename));    
																					 
		while((rowText = one.readLine())!=null)				//calculation of row and column count 
		{
			rowCount++;
			row = rowText.split(" ");
			colCount = row.length;
		}
		one.close();
		
		int matrix[][] = new int[rowCount][colCount];
		
		BufferedReader two = new BufferedReader(new FileReader(filename)); 
		while ((rowText = two.readLine()) != null)			// storing input elements to matrix
		{
			row = rowText.split(" ");
			for(int i=0;i<colCount;i++)			
			{
				matrix[temp][i] = Integer.parseInt(row[i]);
			}
			temp++;
		}
		two.close();
		
		System.out.println("Network Topology Matrix:");
		return matrix;
	}
		
	/*This function is used to display the stored network topology matrix*/
	
    public static void displayMatrix(int matrix[][])
    {
    	for(int i=0;i<matrix.length;i++)
    	{
    		for(int j=0;j<matrix.length;j++)
    		{
    			System.out.print(matrix[i][j] + " ");
    		}
    		System.out.println();
    	}
    }
	
   
    /*This function is executed when user choose 2nd option. First, it checks whether router asked by user is down or not.
     * If it is down, it will tell that router is down, otherwise generates connection table 
     * for respective router prompted by the user*/
    
	public static void buildInterfaceTable(int matrix[][], int router)
	{	
		int i,flag;
		i=0;
		flag = checkIfRouterDown(matrix, router);
	
		if (flag == 1)
		{	
			System.out.println("Dest\tInterface");
			for(i=0;i<matrix.length;i++)
			{
				if(i!=router-1);
				{
					System.out.print("R"+(i+1));
					dijkstra(matrix, router-1, i, 1);
				}
			}	
		}
		else {
			System.out.print("Router "+(router)+ " is down.");
		}
		
	}

	
/*This function checks whether router asked by user is down or not*/
	
public static int checkIfRouterDown(int matrix[][], int router)
{
	int i,j;
	int[] temp1 = new int[matrix.length];
 	for(j=0; j < matrix[0].length; j++)							
 	{
		temp1[j] = -1;
	}
	
	int[] temp2 = new int[matrix.length];
 	for(i=0; i < matrix[0].length; i++)							
 	{
 		temp2[i] = matrix[router-1][i];
	}

 	int flag = 0;
 	
 	for(i=0; i < matrix[0].length; i++)
 	{
 		if(temp1[i] != temp2[i])
 			{
 			flag = 1; break;
 			}
 		else
 			flag = 0;
 	}
 	return flag;
}
	
	/*This function is executed when user choose 3rd option of the menu.
	 * It prompts user to input source and destination routers in order to compute 
	 * shortest path between them. Then, it checks whether source and destination router is down or not.
	 * If both routers are not down, then only this function calls Dijkstra's Algorithm to calculate shortest 
	 * path between source and destination in the network topology*/
	
	public static void shortestPath(int matrix[][]) throws IOException
	{
		Scanner sc =new Scanner(System.in);

		System.out.println("Enter the source router");
		int src = Integer.parseInt(sc.nextLine()) - 1;
		source = src;
		
		System.out.println("Enter the destination router");
		int dest = Integer.parseInt(sc.nextLine()) - 1;
		destination = dest;
		
		int flagSrc, flagDest;
		flagSrc = checkIfRouterDown(matrix, src+1);
		flagDest= checkIfRouterDown(matrix, dest+1);
		
		if (flagSrc == 0 || flagDest == 0)
		{	
			if(flagSrc == 0) System.out.print("Router "+(src+1)+ " is down.");
			if(flagDest == 0) System.out.print("Router "+(dest+1)+ " is down.");
		}
		if(flagSrc == 1 && flagDest == 1)
		{
			dijkstra(matrix,src,dest,2);
			flag=1;	
		}
		
	}

	
	/*This function is executed when user selects 4th option to modify the network topology. It asks user 
	 * to input router that is to be deleted and updates the network topology matrix's specific 
	 * row and column entry of router to -1 which indicates that their is no direct path to that 
	 * router i.e router is down. Then it check whether previously any path calculated or not.
	 * If yes, then it shows the updated cost of that path. If not, then it passes updated matrix to Dijkstra's
	 * algorithm to compute shortest path  */
	
	public static void modifyTopology(int[][] matrix) throws IOException{
		Scanner s = new Scanner(System.in);
		System.out.println("Enter router to be deleted");
		int router = s.nextInt();
		
		for(int i=0;i<matrix.length; i++) //row
		{
			if(i==router-1)
			{
				for(int j=0;j<matrix.length;j++)
				{
					matrix[i][j] = -1;
					matrix[j][i] = -1;
				}
			}
		}
		deletedRouter = router;
		
		displayMatrix(matrix);
		//interfaceTable(matrix,router);
		
		if(flag == 1)
		{
			System.out.println("\nAfter deleting router "+ router + ",");
			dijkstra(matrix,source,destination,2);
		}
		else
		{
			System.out.println("\nTo calculate shortest path\n");
			shortestPath(matrix);
		}
	}
	
	/* This function determines the best router in topology. It calculates shortest path to every other router and stores the sum of cost to every other
	 *  and this sum is calculated for every router and search for minimum sum. Whichever router has that minimum sum, it will be declared as best router in topology */ 
	public static void bestRouter(int[][] matrix) throws IOException
	{
		int temp=1;
		
		for(int i=0; i< matrix[0].length; i++)
		{
			//if(i != deletedRouter-1)	
			dijkstra(matrix,i,temp,3);
		}
		for(int i=0; i< matrix[0].length; i++)
		{	
			if(bestRouterNumber-1 != i)
			dijkstra(matrix,bestRouterNumber-1,i,2);
		}
		}
	
	
	/* This function is called when user want to calculate interface table, shortest path and best router in topology.
	 * Here, if passed parameter 'option' is 1 then it calculates interface table for entered router.
	 * if passed parameter 'option' is 2 then it calculates shortest path and distance for entered source and destination routers.
	 * if passed parameter 'option' is 3 then it calculates best router in topology.   */ 
	public static void dijkstra(int[][] matrix, int sourceRouter , int destinationRouter ,int option)
	{
		
		int[] distance = new int[matrix.length];						// distance array will maintain shortest distance between 2 routers
		int[] visited = new int[matrix.length];							// To keep track of visited routers, visited array is used 
		int[] predecessor = new int[matrix[0].length];					//initialize predecessor that will help further to traverse path
		
				
			
					
		for (int i = 0; i < predecessor.length; i++) 					
		{
			predecessor[i] = sourceRouter;								// filled predecessor array with source number
		}
		
		for(int j=0;j<matrix[0].length;j++)							    //Copy router's  weights into distance array
		{
			distance[j] = matrix[sourceRouter][j];
		}

		int nextRouter = sourceRouter;											 
		
		visited[nextRouter] = 1;							     		//  make initially visited of source as true
		
		for(int x=0;x<matrix[0].length; x++)							//sort distance array based on minimum path weight
		{
			int min = 9999;
			for (int j = 0; j < distance.length; j++)
			{
				if (visited[j]!=1 && j != sourceRouter && distance[j] != -1)
				{
					if (distance[j] < min)
					{
						min = distance[j];
						nextRouter = j;
					}
				}
			}
			
			if (nextRouter == destinationRouter) 										
			{
				break;
			}
			
			visited[nextRouter] = 1;						
			
			for (int i = 0; i < distance.length; i++)
			{
					if(visited[i] != 1 && distance[i] == -1 && matrix[nextRouter][i] != -1)					// for routers with weight -1		
					{
						distance[i] =  matrix[nextRouter][i] + distance[nextRouter];
						predecessor[i] = nextRouter;
					}
					
					else if(matrix[nextRouter][i] != -1 && distance[i] > min+matrix[nextRouter][i]) 		
					{
						distance[i] =  matrix[nextRouter][i] + distance[nextRouter];
						predecessor[i] = nextRouter;	
					}
				
			}
		}
		
		int sum=0,i;
		
		for(i =0; i < distance.length; i++)									// to calculate best router, sum of distance array is calculated 
		{									
			sum = sum + distance[i];
		}
		if(sum < 0)
			tempDistance[sourceRouter] = 9999;
		else 
			tempDistance[sourceRouter] = sum;
		
		// if passed parameter 'option' is 3 then it calculates best router in topology.
		if(option == 3 && sourceRouter==4)											// based on sum of all distance from one particular router, minimum sum is calculated to find best router
		{
			int j=0, minimum = tempDistance[0];
			for(i=0; i < matrix[0].length; i++)
			{
	            if(tempDistance[i]<minimum)
	            {
	                minimum=tempDistance[i];
	                j = i;
	            }
	        }
			bestRouterNumber = j+1;
			System.out.println("Best router for broadcast : " + (j+1));
		}
		
		
		/*User provided source and destination values are used to calculate minimum path*/
		//if passed parameter 'option' is 2 then it calculates shortest path and distance for entered
		//source and destination routers.
		if(option ==2 ) 															
		{
			minimumWeight(predecessor, sourceRouter, destinationRouter, distance.length,1);
			System.out.println();
			int total = distance[destinationRouter] - distance[sourceRouter];
			
			System.out.println("Total cost is =  "+ total);
		}
		
		/*To determine connection table for the router user has requested*/
		/* if passed parameter 'option' is 1 then it calculates interface table for entered router */ 
		if(option==1)
		{
			minimumWeight(predecessor, sourceRouter, destinationRouter, distance.length, 2);
		}
	
	}
	
	/* This function is used to calculate minimum weight using back tracing of predecessor array */
	public static void minimumWeight(int[] predecessor, int source, int destination, int length, int option)
	{

		
		int[] Path = new int[length];
		int i = destination;
		Path[0] = i;
		int now = 1;		
		
		boolean flag=false;
		
		while (predecessor[i] != source) 			// alignment of predecessor routers is done to Path array to calculate the path
		{
			i = predecessor[i];
			Path[now] = i;
			now++;
		}
		Path[now] = source;

		if(option==1) 																// triggered when user wants to output the shortest path between source and destination
		{
			System.out.print("Shortest path from "+(source + 1)+" to "+(destination + 1)+"\n");
			for (int j = now; j > 0; j--)
			{
				System.out.print("R"+(Path[j] + 1)+" to ");
			}
			System.out.print("R"+(Path[0]+1)) ;
		}
		
		
		if(option==2) 															// triggered when user wants to get connection table for specified router
		{	
			if(now>0)
			{
				for (int k = now-1; k > 0; k--)
				{
					System.out.print("\tR" + (Path[k] + 1) + "\n");
					k--;
					flag=true;
				}
			}

			if(flag==false)
			{
				if(source==destination)
				System.out.print("\t--\n");
				else
				System.out.print("\tR" + (Path[0] + 1)+"\n") ;
			}

		}
	}

	
}
