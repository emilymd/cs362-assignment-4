package cs361.battleships.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;
import com.mchange.v1.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Ship {

	@JsonProperty private String kind;
	@JsonProperty protected List<Square> occupiedSquares;
	@JsonProperty private int size;
	@JsonProperty private boolean vertical;
	@JsonProperty public Square captainsQuarters;

	public Ship() {
		occupiedSquares = new ArrayList<>();
	}
	
	public Ship(String kind, int size) {
		this();
		this.kind = kind;
		this.size = size;
	}

	public List<Square> getOccupiedSquares() {
		return occupiedSquares;
	}


	public void place(char col, int row, boolean isVertical) {
	   // vertical = isVertical;
		for (int i=0; i<size; i++) {
			if (isVertical) {
				occupiedSquares.add(new Square(row+i, col));
			} else {
				occupiedSquares.add(new Square(row, (char) (col + i)));
			}
		}
		captainsQuarters = occupiedSquares.get(size-2);
	}

	public void setCaptainsQuarters(Square square){
        captainsQuarters = square;
	    //captainsQuarters = occupiedSquares.get(size-2);
    }

	public Square getCaptainsQuarters() { return captainsQuarters; }

	public boolean getVertical() { return vertical; }

	public boolean overlaps(Ship other) {
		Set<Square> thisSquares = Set.copyOf(getOccupiedSquares());
		Set<Square> otherSquares = Set.copyOf(other.getOccupiedSquares());
		Sets.SetView<Square> intersection = Sets.intersection(thisSquares, otherSquares);
		return intersection.size() != 0;
	}

	public boolean isAtLocation(Square location) {
		return getOccupiedSquares().stream().anyMatch(s -> s.equals(location));
	}

	public String getKind() {
		return kind;
	}

	public Result capQuarters(Square attackedSquare, Square attackedLocation) {

		var result = new Result(attackedLocation);
		result.setShip(this);
		result.getShip();

		if(kind == "MINESWEEPER") {

		for(Square s : getOccupiedSquares()) {
			s.hit();
		}
		result.setResult(AtackStatus.SUNK);
		return result;
		}


		else {
			if (attackedSquare.getCapHit()) {
				for (Square s : getOccupiedSquares()) {
					s.hit();
				}
				result.setResult(AtackStatus.SUNK);
				return result;
			}
			attackedSquare.setCapHit();
			result.setResult(AtackStatus.HIDDEN);
			return result;
		}
    }

	public Result attack(int x, char y) {
		var attackedLocation = new Square(x, y);
		var square = getOccupiedSquares().stream().filter(s -> s.equals(attackedLocation)).findFirst();
		if (!square.isPresent()) {
            return new Result(attackedLocation);
		}
		var attackedSquare = square.get();
		if (attackedSquare.isHit()) {
            var result = new Result(attackedLocation);
			result.setResult(AtackStatus.INVALID);
			return result;
		}
		if(attackedSquare.equals(captainsQuarters)) {
           var result = this.capQuarters(attackedSquare, attackedLocation);
           return result;
		}
		attackedSquare.hit();
		var result = new Result(attackedLocation);
		result.setShip(this);
		result.setResult(AtackStatus.HIT);
		return result;
	}

	@JsonIgnore
	public boolean isSunk() {
		return getOccupiedSquares().stream().allMatch(s -> s.isHit());
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Ship)) {
			return false;
		}
		var otherShip = (Ship) other;

		return this.kind.equals(otherShip.kind)
				&& this.size == otherShip.size
				&& this.occupiedSquares.equals(otherShip.occupiedSquares);
	}

	@Override
	public int hashCode() {
		return 33 * kind.hashCode() + 23 * size + 17 * occupiedSquares.hashCode();
	}

	@Override
	public String toString() {
		return kind + occupiedSquares.toString();
	}
}
