package mipssim;

import java.awt.List;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.PatternSyntaxException;

public class Simulator 
{
	int numRegisters 	= 	32;
	int maxNumIns 		= 	256;
	int maxLabelLength	=	32;
	int maxNumLabels	=	32;
	int	maxLineLength	=	256;
	int memInsStart 	= 	0x4000;
	int memDataStart 	= 	0x1000;
	int memDataSize 	=	0x1000;
	int regGP			=	28;
	int regSP			=	29;
	int NOP = 	-1;
	static int LW= 		35;
	static int SW = 	43;
	static int ADD = 	32;
	static int SUB = 	34;
	static int ADDI = 	8;
	static int BNE = 	5;
	static int BEQ = 	4;
	static int AND = 	36;
	static int OR = 	37;
	static int NOR = 	39;
	static int XOR = 	38;
	static int rs = -1;
	static int rt = -1;
	static int rd = -1;
	int i = 0;
	int addr = 0;
	int[] regFile = new int[numRegisters];
	int[] dataMem = new int[memDataSize/4];
	ArrayList<String> insArray = new ArrayList<String>();
	
	 public static String[] loadFile(String input) throws FileNotFoundException, EOFException, IOException
	 {
		 String path = input;
		 BufferedReader br = null;
		 FileInputStream fs = null;
		 String line = "";
		 
		 fs = new FileInputStream(path);
		 br = new BufferedReader(new InputStreamReader(fs));
		 ArrayList<String> lineArray = new ArrayList<String>();
		 int numIns = 0;
		 while((line = br.readLine()) != null)
		 {
			 lineArray.add(line);
         }
	 }
             
	 
	 public static int[][] stringsToInts(ArrayList<String> s)
	 {
		 int[] ins = new int[128];
		 String currentString;
		 String[] tempArray = new String[6];
		 int[] tempIntArray = new int[6];
		 
		 for(int n = 0; n < s.size(); n++)
		 {
			 
			 currentString = s.get(n);
			 currentString.trim();
			 currentString.replace(",", "");
			 currentString.replace("$", "");

			 tempArray = currentString.split("\\s+");
			 
			 for(int i = 0; n < tempArray.length; n++)
			 {
				 if(n == 0)
				 {
					
					switch (currentString)
					{
						case "lw":
							tempIntArray[0] = LW;
							break;
						case "sw":
							tempIntArray[0] = SW;
							break;
						case "add":
							tempIntArray[0] = ADD;
							break;
						case "sub":
							tempIntArray[0] = SUB;
							break;
						case "addi":
							tempIntArray[0] = ADDI;
							break;
						case "bne":
							tempIntArray[0] = BNE;
							break;
						case "beq":
							tempIntArray[0] = BEQ;
							break;
						case "and":
							tempIntArray[0] = AND;
							break;
						case "or":
							tempIntArray[0] = OR;
							break;
						case "nor":
							tempIntArray[0] = NOR;
							break;
						case "xor":
							tempIntArray[0] = XOR;
							break;
					}
				 }
				 
				 tempIntArray[n] = Integer.parseInt(currentString);
			 }
		 }
	 }
	 
	
}
