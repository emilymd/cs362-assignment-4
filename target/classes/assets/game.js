var isSetup = true;
var placedShips = 0;
var game;
var shipType;
var vertical;
var numShipsSunk = 0;
var numSonarUsed = 0;
var sonarIsChecked = false;
var timesMovedFleet = 0;


function makeGrid(table, isPlayer) {
    for (i=0; i<10; i++) {
        let row = document.createElement('tr');
        for (j=0; j<10; j++) {
            let column = document.createElement('td');
            column.addEventListener("click", cellClick);
            row.appendChild(column);
        }
        table.appendChild(row);
    }
}

function markHits(board, elementId, surrenderText) {
    board.attacks.forEach((attack) => {
        let className;
        if (attack.result === "MISS")
            className = "miss";
        else if (attack.result === "HIT")
            className = "hit";
        else if (attack.result === "SUNK"){
            var occupy;
            numShipsSunk++;
            //console.log("SUNK");
            //alert(surrenderText);
            className = "sunk";
            document.getElementById(elementId).rows[attack.location.row-1].cells[attack.location.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.remove("placed");
            for (occupy of attack.ship.occupiedSquares){
                document.getElementById(elementId).rows[occupy.row-1].cells[occupy.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.add(className);
                document.getElementById(elementId).rows[occupy.row-1].cells[occupy.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.remove("hit");
            }
        }
//        else if (attack.result == "HIDDEN"){
//            console.log("HIDDEN");
//            className = "hidden";
//        }
        else if (attack.result === "SURRENDER")
            alert(surrenderText);
        document.getElementById(elementId).rows[attack.location.row-1].cells[attack.location.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.add(className);
    });
}

function redrawGrid() {
    Array.from(document.getElementById("opponent").childNodes).forEach((row) => row.remove());
    Array.from(document.getElementById("player").childNodes).forEach((row) => row.remove());
    makeGrid(document.getElementById("opponent"), false);
    makeGrid(document.getElementById("player"), true);
    if (game === undefined) {
        return;
    }

    game.playersBoard.ships.forEach((ship) => ship.occupiedSquares.forEach((square) => {
        document.getElementById("player").rows[square.row-1].cells[square.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.add("occupied");
    }));
    markHits(game.opponentsBoard, "opponent", "Congrats Player 1, you won the game! \nPress 'New Game' to play another round!");
    markHits(game.playersBoard, "player", "Sorry Player 1, you lost the game... \nPress the 'New Game' button to try again!");
}


function drawSonarGrid() {
    Array.from(document.getElementById("opponent").childNodes).forEach((row) => row.remove());
    Array.from(document.getElementById("player").childNodes).forEach((row) => row.remove());
    makeGrid(document.getElementById("opponent"), false);
    makeGrid(document.getElementById("player"), true);
    if (game === undefined) {
        return;
    }

    game.playersBoard.ships.forEach((ship) => ship.occupiedSquares.forEach((square) => {
        document.getElementById("player").rows[square.row-1].cells[square.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.add("occupied");
    }));


    game.opponentsBoard.sonarOccupiedSquares.forEach((square) => {
        document.getElementById("opponent").rows[square.row-1].cells[square.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.add("sonarOccupied");
    });
    game.opponentsBoard.sonarMiddleSquare.forEach((square) => {
        document.getElementById("opponent").rows[square.row-1].cells[square.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.add("sonarCenter");
    });
    game.opponentsBoard.sonarUnoccupiedSquares.forEach((square) => {
                document.getElementById("opponent").rows[square.row-1].cells[square.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.add("sonarUnoccupied");
        });

    markHits(game.opponentsBoard, "opponent", "Congrats Player 1, you won the game! \nPress 'New Game' to play another round!");
    markHits(game.playersBoard, "player", "Sorry Player 1, you lost the game... \nPress the 'New Game' button to try again!");
}

var oldListener;
function registerCellListener(f) {
    let el = document.getElementById("player");
    for (i=0; i<10; i++) {
        for (j=0; j<10; j++) {
            let cell = el.rows[i].cells[j];
            cell.removeEventListener("mouseover", oldListener);
            cell.removeEventListener("mouseout", oldListener);
            cell.addEventListener("mouseover", f);
            cell.addEventListener("mouseout", f);
        }
    }
    oldListener = f;
}

function cellClick() {
    let row = this.parentNode.rowIndex + 1;
    let col = String.fromCharCode(this.cellIndex + 65);
    if (isSetup) {
        sendXhr("POST", "/place", {game: game, shipType: shipType, x: row, y: col, isVertical: vertical}, function(data) {
            game = data;
            redrawGrid();
            placedShips++;
            if (placedShips == 3) {
                isSetup = false;
                registerCellListener((e) => {});
            }
        });
    } else {

        if (sonarIsChecked){
            if(numShipsSunk < 1 || numSonarUsed >= 2){
                alert("Sorry, you can't do that! \nAt least one Enemy Ship must be sunk and \nyou may only use Sonar Pulse twice per game!");
            }
            else{
                numSonarUsed++;
                sendXhr("POST", "/sonar", {game: game, x: row, y: col}, function(data) {
                    game = data;
                    drawSonarGrid();
                })
            }
            sonarIsChecked=false;
            document.getElementById("run_Sonar").checked=false;
        }

        //if()
        else{
            sendXhr("POST", "/attack", {game: game, x: row, y: col}, function(data) {
                game = data;
                redrawGrid();
            })
        }
    }
}

function sendXhr(method, url, data, handler) {
    var req = new XMLHttpRequest();
    req.addEventListener("load", function(event) {
        if (req.status != 200) {
            alert("Cannot complete the action");
            return;
        }
        handler(JSON.parse(req.responseText));
    });
    req.open(method, url);
    req.setRequestHeader("Content-Type", "application/json");
    req.send(JSON.stringify(data));
}

function moveFleet(inputDir){
    sendXhr("POST","/moveFleet", {game: game, direction: inputDir}, function(data){
        if(timesMovedFleet == 2){
            alert("Sorry, you can't do that! \nYou've already moved your fleet.");
        }
        if(numShipsSunk != 2){
            alert("Sorry, you can't do that! \nYou have to sink 2 enemy ships\nin order to move your fleet.");
        }
        else{
            game = data;
            redrawGrid();
            timesMovedFleet++;
        }


    });
}

function moveFleetUp(){
    //alert("moving up");
    moveFleet('u');
}
function moveFleetRight(){
    //alert("moving right");
    moveFleet('r');
}

function moveFleetLeft(){
    //alert("moving left");
    moveFleet('l');
}

function moveFleetDown(){
    //alert("moving down");
    moveFleet('d');
}

document.getElementById("up").addEventListener("click",moveFleetUp);
document.getElementById("right").addEventListener("click",moveFleetRight);
document.getElementById("left").addEventListener("click",moveFleetLeft);
document.getElementById("down").addEventListener("click",moveFleetDown);

function place(size) {
    return function() {
        let row = this.parentNode.rowIndex;
        let col = this.cellIndex;
        vertical = document.getElementById("is_vertical").checked;
        let table = document.getElementById("player");
        for (let i=0; i<size; i++) {
            let cell;
            if(vertical) {
                let tableRow = table.rows[row+i];
                if (tableRow === undefined) {
                    // ship is over the edge; let the back end deal with it
                    break;
                }
                cell = tableRow.cells[col];
            } else {
                cell = table.rows[row].cells[col+i];
            }
            if (cell === undefined) {
                // ship is over the edge; let the back end deal with it
                break;
            }
            cell.classList.toggle("placed");
        }
    }
}

function initGame() {
    makeGrid(document.getElementById("opponent"), false);
    makeGrid(document.getElementById("player"), true);
    document.getElementById("run_Sonar").addEventListener("click", function(e){
            sonarIsChecked=true;
    });
//    document.getElementById("up").addEventListener("click",moveFleetUp);
//    document.getElementById("right").addEventListener("click",moveFleetRight);
//    document.getElementById("left").addEventListener("click",moveFleetLeft);
//    document.getElementById("down").addEventListener("click",moveFleetDown);

    document.getElementById("place_minesweeper").addEventListener("click", function(e) {
        shipType = "MINESWEEPER";
       registerCellListener(place(2));
    });
    document.getElementById("place_destroyer").addEventListener("click", function(e) {
        shipType = "DESTROYER";
       registerCellListener(place(3));
    });
    document.getElementById("place_battleship").addEventListener("click", function(e) {
        shipType = "BATTLESHIP";
       registerCellListener(place(4));
    });
    sendXhr("GET", "/game", {}, function(data) {
        game = data;
    });
};