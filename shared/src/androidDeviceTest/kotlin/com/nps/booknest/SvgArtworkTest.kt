package com.nps.booknest

import androidx.test.platform.app.InstrumentationRegistry
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.svg.SvgDecoder
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class SvgArtworkTest {
    @Test
    fun homeArtworkDecoderSupportsSvg() =
        runBlocking {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val loader = ImageLoader.Builder(context).components { add(SvgDecoder.Factory()) }.build()
            try {
                val svg =
                    """
                    <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64">
                        <rect width="64" height="64" fill="green"/>
                    </svg>
                    """.trimIndent()
                val result =
                    loader.execute(
                        ImageRequest
                            .Builder(context)
                            .data(svg.encodeToByteArray())
                            .size(64)
                            .build(),
                    )
                assertTrue(result is SuccessResult)
            } finally {
                loader.shutdown()
            }
        }
}
