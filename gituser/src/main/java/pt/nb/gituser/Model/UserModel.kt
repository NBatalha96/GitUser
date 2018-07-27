package pt.nb.gituser.Model

import android.content.Context.MODE_PRIVATE
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import org.json.JSONObject
import pt.nb.gituser.AT_Search
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class UserModel(obj: JSONObject) {

    companion object {
        var avatar : String? = null
    }

    init {
        avatar = obj.getString("avatar_url")
        DownloadImage().execute()
    }

    var name = obj.getString("name")
    var email = obj.getString("email")
    var username = obj.getString("login")
}

internal class DownloadImage : AsyncTask<String, Unit, Unit>() {

    override fun doInBackground(vararg urls: String) {

        val url = URL(UserModel.avatar)
        val httpURLConnection = url.openConnection() as HttpURLConnection

        var img = BitmapFactory.decodeStream(httpURLConnection.inputStream)

        //Saves image in memory
        UserModel.avatar = saveToInternalStorage(img, "avatar")
    }

    private fun saveToInternalStorage(bitmapImage: Bitmap?, path: String): String {

        val cw = ContextWrapper(AT_Search.context)
        // Path
        val directory = cw.getDir("user_img", MODE_PRIVATE)
        // Create dir
        val path = File(directory, "$path.jpg")

        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(path)
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage!!.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return directory.absolutePath + "/avatar.jpg"
    }
}