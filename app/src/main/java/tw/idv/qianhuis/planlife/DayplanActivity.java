package tw.idv.qianhuis.planlife;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class DayplanActivity extends AppCompatActivity {

    //變數
    final Context context= DayplanActivity.this;
    LinearLayout ll_plans;
    ArrayList<PlanItem> l_pi;
    ArrayList<WorkItem> l_wi;

    //DB
    private SQLiteDatabase mSQLiteDatabase= null;
    private static final String DATABASE_NAME = "app.db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dayplan);

        //開啟DB
        mSQLiteDatabase = this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);

        //XML
        ll_plans = findViewById(R.id.ll_dayplan);
        l_pi= new ArrayList<>();
        l_wi= new ArrayList<>();

        showPlans();
/*
        //顯示Item
        ll_plans.removeAllViews();
        for(int i=0; i<l_pi.size(); i++) {

            //建立Plan
            PlanItem pi= l_pi.get(i);
            View view_planitem = View.inflate(context, R.layout.view_planitem, null);

            TextView tv_part= view_planitem.findViewById(R.id.tv_part);
            Button bt_workadd = view_planitem.findViewById(R.id.bt_workadd);
            final LinearLayout ll_works= view_planitem.findViewById(R.id.ll_works);

            tv_part.setText(pi.getPart());

            //新增事件
            bt_workadd.setOnClickListener(new View.OnClickListener() {  //新增事件
                @Override
                public void onClick(View v) {
                    // TODO: 2019/2/24 開啟選擇事件Dialog, 選擇事件.
                    final DayplanDialog dd= new DayplanDialog(context);
                    dd.buildWorkAdd();
                    dd.show();
                    dd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if(!dd.getReturn().equals("")) {
                                // TODO: 2019/2/26 新增成功, 刷新/新增item.
                            }
                        }
                    });
                }

            });

            ll_works.removeAllViews();

            //建立WorkItem
            for(int j=0; j<l_wi.size(); j++) {
                final WorkItem wi= l_wi.get(j);
                // TODO: 2019/2/26 pi.getWorks()拆分比對wi_name; pi找DB/找l_wi.
                if(pi.getWorks()==wi.getName()) {   //若存在則新增workitem.

                    final View v_workitem= View.inflate(context, R.layout.view_workitem, null);
                    TextView tv_completion= v_workitem.findViewById(R.id.tv_completion);
                    Button bt_name = v_workitem.findViewById(R.id.bt_name);
                    TextView tv_deadline= v_workitem.findViewById(R.id.tv_deadline);

                    //wi.getXX()填入.
                    tv_completion.setText("");
                    bt_name.setText("");
                    tv_deadline.setText("");

                    // TODO: 2019/2/24 btplan短按事件, 開啟選擇事件.
                    //短按標註完成
                    bt_name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TextView tv_completion= v_workitem.findViewById(R.id.tv_completion);
                            if(tv_completion.getText().toString().equals("")) {
                                tv_completion.setText("完成");
                                // TODO: 2019/2/26 優化至wi的dbfunction 修改.
                                String UPDATE_TABLE = "UPDATE "+ WorkItem.table_work +" SET " +
                                        WorkItem.work_completion +"='完成' where "+ WorkItem.work_id +"=" + wi.getId();
                                mSQLiteDatabase.execSQL(UPDATE_TABLE);
                            } else {
                                tv_completion.setText("");
                                // TODO: 2019/2/26 優化至wi的dbfunction 修改.
                                String UPDATE_TABLE = "UPDATE "+ WorkItem.table_work +" SET " +
                                        WorkItem.work_completion +"='' where "+ WorkItem.work_id +"=" + wi.getId();
                                mSQLiteDatabase.execSQL(UPDATE_TABLE);
                            }
                        }
                    });

                    //長按刪除事件
                    bt_name.setOnLongClickListener(new View.OnLongClickListener() {     //長按移除.
                        @Override
                        public boolean onLongClick(View v) {
                            ll_works.removeView(v_workitem);
                            // TODO: 2019/2/26 刪除DB資料.
                            return true;   //表只執行長按事件; 長按結束後不執行短按事件.
                        }
                    });

                    ll_works.addView(v_workitem);
                }

            }

        }
*/
    }
/*
    @Override
    protected void onResume() {
        super.onResume();

        showWorks(ALL);
    }
*/
    // TODO: 2019/2/26 改變順序問題??
    //顯示項目
    public void showPlans() {
        //取得Table資料
        ll_plans.removeAllViews();    //清空ll.

        //取得DB資料
        Cursor c1;
        c1= mSQLiteDatabase.rawQuery("SELECT * FROM "+ PlanItem.table_plan +" WHERE 1", null);
        c1.moveToFirst();
        if(c1.getCount()!=0) {
            while(!c1.isAfterLast()) {
                final PlanItem pi= new PlanItem(
                        c1.getString(0), c1.getString(1),
                        c1.getString(2), c1.getString(3));

                //l_pi.add(pi);
                c1.moveToNext();

                //建立Plan
                //PlanItem pi= l_pi.get(i);
                View view_planitem = View.inflate(context, R.layout.view_planitem, null);

                TextView tv_part= view_planitem.findViewById(R.id.tv_part);
                Button bt_workadd = view_planitem.findViewById(R.id.bt_workadd);
                final LinearLayout ll_works= view_planitem.findViewById(R.id.ll_works);

                tv_part.setText(pi.getPart());

                //新增事件
                bt_workadd.setOnClickListener(new View.OnClickListener() {  //新增事件
                    @Override
                    public void onClick(View v) {
                        // TODO: 2019/2/24 開啟選擇事件Dialog, 選擇事件.
                        final DayplanDialog dd= new DayplanDialog(context);
                        dd.buildWorkAdd();
                        dd.show();
                        dd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                if(!dd.getReturn().equals("")) {
                                    mSQLiteDatabase.execSQL(    //更新PI.
                                            pi.addWorks(dd.getReturn())
                                            // TODO: 2019/2/28 final的pi, 能否進行修改? 是否造成pi不一致?
                                    );
                                    showWorks(ll_works, pi);
                                }
                            }
                        });
                    }

                });

                showWorks(ll_works, pi);

            }
        }
        c1.close();
    }

    public void showWorks(final LinearLayout ll_works, final PlanItem pi) {
        ll_works.removeAllViews();

        //建立WorkItem
        for(int j=0; !pi.getW(j).equals(""); j++) {
            Cursor c2;
            //查詢指定的work資料
            String WHERE= WorkItem.work_name +" = '"+ pi.getW(j) +"'";
            c2= mSQLiteDatabase.rawQuery("SELECT * FROM "+ WorkItem.table_work +
                    " WHERE "+WHERE, null);
            c2.moveToFirst();
            //if(c.getCount()!=0) {
            //while(!c.isAfterLast()) {
            final WorkItem wi= new WorkItem(
                    c2.getString(0), c2.getString(1),
                    c2.getString(2), c2.getString(3),
                    c2.getString(4), c2.getString(5),
                    c2.getString(6), c2.getString(7));

            //l_wi.add(wi);
            //c.moveToNext();
            //}
            //}
            c2.close();

            final View v_workitem= View.inflate(context, R.layout.view_workitem, null);
            TextView tv_completion= v_workitem.findViewById(R.id.tv_completion);
            Button bt_name = v_workitem.findViewById(R.id.bt_name);
            TextView tv_deadline= v_workitem.findViewById(R.id.tv_deadline);

            //wi.getXX()填入.
            tv_completion.setText("");
            bt_name.setText("");
            tv_deadline.setText("");

            // TODO: 2019/2/24 btplan短按事件, 開啟選擇事件.
            //短按標註完成
            bt_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView tv_completion= v_workitem.findViewById(R.id.tv_completion);
                    if(tv_completion.getText().toString().equals("")) {
                        String text= "完成";
                        wi.setCompletion(text);
                        mSQLiteDatabase.execSQL(WorkItem.updateTable(wi));
                        tv_completion.setText(text);
                    } else {
                        String text= "";
                        wi.setCompletion(text);
                        mSQLiteDatabase.execSQL(WorkItem.updateTable(wi));
                        tv_completion.setText(text);
                    }
                }
            });

            //長按刪除事件
            bt_name.setOnLongClickListener(new View.OnLongClickListener() {     //長按移除.
                @Override
                public boolean onLongClick(View v) {
                    final DayplanDialog dd= new DayplanDialog(context);
                    dd.buildWorkDelete(wi);
                    dd.show();
                    dd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if(!dd.getReturn().equals("")) {  //若刪除成功.
                                mSQLiteDatabase.execSQL(    //更新PI.
                                        pi.removeWorks(dd.getReturn())
                                );
                                ll_works.removeView(v_workitem);
                            }
                        }
                    });
                    return true;   //表只執行長按事件; 長按結束後不執行短按事件.
                }
            });

            ll_works.addView(v_workitem);

            // TODO: 2019/2/28 紀錄PI的works.
        }
    }
}
