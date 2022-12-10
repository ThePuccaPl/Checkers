package com.example.warcaby;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Checkers extends Application {

    public static final int TILE_SIZE = 50;
    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;

    private final Tile board[][] = new Tile[WIDTH][HEIGHT];

    private final Group tiles = new Group();
    private final Group pieces = new Group();
    private Text playerInfo = new Text();
    private Text pointInfo = new Text();
    private PieceType currentPlayer;
    private int bluePoints = 0;
    private int pinkPoints = 0;

    private void changePlayer(){
        if(currentPlayer == PieceType.PINK){
            currentPlayer = PieceType.BLUE;
        }
        else{
            currentPlayer = PieceType.PINK;
        }
        playerInfo.setText("Player " + currentPlayer);
    }

    private Parent createContent() {
        Pane root = new Pane();
        playerInfo.setText("Player " + currentPlayer);
        playerInfo.setX(10);
        playerInfo.setY(415);
        pointInfo.setText("BLUE: " + bluePoints + "\nPINK: " + pinkPoints);
        pointInfo.setX(10);
        pointInfo.setY(430);
        root.setPrefSize(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE+TILE_SIZE);
        Rectangle fillScreen = new Rectangle();
        fillScreen.setFill(Color.WHITE);
        fillScreen.setWidth(WIDTH * TILE_SIZE);
        fillScreen.setHeight(TILE_SIZE);
        fillScreen.relocate(0,HEIGHT * TILE_SIZE);
        root.getChildren().addAll(tiles, pieces,playerInfo,pointInfo);

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Tile tile = new Tile((x + y) % 2 == 0, x, y);
                board[x][y] = tile;
                tiles.getChildren().add(tile);
                Piece piece = null;
                if (y <= 2 && (x + y) % 2 != 0) {
                    piece = makePiece(PieceType.PINK, x, y);
                }
                if (y >= 5 && (x + y) % 2 != 0) {
                    piece = makePiece(PieceType.BLUE, x, y);
                }
                if (piece != null) {
                    tile.setPiece(piece);
                    pieces.getChildren().add(piece);
                }
            }

        }
        return root;
    }

    private MoveResult tryMove(Piece piece, int newX, int newY) {
        if(currentPlayer ==  piece.getType()){
            if (board[newX][newY].hasPiece() || (newX + newY) % 2 == 0) {
                return new MoveResult(MoveType.NONE);
            }
            int x0 = toBoard(piece.getOldX());
            int y0 = toBoard(piece.getOldY());
            if (Math.abs(newX - x0) == 1 && newY - y0 == piece.getType().moveDir) {
                changePlayer();
                return new MoveResult(MoveType.NORMAL);
            }
            else if (Math.abs(newX - x0) == 2 && newY - y0 == piece.getType().moveDir * 2) {

                int x1 = x0 + (newX - x0) / 2;
                int y1 = y0 + (newY - y0) / 2;

                if (board[x1][y1].hasPiece() && board[x1][y1].getPiece().getType() != piece.getType()) {
                    if(piece.getType() == PieceType.PINK){
                        pinkPoints+=1;
                    }
                    else{
                        bluePoints+=1;
                    }
                    pointInfo.setText("BLUE: " + bluePoints + "\nPINK: " + pinkPoints);
                    changePlayer();
                    return new MoveResult(MoveType.KILL, board[x1][y1].getPiece());
                }
            }
            return new MoveResult(MoveType.NONE);
        }
        playerInfo.setText("That's not your piece!");
        return new MoveResult(MoveType.NONE);
    }

    private int toBoard(double pixel) {
        return (int)(pixel + TILE_SIZE / 2) / TILE_SIZE;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        currentPlayer = PieceType.BLUE;
        Scene scene = new Scene(createContent());
        primaryStage.setTitle("Warcaby");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Piece makePiece(PieceType type, int x, int y) {
        Piece piece = new Piece(type, x, y);

        piece.setOnMouseReleased(e -> {
            int newX = toBoard(piece.getLayoutX());
            int newY = toBoard(piece.getLayoutY());

            MoveResult result;

            if (newX < 0 || newY < 0 || newX >= WIDTH || newY >= HEIGHT) {
                result = new MoveResult(MoveType.NONE);
            } else {
                result = tryMove(piece, newX, newY);
            }

            int x0 = toBoard(piece.getOldX());
            int y0 = toBoard(piece.getOldY());

            switch (result.getType()) {
                case NONE:
                    piece.abortMove();
                    break;
                case NORMAL:
                    try{
                        piece.move(newX, newY);
                        board[x0][y0].setPiece(null);
                        board[newX][newY].setPiece(piece);
                    }
                    catch(ArrayIndexOutOfBoundsException asdasdasd){
                        if(y0 == 8){
                            y0 = 7;
                        }
                        if(x0 == 8){
                            x0 = 7;
                        }
                        System.out.println("asdasdsa");
                        piece.move(newX, newY);
                        board[x0][y0].setPiece(null);
                        board[newX][newY].setPiece(piece);
                    }
                    break;
                case KILL:
                    try{
                        piece.move(newX, newY);
                        board[x0][y0].setPiece(null);
                        board[newX][newY].setPiece(piece);
                        Piece otherPiece = result.getPiece();
                        board[toBoard(otherPiece.getOldX())][toBoard(otherPiece.getOldY())].setPiece(null);
                        pieces.getChildren().remove(otherPiece);
                    }
                    catch(ArrayIndexOutOfBoundsException asdadfsd){
                        if(y0 == 8){
                            y0 = 7;
                        }
                        if(x0 == 8){
                            x0 = 7;
                        }
                        piece.move(newX, newY);
                        board[x0][y0].setPiece(null);
                        board[newX][newY].setPiece(piece);
                        Piece otherPiece = result.getPiece();
                        board[toBoard(otherPiece.getOldX())][toBoard(otherPiece.getOldY())].setPiece(null);
                        pieces.getChildren().remove(otherPiece);
                    }
                    break;
            }
        });

        return piece;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
