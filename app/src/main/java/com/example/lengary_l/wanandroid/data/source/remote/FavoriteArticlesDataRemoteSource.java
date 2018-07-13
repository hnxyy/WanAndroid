package com.example.lengary_l.wanandroid.data.source.remote;

import android.support.annotation.NonNull;

import com.example.lengary_l.wanandroid.data.FavoriteArticleDetailData;
import com.example.lengary_l.wanandroid.data.FavoriteArticlesData;
import com.example.lengary_l.wanandroid.data.source.FavoriteArticlesDataSource;
import com.example.lengary_l.wanandroid.retrofit.RetrofitClient;
import com.example.lengary_l.wanandroid.retrofit.RetrofitService;

import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public class FavoriteArticlesDataRemoteSource implements FavoriteArticlesDataSource {
    @NonNull
    private static FavoriteArticlesDataRemoteSource INSTANCE;
    private static final String TAG = "FavoriteArticlesDataRem";
    private FavoriteArticlesDataRemoteSource() {

    }

    public static FavoriteArticlesDataRemoteSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FavoriteArticlesDataRemoteSource();
        }
        return INSTANCE;
    }



    @Override
    public Observable<List<FavoriteArticleDetailData>> getFavoriteArticles(final int page, final boolean forceUpdate, final boolean clearCache) {
        return RetrofitClient.getInstance()
                .create(RetrofitService.class)
                .getFavoriteArticles(page)
                .filter(new Predicate<FavoriteArticlesData>() {
                    @Override
                    public boolean test(FavoriteArticlesData favoriteArticlesData) throws Exception {
                        return favoriteArticlesData.getErrorCode() != -1;
                    }
                }).flatMap(new Function<FavoriteArticlesData, ObservableSource<List<FavoriteArticleDetailData>>>() {
                    @Override
                    public ObservableSource<List<FavoriteArticleDetailData>> apply(FavoriteArticlesData favoriteArticlesData) throws Exception {
                        return Observable.fromIterable(favoriteArticlesData.getData().getDatas()).toSortedList(new Comparator<FavoriteArticleDetailData>() {
                            @Override
                            public int compare(FavoriteArticleDetailData favoriteArticleDetailData, FavoriteArticleDetailData t1) {
                                if (favoriteArticleDetailData.getPublishTime() > t1.getPublishTime()) {
                                    return -1;
                                }else {
                                    return 1;
                                }
                            }
                        }).toObservable();
                    }
                });
    }

    @Override
    public boolean isExist(int userId, int id) {
        //The local has handle it
        return false;
    }
}
