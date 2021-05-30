package ca.limin.restaurantfinder.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DirectionParse {
    public List<List<HashMap<String, String>>> parse(Direction direction)
    {
        List<List<HashMap<String, String>>> routes = new ArrayList<>();
        try {
            //Loop for All Routes
            for(int i = 0; i < direction.getRoutes().size(); i++)
            {
                List<HashMap<String, String>> path = new ArrayList<>();

                //Loop for all Leg routes
                for(int j = 0;i < direction.getRoutes().get(i).getLegs().size();j++)
                {
                    //Loop for ALL points
                    for(int k = 0;k < direction.getRoutes().get(i).getLegs().get(j).getSteps().size(); k++)
                    {
                        String polyline = direction.getRoutes().get(i).getLegs().get(j).getSteps().get(k).getPolyline().getPoints();
                        List<LatLng> list = decodePolyline(polyline);

                        //Loop for ALL points
                        for(int l = 0; l < list.size(); l++)
                        {
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("lat", Double.toString(list.get(l).latitude));
                            hashMap.put("lon", Double.toString(list.get(l).longitude));
                            path.add(hashMap);
                        }
                    }
                    routes.add(path);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return routes;
    }

    private List<LatLng> decodePolyline(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }
}
