package cs361.battleships.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.stream.Collectors;

public class Attack {

    @JsonProperty private List<Ship> ships;
    @JsonProperty private List<Result> attacks;


    public Attack(List<Ship> s, List<Result> a) {

        this.ships = s;
        this.attacks = a;
    }

    public Result attack(Square s) {
        if (attacks.stream().anyMatch(r -> r.getLocation().equals(s))) {
            var attackResult = new Result(s);
            attackResult.setResult(AtackStatus.INVALID);
            return attackResult;
        }

        var shipsAtLocation = ships.stream().filter(ship -> ship.isAtLocation(s)).collect(Collectors.toList());
        if (shipsAtLocation.size() == 0) {
            var attackResult = new Result(s);
            return attackResult;
        }

        Result attackResult;
        attackResult = shipsAtLocation.get(0).attack(s.getRow(), s.getColumn());

        if (attackResult.getResult() == AtackStatus.SUNK) {
            if (ships.stream().allMatch(ship -> ship.isSunk())) {
                attackResult.setResult(AtackStatus.SURRENDER);
            }
        }
        return attackResult;
    }
}
