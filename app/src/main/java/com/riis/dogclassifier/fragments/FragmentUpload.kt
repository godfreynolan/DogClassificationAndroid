package com.riis.dogclassifier.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.riis.dogclassifier.R
import com.riis.dogclassifier.model.ImageItem
import com.riis.dogclassifier.tflite.Classifier
import kotlinx.android.synthetic.main.fragment_upload.*
import java.io.InputStream

class FragmentUpload: Fragment() {
    companion object {
        private const val REQUEST_CODE = 100
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_upload, container, false)

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)

        fab.setOnClickListener{
            openGallery()
        }

        return view
    }

    private fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100 && resultCode == Activity.RESULT_OK){
            val uri = data?.data

            Log.i("ActivityForResult", uri.toString())
            val imageView = view!!.findViewById<ImageView>(R.id.uploadedImage)
            val directionsTextView = view!!.findViewById<TextView>(R.id.directionsTextView)
            imageView.setImageURI(data?.data)

            val uploadItem = view!!.findViewById<View>(R.id.includeUpload)
            directionsTextView.visibility = View.INVISIBLE
            uploadItem.visibility = View.VISIBLE

            //creates an input stream from the selected image's URI
            val inputStream: InputStream? = context!!.contentResolver.openInputStream(uri!!)
            //creates a bitmap of the image
            val image: Bitmap = BitmapFactory.decodeStream(inputStream)
            val item = ImageItem(image)
            startClassifier(item)

        }

    }

    private fun startClassifier(item: ImageItem){
        val textView = view?.findViewById<TextView>(R.id.uploadedTextView)
        //loads the tflite and label files
        val classifier = Classifier(activity!!.assets, "dog_detector_model.tflite", "labels.txt", 224)
        val recognition = classifier.recognizeImage(item.image)
        //displays the dog title and confidence
        if(recognition.isNotEmpty()){
            item.confidence = recognition[0].confidence
            item.label = recognition[0].title
            textView!!.text = item.getTitle()
        } else {
            textView!!.text = getString(R.string.unknown)
        }
    }
}