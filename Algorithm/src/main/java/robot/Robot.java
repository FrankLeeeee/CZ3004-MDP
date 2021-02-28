package robot;

import communication.GrpcControlClient;
import communication.GrpcDataClient;
import communication.MapDescriptor;
import config.MapConst;
import config.RobotConst;
import map.Map;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class Robot {
	// SR: short range, LR: long range
	private final Sensor SRFrontLeft;
	private final Sensor SRFrontCenter;
	private final Sensor SRFrontRight;
	private final Sensor SRLeft;
	private final Sensor SRRight;
	private final Sensor LRLeft;
	private final boolean isRealRobot;

	private int row;
	private int col;
	private RobotConst.DIRECTION dir;
	private int speed;
	private GrpcDataClient dataClient;
	private GrpcControlClient controlClient;
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
		this.SRLeft = new Sensor("SRL", RobotConst.SENSOR_SR_LOW, RobotConst.SENSOR_SR_HIGH, this.row + 1, this.col - 1, updateDir(RobotConst.MOVE.TURN_LEFT));
		this.SRRight = new Sensor("SRR", RobotConst.SENSOR_SR_LOW, RobotConst.SENSOR_SR_HIGH, this.row + 1, this.col + 1, updateDir(RobotConst.MOVE.TURN_RIGHT));
		this.LRLeft = new Sensor("LRL", RobotConst.SENSOR_LR_LOW, RobotConst.SENSOR_LR_HIGH, this.row, this.col - 1, updateDir(RobotConst.MOVE.TURN_LEFT));

		this.dataClient = GrpcDataClient.getInstance();
		this.controlClient = GrpcControlClient.getInstance();
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

	public Sensor getSRLeft() {
		return this.SRLeft;
	}

	public Sensor getSRRight() {
		return this.SRRight;
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
		move(m, true, exploredMap);
	}

	public void move(RobotConst.MOVE m, boolean sendToAndroidFlag, Map exploredMap) {
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

		if (this.isRealRobot) sendMove(m, sendToAndroidFlag, exploredMap);
		else logger.info("Move: " + RobotConst.MOVE.getMove(m));

		// check if robot has reached goal
		if (this.row == MapConst.GOAL_ROW && this.col == MapConst.GOAL_COL) this.hasReachedGoal = true;
	}

	private RobotConst.DIRECTION updateDir(RobotConst.MOVE m) {
		return (m == RobotConst.MOVE.TURN_LEFT) ? RobotConst.DIRECTION.getNextAntiClk(this.dir) : RobotConst.DIRECTION.getNextClk(this.dir);
	}

	private void sendMove(RobotConst.MOVE m, boolean sendMoveToAndroidFlag, Map exploredMap) {
		if (m.equals(RobotConst.MOVE.CALIBRATE)) {
			Boolean response = controlClient.calibrate();
			assert response : "Calibration returns status 0";
		} else {
			Boolean response = controlClient.moveRobot(m, 1);
			assert response : "Movement returns status 0";
		}

		if (sendMoveToAndroidFlag) {
			sendDataToAndroid(exploredMap);
		}
	}


	public void moveForward(int numSteps, Map exploredMap) {
		if (numSteps == 1) {
			move(RobotConst.MOVE.FORWARD, exploredMap);
		} else {
			Boolean response = controlClient.moveRobot(RobotConst.MOVE.FORWARD, numSteps);
			assert response : "Forward by multiple steps returns 0";

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
			sendDataToAndroid(exploredMap);
		}
	}

	public void updateSensorsDirections() {
		switch (this.dir) {
			case NORTH:
				this.SRFrontLeft.updateSensor(this.row + 1, this.col - 1, this.dir);
				this.SRFrontCenter.updateSensor(this.row + 1, this.col, this.dir);
				this.SRFrontRight.updateSensor(this.row + 1, this.col + 1, this.dir);
				this.SRLeft.updateSensor(this.row + 1, this.col - 1, updateDir(RobotConst.MOVE.TURN_LEFT));
				this.SRRight.updateSensor(this.row + 1, this.col + 1, updateDir(RobotConst.MOVE.TURN_RIGHT));
				this.LRLeft.updateSensor(this.row, this.col - 1, updateDir(RobotConst.MOVE.TURN_LEFT));
				break;
			case EAST:
				this.SRFrontLeft.updateSensor(this.row + 1, this.col + 1, this.dir);
				this.SRFrontCenter.updateSensor(this.row, this.col + 1, this.dir);
				this.SRFrontRight.updateSensor(this.row - 1, this.col + 1, this.dir);
				this.SRLeft.updateSensor(this.row + 1, this.col + 1, updateDir(RobotConst.MOVE.TURN_LEFT));
				this.SRRight.updateSensor(this.row - 1, this.col + 1, updateDir(RobotConst.MOVE.TURN_RIGHT));
				this.LRLeft.updateSensor(this.row + 1, this.col, updateDir(RobotConst.MOVE.TURN_LEFT));
				break;
			case SOUTH:
				this.SRFrontLeft.updateSensor(this.row - 1, this.col + 1, this.dir);
				this.SRFrontCenter.updateSensor(this.row - 1, this.col, this.dir);
				this.SRFrontRight.updateSensor(this.row - 1, this.col - 1, this.dir);
				this.SRLeft.updateSensor(this.row - 1, this.col + 1, updateDir(RobotConst.MOVE.TURN_LEFT));
				this.SRRight.updateSensor(this.row - 1, this.col - 1, updateDir(RobotConst.MOVE.TURN_RIGHT));
				this.LRLeft.updateSensor(this.row, this.col + 1, updateDir(RobotConst.MOVE.TURN_LEFT));
				break;
			case WEST:
				this.SRFrontLeft.updateSensor(this.row - 1, this.col - 1, this.dir);
				this.SRFrontCenter.updateSensor(this.row, this.col - 1, this.dir);
				this.SRFrontRight.updateSensor(this.row + 1, this.col - 1, this.dir);
				this.SRLeft.updateSensor(this.row - 1, this.col - 1, updateDir(RobotConst.MOVE.TURN_LEFT));
				this.SRRight.updateSensor(this.row + 1, this.col - 1, updateDir(RobotConst.MOVE.TURN_RIGHT));
				this.LRLeft.updateSensor(this.row - 1, this.col, updateDir(RobotConst.MOVE.TURN_LEFT));
				break;
		}
	}

	public void simulateSense(Map exploredMap, Map actualMap) {
		this.SRFrontLeft.simulateSense(exploredMap, actualMap);
		this.SRFrontCenter.simulateSense(exploredMap, actualMap);
		this.SRFrontRight.simulateSense(exploredMap, actualMap);
		this.SRLeft.simulateSense(exploredMap, actualMap);
		this.SRRight.simulateSense(exploredMap, actualMap);
		this.LRLeft.simulateSense(exploredMap, actualMap);
	}

	public void sense(Map exploredMap) {
		int[] sensorInt = new int[6]; // store the sensor data from the real sensors
		java.util.Map<Integer, Double> response = dataClient.getMetrics();

		// SRFL:SRFC:SRFR:SRL:SRR:LRL
		for (int i = 0; i <= response.size(); i++) {
			sensorInt[i] = response.get(new Integer(i)).intValue();
		}

		SRFrontLeft.sense(exploredMap, sensorInt[0]);
		SRFrontCenter.sense(exploredMap, sensorInt[1]);
		SRFrontRight.sense(exploredMap, sensorInt[2]);
		SRLeft.sense(exploredMap, sensorInt[3]);
		SRRight.sense(exploredMap, sensorInt[4]);
		LRLeft.sense(exploredMap, sensorInt[5]);

		sendDataToAndroid(exploredMap);
	}

	public void sendDataToAndroid(Map exploredMap) {
		String[] mapDescriptors = MapDescriptor.generateMapDescriptor(exploredMap);
		String mapDescrptorString = mapDescriptors[0] + mapDescriptors[1];
//		mapDescrptorString = new BigInteger(mapDescrptorString, 16).toString(2);

		// TODO: fix the map string
		boolean response = dataClient.setMap("010");
		assert response : "Sending explored map to Android returns 0";
	}
}
