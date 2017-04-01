package mipssim;

public abstract class Stage 
{
	protected Simulator sim;
	
	public Stage(Simulator sim)
	{
		this.sim = sim;
	}
	
	public abstract void run();
}
