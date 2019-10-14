package com.example.kent.hyperdeals.FragmentActivities


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kent.hyperdeals.Model.PromoModel

import com.example.kent.hyperdeals.R

class FragmentProMapList: Fragment() {
val TAG = "FragmentProMapList"
val distancePoints =3000
val matchedPoints =5000

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragmentpromaplist,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lateinit var myPromoDistanceSorted: ArrayList<PromoModel>
        lateinit var myPromoMatchedSorted: ArrayList<PromoModel>


       var percentPoints =  getCriteriaPercent()

        for(i in 0 until 1000){
            Log.e(TAG,"Loop ${i}")
            if(FragmentCategory.promoDistanceSorted.size>0 && FragmentCategory.promoMatchedSorted.size>0 ){

                Log.e(TAG,"delivered ${FragmentCategory.promoDistanceSorted.size} ${FragmentCategory.promoMatchedSorted.size} ")
                myPromoDistanceSorted=FragmentCategory.promoDistanceSorted
                myPromoMatchedSorted =FragmentCategory.promoMatchedSorted

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
