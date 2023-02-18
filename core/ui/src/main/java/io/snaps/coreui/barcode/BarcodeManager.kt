package io.snaps.coreui.barcode

import android.content.Context
import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.ceil

@Singleton
class BarcodeManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun getQrCodeBitmap(text: String, size: Float = 150F): Bitmap? {
        val multiFormatWriter = MultiFormatWriter()
        return try {
            val imageSize = dp(size, context)
            val bitMatrix = multiFormatWriter.encode(
                /* contents = */ text,
                /* format = */ BarcodeFormat.QR_CODE,
                /* width = */ imageSize,
                /* height = */ imageSize,
                /* hints = */ hashMapOf(EncodeHintType.MARGIN to 0),
            )
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)
            bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }

    fun dp(dp: Float, context: Context?) = context?.let {
        val density = context.resources.displayMetrics.density
        if (dp == 0f) 0 else ceil((density * dp).toDouble()).toInt()
    } ?: dp.toInt()
}