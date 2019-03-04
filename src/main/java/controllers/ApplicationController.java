package controllers;

import com.google.inject.Singleton;
import cs361.battleships.models.*;
import ninja.Context;
import ninja.Result;
import ninja.Results;

@Singleton
public class ApplicationController {

    public Result index() {
        return Results.html();
    }

    public Result newGame() {
        Game g = new Game();
        return Results.json().render(g);
    }

    public Result placeShip(Context context, PlacementGameAction g) {
        Game game = g.getGame();
        Ship ship;

        switch(g.getShipType()) {
            case "MINESWEEPER":
                ship = new Minesweeper();
                break;
            case "DESTROYER":
                ship = new Destroyer();
                break;
            case "BATTLESHIP":
                ship = new Battleship();
                break;
            case "SUBMARINE":
                System.out.print("never gets here\n");
                ship = new Submarine(false);
                System.out.print("????\n");
                break;
            default:
                return Results.badRequest();
        }
        System.out.print("done with app\n");
        boolean result = game.placeShip(ship, g.getActionRow(), g.getActionColumn(), g.isVertical());
        if (result) {
            return Results.json().render(game);
        } else {
            return Results.badRequest();
        }
    }

    public Result attack(Context context, AttackGameAction g) {
        Game game = g.getGame();
        boolean result = game.attack(g.getActionRow(), g.getActionColumn());
        if (result) {
            return Results.json().render(game);
        } else {
            return Results.badRequest();
        }
    }

    public Result laserAttack(Context context, LaserAttackGameAction g) {
        Game game = g.getGame();
        boolean result = game.laserAttack(g.getActionRow(), g.getActionColumn());
        if (result) {
            return Results.json().render(game);
        } else {
            return Results.badRequest();
        }
    }

    public Result sonarPulse(Context context, SonarPulseGameAction g) {
        Game game = g.getGame();
        boolean result = game.sonarPulse(g.getActionRow(), g.getActionColumn());
        if(result){
            return Results.json().render(game);
        } else {
            return Results.badRequest();
        }
    }
}
