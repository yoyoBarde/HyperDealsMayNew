package com.example.kent.hyperdeals.FragmentActivities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.util.SparseBooleanArray
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kent.hyperdeals.FragmentsBusiness.Business_PromoProfile
import com.example.kent.hyperdeals.MyAdapters.PromoListAdapter
import com.example.kent.hyperdeals.Model.PromoModel
import com.example.kent.hyperdeals.Interface.RecyclerTouchListener
import com.example.kent.hyperdeals.LoginActivity
import com.example.kent.hyperdeals.LoginActivityBusinessman

import com.example.kent.hyperdeals.R
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialogbox_businessman.*
import kotlinx.android.synthetic.main.fragment_fragment_reach.*
import org.jetbrains.anko.support.v4.toast


class FragmentReach : Fragment() {

    private var myDialogBusiness: Dialog ?=null
    private var myDialog: Dialog? = null
    private var promolist = ArrayList<PromoModel>()
    private var mAdapter : PromoListAdapter? = null
    private var mSelected: SparseBooleanArray = SparseBooleanArray()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_fragment_reach, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        promolist = ArrayList()

        val database = FirebaseFirestore.getInstance()

        val layoutManager = LinearLayoutManager(context)
        recyclerViewReach.layoutManager = layoutManager
        recyclerViewReach.itemAnimator = DefaultItemAnimator()


        database.collection("PromoDetails").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (DocumentSnapshot in task.result) {
                    val upload = DocumentSnapshot.toObject(PromoModel::class.java)
                    Log.d(FragmentProMap.TAG, DocumentSnapshot.getId() + " => " + DocumentSnapshot.getData())
                    promolist.add(upload)
                    toast("success")


                }
                Log.e("atay","MANA")
                setAdapter(promolist)


            } else
                toast("error")
        }



        recyclerViewReach.addOnItemTouchListener(RecyclerTouchListener(this.context!!.applicationContext, object : RecyclerTouchListener.ClickListener {
            @SuppressLint("SetTextI18n")
            override fun onClick(view: View, position: Int) {

                val promos = promolist[position]

                myDialogBusiness = Dialog(activity)
                myDialogBusiness?.setContentView(R.layout.dialogbox_businessman)


                Picasso.get()
                        .load(promos.promoImageLink)
                        .placeholder(R.mipmap.ic_launcher)
                        .into(myDialogBusiness!!.reachPromoPicture)


                myDialogBusiness!!.specificPromoContainer.setOnClickListener {
                    PromoListAdapter.promoProfile = promos
                    startActivity(Intent(activity!!,Business_PromoProfile::class.java))

                }
                myDialogBusiness!!.tvPromoNameBusinessman.text = promolist[position].promoname
                myDialogBusiness!!.tvPromoLocationBusinessman.text = promolist[position].promoPlace
                myDialogBusiness!!.tvSent.text = promos.sent.toString() + " people sent"
                myDialogBusiness!!.tvDismssed.text = promos.dismissed.toString() + " people dismissed"
                myDialogBusiness!!.tvViewed.text = promos.viewed.toString() + " people viewed"
                myDialogBusiness!!.tvInterested.text = promos.interested.toString()+ " people are interested"

                myDialogBusiness?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                myDialogBusiness?.window?.setGravity(Gravity.CENTER)
                myDialogBusiness?.show()

            }
        }))



            }

    fun setAdapter(promoList:ArrayList<PromoModel>){
        var newlist = ArrayList<PromoModel>()

        for (i in 0 until promoList.size){
            if(promoList[i].posterBy==LoginActivity.userUIDS)
                newlist.add(promolist[i])
        }
        promolist = newlist
        mAdapter = PromoListAdapter(activity!!,mSelected, newlist)
        recyclerViewReach.adapter = mAdapter



    }
}



