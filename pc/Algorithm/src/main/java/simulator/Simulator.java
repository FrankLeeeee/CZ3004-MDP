package simulator;

import algorithms.Exploration;
import algorithms.FastestPath;
import communication.GrpcClient;
import communication.MapDescriptor;
import config.GrpcConst;
import config.MapConst;
import config.RobotConst;
import config.SimulatorConst;
import grpc.GrpcService;
import map.Cell;
import map.Map;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import robot.Robot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class Simulator {
	private static JFrame mainFrame;
	private static JPanel mapPanel;
	private static JPanel btnPanel;
	private static JButton btnSettings;
	private static JButton btnReadMap;
	private static JButton btnExplore;
	private static JButton btnFastestPath;
	public static JPanel specPanel;
	public static JLabel timeElapsedLbl;
	public static JLabel areaCoveredLbl;
	public static long exStartTime;  // exploration
	public static long fpStartTime;  // fastest path
	private static Timer timer;
	private static JDialog settingsDialog;

	private static Robot robot;
	private static Map actualMap;
	private static Map exploredMap;
	private static int coverageLimit = RobotConst.COVERAGE_LIMIT;
	private static int timeLimit = RobotConst.TIME_LIMIT;
	private static int speed = RobotConst.SPEED; // steps per second
	private static int wayPointR = 4;  // default
	private static int wayPointC = 9;  // default

	private static final String EXP_MAP = "EXPLORED_MAP";
	private static final String ACT_MAP = "ACTUAL_MAP";
	public static Logger logger = Logger.getLogger(Simulator.class);

	private static final boolean realRun = false;
	private static final boolean testArduino = false;
	public static final String task = "IMG";

	private static final String mapPathForFP = "/Users/franklee/Documents/CZ3004-MDP/Algorithm/TestMD/test1.txt";
	public static final String imgPathforIMG = "/Users/franklee/Documents/CZ3004-MDP/Algorithm/img_rec.jpg";


	public static void main(String[] args) throws InterruptedException, IOException {
		BasicConfigurator.configure();

		// create a Robot object
		robot = new Robot(MapConst.START_ROW, MapConst.START_COL, realRun);

//		GrpcClient client = GrpcClient.getInstance();
//		logger.info("connecting grpc");
//		while (!client.isConnected()) {
//			try {
//				client.connect(GrpcConst.CLIENT_HOST, GrpcConst.CLIENT_PORT);
//			} catch (Exception e) {
//				logger.error("data client encountered connection error, retrying after 5 seconds...");
//				TimeUnit.SECONDS.sleep(5);
//			}
//		}
//		logger.info("grpc connected");
//		Map exploredMap = new Map(robot);
//		exploredMap.setAllCellsExplored();
//		client.setMap(exploredMap);
//		client.takePhoto();
//		System.exit(0);//		client.getFinalImageResults(imgPathforIMG);
//
		// initialize the map
		exploredMap = new Map(robot);
		exploredMap.setAllCellsUnexplored();
//
//		initialize the app
		initializeFrame();

		if (realRun) {
			if (testArduino) {
				test();
			} else {
				logger.info("Initializing gRPC clients");

				// create gRPC client instances
				GrpcClient client = GrpcClient.getInstance();

				while (!client.isConnected()) {
					try {
						client.connect(GrpcConst.CLIENT_HOST, GrpcConst.CLIENT_PORT);
					} catch (Exception e) {
						logger.error("data client encountered connection error, retrying after 5 seconds...");
						TimeUnit.SECONDS.sleep(5);
					}
				}

				assert client.isConnected();
				logger.info("gRPC connection is set up.");

				if (task == "EXP" || task == "IMG") {
					mapPanel.add(exploredMap, EXP_MAP);
					(new Simulator.Explore()).execute();
				} else if (task == "FP") {
					MapDescriptor.readMap(exploredMap, mapPathForFP);
					mapPanel.add(exploredMap, EXP_MAP);
					(new Simulator.Fastest()).execute();
				}
			}
		} else {
			// run virtual testing
			logger.info("Run virtual testing");
			actualMap = new Map(robot);
			MapDescriptor.readMap(actualMap, mapPathForFP);

			if (task == "EXP" || task == "IMG") {
				mapPanel.add(exploredMap, EXP_MAP);
				mapPanel.add(actualMap, ACT_MAP);
//				actualMap.getArena()[2][3].setObstacle(true);
//				(new Simulator.Explore()).execute();

			} else if (task == "FP") {
				MapDescriptor.readMap(exploredMap, mapPathForFP);
				mapPanel.add(exploredMap, EXP_MAP);
				mapPanel.add(actualMap, ACT_MAP);
//				(new Simulator.Fastest()).execute();
			}
		}
	}

	private static void test() throws InterruptedException {
		// Test
		// create gRPC client instances

		GrpcClient client = GrpcClient.getInstance();

		while (!client.isConnected()) {
			try {
				client.connect(GrpcConst.CLIENT_HOST, GrpcConst.CLIENT_PORT);
			} catch (Exception e) {
				logger.error("data client encountered connection error, retrying after 5 seconds...");
				TimeUnit.SECONDS.sleep(5);
			}
		}

		System.out.println("connected");

		java.util.Map<Integer, Float> turnLeftResponse = client.moveRobot(RobotConst.MOVE.TURN_LEFT, 90);
		System.out.println("turn left done");
		System.out.println(turnLeftResponse);

//		boolean cal = client.calibrate();
//		System.out.println("calibrate done");
//		System.out.println(cal);

		java.util.Map<Integer, Float> turnRightResponse = client.moveRobot(RobotConst.MOVE.TURN_RIGHT, 90);
		System.out.println("turn right done");
		System.out.println(turnRightResponse);


		java.util.Map<Integer, Float> forwardResponse = client.moveRobot(RobotConst.MOVE.FORWARD, 1);
		System.out.println("forward done");
		System.out.println(forwardResponse);

		java.util.Map<Integer, Float> sensorData = client.getMetrics();
		System.out.println("get sensor data done");
		System.out.println(sensorData);

		boolean stopRob = client.stopRobot();
		System.out.println("stop robot done");
		System.out.println(stopRob);

	}


	private static void initializeFrame() {
		// create the main frame
		mainFrame = new JFrame();
		mainFrame.setTitle(SimulatorConst.FRAME_TITLE);
		mainFrame.setSize(SimulatorConst.FRAME_WIDTH, SimulatorConst.FRAME_HEIGHT);
		mainFrame.setResizable(false);

		// center the main frame
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (dim.width - mainFrame.getSize().width) / 2;
		int y = (dim.height - mainFrame.getSize().height) / 2;
		mainFrame.setLocation(x, y);

		// initialize panels
		initializeMapPanel();
		initializeButtonPanel();
		initliazeSpecPanel();

		// show the app
		mainFrame.setVisible(true);
		mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	private static void initializeButtonPanel() {
		// which the parent
		JPanel btnPanelParent = new JPanel();
		GridBagLayout side_gbl = new GridBagLayout();
		btnPanelParent.setBackground(Color.GRAY);
		btnPanelParent.setLayout(side_gbl);
		btnPanelParent.setPreferredSize(new Dimension(
				SimulatorConst.BUTTON_PANEL_WIDTH,
				SimulatorConst.BUTTON_PANEL_HEIGHT)); // size of side menu
		mainFrame.add(btnPanelParent, BorderLayout.PAGE_END);

		// set as vertical menu
		btnPanel = new JPanel();
		GridLayout side_gl = new GridLayout(1, 5);
		btnPanel.setLayout(side_gl);
		btnPanel.setOpaque(false);
		side_gl.setHgap(10); // vertical gap between each button
		btnPanelParent.add(btnPanel);

		// read map Button
		// button is only added when it is simulation
		if (!realRun) {
			btnReadMap = new JButton("READ MAP");
			standardiseBtn(btnReadMap);
			btnReadMap.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					super.mousePressed(e);
					// show a dialog to choose the map file
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
					if (fileChooser.showOpenDialog(mainFrame) == 0) {
						// re-build the map panel
						File file = fileChooser.getSelectedFile();
						mapPanel.removeAll();
						MapDescriptor.readMap(actualMap, file.getAbsolutePath());
						mapPanel.add(actualMap, ACT_MAP);
						mapPanel.add(exploredMap, EXP_MAP);
						mainFrame.repaint();
					}
				}
			});

			btnPanel.add(btnReadMap);

			// explore button
			btnExplore = new JButton("EXPLORE");
			standardiseBtn(btnExplore);
			btnExplore.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					super.mousePressed(e);
					// click to start exploration
					CardLayout cl = (CardLayout) mapPanel.getLayout();
					cl.show(mapPanel, EXP_MAP);
					(new Simulator.Explore()).execute();
				}
			});
			btnPanel.add(btnExplore);


			// fastest path button
			btnFastestPath = new JButton("FASTEST PATH");
			standardiseBtn(btnFastestPath);
			btnFastestPath.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					super.mousePressed(e);
					// click to show fastest path
					CardLayout cl = (CardLayout) mapPanel.getLayout();
					cl.show(mapPanel, EXP_MAP);
					specPanel.getComponent(1).setVisible(false);
					(new Simulator.Fastest()).execute();
				}
			});

			btnPanel.add(btnFastestPath);

			// settings button
			btnSettings = new JButton("SETTINGS");
			standardiseBtn(btnSettings);
			btnSettings.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					super.mousePressed(e);
					// click to change setting
					showSettingDialog();
				}
			});

			btnPanel.add(btnSettings);
		}


	}

	private static void standardiseBtn(JButton btn) {
		btn.setFont(new Font("Arial", 1, 13));
		btn.setFocusPainted(false);
	}

	private static void initliazeSpecPanel() {
		specPanel = new JPanel();
		GridLayout spec_gl = new GridLayout(1, 2);
		specPanel.setLayout(spec_gl);
		specPanel.setBackground(Color.WHITE);
		spec_gl.setHgap(10);

		JPanel timerPanel = new JPanel();
		timerPanel.setLayout(new GridLayout(1, 2));
		timerPanel.setBackground(Color.WHITE);

		JLabel timeLbl = new JLabel("Time Elapsed:");
		timerPanel.add(timeLbl);
		timeElapsedLbl = new JLabel("time");
		timeElapsedLbl.setFont(new Font("Arial", 1, 13));
		timerPanel.add(timeElapsedLbl);

		specPanel.add(timerPanel);

		JPanel cvgPanel = new JPanel();
		cvgPanel.setLayout(new GridLayout(1, 2));
		cvgPanel.setBackground(Color.WHITE);

		JLabel coverageLbl = new JLabel("Coverage:");
		cvgPanel.add(coverageLbl);
		areaCoveredLbl = new JLabel("area");
		areaCoveredLbl.setFont(new Font("Arial", 1, 13));
		cvgPanel.add(areaCoveredLbl);

		specPanel.add(cvgPanel);
		mainFrame.add(specPanel, BorderLayout.PAGE_START);
	}

	private static void initializeMapPanel() {
		Container mainContentPane = mainFrame.getContentPane();
		mapPanel = new JPanel(new CardLayout());
		mainContentPane.add(mapPanel, BorderLayout.CENTER);
	}

	static class Explore extends SwingWorker<Integer, String> {
		protected Integer doInBackground() throws Exception {
			logger.info("starting exploration");

			// disable button
			if (!realRun) {
				btnExplore.setEnabled(false);
				btnFastestPath.setEnabled(false);
				btnReadMap.setEnabled(false);
				btnSettings.setEnabled(false);
			}

			// initialize the setting
			robot.setRow(MapConst.START_ROW);
			robot.setCol(MapConst.START_COL);
			robot.setSpeed(speed);
			exploredMap.repaint();
			Exploration exploration;

			if (!realRun) {
				// the actual map is given to simulate sensor data
				exploration = new Exploration(exploredMap, robot, coverageLimit, timeLimit, actualMap);
			} else {
				GrpcClient client = GrpcClient.getInstance();
				exploration = new Exploration(exploredMap, robot, coverageLimit, timeLimit);
				exploration.initialCalibration();


				while (true) {
					// waiting for android to send the start exploration message
					boolean response = client.waitForRobotStart(GrpcService.RobotStatus.Mode.EXPLORATION);
					assert response : "gRPC server claims that the robot is not started";
					logger.info("Rpi signals to start exploration after initial calibration.");
					break;
				}
			}

			exStartTime = System.currentTimeMillis();
			displayElapsedTime("EX");
			timer.start();
			exploration.run();
			String[] mapDescriptors = MapDescriptor.generateMapDescriptor(exploredMap);
			MapDescriptor.writeFile(mapDescriptors);

			logger.info("Map descriptor saved");

			if (realRun) {
				robot.sendDataToAndroid(exploredMap);
			}

			timer.stop();

			// enable button
			if (!realRun) {
				btnExplore.setEnabled(true);
				btnFastestPath.setEnabled(true);
				btnReadMap.setEnabled(true);
				btnSettings.setEnabled(true);
			}

			return 111;
		}
	}

	static class Fastest extends SwingWorker<Integer, String> {
		Exploration exMode;

		public Fastest(Exploration exMode) {
			//  Auto-generated constructor stub
			this.exMode = exMode;
		}

		public Fastest() {

		}

		protected Integer doInBackground() throws Exception {
			logger.info("start background task");
			if (!realRun) {
				btnExplore.setEnabled(false);
				btnFastestPath.setEnabled(false);
				btnReadMap.setEnabled(false);
				btnSettings.setEnabled(false);
			}

			// start fastest path
			robot.setRow(MapConst.START_ROW);
			robot.setCol(MapConst.START_COL);
			robot.setSpeed(speed);
			exploredMap.repaint();

			// send explored map to android
			GrpcClient client = GrpcClient.getInstance();
			logger.info("setting the map");
			if (realRun) {
				client.setMap(exploredMap);
			}
			logger.info("map is set");


			// init mvoes
			FastestPath fastestPathWayPoint, fastestPathGoal;
			ArrayList<RobotConst.MOVE> movesWayPoint, movesGoal;

			// fastest path from start zone to way point
			fastestPathWayPoint = !realRun ? new FastestPath(exploredMap, robot, actualMap)
					: new FastestPath(exploredMap, robot);
			fastestPathWayPoint.setExMode(exMode);
			if (realRun) {
				// TODO: change back
				logger.info("Waiting for Way Point command....");
				while (true) {
					GrpcService.Position pos = client.getWayPoint();
					wayPointC = pos.getX();
					wayPointR = pos.getY();
					if (wayPointC == -1 || wayPointR == -1) {
						continue;
					}
					break;
				}
				logger.info("Wait point is " + wayPointR + ", " + wayPointC);

				// TODO: change back
				logger.info("Waiting for FP_START command....");
				while (true) {
					boolean response = client.waitForRobotStart(GrpcService.RobotStatus.Mode.FASTEST_PATH);
					assert response : "Waiting to start FP returns 0";
					break;
				}
				logger.info("fastest path start");
				exploredMap.repaint();

//				initial calibration
				robot.move(RobotConst.MOVE.TURN_LEFT, exploredMap, false, false);
				robot.move(RobotConst.MOVE.CALIBRATE_FRONT, exploredMap, false, false);
				robot.move(RobotConst.MOVE.TURN_LEFT, exploredMap, false, false);
				robot.move(RobotConst.MOVE.CALIBRATE_FRONT, exploredMap, false, false);
				robot.move(RobotConst.MOVE.TURN_LEFT, exploredMap, false, false);
			}

			fpStartTime = System.currentTimeMillis();
			displayElapsedTime("FP");
			timer.start();
			fastestPathWayPoint.setToGoal(true);
			movesWayPoint = fastestPathWayPoint.computeFastestPath(wayPointR, wayPointC);
			fastestPathWayPoint.executeMoves(movesWayPoint, robot);

			// fastest path from way point to goal zone
			fastestPathGoal = !realRun ? new FastestPath(exploredMap, robot, actualMap)
					: new FastestPath(exploredMap, robot);
			fastestPathGoal.setToGoal(true);
			movesGoal = fastestPathGoal.computeFastestPath(MapConst.GOAL_ROW, MapConst.GOAL_COL);
			fastestPathGoal.executeMoves(movesGoal, robot);
			timer.stop();
			logger.info("Time taken: " + (System.currentTimeMillis() - fpStartTime) / 1000 + "s");

			// enable button
			if (!realRun) {
				btnExplore.setEnabled(true);
				btnFastestPath.setEnabled(true);
				btnReadMap.setEnabled(true);
				btnSettings.setEnabled(true);
			} else {
				// TODO: change back
				client.stopRobot();
			}
			return 222;
		}

	}

	private static void showSettingDialog() {
		settingsDialog = new JDialog(mainFrame, "Settings", true);
		settingsDialog.setSize(600, 200);
		settingsDialog.setLayout(new FlowLayout());

		// set location of dialog at center
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		settingsDialog.setLocation(dim.width / 2 - settingsDialog.getSize().width / 2,
				dim.height / 2 - settingsDialog.getSize().width / 2);

		// set default settings
		final JTextField timeLimitInput = new JTextField(Integer.toString(timeLimit), 4);
		final JTextField coverageLimitInput = new JTextField(Integer.toString(100), 3);
		final JTextField wayPointRInput = new JTextField(Integer.toString(wayPointR), 2);
		final JTextField wayPointCInput = new JTextField(Integer.toString(wayPointC), 2);
		final JTextField speedInput = new JTextField(Integer.toString(speed), 3);

		// save button
		JButton btnSave = new JButton("SAVE");
		btnSave.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				timeLimit = Integer.parseInt(timeLimitInput.getText());
				coverageLimit = (int) Math
						.ceil((Integer.parseInt(coverageLimitInput.getText()) / 100.0) * MapConst.NUM_CELLS);
				speed = Integer.parseInt(speedInput.getText());

				int tmpR = Integer.parseInt(wayPointRInput.getText());
				int tmpC = Integer.parseInt(wayPointCInput.getText());
				if (isExploredAndFree(tmpR, tmpC)) {
					wayPointR = tmpR;
					wayPointC = tmpC;
					exploredMap.getArena()[wayPointR][wayPointC].setWayPoint(true);
					actualMap.getArena()[wayPointR][wayPointC].setWayPoint(true);
				} else {
					JOptionPane.showMessageDialog(mainFrame, "Invalid Way Point!");
				}

				// close the settings dialog
				settingsDialog.setVisible(false);
				mainFrame.repaint();
			}
		});

		// define panel
		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		JPanel panelSave = new JPanel();

		panel1.add(new JLabel("Time Limit: "));
		panel1.add(timeLimitInput);
		panel1.add(new JLabel("Coverage Limit: "));
		panel1.add(coverageLimitInput);

		if (!realRun) {
			panel2.add(new JLabel("Speed: "));
			panel2.add(speedInput);
		}

		panel2.add(new JLabel("Way Point Row: "));
		panel2.add(wayPointRInput);
		panel2.add(new JLabel("Way Point Col: "));
		panel2.add(wayPointCInput);

		panelSave.add(btnSave);

		settingsDialog.add(panel1);
		settingsDialog.add(panel2);
		settingsDialog.add(panelSave);
		settingsDialog.setVisible(true);
	}


	private static boolean isExploredAndFree(int r, int c) {
		if (!exploredMap.checkValidCoordinates(r, c))
			return false;
		Cell cell = exploredMap.getArena()[r][c];

		return cell.isExplored() && !cell.isObstacle() && !cell.isVirtualWall();
	}

	// to reset and display the timer
	private static void displayElapsedTime(String mode) {
		int delay = 1000;

		timer = new Timer(delay, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (mode == "EX") {
					float elapsedTime = (System.currentTimeMillis() - exStartTime) / 1000;
					timeElapsedLbl.setText(String.valueOf(elapsedTime));
				} else if (mode == "FP") {
					float elapsedTime = (System.currentTimeMillis() - fpStartTime) / 1000;
					timeElapsedLbl.setText(String.valueOf(elapsedTime));
				}
			}
		});
	}
}
