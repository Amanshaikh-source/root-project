package com.example.finalpro.Utils

import com.example.finalpro.dbHelper.DbHelper

object IdUtils {

    fun generateUniqueProductId(dbHelper: DbHelper): Int {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT MAX(ID) FROM ${DbHelper.DATABASE_TABLE}", null)
        var maxId = 0

        if (cursor.moveToFirst()) {
            maxId = cursor.getInt(0) ?: 0
        }
        cursor.close()
        return maxId + 1
    }
}
