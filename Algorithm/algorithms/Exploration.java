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
	private long startT;
	private long endT;
	public static int prevCalibrateTurns;
	private boolean caliMode;
	private int caliLimit = 6;
	public static boolean overwritten = false;
	public static boolean expEnded = false;
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

	public long getStartT() {
		return this.startT;
	}

	public void setStartT(long startT) {
		this.startT = startT;
	}

	public long getEndT() {
		return this.endT;
	}

	public void setEndT(long endT) {
		this.endT = endT;
	}

	public int getPrevCalibrateTurns() {
		return this.prevCalibrateTurns;
	}

	public void setPrevCalibrateTurns(int numTurns) {
		this.prevCalibrateTurns = numTurns;
	}

	public void run() {
		if (this.robot.isRealRobot()) {
			System.out.println("Start calibration....");
			this._initialCalibration();

			while (true) {
				System.out.println("Waiting for EX_START message from Android....");
				String msg = CommunicationManager.getCommMgr().receiveMsg();
				if (msg != null && msg.split(CommunicationManager.SEPARATOR)[0].equals(CommunicationManager.EX_START))
					break;
			}

			System.out.println("Start exploration....");

			this.startT = System.currentTimeMillis();
			this.endT = this.startT + (timeLimit * 1000);

		} else {
			System.out.println("Start exploration....");

			this.startT = System.currentTimeMillis();
			this.endT = this.startT + (timeLimit * 1000);
		}

		CommunicationManager.getCommMgr().sendMsg("S", CommunicationManager.ROBOT_START);
		robotSenseAndMapRepaint();

		this.areaExplored = this.exploredMap.calAreaExplored();
		System.out.println("Explored Area: " + areaExplored);
		System.out.println("Starting position: " + this.robot.getRow() + ", " + this.robot.getCol());

		_explore();
	}

	private void robotSenseAndMapRepaint() {
		this.robot.updateSensorsDirections();

		if (this.robot.isRealRobot()) this.robot.sense(this.exploredMap);
		else this.robot.simulateSense(this.exploredMap, this.actualMap);

		double areaCoveredPc = (new Double(this.areaExplored) / MapConst.NUM_CELLS) * 100;
		String areaCoveredPcStr = String.format("%.2f %%", areaCoveredPc);
		Simulator.areaCoveredLbl.setText(areaCoveredPcStr);

		this.exploredMap.repaint();
	}

	private void _explore() {
		do {
			_nextMove();

			this.areaExplored = this.exploredMap.calAreaExplored();
			System.out.println("Area explored: " + this.areaExplored);

			// termination condition
			if ((this.robot.getRow() == MapConst.START_ROW && this.robot.getCol() == MapConst.START_COL)
					&& ((new Double(this.areaExplored) / MapConst.NUM_CELLS) * 100 > 60)
					|| (System.currentTimeMillis() > (this.endT - 0.1 * this.timeLimit * 1000))) {
				break;
			}

		} while (this.areaExplored < this.coverageLimit && System.currentTimeMillis() < this.endT);

		// explore unexplored cells
		boolean state = true;
		while (state && this.areaExplored < this.coverageLimit * 0.99 && System.currentTimeMillis() < (this.endT - 0.15 * this.timeLimit * 1000)) {
			state = _exploreUnexplored();
			this.areaExplored = this.exploredMap.calAreaExplored();
			System.out.println("Area explored: " + this.areaExplored);
		}

		if (this.robot.isRealRobot()
				|| (!this.robot.isRealRobot() && this.areaExplored <= this.coverageLimit && System.currentTimeMillis() < (this.endT - 0.1 * this.timeLimit * 1000))) {
			_goToStart();
        	/*do {
        		if (this.robot.getRow() == MapConst.START_ROW && this.robot.getCol() == MapConst.START_COL) 
        			break;
        		_nextMove();
        	} while (true);*/
		}
	}

	private void _nextMove() {
		int r = this.robot.getRow();
		int c = this.robot.getCol();
		if (_isRightFree(this.robot.getRow(), this.robot.getCol()) && _isLeftFree(this.robot.getRow(), this.robot.getCol()) &&
				(this.robot.getDir().equals(RobotConst.DIRECTION.NORTH) || this.robot.getDir().equals(RobotConst.DIRECTION.SOUTH)) &&
				overwritten) {
			_moveRobot(RobotConst.MOVE.TURN_LEFT);
			if (_isFrontFree(this.robot.getRow(), this.robot.getCol())) {
				_moveRobot(RobotConst.MOVE.FORWARD);
			}
			RFCount = 0;
		} else if (RFCount > 4) {
			if (this.robot.getRow() < MapConst.NUM_COLS / 2)
				_turnRobot(RobotConst.DIRECTION.EAST);
			else
				_turnRobot(RobotConst.DIRECTION.WEST);
			while (_isFrontFree(this.robot.getRow(), this.robot.getCol())) {
				_moveRobot(RobotConst.MOVE.FORWARD);
			}
//            if (this.robot.getRow() < MapConst.NUM_COLS / 2)
//                _turnRobot(RobotConst.DIRECTION.SOUTH);
//            else
//                _turnRobot(RobotConst.DIRECTION.NORTH);
//            while (_isFrontFree(this.robot.getRow(), this.robot.getCol())) {
//                _moveRobot(RobotConst.MOVE.FORWARD);
//            }
			_turnRobot(RobotConst.DIRECTION.getNextAntiClk(this.robot.getDir()));
			RFCount = 0;
		} else if (_isRightFree(this.robot.getRow(), this.robot.getCol())) { // turn right and move forward
			_moveRobot(RobotConst.MOVE.TURN_RIGHT);
			if (_isFrontFree(this.robot.getRow(), this.robot.getCol())) {
				_moveRobot(RobotConst.MOVE.FORWARD);
				RFCount++;
			}
		} else if (_isFrontFree(this.robot.getRow(), this.robot.getCol())) { // move forward
			_moveRobot(RobotConst.MOVE.FORWARD);
			RFCount = 0;
		} else if (_isLeftFree(this.robot.getRow(), this.robot.getCol())) { // turn left and move forward
			_moveRobot(RobotConst.MOVE.TURN_LEFT);
			if (_isFrontFree(this.robot.getRow(), this.robot.getCol())) {
				_moveRobot(RobotConst.MOVE.FORWARD);
			}
			RFCount = 0;
		} else { // u turn
			_moveRobot(RobotConst.MOVE.TURN_LEFT);
			_moveRobot(RobotConst.MOVE.TURN_LEFT);
			RFCount = 0;
		}
		overwritten = false;

	}

	private void _moveRobot(RobotConst.MOVE m) {
		this.robot.move(m, this.exploredMap);
		if (m == RobotConst.MOVE.CALIBRATE)
			this._waitCali();
		this.exploredMap.repaint();
		robotSenseAndMapRepaint();

		if (this.robot.isRealRobot() && !this.caliMode && !this.expEnded) {

			this.caliMode = true;
//            if (_canCalibrate(this.robot.getDir()) && (this.prevCalibrateTurns + 2 >= this.caliLimit)) {
			if (_canCalibrate(this.robot.getDir())) {
				if (this.prevCalibrateTurns >= 3 &&
						!(this._canCalibrate(RobotConst.DIRECTION.getNextClk(this.robot.getDir())) &&
								this._canCalibrate(RobotConst.DIRECTION.getNextAntiClk(this.robot.getDir()))))
					// if the robot is now at a corner, turn and calibrate too. This helps to ensure the robot's orientation
					// the threshold of 3 is to prevent the bot from calibrating too much
					this._turnAndCalibrate();
				this._moveRobot(RobotConst.MOVE.CALIBRATE);
				this.prevCalibrateTurns = 0;
			} else {
				this.prevCalibrateTurns++;
				// if never calibrate for more than or equals to caliLimit steps
				// shall force the robot to calibrate
				if (this.prevCalibrateTurns >= this.caliLimit) {
					this._turnAndCalibrate();
				}
			}

			this.caliMode = false;
		}
	}

	private void _turnAndCalibrate() {
		RobotConst.DIRECTION targetDir;
		RobotConst.DIRECTION curDir;
		targetDir = _getCalibrateDir();
		if (targetDir != null) {
			this.prevCalibrateTurns = 0;
			curDir = this.robot.getDir();
			_turnRobot(targetDir);
			_moveRobot(RobotConst.MOVE.CALIBRATE);
			_turnRobot(curDir);
		}
	}

	private void _waitCali() {
		while (true) {
			if (CommunicationManager.getCommMgr().receiveMsg().split(CommunicationManager.SEPARATOR)[0].equals(CommunicationManager.C_DONE)) {
				break;
			}
		}
	}

	private boolean _canCalibrate(RobotConst.DIRECTION targetDir) {
		int r = this.robot.getRow();
		int c = this.robot.getCol();

		// using the 3 front SR sensors to do the calibration
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

	private RobotConst.DIRECTION _getCalibrateDir() {
		RobotConst.DIRECTION curDir = this.robot.getDir();
		RobotConst.DIRECTION targetDir;

		targetDir = RobotConst.DIRECTION.getNextAntiClk(curDir);
		if (this._canCalibrate(targetDir)) return targetDir;

		targetDir = RobotConst.DIRECTION.getNextClk(curDir);
		if (this._canCalibrate(targetDir)) return targetDir;

		return null;
	}

	private void _turnRobot(RobotConst.DIRECTION targetDir) {
		int numMovesNeeded = Math.abs(this.robot.getDir().ordinal() - targetDir.ordinal());
		if (numMovesNeeded == 0) return;
		if (numMovesNeeded > 2) numMovesNeeded = numMovesNeeded % 2;

		if (numMovesNeeded == 1) {
			if (RobotConst.DIRECTION.getNextClk(this.robot.getDir()) == targetDir)
				_moveRobot(RobotConst.MOVE.TURN_RIGHT);
			else
				_moveRobot(RobotConst.MOVE.TURN_LEFT);
		} else {
			_moveRobot(RobotConst.MOVE.TURN_RIGHT);
			_moveRobot(RobotConst.MOVE.TURN_RIGHT);
		}
	}

	private boolean _isRightFree(int r, int c) {
		switch (this.robot.getDir()) {
			case NORTH:
				return _isEastFree(r, c);
			case EAST:
				return _isSouthFree(r, c);
			case SOUTH:
				return _isWestFree(r, c);
			case WEST:
				return _isNorthFree(r, c);
		}

		return false;
	}

	private boolean _isLeftFree(int r, int c) {
		switch (this.robot.getDir()) {
			case NORTH:
				return _isWestFree(r, c);
			case EAST:
				return _isNorthFree(r, c);
			case SOUTH:
				return _isEastFree(r, c);
			case WEST:
				return _isSouthFree(r, c);
		}

		return false;
	}

	private boolean _isFrontFree(int r, int c) {
		switch (this.robot.getDir()) {
			case NORTH:
				return _isNorthFree(r, c);
			case EAST:
				return _isEastFree(r, c);
			case SOUTH:
				return _isSouthFree(r, c);
			case WEST:
				return _isWestFree(r, c);
		}

		return false;
	}

	private boolean _isNorthFree(int r, int c) {
		return _isExploredAndNotObstacle(r + 1, c - 1)
				&& _isExploredAndFree(r + 1, c)
				&& _isExploredAndNotObstacle(r + 1, c + 1);
	}

	private boolean _isEastFree(int r, int c) {
		return _isExploredAndNotObstacle(r + 1, c + 1)
				&& _isExploredAndFree(r, c + 1)
				&& _isExploredAndNotObstacle(r - 1, c + 1);
	}

	private boolean _isSouthFree(int r, int c) {
		return _isExploredAndNotObstacle(r - 1, c - 1)
				&& _isExploredAndFree(r - 1, c)
				&& _isExploredAndNotObstacle(r - 1, c + 1);
	}

	private boolean _isWestFree(int r, int c) {
		return _isExploredAndNotObstacle(r + 1, c - 1)
				&& _isExploredAndFree(r, c - 1)
				&& _isExploredAndNotObstacle(r - 1, c - 1);
	}

	private boolean _isExploredAndNotObstacle(int r, int c) {
		if (!this.exploredMap.checkValidCoordinates(r, c)) return false;
		Cell cell = this.exploredMap.getArena()[r][c];

		return cell.isExplored() && !cell.isObstacle();
	}

	private boolean _isExploredAndFree(int r, int c) {
		if (!this.exploredMap.checkValidCoordinates(r, c)) return false;
		Cell cell = this.exploredMap.getArena()[r][c];

		return cell.isExplored() && !cell.isObstacle() && !cell.isVirtualWall();
	}

	private boolean _exploreUnexplored() {
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
								if (_isExploredAndFree(cell.getRow() + i, cell.getCol() + j)) {
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
								if (_isExploredAndFree(cell.getRow() + i, cell.getCol() + j)) {
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

	private void _goToStart() {
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
		System.out.println("Time taken: " + (System.currentTimeMillis() - this.startT) / 1000 + "s");

		// Calibrate
		if (robot.isRealRobot()) {
			this.expEnded = true;
			this._finalCalibration();
		}

		_turnRobot(RobotConst.DIRECTION.NORTH);
	}

	private void _initialCalibration() {
		this.robot.move(RobotConst.MOVE.TURN_LEFT, false, this.exploredMap);
		CommunicationManager.getCommMgr().receiveMsg();
		this.robot.move(RobotConst.MOVE.CALIBRATE, false, this.exploredMap);
		this._waitCali();
		CommunicationManager.getCommMgr().receiveMsg();
		this.robot.move(RobotConst.MOVE.TURN_LEFT, false, this.exploredMap);
		CommunicationManager.getCommMgr().receiveMsg();
		this.robot.move(RobotConst.MOVE.CALIBRATE, false, this.exploredMap);
		this._waitCali();
		CommunicationManager.getCommMgr().receiveMsg();
		this.robot.move(RobotConst.MOVE.TURN_LEFT, false, this.exploredMap);
	}

	private void _finalCalibration() {
		if (Exploration.expEnded)
			CommunicationManager.getCommMgr().sendMsg("", CommunicationManager.EX_DONE);
		String msg;
		while (true) {
			msg = CommunicationManager.getCommMgr().receiveMsg();
			String[] msgArr = msg.split(CommunicationManager.SEPARATOR);
			if (msgArr[0].equals(CommunicationManager.WAY_POINT)) {
				break;
			}
		}
		_turnRobot(RobotConst.DIRECTION.WEST);
		this.robot.move(RobotConst.MOVE.CALIBRATE, false, this.exploredMap);
		this._waitCali();
		CommunicationManager.getCommMgr().receiveMsg();

		_turnRobot(RobotConst.DIRECTION.SOUTH);
		this.robot.move(RobotConst.MOVE.CALIBRATE, false, this.exploredMap);
		this._waitCali();
		CommunicationManager.getCommMgr().receiveMsg();

		_turnRobot(RobotConst.DIRECTION.WEST);
		this.robot.move(RobotConst.MOVE.CALIBRATE, false, this.exploredMap);
		this._waitCali();
		CommunicationManager.getCommMgr().receiveMsg();
	}
}
