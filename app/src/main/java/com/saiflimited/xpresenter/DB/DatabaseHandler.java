package com.saiflimited.xpresenter.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.saiflimited.xpresenter.Models.Content;
import com.saiflimited.xpresenter.Models.ContentData.ContentDetail;
import com.saiflimited.xpresenter.Models.ContentData.ContentDocument;
import com.saiflimited.xpresenter.Models.ContentData.ContentItem;
import com.saiflimited.xpresenter.Models.ContentData.ContentItemList;
import com.saiflimited.xpresenter.Models.Publisher.ContentType;
import com.saiflimited.xpresenter.Models.Publisher.Publisher;
import com.saiflimited.xpresenter.Models.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static DatabaseHandler mInstance = null;

    private SimpleDateFormat dtFormatterForDB = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    //region Database

    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static factory method "getInstance()" instead.
     */
    private DatabaseHandler(Context context) {
        super(context, "IPREMIOS", null, DATABASE_VERSION);
    }

    public static DatabaseHandler getInstance(Context ctx) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            mInstance = new DatabaseHandler(ctx.getApplicationContext());
        }
        return mInstance;
    }

    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE Publisher(   " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", code TEXT NOT NULL" +
                ", name TEXT NOT NULL" +
                ", logo TEXT NOT NULL" +
                ", background TEXT NOT NULL" +
                ", appname TEXT NOT NULL" +
                ", messages TEXT" +
                ", lastUpdated DATE NOT NULL )");

        db.execSQL("CREATE TABLE PublisherContentType( " +
                "_id INTEGER PRIMARY KEY  AUTOINCREMENT" +
                ", publisher_code TEXT NOT NULL" +
                ", sequence TEXT NOT NULL" +
                ", code TEXT NOT NULL" +
                ", label TEXT NOT NULL" +
                ", format TEXT NOT NULL" +
                ", FOREIGN KEY(publisher_code) REFERENCES Publisher (code) )");

        db.execSQL("CREATE TABLE User(  " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", username TEXT NOT NULL" +
                ", name TEXT NOT NULL" +
                ", pin TEXT NOT NULL" +
                ", mobileNumber TEXT NOT NULL" +
                ", loginRestriction TEXT NOT NULL" +
                ", maxUserSyncDelta INT NOT NULL" +
                ", timezone TEXT NOT NULL" +
                ", lastUpdate DATE NOT NULL" +
                ", lastLoginDate DATE NOT NULL" +
                ", lastSyncDate DATE NOT NULL" +
                ", [language] TEXT" +
                ", dateFormat TEXT NOT NULL" +
                ", decimalSep TEXT NOT NULL" +
                ", firstAccess TEXT )");

        db.execSQL("CREATE TABLE Content(  " +
                "  id INTEGER " +
                ", name TEXT NOT NULL" +
                ", [from] TEXT NOT NULL" +
                ", format TEXT NOT NULL" +
                ", type TEXT NOT NULL" +
                ", fromDate DATE NOT NULL" +
                ", toDate DATE NOT NULL" +
                ", lastUpdated DATE NOT NULL" +
                ", contentData BLOB " +
                ", brand TEXT NOT NULL" +
                ", activity TEXT NOT NULL" +
                ", goal TEXT NOT NULL" +
                ", dateStart TEXT NOT NULL" +
                ", dateEnd TEXT NOT NULL" +
                ", reachDescription TEXT NOT NULL" +
                ", rule TEXT NOT NULL" +
                ", implementedBy TEXT NOT NULL )");
//                ", contentData BLOB )");

        db.execSQL("CREATE TABLE ContentDetail(  " +
                "  id INTEGER " +
                ", seq INTEGER NOT NULL" +
                ", name TEXT NOT NULL" +
                ", FOREIGN KEY(id) REFERENCES Content (id) )");

        db.execSQL("CREATE TABLE ContentItem(  " +
                "  seq INTEGER NOT NULL" +
                ", id INTEGER NOT NULL " +
                ", name TEXT NOT NULL" +
                ", icon BLOB NOT NULL" +
                ", htmlBase64 BLOB NOT NULL" +
                ", FOREIGN KEY(seq) REFERENCES ContentDetail (seq) )");


    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Publisher");
        db.execSQL("DROP TABLE IF EXISTS PublisherContentType");
        db.execSQL("DROP TABLE IF EXISTS User");
        db.execSQL("DROP TABLE IF EXISTS Content");
        onCreate(db);
    }
    //endregion

    //region User
    public long addUser(User user) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", user.getUsername());
        values.put("name", user.getName());
        values.put("pin", user.getPin());
        values.put("mobileNumber", user.getMobileNumber());
        values.put("loginRestriction", user.getLoginRestriction());
        values.put("maxUserSyncDelta", user.getMaxUserSyncDelta());
        values.put("timezone", user.getTimezone());
        values.put("lastUpdate", "");
        values.put("lastLoginDate", "");
        values.put("lastSyncDate", "");
        values.put("language", user.getLanguage());
        values.put("dateFormat", user.getFormat());
        values.put("decimalSep", user.getDecimalSep());
        long l = writableDatabase.insert("User", null, values);
        writableDatabase.close();
        return l;
    }

    public void deleteUserList(ArrayList<User> numbers) {

        //The string builder used to construct the string
        StringBuilder commaSepValueBuilder = new StringBuilder();

        //Looping through the list
        for (int i = 0; i < numbers.size(); i++) {
            //append the value into the builder
            commaSepValueBuilder.append(numbers.get(i).getUsername());
            //if the value is not the last element of the list
            //then append the comma(,) as well
            if (i != numbers.size() - 1) {
                commaSepValueBuilder.append("', '");
            }
        }

        SQLiteDatabase writableDatabase = getWritableDatabase();
        int i = writableDatabase.delete("User", "username NOT IN ('" + commaSepValueBuilder + "')", null);
        writableDatabase.close();
    }

    public boolean isFirstAccess(String username) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT pin from User WHERE username = '" + username + "'", null);
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToNext()) {
                String PIN = cursor.getString(0);
                if (PIN.equals("")) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean updatePIN(String username, String PIN) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("pin", PIN);
        return readableDatabase.update("User", values, "username = ?", new String[]{username}) == 1;
    }

    public void updateUserLastSyncDate(String username) {
        Calendar calendar = Calendar.getInstance();
        SQLiteDatabase readableDatabase = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("lastSyncDate", this.dtFormatterForDB.format(calendar.getTime()));
        String[] strings = new String[1];
        strings[0] = username;
        readableDatabase.update("User", values, "username = ?", strings);
    }

    public void updateUserLastUpdateDate(User user) {
        Calendar calendar = Calendar.getInstance();
        SQLiteDatabase readableDatabase = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("lastUpdate", this.dtFormatterForDB.format(calendar.getTime()));
        String[] strings = new String[1];
        strings[0] = user.getUsername();
        readableDatabase.update("User", values, "username = ?", strings);
    }

    public void updateUserLastLoginDate(String username) {
        Calendar calendar = Calendar.getInstance();
        SQLiteDatabase readableDatabase = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("lastLoginDate", this.dtFormatterForDB.format(calendar.getTime()));
        String[] strings = new String[1];
        strings[0] = username;
        readableDatabase.update("User", values, "username = ?", strings);
    }

    public boolean usernameExists(String username) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT username from User WHERE username = '" + username + "'", null);
        boolean valid = false;
        if (cursor != null) {
            int i = cursor.getCount();
            valid = false;
            if (i > 0) {
                valid = true;
            }
        }
        return valid;
    }

    public boolean validatePIN(String username, String PIN) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT pin from User WHERE username = '" + username + "'", null);
        boolean valid = false;
        if (cursor != null) {
            cursor.moveToNext();
            if (cursor.getString(0).equals(PIN)) {
                valid = true;
            } else {
                valid = false;
            }
        }
        return valid;
    }

    public String getLoginRestriction(String username) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT loginRestriction FROM User WHERE username = '" + username + "'", null);
        int i = cursor.getCount();
        String str = null;
        if (i > 0) {
            cursor.moveToNext();
            str = cursor.getString(0);
        }
        return str;
    }

    public int getMaxSyncDelta(String username) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT maxUserSyncDelta FROM User WHERE username = '" + username + "'", null);
        int i = cursor.getCount();
        int maxSyncDelta = 0;
        if (i > 0) {
            cursor.moveToNext();
            maxSyncDelta = cursor.getInt(0);
        }
        return maxSyncDelta;
    }

    public String getLastSyncDate(String username) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT lastSyncDate FROM User WHERE username = '" + username + "'", null);
        int i = cursor.getCount();
        String str = null;
        if (i > 0) {
            cursor.moveToNext();
            str = cursor.getString(0);
        }
        return str;
    }

    public String getMobileNumber(String username) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT mobileNumber FROM User WHERE username = '" + username + "'", null);
        int i = cursor.getCount();
        String str = null;
        if (i > 0) {
            cursor.moveToNext();
            str = cursor.getString(0);
        }
        return str;
    }
    //endregion

    //region Publisher
    public long addPublisher(Publisher publisher) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("code", publisher.getCode());
        values.put("name", publisher.getName());
        values.put("logo", publisher.getLogo());
        values.put("background", publisher.getBackground());
        values.put("appname", publisher.getAppname());
        values.put("messages", publisher.getMessages());
        values.put("lastUpdated", publisher.getLastUpdated());
        long l = writableDatabase.insert("Publisher", null, values);
        writableDatabase.close();
        return l;
    }

    public long addPublisherContentType(ContentType contentType) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("publisher_code", contentType.getPublisherCode());
        values.put("sequence", contentType.getSequence());
        values.put("code", contentType.getCode());
        values.put("label", contentType.getLabel());
        values.put("format", contentType.getFormat());
        long l = writableDatabase.insert("PublisherContentType", null, values);
        writableDatabase.close();
        return l;
    }

    public void deleteContentType() {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.delete("PublisherContentType", null, null);
        writableDatabase.close();
    }

    public void deletePublisher() {
        SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
        localSQLiteDatabase.delete("Publisher", null, null);
        localSQLiteDatabase.close();
    }

    public String getBackground() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT background FROM Publisher", null);
        int i = cursor.getCount();
        String str = null;
        if (i > 0) {
            str = null;
            if (cursor != null) {
                cursor.moveToNext();
                str = cursor.getString(0);
            }
        }
        cursor.close();
        db.close();
        return str;
    }

    public String getLogo() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT logo FROM Publisher", null);
        int i = cursor.getCount();
        String str = null;
        if (i > 0) {
            str = null;
            if (cursor != null) {
                cursor.moveToNext();
                str = cursor.getString(0);
            }
        }
        cursor.close();
        db.close();
        return str;
    }

    public int getPublisherContentTypeCount() {
        Cursor localCursor = getReadableDatabase().rawQuery("SELECT * from PublisherContentType", null);
        if (localCursor != null) {
            return localCursor.getCount();
        }
        return 0;
    }

    public String getPublisherContentTypeName(int paramInt) {
        Cursor localCursor = getReadableDatabase().rawQuery("SELECT label FROM PublisherContentType WHERE sequence = " + paramInt, null);
        int i = localCursor.getCount();
        String str = null;
        if (i > 0) {
            localCursor.moveToNext();
            str = localCursor.getString(0);
        }
        return str;
    }

    public String getFormat(CharSequence label) {
        Cursor localCursor = getReadableDatabase().rawQuery("SELECT format FROM PublisherContentType WHERE label = '" + label + "'", null);
        int i = localCursor.getCount();
        String str = null;
        if (i > 0) {
            localCursor.moveToNext();
            str = localCursor.getString(0);
        }
        return str;
    }

    public String getAppName() {
        Cursor localCursor = getReadableDatabase().rawQuery("SELECT appname FROM Publisher ", null);
        int i = localCursor.getCount();
        String str = null;
        if (i > 0) {
            localCursor.moveToNext();
            str = localCursor.getString(0);
        }
        return str;
    }
    //endregion

    //region Content
    public long addContent(Content content) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", content.getId());
        values.put("name", content.getName());
        values.put("[from]", content.getFrom());
        values.put("format", content.getFormat());
        values.put("type", content.getType());
        values.put("fromDate", content.getFromDate());
        values.put("toDate", content.getToDate());
        values.put("lastUpdated", content.getLastUpdated());
        values.put("contentData", content.getContentDocument());
        values.put("brand", content.getBrand());
        values.put("activity", content.getActivity());
        values.put("goal", content.getGoal());
        values.put("dateStart", content.getDateStart());
        values.put("dateEnd", content.getDateEnd());
        values.put("reachDescription", content.getReachDescription());
        values.put("rule", content.getRule());
        values.put("implementedBy", content.getImplementedBy());
        long l = database.insert("Content", null, values);
        database.close();
        return l;
    }

    public void deleteContent() {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.delete("Content", null, null);
        writableDatabase.rawQuery("DELETE FROM Content", null);
        writableDatabase.close();
    }

    public Cursor getAllContentId() {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT id FROM Content", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            return cursor;
        }
        return null;
    }

    public int getContentCount() {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * from Content", null);
        if (cursor != null) {
            return cursor.getCount();
        }
        return 0;
    }

    public String getContentDoc(int contentId) {
        //TODO change to id
        Cursor cursor = getReadableDatabase().rawQuery("SELECT contentData FROM Content WHERE id = 1"/* + contentId*/, null);
        if ((cursor.getCount() > 0) && (cursor != null)) {
            cursor.moveToNext();
        }
        for (String str = cursor.getString(0); ; str = null) {
            cursor.close();
            return str;
        }
    }

    public void updateContentData(String contentId, String contentData, ContentDocument contentDocument) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        com.saiflimited.xpresenter.Models.ContentData.Content content = contentDocument.getContent();
        ContentValues values = new ContentValues();
        values.put("contentData", contentData);
        values.put("brand", content.getBrand());
        values.put("activity", content.getActivity());
        values.put("goal", content.getGoal());
        values.put("dateStart", content.getDateStart());
        values.put("dateEnd", content.getDateEnd());
        values.put("reachDescription", content.getReachDescription());
        values.put("rule", content.getRule());
        values.put("implementedBy", content.getImplementedBy());
        readableDatabase.update("Content", values, "id = ?", new String[]{contentId});
    }

    public String getLastContentUpdateDate(int contentId) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT lastUpdated FROM Content WHERE id = '" + contentId + "'", null);
        int i = cursor.getCount();
        String str = null;
        if (i > 0) {
            cursor.moveToNext();
            str = cursor.getString(0);
        }
        return str;
    }

    public boolean contentExists() {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT id from Content", null);
        boolean valid = false;
        if (cursor != null) {
            int i = cursor.getCount();
            valid = false;
            if (i > 0) {
                valid = true;
            }
        }
        return valid;
    }

    public void addContentDocument(String contentId, ContentDocument contentDocument) {
        ArrayList<ContentDetail> contentDetails = contentDocument.getContent().getContentDetailList();
        for (int i = 0; i < contentDetails.size(); i++) {
            addContentDetail(contentId, contentDetails.get(i));
            ArrayList<ContentItemList> contentItemLists = contentDetails.get(i).getContentItemList();
            for (int j = 0; j < contentItemLists.size(); j++) {
                addContentItem(contentDetails.get(i).getSeq(), contentItemLists.get(j).getContentItem());
            }
        }
    }

    private long addContentItem(String seq, ContentItem contentItem) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("seq", seq);
        values.put("id", contentItem.getId());
        values.put("name", contentItem.getName());
        values.put("icon", contentItem.getIcon());
        values.put("htmlBase64", contentItem.getHtmlBase64());
        long l = database.insert("ContentItem", null, values);
        database.close();
        return l;
    }

    private long addContentDetail(String contentId, ContentDetail contentDetail) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", contentId);
        values.put("seq", contentDetail.getSeq());
        values.put("name", contentDetail.getName());
        long l = database.insert("ContentDetail", null, values);
        database.close();
        return l;
    }

    public Cursor getContentIdsForType(String type) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT id FROM Content where type = '" + type + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            return cursor;
        }
        return null;
    }

    public Cursor getContentList(String label) {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT id as _id" +
                ", brand" +
                ", activity" +
                ", goal " +
                "FROM content c " +
                "INNER JOIN PublisherContentType p " +
                "ON c.type = p.code " +
                "AND p.label = '" + label + "'" +
                "ORDER BY brand ASC";

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            cursor.moveToNext();
        }

        return cursor;
    }
    //endregion

}