package com.example.pr19_1_Leshukov

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar

import java.util.Date
import java.util.UUID



private const val REQUEST_DATE = 0;
private const val DATE_FORMAT = "EEE, MMM,dd"
class CrimeFragment : Fragment() {
    private val REQUEST_CONTACT = 1
    private val PERMISSION_REQUEST_READ_CONTACTS = 100
    private lateinit var btn_choose: Button
    private lateinit var crime: Crime
    private lateinit var callButton: Button
    private lateinit var titleField: EditText
    private lateinit var dateButton: ExtendedFloatingActionButton
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var btn_send:Button

    private var selectedPhoneNumber: String? = null
    private var selectedContactId: String? = null
    private val dateString: String
        get() = DateFormat.format(DATE_FORMAT, crime.date).toString()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        crime = Crime(UUID.randomUUID(), Date())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime, container, false)


        callButton = view.findViewById(R.id.call_btn) as Button
        btn_choose = view.findViewById(R.id.choose_suspect) as Button
        btn_send = view.findViewById(R.id.send_crime_report) as Button
        titleField = view.findViewById(R.id.crime_title) as EditText
        dateButton = view.findViewById(R.id.crime_date) as ExtendedFloatingActionButton
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox
        solvedCheckBox.isEnabled = false
        callButton.isEnabled = false

        btn_choose.setOnClickListener {
            checkContactPermissionAndPickContact()

        }

        dateButton.apply {
            isEnabled = false
            text = crime.date.toString()
        }
        btn_send.setOnClickListener{

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
            }
            startActivity(intent)
        }

        callButton.setOnClickListener {
            if (selectedPhoneNumber != null) {
                callSelectedContact()
            }
        }
        dateButton.setOnClickListener{
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MainFragment(crime))
                .commit()

        }

        return view
    }

    private fun callSelectedContact() {
        if (selectedPhoneNumber.isNullOrBlank()) {
            return
        }

        val callUri = Uri.parse("tel:${selectedPhoneNumber}")
        val callIntent = Intent(Intent.ACTION_DIAL, callUri)

        try {
            startActivity(callIntent)
        } catch (e: Exception) {
            Snackbar.make(
                requireView(),
                "Не удалось открыть приложение для звонков",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }
    private fun getCrimeReport():String{
        val solvedString = if(crime.isSolved){
            getString(R.string.case_solved)
        }
        else{
            getString(R.string.case_notsolved)
        }
        val daateString = DateFormat.format(DATE_FORMAT,crime.date).toString()
        var suspect = if(crime.suspect.isBlank()){
            getString(R.string.nosuspect)
        }
        else{
            getString(R.string.suspectis,crime.suspect)
        }
        return getString(R.string.crime_disc,crime.title,dateString,solvedString,suspect)
    }

    private fun checkContactPermissionAndPickContact() {//Проверка на разрешение
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {
                 openContactPicker()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                android.Manifest.permission.READ_CONTACTS
            ) -> {
                // Показываем объяснение и запрашиваем разрешение
                Snackbar.make(
                    requireView(),
                    "Для выбора контакта необходимо разрешение на чтение контактов",
                    Snackbar.LENGTH_LONG
                ).show()
                requestPermissions(
                    arrayOf(android.Manifest.permission.READ_CONTACTS),
                    PERMISSION_REQUEST_READ_CONTACTS
                )
            }
            
            else -> {
                // Запрашиваем разрешение в первый раз
                requestPermissions(
                    arrayOf(android.Manifest.permission.READ_CONTACTS),
                    PERMISSION_REQUEST_READ_CONTACTS
                )
            }
        }
    }
    private fun openContactPicker() {
        val pickContactIntent = Intent(
            Intent.ACTION_PICK,
            ContactsContract.Contacts.CONTENT_URI
        )
        startActivityForResult(pickContactIntent, REQUEST_CONTACT)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_READ_CONTACTS -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openContactPicker()
                } else {
                    Snackbar.make(
                        requireView(),
                        "Без разрешения невозможно выбрать контакт",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val titleWatcher = object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?,
                                           start: Int,
                                           count: Int,
                                           after: Int) {
            }

            override fun onTextChanged(s: CharSequence?,
                                       start: Int,
                                       before: Int,
                                       count: Int) {
                if(titleField.text.toString()!="") {
                    solvedCheckBox.isEnabled = true
                }
                else{
                    solvedCheckBox.isEnabled = false
                }
                crime.title = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }

        titleField.addTextChangedListener(titleWatcher)

        solvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked ->  crime.isSolved = isChecked
                if(crime.isSolved==true){
            dateButton.isEnabled=true}
                else{
                    dateButton.isEnabled=false
                }
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CONTACT -> {
                if (resultCode != Activity.RESULT_OK || data?.data == null) {
                    return
                }

                val contactUri: Uri = data.data!!

                val cursor = requireActivity().contentResolver.query(
                    contactUri,
                    arrayOf(
                        ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.Contacts.HAS_PHONE_NUMBER
                    ),
                    null,
                    null,
                    null
                )

                cursor?.use {
                    if (it.moveToFirst()) {
                        selectedContactId = it.getString(0)
                        crime.suspect = it.getString(1)
                        val hasPhone = it.getInt(2) == 1

                        btn_choose.text = crime.suspect

                        if (hasPhone && selectedContactId != null) {
                            getPhoneNumber(selectedContactId!!)
                        } else {
                            callButton.isEnabled = false
                            selectedPhoneNumber = null
                        }
                    }
                }
            }
        }
    }
    private fun getPhoneNumber(contactId: String) {
        val phoneCursor = requireActivity().contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
            arrayOf(contactId),
            null
        )

        phoneCursor?.use {
            if (it.moveToFirst()) {
                selectedPhoneNumber = it.getString(0)
                selectedPhoneNumber = selectedPhoneNumber?.replace(Regex("[^0-9+]"), "")
                callButton.isEnabled = true
            } else {
                callButton.isEnabled = false
                selectedPhoneNumber = null
            }
        }
    }


}