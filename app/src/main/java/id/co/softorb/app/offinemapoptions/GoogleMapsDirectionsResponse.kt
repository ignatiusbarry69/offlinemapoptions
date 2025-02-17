package id.co.softorb.app.offinemapoptions

import com.google.gson.annotations.SerializedName

data class GoogleMapsDirectionsResponse(

	@field:SerializedName("routes")
	val routes: List<RoutesItem>,

	@field:SerializedName("status")
	val status: String
)

data class EndLocation(

	@field:SerializedName("lng")
	val lng: Any,

	@field:SerializedName("lat")
	val lat: Any
)

data class Polyline(

	@field:SerializedName("points")
	val points: String
)

data class RoutesItem(

	@field:SerializedName("legs")
	val legs: List<LegsItem>,

	@field:SerializedName("overview_polyline")
	val overviewPolyline: OverviewPolyline
)

data class StepsItem(

	@field:SerializedName("start_location")
	val startLocation: StartLocation,

	@field:SerializedName("travel_mode")
	val travelMode: String,

	@field:SerializedName("html_instructions")
	val htmlInstructions: String,

	@field:SerializedName("end_location")
	val endLocation: EndLocation,

	@field:SerializedName("polyline")
	val polyline: Polyline
)

data class LegsItem(

	@field:SerializedName("start_address")
	val startAddress: String,

	@field:SerializedName("end_address")
	val endAddress: String,

	@field:SerializedName("steps")
	val steps: List<StepsItem>
)

data class StartLocation(

	@field:SerializedName("lng")
	val lng: Any,

	@field:SerializedName("lat")
	val lat: Any
)

data class OverviewPolyline(

	@field:SerializedName("points")
	val points: String
)
