package com.riis.dogdetector.tflite

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.test.platform.app.InstrumentationRegistry
import com.riis.dogclassifier.tflite.Classifier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import java.io.InputStream


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class ClassifierTest {


    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    private val testContext = InstrumentationRegistry.getInstrumentation().context


    @Test
    fun testClassifyImage(){
        val classifier = Classifier(
            appContext.assets,
            "model.tflite",
            "labels.txt",
            224
        )
        val testImageStream: InputStream = this.testContext.assets.open("drawable/yorkshire_terrier.bmp")
        val testImage:Bitmap = BitmapFactory.decodeStream(testImageStream)
        val recognitions = classifier.recognizeImage(testImage)
        assertNotEquals(0, recognitions.size)
        assertEquals(recognitions[0].title, "yorkshire terrier")
    }

}
