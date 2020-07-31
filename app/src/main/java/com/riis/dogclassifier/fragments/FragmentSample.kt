package com.riis.dogclassifier.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.riis.dogclassifier.R
import com.riis.dogclassifier.adapter.ImageItemAdapter
import com.riis.dogclassifier.model.ImageItem
import com.riis.dogclassifier.tflite.Classifier
import java.io.InputStream

class FragmentSample: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sample, container, false)
        val classifier = Classifier(context!!.assets, "dog_detector_model.tflite", "labels.txt", 224)
        val imageListView: RecyclerView = view.findViewById(R.id.image_list_view)
        val items = getImageItems()

        val adapter = ImageItemAdapter(items, classifier)

        val staggeredGridLayoutManager = GridLayoutManager(
            activity?.applicationContext,
            2,
            GridLayoutManager.VERTICAL,
            false
        )

        imageListView.layoutManager = staggeredGridLayoutManager
        imageListView.adapter = adapter

        return view
    }

    private fun getImageItems(): MutableList<ImageItem>{
        val items = mutableListOf<ImageItem>()
        val fileNames = context!!.assets.list("images")
        if (fileNames != null) {
            for (name in fileNames){
                if(name.matches(""".*.bmp""".toRegex())){
                    val stream: InputStream = context!!.assets.open("images/$name")
                    val image: Bitmap = BitmapFactory.decodeStream(stream)
                    val item = ImageItem(image)
                    items.add(item)
                }
            }
        }
        return items
    }
}