package com.juanantbuit.weatherproject.framework.ui.searchList

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.juanantbuit.weatherproject.R
import com.juanantbuit.weatherproject.databinding.SearchItemBinding
import com.juanantbuit.weatherproject.domain.models.CityInfoModel
import com.juanantbuit.weatherproject.domain.models.SearchItemModel
import com.juanantbuit.weatherproject.utils.LANG

class SearchItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = SearchItemBinding.bind(view)

    fun render(
        searchItem: SearchItemModel,
        onClickListener: (SearchItemModel) -> Unit,
        cityInfo: CityInfoModel
    ) {
        val countryName = getCountryName(cityInfo, searchItem)

        binding.cityAndCountry.text =
            itemView.context.getString(R.string.city_name, searchItem.name, countryName)
        binding.subcountry.text = searchItem.subcountry
        binding.temperature.text =
            itemView.context.getString(R.string.temperature, cityInfo.mainInfo.temp.toInt())

        loadIcon(getImageUrl(cityInfo.iconId[0].idIcon), binding.imageView)

        itemView.setOnClickListener { onClickListener(searchItem) }
    }

    /**
     * If the application is in English, it returns the country as returned by the searchItem
     * if it is in Spanish, search the country name in Spanish in the strings.
     */
    private fun getCountryName(cityInfo: CityInfoModel, searchItem: SearchItemModel): String {
        if (LANG == "en") {
            return searchItem.country
        }

        var countryId: String = cityInfo.country.countryId.lowercase()
        // Since "do" cannot be used as id, "dor" was used for the Dominican Republic.
        if (countryId == "do") countryId = "dor"

        val resourceId = itemView.context.resources.getIdentifier(
            countryId, "string", itemView.context.packageName
        )

        if(resourceId == 0) {
            return "NotFound"
        }

        return itemView.context.getString(resourceId)
    }

    private fun loadIcon(url: String, imageView: ImageView) {
        Glide.with(itemView).load(url).error(R.drawable.ic_launcher_foreground)
            .diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(imageView)
    }

    private fun getImageUrl(idIcon: String): String {
        return "https://openweathermap.org/img/wn/$idIcon@4x.png"
    }

}
