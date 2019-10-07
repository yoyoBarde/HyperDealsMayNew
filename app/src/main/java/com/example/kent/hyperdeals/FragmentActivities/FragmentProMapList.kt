package com.example.kent.hyperdeals.FragmentActivities


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

                promoDistance[i].distancePoints = percentPoints[i]

            }
            if(i<promoMatched.size){
                if(i>0) {
                    if (promoMatched[i].preferenceMatched == promoMatched[i - 1].preferenceMatched) {
                        promoMatched[i].matchedPoints = promoMatched[i - 1].matchedPoints
                    }
                    else {
                        promoMatched[i].matchedPoints = percentPoints[i]
                    }
                }
                else {
                    promoMatched[i].matchedPoints = percentPoints[i]
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
        percentPoints.add(8.1)
        percentPoints.add(7.6)
        percentPoints.add(7.1)
        percentPoints.add(6.5)
        percentPoints.add(6.1)
        percentPoints.add(5.8)
        percentPoints.add(5.5)
        percentPoints.add(5.3)
        percentPoints.add(5.0)
        percentPoints.add(4.7)
        percentPoints.add(4.4)
        percentPoints.add(4.2)
        percentPoints.add(3.9)
        percentPoints.add(3.6)
        percentPoints.add(3.4)
        percentPoints.add(3.1)
        percentPoints.add(2.8)
        percentPoints.add(2.5)
        percentPoints.add(2.3)
        percentPoints.add(2.0)

return percentPoints



    }

    }
