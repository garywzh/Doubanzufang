package org.garywzh.doubanzufang.network;

import org.garywzh.doubanzufang.model.ResponseBean;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by WZH on 2016/7/3.
 */
public interface ItemsService {
    @GET("search.php")
    Observable<ResponseBean> getItems(@Query("kw") String location, @Query("sp") String sp);
}
