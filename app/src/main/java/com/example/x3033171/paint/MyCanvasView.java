package com.example.x3033171.paint;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MyCanvasView extends View {

    private ArrayList<Integer> array_x, array_y;    // 筆跡の座標を保存用
    private ArrayList<DrawArray> array_draw;        // DrawArray(筆跡、色、太さ等を保存するクラス)のArrayList
    private int x1, y1, x2, y2;                     // 始点・終点座標
    private int color, bold, mode, currentIndex;    // 色、太さ、描画モード、array_drawから表示する最後のインデックス
    private boolean drawingIs;                      // 描画中か否か

    // コンストラクタ3種
    public MyCanvasView(Context context) {
        super(context);
        init();
    }

    public MyCanvasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MyCanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    // 初期設定
    private void init() {
        array_draw = new ArrayList<>();
        color = Color.BLACK;    // ペンの色は黒色に
        bold = 3;               // ペンの太さは3に
        mode = 1;               // 描画モードはフリーハンドに
        currentIndex = -1;      // 1つも描画しない
        drawingIs = false;      // 描画中ではない
    }

    // ゲッター、セッターメソッド
    public ArrayList<DrawArray> getArray_draw() { return array_draw; }

    public int getCurrentIndex() { return currentIndex; }

    public void setArray_draw(ArrayList<DrawArray> array_draw) { this.array_draw = array_draw; }

    public void setCurrentIndex(int currentIndex) { this.currentIndex = currentIndex; }

    public void setPenColor(int c) { color = c; }

    public int getPenColor() { return color; }

    public void setPenBold(int b) { bold = b; }

    public int getBold() { return bold; }

    public void setMode(int m) { mode = m; }

    public int getMode() { return mode; }

    // Clearメソッド
    public void clear() {
        array_draw.clear();     // 初期化
        currentIndex = -1;      // 1つも描画しない
        invalidate();           // 再描画
    }

    // Undoメソッド
    public void undo() {
        if (currentIndex>-1){   // 1つでも描画していたら
            currentIndex--;     // currentIndexを1つ前に
            invalidate();       // 再描画
        }
    }

    // Redoメソッド
    public void redo() {
        if (currentIndex != array_draw.size()-1) {  // すべて描画していなかったら
            currentIndex++;     // currentIndexを1つ後ろに
            invalidate();       // 再描画
        }
    }

    // このViewがタッチされたときのイベント
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int x = (int)event.getX();  // 座標を取得
        int y = (int)event.getY();

        int a = event.getAction();  // このViewにされたアクションを取得
        switch (a) {
            case MotionEvent.ACTION_DOWN:       // 指が触れたら
            case MotionEvent.ACTION_POINTER_DOWN:
                if (mode == 1) {                    // フリーハンドモードなら
                    array_x = new ArrayList<>();    // 筆跡を記録するArrayList
                    array_y = new ArrayList<>();
                    array_x.add(x);                 // 座標を追加
                    array_y.add(y);
                } else if (mode > 1) {      // 楕円、四角形モードなら
                    x1 = x;     // 始点を取得
                    y1 = y;
                    x2 = x;     // はじめは終点と始点を同じに
                    y2 = y;
                }
                if (currentIndex < array_draw.size()-1) {       // array_drawにcurrentIndex以降にも要素がある場合（リドゥが可能の状況）
                    int array_draw_size = array_draw.size();        // array_draw.size()を保存（for文中で変動するため）
                    for (int i=currentIndex+1; i<array_draw_size; i++) {    // currentIndex以降の要素を削除
                        array_draw.remove(array_draw.size()-1);
                    }
                }
                drawingIs = true;               // 描画中に
                invalidate();                   // 再描画
                break;
            case MotionEvent.ACTION_MOVE:       // 指が動いたら
                if (mode == 1) {        // フリーハンドモードなら
                    array_x.add(x);     // 座標を追加
                    array_y.add(y);
                } else if (mode > 1) {  // 楕円・四角形モードなら
                    x2 = x;     // 終点を取得
                    y2 = y;
                }
                invalidate();       // 再描画
                break;
            case MotionEvent.ACTION_UP:         // 指が離れたら
            case MotionEvent.ACTION_POINTER_UP:
                if (mode == 1) {
                    array_x.add(x);
                    array_y.add(y);
                    array_draw.add(new DrawArray(array_x, array_y, color, bold));   // DrawArrayのインスタンスを作成し、ArrayListに追加
                } else if (mode > 1) {
                    x2 = x;
                    y2 = y;
                    array_draw.add(new DrawArray(x1, y1, x2, y2, color, bold, mode));   // DrawArrayのインスタンスを作成し、ArrayListに追加
                }
                drawingIs = false;      // 描画中ではない
                currentIndex++;     // currentIndexを1つ後ろに
                invalidate();       // 再描画
                break;
        }
        return true;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);   // 塗りつぶしに
        paint.setColor(Color.WHITE);        // ペンを白色に
        canvas.drawRect(new Rect(0, 0, getWidth(), getHeight()), paint);    // 背景を描画

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);       // アンチエイリアスを有効にし、描画をなめらかに
        paint.setStyle(Paint.Style.STROKE);     // 線で描画
        paint.setStrokeCap(Paint.Cap.ROUND);    // 線の先を丸く

        // array_drawを描画
        for (int i=0; i<=currentIndex; i++) {   // currentIndexまで
            array_draw.get(i).myDraw(canvas, paint);    // 描画メソッドを呼び出す
        }

        // 描画中の線をリアルタイムに描画
        if (drawingIs) {        // 描画中なら
            paint.setColor(color);      // ペンの色を設定
            paint.setStrokeWidth(bold); // ペンの太さを設定
            if (mode == 1) {            // フリーハンドモード
                for (int i=1; i<array_x.size(); i++) {  // 筆跡をたどって描画
                    int x1 = array_x.get(i-1);
                    int y1 = array_y.get(i-1);
                    int x2 = array_x.get(i);
                    int y2 = array_y.get(i);
                    canvas.drawLine(x1, y1, x2, y2, paint);
                }
            } else if (mode == 2) {     // 楕円モード
                canvas.drawOval(x1, y1, x2, y2, paint);
            } else if (mode == 3) {     // 四角形モード
                canvas.drawRect(x1, y1, x2, y2, paint);
            }
        }
    }
}
