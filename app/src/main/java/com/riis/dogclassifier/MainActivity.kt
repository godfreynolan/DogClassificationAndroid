package com.riis.dogclassifier

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.riis.dogclassifier.adapter.ImageItemAdapter
import com.riis.dogclassifier.model.ImageItem
import com.riis.dogclassifier.tflite.Classifier
import com.riis.dogclassifier.R
import java.io.InputStream


class MainActivity : AppCompatActivity() {
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
        val items = getImageItems()

        val adapter: ImageItemAdapter =
            ImageItemAdapter(items, classifier)

        val staggeredGridLayoutManager = GridLayoutManager(
            applicationContext,
            2,
            GridLayoutManager.VERTICAL,
            false
        )

        imageListView.layoutManager = staggeredGridLayoutManager
        imageListView.adapter = adapter
    }

    fun getImageItems():List<ImageItem>{
        val items = ArrayList<ImageItem>()
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
}