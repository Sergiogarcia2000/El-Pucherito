package com.ttt.elpucherito.activity.restaurantActiviy

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ttt.elpucherito.R
import com.ttt.elpucherito.activity.restaurantsActivity.RestaurantItem
import com.ttt.elpucherito.db.ElPucheritoDB
import com.ttt.elpucherito.db.entity.Assessment
import com.ttt.elpucherito.db.relations.RestaurantWithDishes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class RestaurantActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)

        val restaurantAvatar : ImageView = findViewById(R.id.restaurant_avatar)
        val restaurantTvName : TextView  = findViewById(R.id.restaurant_tv_name)
        val restaurantAssesment : RatingBar = findViewById(R.id.restaurant_ratingbar)

        var restaurant = intent.getSerializableExtra("Restaurant") as RestaurantItem

        Thread{
            val db : ElPucheritoDB = ElPucheritoDB.getInstance(this)
            val userEmail = db.userDao().getLoggedUser().email
            val assesment = db.assessmentDao().getAssessmentByEmailAndRestaurantID(userEmail, restaurant.resturant_id!!)
            if (assesment != null){
                restaurantAssesment.rating = assesment.rating
            }else{
                restaurantAssesment.rating = 0f
            }
        }.start()

        var context : Context = this

        restaurantAssesment.setOnRatingBarChangeListener( object : RatingBar.OnRatingBarChangeListener {
            override fun onRatingChanged(p0: RatingBar?, p1: Float, p2: Boolean) {
                Thread {
                    println("Se ha cambiado!!")
                    var db: ElPucheritoDB = ElPucheritoDB.getInstance(context)
                    var userMail = db.userDao().getLoggedUser().email
                    launch {
                        try {
                            db.assessmentDao().insertAssessments(
                                Assessment(
                                    null,
                                    userMail,
                                    p1,
                                    restaurant.resturant_id!!
                                )
                            )
                            db.assessmentDao().getAssessments().forEach{
                                println(it.rating)
                            }
                        } catch (e: Exception) {
                            Log.e("UNIQUE", "Este usuario ya ha dado su puntuacion")
                        }
                    }
                }.start()
            }
        })

        restaurantTvName.text = restaurant.name

        val id = this.resources.getIdentifier(restaurant.image, "drawable", this.packageName)
        restaurantAvatar.setImageResource(id)

        val dishes : ArrayList<DishItem> = ArrayList()

        val recyclerView : RecyclerView = findViewById(R.id.restaurant_chart_dishes)
        recyclerView.adapter = ChartAdapter(dishes,this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
    }

    private fun getDishesFromRestaurant() : ArrayList<DishItem> {

        val dishList : List<RestaurantWithDishes>
        val dishItems : ArrayList<DishItem> = ArrayList()

        val db : ElPucheritoDB = ElPucheritoDB.getInstance(this)

        dishList = db.restaurantDao().getDishes()

        return dishItems

    }
}
