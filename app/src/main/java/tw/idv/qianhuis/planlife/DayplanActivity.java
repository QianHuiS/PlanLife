package tw.idv.qianhuis.planlife;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DayplanActivity extends AppCompatActivity {

    //變數
    final Context context= DayplanActivity.this;
    LinearLayout ll_dayplan;

    //DB

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dayplan);

        //XML
        ll_dayplan= findViewById(R.id.ll_dayplan);

        //建立時段
        for(int i=0; ll_dayplan.getChildCount()<3; i++) {    //如果沒view則新增.
            final View v_plan = View.inflate(context, R.layout.view_plan, null);
            TextView tv_part= v_plan.findViewById(R.id.tv_part);
            Button bt_planadd = v_plan.findViewById(R.id.bt_planadd);
            final LinearLayout ll_plans= v_plan.findViewById(R.id.ll_plans);

            if(i==0)    tv_part.setText("起床");
            else if(i==1)    tv_part.setText("午餐");
            else if(i==2)    tv_part.setText("晚餐");
            bt_planadd.setOnClickListener(new View.OnClickListener() {  //新增事件
                @Override
                public void onClick(View v) {
                    // TODO: 2019/2/24 開啟選擇事件Dialog, 選擇事件.
                    final DayplanDialog dd= new DayplanDialog(context);
                    dd.buildDWInput();
                    dd.show();
                    dd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if(!dd.getReturn().equals("")) {
                                //新增事件按紐
                                final Button bt_plan= new Button(context);
                                bt_plan.setText(dd.getReturn());    // TODO: 2019/2/24 回傳的事件名稱.
                                bt_plan.setTextSize(12);
                                //bt_plan.setBackground((getResources().getDrawable(context, R.drawable.XX_bg));
                                //備註, Activity用getResources(); Dialog用ContextCompat.
                                // TODO: 2019/2/24 要不要用width=parent???
                                //bt_plan.setLayoutParams(new LinearLayout.LayoutParams(
                                  //      300, LinearLayout.LayoutParams.WRAP_CONTENT, 0));    //設定比重weight.

                                // TODO: 2019/2/24 btplan短按事件, 開啟選擇事件.

                                bt_plan.setOnLongClickListener(new View.OnLongClickListener() {     //長按移除.
                                    @Override
                                    public boolean onLongClick(View v) {
                                        ll_plans.removeView(bt_plan);
                                        return true;   //表只執行長按事件; 長按結束後不執行短按事件.
                                    }
                                });

                                ll_plans.addView(bt_plan);
                            } else {
                                Toast.makeText(context, "事件名為空白!", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }

            });
            ll_dayplan.addView(v_plan);
        }


    }
}
