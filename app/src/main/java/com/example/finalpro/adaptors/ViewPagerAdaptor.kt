package com.example.finalpro.adaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.example.finalpro.R

class ViewPagerAdaptor(private var list: List<Int>,private val context: Context) : PagerAdapter() {
    // private lateinit var ImgList : List<Int>

    private lateinit var layoutInflater: LayoutInflater

    override fun getCount(): Int {
        return list.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.viewpager_list,container,false)
        val img = view.findViewById<ImageView>(R.id.imageview)
        img.setImageResource(list[position])
//        img.setOnClickListener {
//            fun onClick(view: View){
//                Snackbar.make(view,"Image"+position,Snackbar.LENGTH_LONG).show()
//            }
//        }
        container.addView(view,0)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}