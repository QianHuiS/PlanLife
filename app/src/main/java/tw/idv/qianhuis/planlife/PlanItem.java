package tw.idv.qianhuis.planlife;

import android.util.Log;

public class PlanItem {

    public static final String table_plan = "table_plan";
    public static final String plan_id = "plan_id";
    public static final String plan_part = "plan_part";
    public static final String plan_works = "plan_works";
    public static final String plan_date = "plan_date";

    private String id;
    private String part;
    private String works;
    private String date;

    public PlanItem(String id, String part, String works, String date) {
        this.id = id;
        this.part = part;
        this.works = works;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getWorks() {
        return works;
    }

    public void setWorks(String works) {
        this.works = works;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getW(int w) {
        String work= "";

        String tmp= works;  //works格式: XX_XX_...XX_XX
        int i= 0;
        while(tmp.contains("_")) {    //字串是否包含"_".
            if(i== w) {
                work= work.concat(tmp.substring(0, tmp.indexOf("_")));     //取得開始到第一個"_"前的子字串.
                work= work.trim();
            }
            i++;
            tmp= tmp.substring(tmp.indexOf("_")+1);   //tmp1= 第一個"_"後, 到結尾的字串.
        }
        //剩最後一項(tmp沒有"_")
        if(i==w) {
            work = work.concat(tmp).trim();
        }

        return work;
    }

    // TODO: 2019/2/28 final的pi是否可以使用??
    public String addWorks(String addWname) {
        //判斷是否位在第一個
        if(works.equals("")) works = works.concat(addWname);
        else works= works.concat("_"+addWname);

        return updateTable(this);
    }

    public String removeWorks(String removeWname) {
        //判斷是否位在第一個
        if(!works.contains("_"+removeWname)) works = works.replace(removeWname,"");
            //在名稱不會重複的前提下, _removeWname不存在是不可能的, 故表示位在第一(沒有"_").
        else works= works.replace("_"+removeWname,"");

        return updateTable(this);
    }

    //DB function
    public static String insertTable(PlanItem pi) {
        String INSERT_TABLE= "INSERT INTO "+table_plan+" ("+
                plan_part +", " +
                plan_works +", " +
                plan_date +" ) " +
                "VALUES('"+pi.getPart()+"', '"+pi.getWorks()+"', '"+pi.getDate()+"' )";
        return INSERT_TABLE;
    }

    public static String updateTable(PlanItem pi) {
        String UPDATE_TABLE= "UPDATE "+ table_plan +" SET " +
                plan_part +"='"+pi.getPart()+"', " +
                plan_works +"='"+pi.getWorks()+"', " +
                plan_date +"='"+pi.getDate()+"' " +
                "WHERE "+ plan_id +"=" + pi.getId();
        return UPDATE_TABLE;
    }

    public static String deleteTable(PlanItem pi) {
        String DELETE_TABLE= "DELETE FROM "+ table_plan +" WHERE "+ plan_id +"=" +pi.getId();
        return DELETE_TABLE;
    }
}
