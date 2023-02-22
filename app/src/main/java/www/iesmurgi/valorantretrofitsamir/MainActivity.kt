package www.iesmurgi.valorantretrofitsamir


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import www.iesmurgi.valorantretrofitsamir.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var bind : ActivityMainBinding
    private lateinit var adapter: AgentsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)




        val retrofit = Retrofit.Builder()
            .baseUrl("https://valorant-api.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val valorantApiService = retrofit.create(ValorantApiService::class.java)

        val call = valorantApiService.getAgents()
        call.enqueue(object : Callback<AgentResponse> {
            override fun onResponse(
                call: Call<AgentResponse>,
                response: Response<AgentResponse>
            ) {
                if (response.isSuccessful) {
                    val agentResponse = response.body()
                    if (agentResponse != null) {
                        val agents = agentResponse.data
                        bind.rvAgentes.layoutManager = LinearLayoutManager(applicationContext)
                        adapter = AgentsAdapter(agents)
                        bind.rvAgentes.adapter = adapter
                    }
                }
            }

            override fun onFailure(call: Call<AgentResponse>, t: Throwable) {
                Log.d("VALORANT",t.toString())
            }
        })

        bind.svAgentes.setIconifiedByDefault(false)
        bind.svAgentes.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filterAgents(newText ?: "")
                return true
            }
        })

    }
}



interface ValorantApiService {
    @GET("v1/agents")
    fun getAgents(): Call<AgentResponse>
}

data class AgentResponse(val status: Int, val data: List<Agent>)

data class Agent(
    val displayName: String,
    val description: String,
    val displayIcon: String,
    val voiceLine: VoiceLine
)

data class VoiceLine(
    val minDuration: Double,
    val maxDuration: Double,
    val mediaList: List<Media>
)

data class Media(
    val id: Int,
    val wwise: String,
    val wave: String
)