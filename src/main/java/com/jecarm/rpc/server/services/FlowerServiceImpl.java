package com.jecarm.rpc.server.services;

/**
 * Created by loagosad on 2018/8/19.
 */
public class FlowerServiceImpl implements FlowerService{
    private String[] flowers = new String[]{
            "Rose",
            "Lily",
            "Tulips",
            "Babysbreath"
    };

    @Override
    public String[] getAllFlowers(){
        return flowers;
    }
}
