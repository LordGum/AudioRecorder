package com.example.audiorecorder1;

//в этом классе я буду вести список из двух элементов
// 1) position предыдущей песни
// 2) position текущей песни
// чтобы можно было бы поменять PlayButton в recordRecycler


public class fakeDatabase {
    private static fakeDatabase instance = null;
    private int previousRecordPosition;

    private fakeDatabase() {
        previousRecordPosition = 0;
    }

    public static fakeDatabase getInstance() {
        if (instance == null) {
            instance = new fakeDatabase();
        }
        return instance;
    }

    public int getPreviousRecordPosition() {
        return previousRecordPosition;
    }

    public void setPreviousRecordPosition(int previousRecordPosition) {
        this.previousRecordPosition = previousRecordPosition;
    }

}
