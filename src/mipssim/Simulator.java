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
	static int numRegisters 	= 	32;
	int maxNumIns 				= 	256;
	int maxLabelLength			=	32;
	int maxNumLabels			=	32;
	int	maxLineLength			=	256;
	static int PCPointer 		= 	16384;
	int memDataStart 			= 	0x1000;
	int memDataSize 			=	0x1000;
	int regGP					=	28;
	int regSP					=	29;
	int NOP 					= 	-1;
	
	// OpCode + Function
	static String _R = 		"000000";
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
	static int RegDst;
	static int ALUSrc;
	static int MemtoReg;
	static int RegWrite;
	static int MemRead;
	static int MemWrite;
	static int Branch;
	static int ALUOp;
	
	// Other variables
	static int rs = -1;
	static int rt = -1;
	static int rd = -1;
	int i = 0;
	int addr = 0;
	static int[] regFile = new int[numRegisters];
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
			
				// TODO: Make sure immediate is correct, and make sure formatting take "()" into consideration
				case "lw":
					tempString = LW;
					rs = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[2]))).replace(' ', '0');
					rt = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[1]))).replace(' ', '0');
					immediate = tempArray[3];
					ins = LW + rs + rt + immediate;
					break;
					
				// TODO: This
				case "sw":
					tempString = SW;
					break;
				case "add":
					rd = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[1]))).replace(' ', '0');
					rs = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[2]))).replace(' ', '0');
					rt = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[3]))).replace(' ', '0');
					tempString = _R + rs + rt + rd + "00000" + ADD;
					ins = tempString;
					break;
				case "sub":
					rd = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[1]))).replace(' ', '0');
					rs = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[2]))).replace(' ', '0');
					rt = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[3]))).replace(' ', '0');
					tempString = _R + rs + rt + rd + "00000" + SUB;
					ins = tempString;
					break;
				case "addi":
					rs = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[2]))).replace(' ', '0');
					rt = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[1]))).replace(' ', '0');
					immediate = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[3]))).replace(' ', '0');
					tempString = ADDI + rs + rt + immediate;
					ins = tempString;
					break;
					
					// TODO: Make sure immediate is correct
				case "bne":
					immediate = String.format("%26s", Integer.toBinaryString(Integer.parseInt(tempArray[3]))).replace(' ', '0');
					tempString = BNE + immediate;
					break;
					
					// TODO: Make sure immediate is correct
				case "beq":
					immediate = String.format("%26s", Integer.toBinaryString(Integer.parseInt(tempArray[3]))).replace(' ', '0');
					tempString = BEQ + immediate;
					break;
				case "and":
					rd = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[1]))).replace(' ', '0');
					rs = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[2]))).replace(' ', '0');
					rt = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[3]))).replace(' ', '0');
					tempString = _R + rs + rt + rd + "00000" + AND;
					ins = tempString;
					break;
				case "or":
					rd = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[1]))).replace(' ', '0');
					rs = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[2]))).replace(' ', '0');
					rt = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[3]))).replace(' ', '0');
					tempString = _R + rs + rt + rd + "00000" + OR;
					ins = tempString;
					break;
				case "nor":
					rd = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[1]))).replace(' ', '0');
					rs = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[2]))).replace(' ', '0');
					rt = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[3]))).replace(' ', '0');
					tempString = _R + rs + rt + rd + "00000" + NOR;
					ins = tempString;
					break;
				case "xor":
					rd = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[1]))).replace(' ', '0');
					rs = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[2]))).replace(' ', '0');
					rt = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[3]))).replace(' ', '0');
					tempString = _R + rs + rt + rd + "00000" + XOR;
					ins = tempString;
					break;
			}
		 return ins;
	 }
	 
	 
	 
	 public static void main(String[] args)
	 {
		 int PC = PCPointer;
		 String decodedIns = "";
		 String PCSource;
		 String op = "";
		 String rs = "";
		 String rt = "";
		 String rd = "";
		 String shift = "";
		 String func = "";
		 int a = 0;
		 int b = 0;
		 int c = 0;
		 
		 Boolean stall = false;
		
		 
		 // Change these to HashMap for easy Key,Value search.
		 String[] IFtoID = new String[3]; 		// 0: Instruction 1: Binary of Instruction 2: PC+4
		 String[] IDtoEXE = new String[10];		// 0: Instruction 1: rd 2: A 3: B 4: PC+4
		 String[] EXEtoMEM = new String[10];	// 0: Instruction
		 String[] MEMtoWB = new String[10];		// 0: Instruction
		 
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
			if(i + count <= insArray.size() && IDtoEXE[0] != null && EXEtoMEM[0] != null && MEMtoWB[0] != null)
			{
				for(i = 0; i < cycles; i++)
				{
					System.out.println("");
					System.out.println("Cycle: " + count);
					
					// IF Stage
					if(insArray.size() > count)
					{
						String s = insArray.get(count);
						decodedIns = inputToBinary(s);
						
						System.out.println("");
						System.out.println("  IFtoID REG  ");
						System.out.println("==============");
						System.out.println("Instruction Fetched: " + insArray.get(count) + " from 0x" + Integer.toHexString(PC));
						System.out.println("PC + 4 = 0x" + Integer.toHexString(PC+4));
						System.out.println("Instruction: " + decodedIns);
					}
					else
					{
						IFtoID[0] = null;
					}
					
					// ID Stage
					// TODO: Implement other instruction types
					//		- Add control signals
					if(count > 0 && IFtoID[0] != null)
					{
						op = IFtoID[1].substring(0, 6);
						System.out.println(op);
						// R-Type
						if(op.equals(_R))
						{
							RegDst = 1;
							ALUSrc = 0;
							MemtoReg = 0;
							RegWrite = 1;
							MemRead = 0;
							MemWrite = 0;
							Branch = 0;
							ALUOp = 10;
							rs = IFtoID[1].substring(6, 11);
							rt = IFtoID[1].substring(11, 16);
							rd = IFtoID[1].substring(16, 21);
							shift = IFtoID[1].substring(21, 26);
							func = IFtoID[1].substring(26, 32);
							int temp1 = Integer.parseInt(rs, 2);
							int temp2 = Integer.parseInt(rt, 2);
							a = regFile[temp1];
							b = regFile[temp2];
						}
						else if (op.equals(LW))
						{
							RegDst = 0;
							ALUSrc = 1;
							MemtoReg = 1;
							RegWrite = 1;
							MemRead = 1;
							MemWrite = 0;
							Branch = 0;
							ALUOp = 0;
						}
						else if (op.equals(SW))
						{
							RegDst = 0;
							ALUSrc = 1;
							MemtoReg = 0;
							RegWrite = 0;
							MemRead = 0;
							MemWrite = 1;
							Branch = 0;
							ALUOp = 0;
						}
						else if (op.equals(BNE) || op.equals(BEQ))
						{
							RegDst = 0;
							ALUSrc = 0;
							MemtoReg = 0;
							RegWrite = 1;
							MemRead = 0;
							MemWrite = 0;
							Branch = 1;
							ALUOp = 01;
						}
						
						System.out.println("");
						System.out.println("  IDtoEX REG  ");
						System.out.println("==============");
						System.out.println("Instruction Decoded: " + insArray.get(count-1));
						System.out.println("Value at rs: " + a);
						System.out.println("Value at rt: " + b);
						System.out.println("rd: " + rd);
						System.out.println("shift: " + shift);
						System.out.println("func: " + func);
						
					}
					else
					{
						System.out.println("");
						System.out.println("  IDtoEX REG  ");
						System.out.println("==============");
						System.out.println("Instruction Decoded: NOP");
						IDtoEXE[0] = null;
					}
					// EX Stage
					//TODO: Implement this
					if(count > 1 && IDtoEXE[0] != null)
					{
						System.out.println("");
						System.out.println("  EXtoME REG  ");
						System.out.println("==============");
						//System.out.println("Instruction Decoded: " + decodedIns);
					}
					else
					{
						EXEtoMEM[0] = null;
					}
					// MEM Stage 
					// TODO: Implement this
					if(count > 2 && EXEtoMEM[0] != null)
					{
						
						System.out.println("");
						System.out.println("  MEtoWB REG  ");
						System.out.println("==============");
						//System.out.println("Instruction Decoded: " + decodedIns);
					}
					else
					{
						MEMtoWB[0] = null;
						System.out.println("");
						System.out.println("  MEtoWB REG  ");
						System.out.println("==============");
						System.out.println("NOP");
					}
					// WB Stage
					// TODO: Implement this
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
					
					//Pipeline Register MEMtoWB Update
					// TODO: Add all register info
					MEMtoWB[0] = EXEtoMEM[0];
					
					
					// Pipeline Register EXEtoMEM Update
					// TODO: Add all pipeline info
					EXEtoMEM[0] = IDtoEXE[0];
					
					
					// Pipeline Register IDtoEXE Update
					// TODO: Check if all this is correct
					//		- Add control signals
					IDtoEXE[0] = IFtoID[0];
					IDtoEXE[1] = rd;
					IDtoEXE[2] = Integer.toBinaryString(a);
					IDtoEXE[3] = Integer.toBinaryString(b);
					IDtoEXE[4] = IFtoID[2];
					
					
					
					// Pipeline Register IFtoID Update
					PC+=4;
					IFtoID[0] = insArray.get(count);
					//System.out.println("IF_ID = " + IFtoID[0]);
					IFtoID[1] = decodedIns;
					IFtoID[2] = Integer.toString(PC);
					
					
					
					
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
