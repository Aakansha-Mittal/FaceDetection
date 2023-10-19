package com.example.facedetection

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnCamera = findViewById<Button>(R.id.btnCamera)

        btnCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if(intent.resolveActivity(packageManager)!=null) {

                startActivityForResult(intent, 123)
                Toast.makeText(this, "Image captured", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "Camera not responding", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==123 && resultCode== RESULT_OK){
            val extras = data?.extras
            val bitmap = extras?.get("data") as? Bitmap

            if (bitmap != null) {
                faceDetect(bitmap)
            } else {
                Toast.makeText(this@MainActivity, "Bitmap is null", Toast.LENGTH_SHORT).show()
            }

        } else {
            Toast.makeText(this@MainActivity, "Image is not recieved", Toast.LENGTH_SHORT).show()
        }
    }

    private fun faceDetect( bitmap : Bitmap){
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()

        val detector = FaceDetection.getClient(options)

        val image = InputImage.fromBitmap(bitmap, 0)

        val tvResult = findViewById<TextView>(R.id.tvResult)

        val result = detector.process(image)
            .addOnSuccessListener { faces ->
                var resultText = ""
                var i = 1
                for (face in faces){
                    resultText = resultText + "\nFace Number : $i" +
                            "\nSmile : ${face.smilingProbability?.times(100)}%" +
                            "\nLeft Eye open : ${face.leftEyeOpenProbability?.times(100)}%" +
                            "\nRight Eye open : ${face.rightEyeOpenProbability?.times(100)}%\n"
                    i++
                }
                    tvResult.text = resultText

            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                // ...
                Toast.makeText(this@MainActivity, "Model not able to work", Toast.LENGTH_SHORT).show()
            }
    }
}