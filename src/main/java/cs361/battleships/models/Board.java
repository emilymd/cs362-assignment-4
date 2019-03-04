package cs361.battleships.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;

public class Board {

	@JsonProperty private List<Ship> ships;
	@JsonProperty private List<Result> attacks;
	@JsonProperty private List<Ship> subShips;
	public List<Square> sonarOccupiedSquares;
	public List<Square> sonarUnoccupiedSquares;
	public List<Square> sonarMiddleSquare;

	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public Board() {
		ships = new ArrayList<>();
		attacks = new ArrayList<>();
		subShips = new ArrayList<>();
	}

	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public boolean placeShip(Ship ship, int x, char y, boolean isVertical) {
		if (subShips.size() >= 4 ) {
			return false;
		}
		if (ships.stream().anyMatch(s -> s.getKind().equals(ship.getKind()))) {
			return false;
		}

		Ship placedShip;

		switch(ship.getKind()) {
            case "MINESWEEPER":
                placedShip = new Minesweeper();
                break;
            case "DESTROYER":
                placedShip = new Destroyer();
                break;
            case "BATTLESHIP":
                placedShip = new Battleship();
                break;
            default:
                return false;
        }
        placedShip.place(y, x, isVertical);
		if (ships.stream().anyMatch(s -> s.overlaps(placedShip))) {
			return false;
		}
		if (placedShip.getOccupiedSquares().stream().anyMatch(s -> s.isOutOfBounds())) {
			return false;
		}
		ships.add(placedShip);
		subShips.add(placedShip);
		return true;
	}

	public boolean placeSubmarine(Ship ship, int x, char y, boolean isVertical) {

	    if (subShips.size() >= 4) {
	        return false;
        }

        if (ships.stream().anyMatch(s -> s.getKind().equals(ship.getKind()))) {
            return false;
        }

        if(subShips.stream().anyMatch((s -> s.getKind().endsWith((ship.getKind()))))) {
            return false;
        }

        if (ship.getKind() != "SUBMARINE") {
            return false;
        }

        Submarine testShip = (Submarine) ship;

	    if(testShip.getSubmerged()) {

			Submarine placedShip = new Submarine(true);

			placedShip.place(y, x, isVertical);

			if (placedShip.getOccupiedSquares().stream().anyMatch(s -> s.isOutOfBounds())) {
				return false;
			}
			subShips.add(placedShip);
			return true;
		}
		else {

            Submarine placedShip = new Submarine(false);

            placedShip.place(y, x, isVertical);

            if (ships.stream().anyMatch(s -> s.overlaps(placedShip))) {
                return false;
            }
            if (placedShip.getOccupiedSquares().stream().anyMatch(s -> s.isOutOfBounds())) {
                return false;
            }
            ships.add(placedShip);
            subShips.add(placedShip);
            System.out.print("where");
            return true;
        }
	}

	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public Result attack(int x, char y) {
		Attack A = new Attack(ships, attacks);
		Result attackResult = A.attack(new Square(x, y));
		if(attackResult.getResult() == AtackStatus.HIDDEN) {
		    //don't add attackResult
            return attackResult;
        }
		attacks.add(attackResult);
		return attackResult;
	}

	public Result laserAttack(int x, char y) {
		LaserAttack A = new LaserAttack(subShips, attacks);
		Result attackResult = A.laserAttack(new Square(x, y));
		if(attackResult.getResult() == AtackStatus.HIDDEN) {
			//don't add attackResult
			return attackResult;
		}
		attacks.add(attackResult);
		return attackResult;
	}

	public boolean sonarPulse(int x, char y){
		sonarOccupiedSquares = new ArrayList<>();
		sonarUnoccupiedSquares = new ArrayList<>();
		sonarMiddleSquare = new ArrayList<>();
		sonarMiddleSquare.add(new Square(x, y));


		int row;
		int col;

		for(int j = -1; j < 2; j++) {
			row = x+j;

			for(int i = -1; i < 2; i++) {
				col = (int)y +i;

				checkSonar(row, col);
			}
		}

		for(int i = -2; i < 5; i=i+4) {
			row = x;
			col = (int)y + i;

			checkSonar(row, col);

			row = x + i;
			col = y;

			checkSonar(row, col);
		}

		return true;
	}


	public void checkSonar(int row, int col) {

        if(sonarIsInBounds(row, (char)col)) {

            boolean isOccupied = sonarIsOccupied(row, (char) col);

            if (isOccupied) {
                var s = new Square(row, (char)col);
                sonarOccupiedSquares.add(s);
            } else {
                var s = new Square(row, (char)col);
                sonarUnoccupiedSquares.add(s);
            }
        }
    }

	public boolean sonarIsInBounds(int x, char y){
		var square = new Square(x, y);
		if(square.isOutOfBounds()){
			return false;
		}
		else
			return true;
	}

	public boolean sonarIsOccupied(int x, char y){
		var square = new Square(x, y);
		//next lines taken from attack() function - not sure what they do
		var shipsAtLocation = ships.stream().filter(ship -> ship.isAtLocation(square)).collect(Collectors.toList());
		if (shipsAtLocation.size() == 0) {
			return false; //square is not occupied
		}
		else
			return true; //square is occupied
	}

	public void moveFleet(char dir){
		boolean ordered = false;
		boolean	shipsMoved;

		if(dir == 'u' || dir == 'd'){
			while (ordered == false){
				ordered = true;

				for (int i = 1; i < ships.size(); i++){
					if(this.ships.get(i).getOccupiedSquares().get(0).getRow() < this.ships.get(i-1).getOccupiedSquares().get(0).getRow());
					{
						Collections.swap(ships,i,i-1);
						ordered=false;
					}
				}
			}
		}

		else if (dir == 'l' || dir == 'r'){
			while (ordered == false){
				ordered = true;

				for(int i = 1; i < ships.size(); i++){
					if(this.ships.get(i).getOccupiedSquares().get(0).getColumn() < this.ships.get(i-1).getOccupiedSquares().get(0).getColumn()){
						Collections.swap(ships,i,i-1);
						ordered = false;
					}
				}
			}
		}

		if (dir == 'u'){
			for(int i = 0; i < ships.size(); i++){
				shipsMoved = moveShipUp(ships.get(i));
			}
		}

		else if (dir == 'd'){
			for(int i = 0; i < ships.size(); i++){
				shipsMoved = moveShipDown(ships.get(i));
			}
		}

		else if (dir == 'r'){
			for (int i = 0; i < ships.size(); i++){
				shipsMoved = moveShipRight(ships.get(i));
			}
		}

		else if (dir == 'l'){
			for (int i = 0; i < ships.size(); i++){
				shipsMoved = moveShipLeft(ships.get(i));
			}
		}
	}

	public boolean moveShipUp(Ship ship){
		List <Square> squaresToCheck = ship.getOccupiedSquares();
		List <Result> hitSquares = new ArrayList<>();

		for (int i = 0; i < squaresToCheck.size(); i++){
			Result test = attack(squaresToCheck.get(i).getRow(),(char)squaresToCheck.get(i).getColumn());
			if (test.getResult() == AtackStatus.HIT){
				hitSquares.add(test);
			}
		}

		for(int i = 0; i < squaresToCheck.size(); i++){
			if(squaresToCheck.get(i).getRow()-1 < 1){
				return false;
			}
			if(shipCollision(squaresToCheck.get(i).getRow()-1,squaresToCheck.get(i).getColumn(),ship)){
				return false;
			}

			if (ship.isSunk()){
				return false;
			}
		}
		for(int i = 0; i < squaresToCheck.size(); i++){
			ship.occupiedSquares.set(i, new Square(squaresToCheck.get(i).getRow() - 1, squaresToCheck.get(i).getColumn()));
		}
		for(int i = 0; i < hitSquares.size(); i++){
			hitSquares.get(i).setLocation(new Square(hitSquares.get(i).getLocation().getRow()-1,hitSquares.get(i).getLocation().getColumn()));
		}

		int t_row = ship.getCaptainsQuarters().getRow();
		char t_col = ship.getCaptainsQuarters().getColumn();
		Square new_CQ = new Square(t_row, ((char) (t_col+2)));
		ship.setCaptainsQuarters(new_CQ);
		return true;

	}

	public boolean moveShipDown(Ship ship){
		List <Square> squaresToCheck = ship.getOccupiedSquares();
		List <Result> hitSquares = new ArrayList<>();

		for (int i = 0; i < squaresToCheck.size(); i++){
			Result test = attack(squaresToCheck.get(i).getRow(),(char)squaresToCheck.get(i).getColumn());
			if (test.getResult() == AtackStatus.HIT){
				hitSquares.add(test);
			}
		}

		for(int i = 0; i < squaresToCheck.size(); i++){
			if(squaresToCheck.get(i).getRow()+1 < 1){
				return false;
			}
			if(shipCollision(squaresToCheck.get(i).getRow()+1, (squaresToCheck.get(i).getColumn()),ship)){
				return false;
			}

			if (ship.isSunk()){
				return false;
			}
		}
		for(int i = 0; i < squaresToCheck.size(); i++){
			ship.occupiedSquares.set(i, new Square(squaresToCheck.get(i).getRow(), (char) (squaresToCheck.get(i).getColumn()-1)));
		}
		for(int i = 0; i < hitSquares.size(); i++){
			hitSquares.get(i).setLocation(new Square(hitSquares.get(i).getLocation().getRow(), (char)(hitSquares.get(i).getLocation().getColumn()-1)));
		}


		int t_row = ship.getCaptainsQuarters().getRow();
		char t_col = ship.getCaptainsQuarters().getColumn();
		Square new_CQ = new Square(t_row, ((char) (t_col+2)));
		ship.setCaptainsQuarters(new_CQ);
		return true;

	}

	public boolean moveShipRight(Ship ship){
		List <Square> squaresToCheck = ship.getOccupiedSquares();
		List <Result> hitSquares = new ArrayList<>();

		for (int i = 0; i < squaresToCheck.size(); i++){
			Result test = attack(squaresToCheck.get(i).getRow(),squaresToCheck.get(i).getColumn());
			if (test.getResult() == AtackStatus.HIT){
				hitSquares.add(test);
			}
		}

		for(int i = 0; i < squaresToCheck.size(); i++){
			if(squaresToCheck.get(i).getRow()+1 > 'J'){
				return false;
			}
			if(shipCollision(squaresToCheck.get(i).getRow(),(char) (squaresToCheck.get(i).getColumn()+1),ship)){
				return false;
			}

			if (ship.isSunk()){
				return false;
			}
		}
		boolean vert = ship.getVertical();
		//ship.place((char)(squaresToCheck.get(0).getColumn()+1),squaresToCheck.get(0).getRow(), vert);
//		for(int i = 0; i < squaresToCheck.size(); i++){
//			ship.occupiedSquares.set(i, new Square(squaresToCheck.get(i).getRow(), (char) (squaresToCheck.get(i).getColumn()+1)));
//		}
		for(int i = 0; i < hitSquares.size(); i++){
			hitSquares.get(i).setLocation(new Square(hitSquares.get(i).getLocation().getRow(), (char) (hitSquares.get(i).getLocation().getColumn()+1)));
		}

		Square temp = ship.getCaptainsQuarters();
		int t_row = ship.getCaptainsQuarters().getRow();
		char t_col = ship.getCaptainsQuarters().getColumn();
		Square new_CQ = new Square(t_row+2,t_col);
		ship.setCaptainsQuarters(new_CQ);
		return true;

	}

	public boolean moveShipLeft(Ship ship){
		List <Square> squaresToCheck = ship.getOccupiedSquares();
		List <Result> hitSquares = new ArrayList<>();

		for (int i = 0; i < squaresToCheck.size(); i++){
			Result test = attack(squaresToCheck.get(i).getRow(),(char)squaresToCheck.get(i).getColumn());
			if (test.getResult() == AtackStatus.HIT){
				hitSquares.add(test);
			}
			//hitSquares.add(attack(squaresToCheck.get(i).getRow(),(char)squaresToCheck.get(i).getColumn()));
		}

		for(int i = 0; i < squaresToCheck.size(); i++){
			if((char) (squaresToCheck.get(i).getRow()-1) < 'A'){
				return false;
			}
			if(shipCollision(squaresToCheck.get(i).getRow(), (char) (squaresToCheck.get(i).getColumn()-1),ship)){
				return false;
			}

			if (ship.isSunk()){
				return false;
			}
		}
		for(int i = 0; i < squaresToCheck.size(); i++){
			ship.occupiedSquares.set(i, new Square(squaresToCheck.get(i).getRow(), (char) (squaresToCheck.get(i).getColumn()+1)));
		}
		for(int i = 0; i < hitSquares.size(); i++){
			hitSquares.get(i).setLocation(new Square(hitSquares.get(i).getLocation().getRow(),(char) (hitSquares.get(i).getLocation().getColumn()+1)));
		}

		int t_row = ship.getCaptainsQuarters().getRow();
		char t_col = ship.getCaptainsQuarters().getColumn();
		Square new_CQ = new Square(t_row+2,t_col);
		ship.setCaptainsQuarters(new_CQ);
		return true;

	}

	public boolean shipCollision(int x, char y, Ship ship){
		for(int i = 0; i < ships.size(); i++){
			if (!ships.get(i).getKind().equals(ship.getKind())){
				List<Square> occupiedSquares = ships.get(i).getOccupiedSquares();
				for(int j = 0; j < occupiedSquares.size(); j++){
					Square location = occupiedSquares.get(j);
					if (location != null){
						if (location.getRow() == x && location.getColumn() == y){
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	List<Ship> getShips() {
		return ships;
	}
}
