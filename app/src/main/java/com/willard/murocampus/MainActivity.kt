package com.willard.murocampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.willard.murocampus.data.PostRepositoryRemote
import com.willard.murocampus.data.remote.RetrofitClient
import com.willard.murocampus.ui.PantallaCrear
import com.willard.murocampus.ui.PantallaDetalle
import com.willard.murocampus.ui.PantallaLista
import com.willard.murocampus.ui.theme.MuroCampusTheme
import com.willard.murocampus.viewmodel.PostsViewModel
import com.willard.murocampus.viewmodel.PostsViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MuroCampusTheme {
                AppMuroCampus()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppMuroCampus(
    vm: PostsViewModel = viewModel(
        factory = PostsViewModelFactory(PostRepositoryRemote(RetrofitClient.api))
    )
) {
    var idSeleccionado by rememberSaveable { mutableStateOf<Int?>(null) }
    var creando by rememberSaveable { mutableStateOf(false) }

    BackHandler(enabled = idSeleccionado != null || creando) {
        idSeleccionado = null
        creando = false
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "MuroCampus", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            if (idSeleccionado == null && !creando) {
                FloatingActionButton(
                    onClick = {
                        vm.reiniciarPublicacion()
                        creando = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text(text = "+", style = MaterialTheme.typography.headlineSmall)
                }
            }
        }
    ) { innerPadding ->
        val contenido = Modifier.padding(innerPadding)
        val id = idSeleccionado

        when {
            creando -> PantallaCrear(vm = vm, modifier = contenido)

            id != null -> PantallaDetalle(vm = vm, id = id, modifier = contenido)

            else -> PantallaLista(
                vm = vm,
                onAbrir = { nuevoId ->
                    vm.abrir(nuevoId)
                    idSeleccionado = nuevoId
                },
                modifier = contenido
            )
        }
    }
}