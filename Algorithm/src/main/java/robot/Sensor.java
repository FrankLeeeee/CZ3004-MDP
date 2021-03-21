package robot;

import algorithms.Exploration;
import config.RobotConst;
import map.Map;
import org.apache.log4j.Logger;
import simulator.Simulator;

public class Sensor {
	private final String id;
	private final int lRange;
	private final int uRange;

	private int row;
	private int col;
	private RobotConst.DIRECTION dir;

	private static Logger logger = Logger.getLogger(Sensor.class);

	public Sensor(String id, int lRange, int uRange, int row, int col, RobotConst.DIRECTION dir) {
		this.id = id;
		this.lRange = lRange;
		this.uRange = uRange;
		this.row = row;
		this.col = col;
		this.dir = dir;
	}

	public String getId() {
		return this.id;
	}

	public int getlRange() {
		return this.lRange;
	}

	public int getuRange() {
		return this.uRange;
	}

	public int getRow() {
		return this.row;
	}

	public int getCol() {
		return this.col;
	}

	public RobotConst.DIRECTION getDir() {
		return this.dir;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public void setDir(RobotConst.DIRECTION dir) {
		this.dir = dir;
	}

	/**
	 * This function simulate the process of
	 * sensing and returning number of cells to the nearest detected obstacle from current position.
	 * Return -1 if no obstacle is detected.
	 *
	 * @param exploredMap is the map explored so far
	 * @param actualMap   is the real map of the arena
	 */
	public void simulateSense(Map exploredMap, Map actualMap) {

		switch (this.dir) {
			case NORTH:
				simulateSense(exploredMap, actualMap, 1, 0);
				break;
			case EAST:
				simulateSense(exploredMap, actualMap, 0, 1);
				break;
			case SOUTH:
				simulateSense(exploredMap, actualMap, -1, 0);
				break;
			case WEST:
				simulateSense(exploredMap, actualMap, 0, -1);
				break;
		}

	}

	private void simulateSense(Map exploredMap, Map actualMap, int rowInc, int colInc) {
		// Check if starting point is valid for sensors with lRange > 1.
		for (int i = 1; i < this.lRange; i++) {
			int r = this.row + (rowInc * i);
			int c = this.col + (colInc * i);
			if (!exploredMap.checkValidCoordinates(r, c)) return;
			if (actualMap.getArena()[r][c].isObstacle()) return;
			if (!exploredMap.getArena()[r][c].isExplored()) return;
		}

		// Check if anything is detected by the sensor and return that value.
		for (int i = this.lRange; i <= this.uRange; i++) {
			int r = this.row + (rowInc * i);
			int c = this.col + (colInc * i);
			if (!exploredMap.checkValidCoordinates(r, c)) return;
			exploredMap.getArena()[r][c].setExplored(true);

			// TO simulate phantom block
//			if (Math.random() > 0.99) {
//				exploredMap.setObstacleCell(r, c, true);
//
//				if (Simulator.task == "IMG") {
//					updateIfCanTakePhoto(exploredMap, r, c);
//				}
//				exploredMap.repaint();
//				break;
//			}
//
//			if (exploredMap.getArena()[r][c].isObstacle() && !actualMap.getArena()[r][c].isObstacle()) {
//				exploredMap.setObstacleCell(r, c, false);
//			}

			// the following simulates the sensing,
			// and update the exploredMap accordingly
			if (actualMap.getArena()[r][c].isObstacle()) {
				exploredMap.setObstacleCell(r, c, true);

				if (Simulator.task == "IMG") {
					updateIfCanTakePhoto(exploredMap, r, c);
				}
				exploredMap.repaint();
				break;
			}

		}
	}

	public void sense(Map exploredMap, int sensorVal) {
		switch (this.dir) {
			case NORTH:
				sense(exploredMap, sensorVal, 1, 0);
				break;
			case EAST:
				sense(exploredMap, sensorVal, 0, 1);
				break;
			case SOUTH:
				sense(exploredMap, sensorVal, -1, 0);
				break;
			case WEST:
				sense(exploredMap, sensorVal, 0, -1);
				break;
		}

	}

	public void sense(Map exploredMap, int sensorVal, int rowInc, int colInc) {
		// to avoid negative value
		if (sensorVal < 0) {
			sensorVal = this.uRange + 1;
		}

//		if (sensorVal == 0) return;  // return value for LR sensor if obstacle before lRange

		// If above fails, check if starting point is valid for sensors with lowerRange > 1.
		for (int i = 1; i < this.lRange; i++) {
			int r = this.row + (rowInc * i);
			int c = this.col + (colInc * i);

			if (!exploredMap.checkValidCoordinates(r, c)) return;
			if (exploredMap.getArena()[r][c].isObstacle()) return;
			if (this.id.equals("LRL") && !exploredMap.getArena()[r][c].isExplored()) return;
		}

		// Update map according to sensor's value.
		for (int i = this.lRange; i <= this.uRange; i++) {
			int r = this.row + (rowInc * i);
			int c = this.col + (colInc * i);

			// TODO: check on this
			if (!exploredMap.checkValidCoordinates(r, c)) return;
//			if (!exploredMap.checkValidCoordinates(r, c)) continue;


			// update exploredMap
			if (sensorVal == i) {
				// TODO: check on this
//				if (exploredMap.getArena()[r][c].isExplored() && !exploredMap.getArena()[r][c].isObstacle() && id.startsWith("LR")) {
//					return;
//				}
				exploredMap.getArena()[r][c].setExplored(true);
				exploredMap.setObstacleCell(r, c, true);

				if (Simulator.task == "IMG") {
					updateIfCanTakePhoto(exploredMap, r, c);
				}

				exploredMap.repaint();
				return;
			}

			exploredMap.getArena()[r][c].setExplored(true);
			exploredMap.repaint();

			// As long range sensors may have wrongly detected cells further away as obstacles,
			// we shall override previous obstacle value if front short range sensors detects no obstacle.
			if (exploredMap.getArena()[r][c].isObstacle()) {
				if (id.startsWith("SRF") || id.startsWith("SRR") && i == this.lRange) {
					if (Exploration.prevCalibrateTurns < 6) {
						System.out.println("Overwriting");
						exploredMap.setObstacleCell(r, c, false);
						exploredMap.repaint();
						Exploration.overwritten = true;
					}
				} else
					return;
			}


//				if (id.startsWith("SRF") || id.startsWith("SRR") && i == this.lRange) {
//					if (Exploration.prevCalibrateTurns < 6) {
//
////						logger.info("Overwriting");
//						exploredMap.setObstacleCell(r, c, false);
//
//						if (Simulator.task == "IMG")
//							removeCanTakePhoto(exploredMap, r, c);
//						Exploration.overwritten = true;
//					}
//				} else
//					return;
//			}
		}
	}

	public void updateSensor(int r, int c, RobotConst.DIRECTION dir) {
		this.row = r;
		this.col = c;
		this.dir = dir;
	}

	public void updateIfCanTakePhoto(Map exploredMap, int r, int c) {
		if (this.uRange < 3) {
			switch (this.dir) {
				case NORTH:
					setCellToTakePhoto(exploredMap, r - 2, c);
					setCellToTakePhoto(exploredMap, r - 2, c - 1);
					setCellToTakePhoto(exploredMap, r - 2, c + 1);
					break;
				case SOUTH:
					setCellToTakePhoto(exploredMap, r + 2, c);
					setCellToTakePhoto(exploredMap, r + 2, c - 1);
					setCellToTakePhoto(exploredMap, r + 2, c + 1);
					break;
				case EAST:
					setCellToTakePhoto(exploredMap, r, c - 2);
					setCellToTakePhoto(exploredMap, r + 1, c - 2);
					setCellToTakePhoto(exploredMap, r - 1, c - 2);
					break;
				case WEST:
					setCellToTakePhoto(exploredMap, r, c + 2);
					setCellToTakePhoto(exploredMap, r + 1, c + 2);
					setCellToTakePhoto(exploredMap, r - 1, c + 2);
					break;
			}
		}
	}

	private void setCellToTakePhoto(Map exploredMap, int r, int c) {
		if (exploredMap.checkValidCoordinates(r, c) &&
				exploredMap.getArena()[r][c].isExplored() &&
				(!exploredMap.isObstacleOrWall(r, c))) {
			exploredMap.getArena()[r][c].setCanTakePhoto(true);
		}
	}

//	public void removeCanTakePhoto(Map exploredMap, int r, int c) {
//		if (exploredMap.checkValidCoordinates(r + 1, c)) {
//			exploredMap.getArena()[r + 1][c].setCanTakePhoto(false);
//		}
//
//		if (exploredMap.checkValidCoordinates(r - 1, c)) {
//			exploredMap.getArena()[r - 1][c].setCanTakePhoto(false);
//		}
//
//		if (exploredMap.checkValidCoordinates(r, c + 1)) {
//			exploredMap.getArena()[r][c + 1].setCanTakePhoto(false);
//		}
//
//		if (exploredMap.checkValidCoordinates(r, c - 1)) {
//			exploredMap.getArena()[r][c - 1].setCanTakePhoto(false);
//		}
//	}
}
