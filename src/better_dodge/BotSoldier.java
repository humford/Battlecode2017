package first;
import battlecode.common.*;

public class BotSoldier extends Globals {
	public static void loop() throws GameActionException {
		while (true) {
            try {
            	loop_common();
            	
            	Micro.SoldierFight();
                
                Clock.yield();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}
}
