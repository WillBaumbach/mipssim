package mipssim;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class Operator 
{
	private Simulator sim;
	private Interpreter interpreter;
	
	public static void main(String[] args) throws Exception
	{
		
	}
	
	public Operator() throws FileNotFoundException
	{
		sim = new Simulator();
		interpreter = new Interpreter();
	}
	
	public Simulator getSimulator()
	{
		return sim;
	}
	public Interpreter getInterpreter()
	{
		return interpreter;
	}
}
