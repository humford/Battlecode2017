package scoutmania;
import battlecode.common.*;

public class BuildQueue extends Globals{
	
	public static int POINTER_CHANNEL = 498;
	public static int LENGTH_CHANNEL = 499;
	
	public static int low_endpoint = 500;
	public static int up_endpoint = 600;
	
	
	private static int robotInt(RobotType type) throws GameActionException, Exception
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
	
	private static RobotType robotTypeFromInt(int type) throws Exception
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
	
	private static int getPointer() throws GameActionException
	{
		return rc.readBroadcast(POINTER_CHANNEL);
	}
	
	public static int getLength() throws GameActionException
	{
		return rc.readBroadcast(LENGTH_CHANNEL);
	}
	
	private static void increment() throws GameActionException
	{
		int length = rc.readBroadcast(LENGTH_CHANNEL);
		rc.broadcast(LENGTH_CHANNEL, length + 1);
	}
	
	private static void decrement() throws GameActionException
	{
		int pointer = rc.readBroadcast(POINTER_CHANNEL);
		rc.broadcast(POINTER_CHANNEL, pointer + 1);
		int length = rc.readBroadcast(LENGTH_CHANNEL);
		rc.broadcast(LENGTH_CHANNEL, length - 1);
	}
	
	
	
	public static void enqueue(RobotType robot)
	{
		try{
			if(getLength() + 1 >= (up_endpoint - low_endpoint)) throw new Exception("MAX LENGTH EXCEEDED");
			
			int channel = (getPointer() + getLength()) % (up_endpoint - low_endpoint) + low_endpoint;
			rc.broadcast(channel, robotInt(robot));
			
			increment();
		}
		catch(Exception e)
		{
			System.out.println("BAD QUEUE");
			e.printStackTrace();
		}
	}
	
	public static RobotType dequeue()
	{
		try{
			if(getLength() <= 0) throw new Exception("MIN LENGTH EXCEEDED");
			int typeInt = rc.readBroadcast(getPointer());
			decrement();
			return robotTypeFromInt(typeInt);
		}
		catch(Exception e)
		{
			System.out.println("BAD QUEUE");
			e.printStackTrace();
			return RobotType.ARCHON;
		}
	}
	
	public static RobotType peak()
	{
		try{
			if(getLength() <= 0) throw new Exception("MIN LENGTH EXCEEDED");
			int typeInt = rc.readBroadcast(getPointer());
			return robotTypeFromInt(typeInt);
		}
		catch(Exception e)
		{
			System.out.println("BAD QUEUE");
			e.printStackTrace();
			return RobotType.ARCHON;
		}
	}
	
	public static void printQueue()
	{
		try{
			for(int i = getPointer(); i < getPointer() + getLength(); i++)
			{
				System.out.print(rc.readBroadcast(i));
				System.out.println("");
			}
		}
		
		catch(Exception e)
		{
			System.out.println("BAD QUEUE");
			e.printStackTrace();
		}
	}
}
