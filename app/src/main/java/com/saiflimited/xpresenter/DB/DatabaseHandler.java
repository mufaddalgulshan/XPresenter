package com.saiflimited.xpresenter.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import com.saiflimited.xpresenter.Models.Content.Content;
import com.saiflimited.xpresenter.Models.Content.ContentDetail;
import com.saiflimited.xpresenter.Models.Content.ContentDocument;
import com.saiflimited.xpresenter.Models.Content.ContentItem;
import com.saiflimited.xpresenter.Models.Content.ContentItemList;
import com.saiflimited.xpresenter.Models.Publisher.ContentType;
import com.saiflimited.xpresenter.Models.Publisher.Publisher;
import com.saiflimited.xpresenter.Models.User.User;
import com.saiflimited.xpresenter.Utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static DatabaseHandler mInstance = null;
    private Context context;

    private SimpleDateFormat dtFormatterForDB = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    //region Database

    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static factory method "getInstance()" instead.
     */
    private DatabaseHandler(Context context) {
        super(context, "IPREMIOS", null, DATABASE_VERSION);
        this.context = context;
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
                ", implementedBy TEXT NOT NULL" +
                ", icon BLOB NOT NULL" +
                ", image BLOB NOT NULL )");
//                ", contentData BLOB )");

        db.execSQL("CREATE TABLE ContentDetail(  " +
                "  id INTEGER " +
                ", seq INTEGER NOT NULL" +
                ", name TEXT NOT NULL" +
                ", FOREIGN KEY(id) REFERENCES Content (id) )");

        db.execSQL("CREATE TABLE ContentItem(  " +
                "  seq INTEGER NOT NULL" +
                ", id INTEGER NOT NULL " +
                ", contentId INTEGER NOT NULL" +
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
        db.execSQL("DROP TABLE IF EXISTS ContentDetail");
        db.execSQL("DROP TABLE IF EXISTS ContentItem");
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

    public String getPIN(String username) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT pin FROM User WHERE username = '" + username + "'", null);
        int i = cursor.getCount();
        String str = null;
        if (i > 0) {
            cursor.moveToNext();
            str = cursor.getString(0);
        }
        return str;
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

    //region Add
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
        values.put("icon", content.getIcon());
        values.put("image", content.getImage());
        long l = database.insert("Content", null, values);
        database.close();
        return l;
    }

    public void addContentDocument(String contentId, ContentDocument contentDocument) {
        ArrayList<ContentDetail> contentDetails = contentDocument.getContent().getContentDetailList();
        for (int i = 0; i < contentDetails.size(); i++) {
            addContentDetail(contentId, contentDetails.get(i));
            ArrayList<ContentItemList> contentItemLists = contentDetails.get(i).getContentItemList();
            for (int j = 0; j < contentItemLists.size(); j++) {
                addContentItem(contentDetails.get(i).getSeq(), contentId, contentItemLists.get(j).getContentItem());
            }
        }
    }

    public long addContentItem(String seq, String contentId, ContentItem contentItem) {
        SQLiteDatabase database = getWritableDatabase();

        Uri uri = Utils.getUri(contentItem.getIcon(), contentId + "_" + contentItem.getName(), context);
        contentItem.setIcon(uri.toString());
        Log.i("Content Item URI", uri.toString());
        ContentValues values = new ContentValues();
        values.put("seq", seq);
        values.put("id", contentItem.getId());
        values.put("contentId", contentId);
        values.put("name", contentItem.getName());
        values.put("icon", contentItem.getIcon());
        values.put("htmlBase64", contentItem.getHtmlBase64());
        long l = database.insert("ContentItem", null, values);
        database.close();
        return l;
    }

    public long addContentDetail(String contentId, ContentDetail contentDetail) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", contentId);
        values.put("seq", contentDetail.getSeq());
        values.put("name", contentDetail.getName());
        long l = database.insert("ContentDetail", null, values);
        database.close();
        return l;
    }

    //JSON Content Document
    public void updateContentDoc(String contentId, String contentDoc, ContentDocument contentDocument) {
        SQLiteDatabase readableDatabase = getReadableDatabase();

        Content content = contentDocument.getContent();

        Uri uri = Utils.getUri(content.getIcon(), "icon_" + content.getBrand(), context);
        content.setIcon(uri.toString());
        Log.i("Icon URI", uri.toString());
        uri = Utils.getUri(content.getImage(), "image_" + content.getBrand(), context);
        content.setImage(uri.toString());
        Log.i("Image URI", uri.toString());

        ContentValues values = new ContentValues();
        values.put("contentData", contentDoc);
        values.put("brand", content.getBrand());
        values.put("activity", content.getActivity());
        values.put("goal", content.getGoal());
        values.put("dateStart", content.getDateStart());
        values.put("dateEnd", content.getDateEnd());
        values.put("reachDescription", content.getReachDescription());
        values.put("rule", content.getRule());
        values.put("implementedBy", content.getImplementedBy());
        values.put("icon", content.getIcon());
        values.put("image", content.getImage());
        readableDatabase.update("Content", values, "id = ?", new String[]{contentId});
    }

    //HTML Content Document
    public void updateContentDoc(String contentId, String contentDoc) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("contentData", contentDoc);
        readableDatabase.update("Content", values, "id = ?", new String[]{contentId});
    }

    public void deleteContent() {
        deleteContentDetail();
        deleteContentItem();
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.delete("Content", null, null);
        writableDatabase.close();
    }

    public void deleteContentItem() {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.delete("ContentItem", null, null);
        writableDatabase.close();
    }

    public void deleteContentDetail() {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.delete("ContentDetail", null, null);
        writableDatabase.close();
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

    public Cursor getAllContentId() {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT id FROM Content", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            return cursor;
        }
        return null;
    }

    public String getContentDoc(String label) {

        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT c.contentData" +
                " FROM content c " +
                " INNER JOIN PublisherContentType p " +
                " ON LOWER(c.type) = LOWER(p.code) " +
                " AND LOWER(p.label) = '" + label.toLowerCase() + "'" +
                " AND LOWER(c.format) = 'html' ";

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            cursor.moveToNext();
        }

        return cursor.getString(0);
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

    public Cursor getContentList(String label) {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT id as _id" +
                ", brand" +
                ", activity" +
                ", goal " +
                ", icon " +
                " FROM content c " +
                " INNER JOIN PublisherContentType p ";

        //TODO remove if statement
        if (label.toLowerCase().equals("marcas")) {
            sql = sql + "ON LOWER(c.type) = 'brand' ";
        } else {
            sql = sql + "ON LOWER(c.type) = LOWER(p.code) ";
        }

        sql = sql +
                " AND LOWER(p.label) = '" + label.toLowerCase() + "'" +
                " AND c.format = 'JSON' " +
                " ORDER BY brand ASC";

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            cursor.moveToNext();
        }

        return cursor;
    }

    public Cursor getContentDrilldown(long contentId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT seq as _id" +
                ", name " +
                "FROM ContentDetail " +
                "WHERE id = " + contentId +
                " ORDER BY name ASC";

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            cursor.moveToNext();
        }

        return cursor;
    }

    public Cursor getContentDetailsDrilldown(long contentId, int seq) {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT id as _id" +
                ", name" +
                ", icon " +
                " FROM ContentItem " +
                " WHERE seq = " + seq +
                " AND contentId = " + contentId +
                " ORDER BY name ASC";

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            cursor.moveToNext();
        }

        return cursor;
    }

    public Content getContent(long contentId) {
        Content content = new Content();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT " +
                "brand" +
                ", activity" +
                ", goal" +
                ", dateStart" +
                ", reachDescription" +
                ", rule" +
                ", image" +
                " FROM Content WHERE id = " + contentId, null);

        if ((cursor.getCount() > 0) && (cursor != null)) {
            cursor.moveToNext();
        }

        content.setBrand(cursor.getString(0));
        content.setActivity(cursor.getString(1));
        content.setGoal(cursor.getString(2));
        content.setDateStart(cursor.getString(3));
        content.setReachDescription(cursor.getString(4));
        content.setRule(cursor.getString(5));
        content.setImage(cursor.getString(6));

        return content;
    }

    public String getContentFormat(String label) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT format FROM PublisherContentType" +
                " WHERE label = '" + label + "'", null);
        int i = cursor.getCount();
        String str = null;
        if (i > 0) {
            cursor.moveToNext();
            str = cursor.getString(0);
        }
        return str;
    }

    public String getHtmlBase64(long contentItemId) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT htmlbase64 FROM ContentItem" +
                " WHERE id = '" + contentItemId + "'", null);
        int i = cursor.getCount();
        String str = null;
        if (i > 0) {
            cursor.moveToNext();
            str = cursor.getString(0);
        }
        return str;
    }

    //endregion


}