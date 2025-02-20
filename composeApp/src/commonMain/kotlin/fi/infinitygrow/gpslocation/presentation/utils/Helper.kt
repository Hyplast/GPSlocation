package fi.infinitygrow.gpslocation.presentation.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal fun <T : Any> StateFlow<T>.common(): CommonStateFlow<T> = CommonStateFlow<T>(this)

class CommonCancelable(val cancel: () -> Unit)

class CommonStateFlow<T>(
    private val underlyingStateFlow: StateFlow<T>
) : StateFlow<T> {
    override val replayCache: List<T>
        get() = underlyingStateFlow.replayCache
    override val value: T
        get() = underlyingStateFlow.value

    override suspend fun collect(collector: FlowCollector<T>): Nothing {
        underlyingStateFlow.collect(collector)
    }

    fun startCollect(
        onEach: (T) -> Unit,
        onCancel: () -> Unit
    ): CommonCancelable {
        val collectionScope = CoroutineScope(Dispatchers.Main)
        val collectionJob = collectionScope.launch {
            try {
                collect(collector = { onEach(it) })
            } catch (e: Exception) {
                onCancel()
                throw e
            }
        }
        return CommonCancelable { collectionJob.cancel() }
    }
}