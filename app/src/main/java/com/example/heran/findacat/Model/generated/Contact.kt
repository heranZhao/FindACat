package com.example.heran.findacat.Model.generated

import com.squareup.moshi.Json

data class Contact(

	@Json(name="zip")
	val zip: Zip? = null,

	@Json(name="phone")
	val phone: Phone? = null,

	@Json(name="address2")
	val address2: Address2? = null,

	@Json(name="city")
	val city: City? = null,

	@Json(name="address1")
	val address1: Address1? = null,

	@Json(name="state")
	val state: State? = null,

	@Json(name="fax")
	val fax: Fax? = null,

	@Json(name="email")
	val email: Email? = null
)