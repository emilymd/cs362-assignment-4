package cs361.battleships.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("unused")
public class Square {

	@JsonProperty private int row;
	@JsonProperty private char column;
	//@JsonProperty private Square location;
	@JsonProperty private boolean hit = false;
	@JsonProperty private boolean capHit = false;

	public Square() {
	}

	public Square(int row, char column) {
		this.row = row;
		this.column = column;
		//this.location = new Square(row,column);
	}

	public void setRow(int row){
		this.row = row;
	}

	public void setColumn(char col){
		this.column = col;
	}

	public char getColumn() {
		return column;
	}

	public int getRow() {
		return row;
	}

	//public Square getLocation(){ return location; }

	@Override
	public boolean equals(Object other) {
		if (other instanceof Square) {
			boolean r = ((Square) other).row == this.row;
			boolean c = ((Square) other).column == this.column;

			return (c && r);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 31 * row + column;
	}

	@JsonIgnore
	public boolean isOutOfBounds() {
		return row > 10 || row < 1 || column > 'J' || column < 'A';
	}

	public boolean isHit() {
		return hit;
	}

	public void hit() {
		hit = true;
	}

	public void setCapHit() { capHit = true; }

	public boolean getCapHit() { return capHit; }

	@Override
	public String toString() {
		return "(" + row + ", " + column + ')';
	}
}
