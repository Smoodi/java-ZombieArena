package fox.smoodi;
import java.util.Random;

/*
 * Basic class providing some utilities needed.
 */

public class Util {

	private static Random random = new Random();
	
	/**
	 * Returns a random integer in range.
	 * @param min - The minimum number to get.
	 * @param max - The maximum number to get.
	 * @return Returns a random integer in the given range.
	 */
	public static int inRange(int min, int max) {
		return random.nextInt((max - min) + 1) + min;
	}

}
