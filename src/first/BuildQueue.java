package first;
import battlecode.common.*;

import scala.tools.nsc.settings.RC;

public class BuildQueue {
	
	private int pointer;
	private int length;
	
	private int low_endpoint;
	private int up_endpoint;
	
	BuildQueue(int low_limit, int up_limit)
	{
		low_endpoint = low_limit;
		up_endpoint = up_limit;
		pointer = low_endpoint;
		length = 0;
	}
	
	private int robotInt(RobotType type) throws GameActionException, Exception
	{
		switch (type) {
         case ARCHON:
             return 0;

         case GARDENER:
             return 1;

         case LUMBERJACK:
        	 return 2;

         case SCOUT:
        	 return 3;

         case SOLDIER:
             return 4;

         case TANK:
             return 5;

         default:
             throw new Exception("weird robot type " + type);
		}
	}
	
	private RobotType robotTypeFromInt(int type) throws GameActionException, Exception
	{
		switch (type) {
         case 0:
             return RobotType.ARCHON;

         case 1:
             return RobotType.GARDENER;

         case 2:
        	 return RobotType.LUMBERJACK;

         case 3:
        	 return RobotType.SCOUT;

         case 4:
             return RobotType.SOLDIER;

         case 5:
             return RobotType.TANK;

         default:
             throw new Exception("weird robot type " + type);
		}
	}
	
	public void enqueue(RobotController rc, RobotType robot) throws Exception
	{
		if(length + 1 >= (up_endpoint - low_endpoint)) throw new Exception("MAX LENGTH EXCEEDED");
		
		int channel = (pointer + length) % (up_endpoint - low_endpoint);
		try {
			rc.broadcast(channel, robotInt(robot));
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		length ++;
	}
	
	public RobotType dequeue(RobotController rc) throws Exception
	{
		if(length <= 0) throw new Exception("MIN LENGTH EXCEEDED");
		int typeInt = rc.readBroadcast(pointer);
		pointer++;
		length --;
		return robotTypeFromInt(typeInt);
	}
	
	public RobotType peak(RobotController rc) throws Exception
	{
		if(length <= 0) throw new Exception("MIN LENGTH EXCEEDED");
		int typeInt = rc.readBroadcast(pointer);
		return robotTypeFromInt(typeInt);
	}
}
