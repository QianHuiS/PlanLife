package tw.idv.qianhuis.planlife;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

/*使用方法:
* Dialog ALERT= new FridgeDialog(CONTEXT.this);
* ALERT.BUILD();
* ALERT.show();
* ALERT.setOnDismissListener(new ...{
*   if(!ALERT.getReturn().equals("")) {  mSQLiteDatabase.execSQL(ALERT.getReturn());   }  }
*/

public class DayplanDialog extends Dialog {
    private Context context;
    private String rcontent= "";

    //DB
    private SQLiteDatabase mSQLiteDatabase= null;
    private static final String DATABASE_NAME = "app.db";
    //mSQLiteDatabase = this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);

    public DayplanDialog(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        //開啟DB
        mSQLiteDatabase = context.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        //build();
    }

    //Work Add
    public void buildWorkAdd() {
        LayoutInflater li= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View alertView= li.inflate(R.layout.dialog_ddworkadd, null);

        EditText et_content= alertView.findViewById(R.id.et_content);
        et_content.setMovementMethod(ScrollingMovementMethod.getInstance());    //滾動效果.

        final Button bt_deadline= alertView.findViewById(R.id.bt_deadline);
        bt_deadline.setTag("");
        bt_deadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(bt_deadline.getTag().equals("")) {   //若已選擇過日期, 再次點選擇取消選擇.
                    //開啟日期選窗
                    final DateFunction df= new DateFunction();
                    df.dateSelection(context).setOnDismissListener(new OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if(!df.getRcontent().equals("")) {
                                bt_deadline.setText(df.getRcontent());
                                Log.d("after dialog", "df.getRcontent()= "+df.getRcontent());
                                Log.d("after dialog", "bt.getText().toString()= "+bt_deadline.getText().toString());
                                bt_deadline.setTag("1");
                            }
                        }
                    });
                } else {
                    Log.d("befor cancel content", "bt.getText().toString()= "+bt_deadline.getText().toString());
                    bt_deadline.setText("");
                    Log.d("after cancel content", "bt.getText().toString()= "+bt_deadline.getText().toString());
                    bt_deadline.setTag("");
                }
            }
        });

        Button bt_ok= alertView.findViewById(R.id.bt_ok);
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_name= alertView.findViewById(R.id.et_name);
                EditText et_content= alertView.findViewById(R.id.et_content);

                WorkItem wi= new WorkItem(null,null,null,
                        et_name.getText().toString(), et_content.getText().toString(),
                        null, "", bt_deadline.getText().toString());
                Log.d("befor insert", "bt.getText().toString()= "+bt_deadline.getText().toString());

                if(!wi.getName().equals("")) {  //若名稱不為空.
                    //判斷名稱是否包含底線
                    if(wi.getName().contains("_")) {
                        Toast.makeText(context, "名稱不可包含「_」!!", Toast.LENGTH_SHORT).show();
                    } else {
                        //檢查事件名稱是否重複
                        Cursor c;
                        String WHERE = WorkItem.work_name + " = '" + wi.getName() + "'";
                        c = mSQLiteDatabase.rawQuery("SELECT * FROM " + WorkItem.table_work +
                                " WHERE " + WHERE, null);

                        if (c.getCount() == 0) {   //若無重複名稱.
                            c.close();
                            mSQLiteDatabase.execSQL(WorkItem.insertTable(wi));
                            Toast.makeText(context, "新增成功!!", Toast.LENGTH_SHORT).show();
                            rcontent = wi.getName();
                            dismiss();

                        } else {
                            c.close();
                            Toast.makeText(context, "名稱重複!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else { Toast.makeText(context, "名稱為空白!!", Toast.LENGTH_SHORT).show(); }
            }
        });

        setContentView(alertView);
        setAlertWindow(0.9, 0.8, true);
    }

    //Work Detail
    public void buildWorkDetail(final WorkItem wi) {
        LayoutInflater li= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View alertView= li.inflate(R.layout.dialog_ddworkdetail, null);

        TextView tv_name= alertView.findViewById(R.id.tv_name);
        tv_name.setText(wi.getName());

        TextView tv_content= alertView.findViewById(R.id.tv_content);
        tv_content.setMovementMethod(ScrollingMovementMethod.getInstance());    //滾動效果.
        tv_content.setText(wi.getContent());

        TextView tv_deadline= alertView.findViewById(R.id.tv_deadline);
        tv_deadline.setText(wi.getDeadline());

        Button bt_revise= alertView.findViewById(R.id.bt_revise);
        bt_revise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DayplanDialog dd= new DayplanDialog(context);
                dd.buildWorkRevise(wi);
                dd.show();
                dd.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if(!dd.getReturn().equals("")) {
                            rcontent= dd.getReturn();
                            dismiss();
                        }
                    }
                });
            }
        });

        Button bt_close= alertView.findViewById(R.id.bt_close);
        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        setContentView(alertView);
        setAlertWindow(0.9, 0.65, true);
    }

    //Work Revise
    public void buildWorkRevise(final WorkItem wi) {
        LayoutInflater li= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View alertView= li.inflate(R.layout.dialog_ddworkadd, null);

        EditText et_name= alertView.findViewById(R.id.et_name);
        et_name.setText(wi.getName());

        // TODO: 2019/3/8 滾動無效?
        EditText et_content= alertView.findViewById(R.id.et_content);
        et_content.setMovementMethod(ScrollingMovementMethod.getInstance());    //滾動效果.
        et_content.setText(wi.getContent());

        final Button bt_deadline= alertView.findViewById(R.id.bt_deadline);
        bt_deadline.setTag(wi.getDeadline());
        bt_deadline.setText(wi.getDeadline());
        bt_deadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(bt_deadline.getTag().equals("")) {   //若已選擇過日期, 再次點選擇取消選擇.
                    //開啟日期選窗
                    final DateFunction df= new DateFunction();
                    df.dateSelection(context).setOnDismissListener(new OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if(!df.getRcontent().equals("")) {
                                bt_deadline.setText(df.getRcontent());
                                Log.d("after dialog", "df.getRcontent()= "+df.getRcontent());
                                Log.d("after dialog", "bt.getText().toString()= "+bt_deadline.getText().toString());
                                bt_deadline.setTag("1");
                            }
                        }
                    });
                } else {
                    Log.d("befor cancel content", "bt.getText().toString()= "+bt_deadline.getText().toString());
                    bt_deadline.setText("");
                    Log.d("after cancel content", "bt.getText().toString()= "+bt_deadline.getText().toString());
                    bt_deadline.setTag("");
                }
            }
        });

        Button bt_ok= alertView.findViewById(R.id.bt_ok);
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_name= alertView.findViewById(R.id.et_name);
                EditText et_content= alertView.findViewById(R.id.et_content);

                WorkItem wiR= new WorkItem(wi.getId(),wi.getPrevious(),wi.getNext(),
                        et_name.getText().toString(),
                        et_content.getText().toString(),
                        wi.getType(), wi.getCompletion(),
                        bt_deadline.getText().toString());
                //Log.d("befor revise", "bt.getText().toString()= "+bt_deadline.getText().toString());

                if(!wiR.getName().equals("")) {  //若名稱不為空.
                    //判斷名稱是否包含底線
                    if(wiR.getName().contains("_")) {
                        Toast.makeText(context, "名稱不可包含「_」!!", Toast.LENGTH_SHORT).show();
                    } else {
                        //檢查事件名稱是否重複
                        Cursor c;
                        String WHERE = WorkItem.work_name + " = '" + wiR.getName() + "'";
                        c = mSQLiteDatabase.rawQuery("SELECT * FROM " + WorkItem.table_work +
                                " WHERE " + WHERE, null);
                        c.moveToFirst();

                        if (c.getCount()==0 ||
                                wiR.getName().equals(wi.getName())) {   //若無重複名稱(不含原名).
                            c.close();
                            mSQLiteDatabase.execSQL(WorkItem.updateTable(wiR));
                            Toast.makeText(context, "修改成功!!", Toast.LENGTH_SHORT).show();
                            rcontent= wi.getName()+"_"+wiR.getName();    //格式: oldName_newName
                            dismiss();

                        } else {
                            c.close();
                            Toast.makeText(context, "名稱重複!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else { Toast.makeText(context, "名稱為空白!!", Toast.LENGTH_SHORT).show(); }
            }
        });

        setContentView(alertView);
        setAlertWindow(0.9, 0.8, true);
    }

    public void buildWorkDelete(final WorkItem wi) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View alertView = inflater.inflate(R.layout.dialog_ddworkdelete, null);    //layout可換!

        TextView tv_message= alertView.findViewById(R.id.tv_message);
        tv_message.setText("確定要刪除事件 "+wi.getName()+" ?");

        alertView.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {    //bt可換!
            @Override
            public void onClick(View v) {
                mSQLiteDatabase.execSQL(WorkItem.deleteTable(wi));
                Toast.makeText(context, "刪除成功!!", Toast.LENGTH_SHORT).show();
                rcontent= wi.getName();
                dismiss();
            }
        });

        alertView.findViewById(R.id.bt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        setContentView(alertView);
        setAlertWindow(0.8, 0.3, true);
    }

    //視窗大小設定
    private void setAlertWindow(double w, double h, boolean touchOut){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();   //取得螢幕寬高.
        Point point = new Point();
        display.getSize(point);

        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();   //取得當前alert參數.

        //window.setGravity(Gravity.BOTTOM);  //對話框居中靠底.
        //lp.x = 250; //alert右移.
        //lp.y = 250; //alert上移.

        //alert尺寸
        lp.width = (int) (point.x * w);     //螢幕寬度的幾倍.
        lp.height = (int) (point.y * h);    //螢幕高度的幾倍.
//        layoutParams.width = (int) (display.getWidth() * 0.5);
//        layoutParams.height = (int) (display.getHeight() * 0.5);
        window.setAttributes(lp);
        setCanceledOnTouchOutside(touchOut);    //是否點擊alert外部區域即關閉alert.

    }

    private void setAlertWindow(double w, double h, boolean touchOut, String gravity, float dimamount){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();   //取得螢幕寬高.
        Point point = new Point();
        display.getSize(point);

        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();   //取得當前alert參數.

        if(!gravity.equals("")) {
            switch (gravity) {
                case "top":
                    window.setGravity(Gravity.TOP);  //對話框居中靠上.
                    break;
                case "bottom":
                    window.setGravity(Gravity.BOTTOM);  //對話框居中靠下.
                    break;
                case "left":
                    window.setGravity(Gravity.START);  //對話框居中靠左.
                    break;
                case "right":
                    window.setGravity(Gravity.END);  //對話框居中靠右.
                    break;
                default:
            }
        }
        window.setDimAmount(dimamount);    //視窗背後遮罩明暗; 亮到暗的透明度變化(0f-1f最暗), 預設為0.6f.

        //alert尺寸
        lp.width = (int) (point.x * w);     //螢幕寬度的幾倍.
        lp.height = (int) (point.y * h);    //螢幕高度的幾倍.
//        layoutParams.width = (int) (display.getWidth() * 0.5);
//        layoutParams.height = (int) (display.getHeight() * 0.5);
        window.setAttributes(lp);
        setCanceledOnTouchOutside(touchOut);    //是否點擊alert外部區域即關閉alert.

    }

    public String getReturn() {
        return rcontent;
    }

}
