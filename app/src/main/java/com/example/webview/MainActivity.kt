package com.example.webview

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
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
    var url by remember { mutableStateOf("https://orbys.eu/") }
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
                with(settings) {
                    // Activa javascript
                    javaScriptEnabled = true
                    // La pagina se ajusta al ancho de la pantalla
                    useWideViewPort = true
                    // Ajusta el contenido
                    loadWithOverviewMode = true
                    // Almacenamiento local en web
                    domStorageEnabled = true
                    // Acceso a archivos locales
                    allowFileAccess = true
                    // Permite el zoom
                    setSupportZoom(true)
                    // Desactiva los controles de zoom
                    builtInZoomControls = false
                    // Oculta los botones de zoom
                    displayZoomControls = false

                    // Reutiliza cache
                    cacheMode = WebSettings.LOAD_DEFAULT
                    // Reproduce videos sin necesidad de tocar la pantalla
                    mediaPlaybackRequiresUserGesture = false
                }

                // WebView ocupe toda la pantalla
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                webViewClient = object : WebViewClient() {
                    var initialUrlLoaded = false

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        // Forzar resolución 4K
                        view?.evaluateJavascript("""
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
                                
                                return document.documentElement.clientWidth + 'x' + document.documentElement.clientHeight;
                            })();
                        """) { result ->
                            onResolutionChange(result)
                        }

                        // Escalar contenido si es necesario
                        view?.evaluateJavascript("""
                            document.body.style.zoom = (window.innerWidth / 3840) * 100 + '%';
                        """, null)

                        if (url == this@apply.originalUrl) {
                            initialUrlLoaded = true
                        }
                    }

                    // Logica de checkbox
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        return if (isChecked) {
                            false
                        } else {
                            initialUrlLoaded
                        }
                    }

                }
                loadUrl(url)
            }
        },
        update = { webViewInstance ->
            if (webViewInstance.url != url) {
                webViewInstance.loadUrl(url)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}