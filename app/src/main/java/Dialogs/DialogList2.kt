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
import kotlinx.android.synthetic.main.dialog_list2.view.*

class DialogList2 : DialogFragment() {

    private var listener:dialogList2Listener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val arg = arguments
            val indexRecibido= arg?.get("KEY1")
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;
            var view: View = inflater.inflate(R.layout.dialog_list2,null)
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout

            builder.setView(view)
                // Add action buttons
                .setPositiveButton("Cambiar", DialogInterface.OnClickListener { dialog, id ->
                    if (view.editTextList2.text.toString()!=""){
                        listener!!.changeName(view.editTextList2.text.toString(),indexRecibido.toString().toInt())}
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
        if (context is dialogList2Listener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }
}

interface dialogList2Listener{
    //fun addList(nameList: String)
    fun changeName(nameList: String,indexRec : Int)
}