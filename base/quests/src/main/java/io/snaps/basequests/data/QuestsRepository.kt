package io.snaps.basequests.data

import dagger.Lazy
import io.snaps.basequests.data.model.QuestItemResponseDto
import io.snaps.basequests.domain.QuestModel
import io.snaps.basequests.domain.QuestPageModel
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.network.PagedLoaderParams
import io.snaps.coredata.network.apiCall
import io.snaps.coreui.viewmodel.tryPublish
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

interface QuestsRepository {

    val currentQuestsState: StateFlow<State<QuestModel>>

    suspend fun updateData(isSilently: Boolean = false): Effect<Completable>

    fun getHistoryQuestsState(): StateFlow<QuestPageModel>

    suspend fun refreshHistoryQuests(): Effect<Completable>

    suspend fun loadNextHistoryQuestPage(): Effect<Completable>

    suspend fun postToInstagram(): Effect<Completable>
}

class QuestsRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val questsApi: Lazy<QuestsApi>,
    private val loaderFactory: HistoryQuestsLoaderFactory,
) : QuestsRepository {

    private val _currentQuestsState = MutableStateFlow<State<QuestModel>>(Loading())
    override val currentQuestsState = _currentQuestsState.asStateFlow()

    // todo if fast switched between screens which update this state, we end up with Loading on other screens
    override suspend fun updateData(isSilently: Boolean): Effect<Completable> {
        if (!isSilently) {
            _currentQuestsState tryPublish Loading()
        }
        return apiCall(ioDispatcher) {
            questsApi.get().currentQuests()
        }.map {
            it.toQuestModel()
        }.also {
            _currentQuestsState tryPublish it
        }.toCompletable()
    }

    private fun getLoader(): HistoryQuestsLoader {
        return loaderFactory.get(Unit) {
            PagedLoaderParams(
                action = { from, count -> questsApi.get().historyQuests(from = from, count = count) },
                pageSize = 20,
                nextPageIdFactory = { it.questDate },
                mapper = List<QuestItemResponseDto>::toModelList,
            )
        }
    }

    override fun getHistoryQuestsState(): StateFlow<QuestPageModel> = getLoader().state

    override suspend fun refreshHistoryQuests(): Effect<Completable> = getLoader().refresh()

    override suspend fun loadNextHistoryQuestPage(): Effect<Completable> = getLoader().loadNext()

    override suspend fun postToInstagram(): Effect<Completable> {
        return apiCall(ioDispatcher) {
            questsApi.get().instagramPost()
        }
    }
}