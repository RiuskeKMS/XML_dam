package ni.est.xml

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import ni.est.xml.databinding.ActivityMainBinding
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.xml.sax.InputSource
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.InputStream
import java.io.StringReader
import java.lang.reflect.Method
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.jvm.Throws

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var text: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonLoad.setOnClickListener {
            //Manda a request usando la url ingresada
            Toast.makeText(this, "Requesting...", Toast.LENGTH_SHORT).show()
            cargarDatosDesdeUrl()
        }


    }

    private fun cargarDatosDesdeUrl() {
        //Se crea una una solicitud usando Volley
        val url = "http://192.168.1.18:8080/files/sample.xml"
        val queue = Volley.newRequestQueue(this)
        Toast.makeText(this, "Queue Requested...", Toast.LENGTH_SHORT).show()

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    try {
                        var data = ""
                        val factory = XmlPullParserFactory.newInstance()
                        factory.isNamespaceAware = true
                        val parser = factory.newPullParser()
                        parser.setInput(response.reader())
                        var eventType = parser.eventType
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            val tagname = parser.name
                            when (eventType) {
                                XmlPullParser.TEXT -> text = parser.text
                                XmlPullParser.END_TAG -> if (tagname.equals("fruit", ignoreCase = true)) {
                                    data += "Fruit: \n"
                                } else if (tagname.equals("name", ignoreCase = true)) {
                                    data += "Name: $text\n"
                                } else if (tagname.equals("color", ignoreCase = true)) {
                                    data += "Color: $text\n"
                                } else if (tagname.equals("taste", ignoreCase = true)) {
                                    data += "Taste: $text\n"
                                }
                                else -> {
                                }
                            }
                            eventType = parser.next()
                        }
                        binding.textViewData.text = data

                    } catch (e: XmlPullParserException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    Toast.makeText(this, "Done...", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error en la solicitud...", Toast.LENGTH_LONG).show()
                }
            }) { error -> println(error.message) }

        queue.add(stringRequest)
    }

}
