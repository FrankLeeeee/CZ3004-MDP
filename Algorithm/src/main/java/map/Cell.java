package map;

import config.MapConst;

public class Cell {
	private final int row;
	private final int col;
	private boolean isExplored;
	private boolean isObstacle;
	private boolean isVirtualWall;
	private boolean isWayPoint;
	private boolean isFastestPath;

	// Graphics attributes
	private final int x;
	private final int y;
	private final int size;
	

	public Cell(int row, int col) {
		this.row = row;
		this.col = col;
		this.isExplored = false;

		// Graphics attributes
		this.x = col * MapConst.CELL_SIZE + MapConst.BORDER + MapConst.X_OFFSET;
		this.y = MapConst.MAP_HEIGHT - (row * MapConst.CELL_SIZE - MapConst.BORDER);
		this.size = MapConst.CELL_SIZE - (MapConst.BORDER * 2);
	}

	public int getRow() {
		return this.row;
	}

	public int getCol() {
		return this.col;
	}

	public boolean isExplored() {
		return this.isExplored;
	}

	public void setExplored(boolean explored) {
		this.isExplored = explored;
	}

	public boolean isObstacle() {
		return this.isObstacle;
	}

	public void setObstacle(boolean obstacle) {
		this.isObstacle = obstacle;
	}

	public boolean isVirtualWall() {
		return this.isVirtualWall;
	}

	public void setVirtualWall(boolean flag) {
		this.isVirtualWall = flag;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getSize() {
		return size;
	}

	public boolean isWayPoint() {
		return this.isWayPoint;
	}

	public void setWayPoint(boolean wayPoint) {
		this.isWayPoint = wayPoint;
	}

	public boolean isFastestPath() {
		return isFastestPath;
	}

	public void setFastestPath(boolean fastestPath) {
		isFastestPath = fastestPath;
	}
}
