package tw.idv.qianhuis.planlife;

import android.content.Intent;
import android.database.Cursor;
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

        //開啟DB
        mSQLiteDatabase = this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);

        //清除Table
        String DROP_TABLE;
        DROP_TABLE=  "DROP TABLE IF EXISTS "+ WorkItem.table_work;
        //mSQLiteDatabase.execSQL(DROP_TABLE);
        DROP_TABLE=  "DROP TABLE IF EXISTS "+ PlanItem.table_plan;
        //mSQLiteDatabase.execSQL(DROP_TABLE);

        //建Table
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +  //建立表單(若表單不存在!!). 建錯移除app.
                WorkItem.table_work +" (" +
                WorkItem.work_id +" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                WorkItem.work_previous +" TEXT, " +
                WorkItem.work_next +" TEXT, " +
                WorkItem.work_name +" TEXT, " +
                WorkItem.work_content +" TEXT, " +
                WorkItem.work_type +" TEXT, " +
                WorkItem.work_completion +" TEXT, " +
                WorkItem.work_deadline +" TEXT )";
        mSQLiteDatabase.execSQL(CREATE_TABLE);     //執行SQL指令的字串.

        CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +  //建立表單(若表單不存在!!). 建錯移除app.
                PlanItem.table_plan +" (" +
                PlanItem.plan_id +" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PlanItem.plan_part +" TEXT, " +
                PlanItem.plan_works +" TEXT, " +
                PlanItem.plan_date +" TEXT )";
        mSQLiteDatabase.execSQL(CREATE_TABLE);     //執行SQL指令的字串.

        Cursor c;
        c= mSQLiteDatabase.rawQuery("SELECT * FROM "+ PlanItem.table_plan +" WHERE 1", null);
        c.moveToFirst();
        if(c.getCount()==0) {
            String INSERT_TABLE;
            INSERT_TABLE = "INSERT INTO "+PlanItem.table_plan+" ("+
                    PlanItem.plan_part +", " + PlanItem.plan_works +", " + PlanItem.plan_date +" ) " +
                    "VALUES('起床後', '', '')";
            mSQLiteDatabase.execSQL(INSERT_TABLE);
            INSERT_TABLE = "INSERT INTO "+PlanItem.table_plan+" ("+
                    PlanItem.plan_part +", " + PlanItem.plan_works +", " + PlanItem.plan_date +" ) " +
                    "VALUES('午餐後', '', '')";
            mSQLiteDatabase.execSQL(INSERT_TABLE);
            INSERT_TABLE = "INSERT INTO "+PlanItem.table_plan+" ("+
                    PlanItem.plan_part +", " + PlanItem.plan_works +", " + PlanItem.plan_date +" ) " +
                    "VALUES('晚餐後', '', '')";
            mSQLiteDatabase.execSQL(INSERT_TABLE);
        }

        //XML
        iv_loading = findViewById(R.id.iv_loading);

    }

    @Override
    protected void onResume() {
        super.onResume();

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
                    Thread.sleep(1000);     //延遲N秒. (N秒=N*1000毫秒)
                    //切換頁面
                    startActivity(
                            new Intent().setClass(MainActivity.this, DayplanActivity.class));
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
