package first;
import battlecode.common.*;
public class Micro extends Globals {
  
 
  
  //First scout always faces outward, makes sure the penta shot does not hit the archons
  //
  public static void firstScoutShots(){}
  
  //Only attack while their obstacle is still there.  When enemy only needs one shot, flag it.  Select final two shots.
  public static void finishHim(){}
  
  //if two robots sense each other stop firing pentashots, start firing trishots
  public static int isCondensed(){
	  RobotInfo[] near = rc.senseNearbyRobots(-1, myTeam);
	  int len = near.length;
	  
	  if(len < 2) return -1;
	  else if(len == 2) return 0;
	  else return 1;
   }
  
  public static void SoldierFight() throws GameActionException
  {
      Pathfinding.dodge();
      
      RobotInfo[] bots = rc.senseNearbyRobots();

      for (RobotInfo b : bots) {
          if (b.getTeam() != rc.getTeam()) {
              Direction towards = rc.getLocation().directionTo(b.getLocation());
              switch(Micro.isCondensed())
              {
              case -1:
              	rc.firePentadShot(towards);
              	break;
              case 0:
              	rc.fireTriadShot(towards);
              	break;
              case 1:
              	rc.fireSingleShot(towards);
              	break;
              	
              }
              break;
          }      
      }
      
      if (! rc.hasAttacked()) {
          for (RobotInfo b : bots) {
              if (b.getTeam() != myTeam) {
                  Direction chase = rc.getLocation().directionTo(b.getLocation());
                  Pathfinding.tryMove(chase);
                  break;
              }
          }
          if(!rc.hasMoved()) moveTwardArchon();
      }
  }
  //If three robots sense each other then stop firing trishots, start firing singleshots
  
  
  //use archon sensors how dense map is with trees
  

  
  
  
  
}
