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

        String tmp= works;  //works格式: _XX_XX_..._XX_XX_
        int i= 0;
        while(tmp.contains("_") && !tmp.equals("_")) {    //字串是否包含"_", 且不為最後一項(只剩下_).
            if(i== w) {
                //Log.d("getW: loop"+i+" concat befor", "work="+work);
                work= work.concat(tmp.substring(1,
                        tmp.substring(1).indexOf("_")+1));     //取得第一個"_"後到第二個"_"前的子字串.
                work= work.trim();
                Log.d("getW: loop"+i+" concat after", "work="+work);
            }
            i++;
            //Log.d("getW: loop"+i+" tmp befor", "tmp="+tmp);
            tmp= tmp.substring(tmp.substring(1).indexOf("_")+1);   //tmp= 第二個"_"前, 到結尾的字串.
            //Log.d("getW: loop"+i+" tmp after", "tmp="+tmp);
        }

        Log.d("getW: return befor", "work="+work);
        return work;
    }

    public String addWorks(String addWname) {
        //判斷是否位在第一個
        if(works.equals("")) works = works.concat("_"+addWname+"_");
        else works= works.concat(addWname+"_");

        return updateTable(this);
    }

    public String reviseWorks(String reviseWnames) {
        String oldWname= reviseWnames.substring(0, reviseWnames.indexOf("_"));
        Log.d("reviseWorks: ", "oldWname="+oldWname);
        String newWname= reviseWnames.substring(reviseWnames.indexOf("_")+1);
        Log.d("reviseWorks: ", "newWnames="+newWname);

        if(works.contains("_"+oldWname+"_")) {
            works= works.replace("_"+oldWname+"_","_"+newWname+"_");
        }

        return updateTable(this);
    }

    public String removeWorks(String removeWname) {
        if(works.contains("_"+removeWname+"_")) {
            works= works.replace("_"+removeWname+"_","_");
            if(works.equals("_"))   works= "";  //若works只有_, 則為空;
        }

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
