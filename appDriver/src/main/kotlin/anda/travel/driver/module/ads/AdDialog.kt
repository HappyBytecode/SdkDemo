package anda.travel.driver.module.ads

import anda.travel.driver.R
import anda.travel.driver.data.ad.AdvertisementEntity
import anda.travel.driver.databinding.HxycLayoutAdBinding
import anda.travel.driver.module.web.H5Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.Window
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.config.IndicatorConfig
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator

class AdDialog(context: Context, entities: List<AdvertisementEntity>) : Dialog(context) {
    private lateinit var binding: HxycLayoutAdBinding
    private var mList: ArrayList<AdvertisementEntity> = ArrayList()

    init {
        if (mList.isNotEmpty()) mList.clear()
        mList.addAll(entities)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCanceledOnTouchOutside(false)
        binding = HxycLayoutAdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (window != null) {
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        val adEntities: MutableList<AdvertisementEntity?> = ArrayList()
        for (i in mList.indices) {
            adEntities.add(mList[i])
        }
        //设置banner样式(显示圆形指示器)
        binding.banner.indicator = CircleIndicator(context)
        //设置指示器位置（指示器居中）
        binding.banner.setIndicatorGravity(IndicatorConfig.Direction.CENTER)
        //设置自动轮播，默认为true
        binding.banner.isAutoLoop(true)
        //设置轮播时间
        binding.banner.setLoopTime(2000)
        //设置banner样式(显示圆形指示器)
        binding.banner.adapter = object : BannerImageAdapter<AdvertisementEntity>(adEntities) {
            override fun onBindView(holder: BannerImageHolder, data: AdvertisementEntity, position: Int, size: Int) {
                //图片加载自己实现
                Glide.with(holder.itemView)
                        .load(data.imgUrl)
                        .thumbnail(Glide.with(holder.itemView).load(R.drawable.ad__banner_placeholder))
                        .error(R.drawable.ad__banner_placeholder)
                        .apply(RequestOptions.bitmapTransform(RoundedCorners(30)))
                        .into(holder.imageView)
            }
        }
        binding.banner.setOnBannerListener { _, position ->
            if (!adEntities[position]?.href.isNullOrEmpty()) {
                if (adEntities[position]?.href?.trim()!!.isNotEmpty()) {
                    if (!TextUtils.isEmpty(adEntities[position]?.uuid)) {
                        H5Activity.actionStart(
                                context, adEntities[position]?.href,
                                adEntities[position]?.title, adEntities[position]?.uuid
                        )
                    } else {
                        H5Activity.actionStart(
                                context, adEntities[position]?.href,
                                adEntities[position]?.title
                        )
                    }
                }
            }
            dismiss()
        }
        //banner设置方法全部调用完毕时最后调用
        binding.banner.start()
        binding.adClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        binding.banner.start()
    }

    override fun onStop() {
        super.onStop()
        //结束轮播
        binding.banner.stop()
    }
}
