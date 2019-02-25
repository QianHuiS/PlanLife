package tw.idv.qianhuis.planlife;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

/*
 * 隱藏StatusBar&ActivityBar:
 *   res\values\styles.xml 加設定
 *   AndroidManifest.xml 更改 <application android:theme>
 */
public class MainActivity extends AppCompatActivity {

    //變數
    AnimationDrawable adLoading;
    ImageView iv_loading;

    //DB
    private SQLiteDatabase mSQLiteDatabase= null;
    private static final String DATABASE_NAME = "app.db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //XML
        iv_loading = findViewById(R.id.iv_loading);

        //Loading動畫
        adLoading = (AnimationDrawable) iv_loading.getDrawable();
        adLoading.start();
        //drawable中包含: 動畫圖組, anim_loading.xml.
        //xml檔使用<animation-list>; oneshot="false"表重複執行動畫(true則執行一次); duration表動畫速度(毫秒).

        //啟動執行續延遲畫面
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(5000);     //延遲N秒. (N秒=N*1000毫秒)
                    //切換頁面
                    startActivity(
                            new Intent().setClass(MainActivity.this, DayplanActivity.class));
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }).start();

        //開啟DB


    }
}
