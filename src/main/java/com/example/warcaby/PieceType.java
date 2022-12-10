package com.example.warcaby;

public enum PieceType{
    PINK(1), BLUE(-1);

    final int moveDir;

    PieceType(int moveDir) {
        this.moveDir = moveDir;
    }

}
