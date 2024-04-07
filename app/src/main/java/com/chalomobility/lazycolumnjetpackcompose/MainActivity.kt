package com.chalomobility.lazycolumnjetpackcompose

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily.Companion.Cursive
import androidx.compose.ui.text.font.FontFamily.Companion.SansSerif
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.chalomobility.lazycolumnjetpackcompose.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import toast

// @AndroidEntryPoint generates an individual Hilt component for each Android class in your project.
// These components can receive dependencies from their respective parent classes as described in Component hierarchy.

//To obtain dependencies from a component, use the "@Inject" annotation to perform field injection:
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val newsViewModel by viewModels<NewsViewModel>()

    /*
        Even though the view model has an @Inject constructor, it is an error to request it from
        Dagger directly (for example, via field injection) since that would result in multiple
        instances. View Models must be retrieved through the ViewModelProvider API or viewModels<>
        This is checked at compile time by Hilt.
     */

    //@Inject
    //lateinit var catsViewModel: CatsViewModel

    private var isInternetAvailable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (checkForInternet()){
            //catsViewModel.getAllCatsData(limit = 10)
            newsViewModel.getNewsData()
            isInternetAvailable = true
        }
        else{
            toast(message = "No Internet Connection!")
        }

        // code if we want to use compose with views
        /*lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                newsViewModel.uiState.collect {
                    // Update UI elements
                    Log.d(TAG, "onCreate: ******* ${it.newsItems}", )
                }
            }
        }*/

        binding.composeView.let {
            it.setViewCompositionStrategy(strategy = ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            it.setContent {
                LatestNewsScreen()
            }
        }
    }

    private fun checkForInternet(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }

    @Composable
    fun LatestNewsScreen(viewModel: NewsViewModel = viewModel()) {
        // Show UI elements based on the viewModel.uiState
        val uriHandler = LocalUriHandler.current

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Text(
                text = "Latest News",
                fontSize = 35.sp,
                fontFamily = Cursive,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight(600),
                modifier = Modifier.padding(top = 15.dp)
            )

            if (viewModel.uiState2.isLoading && isInternetAvailable) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = Color.LightGray,
                        strokeWidth = 5.dp,
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
            ) {
                items(viewModel.uiState2.newsItems) { news: NewsData.Articles ->
                    NewsCard(newsData = news) { url ->
                        Log.d(TAG, "LatestNewsScreen: clicked url = ${url}")
                        uriHandler.openUri(Uri.parse(url).toString())
                    }
                }
            }
        }
    }

    @Composable
    fun NewsCard(newsData: NewsData.Articles, onItemClick:(String) -> Unit) {
        Card(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(corner = CornerSize(16.dp)),
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(newsData.urlToImage?:"https://static.thenounproject.com/png/1269202-200.png")
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = "",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(80.dp)
                        .clip(RoundedCornerShape(corner = CornerSize(16.dp)))
                )
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = newsData.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight(500),
                        fontFamily = SansSerif,
                        modifier = Modifier
                            .padding(top = 15.dp)
                            .clickable { onItemClick.invoke(newsData.url) }
                    )

                    Text(
                        text = "By - ${newsData.author}"?:"N/A",
                        fontSize = 12.sp,
                        fontWeight = FontWeight(400),
                        fontFamily = SansSerif,
                        textAlign = TextAlign.Right,
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .align(Alignment.Start)
                    )

                    Text(
                        text = "Source - ${newsData.source.name}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight(400),
                        fontFamily = SansSerif,
                        textAlign = TextAlign.Right,
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .align(Alignment.Start)
                    )

                    Text(
                        text = newsData.publishedAt,
                        fontSize = 12.sp,
                        fontWeight = FontWeight(400),
                        fontFamily = SansSerif,
                        textAlign = TextAlign.Right,
                        modifier = Modifier
                            .padding(top = 5.dp, end = 7.dp, bottom = 5.dp)
                            .align(Alignment.End)
                    )
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun NewsCardPreview() {
        NewsCard(
            NewsData.Articles(
                title = "Is this what an early-stage slowdown looks like?",
                publishedAt = "2020-02-10T17:06:42Z",
                author = "Alex Wilhelm",
                url = "http://techcrunch.com/2020/02/10/is-this-what-an-early-stage-slowdown-looks-like/",
                source = NewsData.Articles.Source(id = "techcrunch", name = "TechCrunch"),
                description = "",
                urlToImage = "https://cdn2.thecatapi.com/images/3tb.jpg",
                content = ""
            ),
        ){

        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
