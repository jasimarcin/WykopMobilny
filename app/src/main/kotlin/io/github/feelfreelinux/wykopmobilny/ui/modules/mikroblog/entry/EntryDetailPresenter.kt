package io.github.feelfreelinux.wykopmobilny.ui.modules.mikroblog.entry

import io.github.feelfreelinux.wykopmobilny.api.entries.EntriesApi
import io.github.feelfreelinux.wykopmobilny.api.entries.TypedInputStream
import io.github.feelfreelinux.wykopmobilny.base.BasePresenter
import io.github.feelfreelinux.wykopmobilny.utils.rx.SubscriptionHelperApi

class EntryDetailPresenter(private val subscriptionHelper: SubscriptionHelperApi, private val entriesApi: EntriesApi) : BasePresenter<EntryDetailView>() {
    var entryId = 0
    fun loadData() {
        view?.hideInputToolbar()
        subscriptionHelper.subscribe(
                entriesApi.getEntryIndex(entryId),
                { view?.showEntry(it) },
                { view?.showErrorDialog(it) }, this)

    }

    fun addComment(body : String, photo: TypedInputStream) {
        subscriptionHelper.subscribe(
                entriesApi.addEntryComment(body, entryId, photo),
                {
                    view?.hideInputbarProgress()
                    view?.resetInputbarState()
                    loadData() // Refresh view
                },
                {
                    view?.hideInputbarProgress()
                    view?.showErrorDialog(it)
                },
                this
        )
    }

    fun addComment(body : String, photo : String?) {
        subscriptionHelper.subscribe(
                entriesApi.addEntryComment(body, entryId, photo),
                {
                    view?.hideInputbarProgress()
                    view?.resetInputbarState()
                    loadData() // Refresh view
                },
                {
                    view?.hideInputbarProgress()
                    view?.showErrorDialog(it)
                },
                this
        )
    }

    override fun unsubscribe() {
        super.unsubscribe()
        subscriptionHelper.dispose(this)
    }
}