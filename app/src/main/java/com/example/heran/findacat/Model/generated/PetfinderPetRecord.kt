package com.example.heran.findacat.Model.generated

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//@JsonClass(generateAdapter = true)

data class PetfinderPetRecord(

	@Json(name="petfinder")
	val petfinder: Petfinder? = null,

	@Json(name="@version")
	val version: String? = null,

	@Json(name="@encoding")
	val encoding: String? = null
)