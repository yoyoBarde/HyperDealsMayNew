package com.example.kent.hyperdeals.BusinessActivities

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import com.example.kent.hyperdeals.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.zaddpromobusinessman.*
import android.webkit.MimeTypeMap
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.*
import org.jetbrains.anko.toast
import java.util.*
import android.util.Log
import android.view.View
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.GeoPoint
import android.text.format.DateFormat;
import android.widget.*
import com.example.kent.hyperdeals.FragmentActivities.DialogFragmentAddCategoryBusiness
import com.example.kent.hyperdeals.LoginActivity
import com.example.kent.hyperdeals.LoginActivityBusinessman
import com.example.kent.hyperdeals.Model.*
import com.example.kent.hyperdeals.MyAdapters.CategoryAdapterBusiness
import com.example.kent.hyperdeals.MyAdapters.SelectedSubcategoryAdapterBusiness
import org.jetbrains.anko.doAsync
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList


class AddPromo : AppCompatActivity(),DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener, myInterfaces {

    var globalPromoStore = " "
    companion object {
        lateinit var recyclerViewSub:RecyclerView   
        var globalCategorylist=ArrayList<CategoryParse>()
    }
    lateinit var myStoreCategories:ArrayList<String>
    var storeList = ArrayList<StoreModel>()
    lateinit var subcategoryList:ArrayList<String>
    var globalSubcategoryStringList = ArrayList<String>()
    val database = FirebaseFirestore.getInstance()
    private var mImageLink : UploadTask.TaskSnapshot?=null
    var ref=FirebaseDatabase.getInstance().getReference("Geofences")
    var geoFire: GeoFire=GeoFire(ref)
    var  myGeolocation:GeoLocation= GeoLocation(2.2,2.2)
    val PICK_IMAGE_REQUEST = 11
    var imageUri: Uri?=null
    val place:PlaceSelectionListener?=null
    val TAG = "AddPromo"
    var myDate:Date?=null
    var year:Int? =null
    var month:Int? =null
    var day:Int? = null
    var hour:Int? = null
    var mystartDate:TextView?=null
    var myendDate:TextView?=null
    var selectedEndorStart =0

    var startDateCalendar=Calendar.getInstance()
    var endDateCalendar=Calendar.getInstance()
    lateinit var myAdapter: CategoryAdapterBusiness

    var categoryList=ArrayList<CategoryParse>()

    var startDateYear:Int?=null
    var startDateMonth:Int?=null
    var startDateDay:Int?=null
    var endDateYear:Int?=null
    var endDateMonth:Int?=null
    var endDateDay:Int?=null
    var startTimeHour:Int?=null
    var startTimeMinute:Int?=null
    var endTimeHour:Int?=null
    var endTimeMinute:Int?=null

    lateinit var newFragment:DialogFragmentAddCategoryBusiness

    private var mStorage: FirebaseStorage?=null
    private var mStorageReference : StorageReference?=null
    private var mFirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.zaddpromobusinessman)
        getStores2()
        newFragment = DialogFragmentAddCategoryBusiness().newInstance()
        getCategories()
        recyclerViewSub = findViewById<RecyclerView>(R.id.Recycler_selectedSubcategory)
        var  selectedSubAdapter = SelectedSubcategoryAdapterBusiness(this, arrayListOf(""))
        var myStagger = StaggeredGridLayoutManager(3, LinearLayoutManager.HORIZONTAL)
        recyclerViewSub.layoutManager = myStagger
        recyclerViewSub.adapter = selectedSubAdapter





        addPromoImage.setOnClickListener{
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Image"),  PICK_IMAGE_REQUEST)
        }

        addPromoPublish.setOnClickListener{

            uploadFile()


        }

        btn_add_categoru.setOnClickListener {
            showDialog()

        }



        val autocompleteFragment = fragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as PlaceAutocompleteFragment

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {


                val getlat= place.latLng.latitude.toString()
                val getlong = place.latLng.longitude.toString()

                val getPlaceName = place.name.toString()

                addPromoLocation.setText(getPlaceName)

                addPromoPlace.setText(getlat + "," + getlong)
                myGeolocation = GeoLocation(place.latLng.latitude,place.latLng.longitude)
            }

            override fun onError(status: Status) {
                toast("Error")
            }
        })



        startDate.setOnClickListener {
            selectedEndorStart = 1
            var myCalendar = Calendar.getInstance()
            year = myCalendar.get(Calendar.YEAR)
            month = myCalendar.get(Calendar.MONTH)
            day = myCalendar.get(Calendar.DAY_OF_MONTH)
            myDate = myCalendar.time



            var myTimePicker = DatePickerDialog(this,this, year!!,month!!,day!!)
            myTimePicker.show()


        }

        endDate.setOnClickListener {
            selectedEndorStart = 2
            var myCalendar = Calendar.getInstance()
            year = myCalendar.get(Calendar.YEAR)
            month = myCalendar.get(Calendar.MONTH)
            day = myCalendar.get(Calendar.DAY_OF_MONTH)
            myDate = myCalendar.time
            myCalendar.time

            var myTimePicker = DatePickerDialog(this,this, year!!,month!!,day!!)
            myTimePicker.show()


        }


        startTime.setOnClickListener {

            selectedEndorStart = 3
            var myTimepicker =  TimePickerDialog(this,this,8,0,DateFormat.is24HourFormat(this))
            myTimepicker.show()
        }
        endTime.setOnClickListener {

            selectedEndorStart = 4
            var myTimepicker =  TimePickerDialog(this,this,5,0,DateFormat.is24HourFormat(this))
            myTimepicker.show()
        }

    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        Log.e(TAG,"$p1/$p2/$p3")

        var myCalendar  = Calendar.getInstance()
        myCalendar.set(p1,p2,p3)
        var  dateFormat = SimpleDateFormat("MM/dd/yyyy")
        val myDate:Date = myCalendar.time


        if(selectedEndorStart==1){
            startDateYear = p1
            startDateMonth = p2+1
            startDateDay = p3

            startDateCalendar!!.set(p1,p2,p3  )
            Log.e(TAG,"${startDateCalendar.time}")
            var currentCalendar = Calendar.getInstance()
            if(currentCalendar!!.timeInMillis>startDateCalendar!!.timeInMillis){
                Toast.makeText(this,"Start Date should be further than today",Toast.LENGTH_LONG).show()

            }
            else
                startDate.text = dateFormat.format(myDate)

        }
        else if (selectedEndorStart == 2){
            endDateYear = p1
            endDateMonth = p2+1
            endDateDay = p3

            endDateCalendar!!.set(p1,p2,p3)

            if(startDateCalendar!!.timeInMillis>endDateCalendar!!.timeInMillis){
                Toast.makeText(this,"End Date should be further than Start Date",Toast.LENGTH_LONG).show()
            }
            else {
                endDate.text = dateFormat.format(myDate)
            }
        }

    }

    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
        var amorpm = "am"

        if(selectedEndorStart==3){
            startTimeHour = p1
            startTimeMinute = p2
            if(p1==0)
                p1 -1
            if(p1>12) {
                amorpm = "pm"
                p1 - 12

            }
            startTime.text = "$p1:$p2 $amorpm"

        }
        else if (selectedEndorStart==4)
        {
            endTimeHour = p1
            endTimeMinute = p2
            endTime.text = "$p1:$p2 $amorpm"
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, dataa: Intent?) {
        super.onActivityResult(requestCode, resultCode, dataa)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && dataa != null){
            imageUri = dataa.data

            Picasso.get().load(imageUri).into(addPromoImage)


        }
    }



    private fun uploadFile() {

        mStorage = FirebaseStorage.getInstance()
        mStorageReference = mStorage!!.reference

        if (imageUri!=null){
            val ref = mStorageReference!!.child("images/" + UUID.randomUUID().toString())
            ref.putFile(imageUri!!)
                    .addOnSuccessListener {

                        addPromoProgressBar.visibility = View.VISIBLE

                        val image = it.downloadUrl.toString()

                        addPromoImageLink.setText(image)

                        toast("Image Uploaded Successfully")
                        storeDatatoFirestore()
                finish()
                        addPromoProgressBar.visibility = View.INVISIBLE

                    }
                    .addOnFailureListener{
                        toast("Uploading Failed")
                    }

        }




    }
    private fun addGeofence(key:String,location:GeoLocation){
        geoFire.setLocation(key,location, GeoFire.CompletionListener { key, error ->

            Log.d("HyperDeals",key)


        })


    }
    override fun saveCategoriesBusiness(myCategoryList: ArrayList<CategoryParse>) {


        globalCategorylist = myCategoryList

            Log.e(TAG, "saveCategoriesBusiness")
         subcategoryList = ArrayList<String>()
            for (i in 0 until myCategoryList.size) {
                Log.e(TAG, "loop1")

                for (j in 0 until myCategoryList[i].Subcategories.size) {
                    Log.e(TAG, "loop2")
                    if (myCategoryList[i].Subcategories[j].Selected) {
                        subcategoryList.add(myCategoryList[i].Subcategories[j].SubcategoryName)
                    }
                }


            }
            globalSubcategoryStringList = subcategoryList
            for (k in 0 until subcategoryList.size) {

                Log.e(TAG, "selected - " + subcategoryList[k])
            }
setAdapter(subcategoryList)


    }

    private fun storeDatatoFirestore(){
        addPromoProgressBar.visibility = View.VISIBLE
        Log.d("HyperDeals",subsubTag.text.toString())
        val pStore = et_storeName.text.toString()
        val pContact = addPromoContact.text.toString()
        val pName = addPromoName.text.toString()
        val pDescription = addPromoDescription.text.toString()
        val pLatLng = addPromoPlace.text.toString()
        val pPromoPlace = addPromoLocation.text.toString()
        val pPromoImageLink = addPromoImageLink.text.toString()
Log.e(TAG,"$pName $pStore $pContact")
        val pEntity = PromoModelBusinessman("asdasd", pStore, pContact, pDescription, pPromoPlace, pName, pLatLng, pPromoImageLink,
                GeoPoint(myGeolocation.latitude, myGeolocation.longitude), subsubTag.text.toString(), 0, 0, 0, 0
                , startDateYear!!
                , startDateMonth!!
                , startDateDay!!
                , endDateYear!!
                , endDateMonth!!
                , endDateDay!!
                , startTimeHour!!
                , startTimeMinute!!
                , endTimeHour!!
                , endTimeMinute!!

        )
        pEntity.approved = false
        pEntity.posterBy = LoginActivityBusinessman.userBusinessManUsername
        pushtoDataBsae(pStore)

        mFirebaseFirestore.collection("PromoDetails").document(pStore).set(pEntity)
        toast("Success")
        addPromoProgressBar.visibility = View.INVISIBLE
        Log.d("HyperDeals",myGeolocation.latitude.toString()+myGeolocation.longitude.toString())
        addGeofence(pStore,myGeolocation)

    }



    fun getCategories() {
        doAsync {
            database.collection("Categories").get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (DocumentSnapshot in task.result) {
                        var uploaded = DocumentSnapshot.toObject(CategoryParse::class.java)
                        Log.e(TAG, DocumentSnapshot.getId() + " => " + DocumentSnapshot.getData())
                        database.collection("Categories").document(DocumentSnapshot.id).collection("Subcategories").get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                for (DocumentSnapshot in task.result) {
                                    var upload = DocumentSnapshot.toObject(SubcategoryParse::class.java)
                                    Log.e(TAG, " Subcategory - " + upload.toString())
                                    uploaded.Subcategories.add(upload)


                                }


                            } else
                                toast("error")
                        }

                        categoryList.add(uploaded)
                        globalCategorylist.add(uploaded)

                    }

//
//                    for(i in 0 until  categoryList.size){
//                        doAsync {
//                            for (j in 0 until categoryList[i].Subcategories.size){
//                                doAsync {
//                                    categoryList[i].Subcategories[j].Selected = true
//                                }
//                            }
//
//
//
//                        }
//
//
//                    }




                } else
                    toast("error")
            }


        }

    }
    fun setAdapter(myList:ArrayList<String>){
        var  selectedSubAdapter = SelectedSubcategoryAdapterBusiness(this,subcategoryList)

Log.e(TAG,"${myList.size}")


        var myStagger = StaggeredGridLayoutManager(3, LinearLayoutManager.HORIZONTAL)
        recyclerViewSub.layoutManager = myStagger
            recyclerViewSub.adapter = selectedSubAdapter
    }
    fun pushtoDataBsae(PromoStore:String){
var myArrayStringSub = myStoreCategories

        Log.e(TAG,"time for push ${myArrayStringSub.size}")
        for(k in 0 until myArrayStringSub.size){
            doAsync {
                val Subcategory = HashMap<String, Any>()
                Subcategory["SubcategoryName"] = myArrayStringSub[k]


                mFirebaseFirestore.collection("PromoCategories").document(PromoStore).collection("Subcategories")
                        .document().set(Subcategory)
                        .addOnSuccessListener { Log.e(TAG, "DocumentSnapshot successfully written!") }
                        .addOnFailureListener { e -> Log.e(TAG, "Error  writing document", e) }
            }

        }
    }
    private fun showDialog() {
        AddStore.Store = false
        var myDialog = DialogFragmentAddCategoryBusiness()
        myDialog.show(fragmentManager,"myCustomDialog")
    }

    fun setSpinner(stringlist:ArrayList<String>){
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, stringlist)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mySpinner.adapter = aa


        mySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
             //   globalPromoStore = LoginActivityBusinessman.globalUserBusinessman.stores[0]

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
         //       globalPromoStore = LoginActivityBusinessman.globalUserBusinessman.stores[position]
//                Log.e(TAG," selected ${ LoginActivityBusinessman.globalUserBusinessman.stores[position]}")
                for(i in 0 until storeList.size){
                    if(storeList[i].storeName==stringlist[position]){
                        changeFields(storeList[i])

                    }
                    else {

                    }

                }
            }

        }
    }
    fun noneFields(){
        addPromoPublish.visibility = View.GONE
    }
    fun changeFields(myStore:StoreModel){
        myGeolocation =GeoLocation( myStore.storeLatLng.latitude,myStore.storeLatLng.longitude)
        addPromoPublish.visibility = View.VISIBLE

        et_storeName.setText(myStore.storeName)
        addPromoLocation.setText(myStore.storeAddress)
        addPromoContact.setText(myStore.storeContact)

        myStoreCategories = ArrayList<String>()
        myStoreCategories = myStore.storeCategories
        var  selectedSubAdapter = SelectedSubcategoryAdapterBusiness(this,myStore.storeCategories)
        var myStagger = StaggeredGridLayoutManager(3, LinearLayoutManager.HORIZONTAL)
        recyclerViewSub.layoutManager = myStagger
        recyclerViewSub.adapter = selectedSubAdapter

    }

    fun getStores2() {
        var spinerList = arrayListOf<String>("None")
        doAsync {
            database.collection("UserBusinessman").document(LoginActivity.userUIDS).collection("Stores").get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (DocumentSnapshot in task.result) {


                        val storeName = DocumentSnapshot.data.get("storeName").toString()
                        val storeDescription = DocumentSnapshot.data.get("storeDescription").toString()
                        val storeAddress = DocumentSnapshot.data.get("storeAddress").toString()
                        val storeBy  = DocumentSnapshot.data.get("storeBy").toString()
                        val storeContact= DocumentSnapshot.data.get("storeContact").toString()
                        val storeLink = DocumentSnapshot.data.get("storeLink").toString()
                        val storeOpenTime  = DocumentSnapshot.data.get("storeOpenTime").toString()
                        val storeCloseTime = DocumentSnapshot.data.get("storeCloseTime").toString()
                        val storeCategories = DocumentSnapshot.data.get("storeCategories") as ArrayList<String>
                        var storeLatLng = DocumentSnapshot.data.get("storeLatLng") as GeoPoint
                        val storeImage  = DocumentSnapshot.data.get("storeImage").toString()

                        val myStore  = StoreModel(storeImage,storeName,storeContact,storeDescription
                                ,storeLink, storeLatLng ,storeAddress, storeCategories,storeOpenTime,storeCloseTime,storeBy)
                        storeList.add(myStore)
                        spinerList.add(storeName)
                        Log.e(TAG,myStore.toString())
//                        Log.e(TAG,myStore.toString())
                    }
                    setSpinner(spinerList)


                }
            }

        }
    }

}


