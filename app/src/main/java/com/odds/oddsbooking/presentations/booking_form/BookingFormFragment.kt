package com.odds.oddsbooking.presentations.booking_form

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.odds.oddsbooking.R
import com.odds.oddsbooking.interfaces.BookingData
import com.odds.oddsbooking.databinding.FragmentBookingFormBinding
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import com.wdullaer.materialdatetimepicker.time.Timepoint
import java.util.*

class BookingFormFragment : Fragment(), BookingFormPresenter.BookingFormView {

    private val binding by lazy { FragmentBookingFormBinding.inflate(layoutInflater) }

    private val presenter by lazy {
        BookingFormPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.attachView(this)
    }

    private var bookingData: BookingData = BookingData()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val rooms = resources.getStringArray(R.array.rooms)
        val arrayAdapter =
            ArrayAdapter(binding.roomFormDropdown.context, R.layout.dropdown_item, rooms)
        val autocompleteTV = binding.root.findViewById<AutoCompleteTextView>(R.id.roomFormDropdown)
        autocompleteTV.setAdapter(arrayAdapter)

        with(binding) {
            nameFormEditText.doOnTextChanged { text, _, _, _ ->
                presenter.validateFullName(text.toString())
                nameFormEditText.setOnFocusChangeListener { _, _ -> presenter.autoFormatName(text.toString()) }
            }

            emailFormEditText.doOnTextChanged { text, _, _, _ ->
                presenter.validateEmail(text.toString())
            }

            phoneFormEditText.doOnTextChanged { text, _, _, _ ->
                presenter.validatePhoneNumber(text.toString())
            }

            roomFormDropdown.doOnTextChanged { text, _, _, _ ->
                presenter.validateRoom(text.toString())
            }

            reasonFormEditText.doOnTextChanged { text, _, _, _ ->
                presenter.validateReason(text.toString())
            }

            fromTimeFormEditText.isEnabled = fromTimeFormEditText.text?.isNotEmpty() == true
            toTimeFormEditText.isEnabled = fromTimeFormEditText.text?.isNotEmpty() == true
            toDateFormEditText.isEnabled = fromTimeFormEditText.text?.isNotEmpty() == true

            fromDateFormEditText.doOnTextChanged { text, _, _, _ ->
                presenter.validateFromDate(text.toString())
            }
            fromTimeFormEditText.doOnTextChanged { text, _, _, _ ->
                presenter.validateFromTime(text.toString(), fromDateFormEditText.text.toString())
            }
            toDateFormEditText.doOnTextChanged { text, _, _, _ ->
                presenter.validateToDate(text.toString(), fromDateFormEditText.text.toString(), fromTimeFormEditText.text.toString())
            }
            toDateFormEditText.doOnTextChanged { text, _, _, _ ->
                presenter.validateToTime(text.toString())
            }

           //preview button disable check
            nameFormEditText.doAfterTextChanged { text ->
                bookingData.fullName = text.toString()
                presenter.validateForm(bookingData)
            }
            emailFormEditText.doAfterTextChanged { text ->
                bookingData.email = text.toString()
                presenter.validateForm(bookingData)
            }
            phoneFormEditText.doAfterTextChanged { text ->
                bookingData.phoneNumber = text.toString()
                presenter.validateForm(bookingData)
            }
            roomFormDropdown.doAfterTextChanged { text ->
                bookingData.room = text.toString()
                presenter.validateForm(bookingData)
            }
            reasonFormEditText.doAfterTextChanged { text ->
                bookingData.reason = text.toString()
                presenter.validateForm(bookingData)
            }
            fromDateFormEditText.doAfterTextChanged { text ->
                bookingData.fromDate = text.toString()
                presenter.validateForm(bookingData)
            }
            fromTimeFormEditText.doAfterTextChanged { text ->
                bookingData.fromTime = text.toString()
                presenter.validateForm(bookingData)
            }
            toDateFormEditText.doAfterTextChanged { text ->
                bookingData.toDate = text.toString()
                presenter.validateForm(bookingData)
            }
            toTimeFormEditText.doAfterTextChanged { text ->
                bookingData.toTime = text.toString()
                presenter.validateForm(bookingData)
            }

            fromDateFormEditText.setOnClickListener {
                showDatePickerDialog(
                    fromDateFormEditText,
                    System.currentTimeMillis() + (14 * 24 * 60 * 60 * 1000),
                    null
                )
            }

//            toDateFormEditText.setOnClickListener {
//                showDatePickerDialog(toDateFormEditText)
//            }
//
//            fromTimeFormEditText.setOnClickListener {
//                showTimePickerDialog(fromTimeFormEditText)
//            }
//
//            toTimeFormEditText.setOnClickListener {
//                showTimePickerDialog(toTimeFormEditText)
//            }

            // onClick previewButton
            previewButton.setOnClickListener {
                findNavController().apply {
                    navigate(
                        R.id.bookingPreviewFragment,
                        bundleOf(
                            BookingFormActivity.EXTRA_BOOKING to bookingData
                        )
                    )
                }


            }
        }
        bind()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    private fun showDatePickerDialog(
        editText: com.google.android.material.textfield.TextInputEditText,
        minDate: Long,
        maxDate: Long?
    ) {
        val calendar = Calendar.getInstance()
        val years = calendar.get(Calendar.YEAR)
        val months = calendar.get(Calendar.MONTH)
        val days = calendar.get(Calendar.DAY_OF_MONTH)

        val listener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val date = String.format("%d/%02d/%02d", year, month + 1, day)
            editText.setText(date)
        }

        val dialog = DatePickerDialog(
            requireContext(),
            listener,
            years,
            months,
            days
        )
        dialog.datePicker.minDate = minDate
        if (maxDate != null)
            dialog.datePicker.maxDate = maxDate
        dialog.show()
    }

    private fun showTimePickerDialog(
        editText: com.google.android.material.textfield.TextInputEditText,
        timePoint: Array<Timepoint>
    ) {
        val listener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute, _ ->
            val time = String.format("%02d:%02d", hourOfDay, minute)
            editText.setText(time)
        }

        val tpd = TimePickerDialog.newInstance(
            listener,
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
            Calendar.getInstance().get(Calendar.MINUTE),
            true
        )
        tpd.setSelectableTimes(timePoint)
        tpd.show(childFragmentManager, "TimepickerDialog")
    }

    override fun onValidateNameError(errMsg: String) {
        val container = binding.nameFormContainer
        container.isErrorEnabled = true
        container.error = errMsg
    }

    override fun onValidateNameSuccess() {
        val container = binding.nameFormContainer
        container.isErrorEnabled = false
        container.error = null
    }

    override fun onValidateEmailError(errMsg: String) {
        val container = binding.emailFormContainer
        container.isErrorEnabled = true
        container.error = errMsg
    }

    override fun onValidateEmailSuccess() {
        val container = binding.emailFormContainer
        container.isErrorEnabled = false
        container.error = null
    }

    override fun onValidatePhoneNumberError(errMsg: String) {
        val container = binding.phoneFormContainer
        container.isErrorEnabled = true
        container.error = errMsg
    }

    override fun onValidatePhoneNumberSuccess() {
        val container = binding.phoneFormContainer
        container.isErrorEnabled = false
        container.error = null
    }

    override fun onValidateRoomError(errMsg: String) {
        val container = binding.roomFormContainer
        container.isErrorEnabled = true
        container.error = errMsg
    }

    override fun onValidateRoomSuccess() {
        val container = binding.roomFormContainer
        container.isErrorEnabled = false
        container.error = null
    }

    override fun onValidateReasonError(errMsg: String) {
        val container = binding.reasonFormContainer
        container.isErrorEnabled = true
        container.error = errMsg
    }

    override fun onValidateReasonSuccess() {
        val container = binding.reasonFormContainer
        container.isErrorEnabled = false
        container.error = null
    }

    override fun onValidateFromDateError(errMsg: String) {
        val container = binding.fromDateFormContainer
        container.isErrorEnabled = true
        container.error = errMsg
    }

    override fun onValidateFromDateSuccess(timeEnable: Array<Timepoint>) {
        val container = binding.fromDateFormContainer
        container.isErrorEnabled = false
        container.error = null
        with(binding){
            fromTimeFormEditText.isEnabled = true
            fromTimeFormEditText.setOnClickListener {
                showTimePickerDialog(binding.fromTimeFormEditText, timeEnable)
            }
            //clear text in fromTime
            if (fromTimeFormEditText.text!!.isNotEmpty()) {
                fromTimeFormEditText.setText("")
            }

            //disable and clear text in FromTime/ToDate/ToTime
            toDateFormEditText.isEnabled = false
            toTimeFormEditText.isEnabled = false
            if (toDateFormEditText.text!!.isNotEmpty()) {
                toDateFormEditText.setText("")
            }
            if (toTimeFormEditText.text!!.isNotEmpty()) {
                toTimeFormEditText.setText("")
            }
        }
    }

    override fun onValidateFromTimeError(errMsg: String) {
        val container = binding.fromTimeFormContainer
        container.isErrorEnabled = true
        container.error = errMsg
    }

    override fun onValidateFromTimeSuccess(minDate: Long, maxDate: Long) {
        val container = binding.fromTimeFormContainer
        container.isErrorEnabled = false
        container.error = null

        with(binding){
            toDateFormEditText.isEnabled = true
            toDateFormEditText.setOnClickListener {
                showDatePickerDialog( binding.toDateFormEditText, minDate, maxDate)
            }

            //clear text in ToDate
            if (toDateFormEditText.text!!.isNotEmpty()) {
                toDateFormEditText.setText("")
            }

            //disable and clear text in ToTime
            toTimeFormEditText.isEnabled = false
            if (toTimeFormEditText.text!!.isNotEmpty()) {
                toTimeFormEditText.setText("")
            }
        }
    }

    override fun onValidateToDateError(errMsg: String) {
        val container = binding.toDateFormContainer
        container.isErrorEnabled = true
        container.error = errMsg
    }

    override fun onValidateToDateSuccess(timeEnable: Array<Timepoint>) {
        val container = binding.toDateFormContainer
        container.isErrorEnabled = false
        container.error = null

        with(binding){
            toTimeFormEditText.isEnabled = true
            toTimeFormEditText.setOnClickListener {
                showTimePickerDialog(binding.toTimeFormEditText, timeEnable)
            }

            //clear text in toTime
            if (toTimeFormEditText.text!!.isNotEmpty()) {
                toTimeFormEditText.setText("")
            }
        }
    }

    override fun onValidateToTimeError(errMsg: String) {
        val container = binding.toTimeFormContainer
        container.isErrorEnabled = true
        container.error = errMsg
    }

    override fun onValidateToTimeSuccess() {
        val container = binding.toTimeFormContainer
        container.isErrorEnabled = false
        container.error = null
    }

    override fun onNameAutoFormat(name: String) {
        val nameFormatter = name.lowercase().trim().split("\\s+".toRegex()).toMutableList()
        for (index in nameFormatter.indices) {
            nameFormatter[index] = nameFormatter[index].replaceFirstChar { it.uppercaseChar() }
        }
        binding.nameFormEditText.setText(nameFormatter.joinToString(" "))
    }

    override fun enablePreviewButton() {
        binding.previewButton.isEnabled = true
        binding.previewButton.setBackgroundColor(
            resources.getColor(
                R.color.purple_color,
                null
            )
        )
    }

    override fun disablePreviewButton() {
        binding.previewButton.isEnabled = false
        binding.previewButton.setBackgroundColor(
            resources.getColor(
                R.color.gray_outline,
                null
            )
        )
    }

    private fun bind() {
        bookingData = arguments?.getParcelable(BookingFormActivity.EXTRA_BOOKING)!!
        with(binding) {
            if (bookingData.fullName.isNotEmpty())
                nameFormEditText.setText(bookingData.fullName)
            if (bookingData.email.isNotEmpty())
                emailFormEditText.setText(bookingData.email)
            if (bookingData.phoneNumber.isNotEmpty())
                phoneFormEditText.setText(bookingData.phoneNumber)
            if (bookingData.room.isNotEmpty())
                roomFormDropdown.setText(bookingData.room, false)
            if (bookingData.reason.isNotEmpty())
                reasonFormEditText.setText(bookingData.reason)
            if (bookingData.fromDate.isNotEmpty())
                fromDateFormEditText.setText(bookingData.fromDate)
            if (bookingData.fromTime.isNotEmpty())
                fromTimeFormEditText.setText(bookingData.fromTime)
            if (bookingData.toDate.isNotEmpty())
                toDateFormEditText.setText(bookingData.toDate)
            if (bookingData.toTime.isNotEmpty())
                toTimeFormEditText.setText(bookingData.toTime)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(bookingData: BookingData): BookingFormFragment {
            return BookingFormFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(BookingFormActivity.EXTRA_BOOKING, bookingData)
                }
            }
        }
    }
}
