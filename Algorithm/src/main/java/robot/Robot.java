package robot;

import communication.GrpcClient;
import config.MapConst;
import config.RobotConst;
import grpc.GrpcService;
import map.Map;
import org.apache.log4j.Logger;
import simulator.Simulator;

import java.util.concurrent.TimeUnit;

public class Robot {
	// SR: short range, LR: long range
	private final Sensor SRFrontLeft;
	private final Sensor SRFrontCenter;
	private final Sensor SRFrontRight;
	private final Sensor SRRight1;
	private final Sensor SRRight2;
	private final Sensor LRLeft;
	private final boolean isRealRobot;

	private int row;
	private int col;
	private RobotConst.DIRECTION dir;
	private int speed;
	private GrpcClient client;
	private boolean hasReachedGoal;
	private boolean hasReturnedStart = false;

	private static Logger logger = Logger.getLogger(Robot.class);

	public Robot(int row, int col, boolean isRealRobot) {
		this.row = row;
		this.col = col;
		this.isRealRobot = isRealRobot;

		this.dir = RobotConst.START_DIR;
		this.speed = RobotConst.SPEED;

		this.SRFrontLeft = new Sensor("SRFL", RobotConst.SENSOR_SR_LOW, RobotConst.SENSOR_SR_HIGH, this.row + 1, this.col - 1, this.dir);
		this.SRFrontCenter = new Sensor("SRFC", RobotConst.SENSOR_SR_LOW, RobotConst.SENSOR_SR_HIGH, this.row + 1, this.col, this.dir);
		this.SRFrontRight = new Sensor("SRFR", RobotConst.SENSOR_SR_LOW, RobotConst.SENSOR_SR_HIGH, this.row + 1, this.col + 1, this.dir);
		this.SRRight1 = new Sensor("SRR1", RobotConst.SENSOR_SR_LOW, RobotConst.SENSOR_SR_HIGH, this.row + 1, this.col + 1, updateDir(RobotConst.MOVE.TURN_RIGHT));
		this.SRRight2 = new Sensor("SRR2", RobotConst.SENSOR_SR_LOW, RobotConst.SENSOR_SR_HIGH, this.row - 1, this.col + 1, updateDir(RobotConst.MOVE.TURN_RIGHT));
		this.LRLeft = new Sensor("LRL", RobotConst.SENSOR_LR_LOW, RobotConst.SENSOR_LR_HIGH, this.row + 1, this.col + 1, updateDir(RobotConst.MOVE.TURN_LEFT));

		this.client = GrpcClient.getInstance();
	}

	public Sensor getSRFrontLeft() {
		return this.SRFrontLeft;
	}

	public Sensor getSRFrontCenter() {
		return this.SRFrontCenter;
	}

	public Sensor getSRFrontRight() {
		return this.SRFrontRight;
	}

	public Sensor getSRRight1() {
		return this.SRRight1;
	}

	public Sensor getSRRight2() {
		return this.SRRight2;
	}

	public Sensor getLRLeft() {
		return this.LRLeft;
	}

	public boolean isRealRobot() {
		return this.isRealRobot;
	}

	public int getRow() {
		return this.row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return this.col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public RobotConst.DIRECTION getDir() {
		return this.dir;
	}

	public void setDir(RobotConst.DIRECTION dir) {
		this.dir = dir;
	}

	public int getSpeed() {
		return this.speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public boolean hasReachedGoal() {
		return this.hasReachedGoal;
	}

	public void setHasReachedGoal(boolean hasReachedGoal) {
		this.hasReachedGoal = hasReachedGoal;
	}

	public boolean hasReturnedStart() {
		return this.hasReturnedStart;
	}

	public void setHasReturnedStart(boolean hasReturnedStart) {
		this.hasReturnedStart = hasReturnedStart;
	}

	public void move(RobotConst.MOVE m, Map exploredMap) {
		move(m, exploredMap, true, true);
	}

	public void move(RobotConst.MOVE m, Map exploredMap, boolean doSense, boolean sendToAndroidFlag) {
		// if in simulation mode
		if (!this.isRealRobot) {
			try {
				if (this.speed != 0) TimeUnit.MILLISECONDS.sleep((long) 1000.0 / this.speed);
				else TimeUnit.MILLISECONDS.sleep(0);
			} catch (InterruptedException e) {
				logger.info("Robot movement error: " + e);
			}
		}

		switch (m) {
			case FORWARD:
				switch (this.dir) {
					case NORTH:
						this.row++;
						break;
					case EAST:
						this.col++;
						break;
					case SOUTH:
						this.row--;
						break;
					case WEST:
						this.col--;
						break;
				}
				break;
			case TURN_LEFT:
			case TURN_RIGHT:
				this.dir = updateDir(m);
				break;
			case CALIBRATE:
				break;
			default:
				logger.info("Error in movement!");
				break;
		}

		if (this.isRealRobot) sendMove(m, exploredMap, sendToAndroidFlag, doSense);
//		else logger.info("Move: " + RobotConst.MOVE.getMove(m));


		// check if robot has reached goal
		if (this.row == MapConst.GOAL_ROW && this.col == MapConst.GOAL_COL) this.hasReachedGoal = true;

	}

	private RobotConst.DIRECTION updateDir(RobotConst.MOVE m) {
		return (m == RobotConst.MOVE.TURN_LEFT) ? RobotConst.DIRECTION.getNextAntiClk(this.dir) : RobotConst.DIRECTION.getNextClk(this.dir);
	}

	private void sendMove(RobotConst.MOVE m, Map exploredMap, boolean sendMoveToAndroidFlag, boolean doSense) {
		if (m.equals(RobotConst.MOVE.CALIBRATE)) {
			Boolean response = client.calibrate();
			assert response : "Calibration returns status 0";
		} else {
			java.util.Map<Integer, Float> response = client.moveRobot(m, 1);
			assert response.size() == 6 : "Not all sensor data are obtained";

			if (doSense) {
				this.updateSensorsDirections();
				processSensorValue(response, exploredMap);
			}

		}

		if (sendMoveToAndroidFlag) {
			sendDataToAndroid(exploredMap);
		}
	}

	public void moveForward(int numSteps, Map exploredMap) {
		this.moveForward(numSteps, exploredMap, true, true);
	}

	public void moveForward(int numSteps, Map exploredMap, boolean sendToAndroid, boolean doSense) {
		if (numSteps == 1) {
			move(RobotConst.MOVE.FORWARD, exploredMap, false, false);
		} else {
			java.util.Map<Integer, Float> response = client.moveRobot(RobotConst.MOVE.FORWARD, numSteps);
			assert response.size() == 6 : "Not all sensor data are obtained";

			if (doSense) {
				this.updateSensorsDirections();
				processSensorValue(response, exploredMap);
			}

			switch (this.dir) {
				case NORTH:
					this.row += numSteps;
					break;
				case EAST:
					this.col += numSteps;
					break;
				case SOUTH:
					this.row -= numSteps;
					break;
				case WEST:
					this.col -= numSteps;
					break;
			}

			logger.info(this.getRow() + ", " + this.getCol() + " " + this.getDir());

			if (sendToAndroid) {
				sendDataToAndroid(exploredMap);
			}
		}
	}

	public void sendDataToAndroid(Map exploredMap) {
		// pass map info to android
		if (Simulator.task == "EXP") {
			client.setMap(exploredMap);
		}

		// pass robot position to android
		GrpcService.Position.Direction dir;
		switch (this.dir) {
			case NORTH:
				dir = GrpcService.Position.Direction.NORTH;
				break;
			case EAST:
				dir = GrpcService.Position.Direction.EAST;
				break;

			case SOUTH:
				dir = GrpcService.Position.Direction.SOUTH;
				break;

			case WEST:
				dir = GrpcService.Position.Direction.WEST;
				break;

			default:
				dir = GrpcService.Position.Direction.WEST;
				break;
		}

		// TODO: not needed anymore
//		client.setPosition(this.col, this.row, dir);
	}

	public void updateSensorsDirections() {
		switch (this.dir) {
			case NORTH:
				this.SRFrontLeft.updateSensor(this.row + 1, this.col - 1, this.dir);
				this.SRFrontCenter.updateSensor(this.row + 1, this.col, this.dir);
				this.SRFrontRight.updateSensor(this.row + 1, this.col + 1, this.dir);
				this.SRRight1.updateSensor(this.row + 1, this.col + 1, updateDir(RobotConst.MOVE.TURN_RIGHT));
				this.SRRight2.updateSensor(this.row - 1, this.col + 1, updateDir(RobotConst.MOVE.TURN_RIGHT));
				this.LRLeft.updateSensor(this.row + 1, this.col + 1, updateDir(RobotConst.MOVE.TURN_LEFT));
				break;
			case EAST:
				this.SRFrontLeft.updateSensor(this.row + 1, this.col + 1, this.dir);
				this.SRFrontCenter.updateSensor(this.row, this.col + 1, this.dir);
				this.SRFrontRight.updateSensor(this.row - 1, this.col + 1, this.dir);
				this.SRRight1.updateSensor(this.row - 1, this.col + 1, updateDir(RobotConst.MOVE.TURN_RIGHT));
				this.SRRight2.updateSensor(this.row - 1, this.col - 1, updateDir(RobotConst.MOVE.TURN_RIGHT));
				this.LRLeft.updateSensor(this.row - 1, this.col + 1, updateDir(RobotConst.MOVE.TURN_LEFT));
				break;
			case SOUTH:
				this.SRFrontLeft.updateSensor(this.row - 1, this.col + 1, this.dir);
				this.SRFrontCenter.updateSensor(this.row - 1, this.col, this.dir);
				this.SRFrontRight.updateSensor(this.row - 1, this.col - 1, this.dir);
				this.SRRight1.updateSensor(this.row - 1, this.col - 1, updateDir(RobotConst.MOVE.TURN_RIGHT));
				this.SRRight2.updateSensor(this.row + 1, this.col - 1, updateDir(RobotConst.MOVE.TURN_RIGHT));
				this.LRLeft.updateSensor(this.row - 1, this.col - 1, updateDir(RobotConst.MOVE.TURN_LEFT));
				break;
			case WEST:
				this.SRFrontLeft.updateSensor(this.row - 1, this.col - 1, this.dir);
				this.SRFrontCenter.updateSensor(this.row, this.col - 1, this.dir);
				this.SRFrontRight.updateSensor(this.row + 1, this.col - 1, this.dir);
				this.SRRight1.updateSensor(this.row + 1, this.col - 1, updateDir(RobotConst.MOVE.TURN_RIGHT));
				this.SRRight2.updateSensor(this.row + 1, this.col + 1, updateDir(RobotConst.MOVE.TURN_RIGHT));
				this.LRLeft.updateSensor(this.row + 1, this.col - 1, updateDir(RobotConst.MOVE.TURN_LEFT));
				break;
		}
	}

	public void simulateSense(Map exploredMap, Map actualMap) {
		this.SRFrontLeft.simulateSense(exploredMap, actualMap);
		this.SRFrontCenter.simulateSense(exploredMap, actualMap);
		this.SRFrontRight.simulateSense(exploredMap, actualMap);
		this.SRRight1.simulateSense(exploredMap, actualMap);
		this.SRRight2.simulateSense(exploredMap, actualMap);
		this.LRLeft.simulateSense(exploredMap, actualMap);
	}

	public void sense(Map exploredMap) {
		java.util.Map<Integer, Float> response = client.getMetrics();
		processSensorValue(response, exploredMap);
		client.setMap(exploredMap);
	}

	public void processSensorValue(java.util.Map<Integer, Float> values, Map exploredMap) {
//		Double valueFLSR = (1 / (0.0000005 * Math.pow(values.get(new Integer(1)), 2) + 0.00005 * values.get(new Integer(1)) + 0.0081) + 5) / 10.0;
//		Double valueFCSR = (1 / (0.0002 * values.get(new Integer(2)) - 0.0054 + 1) + 5) / 10.0;
//		Double valueRSR = ((1 / (0.0002 * values.get(new Integer(3)) - 0.0063) + 3) + 5) / 10.0;
////		Double valueRSR = ((1 / (0.0000004 * Math.pow(values.get(new Integer(3)), 2) + 0.0001 * values.get(new Integer(3)) + 0.0014)) + 5) / 10.0;
//		Double valueFRSR = ((1 / (0.0000005 * Math.pow(values.get(new Integer(4)), 2) + 0.00006 * values.get(new Integer(4)) + 0.0062)) + 5) / 10.0;
//		Double valueLSR = ((1 / (0.0002 * values.get(new Integer(5)) - 0.0016)) + 5) / 10.0;
//		Double valueLLR = ((1 / (0.00000009 * Math.pow(values.get(new Integer(6)), 2) + 0.00002 * values.get(new Integer(6)) + 0.0085)) + 5) /
//				10.0;

//		logger.info(valueFCSR);
//		logger.info(valueFCSR.intValue());

		SRFrontLeft.sense(exploredMap, values.get(new Integer(1)).intValue());
		SRFrontCenter.sense(exploredMap, values.get(new Integer(2)).intValue());
		SRFrontRight.sense(exploredMap, values.get(new Integer(4)).intValue());
		SRRight1.sense(exploredMap, values.get(new Integer(5)).intValue());
		SRRight2.sense(exploredMap, values.get(new Integer(3)).intValue());
		LRLeft.sense(exploredMap, values.get(new Integer(6)).intValue());
	}
}
