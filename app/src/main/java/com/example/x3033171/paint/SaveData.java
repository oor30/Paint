package com.example.x3033171.paint;

import java.io.Serializable;
import java.util.ArrayList;

class SaveData implements Serializable {    // シリアライズ可能なクラス
    private ArrayList<DrawArray> array_draw;
    private int currentIndex;

    SaveData(MyCanvasView mc) { // セーブボタンが押された後、SaveData関数中でインスタンスを生成され呼び出される
        array_draw = mc.getArray_draw();    // 各データをインスタンス変数に保存
        currentIndex = mc.getCurrentIndex();
    }

    void recover(MyCanvasView mc) { // onCreate→loadDataメソッド中で呼び出される
        mc.setArray_draw(array_draw);   // 各データをmc(MyCanvasView)に渡し、描画情報を回復させる
        mc.setCurrentIndex(currentIndex);
    }
}
