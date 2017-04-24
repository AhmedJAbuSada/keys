package com.keys.DatabaseSQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.keys.Hraj.Model.Cities;
import com.keys.Hraj.Model.Departments;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 6;

    private static final String DATABASE_NAME = "keys";

    private static final String TABLE_DEPARTMENTS = "Departments";
    private static final String TABLE_CITIES = "Cities";

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_IS_ACTIVE = "Active";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        String CREATE_DAY_TABLE = "CREATE TABLE " + TABLE_DEPARTMENTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_IS_ACTIVE + " INTEGER" + ");";
        Log.i("table", CREATE_DAY_TABLE);

        String CREATE_DAY_TABLE_CITY = "CREATE TABLE " + TABLE_CITIES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_IS_ACTIVE + " INTEGER" + ");";
        Log.i("table", CREATE_DAY_TABLE_CITY);

        db.execSQL(CREATE_DAY_TABLE);
        db.execSQL(CREATE_DAY_TABLE_CITY);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEPARTMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITIES);
        onCreate(db);
    }

    public void addDepartments(Departments departments) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DEPARTMENTS, KEY_ID + "=" + departments.getId(), null);
        ContentValues values = new ContentValues();
        values.put(KEY_ID, departments.getId());
        values.put(KEY_NAME, departments.getName());
        values.put(KEY_IS_ACTIVE, departments.getIsActive());
        db.insert(TABLE_DEPARTMENTS, null, values);
        db.close();
    }

    public void addCities(Cities cities) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CITIES, KEY_ID + "=" + cities.getId(), null);
        ContentValues values = new ContentValues();
        values.put(KEY_ID, cities.getId());
        values.put(KEY_NAME, cities.getName());
        values.put(KEY_IS_ACTIVE, cities.getIsActive());
        db.insert(TABLE_CITIES, null, values);
        db.close();
    }

    public Departments getDepartments(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DEPARTMENTS, new String[]{KEY_ID,
                        KEY_NAME, KEY_IS_ACTIVE}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        return new Departments(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), Integer.parseInt(cursor.getString(2)));
    }

    public List<Departments> getAllDepartments() {
        List<Departments> departmentsList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_DEPARTMENTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Departments departments = new Departments();
                departments.setId(Integer.parseInt(cursor.getString(0)));
                departments.setName(cursor.getString(1));
                departments.setIsActive(Integer.parseInt(cursor.getString(2)));
                departmentsList.add(departments);
            } while (cursor.moveToNext());
        }
        return departmentsList;
    }

    public List<Cities> getAllCities() {
        List<Cities> citiesList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CITIES;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Cities cities = new Cities();
                cities.setId(Integer.parseInt(cursor.getString(0)));
                cities.setName(cursor.getString(1));
                cities.setIsActive(Integer.parseInt(cursor.getString(2)));
                citiesList.add(cities);
            } while (cursor.moveToNext());
        }
        return citiesList;
    }

    public int getDepartmentsCount() {
        String countQuery = "SELECT * FROM " + TABLE_DEPARTMENTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        return cursor.getCount();
    }

    public int updateDepartments(Departments departments) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, departments.getName());
        values.put(KEY_IS_ACTIVE, departments.getIsActive());
        return db.update(TABLE_DEPARTMENTS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(departments.getId())});
    }

    public void deleteDepartments(Departments departments) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DEPARTMENTS, KEY_ID + " = ?",
                new String[]{String.valueOf(departments.getId())});
        db.close();
    }
}