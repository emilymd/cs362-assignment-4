package cs361.battleships.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;
import java.util.stream.Collectors;

public class LaserAttack {

    @JsonProperty private List<Ship> ShipsAndSub;
    @JsonProperty private List<Result> attacks;

    public LaserAttack(List<Ship> s, List<Result> a) {

        this.ShipsAndSub = s;
        this.attacks = a;
    }

    public Result laserAttack(Square s) {

        if (attacks.stream().anyMatch(r -> r.getLocation().equals(s))) {

            var attackResult = new Result(s);
            attackResult.setResult(AtackStatus.INVALID);

            return attackResult;
        }

        var shipsAtLocation = ShipsAndSub.stream().filter(ship -> ship.isAtLocation(s)).collect(Collectors.toList());

        if (shipsAtLocation.size() == 0) {

            var attackResult = new Result(s);
            return attackResult;
        }

        Result attackResult;
        attackResult = shipsAtLocation.get(0).attack(s.getRow(), s.getColumn());

        if (attackResult.getResult() == AtackStatus.SUNK) {

            if (ShipsAndSub.stream().allMatch(ship -> ship.isSunk())) {
                attackResult.setResult(AtackStatus.SURRENDER);
            }

        }

        return attackResult;
    }
}