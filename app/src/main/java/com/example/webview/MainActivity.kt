package com.example.webview

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
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

// Clase que maneja la busqueda de paginas web mediante WebView
@RequiresApi(Build.VERSION_CODES.R)
@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreen() {
    // Contexto actual de la aplicacion para obtener el Toast
    val context = LocalContext.current

    // Almacena la url que se cargara en el WebView
    var url by remember { mutableStateOf("") }

    // Referencia a WebView para manejarlo
    var webView by remember { mutableStateOf<WebView?>(null) }

    // Almacena el texto ingresado en el TextField
    var textState by remember { mutableStateOf(url) }

    // Controla el estado del Checkbox
    var isChecked by remember { mutableStateOf(true) }

    var resolution by remember { mutableStateOf("Resolucion: ") }

    // Scaffold que organiza la estructura basica para la topBar y el contenido principal
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Web View", color = Color.White, fontSize = 25.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Blue),
                actions = {

                    IconButton(onClick = {
                        // Metodo de la clase webView que carga Urls a partir de texto
                        webView?.loadUrl(textState)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Cargar URL",
                            tint = Color.White
                        )
                    }

                    // Vacia el textState
                    IconButton(onClick = { textState = "" }) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = "Borrar texto",
                            tint = Color.White
                        )
                    }

                    // Vuelve atras en una url
                    IconButton(onClick = { webView?.goBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Ir atrás",
                            tint = Color.White
                        )
                    }

                    // Va hacia delante en una url
                    IconButton(onClick = { webView?.goForward() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Ir adelante",
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

                        // CheckBox que si esta marcado permitira la navegacion dentro de las url, y vicerversa
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                isChecked = checked
                                val message =
                                    if (checked) {
                                        "Navegación activada"
                                    } else {
                                        "Navegación desactivada"
                                    }
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.padding(end = 8.dp, top = 3.dp)
                        )
                    }
                }

                // Permite integrar WebView en Compose
                AndroidView(
                    // Instancia
                    factory = { context ->
                        WebView(context).apply {
                            // Paginas con javaScript carguen
                            settings.javaScriptEnabled = true
                            // WebClient permite manejar diversas operaciones
                            webViewClient = object : WebViewClient() {

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    view?.evaluateJavascript(
                                        "(function() { return window.innerWidth + 'x' + window.innerHeight; })();"
                                    ) { result ->
                                        resolution = "Resolucion de la pagina: $result"
                                    }
                                }

                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): Boolean {
                                    if (isChecked) {
                                        // Permite la carga de la nueva url
                                        request?.url?.let {
                                            view?.loadUrl(it.toString())
                                        }
                                        // Si no se mantiene la pagina
                                    }
                                    return true

                                }
                            }
                            loadUrl(url)
                            webView = this
                        }
                    },
                    // Actualiza la vista para no crear otra instancia
                    update = { webViewInstance ->
                        // Intancia del factory
                        webViewInstance.loadUrl(url)
                        webView = webViewInstance
                    }
                )
            }
        }
    )
}
