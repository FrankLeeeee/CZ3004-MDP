package algorithms;

import communication.GrpcClient;
import config.MapConst;
import config.RobotConst;
import map.Cell;
import map.Map;
import org.apache.log4j.Logger;
import robot.Robot;
import simulator.Simulator;

import java.io.IOException;
import java.util.ArrayList;


public class Exploration {
	private final Map actualMap;
	private final Map exploredMap;
	private final Robot robot;
	private final int coverageLimit;
	private final int timeLimit;
	private int areaExplored;
	public static long startTime;
	public static long endTime;
	public static int prevCalibrateTurns;
	private boolean caliMode;
	private int caliLimit = 1;
	public static boolean overwritten = false;
	public boolean expEnded = false;
	private static int RFCount = 0;
	private boolean stop = false;
	private GrpcClient client;

	private static Logger logger = Logger.getLogger(Exploration.class);

	// Constructor for real run
	public Exploration(Map exploredMap, Robot robot, int coverageLimit, int timeLimit) {
		this.actualMap = null;
		this.exploredMap = exploredMap;
		this.robot = robot;
		this.coverageLimit = coverageLimit;
		this.timeLimit = timeLimit;
		this.client = GrpcClient.getInstance();
	}

	// Constructor for simulation run
	public Exploration(Map exploredMap, Robot robot, int coverageLimit, int timeLimit, Map actualMap) {
		this.actualMap = actualMap;
		this.exploredMap = exploredMap;
		this.robot = robot;
		this.coverageLimit = coverageLimit;
		this.timeLimit = timeLimit;
		this.client = GrpcClient.getInstance();
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

	public void run() throws IOException {
		this.startTime = System.currentTimeMillis();
		this.endTime = this.startTime + (timeLimit * 1000);

		// send start command
		logger.info("Start exploration....");

		// sense the environment
		robotSenseAndMapRepaint();
		this.areaExplored = this.exploredMap.calAreaExplored();
		logger.info("Explored Area: " + areaExplored);
		logger.info("Starting position: " + this.robot.getRow() + ", " + this.robot.getCol());

		explore();
	}

	private void robotSenseAndMapRepaint() {
		// update the sensor direction
		logger.info("updating sensor and repainting");
		this.robot.updateSensorsDirections();

		// sense the update the explored map
		if (this.robot.isRealRobot()) this.robot.sense(this.exploredMap);
		else this.robot.simulateSense(this.exploredMap, this.actualMap);

		double areaCoveredPc = (new Double(this.areaExplored) / MapConst.NUM_CELLS) * 100;
		String areaCoveredPcStr = String.format("%.2f %%", areaCoveredPc);
		Simulator.areaCoveredLbl.setText(areaCoveredPcStr);

//		// repaint the map
		this.exploredMap.repaint();
		logger.info("Fnished updating sensor and repainting");
	}

	private void explore() throws IOException {
		if (Simulator.task == "IMG") {
			if (this.robot.isRealRobot()) {
				this.tryTakePhoto();
			} else {
				this.simulateTakingPhoto();
			}
		}

		do {
			// move the robot
			nextMove();
//			nextMoveLeftWall();

			if (Simulator.task == "IMG") {
				if (this.robot.isRealRobot()) {
					this.tryTakePhoto();
				} else {
					this.simulateTakingPhoto();
				}
			}

			// calculate area explored
			this.areaExplored = this.exploredMap.calAreaExplored();
			logger.info("Area explored: " + this.areaExplored);

			// terminate when the robot is back to the start
			// and (area explored is more than 60% or 90% percent of the time is used)
			if ((this.robot.getRow() == MapConst.START_ROW && this.robot.getCol() == MapConst.START_COL)
					&& ((new Double(this.areaExplored) / MapConst.NUM_CELLS) * 100 > 60)) {
				break;
			}

			// stop when time is not enough
			if (System.currentTimeMillis() > (this.getEndTime() - 0.20 * this.timeLimit * 1000)) {
				break;
			}

		} while (this.areaExplored < this.coverageLimit);

		// explore unexplored cells when the areaExplored is smaller than 99% of the map
		// and there is still more than 15% of the time left until endTime
		// Q: no need to explore the whole map?
		// A: remove 0.99, run normally with different map
		logger.info("Start exploring the remaining unknown area");
		boolean state = true;
		while (state && this.areaExplored < this.coverageLimit * 0.60 && System.currentTimeMillis() < (this.getEndTime() - 0.20 * this.timeLimit * 1000)) {
			state = exploreUnexplored();
			this.areaExplored = this.exploredMap.calAreaExplored();
			System.out.println("Area explored: " + this.areaExplored);
		}
		logger.info("finish exploring the unexplored area");

		// go back to start
		if (Simulator.task == "EXP") {
			if (this.robot.isRealRobot()
					|| (!this.robot.isRealRobot() && this.areaExplored <= this.coverageLimit && System.currentTimeMillis() < (endTime - 0.1 * this.timeLimit * 1000))) {
				goToStart();
				logger.info("back to start");
			}
		} else if (Simulator.task == "IMG" && this.endTime - System.currentTimeMillis() < 10 * 1000) {
			if (this.endTime - System.currentTimeMillis() < 10 * 1000) {
				logger.info("Start to detect the undetected area");
				goToStart();

				findTheUndetected();

				boolean imgRecState = true;
				while (imgRecState && this.endTime - System.currentTimeMillis() < 10 * 1000) {
					// stop when time is not enough
					if (this.endTime - System.currentTimeMillis() < 10 * 1000) {
						break;
					}
					imgRecState = detectTheUndetected();
				}
			}

			if (robot.isRealRobot()) {
				logger.info("trying to get the image");
				client.getFinalImageResults(Simulator.imgPathforIMG);
				logger.info("successfully get the image");
			} else {
				logger.info("simulate to get photos");
			}
		}

		logger.info("exploration ended");


	}

	private void nextMove() {
		// stop when time is not enough
		if (this.endTime - System.currentTimeMillis() < 10 * 1000) {
			return;
		}

		int r = this.robot.getRow();
		int c = this.robot.getCol();

		if (RFCount > 4) {
			// TODO: why compare row index with number of columns?
			// DONE: ?after go right and forward 4 times
			if (this.robot.getCol() > MapConst.NUM_COLS / 2)
				turnRobot(RobotConst.DIRECTION.EAST);
			else
				turnRobot(RobotConst.DIRECTION.WEST);

			// keep moving forward
			while (isFrontFree(this.robot.getRow(), this.robot.getCol())) {
				moveRobot(RobotConst.MOVE.FORWARD);
			}

			turnRobot(RobotConst.DIRECTION.getNextAntiClk(this.robot.getDir()));
			RFCount = 0;
		} else if (isRightFree(r, c)) {
			moveRobot(RobotConst.MOVE.TURN_RIGHT);
			RFCount++;

			if (isFrontFree(this.robot.getRow(), this.robot.getCol())) {
				moveRobot(RobotConst.MOVE.FORWARD);
			}
		}
//
//		if (isRightFree(r, c) && isLeftFree(r, c) &&
//				(this.robot.getDir().equals(RobotConst.DIRECTION.NORTH) || this.robot.getDir().equals(RobotConst.DIRECTION.SOUTH) && overwritten)) {
//			// TODO: what does overwritten mean here?
//			// DONE: ?Cannot stop exploration if remove overwritten
//			// Q: where set overwritten True?
//			// A: in sensor
//			// the robot must be in vertical direction and have nothing blocking on the left and right
//			// the cell on the left and right must be explored, not an obstacle and not a virtual wall
//			moveRobot(RobotConst.MOVE.TURN_RIGHT);
//
//			if (isFrontFree(this.robot.getRow(), this.robot.getCol())) {
//				moveRobot(RobotConst.MOVE.FORWARD);
//			}
//
//			// Q: what does RF mean?
//			// A: only increment when turn right and move forward -> RF: Right Forward?
//			RFCount = 0;
//		}
		else if (isFrontFree(this.robot.getRow(), this.robot.getCol())) {
			// turn right and move forward
			moveRobot(RobotConst.MOVE.FORWARD);
//			if (isFrontFree(this.robot.getRow(), this.robot.getCol())) {
//				moveRobot(RobotConst.MOVE.FORWARD);
//				RFCount++;
//			}

		} else if (isLeftFree(this.robot.getRow(), this.robot.getCol())) {
			// move forward
			moveRobot(RobotConst.MOVE.TURN_LEFT);
			RFCount = 0;
		} else {
			// move forward
			moveRobot(RobotConst.MOVE.TURN_LEFT);
			moveRobot(RobotConst.MOVE.TURN_LEFT);
			RFCount = 0;
		}
		overwritten = false;
	}

	private void nextMoveLeftWall() {
		// stop when time is not enough
		if (this.endTime - System.currentTimeMillis() < 10 * 1000) {
			return;
		}

		int r = this.robot.getRow();
		int c = this.robot.getCol();
		if (isLeftFree(this.robot.getRow(), this.robot.getCol())) {
			// turn right and move forward
			moveRobot(RobotConst.MOVE.TURN_LEFT);

			if (isFrontFree(this.robot.getRow(), this.robot.getCol())) {
				moveRobot(RobotConst.MOVE.FORWARD);
				RFCount++;
			}

			if (RFCount > 4) {
				// TODO: why compare row index with number of columns?
				// DONE: ?after go right and forward 4 times
				if (this.robot.getRow() < MapConst.NUM_COLS / 2)
					turnRobot(RobotConst.DIRECTION.EAST);
				else
					turnRobot(RobotConst.DIRECTION.WEST);

				// keep moving forward
				while (isFrontFree(this.robot.getRow(), this.robot.getCol())) {
					moveRobot(RobotConst.MOVE.FORWARD);
				}

				turnRobot(RobotConst.DIRECTION.getNextClk(this.robot.getDir()));
				RFCount = 0;
			}
		} else if (isRightFree(r, c) && isLeftFree(r, c) &&
				(this.robot.getDir().equals(RobotConst.DIRECTION.NORTH) || this.robot.getDir().equals(RobotConst.DIRECTION.SOUTH) && overwritten)) {
			// TODO: what does overwritten mean here?
			// DONE: ?Cannot stop exploration if remove overwritten
			// Q: where set overwritten True?
			// A: in sensor
			// the robot must be in vertical direction and have nothing blocking on the left and right
			// the cell on the left and right must be explored, not an obstacle and not a virtual wall
			moveRobot(RobotConst.MOVE.TURN_RIGHT);

			if (isFrontFree(this.robot.getRow(), this.robot.getCol())) {
				moveRobot(RobotConst.MOVE.FORWARD);
			}

			// Q: what does RF mean?
			// A: only increment when turn right and move forward -> RF: Right Forward?
			RFCount = 0;
		} else if (isFrontFree(this.robot.getRow(), this.robot.getCol())) {
			// move forward
			moveRobot(RobotConst.MOVE.FORWARD);
			RFCount = 0;
		} else if (isRightFree(this.robot.getRow(), this.robot.getCol())) {
			// turn left and move forward
			moveRobot(RobotConst.MOVE.TURN_RIGHT);
			if (isFrontFree(this.robot.getRow(), this.robot.getCol())) {
				moveRobot(RobotConst.MOVE.FORWARD);
			}
			RFCount = 0;
		} else {
			// u turn
			moveRobot(RobotConst.MOVE.TURN_RIGHT);
			moveRobot(RobotConst.MOVE.TURN_RIGHT);
			RFCount = 0;
		}
		overwritten = false;
	}

	private void moveRobot(RobotConst.MOVE m) {
		// move the robot
		logger.info("move: " + m);
		this.robot.move(m, this.exploredMap, true, false);

		// sense and re-render on the map
		this.exploredMap.repaint();
		robotSenseAndMapRepaint();

		// calibrate after movement
		if (this.robot.isRealRobot()) {
			if (canCalibrateFront(this.robot.getDir())) {
				this.robot.move(RobotConst.MOVE.CALIBRATE_FRONT, this.exploredMap, true, false);
				logger.info("Calibrating against the front");
			}

			prevCalibrateTurns++;
			if (prevCalibrateTurns >= 1 && canCalibrateRight(this.robot.getDir())) {
				this.robot.move(RobotConst.MOVE.CALIBRATE_WALL, this.exploredMap, true, false);
				logger.info("Calibrating against the right wall");
				prevCalibrateTurns = 0;
			}
		}
	}

	private boolean canCalibrateFront(RobotConst.DIRECTION targetDir) {
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
				return this.exploredMap.isObstacleOrWall(r - 1, c - 2)
						&& this.exploredMap.isObstacleOrWall(r, c - 2)
						&& this.exploredMap.isObstacleOrWall(r + 1, c - 2);
		}
		return false;
	}

	private boolean canCalibrateRight(RobotConst.DIRECTION targetDir) {
		int r = this.robot.getRow();
		int c = this.robot.getCol();

		// using the 3 front SR sensors to do the calibration
		// TODO: why only calibrate when these conditions are met?
		switch (targetDir) {
			case NORTH:
				return this.exploredMap.isObstacleOrWall(r + 1, c + 2)
						&& this.exploredMap.isObstacleOrWall(r, c + 2)
						&& this.exploredMap.isObstacleOrWall(r - 1, c + 2);
			case EAST:
				return this.exploredMap.isObstacleOrWall(r - 2, c + 1)
						&& this.exploredMap.isObstacleOrWall(r - 2, c)
						&& this.exploredMap.isObstacleOrWall(r - 2, c - 1);
			case SOUTH:
				return this.exploredMap.isObstacleOrWall(r + 1, c - 2)
						&& this.exploredMap.isObstacleOrWall(r, c - 2)
						&& this.exploredMap.isObstacleOrWall(r - 1, c - 2);
			case WEST:
				return this.exploredMap.isObstacleOrWall(r + 2, c - 1)
						&& this.exploredMap.isObstacleOrWall(r + 2, c)
						&& this.exploredMap.isObstacleOrWall(r + 2, c + 1);
		}
		return false;
	}

	private void findTheUndetected() {
		logger.info("finding the undetected area");

		for (int r = 0; r < MapConst.NUM_ROWS; r++) {
			for (int c = 0; c < MapConst.NUM_COLS; c++) {
				if (checkNearbyObstacle(r, c))
					this.exploredMap.getArena()[r][c].setCanTakePhoto(true);
			}
		}
		clearUnreachableBlock();
		this.exploredMap.repaint();
	}

	private boolean checkNearbyObstacle(int r, int c) {
		if (!this.exploredMap.checkValidCoordinates(r, c)) {
			return false;
		} else {
			Cell center = this.exploredMap.getArena()[r][c];

			if (center.isCanTakePhoto() || center.isObstacle() || center.isVirtualWall())
				return false;
		}

		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				int new_r = r + i;
				int new_c = c + j;
				if (!this.exploredMap.checkValidCoordinates(new_r, new_c)) {
					continue;
				} else {
					Cell cell = this.exploredMap.getArena()[new_r][new_c];
					if (!cell.isExplored() || cell.isObstacle()) {
						return false;
					}
				}
			}
		}

		for (int i = -2; i < 3; i++) {
			for (int j = -2; j < 3; j++) {
				int new_r = r + i;
				int new_c = c + j;
				if (!this.exploredMap.checkValidCoordinates(new_r, new_c)) {
					continue;
				} else {
					Cell cell = this.exploredMap.getArena()[new_r][new_c];

					if (Math.abs(i) == 2 && Math.abs(j) == 2) {
						continue;
					} else if (Math.abs(i) == 2 || Math.abs(j) == 2) {
						if (cell.isObstacle()) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	private void clearUnreachableBlock() {
		for (int r = 0; r < MapConst.NUM_ROWS; r++) {
			for (int c = 0; c < MapConst.NUM_COLS; c++) {
				Cell cell = this.exploredMap.getArena()[r][c];

				// clear virtual wall blocks
				if (cell.isCanTakePhoto() && cell.isVirtualWall()) {
					cell.setDetectedForImage(true);
				}


				// clear unneccessary block
				if (!(isValidAndAnObstacle(r - 2, c) ||
						isValidAndAnObstacle(r + 2, c) ||
						isValidAndAnObstacle(r, c - 2) ||
						isValidAndAnObstacle(r, c + 2)) &&
						(isValidAndCanTakePhoto(r - 1, c) ^
								isValidAndCanTakePhoto(r + 1, c) ^
								isValidAndCanTakePhoto(r, c + 1) ^
								isValidAndCanTakePhoto(r, c - 1))
				) {
					cell.setDetectedForImage(true);
				}
			}
		}
	}

	private boolean isValidAndAnObstacle(int r, int c) {
		if (this.exploredMap.checkValidCoordinates(r, c)) {
			Cell cell = this.exploredMap.getArena()[r][c];
			return cell.isExplored() && cell.isObstacle();
		} else {
			return false;
		}
	}

	private boolean isValidAndCanTakePhoto(int r, int c) {
		if (this.exploredMap.checkValidCoordinates(r, c)) {
			Cell cell = this.exploredMap.getArena()[r][c];
			return cell.isExplored() && cell.isCanTakePhoto();
		} else {
			return false;
		}
	}


	private boolean detectTheUndetected() {
		// stop when time is not enough
		if (this.endTime - System.currentTimeMillis() < 10 * 1000) {
			return false;
		}

		int targetR = 1, targetC = 1;
		Cell cell = null;
		boolean flag = false;

		for (int r = this.robot.getRow(); r < MapConst.NUM_ROWS; r++) {
			for (int c = 0; c < MapConst.NUM_COLS; c++) {
				cell = this.exploredMap.getArena()[r][c];
				if (cell.isCanTakePhoto() && !cell.isDetectedForImage()) {
					targetR = cell.getRow();
					targetC = cell.getCol();
					flag = true;
					break;
				}
			}

			if (flag) {
				break;
			}
		}

		if (cell == null) {
			return false;
		} else if (targetC == this.robot.getCol() && targetR == this.robot.getRow()) {
			return false;
		} else if (targetC == 1 && targetR == 1) {
			return false;
		}

		// compute fastest path to the unexplored cell
		FastestPath pathToUnExplored;
		ArrayList<RobotConst.MOVE> moves;

		pathToUnExplored = !this.robot.isRealRobot() ? new FastestPath(this.exploredMap, this.robot, this.actualMap) : new FastestPath(this.exploredMap, this.robot);
		moves = pathToUnExplored.computeFastestPath(targetR, targetC);
		if (moves == null) {
			cell.setDetectedForImage(true);
			return true;
		}

		// move to the cell
		logger.info(this.robot.getRow());

		pathToUnExplored.executeMoves(moves, this.robot, this.endTime, this);

		if (this.robot.isRealRobot()) {
			this.tryTakePhoto();
		} else {
			this.simulateTakingPhoto();
		}

		this.exploredMap.repaint();
		return true;
	}

	private RobotConst.DIRECTION getCalibrateDir() {
		RobotConst.DIRECTION curDir = this.robot.getDir();
		RobotConst.DIRECTION targetDir;

		targetDir = RobotConst.DIRECTION.getNextAntiClk(curDir);
		if (this.canCalibrateRight(targetDir)) return targetDir;

		targetDir = RobotConst.DIRECTION.getNextClk(curDir);
		if (this.canCalibrateRight(targetDir)) return targetDir;

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
		// stop when time is not enough
		if (this.endTime - System.currentTimeMillis() < 10 * 1000) {
			return false;
		}

		int targetR = 1, targetC = 1;
		Cell cell;
		boolean flag = false;

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
									if (targetR != this.robot.getRow() && targetC != this.robot.getCol()) {
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
									if (targetR != this.robot.getRow() && targetC != this.robot.getCol()) {
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
			logger.info("Can't explore the unexplored....");
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
		pathToUnExplored.executeMoves(moves, this.robot, this.endTime, this);

		if (Simulator.task == "IMG") {
			this.findTheUndetected();

			if (this.robot.isRealRobot()) {
				this.tryTakePhoto();
			} else {
				this.simulateTakingPhoto();
				this.exploredMap.repaint();
			}
		}

		return true;
	}

	private void goToStart() {
		// stop when time is not enough
		if (this.endTime - System.currentTimeMillis() < 5 * 1000) {
			return;
		}

		logger.info("Going back to the start zone....");
		ArrayList<RobotConst.MOVE> moves;

		// if robot has not reached goal, go to goal
		if (!this.robot.hasReachedGoal() && this.coverageLimit == 300 && this.timeLimit == 3600) {
			FastestPath pathToGoal = new FastestPath(this.exploredMap, this.robot, this.actualMap);
			pathToGoal.setExMode(this);
			moves = pathToGoal.computeFastestPath(RobotConst.GOAL_ROW, RobotConst.GOAL_COL);
			pathToGoal.executeMoves(moves, this.robot, this.endTime, this);
		}

		FastestPath pathToStart = new FastestPath(this.exploredMap, this.robot, this.actualMap);
		pathToStart.setExMode(this);
		moves = pathToStart.computeFastestPath(RobotConst.START_ROW, RobotConst.START_COL);

		if (moves != null)
			pathToStart.executeMoves(moves, this.robot, this.endTime, this);

		logger.info("Exploration has completed!");
		this.areaExplored = this.exploredMap.calAreaExplored();
		logger.info(String.format("Achieved %.2f%% Coverage", (this.areaExplored / 300.0) * 100.0));
		logger.info(", " + this.areaExplored + " Cells");
		logger.info("Time taken: " + (System.currentTimeMillis() - this.startTime) / 1000 + "s");

		// Calibrate
		if (robot.isRealRobot() && Simulator.task == "EXP") {
			this.expEnded = true;
//			this.finalCalibration();
		}

//		turnRobot(RobotConst.DIRECTION.NORTH);
	}

	public void initialCalibration() {
		this.robot.move(RobotConst.MOVE.TURN_LEFT, this.exploredMap, true, false);
		this.robot.move(RobotConst.MOVE.CALIBRATE_FRONT, this.exploredMap, true, false);
		this.robot.move(RobotConst.MOVE.TURN_LEFT, this.exploredMap, true, false);
		this.robot.move(RobotConst.MOVE.CALIBRATE_FRONT, this.exploredMap, true, false);
		this.robot.move(RobotConst.MOVE.TURN_LEFT, this.exploredMap, true, false);
		this.robot.move(RobotConst.MOVE.CALIBRATE_WALL, this.exploredMap, true, false);
	}

	private void finalCalibration() {
		if (this.expEnded)
			client.stopRobot();

		turnRobot(RobotConst.DIRECTION.WEST);
		this.robot.move(RobotConst.MOVE.CALIBRATE_FRONT, this.exploredMap, true, false);

		turnRobot(RobotConst.DIRECTION.SOUTH);
		this.robot.move(RobotConst.MOVE.CALIBRATE_FRONT, this.exploredMap, true, false);

		turnRobot(RobotConst.DIRECTION.WEST);
		this.robot.move(RobotConst.MOVE.CALIBRATE_FRONT, this.exploredMap, true, false);
	}

	private boolean canTakePhoto() {
		int r = this.robot.getRow();
		int c = this.robot.getCol();

		Cell cell = this.exploredMap.getArena()[r][c];
		if (cell.isCanTakePhoto() && isUnnecessaryToTakePhoto(r, c)) {
			return false;
		}
		if (cell.isCanTakePhoto() && !cell.isDetectedForImage()) {
			return true;
		} else {
			return false;
		}

//		if (this.exploredMap.checkValidCoordinates(r+2, c)){
//
//		}
//
//		for (int i = -2; i <= 3; i++) {
//			for (int j = -3; j <= 3; j++) {
//				int blk_r = r + i;
//				int blk_c = c + j;
//
//				if (this.exploredMap.checkValidCoordinates(blk_r, blk_c)) {
//					Cell cell = this.exploredMap.getArena()[blk_r][blk_c];
//					if (cell.isCanTakePhoto() && !cell.isDetectedForImage()) {
//						return new Position(blk_r, blk_c);
//					}
//				}
//			}
//		}

	}

	private boolean isUnnecessaryToTakePhoto(int r, int c) {
		return checkIfCanTakePhoto(r + 1, c - 2, r + 1, c, 0, -1)
				&& checkIfCanTakePhoto(r - 1, c - 2, r - 1, c, 0, -1)
				&& checkIfCanTakePhoto(r - 2, c - 1, r - 1, c - 1, -1, 0)
				&& checkIfCanTakePhoto(r - 2, c + 1, r - 1, c + 1, -1, 0)
				&& checkIfCanTakePhoto(r + 1, c + 2, r + 1, c, 0, 1)
				&& checkIfCanTakePhoto(r - 1, c + 2, r - 1, c, 0, 1)
				&& checkIfCanTakePhoto(r + 2, c - 1, r, c - 1, 1, 0)
				&& checkIfCanTakePhoto(r + 2, c + 1, r, c + 1, 1, 0);

	}

	public boolean checkIfCanTakePhoto(int obstacleRow, int obstacleCol, int targetR, int targetC, int rowOffset, int colOffset) {
		if (!this.exploredMap.checkValidCoordinates(obstacleRow, obstacleCol) || !this.exploredMap.checkValidCoordinates(targetR, targetC)) {
			return false;
		} else {
			Cell obstacle = this.exploredMap.getArena()[obstacleRow][obstacleCol];
			Cell target = this.exploredMap.getArena()[targetR][targetC];
			Cell middle = this.exploredMap.getArena()[targetR + rowOffset][targetC + colOffset];

			if (!obstacle.isExplored() || !obstacle.isObstacle() || !target.isCanTakePhoto() || !middle.isExplored() || middle.isObstacle()) {
				return false;
			} else {
				return true;
			}
		}
	}

	public void simulateTakingPhoto() {
		if (this.canTakePhoto()) {
			// move to the cell
			turnToObstacleForImageRec(2, 0, false);
			turnToObstacleForImageRec(0, -2, false);
			turnToObstacleForImageRec(-2, 0, false);
			turnToObstacleForImageRec(0, 2, false);
			this.exploredMap.getArena()[this.robot.getRow()][this.robot.getCol()].setDetectedForImage(true);
		}
	}

	public void tryTakePhoto() {
		if (this.canTakePhoto()) {
			// move to the cell
			turnToObstacleForImageRec(2, 0, true);
			turnToObstacleForImageRec(0, -2, true);
			turnToObstacleForImageRec(-2, 0, true);
			turnToObstacleForImageRec(0, 2, true);
			this.exploredMap.getArena()[this.robot.getRow()][this.robot.getCol()].setDetectedForImage(true);
		}
	}

	private void turnToObstacleForImageRec(int r_offset, int c_offset, boolean real) {
		int blk_r = this.robot.getRow() + r_offset;
		int blk_c = this.robot.getCol() + c_offset;

		boolean flag = false;

		if (blk_r == 0) {
			for (int i = -1; i < 2; i++) {
				if (this.exploredMap.checkValidCoordinates(blk_r + i, blk_c) && this.exploredMap.getArena()[blk_r + i][blk_c].isObstacle()) {
					flag = true;
					break;
				}
			}
		} else {
			for (int i = -1; i < 2; i++) {
				if (this.exploredMap.checkValidCoordinates(blk_r, blk_c + i) && this.exploredMap.getArena()[blk_r][blk_c + i].isObstacle()) {
					flag = true;
					break;
				}
			}
		}

		if (flag) {
			RobotConst.DIRECTION dir = this.robot.getDir();

			switch (c_offset) {
				case 2:
					this.turnRobot(RobotConst.DIRECTION.NORTH);
					break;
				case -2:
					this.turnRobot(RobotConst.DIRECTION.SOUTH);
					break;
			}

			switch (r_offset) {
				case -2:
					this.turnRobot(RobotConst.DIRECTION.EAST);
					break;
				case 2:
					this.turnRobot(RobotConst.DIRECTION.WEST);
					break;
			}

			if (real) {
				this.client.takePhoto();
			}

			if (this.robot.getDir() != dir) {
				this.turnRobot(dir);
			}
		}
	}


}
