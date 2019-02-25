package tw.idv.qianhuis.planlife;

import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

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
        mSQLiteDatabase = context.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        //build();
    }

    //Work Add
    public void buildDWInput() {
        LayoutInflater li= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View alertView= li.inflate(R.layout.dialog_dwinput, null);

        Button bt_dwcheck= alertView.findViewById(R.id.bt_dwcheck);
        bt_dwcheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_dwname= alertView.findViewById(R.id.et_dwname);
                rcontent= et_dwname.getText().toString();
                dismiss();
                // TODO: 2019/2/25 建資料庫紀錄事件.
            }
        });

        setContentView(alertView);
        setAlertWindow(0.8, 0.4, true);
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
