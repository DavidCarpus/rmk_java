package carpus.util;

public class DataPoint2d
{
    protected int x;
    protected int y;
    protected String description;
    protected String data;

    static int NUMERIC=1;
    static int TEXT=2;
    static int MEMO=3;
    static int DATE=4;
    static int INT=5;

    public int getX(){return x;}
    public int getY(){return y;}
    public String getDescription(){return description;}
    public String getData(){return data;}

    public DataPoint2d(){
	x = 0;
	y = 0;
    }
    public DataPoint2d( int xCoor, int yCoor, String pointDesc, String pointData){
	x = xCoor;
	y=yCoor;
	description=pointDesc;
	data=pointData;
    }

}
