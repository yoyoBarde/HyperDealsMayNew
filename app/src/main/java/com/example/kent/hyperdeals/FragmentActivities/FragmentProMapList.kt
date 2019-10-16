package com.example.kent.hyperdeals.FragmentActivities


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.kent.hyperdeals.LoginActivity
import com.example.kent.hyperdeals.Model.*
import com.example.kent.hyperdeals.MyAdapters.PromoListAdapter

import com.example.kent.hyperdeals.R
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragmentpromaplist.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.uiThread
import java.util.*
import kotlin.collections.ArrayList

class FragmentProMapList: Fragment() {
val TAG = "FragmentProMapList"
val distancePoints =3000
val matchedPoints =5000
    var database = FirebaseFirestore.getInstance()
 var userViewedCategoryParceList = ArrayList<UserCategoriesPreferencesParcelabl>()
 var userViewedSubcategoryParceList = ArrayList<UserSubcategoriesPreferencesParcelable>()
  var  myPromolist = ArrayList<PromoModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragmentpromaplist,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lateinit var myPromoDistanceSorted: ArrayList<PromoModel>
        lateinit var myPromoMatchedSorted: ArrayList<PromoModel>
        userViewedCategoryParceList = ArrayList<UserCategoriesPreferencesParcelabl>()
        userViewedSubcategoryParceList = ArrayList<UserSubcategoriesPreferencesParcelable>()
        var percentPoints =  getCriteriaPercent()

        tvClearPreference.setOnClickListener {

            database.collection("UserViewedPreferences").document(LoginActivity.userUIDS).delete().addOnSuccessListener {
            Log.e(TAG,"Success deleted cached for ${LoginActivity.userUIDS}")

                   toast("Success deleting cached")



            }
                    .addOnFailureListener {

                        Log.e(TAG,"Fail deleted cached")
                        toast("Fail deleting cached")

                    }

        }

        for(i in 0 until 100000){
            if(FragmentCategory.promoDistanceSorted.size>0 && FragmentCategory.promoMatchedSorted.size>0 ){

                Log.e(TAG,"delivered ${FragmentCategory.promoDistanceSorted.size} ${FragmentCategory.promoMatchedSorted.size} ")
                myPromoDistanceSorted=FragmentCategory.promoDistanceSorted
                myPromoMatchedSorted =FragmentCategory.promoMatchedSorted
                myPromolist = FragmentCategory.globalPromoList
                setPromoPoints(percentPoints,myPromoDistanceSorted,myPromoMatchedSorted)
                break
            }


        }


    }
    fun setPromoPoints(percentPoints:ArrayList<Double>,promoDistance: ArrayList<PromoModel>,promoMatched: ArrayList<PromoModel>){
        for(i in 0 until percentPoints.size){

            if(i<promoDistance.size){

                promoDistance[i].distancePoints = percentPoints[i]*distancePoints

            }
            if(i<promoMatched.size){
                if(i>0) {
                    if (promoMatched[i].preferenceMatched == promoMatched[i - 1].preferenceMatched) {
                        promoMatched[i].matchedPoints = promoMatched[i - 1].matchedPoints *matchedPoints
                    }
                    else {
                        promoMatched[i].matchedPoints = percentPoints[i]*matchedPoints
                    }
                }
                else {
                    promoMatched[i].matchedPoints = percentPoints[i]*matchedPoints
                }
            }


try {
    Log.e(TAG,"Loop $i ${ promoMatched[i].promoname} ${ promoMatched[i].matchedPoints} and ${promoDistance[i].promoname} got ${promoDistance[i].distancePoints} ")


}
catch(e:IndexOutOfBoundsException){
    Log.e(TAG,"Exception $e")

}
        }

        getUserViewedPreference(promoDistance,promoMatched)



    }

    private fun transferToGlobalPromoMatched(promoMatched: ArrayList<PromoModel>) {
       for(i in 0 until promoMatched.size){
      for(j in 0 until myPromolist.size){
          if(promoMatched[i].promoID.matches(myPromolist[j].promoID.toRegex())){
          myPromolist[j].matchedPoints = promoMatched[i].matchedPoints
          myPromolist[j].distancematchedPoints = myPromolist[j].distancematchedPoints +    myPromolist[j].matchedPoints
          }
      }

       }
    }
    fun transferToGlobalPromoDistance(promoDistance: ArrayList<PromoModel>){

        for(i in 0 until promoDistance.size){
            for(j in 0 until myPromolist.size){
                if(promoDistance[i].promoID.matches(myPromolist[j].promoID.toRegex())){
                    myPromolist[j].distancePoints = promoDistance[i].distancePoints
                    myPromolist[j].distancematchedPoints = myPromolist[j].distancematchedPoints + myPromolist[j].distancePoints

                }
            }

        }

    }

    fun getCriteriaPercent():ArrayList<Double>{
        var percentPoints = ArrayList<Double>()

        percentPoints.add(.081)
        percentPoints.add(.076)
        percentPoints.add(.071)
        percentPoints.add(.065)
        percentPoints.add(.061)
        percentPoints.add(.058)
        percentPoints.add(.055)
        percentPoints.add(.053)
        percentPoints.add(.050)
        percentPoints.add(.047)
        percentPoints.add(.044)
        percentPoints.add(.042)
        percentPoints.add(.039)
        percentPoints.add(.036)
        percentPoints.add(.034)
        percentPoints.add(.031)
        percentPoints.add(.028)
        percentPoints.add(.025)
        percentPoints.add(.023)
        percentPoints.add(.020)


        return percentPoints



    }


    fun getUserViewedPreference(promoDistance:ArrayList<PromoModel>,promoMatched: ArrayList<PromoModel>){
        transferToGlobalPromoDistance(promoDistance)
        transferToGlobalPromoMatched(promoMatched)
        doAsync {

database.collection("UserViewedPreferences").document(LoginActivity.userUIDS).collection("Categories").get().addOnCompleteListener { task ->
    if (task.isSuccessful) {
        for (DocumentSnapshot in task.result) {
            Log.e(TAG,"${DocumentSnapshot.data}")
            var categoriesParce = DocumentSnapshot.toObject(UserCategoriesPreferencesParcelabl::class.java)
            userViewedCategoryParceList.add(categoriesParce)

        }
        Log.e(TAG,"UserViewedPreferencesCategory ${userViewedCategoryParceList.size}")

        transferGlobalUserViewPreferenceCategory()


        }
    else{
        Log.e(TAG,"UserViewedPreferences Fails  ")
    }

    }
}

        doAsync {

            database.collection("UserViewedPreferences").document(LoginActivity.userUIDS).collection("Subcategories").get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (DocumentSnapshot in task.result) {
                        var subcategoriesParce = DocumentSnapshot.toObject(UserSubcategoriesPreferencesParcelable::class.java)
                        userViewedSubcategoryParceList.add(subcategoriesParce)

                    }
                    Log.e(TAG,"UserViewedPreferencesSubcategory ${userViewedSubcategoryParceList.size}")
                    transferGlobalUserViewPreferenceSubcategory()
                }

            }
        }
}
    fun transferGlobalUserViewPreferenceCategory() {
        for (i in 0 until myPromolist.size) {
            for (j in 0 until userViewedCategoryParceList.size) {
                if (myPromolist[i].categories.contains(userViewedCategoryParceList[j].CategoryName)) {
                    myPromolist[i].categoryPoints = myPromolist[i].categoryPoints + userViewedCategoryParceList[j].viewCount
                    Log.e(TAG,"${ myPromolist[i].promoID}   ${userViewedCategoryParceList[j].CategoryName} ${myPromolist[i].promoname} categoryPoints  ${ myPromolist[i].categoryPoints}")
                }
            }
        }
    }
    fun transferGlobalUserViewPreferenceSubcategory() {
        for (i in 0 until myPromolist.size) {
                for ( j in 0 until userViewedSubcategoryParceList.size) {
                    if(myPromolist[i].subcategories.contains(userViewedSubcategoryParceList[j].subCategoryName)) {
                        myPromolist[i].subcategoryPonts = myPromolist[i].subcategoryPonts + userViewedSubcategoryParceList[j].viewCount
                        Log.e(TAG, "${myPromolist[i].promoID}   ${userViewedSubcategoryParceList[j].subCategoryName} ${myPromolist[i].promoname}  subcategoryPoints  ${myPromolist[i].subcategoryPonts}")

                    }
                }

            }
        sortFinalRecommendedList()
        }

    fun sortFinalRecommendedList(){

        var sortedList = myPromolist.sortedWith(compareByDescending<PromoModel>{it.subcategoryPonts}.thenByDescending { it.categoryPoints  }.thenByDescending {it.distancematchedPoints})
        var sortedRecycler2List = ArrayList<PromoModel>()
        var sortedRecycler3List = ArrayList<PromoModel>()

        //  val sortedList = myPromolist.sortedWith(compareBy({ it.subcategoryPonts }, { it.categoryPoints },{it.distancematchedPoints} ))
        for(i in 0 until sortedList.size){
            Log.e(TAG,"Recommendation Ranked ${i+1} ${sortedList[i].promoname} ${sortedList[i].subcategoryPonts} ${sortedList[i].categoryPoints} ${sortedList[i].distancematchedPoints}")


            if(i==0){

                Picasso.get()
                        .load(sortedList[i].promoImageLink)
                        .placeholder(R.drawable.hyperdealslogofinal)
                        .into(PromoImage)

                PromoStore.setText(sortedList[i].promoStore)
                PromoTitle.setText(sortedList[i].promoname)
                PromoDescription.setText(sortedList[i].promodescription)
                PromoPlace.setText(sortedList[i].promoPlace)
                PromoConctact.setText(sortedList[i].promoContactNumber)

            }
            else{
               if(i<3) {
                   sortedRecycler2List.add(sortedList[i])

               }
                else{
                   if(i<20){
                       sortedRecycler3List.add(sortedList[i])
                   }


               }


            }

        }

        setAdapter2(sortedRecycler2List)
        setAdapter3(sortedRecycler3List)

    }


    fun setAdapter3(promoList:ArrayList<PromoModel>){
        var mSelected: SparseBooleanArray = SparseBooleanArray()

        recyclerView3.layoutManager = LinearLayoutManager(activity!!, LinearLayout.VERTICAL,false)
        recyclerView3.adapter =  PromoListAdapter(activity!!,mSelected, promoList)
        recyclerView3.itemAnimator = DefaultItemAnimator()


    }
    fun setAdapter2(promoList:ArrayList<PromoModel>){
        var mSelected: SparseBooleanArray = SparseBooleanArray()
        recyclerView2.layoutManager = GridLayoutManager(activity!!,2)
        recyclerView2.adapter =  PromoListAdapter(activity!!,mSelected, promoList)
        recyclerView2.itemAnimator = DefaultItemAnimator()


    }

    override fun onPause() {
        Log.e(TAG,"onPause")
        super.onPause()
    }

    override fun onDestroy() {
        Log.e(TAG,"onDestroy")

        super.onDestroy()
    }

    override fun onAttach(context: Context?) {
        Log.e(TAG,"onAttach")

        super.onAttach(context)

    }

    override fun onDetach() {
        Log.e(TAG,"onDetach")

        super.onDetach()
    }

    override fun onStop() {
        Log.e(TAG,"onStop")

        super.onStop()
    }

    override fun onResume() {
        Log.e(TAG,"onResume")

        super.onResume()
    }


    }
