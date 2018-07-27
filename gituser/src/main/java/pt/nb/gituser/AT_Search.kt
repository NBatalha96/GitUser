package pt.nb.gituser

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import org.json.JSONException
import org.json.JSONTokener
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import android.os.AsyncTask
import kotlinx.android.synthetic.main.at_search.*
import pt.nb.gituser.Model.UserModel
import android.util.Base64
import android.util.Log
import java.net.*

class AT_Search : AppCompatActivity() {

    companion object {
        lateinit var context: Context
        lateinit var usm: UserModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.at_search)

        //Set context
        context = this

        //bt_search click
        bt_search.setOnClickListener {

            //Check if there is any input
            if (et_user.text.toString().trim().length == 0) {
                //AD Construction
                var ad_builder = AlertDialog.Builder(context)
                ad_builder.setTitle("Insert a user")
                ad_builder.setMessage("I need to look for something :/")
                ad_builder.setCancelable(true)

                ad_builder.setPositiveButton("Ok") { dialog, id -> dialog.cancel() }

                ad_builder.create().show()
            } else {
                //Execute GitHub API
                SearchTask().execute(et_user.text.toString())
            }
        }
    }
}

internal class SearchTask : AsyncTask<String, Unit, Unit>() {

    private lateinit var obj: JSONObject
    private lateinit var user: String

    override fun doInBackground(vararg urls: String) {

        user = urls[0]

        try {
            var inputStream: InputStream

            var urlConnection = URL("https://api.github.com/search/users?q=" + user).openConnection() as HttpURLConnection

            //set request type
            urlConnection.setRequestMethod("GET")
            urlConnection.setDoInput(true)
            urlConnection.connect()

            //check for HTTP response
            var httpStatus = urlConnection.getResponseCode()

            //if HTTP response is 200 i.e. HTTP_OK read inputstream else read errorstream
            if (httpStatus != HttpURLConnection.HTTP_OK) {
                Log.e("HTTP ERROR", httpStatus.toString())
                inputStream = urlConnection.getInputStream()
            } else {
                inputStream = urlConnection.getInputStream()
            }

            var response = inputStream.bufferedReader().use(BufferedReader::readText)

            //GitHub API has limit to access over HTTP
            //10 req/min for unauthenticated user and 30req/min is for authenticated user
            if (response.contains("API rate limit exceeded")) {
                Log.d("LIMIT", "Exceeded")
            } else {
                //convert data string into JSONObject
                obj = JSONTokener(response).nextValue() as JSONObject
            }
            urlConnection.disconnect()

        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: ProtocolException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onPostExecute(result: Unit?) {
        super.onPostExecute(result)

        //total result count
        //Toast.makeText(AT_Search.context, obj.getString("total_count"), Toast.LENGTH_SHORT).show()

        var users = obj.getString("total_count").toInt()

        //If only 1 user is found
        if (users == 1)
            GetDetails().execute(user)
        else if (users == 0) {
            //AD Construction
            var ad_builder = AlertDialog.Builder(AT_Search.context)
            ad_builder.setTitle("No user found")
            ad_builder.setMessage("The user " + user + " was not found")
            ad_builder.setCancelable(true)

            ad_builder.setPositiveButton("Ok") { dialog, id -> dialog.cancel() }

            ad_builder.create().show()
        }
        else if (users >= 1) {
            var ad_builder = AlertDialog.Builder(AT_Search.context)
            ad_builder.setTitle("Multiple users found")
            ad_builder.setMessage("I've found " + users + " in total")
            ad_builder.setCancelable(true)

            ad_builder.setPositiveButton("Ok") { dialog, id -> dialog.cancel() }

            ad_builder.create().show()
        }
    }
}

internal class GetDetails : AsyncTask<String, Unit, Unit>() {

    private lateinit var obj: JSONObject

    override fun doInBackground(vararg urls: String) {
        var urlConnection: HttpURLConnection
        var url: URL
        var inputStream: InputStream

        try {
            url = URL("https://api.github.com/users/" + urls[0])
            urlConnection = url.openConnection() as HttpURLConnection

            //HTTP Authentication. Excuse me for having the credential showing. It's just to get the emails
            var basicAuth: String = "Basic " + Base64.encodeToString("testoauthv1:passparatestes1".toByteArray(), Base64.NO_WRAP)
            urlConnection.setRequestProperty("Authorization", basicAuth)

            //set request type
            urlConnection.setRequestMethod("GET")
            urlConnection.setDoInput(true)
            urlConnection.connect()

            //check HTTP response
            var httpStatus = urlConnection.getResponseCode()

            if (httpStatus != HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getErrorStream()
                Log.e("HTTP ERROR", httpStatus.toString())
            } else {
                inputStream = urlConnection.getInputStream()
            }

            var response = inputStream.bufferedReader().use(BufferedReader::readText)


            if (response.contains("API rate limit exceeded")) {
                Log.d("LIMIT", "Exceeded")
            } else {
                //convert data string into JSONObject
                obj = JSONTokener(response).nextValue() as JSONObject
            }

            urlConnection.disconnect()

        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: ProtocolException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onPostExecute(result: Unit?) {
        super.onPostExecute(result)

        //Data to Model
        AT_Search.usm = UserModel(obj)

        //Open AT_Details
        val intent = Intent(AT_Search.context, AT_Details::class.java)
        AT_Search.context.startActivity(intent)
    }
}