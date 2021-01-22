package robot;

import config.MapConst;
import config.RobotConst;
import map.Map;
import utils.CommunicationManager;
import utils.MapDescriptor;

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
	private boolean hasReachedGoal;
	private boolean hasReturnedStart = false;

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
				System.out.println("Robot movement error: " + e);
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
				System.out.println("Error in movement!");
				break;
		}

		if (this.isRealRobot) sendMove(m, sendToAndroidFlag, exploredMap);
		else System.out.println("Move: " + RobotConst.MOVE.getMove(m));

		// check if robot has reached goal
		if (this.row == MapConst.GOAL_ROW && this.col == MapConst.GOAL_COL) this.hasReachedGoal = true;
	}

	private RobotConst.DIRECTION updateDir(RobotConst.MOVE m) {
		return (m == RobotConst.MOVE.TURN_LEFT) ? RobotConst.DIRECTION.getNextAntiClk(this.dir) : RobotConst.DIRECTION.getNextClk(this.dir);
	}

	private void sendMove(RobotConst.MOVE m, boolean sendMoveToAndroidFlag, Map exploredMap) {
		// add "" to make it a string type
		if (m.equals(RobotConst.MOVE.FORWARD))
			CommunicationManager.getCommMgr().sendMsg("1", CommunicationManager.INSTRUCTIONS);
		else
			CommunicationManager.getCommMgr().sendMsg(RobotConst.MOVE.getMove(m) + "", CommunicationManager.INSTRUCTIONS);

		System.out.println(this.getRow() + ", " + this.getCol() + " " + this.getDir());
//		if (m != RobotConst.MOVE.CALIBRATE && sendMoveToAndroidFlag) {
		//CommMgr.getCommMgr().sendMsg(this.row + CommMgr.SEPARATOR + this.col + CommMgr.SEPARATOR + RobotConst.DIRECTION.getD(this.dir), CommMgr.ROBOT_POS);

		//sendDataToAndroid(exploredMap);
//		}
	}


	public void moveForward(int numSteps, Map exploredMap) {
		if (numSteps == 1) {
			move(RobotConst.MOVE.FORWARD, exploredMap);
		} else {

			CommunicationManager.getCommMgr().sendMsg(Integer.toString(numSteps), CommunicationManager.INSTRUCTIONS);

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

			System.out.println(this.getRow() + ", " + this.getCol() + " " + this.getDir());
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

        /*String[] mapDescriptors = MapDescriptor.generateMapDescriptor(exploredMap);
        CommMgr.getCommMgr().sendMsg(mapDescriptors[0] + CommMgr.SEPARATOR + mapDescriptors[1], CommMgr.MAP_STRINGS);*/
		sendDataToAndroid(exploredMap);
	}

	public void sense(Map exploredMap) {
		int[] sensorInt = new int[6]; // store the sensor data from the real sensors
		String msg = null;

		while (msg == null) {
			msg = CommunicationManager.getCommMgr().receiveMsg();
			if (msg.split(CommunicationManager.SEPARATOR)[0].equals(CommunicationManager.SENSOR_DATA))
				break;
		}
		String[] sensorString = msg.split(CommunicationManager.SEPARATOR); // SRFL:SRFC:SRFR:SRL:SRR:LRL
		for (int i = 1; i <= sensorInt.length; i++) {
			sensorInt[i - 1] = (Integer.parseInt(sensorString[i]));
		}

		SRFrontLeft.sense(exploredMap, sensorInt[0]);
		SRFrontCenter.sense(exploredMap, sensorInt[1]);
		SRFrontRight.sense(exploredMap, sensorInt[2]);
		SRLeft.sense(exploredMap, sensorInt[3]);
		SRRight.sense(exploredMap, sensorInt[4]);
		LRLeft.sense(exploredMap, sensorInt[5]);

        /*String[] mapDescriptors = MapDescriptor.generateMapDescriptor(exploredMap);
        CommMgr.getCommMgr().sendMsg(mapDescriptors[0] + CommMgr.SEPARATOR + mapDescriptors[1], CommMgr.MAP_STRINGS);*/
		sendDataToAndroid(exploredMap);
	}

	private void sendDataToAndroid(Map exploredMap) {
		try {
			String[] mapDescriptors = MapDescriptor.generateMapDescriptor(exploredMap);
			String pos = CommunicationManager.ROBOT_POS + CommunicationManager.SEPARATOR + this.col + CommunicationManager.SEPARATOR + this.row + CommunicationManager.SEPARATOR
					+ RobotConst.DIRECTION.getD(this.dir);
			String strToAndroid = CommunicationManager.parseToJSON(mapDescriptors[0], mapDescriptors[1], pos);
			CommunicationManager.getCommMgr().sendMsg(strToAndroid, CommunicationManager.ROBOT_INFO);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
