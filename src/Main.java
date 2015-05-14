import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 *  main class.
 *  Author:  Liyun Zhang
 */
public class Main {
	final static String graphFileName = "Data/tmpGraph.txt";
	
	public static void main(String[] args) {
		if(args.length < 4) {
			System.out.println("Usage: java -classpath ./bin Main ancestryMatrixFile  threshold  vafFile  excludeFile");
			System.out.println("\nExample: java -classpath ./bin  Main  data/CLL077_deep_anc.txt  0.8  data/CLL077_deep_vaf.txt data/CLL077_deep_exclude.txt");
			return;
		}
		
		String ancestryMatrixFile = args[0];	
		
		double threshold = Double.parseDouble(args[1]);	

		String vafFile = args[2];
		String excludedFile = args[3];	

		boolean generated = generateGraphFile(ancestryMatrixFile, threshold, vafFile, graphFileName, excludedFile);
		
		if(generated) {
			Graph g = new Graph();
			
			g.initFromFile(graphFileName);	
			g.printLargestSpanningTree();
		}
	}
	
	
	static boolean generateGraphFile(String ancestryMatrixFile, double threshold, String vafFile, String graphFileName, String excludedFile) {
		String lineString = "";
		final int maxRowNum = 200;
		final int maxColNum = 300;
		String[] geneNames = new String[maxColNum]; 
		Set<String> excludedSet = new HashSet<String>();
		
		try {
			/* Read name of genes */
			Scanner file = new Scanner(new File(vafFile));
			lineString = file.nextLine();	
			int rowNum = Integer.parseInt(lineString);
			lineString = file.nextLine();	
			int colNum = Integer.parseInt(lineString);	
			for (int i = 1; i <= rowNum; i++) {
				lineString = file.nextLine();
			}	
			lineString = file.nextLine();	  // empty line
			lineString = file.nextLine();	  // name of samples
			lineString = file.nextLine();	  // name of genes
			Scanner line = new Scanner(lineString);
			for(int i = 1; i <= colNum; i++) {
				geneNames[i] = line.next();
			}
			file.close();
			line.close();
			
			// Read genes excluded
			if(excludedFile != null) {
				file = new Scanner(new File(excludedFile));					
				while(file.hasNext()) {
					lineString = file.nextLine();
					excludedSet.add(lineString);
				}
			}
			
			PrintWriter output = new PrintWriter(graphFileName);	
			
			/* This Scanner will scan the file, one line at a time. */			
			file = new Scanner(new File(ancestryMatrixFile));			
			lineString = file.nextLine();	
			rowNum = Integer.parseInt(lineString);
			lineString = file.nextLine();	
			colNum = Integer.parseInt(lineString);
			if(rowNum != colNum)  {
				output.close();
				file.close();
				return false;
			}
			for (int i = 1; i <= rowNum; i++) {
				lineString = file.nextLine();
				line = new Scanner(lineString);
				for (int j = 1; j <= colNum; j++) {
					double ancestryProb = Double.parseDouble(line.next());
					if(i != j && ancestryProb >= threshold) {						
						if( !excludedSet.contains(geneNames[i]) && !excludedSet.contains(geneNames[j])) {
							output.printf("%s %s\n", geneNames[i], geneNames[j]);
						}
					}
				}
			}
			file.close();
			file = new Scanner(new File(vafFile));
			lineString = file.nextLine();	
			rowNum = Integer.parseInt(lineString);
			lineString = file.nextLine();	
			colNum = Integer.parseInt(lineString);
			

			
			output.printf("STARTVAF---------------------\n");
			
			double[][] vafMatrix = new double[maxRowNum][maxColNum];
			for (int i = 1; i <= rowNum; i++) {
				lineString = file.nextLine();
				line = new Scanner(lineString);
				for (int j = 1; j <= colNum; j++) {
					double vaf = Double.parseDouble(line.next());
					vafMatrix[i][j] = vaf;
				}
			}
			file.close();
			
			for (int j = 1; j <= colNum; j++) {
				if(excludedSet.contains(geneNames[j])) {
					continue;
				}
				output.printf("%s", geneNames[j]);
				for (int i = 1; i <= rowNum; i++) {
					output.printf(" %f", vafMatrix[i][j]);
				}
				output.printf("\n");
			}
			
			output.close();
		}  catch (IOException e) {
				System.out.println("Error accessing " + ancestryMatrixFile);
				System.exit(1);
			} 
		return true;
	}
}
