package dell.com.allindiaitr.interfaces;

import dell.com.allindiaitr.models.HraRentReceiptParams;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface NewAPIInterface {

    @POST("api/HraRentReceipt?")
    Call<Void> hra_rentReceipt(@Body HraRentReceiptParams hraRentReceiptParams);

}
