package Dialogs
/*Dialog para: Cambiar valor de campos del usuario.  Recibe el usuario y el tipo de campo que se desea editar
  y solo es valida si el valor nuevo tiene un largo mayor a 0.
 */
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.todolistproject.R
import com.example.todolistproject.model.UserRoom
import kotlinx.android.synthetic.main.dialog_change_name.view.*

class DialogChangeProfileName : DialogFragment() {

    private var listener:dialogChangeListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val arg = arguments
            val userRecibido= arg?.get("KEY1")
            val tipoRecibido= arg?.get("KEY2")
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater
            val view: View = inflater.inflate(R.layout.dialog_change_name,null)
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout

            builder.setView(view)
                // Add action buttons
                .setPositiveButton("Cambiar", DialogInterface.OnClickListener { dialog, id ->
                    if (view.editTextDialogChange.text.toString()!=""){
                        listener!!.changeName(view.editTextDialogChange.text.toString(),userRecibido as UserRoom,tipoRecibido as String)}
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
        if (context is dialogChangeListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }
}

interface dialogChangeListener{
    fun changeName(newName: String, user: UserRoom, type : String)
}

