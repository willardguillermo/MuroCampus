package com.willard.murocampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.willard.murocampus.data.PostRepository
import com.willard.murocampus.data.mensajeDe
import com.willard.murocampus.model.Comentario
import com.willard.murocampus.model.NuevoPost
import com.willard.murocampus.model.Post
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DetalleUi(
    val post: Post,
    val comentarios: List<Comentario>
)

class PostsViewModel(private val repo: PostRepository) : ViewModel() {

    private val _lista = MutableStateFlow<UiState<List<Post>>>(UiState.Cargando)
    val lista: StateFlow<UiState<List<Post>>> = _lista.asStateFlow()

    private val _detalle = MutableStateFlow<UiState<DetalleUi>>(UiState.Cargando)
    val detalle: StateFlow<UiState<DetalleUi>> = _detalle.asStateFlow()

    private val _usuarioFiltro = MutableStateFlow<Int?>(null)
    val usuarioFiltro: StateFlow<Int?> = _usuarioFiltro.asStateFlow()

    private val _publicacion = MutableStateFlow<UiState<Post>?>(null)
    val publicacion: StateFlow<UiState<Post>?> = _publicacion.asStateFlow()

    private val _eliminacion = MutableStateFlow<UiState<Unit>?>(null)
    val eliminacion: StateFlow<UiState<Unit>?> = _eliminacion.asStateFlow()

    private var trabajoLista: Job? = null
    private var trabajoDetalle: Job? = null

    init {
        cargar()
    }

    fun cargar() {
        trabajoLista?.cancel()
        trabajoLista = viewModelScope.launch {
            _lista.value = UiState.Cargando
            val userId = _usuarioFiltro.value
            val resultado = if (userId == null) repo.listar() else repo.porUsuario(userId)
            resultado
                .onSuccess { _lista.value = UiState.Exito(it) }
                .onFailure { _lista.value = UiState.Error(mensajeDe(it)) }
        }
    }

    fun filtrar(userId: Int?) {
        _usuarioFiltro.value = userId
        cargar()
    }

    fun abrir(id: Int) {
        trabajoDetalle?.cancel()
        trabajoDetalle = viewModelScope.launch {
            _detalle.value = UiState.Cargando
            coroutineScope {
                val post = async { repo.detalle(id) }
                val comentarios = async { repo.comentarios(id) }
                val rPost = post.await()
                val rComentarios = comentarios.await()

                val error = rPost.exceptionOrNull() ?: rComentarios.exceptionOrNull()
                _detalle.value = if (error != null) {
                    UiState.Error(mensajeDe(error))
                } else {
                    UiState.Exito(DetalleUi(rPost.getOrThrow(), rComentarios.getOrThrow()))
                }
            }
        }
    }

    fun publicar(titulo: String, texto: String) {
        viewModelScope.launch {
            _publicacion.value = UiState.Cargando
            val nuevo = NuevoPost(userId = 1, title = titulo.trim(), body = texto.trim())
            repo.crear(nuevo)
                .onSuccess { _publicacion.value = UiState.Exito(it) }
                .onFailure { _publicacion.value = UiState.Error(mensajeDe(it)) }
        }
    }

    fun reiniciarPublicacion() {
        _publicacion.value = null
    }

    fun eliminar(id: Int) {
        viewModelScope.launch {
            _eliminacion.value = UiState.Cargando
            repo.eliminar(id)
                .onSuccess {
                    val estadoActual = _lista.value
                    if (estadoActual is UiState.Exito) {
                        _lista.value = UiState.Exito(estadoActual.datos.filter { post -> post.id != id })
                    }
                    _eliminacion.value = UiState.Exito(Unit)
                }
                .onFailure {
                    _eliminacion.value = UiState.Error(mensajeDe(it))
                }
        }
    }

    fun reiniciarEliminacion() {
        _eliminacion.value = null
    }
}