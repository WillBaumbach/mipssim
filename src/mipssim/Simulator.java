package mipssim;

import java.awt.List;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.PatternSyntaxException;

public class Simulator 
{
	static String filepath = "src/mipssim/Untitled.txt";
	
	//Initial parameters
	int numRegisters 	= 	32;
	int maxNumIns 		= 	256;
	int maxLabelLength	=	32;
	int maxNumLabels	=	32;
	int	maxLineLength	=	256;
	static int PCPointer 	= 	16384;
	int memDataStart 	= 	0x1000;
	int memDataSize 	=	0x1000;
	int regGP			=	28;
	int regSP			=	29;
	int NOP = 				-1;
	
	// OpCode + Type
	static String R = 		"000000";
	static String LW = 		"100011";
	static String SW = 		"101011";
	static String ADD = 	"100000";
	static String SUB = 	"100010";
	static String ADDI = 	"001000";
	static String BNE = 	"000101";
	static String BEQ = 	"000100";
	static String AND = 	"100100";
	static String OR =	 	"100101";
	static String NOR = 	"100111";
	static String XOR = 	"100110";
	
	//Control Signals
	int RegDst;
	int Jump;
	int Branch;
	int MemRead;
	int MemtoReg;
	int ALUOp;
	int MemWrite;
	int ALUSrc;
	int RegWrite;
	
	// Other variables
	static int rs = -1;
	static int rt = -1;
	static int rd = -1;
	int i = 0;
	int addr = 0;
	int[] regFile = new int[numRegisters];
	int[] dataMem = new int[memDataSize/4];
	static ArrayList<String> insArray = new ArrayList<String>();
	static String instruction = null;
	static int numIns = 0;
	
	 @SuppressWarnings("resource")
	public static ArrayList<String> loadFile(String input) throws FileNotFoundException, EOFException, IOException
	 {
		 String path = input;
		 BufferedReader br = null;
		 FileInputStream fs = null;
		 String line = "";
		 
		 fs = new FileInputStream(path);
		 br = new BufferedReader(new InputStreamReader(fs));
		 ArrayList<String> lineArray = new ArrayList<String>();
		 while((line = br.readLine()) != null)
		 {
			 lineArray.add(line);
			 numIns++;
         }
		 
		 
		 return lineArray;
	 }
             
	 /*
	  * TODO: 	Add data structure to hold labels and addresses
	  * 		Finish the code for I type and J ins
	  * 
	  * 
	  * */
	 public static String inputToBinary(String s)
	 {
		 String ins = null;
		 String currentString;
		 String[] tempArray = new String[4];
		 String tempString = "";
		 String rs = "";
		 String rt = "";
		 String rd = "";
		 String immediate = "";
		 String target = "";
		
			currentString = s;
			currentString = currentString.trim();           
			currentString = currentString.replace(",", "");
			currentString = currentString.replace("$", "");
			currentString = currentString.replace("r", "");
			currentString = currentString.replace("R", "");


			tempArray = currentString.split("\\s+");
			switch (tempArray[0])
			{
			
				// Needs work
				case "lw":
					tempString = LW;
					rs = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[2]))).replace(' ', '0');
					rt = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[1]))).replace(' ', '0');
					immediate = tempArray[3];
					break;
					
				// Needs Work
				case "sw":
					tempString = SW;
					break;
				case "add":
					rd = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[1]))).replace(' ', '0');
					rs = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[2]))).replace(' ', '0');
					rt = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[3]))).replace(' ', '0');
					tempString = R + rs + rt + rd + "00000" + ADD;
					ins = tempString;
					break;
				case "sub":
					rd = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[1]))).replace(' ', '0');
					rs = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[2]))).replace(' ', '0');
					rt = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[3]))).replace(' ', '0');
					tempString = R + rs + rt + rd + "00000" + SUB;
					ins = tempString;
					break;
				case "addi":
					rs = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[2]))).replace(' ', '0');
					rt = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[1]))).replace(' ', '0');
					immediate = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[3]))).replace(' ', '0');
					tempString = ADDI + rs + rt + immediate;
					ins = tempString;
					break;
					
					// Needs Work
				case "bne":
					tempString = BNE;
					break;
					
					// Needs Work
				case "beq":
					tempString = BEQ;
					break;
				case "and":
					rd = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[1]))).replace(' ', '0');
					rs = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[2]))).replace(' ', '0');
					rt = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[3]))).replace(' ', '0');
					tempString = R + rs + rt + rd + "00000" + AND;
					ins = tempString;
					break;
				case "or":
					rd = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[1]))).replace(' ', '0');
					rs = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[2]))).replace(' ', '0');
					rt = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[3]))).replace(' ', '0');
					tempString = R + rs + rt + rd + "00000" + OR;
					ins = tempString;
					break;
				case "nor":
					rd = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[1]))).replace(' ', '0');
					rs = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[2]))).replace(' ', '0');
					rt = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[3]))).replace(' ', '0');
					tempString = R + rs + rt + rd + "00000" + NOR;
					ins = tempString;
					break;
				case "xor":
					rd = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[1]))).replace(' ', '0');
					rs = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[2]))).replace(' ', '0');
					rt = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[3]))).replace(' ', '0');
					tempString = R + rs + rt + rd + "00000" + XOR;
					ins = tempString;
					break;
			}
		 return ins;
	 }
	 
	 
	 
	 public static void main(String[] args)
	 {
		 int PC = 0;
		 String ALUSrcA;
		 String IorD;
		 String ALUSrcB;
		 String ALUOp;
		 String PCSource;
		 Boolean stall = false;
		 
		 // Change these to HashMap for easy Key,Value search.
		 String[] IFtoID = new String[2];
		 String[] IDtoEXE = new String[10];
		 String[] EXEtoMEM = new String[10];
		 String[] MEMtoWB = new String[10];
		 
		Scanner kbd = new Scanner(System.in);
		boolean running = true;
		int cycles = 0;
		int i = 0;
		 
		try {
			insArray = loadFile(filepath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (EOFException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		///////////////
		// Main Loop //
		///////////////
		
		int count = 0;
		while(running)
		{
			
			cycles = kbd.nextInt();
			if(i + count <= insArray.size())
			{
				for(i = 0; i < cycles; i++)
				{
					System.out.println("");
					System.out.println("Cycle: " + count);
					// IF Stage
					if(insArray.size() > count)
					{
						ALUSrcA = "0";
						IorD = "0";
						ALUSrcB = "01";
						ALUOp = "00";
						PCSource = "00";
						PC = PCPointer;
						
						System.out.println("");
						System.out.println("  IFtoID REG  ");
						System.out.println("==============");
						System.out.println("Instruction Fetched: " + insArray.get(count) + " from 0x" + Integer.toHexString(PC));

					}
					else
					{
						IFtoID[0] = "";
					}
					
					// ID Stage
					if(count > 0)
					{
						String s = IFtoID[0];
						String decodedIns = inputToBinary(s);
						System.out.println("");
						System.out.println("  IDtoEX REG  ");
						System.out.println("==============");
						System.out.println("Instruction Decoded: " + decodedIns);
					}
					else
					{
						
					}
					// EX Stage
					if(count > 1)
					{
						
						System.out.println("");
						System.out.println("  EXtoME REG  ");
						System.out.println("==============");
						//System.out.println("Instruction Decoded: " + decodedIns);
					}
					else
					{
						
					}
					// MEM Stage
					if(count > 2)
					{
						
						System.out.println("");
						System.out.println("  MEtoWB REG  ");
						System.out.println("==============");
						//System.out.println("Instruction Decoded: " + decodedIns);
					}
					else
					{
						
					}
					// WB Stage
					if(count > 3)
					{
						
						System.out.println("");
						System.out.println("  WB STAGE  ");
						System.out.println("============");
						//System.out.println("Instruction Decoded: " + decodedIns);
					}
					else
					{
						
					}
					
					//Pipeline Register update
					PC+=4;
					IFtoID[0] = insArray.get(count);
					System.out.println(IFtoID[0]);
					IFtoID[1] = ""+PC+"";
					
					
					count++;
				}
			}
			else
			{
				System.out.println("==================");
				System.out.println("= End of Program =");
				System.out.println("==================");
			}
		}
	}
	 
}
