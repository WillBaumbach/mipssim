package mipssim;

public class IFStage extends Stage 
{
	int _PC;
	int _tempPC;
	private int _PCWrite;
	private int _PCSrc;
	
	public IFStage(Simulator sim)
	{
		super(sim);
		_PC = 0;
		_tempPC = 0;
		_PCWrite = 1;
	}
	
	public void setPCWrite(int i)
	{
		this._PCWrite = i;
	}
	
	public int getPCSource()
	{
		return _PCSrc;
	}
	
	@Override
	public void run()
	{

	}
}
