package com.example.heran.findacat.Model.generated

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//@JsonClass(generateAdapter = true)

data class Petfinder(

	@Json(name="@xmlns:xsi")
	val xmlnsXsi: String? = null,

	@Json(name="@xsi:noNamespaceSchemaLocation")
	val xsiNoNamespaceSchemaLocation: String? = null,

	@Json(name="header")
	val header: Header? = null,

    @Json(name = "lastOffset")
    val lastOffset : LastOffset? = null,

    @Json(name="pets")
    val pets: Pets? = null,

	@Json(name="pet")
	val pet: Pet? = null
)