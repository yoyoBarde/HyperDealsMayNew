package com.example.kent.hyperdeals.Admin


import android.app.AlertDialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.example.kent.hyperdeals.Model.*
import com.example.kent.hyperdeals.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_pending_promo.view.*
import org.jetbrains.anko.layoutInflater
import java.util.*


class PendingPromoAdapter(val context:Context , private val promolist : ArrayList<PromoModel>) : RecyclerView.Adapter<PendingPromoAdapter.ViewHolder>(){
    companion object {
        lateinit  var promoProfile: PromoModel
    }
    val database = FirebaseFirestore.getInstance()
    val TAG = "PromoListAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):

            ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_pending_promo,parent,false))


    override fun getItemCount(): Int  = promolist.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val promos = promolist[position]


        Picasso.get()
                .load(promos.promoImageLink)
                .placeholder(R.drawable.hyperdealslogofinal)
                .into(holder.ivPromoImage)

        /* holder.ivPromoImage.setImageResource(R.drawable.bench)*/

        holder.tvPromoTitile.text = promos.promoname
        holder.tvPromoDescription.text = promos.promodescription
        holder.tvPromoLocation.text = promos.promoPlace
        holder.tvPromoContact.text = promos.promoContactNumber
        holder.tvPromoStore.text = promos.promoStore

        holder.tvView.setOnClickListener {  showDialog(promos)}


    }



    inner class ViewHolder (view:View): RecyclerView.ViewHolder(view){
        val ivPromoImage = view.PromoImage!!
        val tvPromoTitile = view.PromoTitle!!
        val tvPromoDescription = view.PromoDescription!!
        val tvPromoLocation = view.PromoPlace!!
        val tvPromoContact = view.PromoConctact!!
        val tvPromoStore = view.PromoStore!!
        val tvView = view.tvViewPromo!!




        val container = view.PromoContainer!!
    }
    fun showDialog(myPromo:PromoModel) {

        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = context.layoutInflater
        val dialogView = inflater.inflate(R.layout.model_aprrove_promo, null)

        val tvArea = dialogView.findViewById(R.id.tvArea) as TextView
        val aprrove = dialogView.findViewById(R.id.btnAprrove) as Button
        val discard = dialogView.findViewById(R.id.btnDiscard) as Button
        val promoName = dialogView.findViewById(R.id.tvPromoname) as TextView
        val storeName = dialogView.findViewById(R.id.tvStorename) as TextView
        val etRadius = dialogView.findViewById(R.id.etRadies) as EditText
        val promoPic = dialogView.findViewById(R.id.ivPromoPic) as ImageView


        tvArea.text = myPromo.areaSqm.toString()
        promoName.text = myPromo.promoname
        storeName.text = myPromo.promoStore
        Picasso.get()
                .load(myPromo.promoImageLink)
                .placeholder(R.drawable.hyperdealslogofinal)
                .into(promoPic)
        dialogBuilder.setView(dialogView)



        Log.e(TAG,"showDialog")



        val b = dialogBuilder.create()
        b.show()
        b.setCancelable(true)

        aprrove.setOnClickListener {
            var myPromotoDB = PromoModelBusinessman(myPromo.promoimage,myPromo.promoStore,myPromo.promoContactNumber,myPromo.promodescription,myPromo.promoPlace,myPromo.promoname,myPromo.promoLatLng
                    ,myPromo.promoImageLink, GeoPoint(myPromo.promoLocation.latitude,myPromo.promoLocation.longitude),
                    "asd",0,0,0,0,
                    myPromo.startDateYear,
                    myPromo.startDateMonth,
                    myPromo.startDateDay,
                    myPromo.endDateYear,
                    myPromo.endDateMonth,
                    myPromo.endDateDay,
                    myPromo.startTimeHour,
                    myPromo.startTimeMinute,
                    myPromo.endTimeHour ,
                    myPromo.endTimeMinute
            )
            myPromotoDB.posterBy = myPromo.posterBy
            myPromotoDB.approved = true
            myPromotoDB.areaSqm = etRadius.text.toString().toInt()


            database.collection("PromoDetails").document(myPromo.promoStore).set(myPromotoDB).addOnSuccessListener {

                Log.e(TAG,"Success rewriting")
                b.dismiss()

            }

        }
        discard.setOnClickListener {


            b.dismiss()

        }





    }








}