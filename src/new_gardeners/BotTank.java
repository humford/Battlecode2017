package new_gardeners;
import battlecode.common.*;

public class BotTank extends Globals {
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
