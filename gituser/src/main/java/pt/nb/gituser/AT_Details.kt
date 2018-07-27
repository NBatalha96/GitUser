package pt.nb.gituser

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.at_details.*
import java.io.File
import android.support.v4.content.ContextCompat

class AT_Details : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.at_details)

        toolbar.setTitle(AT_Search.usm.username);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white))
        setSupportActionBar(toolbar)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)

        setUpDetails()
    }

    fun setUpDetails() {
        tv_name.setText(AT_Search.usm.name)
        tv_email.setText(AT_Search.usm.email)

//        val imgFile = File(UserModel.avatar + "/name.jpg")
        val imgFile = File("data/data/pt.nb.gituser/app_user_img" + "/avatar.jpg")

        if (imgFile.exists()) {

            val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
            iv_avatar.setImageBitmap(myBitmap)
        }
    }

    //Click to return
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}