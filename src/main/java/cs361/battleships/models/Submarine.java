package cs361.battleships.models;

public class Submarine extends Ship {

    private boolean submerged;

    public Submarine(boolean submerged) {

        super("SUBMARINE", 5);

        this.submerged = submerged;

    }

    public boolean getSubmerged() { return submerged; }

    @Override
    public void place(char col, int row, boolean isVertical) {

        if(isVertical) {
            for(int i = 0; i < 4; i++) {
                occupiedSquares.add(new Square(row + i, (col)));
            }
            captainsQuarters = occupiedSquares.get(3);
            occupiedSquares.add(new Square(row + 2, (char) (col + 1)));
        }
        else {
            for (int i = 0; i < 4; i++) {
                occupiedSquares.add(new Square(row, (char) (col + i)));
            }
            captainsQuarters = occupiedSquares.get(3);
            occupiedSquares.add(new Square(row - 1, (char) (col + 2)));
        }
    }
}
