package algorithms;

import config.MapConst;
import config.RobotConst;
import map.Cell;
import map.Map;
import robot.Robot;
import simulator.Simulator;
import utils.CommunicationManager;

import java.util.ArrayList;

public class Exploration {
	private final Map actualMap;
	private final Map exploredMap;
	private final Robot robot;
	private final int coverageLimit;
	private final int timeLimit;
	private int areaExplored;
	private long startTime;
	private long endTime;
	public static int prevCalibrateTurns;
	private boolean caliMode;
	private int caliLimit = 6;
	public static boolean overwritten = false;
	public boolean expEnded = false;
	private static int RFCount = 0;

	// Constructor for real run
	public Exploration(Map exploredMap, Robot robot, int coverageLimit, int timeLimit) {
		this.actualMap = null;
		this.exploredMap = exploredMap;
		this.robot = robot;
		this.coverageLimit = coverageLimit;
		this.timeLimit = timeLimit;
	}

	// Constructor for simulation run
	public Exploration(Map exploredMap, Robot robot, int coverageLimit, int timeLimit, Map actualMap) {
		this.actualMap = actualMap;
		this.exploredMap = exploredMap;
		this.robot = robot;
		this.coverageLimit = coverageLimit;
		this.timeLimit = timeLimit;
	}

	public Map getExploredMap() {
		return this.exploredMap;
	}

	public Robot getRobot() {
		return this.robot;
	}

	public int getCoverageLimit() {
		return this.coverageLimit;
	}

	public int getTimeLimit() {
		return this.timeLimit;
	}

	public int getAreaExplored() {
		return this.areaExplored;
	}

	public void setAreaExplored(int areaExplored) {
		this.areaExplored = areaExplored;
	}

	public long getStartTime() {
		return this.startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return this.endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public int getPrevCalibrateTurns() {
		return prevCalibrateTurns;
	}

	public void setPrevCalibrateTurns(int numTurns) {
		prevCalibrateTurns = numTurns;
	}

	public void run() {
		if (this.robot.isRealRobot()) {
			System.out.println("Start calibration....");
			this.initialCalibration();

			while (true) {
				// wating for android to send the start exploration message
				System.out.println("Waiting for EX_START message from Android....");
				String msg = CommunicationManager.getCommMgr().receiveMsg();
				if (msg != null && msg.split(CommunicationManager.SEPARATOR)[0].equals(CommunicationManager.EX_START))
					break;
			}
		}
		this.startTime = System.currentTimeMillis();
		this.endTime = this.startTime + (timeLimit * 1000);

		// send start command
		System.out.println("Start exploration....");
		if (this.robot.isRealRobot()) {
			CommunicationManager.getCommMgr().sendMsg("S", CommunicationManager.ROBOT_START);
		}

		// sense the environment
		robotSenseAndMapRepaint();
		this.areaExplored = this.exploredMap.calAreaExplored();
		System.out.println("Explored Area: " + areaExplored);
		System.out.println("Starting position: " + this.robot.getRow() + ", " + this.robot.getCol());

		explore();
	}

	private void robotSenseAndMapRepaint() {
		// update the sensor direction
		this.robot.updateSensorsDirections();

		// sense the update the explored map
		if (this.robot.isRealRobot()) this.robot.sense(this.exploredMap);
		else this.robot.simulateSense(this.exploredMap, this.actualMap);

		// TODO: why is areaExplored zero? where it is initialized
		// Where 
		double areaCoveredPc = (new Double(this.areaExplored) / MapConst.NUM_CELLS) * 100;
		String areaCoveredPcStr = String.format("%.2f %%", areaCoveredPc);
		Simulator.areaCoveredLbl.setText(areaCoveredPcStr);

		// repaint the map
		this.exploredMap.repaint();
	}

	private void explore() {
		do {
			// move the robot
			nextMove();

			// calculate area explored
			this.areaExplored = this.exploredMap.calAreaExplored();
			System.out.println("Area explored: " + this.areaExplored);

			// terminate when the robot is back to the start
			// and (area explored is more than 60% or 90% percent of the time is used)
			if ((this.robot.getRow() == MapConst.START_ROW && this.robot.getCol() == MapConst.START_COL)
					&& ((new Double(this.areaExplored) / MapConst.NUM_CELLS) * 100 > 60)
					|| (System.currentTimeMillis() > (this.endTime - 0.1 * this.timeLimit * 1000))) {
				break;
			}
		} while (this.areaExplored < this.coverageLimit && System.currentTimeMillis() < this.endTime);

		// explore unexplored cells when the areaExplored is smaller than 99% of the map
		// and there is still more than 15% of the time left until endTime
		// TODO: no need to explore the whole map?
		// ANSWER: remove 0.99, run normally with different map
		boolean state = true;
		while (state && this.areaExplored < this.coverageLimit * 0.99 && System.currentTimeMillis() < (this.endTime - 0.15 * this.timeLimit * 1000)) {
			state = exploreUnexplored();
			this.areaExplored = this.exploredMap.calAreaExplored();
			System.out.println("Area explored: " + this.areaExplored);
		}

		// go back to start
		if (this.robot.isRealRobot()
				|| (!this.robot.isRealRobot() && this.areaExplored <= this.coverageLimit && System.currentTimeMillis() < (this.endTime - 0.1 * this.timeLimit * 1000))) {
			goToStart();
		}
	}

	private void nextMove() {
		int r = this.robot.getRow();
		int c = this.robot.getCol();

		if (isRightFree(r, c) && isLeftFree(r, c) &&
				(this.robot.getDir().equals(RobotConst.DIRECTION.NORTH) || this.robot.getDir().equals(RobotConst.DIRECTION.SOUTH)) &&
				overwritten) {
			// TODO: what does overwritten mean here?
			// the robot must be in vertical direction and have nothing blocking on the left and right
			// the cell on the left and right must be explored, not an obstacle and not a virutla wall
			moveRobot(RobotConst.MOVE.TURN_LEFT);

			if (isFrontFree(this.robot.getRow(), this.robot.getCol())) {
				moveRobot(RobotConst.MOVE.FORWARD);
			}

			// TODO: what does RF mean?
			// ANSWER: only increment when turn right and move forward -> RF: Right Forward?
			RFCount = 0;
		} else if (RFCount > 4) {
			// TODO: why compare row index with number of columns?
			if (this.robot.getRow() < MapConst.NUM_COLS / 2)
				turnRobot(RobotConst.DIRECTION.EAST);
			else
				turnRobot(RobotConst.DIRECTION.WEST);

			// keep moving forward
			while (isFrontFree(this.robot.getRow(), this.robot.getCol())) {
				moveRobot(RobotConst.MOVE.FORWARD);
			}

			turnRobot(RobotConst.DIRECTION.getNextAntiClk(this.robot.getDir()));
			RFCount = 0;
		} else if (isRightFree(this.robot.getRow(), this.robot.getCol())) {
			// turn right and move forward
			moveRobot(RobotConst.MOVE.TURN_RIGHT);
			if (isFrontFree(this.robot.getRow(), this.robot.getCol())) {
				moveRobot(RobotConst.MOVE.FORWARD);
				RFCount++;
			}
		} else if (isFrontFree(this.robot.getRow(), this.robot.getCol())) {
			// move forward
			moveRobot(RobotConst.MOVE.FORWARD);
			RFCount = 0;
		} else if (isLeftFree(this.robot.getRow(), this.robot.getCol())) {
			// turn left and move forward
			moveRobot(RobotConst.MOVE.TURN_LEFT);
			if (isFrontFree(this.robot.getRow(), this.robot.getCol())) {
				moveRobot(RobotConst.MOVE.FORWARD);
			}
			RFCount = 0;
		} else {
			// u turn
			moveRobot(RobotConst.MOVE.TURN_LEFT);
			moveRobot(RobotConst.MOVE.TURN_LEFT);
			RFCount = 0;
		}
		overwritten = false;

	}

	private void moveRobot(RobotConst.MOVE m) {
		// move the robot
		this.robot.move(m, this.exploredMap);

		// if it is to calicate, then wait
		if (m == RobotConst.MOVE.CALIBRATE)
			this.waitCali();

		// sense and re-render on the map
		this.exploredMap.repaint();
		robotSenseAndMapRepaint();

		// calibrate after movement
		if (this.robot.isRealRobot() && !this.caliMode && !this.expEnded) {
			this.caliMode = true;

			if (canCalibrate(this.robot.getDir())) {
				// if the robot is now at a corner, turn and calibrate too. This helps to ensure the robot's orientation
				// the threshold of 3 is to prevent the bot from calibrating too much
				if (prevCalibrateTurns >= 3 &&
						!(this.canCalibrate(RobotConst.DIRECTION.getNextClk(this.robot.getDir())) &&
								this.canCalibrate(RobotConst.DIRECTION.getNextAntiClk(this.robot.getDir()))))
					this.turnAndCalibrate();

				this.moveRobot(RobotConst.MOVE.CALIBRATE);
				prevCalibrateTurns = 0;
			} else {
				prevCalibrateTurns++;
				// if never calibrate for more than or equals to caliLimit steps
				// shall force the robot to calibrate
				if (prevCalibrateTurns >= this.caliLimit) {
					this.turnAndCalibrate();
				}
			}

			this.caliMode = false;
		}
	}

	private void turnAndCalibrate() {
		RobotConst.DIRECTION targetDir;
		RobotConst.DIRECTION curDir;
		targetDir = getCalibrateDir();

		// if can calibrate in the next clockwise or
		// anticlockwise direction, turn to that direction
		// and calibrate and then turn back
		if (targetDir != null) {
			prevCalibrateTurns = 0;
			curDir = this.robot.getDir();
			turnRobot(targetDir);
			moveRobot(RobotConst.MOVE.CALIBRATE);
			turnRobot(curDir);
		}
	}

	private void waitCali() {
		while (true) {
			if (CommunicationManager.getCommMgr().receiveMsg().split(CommunicationManager.SEPARATOR)[0].equals(CommunicationManager.C_DONE)) {
				break;
			}
		}
	}

	private boolean canCalibrate(RobotConst.DIRECTION targetDir) {
		int r = this.robot.getRow();
		int c = this.robot.getCol();

		// using the 3 front SR sensors to do the calibration
		// TODO: why only calibrate when these conditions are met?
		switch (targetDir) {
			case NORTH:
				return this.exploredMap.isObstacleOrWall(r + 2, c - 1)
						&& this.exploredMap.isObstacleOrWall(r + 2, c)
						&& this.exploredMap.isObstacleOrWall(r + 2, c + 1);
			case EAST:
				return this.exploredMap.isObstacleOrWall(r + 1, c + 2)
						&& this.exploredMap.isObstacleOrWall(r, c + 2)
						&& this.exploredMap.isObstacleOrWall(r - 1, c + 2);
			case SOUTH:
				return this.exploredMap.isObstacleOrWall(r - 2, c - 1)
						&& this.exploredMap.isObstacleOrWall(r - 2, c)
						&& this.exploredMap.isObstacleOrWall(r - 2, c + 1);
			case WEST:
				return this.exploredMap.isObstacleOrWall(r + 1, c - 2)
						&& this.exploredMap.isObstacleOrWall(r, c - 2)
						&& this.exploredMap.isObstacleOrWall(r - 1, c - 2);
		}
		return false;
	}

	private RobotConst.DIRECTION getCalibrateDir() {
		RobotConst.DIRECTION curDir = this.robot.getDir();
		RobotConst.DIRECTION targetDir;

		targetDir = RobotConst.DIRECTION.getNextAntiClk(curDir);
		if (this.canCalibrate(targetDir)) return targetDir;

		targetDir = RobotConst.DIRECTION.getNextClk(curDir);
		if (this.canCalibrate(targetDir)) return targetDir;

		return null;
	}

	private void turnRobot(RobotConst.DIRECTION targetDir) {
		int numMovesNeeded = Math.abs(this.robot.getDir().ordinal() - targetDir.ordinal());
		if (numMovesNeeded == 0) return;
		if (numMovesNeeded > 2) numMovesNeeded = numMovesNeeded % 2;

		if (numMovesNeeded == 1) {
			if (RobotConst.DIRECTION.getNextClk(this.robot.getDir()) == targetDir)
				moveRobot(RobotConst.MOVE.TURN_RIGHT);
			else
				moveRobot(RobotConst.MOVE.TURN_LEFT);
		} else {
			moveRobot(RobotConst.MOVE.TURN_RIGHT);
			moveRobot(RobotConst.MOVE.TURN_RIGHT);
		}
	}

	private boolean isRightFree(int r, int c) {
		switch (this.robot.getDir()) {
			case NORTH:
				return isEastFree(r, c);
			case EAST:
				return isSouthFree(r, c);
			case SOUTH:
				return isWestFree(r, c);
			case WEST:
				return isNorthFree(r, c);
		}

		return false;
	}

	private boolean isLeftFree(int r, int c) {
		switch (this.robot.getDir()) {
			case NORTH:
				return isWestFree(r, c);
			case EAST:
				return isNorthFree(r, c);
			case SOUTH:
				return isEastFree(r, c);
			case WEST:
				return isSouthFree(r, c);
		}

		return false;
	}

	private boolean isFrontFree(int r, int c) {
		switch (this.robot.getDir()) {
			case NORTH:
				return isNorthFree(r, c);
			case EAST:
				return isEastFree(r, c);
			case SOUTH:
				return isSouthFree(r, c);
			case WEST:
				return isWestFree(r, c);
		}

		return false;
	}

	private boolean isNorthFree(int r, int c) {
		return isExploredAndNotObstacle(r + 1, c - 1)
				&& isExploredAndFree(r + 1, c)
				&& isExploredAndNotObstacle(r + 1, c + 1);
	}

	private boolean isEastFree(int r, int c) {
		return isExploredAndNotObstacle(r + 1, c + 1)
				&& isExploredAndFree(r, c + 1)
				&& isExploredAndNotObstacle(r - 1, c + 1);
	}

	private boolean isSouthFree(int r, int c) {
		return isExploredAndNotObstacle(r - 1, c - 1)
				&& isExploredAndFree(r - 1, c)
				&& isExploredAndNotObstacle(r - 1, c + 1);
	}

	private boolean isWestFree(int r, int c) {
		return isExploredAndNotObstacle(r + 1, c - 1)
				&& isExploredAndFree(r, c - 1)
				&& isExploredAndNotObstacle(r - 1, c - 1);
	}

	private boolean isExploredAndNotObstacle(int r, int c) {
		if (!this.exploredMap.checkValidCoordinates(r, c)) return false;

		Cell cell = this.exploredMap.getArena()[r][c];
		return cell.isExplored() && !cell.isObstacle();
	}

	private boolean isExploredAndFree(int r, int c) {
		if (!this.exploredMap.checkValidCoordinates(r, c)) return false;

		Cell cell = this.exploredMap.getArena()[r][c];
		return cell.isExplored() && !cell.isObstacle() && !cell.isVirtualWall();
	}

	private boolean exploreUnexplored() {
		int targetR = 1, targetC = 1;
		Cell cell;
		boolean flag = false;

		System.out.println("Trying to explore the unexplored....");
		for (int r = this.robot.getRow(); r < MapConst.NUM_ROWS; r++) {
			for (int c = 0; c < MapConst.NUM_COLS; c++) {
				cell = this.exploredMap.getArena()[r][c];
				if (!cell.isExplored()) {
					if (cell.isVirtualWall()) {
						// assign target coord as the coord of an explored cell near to this unexplored cell
						for (int i = -2; i < 3; i += 2) {
							for (int j = -2; j < 3; j += 2) {
								if (isExploredAndFree(cell.getRow() + i, cell.getCol() + j)) {
									targetR = cell.getRow() + i;
									targetC = cell.getCol() + j;
									if (targetR != this.robot.getRow() || targetC != this.robot.getCol()) {
										flag = true;
										break;
									} else {
										targetR = 1;
										targetC = 1;
									}
								}
							}
							if (flag) break;
						}
					} else {
						// assign target coord as the coord of an explored cell near to this unexplored cell
						for (int i = -1; i < 2; i++) {
							for (int j = -1; j < 2; j++) {
								if (isExploredAndFree(cell.getRow() + i, cell.getCol() + j)) {
									targetR = cell.getRow() + i;
									targetC = cell.getCol() + j;
									if (targetR != this.robot.getRow() || targetC != this.robot.getCol()) {
										flag = true;
										break;
									} else {
										targetR = 1;
										targetC = 1;
									}
								}
							}
							if (flag) break;
						}
					}
				}
				if (flag) break;
			}
			if (flag) break;
		}


		if (targetR == 1 && targetC == 1) {
			System.out.println("Can't explore the unexplored....");
			return false;
		}

		// compute fastest path to the unexplored cell
		FastestPath pathToUnExplored;
		ArrayList<RobotConst.MOVE> moves;
		pathToUnExplored = !this.robot.isRealRobot() ? new FastestPath(this.exploredMap, this.robot, this.actualMap) : new FastestPath(this.exploredMap, this.robot);
		moves = pathToUnExplored.computeFastestPath(targetR, targetC);
		if (moves == null) {
			return false;
		}

		// move to the cell
		pathToUnExplored.executeMoves(moves, this.robot);
		return true;
	}

	private void goToStart() {
		System.out.println("Going back to the start zone....");
		ArrayList<RobotConst.MOVE> moves;

		// if robot has not reached goal, go to goal
		if (!this.robot.hasReachedGoal() && this.coverageLimit == 300 && this.timeLimit == 3600) {
			FastestPath pathToGoal = new FastestPath(this.exploredMap, this.robot, this.actualMap);
			pathToGoal.setExMode(this);
			moves = pathToGoal.computeFastestPath(RobotConst.GOAL_ROW, RobotConst.GOAL_COL);
			pathToGoal.executeMoves(moves, this.robot);
		}

		FastestPath pathToStart = new FastestPath(this.exploredMap, this.robot, this.actualMap);
		pathToStart.setExMode(this);
		moves = pathToStart.computeFastestPath(RobotConst.START_ROW, RobotConst.START_COL);

		if (moves != null)
			pathToStart.executeMoves(moves, this.robot);

		System.out.println("Exploration has completed!");
		this.areaExplored = this.exploredMap.calAreaExplored();
		System.out.printf("Achieved %.2f%% Coverage", (this.areaExplored / 300.0) * 100.0);
		System.out.println(", " + this.areaExplored + " Cells");
		System.out.println("Time taken: " + (System.currentTimeMillis() - this.startTime) / 1000 + "s");

		// Calibrate
		if (robot.isRealRobot()) {
			this.expEnded = true;
			this.finalCalibration();
		}

		turnRobot(RobotConst.DIRECTION.NORTH);
	}

	private void initialCalibration() {
		this.robot.move(RobotConst.MOVE.TURN_LEFT, false, this.exploredMap);
		CommunicationManager.getCommMgr().receiveMsg();
		this.robot.move(RobotConst.MOVE.CALIBRATE, false, this.exploredMap);
		this.waitCali();
		CommunicationManager.getCommMgr().receiveMsg();
		this.robot.move(RobotConst.MOVE.TURN_LEFT, false, this.exploredMap);
		CommunicationManager.getCommMgr().receiveMsg();
		this.robot.move(RobotConst.MOVE.CALIBRATE, false, this.exploredMap);
		this.waitCali();
		CommunicationManager.getCommMgr().receiveMsg();
		this.robot.move(RobotConst.MOVE.TURN_LEFT, false, this.exploredMap);
	}

	private void finalCalibration() {
		if (this.expEnded)
			CommunicationManager.getCommMgr().sendMsg("", CommunicationManager.EX_DONE);
		String msg;
		while (true) {
			msg = CommunicationManager.getCommMgr().receiveMsg();
			String[] msgArr = msg.split(CommunicationManager.SEPARATOR);
			if (msgArr[0].equals(CommunicationManager.WAY_POINT)) {
				break;
			}
		}
		turnRobot(RobotConst.DIRECTION.WEST);
		this.robot.move(RobotConst.MOVE.CALIBRATE, false, this.exploredMap);
		this.waitCali();
		CommunicationManager.getCommMgr().receiveMsg();

		turnRobot(RobotConst.DIRECTION.SOUTH);
		this.robot.move(RobotConst.MOVE.CALIBRATE, false, this.exploredMap);
		this.waitCali();
		CommunicationManager.getCommMgr().receiveMsg();

		turnRobot(RobotConst.DIRECTION.WEST);
		this.robot.move(RobotConst.MOVE.CALIBRATE, false, this.exploredMap);
		this.waitCali();
		CommunicationManager.getCommMgr().receiveMsg();
	}
}
