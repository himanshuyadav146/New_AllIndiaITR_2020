package dell.com.allindiaitr.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Comment {

    @SerializedName("CommentId")
    @Expose
    var commentId: Any? = null
    @SerializedName("UserAssessmentYearId")
    @Expose
    var userAssessmentYearId: Any? = null
    @SerializedName("CommentType")
    @Expose
    var commentType: String? = null
    @SerializedName("Comment")
    @Expose
    var comment: Any? = null
    @SerializedName("CreatedBy")
    @Expose
    var createdBy: String? = null
    @SerializedName("CreatedDate")
    @Expose
    var createdDate: String? = null
    @SerializedName("Message")
    @Expose
    var message: Any? = null

    private constructor (){
    }
    companion object {
        val instance: Comment by lazy { Comment() }
    }

}