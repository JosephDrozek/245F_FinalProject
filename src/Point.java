/*
 * The point class is the model class
 * It contains integer x and y coordinates (to determine location on the graph)
 * And a String name which determines the Stock which the point is associated with.
 */

import java.io.Serializable;

public class Point implements Serializable {
	private int x;
	private int y;
	private String name;
	public String getName() {
		return name;
	}
	public void setName() {
		this.name = name;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x=x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	//Constructor class for the Point object
	public Point() {
		
	}
	@Override
	public String toString() {
		return String.format("%s %d %d", name,x,y);
	}
}
