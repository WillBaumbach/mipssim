package mipssim;

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
	static int memDataSize 			=	4096;
	int regGP					=	28;
	int regSP					=	29;

	
	// OpCode + Function
	static final String NOP = 	"000000";
	static final String _R 	= 	"000000";
	static final String LW 	= 	"100011";
	static final String SW 	= 	"101011";
	static final String ADD = 	"100000";
	static final String SUB = 	"100010";
	static final String ADDI = 	"001000";
	static final String BNE = 	"000101";
	static final String BEQ = 	"000100";
	static final String AND = 	"100100";
	static final String OR 	=	 "100101";
	static final String NOR = 	"100111";
	static final String XOR = 	"100110";
	
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
	static int[] dataMem = new int[memDataSize/4];
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
					ins = tempString;
					break;
					
					// TODO: Make sure immediate is correct
				case "beq":
					immediate = String.format("%26s", Integer.toBinaryString(Integer.parseInt(tempArray[3]))).replace(' ', '0');
					tempString = BEQ + immediate;
					ins = tempString;
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
		 String s = "";
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
		 
		 int temp = 0;
		 String answer = "N/A";
		 String target = "N/A";
		 String store = "N/A";
		 String load = "N/A";
		 int a = 0;
		 int b = 0;
		 
		 String memOP = "";
		 String result = "";
		 int iresult = 0;
		 int ilocation = 0;
		 String location = "";
		 
		 Boolean stall = false;
		
		 
		 // Change these to HashMap for easy Key,Value search.
		 String[] IFtoID = new String[3]; 		// 0: Instruction 1: Binary of Instruction 2: PC+4
		 String[] IDtoEXE = new String[7];		// 0: Instruction 1: opcode 2: rd(dest) 3: A 4: B or imm 5: PC+4 6: function for R type
		 String[] EXEtoMEM = new String[10];	// 0: Instruction
		 String[] MEMtoWB = new String[10];		// 0: Instruction
		 
		 IFtoID[0] = "x";
		 @SuppressWarnings("resource")
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
			//System.out.println(IFtoID[0]);
			//System.out.println(IDtoEXE[0]);
			//System.out.println(EXEtoMEM[0]);
			//System.out.println(MEMtoWB[0]);
			
			
			if(i + count <= (insArray.size() + 5) && (!(IFtoID[0].equals("NOP")) || !(IDtoEXE[0].equals("NOP")) || !(EXEtoMEM[0].equals("NOP")) || !(MEMtoWB[0].equals("NOP"))))
			{
				cycles = kbd.nextInt();
				for(i = 0; i < cycles; i++)
				{
					System.out.println("***********");
					System.out.println(" Cycle: " + (count+1));
					System.out.println("***********");
					
					s = "";
					decodedIns = "";
					
					// IF Stage
					if(insArray.size() > count && IFtoID[0] != null && !insArray.get(count).equals("nop"))
					{
						s = insArray.get(count);
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
						s = "NOP";
						decodedIns = null;
						
						System.out.println("");
						System.out.println("  IFtoID REG  ");
						System.out.println("==============");
						System.out.println("NOP");
					}
					
					// ID Stage
					//TODO: Compute target address, and stall pipeline accordingly for branch
					// - Need to determine how branches work
					a = 0;
					b = 0;
					rd = "";
					imm = "";
					shift = "";
					func = "";
					op = "";
					if((count > 0) && !(IFtoID[0].equals("NOP")))
					{
						op = IFtoID[1].substring(0, 6);
						// System.out.println(op);
						// R-Type
						if(op.equals(_R))
						{
							regOP = _R;
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
							regOP = LW;
							shift="";
							func = "";
							rs = IFtoID[1].substring(6, 11);
							rt = IFtoID[1].substring(11,16);
							imm = IFtoID[1].substring(16, 32);
							int temp1 = Integer.parseInt(rs, 2);
							a = temp1 * 32 - 1;
							rd = rt;
							
						}
						else if (op.equals(SW))
						{
							regOP = SW;
							shift = "";
							func = "";
							rs = IFtoID[1].substring(6, 11);
							rt = IFtoID[1].substring(11,16);
							imm = IFtoID[1].substring(16, 32);
							int temp1 = Integer.parseInt(rs, 2);
							a = temp1 * 32 - 1;
							rd = rt;
						}
						else if (op.equals(BNE) || op.equals(BEQ))
						{
							regOP = BNE;
							rd = "";
							shift = "";
							func = "";
							imm = IFtoID[1].substring(16, 32);
							imm = String.format("%32s", Integer.toBinaryString(Integer.parseInt(imm, 2))).replace(' ', '0');
						}
						else if(op.equals(ADDI))
						{
							regOP = ADDI;
							shift = "";
							func = "";
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
						System.out.println("Instruction Decoded: " + IFtoID[0]);
						System.out.println("Value at rs: " + a);
						System.out.println("Value at rt: " + b);
						System.out.println("Destination Register: " + rd);
						System.out.println("Immediate: " + imm);
						System.out.println("Shift Ammount: " + shift);
						System.out.println("Function Code: " + func);
						// System.out.println("op: " + op);
					}
					else
					{
						regOP = "";
						rs = null;
						rt = null;
						a = 0;
						b = 0;
						rd = null;
						shift = null;
						func = null;
						
						System.out.println("");
						System.out.println("  IDtoEX REG  ");
						System.out.println("==============");
						System.out.println("NOP");
					}
					
					// EX Stage
					//TODO: Implement this
					// - I think R type is all finished
					// - I think ADDI is all finished
					// - I think LW is all finished 
					// - Need to determine how branches work
					if(count > 1 && !(IDtoEXE[0].equals("NOP")))
					{
						String exeOP = IDtoEXE[1]; 
						String exeFunc = IDtoEXE[6];
						temp = 0;
						answer = "N/A";
						target = "N/A";
						load = "N/A";
						store = "N/A";
						if(exeOP.equals(_R))
						{
							switch (exeFunc)
							{
							case ADD:
								temp = Integer.parseInt(IDtoEXE[3], 2) + Integer.parseInt(IDtoEXE[4], 2);
								answer = Integer.toBinaryString(temp);
								break;
							case SUB:
								temp = Integer.parseInt(IDtoEXE[3], 2) - Integer.parseInt(IDtoEXE[4], 2);
								answer = Integer.toBinaryString(temp);
								break;
							case AND:
								temp = Integer.parseInt(IDtoEXE[3], 2) & Integer.parseInt(IDtoEXE[4], 2);
								answer = Integer.toBinaryString(temp);
								break;
							case OR:
								temp = Integer.parseInt(IDtoEXE[3], 2) | Integer.parseInt(IDtoEXE[4], 2);
								answer = Integer.toBinaryString(temp);
								break;
							case NOR:
								temp = ~(Integer.parseInt(IDtoEXE[3], 2) | Integer.parseInt(IDtoEXE[4], 2));
								answer = Integer.toBinaryString(temp);
								
								break;
							case XOR:
								temp = Integer.parseInt(IDtoEXE[3], 2) ^ Integer.parseInt(IDtoEXE[4], 2);
								answer = Integer.toBinaryString(temp);
								break;
							}
						}
						else if(exeOP.equals(ADDI))
						{
							temp = Integer.parseInt(IDtoEXE[3], 2) + Integer.parseInt(IDtoEXE[4], 2);
							answer = Integer.toBinaryString(temp);
						}
						else if(exeOP.equals(LW) || exeOP.equals(SW))
						{
							store = IDtoEXE[4];
							char sign = store.substring(0, 1).toCharArray()[0];
							store = String.format("%32s", store).replace(' ', sign);
							int t = Integer.parseInt(store, 2);
							int offset = Integer.parseInt(IDtoEXE[3], 2);
							System.out.println("k: " + offset);
							t = t+offset;
							if(exeOP.equals(LW))
							{
								load = Integer.toBinaryString(t);
								store = "N/A";
							}
							else
							{
								store = Integer.toBinaryString(dataMem[t]);
								load = "N/A";
							}
						}
						else if(exeOP.equals(BNE) || exeOP.equals(BEQ))
						{
							temp = Integer.parseInt(IDtoEXE[4],2) << 2;
							target = Integer.toBinaryString(temp);
							char sign = target.substring(0, 1).toCharArray()[0];
							target = String.format("%32s", target).replace(' ', sign);
						}
						
						System.out.println("");
						System.out.println("  EXtoME REG  ");
						System.out.println("==============");
						System.out.println("Instruction executed: " + IDtoEXE[0]);
						System.out.println("Result: " + answer);
						System.out.println("Value to be stored: " + store);
						System.out.println("Register to be loaded from: " + load);
						System.out.println("Branch target: " + target);
					}
					else
					{
						System.out.println("");
						System.out.println("  EXEtoMEM REG  ");
						System.out.println("=================");
						System.out.println("NOP");
					}
					
					// MEM Stage 
					if(count > 2 && !(EXEtoMEM[0].equals("NOP")))
					{
						ilocation = 0;
						iresult = 0;
						memOP = EXEtoMEM[1];
						if(memOP.equals(_R))
						{
							// Do nothing
						}
						else if(memOP.equals(LW))
						{
							iresult = dataMem[Integer.parseInt(EXEtoMEM[7], 2)];
							result = Integer.toBinaryString(iresult);
						}
						else if(memOP.equals(SW))
						{
							ilocation = dataMem[Integer.parseInt(EXEtoMEM[6], 2)];
							iresult = regFile[Integer.parseInt(EXEtoMEM[2], 2)];
							dataMem[ilocation] = iresult;
						}
						else if(memOP.equals(BNE) || memOP.equals(BEQ))
						{
							// Not sure
						}
						else if(memOP.equals(ADDI))
						{
							// Do nothing?
						}
						
						System.out.println("");
						System.out.println("  MEMtoWB REG  ");
						System.out.println("===============");
						System.out.println("Instruction: " + EXEtoMEM[0]);
						System.out.println("Data Stored: " + Integer.toBinaryString(iresult));
						System.out.println("Location: " + Integer.toBinaryString(ilocation));
					}
					else
					{
						System.out.println("");
						System.out.println("  MEMtoWB REG  ");
						System.out.println("===============");
						System.out.println("NOP");
					}
					
					// WB Stage
					// TODO: Implement this
					if(count > 3 && !(MEMtoWB[0].equals("NOP")))
					{
						
						System.out.println("");
						System.out.println("  WB STAGE  ");
						System.out.println("============");
						System.out.println("Instruction: " + MEMtoWB[0]);
						System.out.println("Register Number: "  + MEMtoWB[2]);
						System.out.println("Value: " + MEMtoWB[3]);
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
					MEMtoWB[0] = EXEtoMEM[0]; // Instruction
					MEMtoWB[1] = EXEtoMEM[1]; // Op-Code
					MEMtoWB[5] = EXEtoMEM[5]; // PC+4
					if(memOP.equals(_R) || memOP.equals(ADDI))
					{
						MEMtoWB[2] = EXEtoMEM[2]; // rd (destination for write back)
						MEMtoWB[3] = EXEtoMEM[3]; // ALU result
					}
					else if(memOP.equals(LW))
					{
						MEMtoWB[2] = EXEtoMEM[2]; // destination for load
						MEMtoWB[3] = result; // result to load to reg file
					}
					else if(memOP.equals(BNE) || memOP.equals(BEQ))
					{
						// Nothing else I think
					}
					
					
					// Pipeline Register EXEtoMEM Update
					// TODO: Add all pipeline info
					EXEtoMEM[0] = IDtoEXE[0]; // Instruction
					EXEtoMEM[1] = IDtoEXE[1]; // OpCode
					EXEtoMEM[2] = IDtoEXE[2]; // rd
					EXEtoMEM[3] = answer; // ALU result
					EXEtoMEM[4] = target; // Branch target
					EXEtoMEM[5] = IDtoEXE[5]; // PC+4
					EXEtoMEM[6] = store; // value to store
					EXEtoMEM[7] = load; // load location
					

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
						IDtoEXE[6] = func;
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
					else
					{
						IDtoEXE[0] = "NOP";
					}
					
					// Pipeline Register IFtoID Update
					if(insArray.size() + 5 > count)
					{
						if(s.equals("NOP"))
						{
							IFtoID[0] = "NOP";
							IFtoID[1] = null;
							IFtoID[2] = null;
						}
						else
						{
							//System.out.println("s: " + s);
							//System.out.println("decodedIns: " + decodedIns);
							PC+=4;
							IFtoID[0] = s;
							IFtoID[1] = decodedIns;
							IFtoID[2] = Integer.toString(PC);
						}
					}
					count++;
					System.out.println("#############################################");
				}
			}
			else
			{
				System.out.println("==================");
				System.out.println("= End of Program =");
				System.out.println("==================");
				break;
			}
		}
	} 
}
