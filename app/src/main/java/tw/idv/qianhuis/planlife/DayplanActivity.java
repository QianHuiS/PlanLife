package tw.idv.qianhuis.planlife;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DayplanActivity extends AppCompatActivity {

    //變數
    final Context context= DayplanActivity.this;
    LinearLayout ll_plans, ll_start, ll_top, ll_delete;
    Button bt_other, bt_move, bt_delete, bt_works, bt_ok, bt_cancel;

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
        ll_plans= findViewById(R.id.ll_dayplan);
        bt_other= findViewById(R.id.bt_other);

        ll_top= findViewById(R.id.ll_top);
        bt_move= findViewById(R.id.bt_move);
        bt_delete= findViewById(R.id.bt_delete);

        ll_start= findViewById(R.id.ll_start);
        bt_works= findViewById(R.id.bt_works);

        ll_delete= findViewById(R.id.ll_delete);
        bt_ok= findViewById(R.id.bt_ok);
        bt_cancel= findViewById(R.id.bt_cancel);

        showPlans();
    }

    @Override
    protected void onResume() {
        super.onResume();

        showPlans();

        bt_other.setTag("");
        bt_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bt_other.getTag().equals("")) {
                    ll_top.setVisibility(View.VISIBLE);
                    ll_start.setVisibility(View.VISIBLE);
                    bt_other.setTag("1");
                } else {
                    ll_top.setVisibility(View.GONE);
                    ll_start.setVisibility(View.GONE);
                    bt_other.setTag("");
                }
            }
        });

        bt_move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //move
            }
        });

        bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //顯示delete確認選項
                ll_delete.setVisibility(View.VISIBLE);
                bt_ok.setTag("");

                //顯示work的cb, 顯示確認/取消bt.
                for(int i=0; i<ll_plans.getChildCount(); i++) {
                    View v_planitem= ll_plans.getChildAt(i);
                    Button bt_workadd= v_planitem.findViewById(R.id.bt_workadd);
                    LinearLayout ll_works= v_planitem.findViewById(R.id.ll_works);

                    //禁用btClick
                    bt_workadd.setEnabled(false);

                    for(int j=0; j<ll_works.getChildCount(); j++) {
                        View v_workitem= ll_works.getChildAt(j);
                        Button bt_name= v_workitem.findViewById(R.id.bt_name);
                        CheckBox cb_check= v_workitem.findViewById(R.id.cb_check);

                        final WorkItem wi= selectWorkName(bt_name.getText().toString());

                        //禁用btClick
                        bt_name.setEnabled(false);

                        //顯示cb
                        cb_check.setVisibility(View.VISIBLE);
                        cb_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if(isChecked) {    //勾選則加入刪除名單內.
                                    String btTag= bt_ok.getTag().toString();
                                    Log.d("itemOnClick before: ", "bt_okTag="+btTag);

                                    //判斷是否位在第一個
                                    if(btTag.equals("")) bt_ok.setTag(btTag.concat("_"+wi.getName()+"_"));
                                    else bt_ok.setTag(btTag.concat(wi.getName()+"_"));
                                    btTag= bt_ok.getTag().toString();
                                    Log.d("itemOnClick after: ", "bt_okTag="+btTag);
                                } else {  //未勾選則消除刪除名單.
                                    String btTag= bt_ok.getTag().toString();
                                    Log.d("itemOnClick before: ", "bt_okTag="+btTag);

                                    bt_ok.setTag(btTag.replace("_"+wi.getName()+"_","_"));
                                    btTag= bt_ok.getTag().toString();
                                    if(btTag.equals("_")) bt_ok.setTag(""); //若為空則清空.
                                    Log.d("itemOnClick after: ", "bt_okTag="+btTag);
                                }
                            }
                        });
/*
                        //點擊item時勾選cb
                        v_workitem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(cb_check.isChecked()) {  //已勾選則取消勾選, 消除刪除名單.
                                    String btTag= bt_ok.getTag().toString();
                                    Log.d("itemOnClick before: ", "bt_okTag="+btTag);

                                    bt_ok.setTag(btTag.replace("_"+wi.getName()+"_","_"));
                                    btTag= bt_ok.getTag().toString();
                                    if(btTag.equals("_")) bt_ok.setTag(""); //若為空則清空.
                                    Log.d("itemOnClick after: ", "bt_okTag="+btTag);
                                } else {    //若無勾選, 加入刪除名單內.
                                    String btTag= bt_ok.getTag().toString();
                                    Log.d("itemOnClick before: ", "bt_okTag="+btTag);

                                    //判斷是否位在第一個
                                    if(btTag.equals("")) bt_ok.setTag(btTag.concat("_"+wi.getName()+"_"));
                                    else bt_ok.setTag(btTag.concat(wi.getName()+"_"));
                                    btTag= bt_ok.getTag().toString();
                                    Log.d("itemOnClick after: ", "bt_okTag="+btTag);
                                }

                                cb_check.toggle();  //toggle()為反向選擇.
                            }
                        });
*/
                    }
                }

                bt_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //String wiId= "";
                        String tmp= bt_ok.getTag().toString();  //Tag格式: _XX_XX_..._XX_XX_
                        Log.d("btokOnClick before: ", "bt_okTag="+tmp);

                        if(!tmp.equals("")) {
                            //刪除選取資料
                            while(tmp.contains("_")&& !tmp.equals("_")) {    //字串是否包含"_".
                                WorkItem wi= selectWorkName(tmp.substring(1,
                                        tmp.substring(1).indexOf("_")+1));    //取得第一個"_"後到第二個"_"前的子字串.

                                //刪除wi
                                mSQLiteDatabase.execSQL(    //更新PI.
                                    WorkItem.deleteTable(wi)
                                );
                                //Log.d("btokOnClick ing: ", "WorkItem.deleteTable(wi)");

                                //更新pi_works
                                Cursor c1;
                                c1= mSQLiteDatabase.rawQuery("SELECT * FROM "+ PlanItem.table_plan +" WHERE 1", null);
                                c1.moveToFirst();
                                while(!c1.isAfterLast()) {  //selectPlan 對應pi_works內包含wi者.
                                    // TODO: 2019/3/8 若work可以在多個plan時段重名, 將會造成, 不知道該刪哪時段的work.
                                    PlanItem pi = new PlanItem(
                                            c1.getString(0), c1.getString(1),
                                            c1.getString(2), c1.getString(3));
                                    if(pi.getWorks().contains(wi.getName())) {
                                        mSQLiteDatabase.execSQL(    //更新PI.
                                            pi.removeWorks(wi.getName())
                                        );
                                        // TODO: 2019/3/8 若plan和work分開管理, 要確保work刪除時, 安全移除pi_works中的wi.
                                        //Log.d("btokOnClick ing: ", "pi.removeWorks(wi.getName())");
                                        c1.moveToLast();
                                    }
                                    c1.moveToNext();
                                }
                                c1.close();

                                tmp= tmp.substring(tmp.substring(1).indexOf("_")+1);   //tmp= 第二個"_"前, 到結尾的字串.
                                //Log.d("btokOnClick ing: ", "bt_okTag="+tmp);
                            }
                            //Log.d("btokOnClick after: ", "bt_okTag="+tmp);

                            //隱藏delete確認選項
                            ll_delete.setVisibility(View.GONE);

                            //刷新
                            showPlans();

                        } else Toast.makeText(context, "沒有選擇任何事件!", Toast.LENGTH_SHORT).show();
                    }
                });

                bt_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //隱藏delete確認選項
                        ll_delete.setVisibility(View.GONE);

                        showPlans();
                    }
                });

            }
        });

        bt_works.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //works
            }
        });
    }

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

                //建立Plan
                View v_planitem = View.inflate(context, R.layout.view_planitem, null);

                TextView tv_part= v_planitem.findViewById(R.id.tv_part);
                Button bt_workadd = v_planitem.findViewById(R.id.bt_workadd);
                final LinearLayout ll_works= v_planitem.findViewById(R.id.ll_works);

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
                                            pi.addWorks(dd.getReturn())     //final的pi還是能以function修改內容.
                                    );
                                    Log.d("addworks", "pi.works= "+pi.getWorks());
                                    //showWorks(ll_works, pi);
                                    showPlans();
                                }
                            }
                        });
                    }

                });

                showWorks(ll_works, pi);

                ll_plans.addView(v_planitem);

                c1.moveToNext();
            }
        }
        c1.close();
    }

    public void showWorks(final LinearLayout ll_works, final PlanItem pi) {
        ll_works.removeAllViews();

        //建立WorkItem
        for(int j=0; !pi.getW(j).equals(""); j++) {
            //查詢指定的work資料
            final WorkItem wi= selectWorkName(pi.getW(j));

            final View v_workitem= View.inflate(context, R.layout.view_workitem, null);
            TextView tv_completion= v_workitem.findViewById(R.id.tv_completion);
            Button bt_name= v_workitem.findViewById(R.id.bt_name);
            TextView tv_deadline= v_workitem.findViewById(R.id.tv_deadline);

            tv_completion.setText(wi.getCompletion());
            bt_name.setText(wi.getName());
            tv_deadline.setText(wi.getDeadline());

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
/*
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
                                //ll_works.removeView(v_workitem);
                                showPlans();
                            }
                        }
                    });
                    return true;   //表只執行長按事件; 長按結束後不執行短按事件.
                }
            });
*/

            //長按顯示詳細資料
            bt_name.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final DayplanDialog dd= new DayplanDialog(context);
                    dd.buildWorkDetail(wi);
                    dd.show();
                    dd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if(!dd.getReturn().equals("")) {
                                Log.d("reviseworks befor", "pi.works= "+pi.getWorks());
                                mSQLiteDatabase.execSQL(    //更新PI.
                                        pi.reviseWorks(dd.getReturn())     //final的pi還是能以function修改內容.
                                );
                                Log.d("reviseworks after", "pi.works= "+pi.getWorks());
                                //showWorks(ll_works, pi);
                                showPlans();
                            }
                        }
                    });
                    return true;   //表只執行長按事件; 長按結束後不執行短按事件.
                }
            });

            ll_works.addView(v_workitem);
        }
    }

    public WorkItem selectWorkName(String wName) {
        WorkItem wi;

        Cursor c;
        //查詢指定的work資料
        String WHERE= WorkItem.work_name +" = '"+ wName +"'";
        c= mSQLiteDatabase.rawQuery("SELECT * FROM "+ WorkItem.table_work +
                " WHERE "+WHERE, null);
        c.moveToFirst();

        if(c.getCount()!=0) {
            wi= new WorkItem(
                    c.getString(0), c.getString(1),
                    c.getString(2), c.getString(3),
                    c.getString(4), c.getString(5),
                    c.getString(6), c.getString(7));
            c.close();
        } else {
            wi= new WorkItem();
        }

        return wi;
    }
}
