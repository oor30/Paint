package com.example.x3033171.paint;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import java.io.Serializable;

public class DrawArray implements Serializable {    // シリアライズ可能なクラス
    private Path path;
    private int x1, y1, x2, y2;     // 楕円・四角形用の始点・終点座標
    private int color, bold, mode;  // ペンの色、太さ、描画モード

    // コンストラクタ（セーブボタンが押された後、SaveData関数中で呼び出される）
    // フリーハンド用
    DrawArray(Path p, int c, int b) {
        path = p;
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
            canvas.drawPath(path, paint);
        } else if (mode == 2) {     // 楕円モード
            canvas.drawOval(x1, y1, x2, y2, paint);
        } else if (mode == 3) {     // 四角形モード
            canvas.drawRect(x1, y1, x2, y2, paint);
        }
    }
}
