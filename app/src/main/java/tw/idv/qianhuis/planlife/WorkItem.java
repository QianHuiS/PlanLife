package tw.idv.qianhuis.planlife;

public class WorkItem {

    public static final String table_work = "table_work";
    public static final String work_id = "work_id";
    public static final String work_previous = "work_previous";
    public static final String work_next = "work_next";
    public static final String work_name = "work_name";
    public static final String work_content = "work_content";
    public static final String work_type = "work_type";
    public static final String work_completion = "work_completion";
    public static final String work_deadline = "work_deadline";

    private String id;
    private String previous;
    private String next;
    private String name;
    private String content;
    private String type;
    private String completion;
    private String deadline;

    public WorkItem(String id, String previous, String next,
                    String name, String content, String type,
                    String completion, String deadline) {
        this.id = id;
        this.previous = previous;
        this.next = next;
        this.name = name;
        this.content = content;
        this.type = type;
        this.completion = completion;
        this.deadline = deadline;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCompletion() {
        return completion;
    }

    public void setCompletion(String completion) {
        this.completion = completion;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    //DB function
    public static String insertTable(WorkItem wi) {
        String INSERT_TABLE= "INSERT INTO "+table_work+" ("+
                work_previous +", " +
                work_next +", " +
                work_name +", " +
                work_content +", " +
                work_type +", " +
                work_completion +", " +
                work_deadline +" ) " +
                "VALUES('"+wi.getPrevious()+"', '"+wi.getNext()+"', '"+wi.getName()+"', " +
                "'"+wi.getContent()+"', '"+wi.getType()+"', '"+wi.getCompletion()+"', '"+wi.getDeadline()+"' )";
        return INSERT_TABLE;
    }

    public static String updateTable(WorkItem wi) {
        String UPDATE_TABLE= "UPDATE "+ table_work +" SET " +
                work_previous +"='"+wi.getPrevious()+"', " +
                work_next +"='"+wi.getNext()+"', " +
                work_name +"='"+wi.getName()+"', " +
                work_content +"='"+wi.getContent()+"', " +
                work_type +"='"+wi.getType()+"', " +
                work_completion +"='"+wi.getCompletion()+"', " +
                work_deadline +"='"+wi.getDeadline()+"' " +
                "WHERE "+ work_id +"=" + wi.getId();
        return UPDATE_TABLE;
    }

    public static String deleteTable(WorkItem wi) {
        String DELETE_TABLE= "DELETE FROM "+ table_work +" WHERE "+ work_id +"=" +wi.getId();
        return DELETE_TABLE;
    }
}
