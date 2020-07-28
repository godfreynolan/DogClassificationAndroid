package com.riis.dogclassifier

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Layout
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.riis.dogclassifier.adapter.ImageItemAdapter
import com.riis.dogclassifier.model.ImageItem
import com.riis.dogclassifier.tflite.Classifier
import com.riis.dogclassifier.R
import java.io.InputStream


class MainActivity : AppCompatActivity() {
    val REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val classifier: Classifier =
            Classifier(
                assets,
                "dog_detector_model.tflite",
                "labels.txt",
                224
            )
        val imageListView:RecyclerView = findViewById(R.id.image_list_view)
        var items = getImageItems()

        val adapter: ImageItemAdapter =
            ImageItemAdapter(items, classifier)

        val staggeredGridLayoutManager = GridLayoutManager(
            applicationContext,
            2,
            GridLayoutManager.VERTICAL,
            false
        )

        val fab = findViewById<FloatingActionButton>(R.id.fab)

        fab.setOnClickListener{
            openGallery()
        }

        imageListView.layoutManager = staggeredGridLayoutManager
        imageListView.adapter = adapter
    }

    fun getImageItems(): MutableList<ImageItem>{
        val items = mutableListOf<ImageItem>()
        val fileNames = assets.list("images")
        if (fileNames != null) {
            for (name in fileNames){
                if(name.matches(""".*.bmp""".toRegex())){
                    val stream:InputStream = assets.open("images/$name")
                    val image:Bitmap = BitmapFactory.decodeStream(stream)
                    val item: ImageItem =
                        ImageItem(image)
                    items.add(item)
                }
            }
        }
        return items
    }

    fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //If the image was selected successfully
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val uri = data?.data

            val imageView = findViewById<ImageView>(R.id.uploadedImage)
            imageView.setImageURI(data?.data)

            val uploadItem = findViewById<View>(R.id.includeUpload)
            uploadItem.visibility = View.VISIBLE

            //creates an input stream from the selected image's URI
            val inputStream: InputStream? = contentResolver.openInputStream(uri!!)
            //creates a bitmap of the image
            val image: Bitmap = BitmapFactory.decodeStream(inputStream)
            val item = ImageItem(image)
            startClassifier(item)

        }
    }

    private fun startClassifier(item: ImageItem){
        val textView = findViewById<TextView>(R.id.uploadedTextView)
        //loads the tflite and label files
        val classifier = Classifier(assets, "dog_detector_model.tflite", "labels.txt", 224)
        val recognition = classifier.recognizeImage(item.image)
        //displays the dog title and confidence
        if(recognition.isNotEmpty()){
            item.confidence = recognition[0].confidence
            item.label = recognition[0].title
            textView.text = item.getTitle()
        } else {
            textView.text = getString(R.string.unknown)
        }
    }
}