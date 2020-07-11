package biz.dealnote.messenger.db.column;

import android.provider.BaseColumns;

public class GroupsDetColumns implements BaseColumns {

    public static final String TABLENAME = "groups_det";
    public static final String DATA = "data";
    public static final String FULL_ID = TABLENAME + "." + _ID;
    public static final String FULL_DATA = TABLENAME + "." + DATA;

    private GroupsDetColumns() {
    }
}
