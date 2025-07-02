package com.example.astroyoumat.data.remote

import android.content.Context
import android.util.Log
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

object sendNotification {
    private const val TAG = "sendNotification"

    //  Use your generated OAuth 2.0 access token (for now, hardcoded)
    //remember this token expire 1hour then generate the new token then use
    //because it's only used for testing purpose not deployment phase
    private const val accessToken = "ya29.c.c0ASRK0GaQq8-bLIvBalqs7r-RQLFqRtil4PFQM6BLiQIlcSaM0QM1jvLWoD-YRleIX6O9j1hPvo3SH7aDWqlbe90BbUxbIvwpMF5zYK6gRaVl1B4HL-p-N5h-6uGVdm4G6PPAKx8iyZ8BS1Bz87PXztY_fBPrTKMiSPXjdHTVJetPkwBPiYNXY5_CnEjLYglQ1P-SUAUCA3Q95R_14Y8_snyYExlOLtx5MthyEC0a-sNVXB-EQNHFZYt23qD-WnTVZjduXDmtCVnv7EmpHC2jIUsPT744kORS5zBRpDm1ajrMlg-fulGS1fZVhS7tJK7Y3maRoFC1vkgAe5hJLsPEAQ1tLMzjgWmRA20R5iCo9WK4NxB9TrKEygMoH385AYjF6J2JBaQ_VuyuV_iWaBor6oM4sw1xJwynb9iIsiqQXx-lrUMjOzdVV87_7vRwjhbmnWrhV4QqeJVqebxXOos8f9h1ju6FYci7svVOw1lqWwoJ4pn6qxRx6opdjF-MtVsWU5ZbxVegb_6qaS_hBBcgVM2gYRkt5a4562pj1uymM9QR-i8i_cvhrfQxWxQdInt-cgVIjtBcMVOSssndItgWfu2lr3l0j2qUncuvrVniYw15UksbvtFcJ7zRR_tmhBMqn6teVsi4wshM8feVqig8inMVWoQdWXtQ1enIy3qjfpuMBqwbmntzrcqZwx4yieoj08S5oIIh994BIrkn4SaIjSz_cSUho2qb36Yg7OUxh0UreXeg3ZJSyBBoZO2WmulSaoc3SSyweYyUIZvmQxkUqX5qh1zzczhX1aWYqeWowSVvt7xezUgwlM_ZWc0XolMoonyFlo3e8ZVjJQ2IQ0Wyi42Qe7c0j_V1Ob6_2gumu3di7uhYdx9adyvIcWM8psmIxw_7jhfnxnYJZe60ZzUXc6wFkj2gru_IRyYl2F4VYeSF-UszfnotovSdOXpnyUgwiYB5e1a-zUMjqp91Q9712R5I31cjUSFhzUu8-uWFf-J1f1emUb_mvlh" // 🟡 Paste your access token here

    //  Replace with your Firebase project ID
    private const val projectId = "youmatconnector"

    fun sendPushNotification(
        context: Context,
        token: String,
        title: String,
        message: String
    ) {
        val requestQueue = Volley.newRequestQueue(context)

        val json = JSONObject().apply {
            put("message", JSONObject().apply {
                put("token", token)
                put("notification", JSONObject().apply {
                    put("title", title)
                    put("body", message)
                })
            })
        }

        val url = "https://fcm.googleapis.com/v1/projects/$projectId/messages:send"

        val request = object : JsonObjectRequest(
            Method.POST,
            url,
            json,
            { response -> Log.d(TAG, " FCM sent: $response") },
            { error -> Log.e(TAG, " FCM failed: ${error.message}", error) }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return mutableMapOf(
                    "Authorization" to "Bearer $accessToken",
                    "Content-Type" to "application/json"
                )
            }
        }

        requestQueue.add(request)
    }
}
