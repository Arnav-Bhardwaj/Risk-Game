import com.java.controller.gameplay.GameplayTestSuite;
import com.java.controller.map.MapTestSuite;
import com.java.controller.startup.StartUpPhaseTestSuite;
import com.java.model.map.GameMapTestSuite;
import com.java.model.player.PlayerTestSuite;
import com.java.model.player.RandomModeTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)

@SuiteClasses({ GameplayTestSuite.class, MapTestSuite.class, StartUpPhaseTestSuite.class, GameMapTestSuite.class,
		PlayerTestSuite.class})

/**
 * This class is the suit to run the test cases of every test class.
 * 
 * @author Arnav Bhardwaj
 * @author Karan Dhingra
 * @author Ghalia Elkerdi
 * @author Sahil Singh Sodhi
 * @author Cristian Rodriguez
 * @version 2.0.0
 */
public class Junit4TestSuite {
}
