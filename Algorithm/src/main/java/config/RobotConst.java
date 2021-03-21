package config;

public class RobotConst {
	public static final int SENSOR_SR_LOW = 1;
	public static final int SENSOR_SR_HIGH = 1;
	public static final int SENSOR_LR_LOW = 3;
	public static final int SENSOR_LR_HIGH = 3;
	public static final int START_ROW = 1;
	public static final int START_COL = 1;
	public static final int GOAL_ROW = 18;
	public static final int GOAL_COL = 13;
	public static final int M_COST = 10;
	public static final int T_COST = 20;
	public static final int INF_COST = 2056;
	public static final int SPEED = 10;
	public static final int TIME_LIMIT = 330; //360;
	public static final int COVERAGE_LIMIT = 300;
	public static final DIRECTION START_DIR = DIRECTION.NORTH;

	// TODO: change back for EXP
	public static final int FORWARDLIMIT = 1;

	public enum DIRECTION {
		NORTH, EAST, SOUTH, WEST;

		public static DIRECTION getNextClk(DIRECTION curDir) {
			return values()[(curDir.ordinal() + 1) % values().length];
		}

		public static DIRECTION getNextAntiClk(DIRECTION curDir) {
			return values()[(curDir.ordinal() + values().length - 1) % values().length];
		}

		public static char getD(DIRECTION d) {
			switch (d) {
				case NORTH:
					return 'N';
				case EAST:
					return 'E';
				case SOUTH:
					return 'S';
				case WEST:
					return 'W';
				default:
					return 'X';
			}
		}
	}

	public enum MOVE {
		FORWARD, TURN_RIGHT, TURN_LEFT, CALIBRATE;

		public static char getMove(MOVE m) {
			switch (m) {
				case FORWARD:
					return 'F';
				case TURN_RIGHT:
					return 'R';
				case TURN_LEFT:
					return 'L';
				case CALIBRATE:
					return 'C';
				default:
					return 'E';
			}
		}
	}
}
