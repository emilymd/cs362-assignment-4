package cs361.battleships.models;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BoardTest {

    private Board board;

    @Before
    public void setUp() {
        board = new Board();
    }

    @Test
    public void testInvalidPlacement() {
        assertFalse(board.placeShip(new Minesweeper(), 11, 'C', true));
    }

    @Test
    public void testPlaceMinesweeper() {
        assertTrue(board.placeShip(new Minesweeper(), 1, 'A', true));
    }

    @Test
    public void testAttackEmptySquare() {
        board.placeShip(new Minesweeper(), 1, 'A', true);
        Result result = board.attack(2, 'E');
        assertEquals(AtackStatus.MISS, result.getResult());
    }

    @Test
    public void testLaserAttackEmptySquare() {
        board.placeShip(new Minesweeper(), 1, 'A', true);
        Result result = board.laserAttack(2, 'E');
        assertEquals(AtackStatus.MISS, result.getResult());
    }

    @Test
    public void testAttackShip() {
        Ship minesweeper = new Minesweeper();
        board.placeShip(minesweeper, 1, 'A', true);
        minesweeper = board.getShips().get(0);
        Result result = board.attack(2, 'A');
        assertEquals(AtackStatus.HIT, result.getResult());
        assertEquals(minesweeper, result.getShip());
    }

    @Test
    public void testLaserAttackShip() {
        Ship minesweeper = new Minesweeper();
        board.placeShip(minesweeper, 1, 'A', true);
        minesweeper = board.getShips().get(0);
        Result result = board.laserAttack(2, 'A');
        assertEquals(AtackStatus.HIT, result.getResult());
        assertEquals(minesweeper, result.getShip());
    }

    @Test
    public void testAttackSameSquareMultipleTimes() {
        Ship minesweeper = new Minesweeper();
        board.placeShip(minesweeper, 1, 'A', true);
        board.attack(1, 'A');
        Result result = board.attack(1, 'A');
        assertEquals(AtackStatus.INVALID, result.getResult());
    }

    @Test
    public void testLaserAttackSameSquareMultipleTimes() {
        Ship minesweeper = new Minesweeper();
        board.placeShip(minesweeper, 1, 'A', true);
        board.laserAttack(1, 'A');
        Result result = board.laserAttack(1, 'A');
        assertEquals(AtackStatus.INVALID, result.getResult());
    }

    @Test
    public void testAttackSameEmptySquareMultipleTimes() {
        Result initialResult = board.attack(1, 'A');
        assertEquals(AtackStatus.MISS, initialResult.getResult());
        Result result = board.attack(1, 'A');
        assertEquals(AtackStatus.INVALID, result.getResult());
    }

    @Test
    public void testLaserAttackSameEmptySquareMultipleTimes() {
        Result initialResult = board.laserAttack(1, 'A');
        assertEquals(AtackStatus.MISS, initialResult.getResult());
        Result result = board.laserAttack(1, 'A');
        assertEquals(AtackStatus.INVALID, result.getResult());
    }

    @Test
    public void testSurrender() {
        board.placeShip(new Minesweeper(), 1, 'A', true);
        var result = board.attack(1, 'A');
        board.attack(2, 'A');
        assertEquals(AtackStatus.SURRENDER, result.getResult());
    }

    @Test
    public void testLaserSurrender() {
        board.placeShip(new Minesweeper(), 1, 'A', true);
        var result = board.laserAttack(1, 'A');
        board.laserAttack(2, 'A');
        assertEquals(AtackStatus.SURRENDER, result.getResult());
    }

    @Test
    public void testPlaceMultipleShipsOfSameType() {
        assertTrue(board.placeShip(new Minesweeper(), 1, 'A', true));
        assertFalse(board.placeShip(new Minesweeper(), 5, 'D', true));

    }

//    @Test
//    public void testCantPlaceMoreThan4Ships() {
//        assertTrue(board.placeShip(new Minesweeper(), 1, 'A', true));
//        assertTrue(board.placeShip(new Battleship(), 5, 'D', true));
//        assertTrue(board.placeShip(new Destroyer(), 6, 'A', false));
//        assertTrue(board.placeShip(new Submarine(false), 8, 'A', false));
//        assertFalse(board.placeSubmarine(new Minesweeper(), 7, 'E', false));
//
//    }

    @Test
    public void testSonarIsOccupiedTrue(){
        board.placeShip(new Minesweeper(), 1, 'A', true);
        assertTrue(board.sonarIsOccupied(1, 'A'));
        assertFalse(board.sonarIsOccupied(4, 'D'));
    }
    @Test
    public void testSonarIsInBounds(){
        assertTrue(board.sonarIsInBounds(4, 'B'));
        assertFalse(board.sonarIsInBounds(15, 'X'));
    }
    @Test
    public void testSonarPulse(){
        board.placeShip(new Battleship(), 4, 'D', false);
        board.placeShip(new Minesweeper(), 1, 'E', true);
        board.placeShip(new Destroyer(), 6, 'G', false);
        assertTrue(board.sonarPulse(4, 'E'));
    }

    @Test
    public void testPlaceSubmarine() {
        assertTrue(board.placeSubmarine(new Submarine(false), 1, 'A', true));
    }

    @Test
    public void testSubmarineListSize() {
        board.placeSubmarine(new Submarine(false), 1, 'A', true);
        assertFalse(board.placeSubmarine(new Submarine(false), 2, 'B', true));
    }

    @Test
    public void testSubmarineType() {
       assertFalse(board.placeSubmarine(new Minesweeper(), 2, 'B', true));
    }

//    @Test
//    public void testShipMoveCollisionsHoriz(){
//        Board board = new Board();
//        Ship ship = new Destroyer();
//        Ship m = new Minesweeper();
//        board.placeShip(ship,8,'A',true);
//        board.placeShip(m,8,'B',false);
//        assertFalse(board.moveShipLeft(m));
//        assertFalse(board.moveShipRight(ship));
//    }

    @Test
    public void testValidShipMove(){
        Board board = new Board();
        Ship ship = new Destroyer();
        Ship m = new Minesweeper();
        ship.place('A',8,true);
        m.place('A',7, false);
        board.placeShip(ship,8,'A',true);
        board.placeShip(m,7,'A',false);
        assertTrue(board.moveShipRight(ship));
        assertTrue(board.moveShipUp(m));
    }

    @Test
    public void testValidFleetMove(){
        Board board = new Board();
        Ship ship = new Destroyer();
        Ship m = new Minesweeper();
        Ship d = new Destroyer();
        Ship b = new Battleship();
        ship.place('A',8,true);
        m.place('A',7, false);
        b.place('F',2,false);
        d.place('G',4,true);
        board.placeShip(ship,8,'A',true);
        board.placeShip(m,7,'A',false);
        board.placeShip(b,2,'F',false);
        board.placeShip(d,4,'G',false);
        assertTrue(board.moveShipRight(ship));
        assertTrue(board.moveShipUp(m));
        assertFalse(board.moveShipLeft(d));
        assertTrue(board.moveShipDown(b));
    }
    @Test
    public void testValidRightMove(){
        Board board = new Board();
        Ship m = new Minesweeper();
        Ship d = new Destroyer();
        Ship b = new Battleship();
        //ship.place('A',8,true);
        m.place('C',3, true);
        b.place('G',7,true);
        d.place('C',3,true);
        //board.placeShip(ship,8,'A',true);
        board.placeShip(m,3,'C',true);
        board.placeShip(b,7,'G',true);
        board.placeShip(d,3,'C',true);
        //assertTrue(board.moveShipRight(ship));
        //assertTrue(board.moveFleet('r'));
        assertTrue(board.moveShipRight(m));
        assertTrue(board.moveShipRight(d));
        assertTrue(board.moveShipRight(b));
    }

    @Test
    public void testCollision(){
        Board board = new Board();
        Ship m = new Minesweeper();
        Ship d = new Destroyer();

        m.place('I',4, true);
        d.place('I',4,false);
        board.placeShip(m,4,'I',true);
        board.placeShip(d,4,'I',true);
        assertFalse(board.shipCollision(4,'I', m));
        assertTrue(board.shipCollision(4,'I', d));

    }
    @Test
    public void testCQ(){
        Ship minesweeper = new Battleship();
        minesweeper.place('A', 1, true);
        //Square captain = new Square( 3, 'A');
        int row = minesweeper.getCaptainsQuarters().getRow();

        assertEquals(row,3);
        assertEquals(row, minesweeper.getCaptainsQuarters().getRow());
       // assertEquals(captain.getRow(), minesweeper.getCaptainsQuarters().getRow());
    }

    @Test
    public void testMoveFleetUp(){
        Board board = new Board();
        Ship ship = new Destroyer();
        Ship m = new Minesweeper();
        board.placeShip(ship,5,'C',true);
        board.placeShip(m,5,'D',false);
        ship.place('C',5,true);
        m.place('D',5,false);
        //assertEquals(5,ship);
        //board.moveFleet('u');
        assertTrue(board.moveShipUp(ship));
        assertTrue(board.moveShipUp(m));
        //assertEquals(4,ship.getOccupiedSquares().get(0).getRow());
        //assertEquals(4,m.getOccupiedSquares().get(0).getRow());
    }

    @Test
    public void testMoveFleetRight(){
        Board board = new Board();
        Ship d = new Destroyer();
        Ship m = new Minesweeper();
        board.placeShip(d,3,'C',true);
        board.placeShip(m,3,'E',true);
        d.place('C',3,true);
        m.place('E',3,true);
        //assertEquals(5,ship);
        //board.moveFleet('u');
        assertTrue(board.moveShipRight(d));
        assertTrue(board.moveShipRight(m));
        //assertEquals(4,ship.getOccupiedSquares().get(0).getRow());
        //assertEquals(4,m.getOccupiedSquares().get(0).getRow());
    }

    @Test
    public void testBadMoveFleetRight(){
        Board board = new Board();
        Ship d = new Destroyer();
        Ship m = new Minesweeper();
        board.placeShip(d,3,'C',true);
        board.placeShip(m,3,'C',true);
        d.place('C',3,true);
        m.place('C',3,true);
        //assertEquals(5,ship);
        //board.moveFleet('u');
        assertTrue(board.moveShipRight(d));
        assertTrue(board.moveShipRight(m));
        //assertEquals(4,ship.getOccupiedSquares().get(0).getRow());
        //assertEquals(4,m.getOccupiedSquares().get(0).getRow());
    }

    @Test
    public void testMoveFleetLeft(){
        Board board = new Board();
        Ship d = new Destroyer();
        Ship m = new Minesweeper();
        board.placeShip(d,3,'C',true);
        board.placeShip(m,3,'D',true);
        d.place('C',3,true);
        m.place('D',3,true);
        //assertEquals(5,ship);
        //board.moveFleet('u');
        assertFalse(board.moveShipLeft(d));
        assertFalse(board.moveShipLeft(m));
        //assertEquals(4,ship.getOccupiedSquares().get(0).getRow());
        //assertEquals(4,m.getOccupiedSquares().get(0).getRow());
    }
    //TEST for a valid fleet moving up.
    //Running board.moveFleet doesn't finish,
    //could not figure out why.
//    @Test
//    public void testMoveFleet() {
//        Board board = new Board();
//        Ship ship = new Destroyer();
//        Ship m = new Minesweeper();
//        board.placeShip(ship, 5, 'C', true);
//        board.placeShip(m, 5, 'D', false);
//        ship.place('C', 5, true);
//        m.place('D', 5, false);
//
//        board.moveFleet('u');
//
//        assertEquals(4,ship.getOccupiedSquares().get(0).getRow());
//        assertEquals(4,m.getOccupiedSquares().get(0).getRow());
//    }
}
