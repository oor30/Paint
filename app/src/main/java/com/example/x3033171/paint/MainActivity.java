package com.example.x3033171.paint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // フィールド変数
    MyCanvasView mc;
    ConstraintLayout layout;
    TextView boldTextView;
    int layoutNum;
    static Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);     // activity_main.xmlをレイアウトとして設定
        mc = findViewById(R.id.myCanvasView);       // MyCanvasViewのインスタンス
        layout = findViewById(R.id.menuLayout);     // 画面下部のmenuLayout
        setLayout0();
//        layoutNum = 0;
//        getLayoutInflater().inflate(R.layout.menu_layout0, layout);     // layoutにmenu_layout0を設定
        loadData();     // 端末に保存されたデータを読み込む
    }

    // データをセーブし、その結果をbooleanで返す関数
    private boolean saveData() {
        SaveData data = new SaveData(mc);       // データ保存用のインスタンスを作成
        try {
            FileOutputStream fos = openFileOutput("SaveData.dat", MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
            oos.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 保存データを読み込む関数
    private void loadData() {
        try {
            FileInputStream fis = openFileInput("SaveData.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            SaveData data = (SaveData) ois.readObject();
            data.recover(mc);       // mc(MyCanvasView)を回復
            ois.close();
        }catch (Exception e) {
            toast = Toast.makeText(getApplicationContext(), "データの読み込みに失敗しました", Toast.LENGTH_LONG);
            toast.show();       // エラーメッセージをポップアップで表示
        }
    }

    // menu_layout0に戻すメソッド
    private void setLayout0() {
        layout.removeAllViews();        // layout中のViewを削除
        getLayoutInflater().inflate(R.layout.menu_layout0, layout);     // layoutにmenu_layout0を設定
        layoutNum = 0;

        // 色変更ボタンを現在選択中の色に変更する
        int color = mc.getPenColor();       // mcのペンの色を取得
        Button btColor = findViewById(R.id.btColor);    // 色変更ボタンを取得
        switch (color) {
            case Color.RED:     // ペンが赤色なら
                btColor.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.red_circle_form, null));      // 色変更ボタンの背景を赤に
                break;
            case Color.BLUE:
                btColor.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.blue_circle_form, null));
                break;
            case Color.GREEN:
                btColor.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.green_circle_form, null));
                break;
            case Color.YELLOW:
                btColor.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.yellow_circle_form, null));
                break;
            case Color.BLACK:
                btColor.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.black_circle_form, null));
                break;
            default:    // ペンが紫色なら
                btColor.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.perple_circle_form, null));
                break;
        }

        // 描画モード変更ボタンを現在選択中の図形（フリーハンド、楕円、四角形）に変更する
        int mode = mc.getMode();        // 描画モードを取得
        Button btShape = findViewById(R.id.btShape);    // 描画モード変更ボタンを取得
        switch (mode) { // 選択中のモードが、
            case 1:     // フリーハンドなら
                btShape.setForeground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_freehand, null));    // ボタンの背景をフリーハンド画像に
                break;
            case 2:     // 楕円なら
                btShape.setForeground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_oval, null));
                break;
            case 3:     // 四角形なら
                btShape.setForeground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_rect, null));
                break;
        }
    }

    // すべてのボタンのonClickイベント
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btColor:      // 色変更ボタン
                layout.removeAllViews();        // layout中のViewを削除
                getLayoutInflater().inflate(R.layout.menu_layout1, layout);     // layoutにmenu_layout1を設定
                layoutNum = 1;
                break;
            case R.id.btShape:      // 描画モード変更ボタン
                layout.removeAllViews();
                getLayoutInflater().inflate(R.layout.menu_layout3, layout);     // layoutをmenu=layout3に設定
                layoutNum = 3;
                break;
            case R.id.btBold:       // 太さ変更ボタン
                layout.removeAllViews();
                getLayoutInflater().inflate(R.layout.menu_layout2, layout);     // layoutにmenu_layout2を設定
                layoutNum = 2;

                SeekBar seekBar = findViewById(R.id.boldSeekBar);       // 太さ変更用のシークバー
                seekBar.setProgress(mc.getBold());                      // シークバーの位置を今の太さに合わせる
                seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);        // リスナー登録
                boldTextView = findViewById(R.id.boldTextView);         // 太さの値を表示するTextView
                boldTextView.setText(String.valueOf(mc.getBold()));     // 今の太さに合わせる
                break;
            case R.id.btClear:      // クリアーボタン
                mc.clear();
                break;
            case R.id.btUndo:       // アンドゥボタン
                mc.undo();
                break;
            case R.id.btRedo:       // リドゥボタン
                mc.redo();
                break;
            case R.id.btSave:       // セーブボタン
                if (saveData()) {       // セーブ成功したら
                    toast = Toast.makeText(getApplicationContext(), "保存しました", Toast.LENGTH_LONG);
                    toast.show();       // メッセージ表示
                } else {                // セーブ失敗したら
                    toast = Toast.makeText(getApplicationContext(), "保存に失敗しました", Toast.LENGTH_LONG);
                    toast.show();       // メッセージ表示
                }
                break;
            case R.id.btRed:        // 赤色ボタン
                mc.setPenColor(Color.RED);      // ペンの色を赤に
                setLayout0();
                break;
            case R.id.btBlue:       // 青色ボタン
                mc.setPenColor(Color.BLUE);
                setLayout0();
                break;
            case R.id.btGreen:      // 緑色ボタン
                mc.setPenColor(Color.GREEN);
                setLayout0();
                break;
            case R.id.btYellow:     // 黄色ボタン
                mc.setPenColor(Color.YELLOW);
                setLayout0();
                break;
            case R.id.btPerple:     // 紫色ボタン
                mc.setPenColor(Color.parseColor("#ff00ff"));
                setLayout0();
                break;
            case R.id.btBlack:      // 黒色ボタン
                mc.setPenColor(Color.BLACK);
                setLayout0();
                break;
            case R.id.btLine:       // フリーハンドボタン
                mc.setMode(1);      // 描画モードを1に
                setLayout0();       // layoutをmenu_layout0に
                break;
            case R.id.btOval:       // 楕円ボタン
                mc.setMode(2);
                setLayout0();
                break;
            case R.id.btRect:       // 四角形ボタン
                mc.setMode(3);
                setLayout0();
                break;
        }
    }

    // 太さ変更用のシークバーのイベントリスナー
    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        // シークバーが動かされたとき
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            boldTextView = findViewById(R.id.boldTextView);     // 太さのTextViewを、
            boldTextView.setText(String.valueOf(progress));     // シークバーの値に合わせる
        }

        // シークバーに触れたとき
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        // シークバーから離したとき
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int bold = seekBar.getProgress();       // シークバーの値
            mc.setPenBold(bold);                    // ペンの太さを変更
            setLayout0();
        }
    };
}
