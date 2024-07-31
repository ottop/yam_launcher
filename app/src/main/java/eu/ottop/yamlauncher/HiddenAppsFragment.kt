package eu.ottop.yamlauncher

import android.app.Activity
import android.content.pm.LauncherActivityInfo
import android.os.Bundle
import android.os.UserHandle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HiddenAppsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HiddenAppsFragment : Fragment(), HiddenAppsAdapter.OnItemClickListener, HiddenAppsAdapter.OnShortcutListener, HiddenAppsAdapter.OnItemLongClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val appUtils = AppUtils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hidden_apps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = HiddenAppsAdapter(requireContext(), appUtils.getHiddenApps(activity as Activity).toMutableList(), this, this, this)
        val recyclerView = view.findViewById<RecyclerView>(R.id.hidden_app_recycler)
        val appMenuEdgeFactory = AppMenuEdgeFactory(requireActivity())

        recyclerView.edgeEffectFactory = appMenuEdgeFactory
        recyclerView.adapter = adapter
        recyclerView.scrollToPosition(0)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HiddenAppsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HiddenAppsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onItemClick(appInfo: LauncherActivityInfo, userHandle: UserHandle) {
        TODO("Not yet implemented")
    }

    override fun onShortcut(
        appInfo: LauncherActivityInfo,
        userHandle: UserHandle,
        textView: TextView,
        userProfile: Int,
        shortcutView: TextView
    ) {
        TODO("Not yet implemented")
    }

    override fun onItemLongClick(
        appInfo: LauncherActivityInfo,
        userHandle: UserHandle,
        userProfile: Int,
        textView: TextView,
        actionMenuLayout: LinearLayout,
        editView: LinearLayout,
        position: Int
    ) {
        TODO("Not yet implemented")
    }
}