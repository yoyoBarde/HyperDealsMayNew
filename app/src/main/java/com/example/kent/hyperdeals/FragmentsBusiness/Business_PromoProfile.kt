package com.example.kent.hyperdeals.FragmentsBusiness

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.util.Log
import com.example.kent.hyperdeals.LoginActivity
import com.example.kent.hyperdeals.MainActivity
import com.example.kent.hyperdeals.Model.PromoModel
import com.example.kent.hyperdeals.MyAdapters.PromoListAdapter
import com.example.kent.hyperdeals.R
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_business__promo_profile.*
import org.jetbrains.anko.doAsync
import java.lang.Exception

class Business_PromoProfile : AppCompatActivity() {
val TAG = "Business_promoProfile"
    var database = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e(TAG,"BUsinessPromasdomoadsmodsad")
        setContentView(R.layout.activity_business__promo_profile)
        try {

            var myPromo =   intent.getParcelableExtra<PromoModel>("message")

            var atay = intent.getParcelableExtra<PromoModel>("object")
            Log.e(TAG, "get intent ${atay.promoStore}")
            PromoListAdapter.promoProfile = atay
        }
        catch (e:Exception){
            print(e)
        }

        buttonBack.setOnClickListener { finish()}
        Picasso.get()
                .load(PromoListAdapter.promoProfile.promoImageLink)
                .placeholder(R.drawable.hyperdealslogofinal)
                .into(iv_promo_image)
        userViewedCategory(PromoListAdapter.promoProfile)
        userViewedSubcategory(PromoListAdapter.promoProfile)


        val viewPager   = findViewById<ViewPager>(R.id.viewPagerBusinessman)
        val tabLayout   = findViewById<TabLayout>(R.id.tabLayout)

        tabLayout.setupWithViewPager(viewPager)
        val adapter = com.example.kent.hyperdeals.MyAdapters.PagerAdapter(supportFragmentManager)
        adapter.addFragments(FragmentPrompDetailsBusiness(),"Details")
        adapter.addFragments(FragmentPromoSaleBusiness(),"Items")

        if(MainActivity.userLog){

            adapter.addFragments(FragmentRelatedPromos(),"Similar Promos")

        }
        else{
            adapter.addFragments(FragmentDashboardBusiness(),"Dashboard")

        }
        viewPager.adapter = adapter

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        Log.e(TAG,"resultCode $resultCode "+data!!.getStringExtra("key"))

        super.onActivityResult(requestCode, resultCode, data)


    }



    fun userViewedSubcategory(myPromo:PromoModel) {
        Log.e(TAG,"userViewedSubcategory")

        for (i in 0 until myPromo.subcategories.size) {
            doAsync {
                var prevCount = 0

                database.collection("UserViewedPreferences").document(LoginActivity.userUIDS).collection("Subcategories").document(myPromo.subcategories[i]).get().addOnCompleteListener {   task ->

                if (task.isSuccessful) {

                    val document = task.result
                    if(document.exists()) {

                        var mySubcategoryPref = document.toObject(UserSubcategoriesPreferencesParcelable::class.java)
                        prevCount = mySubcategoryPref.viewCount + 1
                        Log.e(TAG, "Cached document data: ${document?.data}")

                        var mySubcategoryPreference = UserSubcategoriesPreferences(myPromo.subcategories[i], prevCount)
                        database.collection("UserViewedPreferences").document(LoginActivity.userUIDS).collection("Subcategories").document(myPromo.subcategories[i]).set(mySubcategoryPreference).addOnCompleteListener {


                            Log.e(TAG, "UserViewedPreferences Success")
                        }
                    }
                    else{
                        Log.e(TAG, "Cached get failed: ", task.exception)
                        var mySubcategoryPreference = UserSubcategoriesPreferences(myPromo.subcategories[i],1)
                        database.collection("UserViewedPreferences").document(LoginActivity.userUIDS).collection("Subcategories").document(myPromo.subcategories[i]).set(mySubcategoryPreference).addOnCompleteListener {


                            Log.e(TAG,"UserViewedPreferences Success")
                        }
                    }

                } else {
                    Log.e(TAG, "Cached get failed: ", task.exception)
                    var mySubcategoryPreference = UserSubcategoriesPreferences(myPromo.subcategories[i],1)
                    database.collection("UserViewedPreferences").document(LoginActivity.userUIDS).collection("Subcategories").document(myPromo.subcategories[i]).set(mySubcategoryPreference).addOnCompleteListener {


                        Log.e(TAG,"UserViewedPreferences Success")
                    }
                }

            }


        }
        }

    }
    fun userViewedCategory(myPromo:PromoModel) {


        Log.e(TAG,"userViewedSubcategory")

        myPromo.promoCategories.toSet()
        for (i in 0 until myPromo.categories.size) {
            doAsync {
                var prevCount = 0

                database.collection("UserViewedPreferences").document(LoginActivity.userUIDS).collection("Categories").document(myPromo.categories[i]).get().addOnCompleteListener {   task ->

                    if (task.isSuccessful) {

                        val document = task.result
                        if(document.exists()) {
                            var myCategoryPref = document.toObject(UserCategoriesPreferencesParcelabl::class.java)
                            prevCount = myCategoryPref.viewCount + 1
                            Log.e(TAG, "Cached document data: ${document?.data}")

                            var mySubcategoryPreference = UserCategoriesPreferences(myPromo.categories[i], prevCount)
                            database.collection("UserViewedPreferences").document(LoginActivity.userUIDS).collection("Categories").document(myPromo.categories[i]).set(mySubcategoryPreference).addOnCompleteListener {


                                Log.e(TAG, "UserViewedPreferences Success")
                            }
                        }
                        else{
                            Log.e(TAG, "Initializing Document ", task.exception)
                            var mySubcategoryPreference = UserCategoriesPreferences(myPromo.categories[i],1)
                            database.collection("UserViewedPreferences").document(LoginActivity.userUIDS).collection("Categories").document(myPromo.categories[i]).set(mySubcategoryPreference).addOnCompleteListener {
                                Log.e(TAG,"UserViewedPreferences Success")
                            }
                        }
                    } else {
                        Log.e(TAG, "Cached get failed: ", task.exception)
                        var mySubcategoryPreference = UserCategoriesPreferences(myPromo.categories[i],1)
                        database.collection("UserViewedPreferences").document(LoginActivity.userUIDS).collection("Categories").document(myPromo.categories[i]).set(mySubcategoryPreference).addOnCompleteListener {
                            Log.e(TAG,"UserViewedPreferences Success")
                        }
                    }

                }


            }
        }

    }

    data class UserCategoriesPreferences(
            var CategoryName:String,
            var viewCount:Int

    )

    data class UserSubcategoriesPreferences(
            var subCategoryName:String,
            var viewCount:Int

    )
     class UserSubcategoriesPreferencesParcelable():Parcelable{

         var subCategoryName:String = ""
         var viewCount:Int = 0

         constructor(parcel: Parcel) : this() {
             subCategoryName = parcel.readString()
             viewCount = parcel.readInt()
         }

         override fun writeToParcel(parcel: Parcel, flags: Int) {
             parcel.writeString(subCategoryName)
             parcel.writeInt(viewCount)
         }

         override fun describeContents(): Int {
             return 0
         }

         companion object CREATOR : Parcelable.Creator<UserSubcategoriesPreferencesParcelable> {
             override fun createFromParcel(parcel: Parcel): UserSubcategoriesPreferencesParcelable {
                 return UserSubcategoriesPreferencesParcelable(parcel)
             }

             override fun newArray(size: Int): Array<UserSubcategoriesPreferencesParcelable?> {
                 return arrayOfNulls(size)
             }
         }

     }
    class UserCategoriesPreferencesParcelabl() :Parcelable{
        var CategoryName:String = " "
        var viewCount:Int = 0

        constructor(parcel: Parcel) : this() {
            CategoryName = parcel.readString()
            viewCount = parcel.readInt()
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(CategoryName)
            parcel.writeInt(viewCount)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<UserCategoriesPreferencesParcelabl> {
            override fun createFromParcel(parcel: Parcel): UserCategoriesPreferencesParcelabl {
                return UserCategoriesPreferencesParcelabl(parcel)
            }

            override fun newArray(size: Int): Array<UserCategoriesPreferencesParcelabl?> {
                return arrayOfNulls(size)
            }
        }

    }


}
