package com.example.mycatapi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.W900
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mycatapi.network.BaseURL
import com.example.mycatapi.network.model.CatCatalog
import com.example.mycatapi.ui.theme.MyCatAPITheme
import com.example.mycatapi.ui.theme.mythemesetting.MyTheme
import com.example.mycatapi.ui.theme.mythemesetting.ThemeSettings
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyTheme {
                MyScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyScreen() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val showAlertDialog = remember { mutableStateOf(false) }
    val baseUrl = remember { mutableStateOf("") }
    val savedSetting = remember { mutableStateOf(false) }
    val restoreURl = remember { mutableStateOf(true) }
    val showAlertImage = remember { mutableStateOf(false) }
    val urlImage = remember { mutableStateOf("") }

    ReadSettings(baseUrl, restoreURl)

    val viewModel: MainViewModel =
        viewModel(factory = MainViewModelFactory(baseUrl = baseUrl.value))

    SavedSettings(viewModel, ThemeSettings, savedSetting)

    FullImageAlertDialog(
        show = showAlertImage.value,
        urlImage = urlImage.value
    ) { showAlertImage.value = false }

    MyAlertDialog(
        show = showAlertDialog.value,
        onDismiss = { showAlertDialog.value = false }
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                windowInsets = DrawerDefaults.windowInsets
            ) {
                MyDrawerSheet(
                    { scope.launch { drawerState.close() } },
                    { showAlertDialog.value = true },
                    viewModel,
                    savedSetting
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Каталог фото") },
                    colors = topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    ),
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open() // Открытие панели при нажатии
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Меню")
                        }
                    }
                )
            }
        ) { innerPadding ->
            CatList(
                modifier = Modifier.padding(innerPadding),
                viewModel = viewModel,
                urlImage = urlImage,
                showAlertImage = { showAlertImage.value = true }
            )
        }
    }
}

@Composable
fun MyDrawerSheet(
    closeDrawer: () -> Unit,
    showAlertDialog: () -> Unit,
    mainViewModel: MainViewModel,
    savedSetting: MutableState<Boolean>,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                modifier = Modifier
                    .padding(10.dp)
                    .clickable { closeDrawer() },
                imageVector = Icons.Filled.Home,
                contentDescription = "Настройки"
            )
            Spacer(Modifier.weight(1f))
            Text(
                modifier = Modifier.padding(10.dp),
                text = "Настройки",
                fontSize = 22.sp,
                fontWeight = W900
            )
            Spacer(Modifier.weight(1f))
            Icon(
                modifier = Modifier
                    .padding(10.dp)
                    .clickable
                    {
                        showAlertDialog()
                    },
                imageVector = Icons.Filled.Info,
                contentDescription = "О программе"
            )
        }
        HorizontalDivider(thickness = 2.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    ThemeSettings.isDarkThemeEnabled = !ThemeSettings.isDarkThemeEnabled
                    mainViewModel.isDarkThemeEnabled = ThemeSettings.isDarkThemeEnabled
                    savedSetting.value = true
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                modifier = Modifier.padding(10.dp),
                checked = ThemeSettings.isDarkThemeEnabled,
                onCheckedChange = {
                    ThemeSettings.isDarkThemeEnabled = !ThemeSettings.isDarkThemeEnabled
                    mainViewModel.isDarkThemeEnabled = ThemeSettings.isDarkThemeEnabled
                    savedSetting.value = true
                }
            )
            Text(
                text = if (ThemeSettings.isDarkThemeEnabled) {
                    "Включить светлую тему"
                } else {
                    "Включить темную тему"
                },
                modifier = Modifier.padding(10.dp),
                fontSize = 22.sp
            )
        }
        HorizontalDivider(thickness = 2.dp)
        MyBaseUrlSelect(mainViewModel, savedSetting)
    }
}

@Preview(showBackground = true)
@Composable
fun MyDrawerSheetPreview() {
    val saved = remember { mutableStateOf(false) }
    MyDrawerSheet({}, {}, MainViewModel(BaseURL.BASE_URL_CAT), saved)
}

@Composable
fun MyBaseUrlSelect(vModel: MainViewModel, savedSetting: MutableState<Boolean>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                vModel.baseUrl = BaseURL.BASE_URL_CAT
                savedSetting.value = true
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = (vModel.baseUrl == BaseURL.BASE_URL_CAT),
            onClick = {
                vModel.baseUrl = BaseURL.BASE_URL_CAT
                savedSetting.value = true
            }
        )
        Text(
            text = "Использовать API Cat",
            fontSize = 22.sp
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                vModel.baseUrl = BaseURL.BASE_URL_DOG
                savedSetting.value = true
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = (vModel.baseUrl == BaseURL.BASE_URL_DOG),
            onClick = {
                vModel.baseUrl = BaseURL.BASE_URL_DOG
                savedSetting.value = true
            }
        )
        Text(
            text = "Использовать API Dog",
            fontSize = 22.sp
        )
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    )
    {
        Button(
            modifier = Modifier.padding(10.dp),
            onClick = {
                vModel.getCats()

            }
        ) {
            Text("Выполнить запрос")
        }
    }
    HorizontalDivider(thickness = 2.dp)

}

@Composable
fun MyAlertDialog(show: Boolean, onDismiss: () -> Unit) {
    if (show) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                )
                {
                    Text(text = "О программе")
                }
            },
            text = { Text("Программа получает 10 картинок с сайта The Cat API") },
            confirmButton = {
                Button({ onDismiss() }) {
                    Text("OK", fontSize = 22.sp)
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MyAlertDialogPreview() {
    MyAlertDialog(true) { }
}

@Composable
fun CatCard(catCatalog: CatCatalog, urlImage: MutableState<String>, showImage: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .requiredHeight(296.dp),
        elevation = cardElevation(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            catCatalog.id?.let {
                Text(
                    text = it,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                )
            }
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showImage()
                        urlImage.value = catCatalog.url.toString()
                    },
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(catCatalog.url)
                    .crossfade(true)
                    .build(),
                error = painterResource(id = R.drawable.icon_insert_picture),
                placeholder = painterResource(id = R.drawable.img_loading),
                contentDescription = stringResource(R.string.cat_card),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun FullImageAlertDialog(show: Boolean, urlImage: String, onDismiss: () -> Unit) {
    if (show) {
        Dialog(
            onDismissRequest = { onDismiss() },
            content = {
                Box(
                    Modifier
                        .fillMaxWidth()
                        //.padding(16.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surface)

                    //.width(400.dp)
                ) {
                    AsyncImage(
                        model = urlImage,
                        contentDescription = stringResource(R.string.descriptionAlertImage),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Box(
                        Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .height(64.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Black.copy(alpha = 0.33f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    Box(
                        Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(64.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.33f)
                                    )
                                )
                            )
                    )
                    BasicText(
                        text = stringResource(R.string.descriptionAlertImage),
                        style = TextStyle(color = Color.White),
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.BottomStart)
                    )

                    Box(
                        Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .clickable(role = Role.Button) { /* TODO */ }
                            .background(Color.White)
                            .padding(16.dp), contentAlignment = Alignment.Center
                    ) {
                        Image(
                            Icons.Outlined.Favorite,
                            contentDescription = "Favorite",
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FullImageAlertDialogPreview() {
    FullImageAlertDialog(true, "") {}
}

@Composable
fun CatList(
    modifier: Modifier,
    viewModel: MainViewModel,
    urlImage: MutableState<String>,
    showAlertImage: () -> Unit,
) {
    val cats by viewModel.cats.observeAsState()
    LazyColumn(modifier = modifier) {
        items(cats.orEmpty()) { cat ->
            CatCard(cat, urlImage) { showAlertImage() }
        }
    }
    DisposableEffect(Unit) {
        //viewModel.getCats()
        onDispose { }
    }

}

@Preview(showBackground = true)
@Composable
fun CatListPreview() {
    MyCatAPITheme {
        MyScreen()
    }
}