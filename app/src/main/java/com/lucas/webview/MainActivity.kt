package com.lucas.webview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.lucas.webview.ui.theme.WebViewTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WebViewTheme {
                WebViewScreen()
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreen() {
    // Contexto de la aplicacion para mostrar Toast
    val context = LocalContext.current
    // Url en la que hara la busqueda
    var url by remember { mutableStateOf("") }
    // Campo de texto donde incluye la url
    var textState by remember { mutableStateOf(url) }
    // Comprueba el estado del CheckBox para navegar
    var isChecked by remember { mutableStateOf(true) }
    // Checkbox para activar el zoom
    var isCheckedZoom by remember { mutableStateOf(true) }
    // Cambiar de Composable entre los dos que hay
    var showWebView by remember { mutableStateOf(false) }
    // Resolucion de la aplicacion
    var resolution by remember { mutableStateOf("") }

    // Si es true muestra la url
    if (showWebView) {
        Box {
            // Cargar el WebView
            WebViewComponent(url, isChecked, isCheckedZoom, { newResolution ->
                // Actualiza la resolución
                resolution = newResolution
            }) {
                showWebView = false
            }

            // Texto flotante que muestra la resolución
            Text(
                text = "Resolucion: $resolution",
                color = Color.White,
                fontSize = 24.sp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
                    .background(Color.DarkGray)
            )
        }
    } else {
        // Scaffold que gestiona la topBar y contenido
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Web View", color = Color.White, fontSize = 25.sp) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Blue),
                    actions = {

                        /*Checkbox(
                            checked = isCheckedZoom,
                            onCheckedChange = { checked ->
                                isCheckedZoom = checked
                                val message =
                                    if (checked) "Zoom activado" else "Zoom desactivado"
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.padding(end = 8.dp, top = 3.dp),
                            colors = CheckboxDefaults.colors(checkedColor = Color.Red)
                        )*/

                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                isChecked = checked
                                val message =
                                    if (checked) "Navegación activada" else "Navegación desactivada"
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.padding(end = 8.dp, top = 3.dp)
                        )

                        IconButton(onClick = {
                            if (textState.isNotEmpty()) {
                                url = textState
                                // Cambia de pantalla al hacer la busqueda
                                showWebView = true
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Cargar URL",
                                tint = Color.White
                            )
                        }
                    }
                )
            },
            content = { paddingValues ->
                Column(
                    Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    Box(
                        modifier = Modifier
                            .padding(6.dp)
                            .fillMaxWidth()
                            .border(2.dp, Color.DarkGray, RoundedCornerShape(24.dp))
                            .background(Color.White)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BasicTextField(
                                value = textState,
                                onValueChange = { newText ->
                                    textState = newText
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(8.dp),
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                                decorationBox = { innerTextField ->
                                    if (textState.isEmpty()) {
                                        Text(
                                            text = "Buscar URL",
                                            color = Color.DarkGray,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                        }
                    }

                    val urls = listOf(
                        "Orbys" to "https://orbys.eu/",
                        "Google" to "https://www.google.com/",
                        "Marca" to "https://www.marca.com/",
                        "Periódico Mediterráneo" to "https://www.elperiodicomediterraneo.com/",
                        "Tiempo Castellón" to "https://www.eltiempo.es/castellon-de-la-plana.html"
                    )

                    LazyColumn {
                        itemsIndexed(urls) { _, (name, link) ->
                            Button(
                                onClick = {
                                    textState = link
                                    url = textState
                                    showWebView = true
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = name, Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                )
                            }

                        }
                    }
                }
            }
        )
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewComponent(
    url: String,
    isChecked: Boolean,
    isCheckedZoom: Boolean,
    onResolutionChange: (String) -> Unit,
    onBack: () -> Unit,
) {
    // Estado de carga
    val loadingState = remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    with(settings) {
                        javaScriptEnabled = true
                        useWideViewPort = true
                        loadWithOverviewMode = true
                        domStorageEnabled = true
                        allowFileAccess = true
                        setSupportZoom(true)
                        builtInZoomControls = false
                        displayZoomControls = false
                        cacheMode = WebSettings.LOAD_DEFAULT
                        mediaPlaybackRequiresUserGesture = false
                    }

                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    var resolutionAdjusted = false

                    // Mostrar CircularProgressIndicator al empezar carga
                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            loadingState.value = true
                        }

                        // Ocultar CircularProgressIndicator al terminar
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            loadingState.value = false
                        }

                        override fun onLoadResource(view: WebView?, url: String?) {
                            super.onLoadResource(view, url)
                            if (!resolutionAdjusted) {
                                resolutionAdjusted = true
                                view?.evaluateJavascript(
                                    """
                                    (function() {
                                        var meta = document.querySelector('meta[name="viewport"]');
                                        if (!meta) {
                                            meta = document.createElement('meta');
                                            meta.name = "viewport";
                                            document.head.appendChild(meta);
                                        }
                                        meta.content = "width=3840, height=2160, initial-scale=1.0, maximum-scale=1.0, user-scalable=no";

                                        document.body.style.width = '3840px';
                                        document.body.style.height = '2160px';
                                        document.body.style.margin = '0';
                                        document.body.style.padding = '0';

                                        return document.documentElement.clientWidth + 'x' + document.documentElement.clientHeight;
                                    })();
                                    """
                                ) { result ->
                                    Log.d("Resolucion", result)
                                    onResolutionChange(result)
                                }
                            }

                            if (isCheckedZoom) {
                                view?.evaluateJavascript(
                                    """
                                    document.body.style.zoom = '50%';
                                    """, null
                                )
                            }
                        }

                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            return !isChecked
                        }
                    }
                    loadUrl(url)
                }
            },
            update = { webViewInstance ->
                if (webViewInstance.url != url) {
                    loadingState.value = true
                    webViewInstance.loadUrl(url)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (loadingState.value) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(48.dp),
                color = Color.DarkGray,
            )
        }

        FloatingActionButton(
            onClick = { onBack() },
            contentColor = Color.White,
            containerColor = Color.Blue,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(imageVector = Icons.Filled.Home, contentDescription = "Botón flotante")
        }
    }
}
