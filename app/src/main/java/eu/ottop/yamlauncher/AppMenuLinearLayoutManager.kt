package eu.ottop.yamlauncher

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AppMenuLinearLayoutManager(private val activity: MainActivity) : LinearLayoutManager(activity) {

    private var firstVisibleItemPosition = 0
    private var scrollStarted = false

    fun setScrollInfo() {
        firstVisibleItemPosition = findFirstCompletelyVisibleItemPosition()
        scrollStarted = true
    }

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        val scrollRange = super.scrollVerticallyBy(dy, recycler, state)
        val overscroll: Int = dy - scrollRange

        if (overscroll < 0 && firstVisibleItemPosition == 0 && scrollStarted && activity.isJobActive) {
            activity.backToHome()
        }

        if (scrollStarted) {
            scrollStarted = false
        }

        return scrollRange
    }

}