package cs361.battleships.models;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ShipTest {

    @Test
    public void testPlaceMinesweeperHorizontally() {
        Ship minesweeper = new Minesweeper();
        minesweeper.place('A', 1, false);
        List<Square> occupiedSquares = minesweeper.getOccupiedSquares();
        ArrayList<Object> expected = new ArrayList<>();
        expected.add(new Square(1, 'A'));
        expected.add(new Square(1, 'B'));
        assertEquals(expected, occupiedSquares);
        Square captain = new Square( 1, 'A');
        assertEquals(captain, minesweeper.getCaptainsQuarters());
    }

    @Test
    public void testPlaceMinesweeperVertically() {
        Ship minesweeper = new Minesweeper();
        minesweeper.place('A', 1, true);
        List<Square> occupiedSquares = minesweeper.getOccupiedSquares();
        ArrayList<Object> expected = new ArrayList<>();
        expected.add(new Square(1, 'A'));
        expected.add(new Square(2, 'A'));
        assertEquals(expected, occupiedSquares);
        Square captain = new Square( 1, 'A');
        assertEquals(captain, minesweeper.getCaptainsQuarters());
    }

    @Test
    public void testPlaceDestroyerHorizontally() {
        Ship minesweeper = new Destroyer();
        minesweeper.place('A', 1, false);
        List<Square> occupiedSquares = minesweeper.getOccupiedSquares();
        ArrayList<Object> expected = new ArrayList<>();
        expected.add(new Square(1, 'A'));
        expected.add(new Square(1, 'B'));
        expected.add(new Square(1, 'C'));
        assertEquals(expected, occupiedSquares);
        Square captain = new Square( 1, 'B');
        assertEquals(captain, minesweeper.getCaptainsQuarters());
    }

    @Test
    public void testPlaceDestroyerVertically() {
        Ship minesweeper = new Destroyer();
        minesweeper.place('A', 1, true);
        List<Square> occupiedSquares = minesweeper.getOccupiedSquares();
        ArrayList<Object> expected = new ArrayList<>();
        expected.add(new Square(1, 'A'));
        expected.add(new Square(2, 'A'));
        expected.add(new Square(3, 'A'));
        assertEquals(expected, occupiedSquares);
        Square captain = new Square( 2, 'A');
        assertEquals(captain, minesweeper.getCaptainsQuarters());
    }

    @Test
    public void testPlaceBattleshipHorizontally() {
        Ship minesweeper = new Battleship();
        minesweeper.place('A', 1, false);
        List<Square> occupiedSquares = minesweeper.getOccupiedSquares();
        ArrayList<Object> expected = new ArrayList<>();
        expected.add(new Square(1, 'A'));
        expected.add(new Square(1, 'B'));
        expected.add(new Square(1, 'C'));
        expected.add(new Square(1, 'D'));
        assertEquals(expected, occupiedSquares);
        Square captain = new Square( 1, 'C');
        assertEquals(captain, minesweeper.getCaptainsQuarters());
    }

    @Test
    public void testPlaceBattleshipVertically() {
        Ship minesweeper = new Battleship();
        minesweeper.place('A', 1, true);
        List<Square> occupiedSquares = minesweeper.getOccupiedSquares();
        ArrayList<Object> expected = new ArrayList<>();
        expected.add(new Square(1, 'A'));
        expected.add(new Square(2, 'A'));
        expected.add(new Square(3, 'A'));
        expected.add(new Square(4, 'A'));
        assertEquals(expected, occupiedSquares);
        Square captain = new Square( 3, 'A');
        assertEquals(captain, minesweeper.getCaptainsQuarters());
    }

    @Test
    public void testShipOverlaps() {
        Ship minesweeper1 = new Minesweeper();
        minesweeper1.place('A', 1, true);

        Ship minesweeper2 = new Minesweeper();
        minesweeper2.place('A', 1, true);

        assertTrue(minesweeper1.overlaps(minesweeper2));
    }

    @Test
    public void testShipsDontOverlap() {
        Ship minesweeper1 = new Minesweeper();
        minesweeper1.place('A', 1, true);

        Ship minesweeper2 = new Minesweeper();
        minesweeper2.place('C', 2, true);

        assertFalse(minesweeper1.overlaps(minesweeper2));
    }

    @Test
    public void testIsAtLocation() {
        Ship minesweeper = new Battleship();
        minesweeper.place('A', 1, true);

        assertTrue(minesweeper.isAtLocation(new Square(1, 'A')));
        assertTrue(minesweeper.isAtLocation(new Square(2, 'A')));
    }

    @Test
    public void testHit() {
        Ship minesweeper = new Battleship();
        minesweeper.place('A', 1, true);

        Result result = minesweeper.attack(1, 'A');
        assertEquals(AtackStatus.HIT, result.getResult());
        assertEquals(minesweeper, result.getShip());
        assertEquals(new Square(1, 'A'), result.getLocation());
    }

    @Test
    public void testSink() {
        Ship minesweeper = new Minesweeper();
        minesweeper.place('A', 1, true);

        Result result = minesweeper.attack(1, 'A');
        minesweeper.attack(2, 'A');

        assertEquals(AtackStatus.SUNK, result.getResult());
        assertEquals(minesweeper, result.getShip());
        assertEquals(new Square(1, 'A'), result.getLocation());
    }

    @Test
    public void testOverlapsBug() {
        Ship minesweeper = new Minesweeper();
        Ship destroyer = new Destroyer();
        minesweeper.place('C', 5, false);
        destroyer.place('C', 5, false);
        assertTrue(minesweeper.overlaps(destroyer));
    }

    @Test
    public void testAttackSameSquareTwice() {
        Ship minesweeper = new Minesweeper();
        minesweeper.place('A', 1, true);
        var result = minesweeper.attack(2, 'A');
        assertEquals(AtackStatus.HIT, result.getResult());
        result = minesweeper.attack(2, 'A');
        assertEquals(AtackStatus.INVALID, result.getResult());
    }

    @Test
    public void testEquals() {
        Ship minesweeper1 = new Minesweeper();
        minesweeper1.place('A', 1, true);
        Ship minesweeper2 = new Minesweeper();
        minesweeper2.place('A', 1, true);
        assertTrue(minesweeper1.equals(minesweeper2));
        assertEquals(minesweeper1.hashCode(), minesweeper2.hashCode());
    }

    @Test
    public void testCapHit() {
        Ship destroyer = new Destroyer();
        destroyer.place('A', 1, true);
        var result = destroyer.attack(2, 'A');
        assertEquals(AtackStatus.HIDDEN, result.getResult());
    }

    @Test
    public void testCapSunk () {
        Ship battleship = new Destroyer();
        battleship.place('A', 1, true);
        battleship.attack(2,'A');
        var result = battleship.attack(2,'A');
        assertEquals(AtackStatus.SUNK, result.getResult());
    }

    @Test
    public void testPlaceSubmarineHorizontally() {
        Ship submarine = new Submarine(false);
        submarine.place('A', 2, false);
        List<Square> occupiedSquares = submarine.getOccupiedSquares();
        ArrayList<Object> expected = new ArrayList<>();
        expected.add(new Square(2, 'A'));
        expected.add(new Square(2, 'B'));
        expected.add(new Square(2, 'C'));
        expected.add(new Square(2, 'D'));
        expected.add(new Square(1, 'C'));
        assertEquals(expected, occupiedSquares);
        Square captain = new Square( 2, 'D');
        assertEquals(captain, submarine.getCaptainsQuarters());
    }

    @Test
    public void testPlaceSubmarineVertically() {
        Ship submarine = new Submarine(false);
        submarine.place('A', 1, true);
        List<Square> occupiedSquares = submarine.getOccupiedSquares();
        ArrayList<Object> expected = new ArrayList<>();
        expected.add(new Square(1, 'A'));
        expected.add(new Square(2, 'A'));
        expected.add(new Square(3, 'A'));
        expected.add(new Square(4, 'A'));
        expected.add(new Square(3, 'B'));
        assertEquals(expected, occupiedSquares);
        Square captain = new Square( 4, 'A');
        assertEquals(captain, submarine.getCaptainsQuarters());
    }
}
