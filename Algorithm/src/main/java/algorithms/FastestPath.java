package algorithms;

import config.MapConst;
import config.RobotConst;
import map.Cell;
import map.Map;
import org.apache.log4j.Logger;
import robot.Robot;
import simulator.Simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class FastestPath {
	private ArrayList<Cell> toVisit;
	private ArrayList<Cell> visited;
	private HashMap<Cell, Cell> parents;
	private Cell curCell;
	private Cell[] neighbours;
	private RobotConst.DIRECTION curDir;
	private double[][] gCosts;
	private Map exploredMap;
	private Map actualMap;
	private int loopCnt;
	private Robot robot;
	private boolean toGoal = false;
	private Exploration exMode;
	private static Logger logger = Logger.getLogger(FastestPath.class);

	public FastestPath(Map exploredMap, Robot robot) {
		this.exploredMap = exploredMap;
		this.robot = robot;
		this.actualMap = null;
		initialise();
	}

	public FastestPath(Map exploredMap, Robot robot, Map actualMap) {
		this.exploredMap = exploredMap;
		this.robot = robot;
		this.actualMap = actualMap;
		initialise();
	}

	public void setExMode(Exploration exMode) {
		this.exMode = exMode;
	}

	private void initialise() {
		this.toVisit = new ArrayList<>();
		this.visited = new ArrayList<>();
		this.parents = new HashMap<>();
		this.neighbours = new Cell[4];
		this.curCell = this.exploredMap.getArena()[this.robot.getRow()][this.robot.getCol()];
		this.curDir = this.robot.getDir();
		this.gCosts = new double[MapConst.NUM_ROWS][MapConst.NUM_COLS];

		for (int r = 0; r < MapConst.NUM_ROWS; r++) {
			for (int c = 0; c < MapConst.NUM_COLS; c++) {
				// initialise all costs of free cells to 0
				// and costs of obstacle cells to infinity
				// will be updated to correct values during the run
				gCosts[r][c] = isExploredAndFree(r, c) ? 0 : RobotConst.INF_COST;
			}
		}

		// add the start position to toVisit array
		this.toVisit.add(curCell);

		// loop count is just for us to keep track of the number of loops ran
		// it is not involved in the algorithm logic
		this.loopCnt = 0;
	}

	private boolean isExploredAndFree(int r, int c) {
		if (!this.exploredMap.checkValidCoordinates(r, c)) return false;
		Cell cell = this.exploredMap.getArena()[r][c];

		return cell.isExplored() && !cell.isObstacle() && !cell.isVirtualWall();
	}

	public ArrayList<RobotConst.MOVE> computeFastestPath(int targetR, int targetC) {
		// declare vars and get target cell
		Cell potentialCell;
		Cell targetCell = this.exploredMap.getArena()[targetR][targetC];
		int curR, curC;
		double oldG, newG;
		Stack<Cell> pathStack;
		this.curDir = robot.getDir();


		// compute fastest path
		logger.info("Computing the fastest path from " + this.curCell.getRow() + ", " + this.curCell.getCol() + " to " + targetR + ", " + targetC);
		do {
			this.loopCnt++;

			// get the min cost cell
			this.curCell = getMinFCostCell(targetR, targetC);

			// update the list
			this.visited.add(curCell);
			this.toVisit.remove(curCell);

			// update the current direction of robot,
			// which is equivalent to the direction of current cell from its parent
			if (this.parents.containsKey(this.curCell))
				this.curDir = getTargetDir(this.parents.get(curCell), this.curCell);

			// termination condition
			if (this.visited.contains(targetCell)) {
				logger.info("Reached target. Fastest path found!");
				pathStack = getPath(targetR, targetC);
				printPathCoords(pathStack);
				return getPathMoves(pathStack, targetR, targetC);
			}

			curR = this.curCell.getRow();
			curC = this.curCell.getCol();

			// top cell
			potentialCell = this.exploredMap.getArena()[curR + 1][curC];
			this.neighbours[0] = isExploredAndFree(curR + 1, curC) ? potentialCell : null;

			// bottom cell
			potentialCell = this.exploredMap.getArena()[curR - 1][curC];
			this.neighbours[1] = isExploredAndFree(curR - 1, curC) ? potentialCell : null;

			// left cell
			potentialCell = this.exploredMap.getArena()[curR][curC - 1];
			this.neighbours[2] = isExploredAndFree(curR, curC - 1) ? potentialCell : null;

			// right cell
			potentialCell = this.exploredMap.getArena()[curR][curC + 1];
			this.neighbours[3] = isExploredAndFree(curR, curC + 1) ? potentialCell : null;

			for (Cell cell : this.neighbours) {
				if (cell != null) {
					// skip if visited
					if (this.visited.contains(cell)) continue;

					// calculate the new g cost for the neighbour
					newG = this.gCosts[this.curCell.getRow()][this.curCell.getCol()] + calGCost(this.curCell, cell, this.curDir);

					// update g cost if the cell is inside toVisit
					if (this.toVisit.contains(cell)) {
						oldG = this.gCosts[cell.getRow()][cell.getCol()];
						if (newG < oldG) {
							this.parents.put(cell, this.curCell);
							this.gCosts[cell.getRow()][cell.getCol()] = newG;
						}
					} else {
						this.parents.put(cell, this.curCell);
						this.gCosts[cell.getRow()][cell.getCol()] = newG;
						this.toVisit.add(cell);
					}
				}
			}
		} while (!toVisit.isEmpty());

		logger.info("No path is found!");
		return null;
	}

	private Cell getMinFCostCell(int targetR, int targetC) {
		double gCost;
		double fCost;
		double minCost = RobotConst.INF_COST;
		Cell minCostCell = null;

		for (int i = this.toVisit.size() - 1; i >= 0; i--) {
			gCost = gCosts[this.toVisit.get(i).getRow()][this.toVisit.get(i).getCol()];
			fCost = gCost + calHCost(this.toVisit.get(i), targetR, targetC);

			if (fCost < minCost) {
				minCost = fCost;
				minCostCell = this.toVisit.get(i);
			}
		}

		return minCostCell;
	}

	private double calHCost(Cell cell, int targetR, int targetC) {
		double moveCost;
		double turnCost = 0;
		// heuristics cost is computed with estimated manhattan distance
		moveCost = (Math.abs(targetR - cell.getRow()) + Math.abs(targetC - cell.getCol()));
		if (moveCost == 0) return 0; // reached target cell

		// turn cost here is an estimated cost as it only involves 1x turn cost
		// and doesnt take into account the exact number of turns required
		// TODO: is the value of this turn cost reasonable?
		if (targetR != cell.getRow() || targetC != cell.getCol()) turnCost = RobotConst.T_COST;

		return moveCost + turnCost;
	}

	private Stack<Cell> getPath(int targetR, int targetC) {
		Stack<Cell> path = new Stack<>();
		Cell cell = this.exploredMap.getArena()[targetR][targetC];

		while (cell != null) {
			path.push(cell);
			cell = parents.get(cell);
		}

		return path;
	}

	private void printPathCoords(Stack<Cell> pathStack) {
		Stack<Cell> pathStackCopy = (Stack<Cell>) pathStack.clone();
		Cell cell;

		logger.info(this.loopCnt + " number of loops has been run.");
		logger.info("Number of steps required: " + (pathStack.size() - 1));
		String path = "";

		while (!pathStackCopy.isEmpty()) {
			cell = pathStackCopy.pop();

			// update graphics cells in map for updates in simulator
			if (this.toGoal && !cell.isWayPoint()) {
				this.exploredMap.getArena()[cell.getRow()][cell.getCol()].setFastestPath(true);
			}

			// print
			path += cell.getRow() + ", " + cell.getCol();
			if (!pathStackCopy.isEmpty()) path += " => ";
		}

		logger.info("Path found: " + path);
	}

	private ArrayList<RobotConst.MOVE> getPathMoves(Stack<Cell> pathStack, int targetR, int targetC) {
		StringBuilder movesStr = new StringBuilder();
		ArrayList<RobotConst.MOVE> moves = new ArrayList<>();
		Cell cell = pathStack.pop();
		RobotConst.DIRECTION targetDir;
		RobotConst.MOVE targetMove;
		Robot simulatedRobot = new Robot(this.robot.getRow(), this.robot.getCol(), false);
		Cell simulatedRobotCell;

		simulatedRobot.setDir(this.robot.getDir());
		this.curDir = this.robot.getDir();

		simulatedRobot.setSpeed(0);
		while (simulatedRobot.getRow() != targetR || simulatedRobot.getCol() != targetC) {
			if (simulatedRobot.getRow() == cell.getRow() && simulatedRobot.getCol() == cell.getCol())
				cell = pathStack.pop();

			simulatedRobotCell = this.exploredMap.getArena()[simulatedRobot.getRow()][simulatedRobot.getCol()];
			targetDir = getTargetDir(simulatedRobotCell, cell);
			if (simulatedRobot.getDir() != targetDir) targetMove = getTargetMove(simulatedRobot, targetDir);
			else targetMove = RobotConst.MOVE.FORWARD;

			simulatedRobot.move(targetMove, exploredMap);
			moves.add(targetMove);
			movesStr.append(RobotConst.MOVE.getMove(targetMove));
		}

		logger.info("Moves to take: " + movesStr.toString());

		return moves;
	}

	private RobotConst.DIRECTION getTargetDir(Cell fromCell, Cell toCell) {
		if (fromCell.getRow() < toCell.getRow()) return RobotConst.DIRECTION.NORTH;
		else if (fromCell.getRow() > toCell.getRow()) return RobotConst.DIRECTION.SOUTH;
		else {
			if (fromCell.getCol() < toCell.getCol()) return RobotConst.DIRECTION.EAST;
			else if (fromCell.getCol() > toCell.getCol()) return RobotConst.DIRECTION.WEST;
		}

		return this.robot.getDir();
	}

	private RobotConst.MOVE getTargetMove(Robot robot, RobotConst.DIRECTION targetDir) {
		RobotConst.DIRECTION robotDir = robot.getDir();

		switch (robotDir) {
			case NORTH:
				switch (targetDir) {
					case EAST:
						return RobotConst.MOVE.TURN_RIGHT;
					case SOUTH:
					case WEST:
						return RobotConst.MOVE.TURN_LEFT;
				}
				break;
			case EAST:
				switch (targetDir) {
					case SOUTH:
						return RobotConst.MOVE.TURN_RIGHT;
					case WEST:
					case NORTH:
						return RobotConst.MOVE.TURN_LEFT;
				}
				break;
			case SOUTH:
				switch (targetDir) {
					case WEST:
						return RobotConst.MOVE.TURN_RIGHT;
					case NORTH:
					case EAST:
						return RobotConst.MOVE.TURN_LEFT;
				}
				break;
			case WEST:
				switch (targetDir) {
					case NORTH:
						return RobotConst.MOVE.TURN_RIGHT;
					case EAST:
					case SOUTH:
						return RobotConst.MOVE.TURN_LEFT;
				}
		}

		return RobotConst.MOVE.FORWARD;
	}

	private double calGCost(Cell fromCell, Cell toCell, RobotConst.DIRECTION curDir) {
		double moveCost = RobotConst.M_COST;
		double turnCost;
		RobotConst.DIRECTION targetDir;

		targetDir = getTargetDir(fromCell, toCell);
		turnCost = getTurnCost(curDir, targetDir);

		// 1x movement cost as the neighbouring cell is only 1 move away
		// turn cost is the exact number of turns required
		return moveCost + turnCost;
	}

	private double getTurnCost(RobotConst.DIRECTION curDir, RobotConst.DIRECTION targetDir) {
		double turnCost = RobotConst.T_COST;
		int numTurns = Math.abs(curDir.ordinal() - targetDir.ordinal());
		if (numTurns > 2) numTurns %= 2; // only need to turn 2 times at most to get to another target direction

		return numTurns * turnCost;
	}

	public void executeMoves(ArrayList<RobotConst.MOVE> moves, Robot robot) {
		logger.info("Started fastest path!");
		if (!robot.isRealRobot()) {
			// simulation mode
			RobotConst.MOVE m;
			for (int _m = 0; _m < moves.size(); _m++) {
				m = moves.get(_m);
				if (m == RobotConst.MOVE.FORWARD) {
					if (!canMoveForward(robot)) {
						// TODO: should recompute fastest path?
						logger.info("Early termination of fastestpath algorithm");
					}
				}

				robot.move(m, this.exploredMap);
				this.exploredMap.repaint();

				// as there might be phantom blocks on fastest path
				// better re-sense at every move
				robot.updateSensorsDirections();
				robot.simulateSense(this.exploredMap, this.actualMap);
				this.exploredMap.repaint();

				// get area coverage for exploration
				if (this.exMode != null) {
					double areaCoveredPc = (new Double(this.exMode.getAreaExplored()) / MapConst.NUM_CELLS) * 100;
					String areaCoveredPcStr = String.format("%.2f %%", areaCoveredPc);
					Simulator.areaCoveredLbl.setText(areaCoveredPcStr);
				}
			}
		} else {
			int forwardCnt = 0;
			for (RobotConst.MOVE m : moves) {
				if (m == RobotConst.MOVE.FORWARD) {
					forwardCnt++;
					// not making move first, until threshold of 9 is reached
					if (forwardCnt == RobotConst.FORWARDLIMIT) {
						robot.moveForward(forwardCnt, this.exploredMap);
						forwardCnt = 0;
						this.exploredMap.repaint();
					}
				} else if (m == RobotConst.MOVE.TURN_RIGHT || m == RobotConst.MOVE.TURN_LEFT) {
					if (forwardCnt > 0) {
						robot.moveForward(forwardCnt, this.exploredMap);
						forwardCnt = 0;
						this.exploredMap.repaint();
					}

					robot.move(m, this.exploredMap);
					this.exploredMap.repaint();
				}
			}

			if (forwardCnt > 0) {
				robot.moveForward(forwardCnt, this.exploredMap);
				this.exploredMap.repaint();
			}
		}
	}

	private boolean canMoveForward(Robot robot) {
		int r = robot.getRow();
		int c = robot.getCol();

		switch (robot.getDir()) {
			case NORTH:
				return !this.exploredMap.getArena()[r + 2][c - 1].isObstacle()
						&& !this.exploredMap.getArena()[r + 2][c].isObstacle()
						&& !this.exploredMap.getArena()[r + 2][c - 1].isObstacle();
			case EAST:
				return !this.exploredMap.getArena()[r - 1][c + 2].isObstacle()
						&& !this.exploredMap.getArena()[r][c + 2].isObstacle()
						&& !this.exploredMap.getArena()[r + 1][c + 2].isObstacle();
			case SOUTH:
				return !this.exploredMap.getArena()[r - 2][c - 1].isObstacle()
						&& !this.exploredMap.getArena()[r - 2][c].isObstacle()
						&& !this.exploredMap.getArena()[r - 2][c - 1].isObstacle();
			case WEST:
				return !this.exploredMap.getArena()[r - 1][c - 2].isObstacle()
						&& !this.exploredMap.getArena()[r][c - 2].isObstacle()
						&& !this.exploredMap.getArena()[r + 1][c - 2].isObstacle();
			default:
				return false;
		}
	}

	public boolean isToGoal() {
		return this.toGoal;
	}

	public void setToGoal(boolean toGoal) {
		this.toGoal = toGoal;
	}

}
