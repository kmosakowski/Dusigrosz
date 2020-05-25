package com.example.dusigrosz

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.dusigrosz.entity.Borrower
import com.example.dusigrosz.entity.BorrowerProvider


class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    private val myCR: ContentResolver = context.contentResolver

    companion object {
        const val DATABASE_NAME = "borrower.db";
        const val TABLE_NAME = "BORROWER";
        const val ID = "ID";
        const val NAME = "NAME";
        const val SURNAME = "SURNAME";
        const val AMOUNT = "AMOUNT";
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE $TABLE_NAME (" +
                "$ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$NAME TEXT," +
                "$SURNAME TEXT," +
                "$AMOUNT REAL" +
                ")"
        db?.execSQL(createTable);
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME");
        onCreate(db);
    }

    fun getBorrowers(): ArrayList<Borrower> {
        val projection = arrayOf(ID, NAME, SURNAME, AMOUNT)

        val cursor = myCR.query(BorrowerProvider.CONTENT_URI, projection, "", null, null)

        val borrowerList = ArrayList<Borrower>()

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getString(cursor.getColumnIndex(ID))
                    val name = cursor.getString(cursor.getColumnIndex(NAME))
                    val surname = cursor.getString(cursor.getColumnIndex(SURNAME))
                    val amount = cursor.getDouble(cursor.getColumnIndex(AMOUNT))
                    val borrower = Borrower(id.toInt(), name, surname, amount)
                    borrowerList.add(borrower)
                } while (cursor.moveToNext())
            }
        }
        return borrowerList
    }

    fun insert(borrower: Borrower): Boolean {
        val values = ContentValues()
        values.put(NAME, borrower.name)
        values.put(SURNAME, borrower.surname)
        values.put(AMOUNT, borrower.amount)
        myCR.insert(BorrowerProvider.CONTENT_URI, values)
        return true
    }

    fun update(borrower: Borrower): Boolean {
        val values = ContentValues()
        values.put(NAME, borrower.name)
        values.put(SURNAME, borrower.surname)
        values.put(AMOUNT, borrower.amount)

        val selection = "ID = ?"

        return myCR.update(BorrowerProvider.CONTENT_URI, values, selection, arrayOf(borrower.id.toString())) != 0

//        val db = this.writableDatabase
//        val result = db.update(TABLE_NAME, values, "$ID = ?", arrayOf(borrower.id.toString()))
//        db.close()
//        return result != -1
    }

    fun delete(borrowerId: Number): Boolean {
        val selection = "ID = $borrowerId"
        return myCR.delete(BorrowerProvider.CONTENT_URI, selection, null) > 0

    }

}