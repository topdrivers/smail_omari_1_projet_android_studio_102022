package com.example.go4lunch.domain;

import java.util.Map;

public interface RestaurantSpecificDataProvider {
    void getRestaurantClientCountByWorkplace(String workplaceId, Callback<Map<String, Integer>> callback);
}
