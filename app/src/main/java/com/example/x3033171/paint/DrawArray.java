package com.example.x3033171.paint;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.Serializable;
import java.util.ArrayList;

public class DrawArray implements Serializable {    // シリアライズ可能なクラス
    private ArrayList array_x, array_y; //フリーハンド用の筆跡
    private int x1, y1, x2, y2;     // 楕円・四角形用の始点・終点座標
    private int color, bold, mode;  // ペンの色、太さ、描画モード

    // コンストラクタ（セーブボタンが押された後、SaveData関数中で呼び出される）
    // フリーハンド用
    DrawArray(ArrayList x, ArrayList y, int c, int b) {
        array_x = x;    // 各データをインスタンス変数に保存
        array_y = y;
        color = c;
        bold = b;
        mode = 1;
    }

    // 楕円・四角形用
    DrawArray(int x1, int y1, int x2, int y2, int c, int b, int m) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        color = c;
        bold = b;
        mode = m;
    }

    public int getColor() { return color; }

    // 描画メソッド（mc(MyCanvasView)からCanvas,Paintを受け取り、描画を行う）
    void myDraw(Canvas canvas, Paint paint) {
        paint.setColor(color);          // ペンの色を設定
        paint.setStrokeWidth(bold);     // ペンの太さを設定

        if (mode == 1) {    // フリーハンドモード
            for (int i=1; i<array_x.size(); i++) {  // 筆跡をたどって描画
                int x1 = (int) array_x.get(i-1);
                int y1 = (int) array_y.get(i-1);
                int x2 = (int) array_x.get(i);
                int y2 = (int) array_y.get(i);
                canvas.drawLine(x1, y1, x2, y2, paint);
            }
        } else if (mode == 2) {     // 楕円モード
            canvas.drawOval(x1, y1, x2, y2, paint);
        } else if (mode == 3) {     // 四角形モード
            canvas.drawRect(x1, y1, x2, y2, paint);
        }
    }
}
