package eu.ottop.yamlauncher

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AppMenuLinearLayoutManager(private val activity: MainActivity) : LinearLayoutManager(activity) {

        private var scrollState = RecyclerView.SCROLL_STATE_IDLE
        fun setScrollState(state: Int) {
            scrollState = state
        }

        override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
            val scrollRange = super.scrollVerticallyBy(dy, recycler, state)
            val overscroll: Int = dy - scrollRange
            if (overscroll < 0 && scrollState == RecyclerView.SCROLL_STATE_DRAGGING) {
                activity.showHome()
            }
            return scrollRange
        }

    }