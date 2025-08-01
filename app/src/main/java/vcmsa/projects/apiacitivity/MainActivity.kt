package vcmsa.projects.apiacitivity

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    // View variables
    private lateinit var tvJoke: TextView
    private lateinit var ivMeme: ImageView
    private lateinit var btnGetJoke: Button
    private lateinit var btnGetMeme: Button

    private val client = OkHttpClient()
    private val jokeCategories = arrayOf(
        "Dark", "Pun"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        tvJoke = findViewById(R.id.tvJoke)
        ivMeme = findViewById(R.id.ivMeme)
        btnGetJoke = findViewById(R.id.btnGetJoke)
        btnGetMeme = findViewById(R.id.btnGetMeme)

        // Set click listeners
        btnGetJoke.setOnClickListener { getRandomJoke() }
        btnGetMeme.setOnClickListener { getMeme() }
    }

    private fun getRandomJoke() {
        val randomCategory = jokeCategories.random()
        val request = Request.Builder()
            .url("https://v2.jokeapi.dev/joke/$randomCategory?blacklistFlags=nsfw,racist")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                showToast("Joke failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val json = JSONObject(response.body?.string() ?: "")
                    val jokeText = when (json.getString("type")) {
                        "single" -> json.getString("joke")
                        else -> "${json.getString("setup")}\n\n${json.getString("delivery")}"
                    }
                    updateJokeText("$jokeText")
                } catch (e: Exception) {
                    showToast("Error parsing joke")
                }
            }
        })
    }

    private fun getMeme() {
        val request = Request.Builder()
            .url("https://api.imgflip.com/get_memes")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                showToast("Meme failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val memes = JSONObject(response.body?.string() ?: "")
                        .getJSONObject("data").getJSONArray("memes")
                    val randomMeme = memes.getJSONObject((0 until memes.length()).random())
                    loadMemeImage(randomMeme.getString("url"))
                } catch (e: Exception) {
                    showToast("Error parsing meme")
                }
            }
        })
    }

    private fun updateJokeText(text: String) {
        runOnUiThread { tvJoke.text = text }
    }

    private fun loadMemeImage(url: String) {
        runOnUiThread { Glide.with(this).load(url).into(ivMeme) }
    }

    private fun showToast(message: String) {
        runOnUiThread { Toast.makeText(this, message, Toast.LENGTH_SHORT).show() }
    }
}