package oop.project.androidoopproject.ui

object NavRoutes {
    const val LOGIN              = "login"
    const val REGISTER           = "register"
    const val SEEKER_DASHBOARD   = "seeker_dashboard"
    const val EMPLOYER_DASHBOARD = "employer_dashboard"
    const val SEEKER_PROFILE     = "seeker_profile"
    const val EMPLOYER_PROFILE   = "employer_profile"
    const val JOB_DETAIL         = "job_detail/{jobId}"
    const val APPLICANTS         = "applicants/{jobId}/{jobTitle}"

    fun jobDetail(jobId: Long) = "job_detail/$jobId"

    fun applicants(jobId: Long, jobTitle: String) =
        "applicants/$jobId/${java.net.URLEncoder.encode(jobTitle, "UTF-8")}"
}