package Dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.DialogFragment

import com.example.todolistproject.List
import com.example.todolistproject.R
import kotlinx.android.synthetic.main.activity_list.view.*
import kotlinx.android.synthetic.main.dialog_list.view.*


class DialogList : DialogFragment() {

    private var listener:dialogListListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;
            var view: View = inflater.inflate(R.layout.dialog_list,null)
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout

            builder.setView(view)
                // Add action buttons
                .setPositiveButton("Agregar", DialogInterface.OnClickListener { dialog, id ->
                    listener!!.addList(view.editTextList.text.toString())
                })
                .setNegativeButton("Cancelar", DialogInterface.OnClickListener { dialog, id ->
                    getDialog()?.cancel()
                })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        if (context is dialogListListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }
}

interface dialogListListener{
    fun addList(nameList: String)
}
