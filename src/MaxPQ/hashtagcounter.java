package MaxPQ;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

//The running class, hashtagcounter. It starts the program
public class hashtagcounter {
	
	//This is the main function that is executed when running the program
	public static void main(String[] inputOutputFileNames) {
		//Initializing the Heap and making File and scanner variables
		Scanner scanner;
		File inputFile = null;
		FibonacciHeapMax heap = new FibonacciHeapMax();
		
		//Enclosing in a try catch block to catch errors while entering files
		try {
			//Set input file as the 1st argument
			if(inputOutputFileNames.length == 0) {
				inputFile = new File("D:\\ALI\\Drive\\Computer Science\\Courses\\ADS\\project\\input.txt");
				
			}
			else {
				inputFile = new File(inputOutputFileNames[0]);
			}
			//Initialize scanner with input file
			scanner = new Scanner(inputFile);
			
			FileWriter writer = null;
			
			//Set output file as the 2nd argument if there is a 2nd argument
			if(inputOutputFileNames.length == 2) {
				File outFile = new File(inputOutputFileNames[1]);
				writer = new FileWriter(outFile);
			}
			
			//Read every line of input
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				
				//if line starts with "stop" then stop execution
				if (line.equals("stop")) break;
				
				//if line starts with '#' perform insert or increase key
				if (line.startsWith("#")) {
					String[] words = line.split(" ");
					heap.insert(words[0].substring(1), Integer.parseInt(words[1]));
				} 
				
				//otherwise it would start with a number so get top hashtags and output them
				else {
					
					//Get the N most used hashtags from the heap
					ArrayList<String> mostUsed = heap.getTop(Integer.parseInt(line));
					
					//Send output to output file if specified
					if(inputOutputFileNames.length == 2) {	
						writer.write(getString(mostUsed));
					}
					
					//Otherwise print on console
					else {
						System.out.println(getString(mostUsed));
					}
				}
			}
			
			//Close writer if there was an output file
			if(inputOutputFileNames.length == 2) {	
				writer.close();
			}
				
			//Close the scanner
			scanner.close();
		} 
		
		//Error handling
		catch (FileNotFoundException e) {
			System.out.println("File Not Found");
			e.printStackTrace();
		} 
		catch (Exception e) {
			System.out.println("Unknown error occured");
			e.printStackTrace();
		}
	}
	
	//Making output string from returned string list from heap
	static String getString(ArrayList<String> input) {
		
		//Initialize empty string
		String resultString = "";
		
		//For each string in string list append it to the resultString and a comma
		for (String i : input) {
			resultString += i + ",";
		}
		
		//Remove extra comma at the end
		resultString = resultString.substring(0, resultString.length() - 1);
		
		//Add a new line at the end
		resultString += "\n";
		
		//Return the final output string
		return resultString;
	}
}


//Node Class used in the FibonacciHeapMax class
class Node {
	
	//Store the parent node
	Node parent;
	
	//Store the child node
	Node child;
	
	//Store the node at left at same level
	Node left;
	
	//Store the node at right at same level
	Node right;
	
	//Store the hashtag name
	String hashtag;
	
	//Store child cut value
	boolean childLostBefore = false;
	
	//Store the frequency of occurrence of the hashtag
	int frequency;
	
	//Store the number of children the node has
	int degree = 0;
}

//The FibonacciHeapMax class used to implement the Max Fibonacci Heap
class FibonacciHeapMax {
	
	//Store the root node
	Node root;
	
	//Store the node containing hashtag with maximum frequency
	public Node maxIndicator;
	
	//Store the Hash Table with hastags pointing to nodes containing them
	private HashMap <String, Node> HashTable = new HashMap<String, Node>();
	
	//Get the top n hashtags where n is the input
	public ArrayList<String> getTop(int topHastagNum) {
		
		//The list that stores the deleted nodes for reinsertion
		ArrayList<Node> toBeReInserted = new ArrayList<Node>();
		
		//The list that stores the top hashtags
		ArrayList<String> topHastags = new ArrayList<String>();
		
		//N times add the maximum node to both queues and perform delete max
		//where N is the input number
		for (int i = 0; i < topHastagNum; i++) {
			
			//Add the maximum frequency hashtag to this list
			topHastags.add(maxIndicator.hashtag);
			
			//Add the maximum node to the reinsertion list
			toBeReInserted.add(maxIndicator);
			
			//Delete the maximum node
			deleteMaximum();
			
			//repeat N times
		}
		
		//Reinsert all the deleted nodes
		for (int i =0; i < toBeReInserted.size(); i++) {
			//Since the node was deleted it has no child any more
			toBeReInserted.get(i).child = null;
			
			//Similarly no parent as well
			toBeReInserted.get(i).parent = null;
			
			//The degree is also 0
			toBeReInserted.get(i).degree = 0;
			
			//After these changes add it back into the heap
			reinsert(toBeReInserted.get(i));
			
			//repeat till all nodes are reinserted
		}
		
		//returning the list containing the top hashtags
		return topHastags;
	}
	
	//Reinsert the Node in input back into the heap
	void reinsert(Node CurrentNode) {
		
		//The case when the heap is empty
		if (root == null) {
			
			//Make the node to be reinserted as the root
			root = CurrentNode;
			
			//Since root is the only element in the heap, the right of it is itself
			root.right = root;
			
			//Similarly left of it is itself
			root.left = root;
			
			//Root has no parent node
			root.parent = null;
			
			//making childcut false
			root.childLostBefore = false;
			
			//root is the only element so it is also the maximum element
			maxIndicator = root;
		}
		//The other case when the heap is not empty
		else {
			
			//get the node left of root
			Node lastNode = root.left;
			
			//put the node left of root as left of current node
			CurrentNode.left = lastNode;
			
			//put the root as right of current node
			CurrentNode.right = root;
			
			//make the current node as left of root
			root.left = CurrentNode;
			
			//the node that originally had root at right will now have the current node
			lastNode.right = CurrentNode;
			
			//since this is the top level, current node has no parent
			CurrentNode.parent = null;
			
			//make the childcut value as false
			CurrentNode.childLostBefore = false;
			
			//Since it is possible that newly inserted node can have frequency
			//higher than the maximum node, so if this is true make new node as maximum
			if (CurrentNode.frequency > maxIndicator.frequency) {
				maxIndicator = CurrentNode;
			}
		}
	}
	
	//Delete the maximum node and reorganize the heap
	void deleteMaximum() {
		
		//find the child node of the maximum node
		Node ChildOfMaxNode = maxIndicator.child;
		
		//traverse through all the children of the maximum node and remove
		//them from the heap and reinsert so that they get inserted at 
		//the top level
		for (int i = 0; i < maxIndicator.degree; i++) {
			
			//Get the right node at same level
			Node SiblingOfChildOfMaxNode = ChildOfMaxNode.right;
			
			//Remove the node currently being processed
			joinList(ChildOfMaxNode);
			
			//Reinsert the node back into heap so it is inserted at top level
			reinsert(ChildOfMaxNode);
			
			//New node to be processed is the right of current node
			ChildOfMaxNode = SiblingOfChildOfMaxNode;
			
		}
		
		//Remove the maximum node now from the heap
		removeNode(maxIndicator);
		
		//At this point if the root is null it means the heap is empty so stop code execution
		if (root == null) {
			return;
		}
		
		//However if heap is not empty then reorganize heap, so make the root as the new maximum node
		maxIndicator = root;

		//Current node to process is the root
		Node CurrentNodeToProcess = root;
		
		//Count the nodes on the top level linked list
		int size = 0;
		do {
			size++;
			CurrentNodeToProcess = CurrentNodeToProcess.right;
		} while (CurrentNodeToProcess != this.root);
		
		//Make a hashtable to store the degree of the nodes so that same degree nodes can be combined
		HashMap<Integer, Node> degreeHashTable = new HashMap<Integer, Node>();
		
		//first node to enter is the root with its degree as the key
		degreeHashTable.put(root.degree, root);
		
		//Next node to process is the right of root
		CurrentNodeToProcess = root.right;
		
		//Initializing the counter
		int count = 1;
		
		//Loop through all the top level nodes
		while (count < size) {
			int degree = CurrentNodeToProcess.degree;
			Node next = CurrentNodeToProcess.right;
			
			//If a same degree node is already seen before meld them
			while(degreeHashTable.containsKey(degree)) {
				Node meldedNode = null;
				Node PreExistingNode = degreeHashTable.get(degree);
				removeNode(PreExistingNode);
				removeNode(CurrentNodeToProcess);
				if (PreExistingNode.frequency > CurrentNodeToProcess.frequency) {
					PreExistingNode.degree++;
					if (PreExistingNode.child == null) {
						PreExistingNode.child = CurrentNodeToProcess;
						CurrentNodeToProcess.right = CurrentNodeToProcess;
						CurrentNodeToProcess.left = CurrentNodeToProcess;
					} else {
						Node lastNode = PreExistingNode.child.left;
						CurrentNodeToProcess.left = lastNode;
						CurrentNodeToProcess.right = PreExistingNode.child;
						lastNode.right = CurrentNodeToProcess;
						PreExistingNode.child.left = CurrentNodeToProcess;
					}
					CurrentNodeToProcess.parent = PreExistingNode;
					reinsert(PreExistingNode);
					meldedNode = PreExistingNode;
				} else {
					CurrentNodeToProcess.degree++;
					if (CurrentNodeToProcess.child == null) {
						CurrentNodeToProcess.child = PreExistingNode;
						PreExistingNode.right = PreExistingNode;
						PreExistingNode.left = PreExistingNode;
					} else {
						Node lastNode = CurrentNodeToProcess.child.left;
						PreExistingNode.left = lastNode;
						PreExistingNode.right = CurrentNodeToProcess.child;
						lastNode.right = PreExistingNode;
						CurrentNodeToProcess.child.left = PreExistingNode;
					}
					PreExistingNode.parent = CurrentNodeToProcess;
					reinsert(CurrentNodeToProcess);
					meldedNode = CurrentNodeToProcess;
				}
				
				degreeHashTable.remove(degree);
				degree = meldedNode.degree;
				CurrentNodeToProcess = meldedNode;
			}
			degreeHashTable.put(degree, CurrentNodeToProcess);
			if (maxIndicator.frequency < CurrentNodeToProcess.frequency) {
				maxIndicator = CurrentNodeToProcess;
			}
			CurrentNodeToProcess = next;
			count++;
		}
	}
	
	//Remove the node given as input from the linked list
	void joinList(Node CurrentNode) {
		CurrentNode.left.right = CurrentNode.right;
		CurrentNode.right.left = CurrentNode.left;
	}
	
	//Remove the node given as input from the heap
	void removeNode(Node CurrentNode) {
		if (CurrentNode.right == CurrentNode) {
			root = null;
			return;
		}
		joinList(CurrentNode);
		if (root == CurrentNode) {
			root = CurrentNode.right;
		}
	}
	
	//Insert or increase key
	void insert(String hashtag, int frequency) {
		
		//If it already exists then perform increase key
		if (HashTable.containsKey(hashtag)) {
			Node CurrentNode = HashTable.get(hashtag);
			CurrentNode.frequency += frequency;
			Node ParentOfCurrentNode = CurrentNode.parent;
			if (ParentOfCurrentNode != null && CurrentNode.frequency > ParentOfCurrentNode.frequency) {
				CompleteCascadingCut(CurrentNode, ParentOfCurrentNode);
			} else {
				if (CurrentNode.frequency > maxIndicator.frequency) {
					maxIndicator = CurrentNode;
				}
			}
			
		} 
		
		//If it does not exist then insert it
		else {
			Node CurrentNode = new Node();
			CurrentNode.frequency = frequency;
			CurrentNode.hashtag = hashtag;
			if (root == null) {
				root = CurrentNode;
				root.frequency = frequency;
				root.hashtag = hashtag;
				root.right = root;
				root.left = root;
				root.parent = null;
				maxIndicator = root;
				HashTable.put(hashtag, root);
			}
			else {
				Node NodeLeftOfRoot = root.left;
				CurrentNode.left = NodeLeftOfRoot;
				CurrentNode.right = root;
				root.left = CurrentNode;
				NodeLeftOfRoot.right = CurrentNode;
				CurrentNode.parent = null;
				if (CurrentNode.frequency > maxIndicator.frequency) {
					maxIndicator = CurrentNode;
				}
				HashTable.put(hashtag, CurrentNode);
			}
		}
	}
	
	//Perform Cascading cut
	void CompleteCascadingCut(Node CurrentNode, Node ParentOfCurrentNode) {
		
		//Since we are removing the Current Node, we have to point the 
		//parents child to its right if it was the child node
		if (ParentOfCurrentNode.child == CurrentNode) {
			ParentOfCurrentNode.child = CurrentNode.right;
		}
		
		//Remove the Current Node
		joinList(CurrentNode);
		
		//Remove parent relation and reinsert on top level
		CurrentNode.parent = null;
		reinsert(CurrentNode);
		
		//Since the node is now reinserted, its childcut will now be reset and false
		CurrentNode.childLostBefore = false;
		
		//Parent lost a child so degree is reduced
		ParentOfCurrentNode.degree--;
		
		//We need to check if the degree has gone to 0, if so make child null
		if (ParentOfCurrentNode.degree == 0) {
			ParentOfCurrentNode.child = null;
		}
		
		//If parent node exists, perform cascading cut on it if child cut 
		//is true otherwise dont and make child cut true
		if (ParentOfCurrentNode != null) {
			if (CurrentNode.childLostBefore) {
				CompleteCascadingCut(CurrentNode, ParentOfCurrentNode);
			} else {
				CurrentNode.childLostBefore = true;
			}
		}
	}
		
}


