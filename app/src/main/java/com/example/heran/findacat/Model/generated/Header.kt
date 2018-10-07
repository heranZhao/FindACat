package com.example.heran.findacat.Model.generated

import com.squareup.moshi.Json

data class Header(

	@Json(name="version")
	val version: Version? = null,

	@Json(name="timestamp")
	val timestamp: Timestamp? = null,

	@Json(name="status")
	val status: Status? = null
)