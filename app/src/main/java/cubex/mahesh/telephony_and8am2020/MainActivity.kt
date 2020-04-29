package cubex.mahesh.telephony_and8am2020

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.telephony.SmsManager
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity() {
        var sms_permission = false
        var call_permission = false
        var _uri:Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var sp_status = ContextCompat.checkSelfPermission(
            this,Manifest.permission.SEND_SMS)
        var cp_status = ContextCompat.checkSelfPermission(
            this,Manifest.permission.CALL_PHONE)
        if(sp_status == PackageManager.PERMISSION_GRANTED){
            sms_permission = true
        }
        if(cp_status == PackageManager.PERMISSION_GRANTED){
            call_permission = true
        }

        if(!sms_permission || !call_permission){
            if(!sms_permission && !call_permission){
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.SEND_SMS,
         Manifest.permission.CALL_PHONE),100)
            }else if(!sms_permission){
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.SEND_SMS),
                    101)
            }else if(!call_permission){
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    102)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            100 -> {
                    if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
                        sms_permission = true
                if(grantResults[1]==PackageManager.PERMISSION_GRANTED)
                    call_permission = true
            }
            101 ->{
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
                    sms_permission = true
            }
            102->{
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
                    call_permission = true
            }
        }

    }

    fun sendSMS(view: View) {
        /* String destinationAddress, String scAddress, String text,
            PendingIntent sentIntent, PendingIntent deliveryIntent */
        if(sms_permission) {
            var sIntent = Intent(
                this,
                SentActivity::class.java
            )
            var dIntent = Intent(
                this,
                DeliverActivity::class.java
            )
            var spIntent = PendingIntent.getActivity(
                this,
                0, sIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            var dpIntent = PendingIntent.getActivity(
                this,
                0, dIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            var sManager = SmsManager.getDefault()
            sManager.sendTextMessage(
                mobileno.text.toString(),
                null, message.text.toString(),
                spIntent, dpIntent
            )
        }else{
            Toast.makeText(this,
                "Please provide SMS Permission ",
                Toast.LENGTH_LONG
                ).show()
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.SEND_SMS),
                101)
        }
    }
    fun call(view: View) {
        if(call_permission) {
            var i = Intent()
            i.setAction(Intent.ACTION_CALL)
            i.setData(Uri.parse("tel:" + mobileno.text.toString()))
            startActivity(i)
        }else{
            Toast.makeText(this,
                "Please provide Call Permission ",
                Toast.LENGTH_LONG
            ).show()
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CALL_PHONE),
                102)
        }
    }

    fun sendMail(view: View) {
            var i = Intent()
            i.setAction(Intent.ACTION_SEND)
            i.putExtra(Intent.EXTRA_EMAIL,
                 arrayOf(to.text.toString()))
            i.putExtra(Intent.EXTRA_SUBJECT,
                subject.text.toString())
            i.putExtra(Intent.EXTRA_TEXT,
                email_text.text.toString())
            i.putExtra(Intent.EXTRA_STREAM,_uri)
            i.setType("message/rfc822")
            startActivity(i)
    }
    fun attachment(view: View) {

        var cpstatus = ContextCompat.checkSelfPermission(this,
            Manifest.permission.CAMERA)
        var spstatus = ContextCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if(cpstatus != PackageManager.PERMISSION_GRANTED ||
                spstatus != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE),
                103)
        }

        var aDialog = AlertDialog.Builder(this)
        aDialog.setTitle("Message")
        aDialog.setMessage("Please selection option")
        aDialog.setPositiveButton("Camera") {
                dialog, which ->
                var i = Intent()
                i.setAction("android.media.action.IMAGE_CAPTURE")
                startActivityForResult(i,10)
        }
        aDialog.setNegativeButton("Files"){
            dialog, which ->
            var i = Intent()
            i.setAction(Intent.ACTION_GET_CONTENT)
            i.setType("*/*")
            startActivityForResult(i,11)
        }
        aDialog.show()
    }

    override fun onActivityResult(requestCode: Int,
                                  resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 10){
            if(resultCode == Activity.RESULT_OK){
                var bmp:Bitmap = data?.extras?.get("data") as Bitmap
                _uri = getImageUri(this,
                    bmp)
            }
        }else if(requestCode == 11){
            if(resultCode == Activity.RESULT_OK){
                    _uri = data?.data
            }
        }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.getContentResolver(),
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    fun javaMail(view: View) {
        var lop = LongOperation(to.text.toString(),
            subject.text.toString(),
            email_text.text.toString())
        lop.execute()
    }
}
