package com.ttt.elpucherito.activities.shoppingcart
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.ttt.elpucherito.R
import com.ttt.elpucherito.activities.restaurants.RestaurantsActivity
import com.ttt.elpucherito.activities.users.LoginActivity
import com.ttt.elpucherito.activities.users.ModifyProfile
import com.ttt.elpucherito.db.ElPucheritoDB
import kotlinx.android.synthetic.main.activity_order.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.concurrent.thread

class OrderActivity : AppCompatActivity() {
    private var expandableListView: ExpandableListView? = null
    private var adapter: ExpandableListAdapter? = null
    private var titleList: List<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        val imageArrow : ImageView = findViewById(R.id.imageArrow)

        imageArrow.setOnClickListener(View.OnClickListener {
            var intent = Intent(this, RestaurantsActivity::class.java)
            startActivity(intent)
        })

        title = "KotlinApp"
        expandableListView = findViewById(R.id.expendableList)
        if (expandableListView != null) {
            val listData = getDatesOrders()
            var tvEmpty:TextView = findViewById(R.id.tv_empty_order)
            if(listData.isEmpty()){

                tvEmpty.setText("No existe ningun pedido")

            }else{
                tvEmpty.setText("")
            }
            titleList = ArrayList(listData.keys)
            adapter = OrderExpandableListAdapter(this, titleList as ArrayList<String>, listData)

            expandableListView!!.setAdapter(adapter)
            expandableListView!!.setOnGroupExpandListener { groupPosition ->
                Toast.makeText(
                    applicationContext,
                    (titleList as ArrayList<String>)[groupPosition] + " List Expanded.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            expandableListView!!.setOnGroupCollapseListener { groupPosition ->
                Toast.makeText(
                    applicationContext,
                    (titleList as ArrayList<String>)[groupPosition] + " List Collapsed.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            expandableListView!!.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
                Toast.makeText(
                    applicationContext,
                    "Clicked: " + (titleList as ArrayList<String>)[groupPosition] + " -> " + listData[(
                            titleList as
                                    ArrayList<String>
                            )
                            [groupPosition]]!!.get(
                        childPosition
                    ),
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
        }
    }

    private fun getDatesOrders() : MutableMap<String, List<String>>{

        var dateOrders : MutableMap<String, List<String>> = HashMap()
        Thread {

            val db : ElPucheritoDB = ElPucheritoDB.getInstance(this)

            val orders = db.shoppingCartDao().getOrders()
            var totalPrice = 0f
            orders.forEach{

                var mutableList: MutableList<String> = ArrayList()
                var dishes = db.dishShoppingCartDao().getDishesWithShoppingCartID(it.shopping_cart_id!!)
                dishes.forEach {

                    var dish = db.dishDao().getDishByID(it.dish_id)
                    mutableList.add("${dish.name} \n  \n ${dish.price}€ \n")
                    totalPrice += dish.price
                }
                mutableList.add("Precio Total ${totalPrice}€")
                totalPrice = 0f

                dateOrders[it.purchase_date!!.toString()] = mutableList

            }
        }.start()

        Thread.sleep(100)

        return TreeMap(dateOrders)
    }

    override fun onBackPressed() {
        startActivity(Intent(this, RestaurantsActivity::class.java))
    }

    override fun onPause() {
        super.onPause()
        this.finish()
    }

}
