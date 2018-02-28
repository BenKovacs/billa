package billabong.ai;

public class Diffuser {
	private static Diffuser instance;
	public double[][] weights;
	
	
	private Diffuser()
	{
		weights = new double[16][14];
		
		
		int cx = 8;
		int cy = 7;
		for(int y = 7; y<14; y++)
		{
			for(int x = 8; x<16; x++)
			{	
				double dx = x+0.5d;
				double dy = y+0.5d;
				
				double diffx = dx - cx;
				double diffy = dy - cy;
				double ttt = diffx / diffy;
				
				double angle = Math.toDegrees(Math.atan(ttt));
				if (ttt > 0){
					angle = 360 - angle;
				}else{
					angle = 180 + angle;
				}
				
				weights[x][y] = angle;
				weights[15 -x][y] = 360-angle;
			}
		}
		
		
		for(int y = 0; y<7; y++)
		{
			for(int x = 8; x<16; x++)
			{	
				double dx = x+0.5d;
				double dy = y+0.5d;
				
				double diffx = dx - cx;
				double diffy = dy - cy;
				double ttt = diffx / diffy;
				
				double angle = Math.toDegrees(Math.atan(ttt));
				if (ttt > 0){
					angle = 360 - angle;
				}else{
					angle = 180 + angle;
				}
				
				weights[x][y] = 360-angle;
				weights[15 -x][y] = angle;
			}
		}

		for(int y = 6; y<8; y++)
		{
			for(int x = 6; x<10; x++)
			{
				weights[x][y] = -999999999;
			}
		}
		
	}
	

	public double getWeight(int x, int y)
	{
		return weights[x][y];
	}


	public static Diffuser getInstance() {
		if (instance == null){
			instance = new Diffuser();
		}
		return instance;
	}

}
