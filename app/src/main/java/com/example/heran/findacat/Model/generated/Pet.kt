package com.example.heran.findacat.Model.generated

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//@JsonClass(generateAdapter = true)

data class Pet(

	@Json(name="sex")
	val sex: Sex? = null,

	@Json(name="description")
	val description: Description? = null,

	@Json(name="media")
	val media: Media? = null,

	@Json(name="shelterId")
	val shelterId: ShelterId? = null,

	@Json(name="breeds")
	val breeds: Breeds? = null,

	@Json(name="size")
	val size: Size? = null,

	@Json(name="contact")
	val contact: Contact? = null,

	@Json(name="lastUpdate")
	val lastUpdate: LastUpdate? = null,

	@Json(name="options")
	val options: Options? = null,

	@Json(name="name")
	val name: Name? = null,

	@Json(name="animal")
	val animal: Animal? = null,

	@Json(name="id")
	val id: Id? = null,

	@Json(name="mix")
	val mix: Mix? = null,

	@Json(name="age")
	val age: Age? = null,

	@Json(name="shelterPetId")
	val shelterPetId: ShelterPetId? = null,

	@Json(name="status")
	val status: Status? = null
)