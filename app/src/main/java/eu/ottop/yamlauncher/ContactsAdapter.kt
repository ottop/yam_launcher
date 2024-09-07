package eu.ottop.yamlauncher

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import eu.ottop.yamlauncher.settings.SharedPreferenceManager
import eu.ottop.yamlauncher.utils.UIUtils

class ContactsAdapter(
    private val context: Context,
    private var contacts: MutableList<Pair<String, Int>>,
    private val contactClickListener: OnContactClickListener
) :
    RecyclerView.Adapter<ContactsAdapter.AppViewHolder>() {

        private val uiUtils = UIUtils(context)
        private val sharedPreferenceManager = SharedPreferenceManager(context)

    interface OnContactClickListener {
        fun onContactClick(contactId: Int)
    }

    inner class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val listItem: FrameLayout = itemView.findViewById(R.id.listItem)
        val textView: TextView = listItem.findViewById(R.id.appName)

        init {
            textView.setOnClickListener {
                val position = bindingAdapterPosition
                val contact = contacts[position]
                contactClickListener.onContactClick(contact.second)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.app_item_layout, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val contact = contacts[position]
        holder.textView.setCompoundDrawablesWithIntrinsicBounds(
            ResourcesCompat.getDrawable(context.resources, R.drawable.ic_empty, null),null, ResourcesCompat.getDrawable(context.resources, R.drawable.ic_empty, null),null)

        uiUtils.setAppAlignment(holder.textView)

        uiUtils.setAppSize(holder.textView)

        uiUtils.setItemSpacing(holder.textView)

        uiUtils.setTextFont(holder.listItem)
        holder.textView.setTextColor(sharedPreferenceManager.getTextColor())

        holder.textView.text = contact.first

        holder.textView.visibility = View.VISIBLE
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateContacts(newContacts: List<Pair<String, Int>>) {
        contacts = newContacts.toMutableList()
        notifyDataSetChanged()
    }
}