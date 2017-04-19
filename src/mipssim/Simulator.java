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
	static String filepath = "src/mipssim/tv1.txt";
	
	//Initial parameters
	static int numRegisters 	= 	32;
	int maxNumIns 				= 	256;
	int	maxLineLength			=	256;
	static int PCPointer 		= 	16384;
	int memDataStart 			= 	4096;
	int memDataSize 			=	4096;
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
			currentString = currentString.replace("0x", "");
			currentString = currentString.replace("(", " ");
			currentString = currentString.replace(")", "");

			System.out.println(currentString);

			tempArray = currentString.split("\\s+");
			switch (tempArray[0])
			{
			
				// TODO: Make sure immediate is correct, and make sure formatting take "()" into consideration
				case "lw":
					tempString = LW;
					rs = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[3]))).replace(' ', '0');
					rt = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[1]))).replace(' ', '0');
					immediate = String.format("%16s", Integer.toBinaryString(Integer.parseInt(tempArray[2]))).replace(' ', '0');
					ins = LW + rs + rt + immediate;
					break;
					
				// TODO: Make sure immediate is correct, and make sure formatting takes "()" into consideration
				case "sw":
					tempString = SW;
					rs = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[3]))).replace(' ', '0');
					rt = String.format("%5s", Integer.toBinaryString(Integer.parseInt(tempArray[1]))).replace(' ', '0');
					immediate = String.format("%16s", Integer.toBinaryString(Integer.parseInt(tempArray[2]))).replace(' ', '0');
					ins = SW + rs + rt + immediate;
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
					immediate = String.format("%16s", Integer.toBinaryString((int)Long.parseLong(tempArray[3], 16))).replace(' ', '0');
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
		 String regOP = "";
		 String decodedIns = "";
		 String op = "";
		 String rs = "";
		 String rt = "";
		 String rd = "";
		 String imm = "";
		 String shift = "";
		 String func = "";
		 int a = 0;
		 int b = 0;
		 int c = 0;
		 
		 Boolean stall = false;
		
		 
		 // Change these to HashMap for easy Key,Value search.
		 String[] IFtoID = new String[3]; 		// 0: Instruction 1: Binary of Instruction 2: PC+4
		 String[] IDtoEXE = new String[6];		// 0: Instruction 1: opcode 2: rd(dest) 3: A 4: B or imm 5: PC+4
		 String[] EXEtoMEM = new String[10];	// 0: Instruction
		 String[] MEMtoWB = new String[10];		// 0: Instruction
		 
		 IFtoID[0] = "";
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
			// && (IDtoEXE[0] != null && EXEtoMEM[0] != null && MEMtoWB[0] != null)
			if(i + count <= insArray.size())
			{
				for(i = 0; i < cycles; i++)
				{
					System.out.println("***********");
					System.out.println("Cycle: " + (count+1));
					System.out.println("***********");
					// IF Stage
					if(insArray.size() > count && IFtoID[0] != null)
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
						for(int n =0; n<IFtoID.length; n++)
						{
							IFtoID[n] = null;
						}
					}
					
					// ID Stage
					//TODO: Compute target address, and stall pipeline accordingly for branch
					if((count > 0) && (IFtoID[1] != null))
					{
						op = IFtoID[1].substring(0, 6);
						// R-Type
						if(op.equals(_R))
						{
							regOP = _R;
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
							imm = "";
						}
						else if (op.equals(LW))
						{
							shift="";
							func = "";
							RegDst = 0;
							ALUSrc = 1;
							MemtoReg = 1;
							RegWrite = 1;
							MemRead = 1;
							MemWrite = 0;
							Branch = 0;
							ALUOp = 0;
							rs = IFtoID[1].substring(6, 11);
							rt = IFtoID[1].substring(11,16);
							imm = IFtoID[1].substring(16, 32);
							int temp1 = Integer.parseInt(rs, 2);
							a = regFile[temp1];
							rd = rt;
							
						}
						else if (op.equals(SW))
						{
							shift = "";
							func = "";
							RegDst = 0;
							ALUSrc = 1;
							MemtoReg = 0;
							RegWrite = 0;
							MemRead = 0;
							MemWrite = 1;
							Branch = 0;
							ALUOp = 0;
							rs = IFtoID[1].substring(6, 11);
							rt = IFtoID[1].substring(11,16);
							imm = IFtoID[1].substring(16, 32);
							int temp1 = Integer.parseInt(rs, 2);
							a = regFile[temp1];
							rd = rt;
						}
						else if (op.equals(BNE) || op.equals(BEQ))
						{
							rd = "";
							shift = "";
							func = "";
							RegDst = 0;
							ALUSrc = 0;
							MemtoReg = 0;
							RegWrite = 1;
							MemRead = 0;
							MemWrite = 0;
							Branch = 1;
							ALUOp = 01;
							imm = IFtoID[1].substring(6, 32);
						}
						else if(op.equals(ADDI))
						{
							shift = "";
							func = "";
							RegDst = 0;
							ALUSrc = 1;
							MemtoReg = 0;
							RegWrite = 1;
							MemRead = 0;
							MemWrite = 0;
							Branch = 0;
							ALUOp = 00;
							rs = IFtoID[1].substring(6, 11);
							rt = IFtoID[1].substring(11, 16);
							imm = IFtoID[1].substring(16, 32);
							int temp1 = Integer.parseInt(rs, 2);
							a = regFile[temp1];
							rd = rt;
						}
						
						System.out.println("");
						System.out.println("  IDtoEX REG  ");
						System.out.println("==============");
						System.out.println("Instruction Decoded: " + insArray.get(count-1));
						System.out.println("Value at rs: " + a);
						System.out.println("Value at rt: " + b);
						System.out.println("Destination Register: " + rd);
						System.out.println("Immediate: " + imm);
						System.out.println("Shift Ammount: " + shift);
						System.out.println("Function Code: " + func);
						
					}
					else
					{
						System.out.println("");
						System.out.println("  IDtoEX REG  ");
						System.out.println("==============");
						System.out.println("NOP");
						for(int n =0; n<IDtoEXE.length; n++)
						{
							IDtoEXE[n] = null;
						}
					}
					
					// EX Stage
					//TODO: Implement this
					if(count > 1 && IDtoEXE[0] != null)
					{
						
						
						System.out.println("");
						System.out.println("  EXtoME REG  ");
						System.out.println("==============");
						System.out.println("Result: ");
						System.out.println("Value to be stored: " );
						System.out.println("Register to be loaded from: ");
					}
					else
					{
						EXEtoMEM[0] = null;
						System.out.println("");
						System.out.println("  EXEtoMEM REG  ");
						System.out.println("=================");
						System.out.println("NOP");
						for(int n =0; n<EXEtoMEM.length; n++)
						{
							EXEtoMEM[n] = null;
						}
					}
					
					// MEM Stage 
					// TODO: Implement this
					if(count > 2 && EXEtoMEM[0] != null)
					{
						
						System.out.println("");
						System.out.println("  MEMtoWB REG  ");
						System.out.println("===============");
						//System.out.println("Instruction Decoded: " + decodedIns);
					}
					else
					{
						MEMtoWB[0] = null;
						System.out.println("");
						System.out.println("  MEMtoWB REG  ");
						System.out.println("===============");
						System.out.println("NOP");
						for(int n =0; n<MEMtoWB.length; n++)
						{
							MEMtoWB[n] = null;
						}
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
						System.out.println("");
						System.out.println("  WB STAGE  ");
						System.out.println("============");
						System.out.println("NOP");
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
					if(regOP.equals(_R))
					{
						IDtoEXE[0] = IFtoID[0]; //Instruction
						IDtoEXE[1] = op; // op-code for instruction
						IDtoEXE[2] = rd; // Destination register for add
						IDtoEXE[3] = Integer.toBinaryString(a); // first operand
						IDtoEXE[4] = Integer.toBinaryString(b); // second operand
						IDtoEXE[5] = IFtoID[2]; //PC+4
					}
					else if (regOP.equals(ADDI))
					{
						IDtoEXE[0] = IFtoID[0]; // Instruction
						IDtoEXE[1] = op; // opcode of instruction
						IDtoEXE[2] = rd; // Destination register number of Add
						IDtoEXE[3] = Integer.toBinaryString(a); // first operand
						IDtoEXE[4] = imm; // Immediate to be added
						IDtoEXE[5] = IFtoID[2]; // PC+4
					}
					else if(regOP.equals(LW) || regOP.equals(SW))
					{
						IDtoEXE[0] = IFtoID[0]; // Instruction
						IDtoEXE[1] = op; // opcode of instruction
						IDtoEXE[2] = rd; // Destination of loaded value
						IDtoEXE[3] = Integer.toBinaryString(a); // Register to load from/store to
						IDtoEXE[4] = imm; // Immediate for load/store
						IDtoEXE[5] = IFtoID[2]; // PC+4
					}
					else if(regOP.equals(BNE) || regOP.equals(BEQ))
					{
						IDtoEXE[0] = IFtoID[0]; // Instruction
						IDtoEXE[1] = op; // opcode
						IDtoEXE[2] = ""; // No rd for branch
						IDtoEXE[3] = ""; // No a for branch
						IDtoEXE[4] = imm; // address for branch
						IDtoEXE[5] = IFtoID[2]; //PC+4
					}
					
					
					// Pipeline Register IFtoID Update
					PC+=4;
					IFtoID[0] = insArray.get(count);
					IFtoID[1] = decodedIns;
					IFtoID[2] = Integer.toString(PC);
					
					
					
					
					count++;
					System.out.println("#############################################");
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
