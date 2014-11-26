package com.saiflimited.xpresenter.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.saiflimited.xpresenter.Models.Content;
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

    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static factory method "getInstance()" instead.
     */
    private DatabaseHandler(Context ctx) {
        super(ctx, "IPREMIOS", null, DATABASE_VERSION);
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
        long l = database.insert("Content", null, values);
        database.close();
        return l;
    }

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

    public void deleteContent() {
        SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
        localSQLiteDatabase.delete("Content", null, null);
        localSQLiteDatabase.rawQuery("DELETE FROM Content", null);
        localSQLiteDatabase.close();
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

    public Cursor getAllContentId() {
        Cursor localCursor = getReadableDatabase().rawQuery("SELECT id FROM Content", null);
        if (localCursor.getCount() > 0) {
            localCursor.moveToNext();
            return localCursor;
        }
        return null;
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

    public int getContentCount() {
        Cursor localCursor = getReadableDatabase().rawQuery("SELECT * from Content", null);
        if (localCursor != null) {
            return localCursor.getCount();
        }
        return 0;
    }

    public String getContentDoc(int paramInt) {
        //TODO change to id
        Cursor localCursor = getReadableDatabase().rawQuery("SELECT contentData FROM Content WHERE id = 1"/* + paramInt*/, null);
        if ((localCursor.getCount() > 0) && (localCursor != null)) {
            localCursor.moveToNext();
        }
        for (String str = localCursor.getString(0); ; str = null) {
            localCursor.close();
            return str;
        }
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

    public void onCreate(SQLiteDatabase paramSQLiteDatabase) {
        paramSQLiteDatabase.execSQL("CREATE TABLE Publisher(   " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", code TEXT NOT NULL" +
                ", name TEXT NOT NULL" +
                ", logo TEXT NOT NULL" +
                ", background TEXT NOT NULL" +
                ", appname TEXT NOT NULL" +
                ", messages TEXT" +
                ", lastUpdated DATE NOT NULL )");
        paramSQLiteDatabase.execSQL("CREATE TABLE PublisherContentType( " +
                "_id INTEGER PRIMARY KEY  AUTOINCREMENT" +
                ", publisher_code TEXT NOT NULL" +
                ", sequence TEXT NOT NULL" +
                ", code TEXT NOT NULL" +
                ", label TEXT NOT NULL" +
                ", format TEXT NOT NULL" +
                ", FOREIGN KEY(publisher_code) REFERENCES Publisher (code) )");
        paramSQLiteDatabase.execSQL("CREATE TABLE User(  " +
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
        paramSQLiteDatabase.execSQL("CREATE TABLE Content(  " +
                "id INTEGER " +
                ", name TEXT NOT NULL" +
                ", [from] TEXT NOT NULL" +
                ", format TEXT NOT NULL" +
                ", type TEXT NOT NULL" +
                ", fromDate DATE NOT NULL" +
                ", toDate DATE NOT NULL" +
                ", lastUpdated DATE NOT NULL" +
                ", contentData BLOB )");
        paramSQLiteDatabase.execSQL("CREATE TABLE UserFirstAccess(  " +
                "username TEXT PRIMARY KEY" +
                ", pin TEXT NOT NULL )");
    }

    public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2) {
        paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS Publisher");
        paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS PublisherContentType");
        paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS User");
        paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS Content");
        paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS UserFirstAccess");
        onCreate(paramSQLiteDatabase);
    }

    /*
    public boolean publisherExists(String paramString)
    {
      Cursor localCursor = getReadableDatabase().rawQuery("SELECT code from Publisher WHERE code = '" + paramString + "'", null);
      boolean bool = false;
      if (localCursor != null)
      {
        int i = localCursor.getCount();
        bool = false;
        if (i > 0) {
          bool = true;
        }
      }
      return bool;
    }
*/
    public void updateContentData(String contentId, String contentData) {
        SQLiteDatabase localSQLiteDatabase = getReadableDatabase();
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("contentData", contentData);
        localSQLiteDatabase.update("Content", localContentValues, "id = ?", new String[]{contentId});
    }

    public boolean updatePIN(String username, String PIN) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("pin", PIN);
        return readableDatabase.update("User", values, "username = ?", new String[]{username}) == 1;
    }

    public void updatePublisher(Publisher paramPublisher) {
        Calendar localCalendar = Calendar.getInstance();
        SQLiteDatabase localSQLiteDatabase = getReadableDatabase();
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("lastUpdated", this.dtFormatterForDB.format(localCalendar.getTime()));
        String[] arrayOfString = new String[1];
        arrayOfString[0] = paramPublisher.getCode();
        localSQLiteDatabase.update("Publisher", localContentValues, "code = ?", arrayOfString);
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
}