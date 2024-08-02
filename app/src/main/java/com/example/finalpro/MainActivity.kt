package com.example.finalpro

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.finalpro.Login.PhoneActivity
import com.example.finalpro.adaptors.RecyclerFourAdaptor
import com.example.finalpro.adaptors.RecyclerOneAdaptor
import com.example.finalpro.adaptors.RecyclerThreeAdaptor
import com.example.finalpro.adaptors.RecyclerTwoAdaptor
import com.example.finalpro.adaptors.ViewPagerAdaptor
import com.example.finalpro.cart.Cart
import com.example.finalpro.dataModel.DataModel
import com.example.finalpro.myNotification.MyNotification
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import me.relex.circleindicator.CircleIndicator
import kotlin.collections.ArrayList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() ,NavigationView.OnNavigationItemSelectedListener{

    private val imagesList = mutableListOf<Int>()
    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var viewPager : ViewPager
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerThreeAdapter: RecyclerThreeAdaptor
    private lateinit var recyclerFourAdapter : RecyclerFourAdaptor
    private var productList = ArrayList<DataModel>()
    private var filteredProductList = ArrayList<DataModel>()
    private var productListTwo = ArrayList<DataModel>()
    private var filteredProductListTwo = ArrayList<DataModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        toolbar = findViewById(R.id.toolbar)
        navigationView = findViewById(R.id.NavigationView)
        drawerLayout = findViewById(R.id.drawerLayout)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val toggle = ActionBarDrawerToggle(this,drawerLayout,toolbar,0,0)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)

        // RecyclerVew One
        recyclerViewOne()

        // RecyclerView Two
        recyclerViewTwo()

        // RecyclerView Three
        recyclerViewThree()

        // RecyclerView Four
        recyclerViewFour()

        //Viewpager2
        postToList()

        viewPager = findViewById(R.id.viewpager)
        val adaptor = ViewPagerAdaptor(imagesList,this)
        viewPager.adapter = adaptor

        val indicator = findViewById<CircleIndicator>(R.id.indicator)
        indicator.setViewPager(viewPager)

      }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else{
        super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.categoryMenu -> {
                startActivity(Intent(this,AllCategories::class.java))
//                Toast.makeText(this,"Home Clicked",Toast.LENGTH_LONG).show()
            }
            R.id.cartMenu -> {
                startActivity(Intent(this, Cart::class.java))
            }
            R.id.myNotification -> {
                startActivity(Intent(this, MyNotification::class.java))
            }
            R.id.SignOut -> {
                auth = FirebaseAuth.getInstance()
                auth.signOut()
                startActivity(Intent(this, PhoneActivity::class.java))
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_two,menu)

        val searchtem = menu?.findItem(R.id.action_search)
        val searchView = searchtem?.actionView as SearchView

        if (searchView != null) {
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let {
                        CoroutineScope(Dispatchers.Default).launch {
                            val filteredList = productList.filter {
                                it.Name.contains(
                                    newText.toString(),
                                    ignoreCase = true
                                )
                            }
                            val filteredListfour = productListTwo.filter {
                                it.Name.contains(
                                    newText.toString(),
                                    ignoreCase = true
                                )
                            }

                            withContext(Dispatchers.Main) {
                                recyclerThreeAdapter.filteredList(filteredList)
                                recyclerFourAdapter.filteredListfour(filteredListfour)
                            }
                        }
                    }
                    return true
                }
            })
            searchView.queryHint = "Search Items ..."
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.menuNotification -> {
                startActivity(Intent(applicationContext, MyNotification::class.java))
            }
            R.id.menuCart -> {
                startActivity(Intent(this,Cart::class.java))
            }
        }
        return true
    }

    private fun recyclerViewOne(){
        val rv = findViewById<RecyclerView>(R.id.rv_one)
        val rvOne = ArrayList<DataModel>()
        rvOne.add(DataModel(0,"Top Offers","https://rukminim1.flixcart.com/flap/128/128/image/f15c02bfeb02d15d.png?q=100",0,""))
        rvOne.add(DataModel(0,"Perfumes","https://rukminim1.flixcart.com/image/416/416/j3j1le80/deodorant/v/q/r/120-absolute-deo-for-women-deodorant-spray-fogg-women-original-imaeun5djamxrgy3.jpeg?q=70",0,""))
        rvOne.add(DataModel(0,"Mobiles","https://rukminim1.flixcart.com/flap/128/128/image/22fddf3c7da4c4f4.png?q=100",0,""))
        rvOne.add(DataModel(0,"Fashion", "https://rukminim1.flixcart.com/flap/128/128/image/82b3ca5fb2301045.png?q=100",0,"",))
        rvOne.add(DataModel(0,"Electronic","https://rukminim1.flixcart.com/image/150/150/jlqwpe80-1/tv-entertainment-unit/d/t/f/particle-board-holland-tv-unit-black-forzza-black-original-imaf8t5ybywcdtys.jpeg?q=70",0,""))

        val adaptorOne = RecyclerOneAdaptor(this, rvOne)
        rv?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv?.adapter = adaptorOne
    }

    private fun recyclerViewTwo(){
        val rv = findViewById<RecyclerView>(R.id.rv_two)
        val recyclerTwo = ArrayList<DataModel>()
        recyclerTwo.add(DataModel(0,"Deals","https://images-eu.ssl-images-amazon.com/images/G/31/IMG15/Irfan/GATEWAY/MSO/Appliances-QC-PC-186x116--B08345R1ZW._SY116_CB667322346_.jpg",0,"Shop Now"))
        recyclerTwo.add(DataModel(0,"Deals","https://images-eu.ssl-images-amazon.com/images/G/31/IMG15/Irfan/GATEWAY/MSO/Appliances-QC-PC-186x116--B08RDL6H79._SY116_CB667322346_.jpg",0,"Shop Now"))
        recyclerTwo.add(DataModel(0,"Deals","https://images-eu.ssl-images-amazon.com/images/G/31/IMG15/Irfan/GATEWAY/MSO/186x116---wm._SY116_CB667322346_.jpg",0,"Shop Now"))

        val adaptorTwo = RecyclerTwoAdaptor(this,recyclerTwo)
        rv?.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        rv?.adapter = adaptorTwo
    }

    private fun recyclerViewThree(){

        val rv_three = findViewById<RecyclerView>(R.id.rv_three)
       // val rvthree = ArrayList<DataModel>()
        productList.add(DataModel(0,"Apple Products", "https://rukminim2.flixcart.com/image/416/416/xif0q/mobile/k/l/l/-original-imagtc5fz9spysyk.jpeg?q=70",149900 , "APPLE iPhone 15 (Blue, 128 GB)"))
        productList.add(DataModel(0,"Apple iPhone 15 Pro Max","https://m.media-amazon.com/images/I/81Os1SDWpcL._SL1500_.jpg", 151700,"Apple iPhone 15 Pro Max (256 GB) - Black Titanium"))
        productList.add(DataModel(0,"Samsung Galaxy Z Flip5 5G","https://m.media-amazon.com/images/I/71rMdsTWkmL._SL1500_.jpg",99999, "Samsung Galaxy Z Flip5 5G AI Smartphone (Cream, 8GB RAM, 256GB Storage)"))
        productList.add(DataModel(0,"Samsung Galaxy Z Fold4 5G","https://m.media-amazon.com/images/I/71Vd1+ZnY-L._SL1500_.jpg",112999,"Samsung Galaxy Z Fold4 5G (Graygreen, 12GB RAM, 256GB Storage) "))
        productList.add(DataModel(0,"Samsung S24 Ultra 5G AI Smart","https://m.media-amazon.com/images/I/71RVuBs3q9L._SL1500_.jpg",129999,"Samsung Galaxy S24 Ultra 5G AI Smartphone (Titanium Black, 12GB, 256GB Storage)"))
        productList.add(DataModel(0,"Samsung Galaxy S24 5G AI Smartphone", "https://m.media-amazon.com/images/I/71ez69tPl4L._SL1500_.jpg", 79999,"Samsung Galaxy S24 5G AI Smartphone (Amber Yellow, 8GB, 256GB Storage)"))
        productList.add(DataModel(0,"OnePlus Nord Buds 2r True Wireless","https://m.media-amazon.com/images/I/51oMWaW7tKL._SL1500_.jpg",1799, "OnePlus Nord Buds 2r True Wireless in Ear Earbuds with Mic, 12.4mm Drivers, Playback:Upto 38hr case,4-Mic Design, IP55 Rating [Deep Grey]"))
        productList.add(DataModel(0,"Noise Buds N1 in-Ear Truly Wireless Earbuds","https://m.media-amazon.com/images/I/61WAxDWqh3L._SL1500_.jpg", 1299,"Noise Buds N1 in-Ear Truly Wireless Earbuds with Chrome Finish, 40H of Playtime, Quad Mic with ENC, Ultra Low Latency(up to 40 ms), Instacharge(10 min=120 min), BT v5.3(Carbon Black)"))
        productList.add(DataModel(0,"boAt Airdopes 141 Bluetooth","https://m.media-amazon.com/images/I/41nzykE3sIL._SX300_SY300_QL70_FMwebp_.jpg",999,"boAt Airdopes 141 Bluetooth TWS Earbuds with 42H Playtime,Low Latency Mode for Gaming, ENx Tech, IWP, IPX4 Water Resistance, Smooth Touch Controls(Bold Black), in Ear"))
        productList.add(DataModel(0, "boAt Airdopes Atom 81", "https://m.media-amazon.com/images/I/61yyQD1KLOL._SL1500_.jpg",799, "boAt Airdopes Atom 81 in Ear TWS Earbuds with Upto 50H Playtime, Quad Mics ENx Tech, 13MM Drivers,Super Low Latency(50ms), ASAP Charge, BT v5.3(Opal Black)"))
        productList.add(DataModel(0,"OnePlus 12", "https://m.media-amazon.com/images/I/61BAuSC0UnL._SL1464_.jpg",69998,"OnePlus 12 (Flowy Emerald, 16GB RAM, 512GB Storage)"))
        productList.add(DataModel(0,"Nike", "https://static.nike.com/a/images/t_PDP_1728_v1/f_auto,q_auto:eco/fd4a337e-51cf-46d1-9ef4-e2d41463c12d/air-force-1-07-fresh-shoes-bBRnbq.png",11295,"Nike Air Force 1 '07 Fresh\n" +
                "Men's Shoes"))
        productList.add(DataModel(0,"Nike","https://static.nike.com/a/images/t_prod_ss/w_640,c_limit,f_auto/01a2382d-fa11-4fe0-802a-b4f1758819f9/air-force-1-x-tiffany-co-1837-dz1382-001-release-date.jpg",33595,"Air Force 1 x Tiffany & Co."))
        productList.add(DataModel(0,"Cricket Bat","https://m.media-amazon.com/images/I/51NwRpWIHXL._SL1355_.jpg",100,"VINOX Wood Precision Strike Willow Cricket Bat: Elevate Your Game To New Heights (3)"))

        filteredProductList.addAll(productList)

        recyclerThreeAdapter = RecyclerThreeAdaptor(this, filteredProductList)
        rv_three?.layoutManager = GridLayoutManager(this, 2)
        rv_three?.adapter = recyclerThreeAdapter
    }

    private fun recyclerViewFour(){
        val rv_four = findViewById<RecyclerView>(R.id.rv_four)
      //  val rvfour = ArrayList<DataModel>()
        productListTwo.add(DataModel(0,"ALPINO High Protein Dark Chocolate","https://rukminim2.flixcart.com/image/416/416/xif0q/jam-spread/4/j/w/1-high-protein-dark-chocolate-peanut-butter-smooth-jar-1-nut-original-imahyjk3mnkz5td2.jpeg?q=70&crop=false",631,"ALPINO High Protein Dark Chocolate Peanut Butter Smooth 1 kg"))
        productListTwo.add(DataModel(0,"ALPINO Chocolate","https://rukminim2.flixcart.com/image/416/416/xif0q/jam-spread/n/f/7/1-chocolate-peanut-butter-crunch-jar-1-nut-butter-alpino-original-imahyjjzycxzmtzm.jpeg?q=70&crop=false",421,"ALPINO Chocolate Peanut Butter Crunch 1 kg"))
        productListTwo.add(DataModel(0,"ALPINO Organic Natural Peanut Butter","https://rukminim2.flixcart.com/image/416/416/xif0q/jam-spread/d/n/m/1000-organic-natural-peanut-butter-crunch-unsweetened-jar-1-nut-original-imahyjpsyfhfjfrh.jpeg?q=70&crop=false",401,"ALPINO Organic Natural Peanut Butter Crunch Unsweetened 1000 g"))
        productListTwo.add(DataModel(0,"MYFITNESS Chocolate","https://rukminim2.flixcart.com/image/416/416/xif0q/jam-spread/p/g/p/-original-imah28h7eshqxbcc.jpeg?q=70&crop=false",291,"MYFITNESS Chocolate Peanut Butter (Smooth) 510 g"))
        productListTwo.add(DataModel(0,"MYFITNESS High Protein","https://rukminim2.flixcart.com/image/416/416/xif0q/jam-spread/q/g/r/-original-imagvrdzk2fcgeaj.jpeg?q=70&crop=false",349,"MYFITNESS High Protein Dark Chocolate Peanut Butter (Crispy) 510 g"))
        productListTwo.add(DataModel(0,"AS-IT-IS Nutrition AS-IT-IS ATOM","https://rukminim2.flixcart.com/image/416/416/l1zc6fk0/jam-spread/l/8/g/1-as-it-is-atom-chocolate-peanut-butter-crunchy-1kg-gluten-free-original-imagdfbdzgkunmf4.jpeg?q=70&crop=false",454,"AS-IT-IS Nutrition AS-IT-IS ATOM Chocolate Peanut Butter Crunchy 1Kg | Gluten Free 1 kg"))
       // productListTwo.add(DataModel(406,"SAMSUNG GALAXY S23 FE 5G","https://m.media-amazon.com/images/I/617l83eY1rL._SL1500_.jpg",41200,"SAMSUNG GALAXY S23 FE 5G (MINT 256 GB STORAGE) (8 GB RAM)"))
       // productListTwo.add(DataModel(407,"Samsung Galaxy S20 FE 5G","https://m.media-amazon.com/images/I/812yohjGZ2L._SL1500_.jpg",30890,"Samsung Galaxy S20 FE 5G (Cloud Mint, 8GB RAM, 128GB Storage)"))

        filteredProductListTwo.addAll(productListTwo)
        recyclerFourAdapter = RecyclerFourAdaptor(this, filteredProductListTwo)
        rv_four?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        rv_four?.adapter = recyclerFourAdapter

    }

    private fun addToList(image: Int){
        imagesList.add(image)
    }

    private fun postToList(){
        for (i in 0..0){
            addToList(R.drawable.v1)
            addToList(R.drawable.v2)
            addToList(R.drawable.v3)
            addToList(R.drawable.v4)
            addToList(R.drawable.v5)
        }
    }
}


