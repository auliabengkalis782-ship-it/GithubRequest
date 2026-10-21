package com.example.githubrequest


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.githubrequest.databinding.ActivityMainBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val client = OkHttpClient()
    private val request = OkHttpRequest(client)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGetInfo.setOnClickListener {
            val loginID = binding.tvSearch.text.toString()
            if(loginID.isEmpty()){
                Toast.makeText(applicationContext, "Silah masukan login id akun github Anda!", Toast.LENGTH_SHORT).show()
            }else{
                val url = "https://api.github.com/users/$loginID"
                fetchGitHubInfo(url)
            }
        }
    }

    private fun fetchGitHubInfo(strURL: String) {
        request.GET(strURL, object: Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                runOnUiThread {
                    if (response.isSuccessful && !responseData.isNullOrEmpty()) {
                        try {
                            val userObject = JSONObject(responseData)
                            val id = userObject.optString("id", "-")
                            val name = userObject.optString("name", "-")
                            val url = userObject.optString("html_url", userObject.optString("url", "-"))
                            val blog = userObject.optString("blog", "-")
                            val bio = userObject.optString("bio", "-")
                            val company = userObject.optString("company", "-")
                            binding.tvUserInfo.text =
                                "ID: $id\nName: $name\nURL: $url\nBlog: $blog\nBio: $bio\nCompany: $company"
                        } catch (e: JSONException) {
                            Toast.makeText(applicationContext, "Gagal memproses data!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(applicationContext, "User GitHub tidak ditemukan!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(applicationContext, "Koneksi gagal: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        binding.tvSearch.setText("")
        binding.tvSearch.hint = "Enter GitHub username"
    }

}
