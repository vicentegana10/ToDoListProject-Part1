package Dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.todolistproject.R
import kotlinx.android.synthetic.main.dialog_change_name.view.*
import kotlinx.android.synthetic.main.dialog_list2.view.*

class DialogChangeItemName : DialogFragment() {

    private var listener:dialogChangeItemNameListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val arg = arguments
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;
            var view: View = inflater.inflate(R.layout.dialog_change_name,null)
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout

            builder.setView(view)
                // Add action buttons
                .setPositiveButton("Cambiar", DialogInterface.OnClickListener { dialog, id ->
                    if (view.editTextDialogChange.text.toString()!=""){
                    listener!!.changeItemName(view.editTextDialogChange.text.toString())}
                    else{
                        Toast.makeText(view.context,"Error, ingrese valor válido", Toast.LENGTH_LONG).show()
                        getDialog()?.cancel()}
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
        if (context is dialogChangeItemNameListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }
}

interface dialogChangeItemNameListener{
    //fun addList(nameList: String)
    fun changeItemName(nameItem: String)
}