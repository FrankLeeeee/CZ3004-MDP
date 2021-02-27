package map;

import config.MapConst;
import config.RobotConst;
import robot.Robot;

import javax.swing.*;
import java.awt.*;


public class Map extends JPanel {
	private final Cell[][] arena;
	private Robot robot;

	public Map(Robot robot) {
		this.arena = new Cell[MapConst.NUM_ROWS][MapConst.NUM_COLS];
		for (int r = 0; r < MapConst.NUM_ROWS; r++) {
			for (int c = 0; c < MapConst.NUM_COLS; c++) {
				this.arena[r][c] = new Cell(r, c);

				// default cells along the boundaries of arena as Virtual Walls
				if (inBoundary(r, c)) {
					this.arena[r][c].setVirtualWall(true);
				}

				// default cells in start zone and goal zone as Explored
				if (inStartZone(r, c) || inGoalZone(r, c)) {
					this.arena[r][c].setExplored(true);
				}
			}
		}

		this.robot = robot;
	}

	public Cell[][] getArena() {
		return this.arena;
	}

	public Robot getRobot() {
		return this.robot;
	}

	public void setRobot(Robot robot) {
		this.robot = robot;
	}

	public boolean inBoundary(int r, int c) {
		return r == 0 || c == 0 || r == MapConst.NUM_ROWS - 1 || c == MapConst.NUM_COLS - 1;
	}

	public boolean inStartZone(int r, int c) {
		return r >= MapConst.START_ROW - 1 && r <= MapConst.START_ROW + 1 && c >= MapConst.START_COL - 1 && c <= MapConst.START_COL + 1;
	}

	public boolean inGoalZone(int r, int c) {
		return r >= MapConst.GOAL_ROW - 1 && r <= MapConst.GOAL_ROW + 1 && c >= MapConst.GOAL_COL - 1 && c <= MapConst.GOAL_COL + 1;
	}

	public boolean checkValidCoordinates(int r, int c) {
		return r >= 0 && c >= 0 && r < MapConst.NUM_ROWS && c < MapConst.NUM_COLS;
	}

	public boolean isObstacleOrWall(int r, int c) {
		return !checkValidCoordinates(r, c) || this.arena[r][c].isObstacle();
	}

	/**
	 * Set the obstacle flag of a cell as True or False,
	 * and update the surrounding cells' Virtual Wall flags.
	 * <p>
	 * When look at the robot, we only care the center coordinate of
	 * this robot, however, an obstacle can be 1x1 and the robot is
	 * 3x3. Thus, virtual wall is set to be points on which the center
	 * of the robot cannot be positioned. The virtual wall is the 8 points
	 * surrounding the 1x1 obstable point.
	 *
	 * @param r    is row index of cell
	 * @param c    is col index of cell
	 * @param flag is flag to be set for the cell's obstacle flag
	 */
	public void setObstacleCell(int r, int c, boolean flag) {
		// start zone and goal zone cannot have obstacles
		if (flag && (inStartZone(r, c) || inGoalZone(r, c))) return;

		// update obstacle flag of cell
		this.arena[r][c].setObstacle(flag);

		// bear in mind that boundaries are default virtual walls
		// update bottom cells' virtual wall flags
		if (r >= 1) {
			if (!this.inBoundary(r - 1, c))
				this.arena[r - 1][c].setVirtualWall(flag);

			if (c >= 1 && !this.inBoundary(r - 1, c - 1)) this.arena[r - 1][c - 1].setVirtualWall(flag);

			if (c < MapConst.NUM_COLS - 1 && !this.inBoundary(r - 1, c + 1))
				this.arena[r - 1][c + 1].setVirtualWall(flag);
		}

		// bear in mind that boundaries are default virtual walls
		// update top cells' virtual wall flags
		if (r < MapConst.NUM_ROWS - 1) {
			if (!this.inBoundary(r + 1, c))
				this.arena[r + 1][c].setVirtualWall(flag);

			if (c >= 1 && !this.inBoundary(r + 1, c - 1)) this.arena[r + 1][c - 1].setVirtualWall(flag);

			if (c < MapConst.NUM_COLS - 1 && !this.inBoundary(r + 1, c + 1))
				this.arena[r + 1][c + 1].setVirtualWall(flag);
		}

		// bear in mind that boundaries are default virtual walls
		// update left cell virtual wall flag
		if (c >= 1 && !this.inBoundary(r, c - 1)) this.arena[r][c - 1].setVirtualWall(flag);

		// bear in mind that boundaries are default virtual walls
		// update right cell virtual wall flag
		if (c < MapConst.NUM_COLS - 1 && !this.inBoundary(r, c + 1)) this.arena[r][c + 1].setVirtualWall(flag);
	}

	/**
	 * Set all cells as explored
	 */
	public void setAllCellsExplored() {
		for (Cell[] row : this.arena) {
			for (Cell cell : row) {
				cell.setExplored(true);
			}
		}
	}

	/**
	 * Set all cells as unexplored, except cells in start zone and goal zone
	 */
	public void setAllCellsUnexplored() {
		for (int r = 0; r < MapConst.NUM_ROWS; r++) {
			for (int c = 0; c < MapConst.NUM_COLS; c++) {
				if (inStartZone(r, c) || inGoalZone(r, c)) this.arena[r][c].setExplored(true);
				else this.arena[r][c].setExplored(false);
			}
		}
	}

	/**
	 * Overrides JComponent's paintComponent() method.
	 * Put all graphic codes inside this method.
	 * This method will be called automatically by the system.
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Render Cells
		for (Cell[] row : this.arena) {
			for (Cell cell : row) {
				// color each cell based its property
				Color cellColor;
				if (inStartZone(cell.getRow(), cell.getCol())) cellColor = MapConst.COLOR_START;
				else if (inGoalZone(cell.getRow(), cell.getCol())) cellColor = MapConst.COLOR_GOAL;
				else if (!cell.isExplored()) cellColor = MapConst.COLOR_UNEXPLORED;
				else if (cell.isObstacle()) cellColor = MapConst.COLOR_OBSTACLE;
				else if (cell.isWayPoint()) cellColor = MapConst.COLOR_WAYPOINT;
				else if (cell.isFastestPath()) cellColor = MapConst.COLOR_FP;
				else cellColor = MapConst.COLOR_FREE;

				// draw a rectangle with corresponding color
				g.setColor(cellColor);
				g.fillRect(cell.getX(), cell.getY(), cell.getSize(), cell.getSize());
			}
		}

		// Render Robot
		g.setColor(MapConst.COLOR_ROBOT);
		int robotX = this.robot.getCol();
		int robotY = this.robot.getRow();
		g.fillOval(
				(robotX - 1) * MapConst.CELL_SIZE + MapConst.X_OFFSET + MapConst.ROBOT_X_OFFSET,
				MapConst.MAP_HEIGHT - (robotY * MapConst.CELL_SIZE + MapConst.ROBOT_Y_OFFSET),
				MapConst.ROBOT_DIAMETER, MapConst.ROBOT_DIAMETER);

		// Render Robot's direction indicator by a triangle
		g.setColor(MapConst.COLOR_ROBOT_DIR);
		RobotConst.DIRECTION d = this.robot.getDir();

		int frontPtX, leftPtX, rightPtX, frontPtY, leftPtY, rightPtY;
		g.setColor(Color.YELLOW);

		switch (d) {
			case NORTH:
               /*g.fillOval(robotX * MapConst.CELL_SIZE + 10 + MapConst.X_OFFSET,
                        MapConst.MAP_HEIGHT - robotY * MapConst.CELL_SIZE - 15,
                        MapConst.ROBOT_DIR_DIAMETER,
                        MapConst.ROBOT_DIR_DIAMETER);*/

				frontPtX = robotX * MapConst.CELL_SIZE + 10 + MapConst.X_OFFSET;
				frontPtY = MapConst.MAP_HEIGHT - robotY * MapConst.CELL_SIZE - 15;

				leftPtX = frontPtX - 15;
				leftPtY = frontPtY - 25;

				rightPtX = frontPtX + 15;
				rightPtY = frontPtY - 25;

				int xpoints[] = {frontPtX, leftPtX, rightPtX}; // 70,50,90
				int ypoints[] = {frontPtY, leftPtY, rightPtY}; // 555,525,525
				int npoints = xpoints.length;

				g.fillPolygon(xpoints, ypoints, npoints);

				break;
			case EAST:
                /*g.fillOval(robotX * MapConst.CELL_SIZE + 35 + MapConst.X_OFFSET,
                        MapConst.MAP_HEIGHT - robotY * MapConst.CELL_SIZE + 10,
                        MapConst.ROBOT_DIR_DIAMETER,
                        MapConst.ROBOT_DIR_DIAMETER);*/

				frontPtX = robotX * MapConst.CELL_SIZE + 35 + MapConst.X_OFFSET;
				frontPtY = MapConst.MAP_HEIGHT - robotY * MapConst.CELL_SIZE + 10;

				leftPtX = frontPtX + 25;
				leftPtY = frontPtY - 15;

				rightPtX = frontPtX + 25;
				rightPtY = frontPtY + 15;

				int xpoints1[] = {frontPtX, leftPtX, rightPtX}; // 70,50,90
				int ypoints1[] = {frontPtY, leftPtY, rightPtY}; // 555,525,525
				int npoints1 = xpoints1.length;

				g.fillPolygon(xpoints1, ypoints1, npoints1);

				break;
			case SOUTH:
                /*g.fillOval(robotX * MapConst.CELL_SIZE + 10 + MapConst.X_OFFSET,
                        MapConst.MAP_HEIGHT - robotY * MapConst.CELL_SIZE + 35,
                        MapConst.ROBOT_DIR_DIAMETER,
                        MapConst.ROBOT_DIR_DIAMETER);*/

				frontPtX = robotX * MapConst.CELL_SIZE + 10 + MapConst.X_OFFSET;
				frontPtY = MapConst.MAP_HEIGHT - robotY * MapConst.CELL_SIZE + 35;

				leftPtX = frontPtX + 15;
				leftPtY = frontPtY + 25;

				rightPtX = frontPtX - 15;
				rightPtY = frontPtY + 25;

				int xpoints2[] = {frontPtX, leftPtX, rightPtX}; // 70,50,90
				int ypoints2[] = {frontPtY, leftPtY, rightPtY}; // 555,525,525
				int npoints2 = xpoints2.length;

				g.fillPolygon(xpoints2, ypoints2, npoints2);

				break;
			case WEST:
                /*g.fillOval(robotX * MapConst.CELL_SIZE - 15 + MapConst.X_OFFSET,
                        MapConst.MAP_HEIGHT - robotY * MapConst.CELL_SIZE + 10,
                        MapConst.ROBOT_DIR_DIAMETER,
                        MapConst.ROBOT_DIR_DIAMETER);*/

				frontPtX = robotX * MapConst.CELL_SIZE - 15 + MapConst.X_OFFSET;
				frontPtY = MapConst.MAP_HEIGHT - robotY * MapConst.CELL_SIZE + 10;

				leftPtX = frontPtX - 15;
				leftPtY = frontPtY + 25;

				rightPtX = frontPtX - 15;
				rightPtY = frontPtY - 25;

				int xpoints3[] = {frontPtX, leftPtX, rightPtX}; // 70,50,90
				int ypoints3[] = {frontPtY, leftPtY, rightPtY}; // 555,525,525
				int npoints3 = xpoints3.length;

				g.fillPolygon(xpoints3, ypoints3, npoints3);

				break;
		}
	}

	public int calAreaExplored() {
		int area = 0;
		for (Cell[] row : this.arena) {
			for (Cell cell : row) {
				if (cell.isExplored()) area++;
			}
		}

		return area;
	}
}
