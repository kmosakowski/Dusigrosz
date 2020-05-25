package com.example.dusigrosz.entity

import android.content.*
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.text.TextUtils
import com.example.dusigrosz.DatabaseHelper


class BorrowerProvider: ContentProvider() {

    private var myDB: DatabaseHelper? = null

    private val referToTable = 1
    private val referToRows = 2

    private val sURIMatcher = UriMatcher(UriMatcher.NO_MATCH)

    companion object {
        private const val AUTHORITY = "com.example.dusigrosz.entity.BorrowerProvider"
        private const val PRODUCTS_TABLE = "BORROWER"
        val CONTENT_URI : Uri = Uri.parse("content://" + AUTHORITY + "/" +
                PRODUCTS_TABLE)
    }

    init {
        sURIMatcher.addURI(AUTHORITY, PRODUCTS_TABLE, referToTable)
        sURIMatcher.addURI(AUTHORITY, "$PRODUCTS_TABLE/#",
            referToRows)
    }

    override fun onCreate(): Boolean {
        myDB = DatabaseHelper(context!!)
        return false
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val uriType = sURIMatcher.match(uri)
        val sqlDB = myDB!!.writableDatabase
        val id: Long
        when (uriType) {
            referToTable -> id = sqlDB.insert(DatabaseHelper.TABLE_NAME, null, values)
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        context!!.contentResolver.notifyChange(uri, null)
        return Uri.parse("$PRODUCTS_TABLE/$id")
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {

        val queryBuilder = SQLiteQueryBuilder()
        queryBuilder.tables = DatabaseHelper.TABLE_NAME

        when (sURIMatcher.match(uri)) {
            referToRows -> queryBuilder.appendWhere(DatabaseHelper.ID + "=" + uri.lastPathSegment)
            referToTable -> { }
            else -> throw IllegalArgumentException("Unknown URI")
        }

        val cursor = queryBuilder.query(myDB?.readableDatabase, projection, selection, selectionArgs, null, null, sortOrder)
        cursor.setNotificationUri(context!!.contentResolver, uri)
        return cursor
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        val uriType = sURIMatcher.match(uri)
        val sqlDB: SQLiteDatabase = myDB!!.writableDatabase
        val rowsUpdated: Int

        rowsUpdated = when (uriType) {
            referToTable -> sqlDB.update(DatabaseHelper.TABLE_NAME, values, selection, selectionArgs)
            referToRows -> {
                val id = uri.lastPathSegment
                if (TextUtils.isEmpty(selection)) {

                    sqlDB.update(DatabaseHelper.TABLE_NAME, values, DatabaseHelper.ID + "=" + id, null)

                } else {
                    sqlDB.update(DatabaseHelper.TABLE_NAME, values, DatabaseHelper.ID + "=" + id + " and " + selection, selectionArgs)
                }
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        context!!.contentResolver.notifyChange(uri, null)
        return rowsUpdated
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val uriType = sURIMatcher.match(uri)
        val sqlDB = myDB!!.writableDatabase
        val rowsDeleted: Int

        rowsDeleted = when (uriType) {
            referToTable -> sqlDB.delete(DatabaseHelper.TABLE_NAME,
                selection,
                selectionArgs)

            referToRows -> {
                val id = uri.lastPathSegment
                if (TextUtils.isEmpty(selection)) {
                    sqlDB.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.ID + "=" + id, null)
                } else {
                    sqlDB.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.ID + "=" + id + " and " + selection, selectionArgs)
                }
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        context!!.contentResolver.notifyChange(uri, null)
        return rowsDeleted
    }

    override fun getType(uri: Uri): String? = when (sURIMatcher.match(uri)) {
        1 -> "vnd.android.cursor.dir/vnd.com.example.dusigrosz.entity.Borrower"
        2 -> "vnd.android.cursor.item/vnd.com.example.dusigrosz.entity.Borrower"
        else -> throw IllegalArgumentException()
    }


}