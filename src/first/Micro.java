package first;
import battlecode.common.*;
public class Micro extends Globals {
  static int CONDENSED =-1;//initially not condensed
  /*
  Condensed = 0->mediumCondensed
  Condensed = 1->veryCondensed
  */
  Team us = rc.getTeam();
  
  //First scout always faces outward, makes sure the penta shot does not hit the archons
  //
  public static void firstScoutShots(){}
  
  //Only attack while their obstacle is still there.  When enemy only needs one shot, flag it.  Select final two shots.
  public static void finishHim(){}
  
  //if two robots sense each other stop firing pentashots, start firing trishots
  public static void isCondensed(RobotController rc){
      RobotInfo[] near = rc.senseNearbyRobots();
      int counter = 0;
      for(RobotInfo x: near){
        if(x.team==us){
          counter++;
        }
      if(counter ==2){
        CONDENSED=0;
       }
      else if(counter >2 && CONDENSED=0){
      CONDENSED=-1;
      }
    }
    }
  //If three robots sense each other then stop firing trishots, start firing singleshots
  
  
  //use archon sensors how dense map is with trees
  

  
  
  
  
}
