package com.example.trello.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.ActivityChooserView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trello.Dialogs.LabelColorListDialog
import com.example.trello.Dialogs.MembersListDialog
import com.example.trello.R
import com.example.trello.adapters.CardMembersListItemsAdapter
import com.example.trello.adapters.LabelColorListItemsAdapter
import com.example.trello.firebase.FirestoreClass
import com.example.trello.models.*
import com.example.trello.utils.Constants
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class CardDetailsActivity : BaseActivity() {
    private lateinit var mBoardDetails:Board
    private var mtasklistposition = -1
    private var mcardlistposition = -1
    private var mSelectedColor:String=""
    private lateinit var mMembersDetailsList:ArrayList<User>
    private var mSelectedDueDateMilliSeconds:Long=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)
        getIntentData()
        setupActionBar()

        findViewById<Button>(R.id.btn_update_card_details).setOnClickListener {
            if(findViewById<EditText>(R.id.et_name_card_details).text.toString().isNotEmpty()){
                updateCardDetails()
            }
            else{
                Toast.makeText(this,"PLease enter a Card name",Toast.LENGTH_SHORT).show()
            }
        }
        findViewById<TextView>(R.id.tv_select_label_color).setOnClickListener {
            labelColorsListDialog()
        }
        findViewById<TextView>(R.id.tv_select_members).setOnClickListener {
            membersListDialog()
        }
        mSelectedDueDateMilliSeconds = mBoardDetails.taskList[mtasklistposition].cards[mcardlistposition].dueDate
        if(mSelectedDueDateMilliSeconds>0){
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val selectedDate = simpleDateFormat.format(Date(mSelectedDueDateMilliSeconds))
            findViewById<TextView>(R.id.tv_select_due_date).text = selectedDate
        }
        findViewById<TextView>(R.id.tv_select_due_date).setOnClickListener {
            showDatePicker()

        }

        var name_card = findViewById<EditText>(R.id.et_name_card_details)
        name_card.setText(mBoardDetails.taskList[mtasklistposition].cards[mcardlistposition].name)
        name_card.setSelection(name_card.text.toString().length)
        mSelectedColor = mBoardDetails.taskList[mtasklistposition].cards[mcardlistposition].labelcolor
        if(mSelectedColor.isNotEmpty()){
            setColor()
        }
        setUpSelectedMembersList()
    }
    private fun setupActionBar(){
        val toolbar = findViewById<Toolbar>(R.id.toolbar_card_details_activity)
        setSupportActionBar(toolbar)
        val actionbar = supportActionBar
        if(actionbar!=null){
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
            actionbar.title = mBoardDetails.taskList[mtasklistposition].cards[mcardlistposition].name
        }
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
    private fun getIntentData(){
        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }
        if(intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)){
            mtasklistposition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION,-1)

        }
        if(intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)){
            mcardlistposition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION,-1)
        }
        if(intent.hasExtra(Constants.BOARD_MEMBERS_LIST)){
            mMembersDetailsList = intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_LIST)!!
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_delete_card ->{
                alertDialogForDeleteCard(mBoardDetails.taskList[mtasklistposition].cards[mcardlistposition].name)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun addUpdateTaskListSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
    private fun updateCardDetails(){
        val name = findViewById<EditText>(R.id.et_name_card_details)
        val card = Card(
            name.text.toString(),
            mBoardDetails.taskList[mtasklistposition].cards[mcardlistposition].createdBy,
            mBoardDetails.taskList[mtasklistposition].cards[mcardlistposition].assignedTo
            ,mSelectedColor,mSelectedDueDateMilliSeconds
        )
        val taskList:ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)

        mBoardDetails.taskList[mtasklistposition].cards[mcardlistposition] = card
        showProgressDialog("Please Wait...")
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity,mBoardDetails)
    }

    private fun deleteCard(){
        var cardList:ArrayList<Card> = mBoardDetails.taskList[mtasklistposition].cards
        cardList.removeAt(mcardlistposition)
        val taskList : ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)
        taskList[mtasklistposition].cards = cardList
        showProgressDialog("Please Wait...")
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity,mBoardDetails)

    }
    private fun alertDialogForDeleteCard(cardName:String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert")
        builder.setMessage("Are you sure you want to delete card ${cardName}")
        builder.setIcon(R.drawable.alert_dialog_icon)
        builder.setPositiveButton("Yes"){dialogInterFace, which ->
            dialogInterFace.dismiss()
            deleteCard()
        }
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss()
        }
        val alertDialog:AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
    private fun colorsList():ArrayList<String>{
        val colorList:ArrayList<String> = ArrayList()
        colorList.add("#43C86F")
        colorList.add("#0C90F1")
        colorList.add("#F72400")
        colorList.add("#7A8089")
        colorList.add("#D57C1D")
        colorList.add("#770000")
        colorList.add("#0022F8")
        return colorList
    }
    private fun setColor(){
        findViewById<TextView>(R.id.tv_select_label_color).text = ""
        findViewById<TextView>(R.id.tv_select_label_color).setBackgroundColor(Color.parseColor(mSelectedColor))
    }
    private fun labelColorsListDialog(){
        val colorList:ArrayList<String> = colorsList()
        val listdialog = object:LabelColorListDialog(this,colorList,"Select Label Color ",mSelectedColor){
            override fun onItemSelected(color: String) {
                mSelectedColor = color
                setColor()
            }
        }
        listdialog.show()
    }

    private fun membersListDialog(){
        var cardAssignedMembersList = mBoardDetails.taskList[mtasklistposition].cards[mcardlistposition].assignedTo
        if(cardAssignedMembersList.size>0){
            for(i in mMembersDetailsList.indices){
                for(j in cardAssignedMembersList){
                    if(mMembersDetailsList[i].id==j){
                        mMembersDetailsList[i].selected = true
                    }
                }
            }
        }
        else{
            for(i in mMembersDetailsList.indices){
                mMembersDetailsList[i].selected = false
            }
        }
        val listDailog = object :MembersListDialog(
            this,mMembersDetailsList,"Select Member"
        ){
            override fun onItemSelected(user: User, action: String) {
                if(action==Constants.SELECT){
                    if(!mBoardDetails.taskList[mtasklistposition].cards[mcardlistposition].assignedTo.contains(user.id)){
                        mBoardDetails.taskList[mtasklistposition].cards[mcardlistposition].assignedTo.add(user.id)
                    }
                }
                else{
                    mBoardDetails.taskList[mtasklistposition].cards[mcardlistposition].assignedTo.remove(user.id)
                    for(i in mMembersDetailsList.indices){
                        if(mMembersDetailsList[i].id==user.id){
                            mMembersDetailsList[i].selected=false
                        }
                    }
                }
                setUpSelectedMembersList()

            }

        }
        listDailog.show()
    }
    private fun setUpSelectedMembersList(){
        val cardAssignedMembersList = mBoardDetails.taskList[mtasklistposition].cards[mcardlistposition].assignedTo
        val selectedMembersList:ArrayList<SelectedMembers> = ArrayList()
        for(i in mMembersDetailsList.indices){
            for(j in cardAssignedMembersList){
                if(mMembersDetailsList[i].id==j){
                    val selectedMember = SelectedMembers(
                    mMembersDetailsList[i].id,
                        mMembersDetailsList[i].image
                    )
                    selectedMembersList.add(selectedMember)
                }
            }
        }
        if(selectedMembersList.size>0){
            selectedMembersList.add(SelectedMembers("",""))
            findViewById<TextView>(R.id.tv_select_members).visibility = View.GONE
            findViewById<RecyclerView>(R.id.rv_selected_members_list).visibility = View.VISIBLE
            findViewById<RecyclerView>(R.id.rv_selected_members_list).layoutManager = GridLayoutManager(this,6)
            val adapter = CardMembersListItemsAdapter(this,selectedMembersList,true)
            findViewById<RecyclerView>(R.id.rv_selected_members_list).adapter=adapter
            adapter.setOnClickListener(
                object: CardMembersListItemsAdapter.OnClickListener{
                    override fun onClick() {
                        membersListDialog()
                    }
                }
            )
        }
        else{
            findViewById<TextView>(R.id.tv_select_members).visibility = View.VISIBLE
            findViewById<RecyclerView>(R.id.rv_selected_members_list).visibility = View.GONE
        }
    }
    private fun showDatePicker(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(this,
            { view,year,monthOfYear,dayOfMonth->
                val sDayOfMonth =
                    if(dayOfMonth<10) "0$dayOfMonth" else "$dayOfMonth"
                val sMonthOfYear =
                    if(monthOfYear+1<10) "0${monthOfYear+1}" else "${monthOfYear+1}"
                val selectedDate = "$sDayOfMonth/$sMonthOfYear/$year"
                findViewById<TextView>(R.id.tv_select_due_date).text = selectedDate
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                val theDate = sdf.parse(selectedDate)
                mSelectedDueDateMilliSeconds = theDate!!.time
                },year,month,day
            )
        dpd.show()
    }
}