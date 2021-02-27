package config;

import java.awt.*;

public class MapConst {
	public static final int NUM_ROWS = 20;
	public static final int NUM_COLS = 15;
	public static final int NUM_CELLS = NUM_ROWS * NUM_COLS;
	public static final int START_ROW = 1;
	public static final int START_COL = 1;
	public static final int GOAL_ROW = 18;
	public static final int GOAL_COL = 13;

	// Map Graphics Constants
	public static final int CELL_SIZE = 30;
	public static final int MAP_HEIGHT = CELL_SIZE * NUM_ROWS;
	public static final int BORDER = 2;
	public static final int X_OFFSET = 130;

	public static final int ROBOT_DIAMETER = 60;
	public static final int ROBOT_DIR_DIAMETER = 10;
	public static final int ROBOT_X_OFFSET = 10;
	public static final int ROBOT_Y_OFFSET = 20;

	public static final Color COLOR_START = Color.GREEN;
	public static final Color COLOR_GOAL = Color.ORANGE;
	public static final Color COLOR_UNEXPLORED = Color.GRAY;
	public static final Color COLOR_WAYPOINT = Color.PINK;
	public static final Color COLOR_FP = Color.CYAN;
	public static final Color COLOR_FREE = Color.WHITE;
	public static final Color COLOR_OBSTACLE = Color.BLACK;
	public static final Color COLOR_ROBOT = Color.BLUE;
	public static final Color COLOR_ROBOT_DIR = Color.WHITE;
}
