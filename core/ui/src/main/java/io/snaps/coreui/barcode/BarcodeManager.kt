package io.snaps.coreui.barcode

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
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
            val imageSize = dp(size)
            val bitMatrix = multiFormatWriter.encode(
                /* contents = */ text,
                /* format = */ BarcodeFormat.QR_CODE,
                /* width = */ imageSize,
                /* height = */ imageSize,
                /* hints = */ hashMapOf(EncodeHintType.MARGIN to 0),
            )
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)
            getRoundedCornerBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }

    private fun dp(dp: Float): Int {
        val density = context.resources.displayMetrics.density
        return if (dp == 0f) 0 else ceil((density * dp).toDouble()).toInt()
    }

    private fun getRoundedCornerBitmap(bitmap: Bitmap): Bitmap? {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        Canvas(output).apply {
            val padding = dp(32f)
            val radius = dp(32f).toFloat()
            val width = bitmap.width
            val height = bitmap.height
            val paint = Paint().apply {
                isAntiAlias = true
                color = Color.WHITE
            }
            val rectF = RectF(
                /* left = */ 0f,
                /* top = */ width.toFloat(),
                /* right = */ height.toFloat(),
                /* bottom = */ 0f,
            )
            drawRoundRect(rectF, radius, radius, paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            drawBitmap(
                Bitmap.createScaledBitmap(bitmap, width - padding * 2, height - padding * 2, false),
                padding.toFloat(),
                padding.toFloat(),
                null
            )
        }
        return output
    }
}