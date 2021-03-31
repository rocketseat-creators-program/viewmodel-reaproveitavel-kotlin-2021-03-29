package br.com.kassiano.rocketseat

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

abstract class RequestViewModel<T>: ViewModel() {

    abstract suspend fun request() : T

    val liveData = liveData<Result<T>> {

        try {
            val result = withContext(Dispatchers.IO) {
                request()
            }

            withContext(Dispatchers.Main) {
                emit(Result.success(result))
            }
        } catch (error: Throwable) {
            emit(Result.failure(error))
        }
    }
}

//Exemplos

//Exemplo Pedidos
class PedidosViewModel: RequestViewModel<List<String>>() {

    override suspend fun request(): List<String> {
        delay(2000)
        return listOf("Pedido 1", "Pedido 2", "Pedido 3")
    }
}

//Exemplo Clientes
data class ClienteResult(val clientes: List<String>)

class ClientesViewModel: RequestViewModel<ClienteResult>() {
    override suspend fun request(): ClienteResult {
        return ClienteResult(
            clientes = listOf("Cliente1", "Cliente2")
        )
    }
}
/********************/

//Exemplo Fragment

class PedidosFragment: Fragment() {

    lateinit var viewModel: PedidosViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.liveData.observe(viewLifecycleOwner) {

            when {
                it.isSuccess -> {
                    it.getOrNull()?.map { print(it) }
                }
                it.isFailure -> {
                    print(it.exceptionOrNull()?.message)
                }
            }
        }
    }
}

//Dependência: implementation "androidx.lifecycle:lifecycle-livedata-ktx:2.3.0"

//linkedin: Kassiano Resende
//@kassianoresende

