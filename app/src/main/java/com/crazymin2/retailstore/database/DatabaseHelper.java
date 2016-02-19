package com.crazymin2.retailstore.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.crazymin2.retailstore.ApplicationController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.crazymin2.retailstore.util.LogUtils.LOGD;
import static com.crazymin2.retailstore.util.LogUtils.makeLogTag;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = makeLogTag(DatabaseHelper.class);

    /**
     * Database Version
     */
    private static final int DB_VERSION = 1;
    private String databasePath;
    private SQLiteDatabase mDataBase;
    private Context mContext;

    /**
     * Database Name
     */
    private static final String DB_NAME = "retail_store";

    /**
     * Constructor Takes and keeps a reference of the passed context in order to access to the
     * application assets and resources.
     *
     * @param context
     */
    public DatabaseHelper(Context context) {

        this(context, DB_NAME, DB_VERSION);
    }

    /**
     * Constructor of class. Database Helper class for creating database.
     *
     * @param context
     * @param DB_NAME
     * @param version
     */
    private DatabaseHelper(Context context, String DB_NAME, int version) {
        super(context, DB_NAME, null, version);

        this.databasePath = ApplicationController.getInstance().getFilesDir().getParentFile().getPath() + "/databases/";

        this.mContext = context;

        try {

            LOGD(TAG, "Initializing database..." + DB_NAME);
            createDataBase();
        } catch (IOException e) {
            LOGD(TAG, "exception while creating the database", e);
        }
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     */
    private void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        if (dbExist) {
            LOGD(TAG, "Database already created... turning to readable");
            this.getReadableDatabase();
            close();
            return;
        }
        LOGD(TAG, "Creating fresh database...");
        this.getReadableDatabase();

        try {
            copyDataBase();
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * check database if it exists then open database in readable mode
     */
    public void checkUpgradeDatabase() {
        boolean dbExist = checkDataBase();
        if (dbExist) {
            this.getReadableDatabase();
            return;
        }
    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the
     * application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = databasePath + DB_NAME;
            File file = new File(myPath);
            if (file.exists()) {
                checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
            } else {
                LOGD(TAG, "Database doesn't exist yet");
            }

        } catch (SQLiteException e) {
            Log.e(TAG, "database doesn't exist yet");
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled. this is achieved by transferring
     * byte stream.
     */
    private void copyDataBase() throws IOException {

        // Open your local db as the input stream
        InputStream myInput = mContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = databasePath + DB_NAME;

        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        // transfer bytes from the input file to the output file
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    /**
     * open database
     */
    public SQLiteDatabase openDataBase() throws SQLException {
        // Open the database
        String myPath = databasePath + DB_NAME;
        mDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        return mDataBase;
    }

    @Override
    public synchronized void close() {
        if (mDataBase != null)
            mDataBase.close();
        super.close();
    }

    /**
     * onCreate method is called for the 1st time when database doesn't exists.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    /**
     * This method is called when database version changes.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Create tables again
        onCreate(db);
        LOGD(TAG, "Upgrading started...older version = " + oldVersion + " and new version = " + newVersion);
        
    }
}
