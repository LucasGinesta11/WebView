package com.example.webview

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebResourceRequest
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.webview.ui.theme.WebViewTheme

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
    var url by remember { mutableStateOf("https://www.google.com/") }
    // Campo de texto donde incluye la url
    var textState by remember { mutableStateOf(url) }
    // Comprueba el estado del CheckBox
    var isChecked by remember { mutableStateOf(true) }
    // Cambiar de Composable entre los dos que hay
    var showWebView by remember { mutableStateOf(false) }
    // Resolucion de la aplicacion
    var resolution by remember { mutableStateOf("") }

    // Si es true muestra la url
    if (showWebView) {
        Box {
            // Cargar el WebView
            WebViewComponent(url, isChecked) { newResolution ->
                // Actualiza la resolución
                resolution = newResolution
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
                                onValueChange = { textState = it },
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
    onResolutionChange: (String) -> Unit
) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true
                settings.domStorageEnabled = true
                settings.allowFileAccess = true
                settings.setSupportZoom(true)

                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                settings.userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36"

                webViewClient = object : WebViewClient() {
                    var initialUrlLoaded = false

                    override fun onPageFinished(view: WebView?, url: String?) {
                        view?.evaluateJavascript("""
                            (function() {

                                var meta = document.querySelector('meta[name="viewport"]');
                                if (!meta) {
                                    meta = document.createElement('meta');
                                    meta.name = "viewport";
                                    document.head.appendChild(meta);
                                }
                                meta.content = "width=3840, initial-scale=1.0";

                                document.body.style.width = "3840px";
                                document.body.style.minHeight = "2160px";

                                return window.innerWidth + 'x' + window.innerHeight;
                            })();
                        """) { result ->
                            onResolutionChange(result)
                        }

                        if (url == this@apply.originalUrl) {
                            initialUrlLoaded = true
                        }
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        return if (isChecked) {
                            false
                        } else {
                            if (!initialUrlLoaded) {
                                false
                            } else {
                                true
                            }
                        }
                    }
                }
                loadUrl(url)
            }
        },
        update = { webViewInstance ->
            webViewInstance.loadUrl(url)
        }
    )
}