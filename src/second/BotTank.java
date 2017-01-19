package second;
import battlecode.common.*;

public class BotTank extends Globals {
	public static void loop() throws GameActionException {
		while (true) {
            try {
            	locateArchon();
            	
            	Micro.SoldierFight();
               
                Clock.yield();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}
}
