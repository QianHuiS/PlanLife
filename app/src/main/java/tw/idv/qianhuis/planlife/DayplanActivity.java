package tw.idv.qianhuis.planlife;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class DayplanActivity extends AppCompatActivity {

    //變數
    final Context context= DayplanActivity.this;
    LinearLayout ll_plans, ll_start, ll_top, ll_move, ll_check;
    Button bt_other, bt_move, bt_delete, bt_works, bt_up, bt_down, bt_ok, bt_cancel;

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

        ll_move = findViewById(R.id.ll_move);
        bt_up= findViewById(R.id.bt_up);
        bt_down= findViewById(R.id.bt_down);

        ll_check = findViewById(R.id.ll_check);
        bt_ok= findViewById(R.id.bt_ok);
        bt_cancel= findViewById(R.id.bt_cancel);

        showPlans();
    }

    @Override
    protected void onResume() {
        super.onResume();

        showPlans();

        //bt_other.setTag("");
        bt_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!bt_other.isSelected()) {
                //if(bt_other.getTag().equals("")) {
                    bt_other.setSelected(true);
                    ll_top.setVisibility(View.VISIBLE);
                    ll_start.setVisibility(View.VISIBLE);
                    //bt_other.setTag("1");
                } else {
                    bt_other.setSelected(false);
                    ll_top.setVisibility(View.GONE);
                    ll_start.setVisibility(View.GONE);
                    //bt_other.setTag("");
                }
            }
        });

        bt_move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判斷是否已點擊
                if(bt_move.isSelected()) {
                    bt_move.setSelected(false);
                    //隱藏確認選項
                    ll_check.setVisibility(View.GONE);
                    ll_move.setVisibility(View.GONE);

                    showPlans();
                } else {
                    if(bt_delete.isSelected()) bt_delete.callOnClick(); //若bt啟用, 則再次點擊以關閉.
                    bt_move.setSelected(true);
                    //顯示確認選項
                    ll_check.setVisibility(View.VISIBLE);
                    bt_ok.setTag("");
                    ll_move.setVisibility(View.VISIBLE);

                    //顯示work的itmove
                    for(int i=0; i<ll_plans.getChildCount(); i++) {
                        final View v_planitem= ll_plans.getChildAt(i);
                        Button bt_workadd= v_planitem.findViewById(R.id.bt_workadd);
                        final LinearLayout ll_works= v_planitem.findViewById(R.id.ll_works);

                        //禁用btClick
                        bt_workadd.setEnabled(false);

                        for(int j=0; j<ll_works.getChildCount(); j++) {
                            final View v_workitem= ll_works.getChildAt(j);
                            Button bt_name= v_workitem.findViewById(R.id.bt_name);
                            CheckBox cb_check= v_workitem.findViewById(R.id.cb_check);

                            //禁用btClick
                            bt_name.setEnabled(false);

                            cb_check.setVisibility(View.VISIBLE);
                            cb_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if(isChecked) { //若未點選任何cb.
                                        LinearLayout ll_ws= (LinearLayout)v_workitem.getParent();
                                        View v_pi= (View) ll_ws.getParent();
                                        LinearLayout ll_ps= (LinearLayout)v_pi.getParent();
                                        int thisi= ll_ps.indexOfChild(v_pi);
                                        int thisj= ll_ws.indexOfChild(v_workitem);
                                        //Log.d("", "thisi="+thisi);    Log.d("", "thisj="+thisj);

                                        //清除其他cbCheck
                                        for(int i=0; i<ll_plans.getChildCount(); i++) {
                                            View v_planitem= ll_plans.getChildAt(i);
                                            LinearLayout ll_works= v_planitem.findViewById(R.id.ll_works);

                                            for(int j=0; j<ll_works.getChildCount(); j++) {
                                                View v_workitem = ll_works.getChildAt(j);
                                                CheckBox cb_check = v_workitem.findViewById(R.id.cb_check);
                                                if(!(i==thisi && j==thisj))    cb_check.setChecked(false);
                                            }
                                        }

                                        //設定移動
                                        bt_up.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                LinearLayout ll_ws= (LinearLayout)v_workitem.getParent();
                                                View v_pi= (View) ll_ws.getParent();
                                                LinearLayout ll_ps= (LinearLayout)v_pi.getParent();
                                                int thisi= ll_ps.indexOfChild(v_pi);
                                                int thisj= ll_ws.indexOfChild(v_workitem);
                                                //互換view位置
                                                if(thisj-1>=0) {    //互換者為j-1.
                                                    Log.d("", "thisi="+thisi);  Log.d("", "thisj="+thisj);
                                                    View v_tmp= ll_ws.getChildAt(thisj-1);   //暫存前v.
                                                    ll_ws.removeViewAt(thisj-1); //刪除前v, 原v往前遞補.
                                                    ll_ws.addView(v_tmp, thisj); //在原本位置插入前v.
                                                } else if(thisi-1>=0) { //無互換者, 只改變所屬ps.    //另: 互換者為 i-1的j=總數(最後一個v).
                                                    Log.d("", "thisi="+thisi);  Log.d("", "thisj="+thisj);
                                                    LinearLayout ll_prevws= ll_ps.getChildAt(thisi-1).findViewById(R.id.ll_works);
                                                    //View v_tmp= ll_prevws.getChildAt(ll_prevws.getChildCount()-1);   //暫存前v.
                                                    //ll_prevws.removeViewAt(ll_prevws.getChildCount()-1); //刪除前v.
                                                    ll_ws.removeViewAt(thisj);   //刪除原位原v.
                                                    ll_prevws.addView(v_workitem);  //補上原v.     //若未先刪除, 會造成"已有所屬parent"無法add.
                                                    //ll_ws.addView(v_tmp, thisj); //在原位插入前v.
                                                }
                                            }
                                        });

                                        bt_down.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                LinearLayout ll_ws= (LinearLayout)v_workitem.getParent();
                                                View v_pi= (View) ll_ws.getParent();
                                                LinearLayout ll_ps= (LinearLayout)v_pi.getParent();
                                                int thisi= ll_ps.indexOfChild(v_pi);
                                                int thisj= ll_ws.indexOfChild(v_workitem);
                                                //互換view位置
                                                if(thisj+1<ll_ws.getChildCount()) {    //互換者為j+1.
                                                    Log.d("", "thisi="+thisi);  Log.d("", "thisj="+thisj);
                                                    ll_ws.removeViewAt(thisj); //刪除原v, 後v往前遞補.
                                                    ll_ws.addView(v_workitem, thisj+1); //在後v位置插入原v.    //若後v為最後是否會bug?
                                                } else if(thisi+1<ll_ps.getChildCount()) { //無互換者, 只改變所屬ps.    //另: 互換者為 i+1的j=0(第一個v).
                                                    Log.d("", "thisi="+thisi);  Log.d("", "thisj="+thisj);
                                                    LinearLayout ll_nextws= ll_ps.getChildAt(thisi+1).findViewById(R.id.ll_works);
                                                    //View v_tmp= ll_prevws.getChildAt(0);   //暫存後v.
                                                    //ll_prevws.removeViewAt(0); //刪除後v.
                                                    ll_ws.removeViewAt(thisj);   //刪除原位原v.
                                                    ll_nextws.addView(v_workitem, 0);  //在後v位置插入原v.
                                                    //ll_ws.addView(v_tmp, thisj); //在原位插入後v.
                                                }
                                            }
                                        });

                                    } else {
                                        bt_up.setOnClickListener(null);
                                        bt_down.setOnClickListener(null);
                                    }
                                }
                            });

                        }
                    }

                    bt_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //取得每個view名稱 重建works清單
                            for(int i=0; i<ll_plans.getChildCount(); i++) {
                                View v_planitem = ll_plans.getChildAt(i);
                                TextView tv_part = v_planitem.findViewById(R.id.tv_part);
                                LinearLayout ll_works = v_planitem.findViewById(R.id.ll_works);

                                //重建pi_works
                                Cursor c1;
                                String WHERE= PlanItem.plan_part+"= '"+tv_part.getText().toString()+"'";
                                c1= mSQLiteDatabase.rawQuery("SELECT * FROM "+ PlanItem.table_plan +" WHERE "+WHERE, null);
                                c1.moveToFirst();
                                PlanItem pi = new PlanItem(
                                        c1.getString(0), c1.getString(1),
                                        c1.getString(2), c1.getString(3));
                                c1.close();

                                Log.d("原本 "+tv_part.getText().toString(), "pi_works="+pi.getWorks());
                                Log.d(""+tv_part.getText().toString(), pi.getId()+" "+pi.getPart()+"\n"+pi.getWorks()+"\n"+pi.getDate());
                                pi.setWorks("");

                                for (int j = 0; j < ll_works.getChildCount(); j++) {
                                    View v_workitem = ll_works.getChildAt(j);
                                    Button bt_name = v_workitem.findViewById(R.id.bt_name);

                                    pi.addWorks(bt_name.getText().toString());  //未實質更新.
                                }

                                //更新works清單
                                Log.d("重建", "pi_works="+pi.getWorks());
                                mSQLiteDatabase.execSQL(
                                        PlanItem.updateTable(pi)
                                );
                            }

                            Toast.makeText(context, "移動成功!!", Toast.LENGTH_SHORT).show();

                            //隱藏確認選項
                            ll_check.setVisibility(View.GONE);
                            ll_move.setVisibility(View.GONE);
                            bt_move.setSelected(false);

                            //刷新
                            showPlans();
                        }
                    });

                    bt_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //隱藏確認選項
                            ll_check.setVisibility(View.GONE);
                            ll_move.setVisibility(View.GONE);
                            bt_move.setSelected(false);

                            showPlans();
                        }
                    });
                }
            }
        });

        bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bt_delete.isSelected()) {
                    bt_delete.setSelected(false);
                    //隱藏確認選項
                    ll_check.setVisibility(View.GONE);

                    showPlans();
                } else {
                    if(bt_move.isSelected()) bt_move.callOnClick(); //若bt啟用, 則再次點擊以關閉.
                    bt_delete.setSelected(true);
                    //顯示delete確認選項
                    ll_check.setVisibility(View.VISIBLE);
                    bt_ok.setTag("");

                    //顯示work的cb
                    for (int i = 0; i < ll_plans.getChildCount(); i++) {
                        View v_planitem = ll_plans.getChildAt(i);
                        Button bt_workadd = v_planitem.findViewById(R.id.bt_workadd);
                        LinearLayout ll_works = v_planitem.findViewById(R.id.ll_works);

                        //禁用btClick
                        bt_workadd.setEnabled(false);

                        for (int j = 0; j < ll_works.getChildCount(); j++) {
                            View v_workitem = ll_works.getChildAt(j);
                            Button bt_name = v_workitem.findViewById(R.id.bt_name);
                            CheckBox cb_check = v_workitem.findViewById(R.id.cb_check);

                            final WorkItem wi = selectWorkName(bt_name.getText().toString());

                            //禁用btClick
                            bt_name.setEnabled(false);

                            //顯示cb
                            cb_check.setVisibility(View.VISIBLE);
                            cb_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked) {    //勾選則加入刪除名單內.
                                        String btTag = bt_ok.getTag().toString();
                                        Log.d("itemOnClick before: ", "bt_okTag=" + btTag);

                                        //判斷是否位在第一個
                                        if (btTag.equals(""))
                                            bt_ok.setTag(btTag.concat("_" + wi.getName() + "_"));
                                        else bt_ok.setTag(btTag.concat(wi.getName() + "_"));
                                        btTag = bt_ok.getTag().toString();
                                        Log.d("itemOnClick after: ", "bt_okTag=" + btTag);
                                    } else {  //未勾選則消除刪除名單.
                                        String btTag = bt_ok.getTag().toString();
                                        Log.d("itemOnClick before: ", "bt_okTag=" + btTag);

                                        bt_ok.setTag(btTag.replace("_" + wi.getName() + "_", "_"));
                                        btTag = bt_ok.getTag().toString();
                                        if (btTag.equals("_")) bt_ok.setTag(""); //若為空則清空.
                                        Log.d("itemOnClick after: ", "bt_okTag=" + btTag);
                                    }
                                }
                            });
                        }
                    }

                    bt_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String tmp = bt_ok.getTag().toString();  //Tag格式: _XX_XX_..._XX_XX_
                            Log.d("btokOnClick before: ", "bt_okTag=" + tmp);

                            if (!tmp.equals("")) {
                                //刪除選取資料
                                while (tmp.contains("_") && !tmp.equals("_")) {    //字串是否包含"_".
                                    WorkItem wi = selectWorkName(tmp.substring(1,
                                            tmp.substring(1).indexOf("_") + 1));    //取得第一個"_"後到第二個"_"前的子字串.

                                    //刪除wi
                                    mSQLiteDatabase.execSQL(    //更新PI.
                                            WorkItem.deleteTable(wi)
                                    );
                                    //Log.d("btokOnClick ing: ", "WorkItem.deleteTable(wi)");

                                    //更新pi_works
                                    Cursor c1;
                                    c1 = mSQLiteDatabase.rawQuery("SELECT * FROM " + PlanItem.table_plan + " WHERE 1", null);
                                    c1.moveToFirst();
                                    while (!c1.isAfterLast()) {  //selectPlan 對應pi_works內包含wi者.
                                        // TODO: 2019/3/8 若work可以在多個plan時段重名, 將會造成, 不知道該刪哪時段的work.
                                        PlanItem pi = new PlanItem(
                                                c1.getString(0), c1.getString(1),
                                                c1.getString(2), c1.getString(3));
                                        if (pi.getWorks().contains(wi.getName())) {
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

                                    tmp = tmp.substring(tmp.substring(1).indexOf("_") + 1);   //tmp= 第二個"_"前, 到結尾的字串.
                                    //Log.d("btokOnClick ing: ", "bt_okTag="+tmp);
                                }
                                //Log.d("btokOnClick after: ", "bt_okTag="+tmp);
                                Toast.makeText(context, "刪除成功!!", Toast.LENGTH_SHORT).show();

                                //隱藏確認選項
                                ll_check.setVisibility(View.GONE);
                                bt_delete.setSelected(false);

                                //刷新
                                showPlans();

                            } else Toast.makeText(context, "沒有選擇任何事件!", Toast.LENGTH_SHORT).show();
                        }
                    });

                    bt_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //隱藏delete確認選項
                            ll_check.setVisibility(View.GONE);
                            bt_delete.setSelected(false);

                            showPlans();
                        }
                    });
                }
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
                Log.d("刷新 "+tv_part.getText().toString(), "pi_works="+pi.getWorks());
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
                                if(dd.getReturn().contains("_")) {  //更新才包含底線, 刪除無.
                                    //Log.d("reviseworks befor", "pi.works= "+pi.getWorks());
                                    mSQLiteDatabase.execSQL(    //更新PI.
                                            pi.reviseWorks(dd.getReturn())
                                    );     //final的pi還是能以function修改內容.
                                    //Log.d("reviseworks after", "pi.works= "+pi.getWorks());
                                } else {    //刪除.
                                    mSQLiteDatabase.execSQL(    //更新PI.
                                            pi.removeWorks(dd.getReturn())
                                    );
                                }
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
