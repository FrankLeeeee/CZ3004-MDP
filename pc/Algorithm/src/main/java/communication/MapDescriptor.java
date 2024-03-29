package communication;

import config.MapConst;
import map.Cell;
import map.Map;
import org.apache.log4j.Logger;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MapDescriptor {

	private static Logger logger = Logger.getLogger(MapDescriptor.class);


	public static void readMap(Map map, String mapPath) {
		try {
			InputStream inStream = new FileInputStream(mapPath);
			BufferedReader buffReader = new BufferedReader(new InputStreamReader(inStream));

			// read in first line of the file
			String line = buffReader.readLine();
			StringBuilder strBuilder = new StringBuilder();

			while (line != null) {
				strBuilder.append(line);
				line = buffReader.readLine();
			}

			// convert to one whole string
			String hexMap = strBuilder.toString();
			String binaryMap = hexStrToBin(hexMap);

			int idx = 0;
			for (int r = 0; r < MapConst.NUM_ROWS; r++) {
				for (int c = 0; c < MapConst.NUM_COLS; c++) {
					if (binaryMap.charAt(idx) == '1') map.setObstacleCell(r, c, true);
					idx++;
				}
			}

			// set all cells to explored,
			// so that the map read in can be displayed on the screen
			map.setAllCellsExplored();

		} catch (IOException e) {
			logger.error("Error reading map: " + e.toString());
		}
	}

	private static String binStrToHexStr(String binaryString) {
		return Integer.toHexString(Integer.parseInt(binaryString, 2));
	}

	private static String hexStrToBin(String hex) {
		hex = hex.replaceAll("0", "0000");
		hex = hex.replaceAll("1", "0001");
		hex = hex.replaceAll("2", "0010");
		hex = hex.replaceAll("3", "0011");
		hex = hex.replaceAll("4", "0100");
		hex = hex.replaceAll("5", "0101");
		hex = hex.replaceAll("6", "0110");
		hex = hex.replaceAll("7", "0111");
		hex = hex.replaceAll("8", "1000");
		hex = hex.replaceAll("9", "1001");
		hex = hex.replaceAll("A", "1010");
		hex = hex.replaceAll("B", "1011");
		hex = hex.replaceAll("C", "1100");
		hex = hex.replaceAll("D", "1101");
		hex = hex.replaceAll("E", "1110");
		hex = hex.replaceAll("F", "1111");
		return hex;
	}

	public static String[] generateMapDescriptor(Map map) {
		String[] descriptor = new String[2];

		// DONE: p1: explored or not cell p2: obstacle or not
		StringBuilder p1Hex = new StringBuilder();
		StringBuilder p1Bin = new StringBuilder();
		StringBuilder p2Hex = new StringBuilder();
		StringBuilder p2Bin = new StringBuilder();

		// need to append to make it complete bytes
		p1Bin.append("11");
		for (Cell[] row : map.getArena()) {
			for (Cell cell : row) {
				if (cell.isExplored()) {
					p1Bin.append("1");

					if (cell.isObstacle()) p2Bin.append("1");
					else p2Bin.append("0");

					// reset p2 binary
					if (p2Bin.length() == 4) {
						p2Hex.append(binStrToHexStr(p2Bin.toString()));
						p2Bin.setLength(0);
					}
				} else {
					p1Bin.append("0");
				}

				if (p1Bin.length() == 4) {
					p1Hex.append(binStrToHexStr(p1Bin.toString()));
					p1Bin.setLength(0);
				}
			}
		}

		p1Bin.append("11");
		p1Hex.append(binStrToHexStr(p1Bin.toString()));
		descriptor[0] = p1Hex.toString();

		if (p2Bin.length() > 0) p2Hex.append(binStrToHexStr(p2Bin.toString()));
		descriptor[1] = p2Hex.toString();

		return descriptor;
	}

	public static void writeFile(String[] MD) {
		FileWriter fr = null;

		try {
			String path = (new File(".")).getCanonicalPath();
			DateFormat dateFormat = new SimpleDateFormat("yyMMdd_HHmm");
			Date date = new Date();

			File dir = new File(path + "/MD");
			dir.mkdir();

			fr = new FileWriter(path + "/MD/" + dateFormat.format(date) + "_MD.txt");
			StringBuilder sb = new StringBuilder();
			String[] var6 = MD;
			int var7 = MD.length;

			for (int var8 = 0; var8 < var7; ++var8) {
				String row = var6[var8];
				sb.append(row);
				sb.append("\n");
			}

			fr.write(sb.toString());
		} catch (IOException var18) {
			var18.printStackTrace();
		} finally {
			try {
				fr.close();
			} catch (IOException var17) {
				var17.printStackTrace();
			}

		}

	}
}
