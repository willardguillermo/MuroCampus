package com.willard.murocampus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.willard.murocampus.viewmodel.PostsViewModel
import com.willard.murocampus.viewmodel.UiState

private val FondoExito = Color(0xFFE6F4EA)
private val TextoExito = Color(0xFF1E6B34)

@Composable
fun PantallaCrear(vm: PostsViewModel, modifier: Modifier = Modifier) {
    val estado by vm.publicacion.collectAsStateWithLifecycle()
    var titulo by rememberSaveable { mutableStateOf("") }
    var texto by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(estado) {
        if (estado is UiState.Exito) {
            titulo = ""
            texto = ""
        }
    }

    val enviando = estado is UiState.Cargando
    val puedePublicar = titulo.isNotBlank() && texto.isNotBlank() && !enviando

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text(text = "Título") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = texto,
            onValueChange = { texto = it },
            label = { Text(text = "Contenido") },
            minLines = 4,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { vm.publicar(titulo, texto) },
            enabled = puedePublicar
        ) {
            Text(text = "Publicar")
        }

        when (val e = estado) {
            null -> Unit

            is UiState.Cargando -> CircularProgressIndicator(modifier = Modifier.size(32.dp))

            is UiState.Exito -> Text(
                text = "Publicado con id ${e.datos.id}",
                color = TextoExito,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FondoExito, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            )

            is UiState.Error -> Text(
                text = e.mensaje,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            )
        }
    }
}