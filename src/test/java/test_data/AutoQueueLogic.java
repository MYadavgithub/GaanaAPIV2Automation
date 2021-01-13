package test_data;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.json.JSONArray;
import org.json.JSONObject;
import common.CommonPath;
import common.Helper;
import config.Constants;
import utils.CsvReader;

public class AutoQueueLogic {

    Helper helper = new Helper();

    public ArrayList<String> getExperimentOneSongs(JSONArray data) {
        ArrayList<String> tracks_list = new ArrayList<>();
        Map<Long, String> tempData = new TreeMap<>();
        for (int i = 0; i < data.length(); i++) {
            String track_id = data.getJSONObject(i).getString("track_id").toString().trim();
            double played_duration = Double
                    .parseDouble(data.getJSONObject(i).optString("played_duration").toString().trim());
            long track_start_time = Long
                    .parseLong(data.getJSONObject(i).optString("track_start_time").toString().trim());
            if (played_duration > 15) {
                tempData.put(track_start_time, track_id);
            }
        }

        // tempData.put(1609942091248L, "33848114");
        // tempData.put(1609942091250L, "32687916");
        // tempData.put(1609942148250L, "29822818");
        // tempData.put(1609942190231L, "32116617");
        // Map<Long, String> filterData = new TreeMap<>();

        int tempSize = tempData.size();
        if (tempSize > 5) {
            int counter = 1;
            for (Long start_time : tempData.keySet()) {
                if (counter >= (tempSize + 5) - tempSize) {
                    tracks_list.add(tempData.get(start_time));
                    // filterData.put(start_time, tempData.get(start_time));
                }
                counter++;
            }
        } else {
            for (Long start_time : tempData.keySet()) {
                tracks_list.add(tempData.get(start_time));
            }
        }
        return tracks_list;
    }

    public String getAllTrackIds(String device_id, JSONObject tracks_details) {
        JSONArray tracks;
        ArrayList<String> trackList = new ArrayList<>();
        if (tracks_details.length() > 0) {
            if(device_id == null){
                tracks = tracks_details.getJSONArray("tracks");
            }else{
                tracks = tracks_details.getJSONArray(device_id);
            }
            Iterator<Object> itr = tracks.iterator();

            while (itr.hasNext()) {
                JSONObject track = (JSONObject) itr.next();
                String track_id = track.getString("track_id").trim();
                trackList.add(track_id);
            }

            return trackList.toString().replaceAll("[\\[\\]\\(\\)]", " ").trim();
        }
        return null;
    }

    public JSONObject getSavedData() {
        String file_location = CommonPath.AUTO_QUEUE_NEW_SEED_CSV_DATA_LOGIC_PATH;
        Map<Integer, ArrayList<String>> five_seeds = CsvReader
                .readCsvLineWise(file_location + Constants.FIVE_SEED_TRACKS_CSV);
        Map<Integer, ArrayList<String>> all_seeds = CsvReader
                .readCsvLineWise(file_location + Constants.ALL_SEED_TRACKS_CSV);
        JSONObject object = new JSONObject();
        JSONArray five_seeds_arr = new JSONArray();
        JSONArray all_seeds_arr = new JSONArray();
        object.put("five_seeds", createArray(five_seeds_arr, five_seeds));
        object.put("all_seeds", createArray(all_seeds_arr, all_seeds));
        return object;
    }

    private JSONArray createArray(JSONArray arr, Map<Integer, ArrayList<String>> seeds) {
        String keys[] = { "track_id", "release_year", "tags" };
        if (seeds.size() > 0) {
            for (Integer seed : seeds.keySet()) {
                ArrayList<String> data = seeds.get(seed);
                JSONObject obj = new JSONObject();
                obj.put(keys[0], data.get(0));
                obj.put(keys[1], data.get(1));
                obj.put(keys[2], data.get(2));
                arr.put(obj);
            }
        }
        return arr;
    }

    public ArrayList<String> filterZeroMatching(JSONObject realeaseYearEra) {
        JSONArray init_seeds = realeaseYearEra.getJSONArray("five_seeds");
        JSONArray all_tracks = realeaseYearEra.getJSONArray("all_seeds");

        ArrayList<String> tracks_to_remove = new ArrayList<>();
        Iterator<Object> itr = all_tracks.iterator();
        while (itr.hasNext()) {
            JSONObject track = (JSONObject) itr.next();
            for (int i = 0; i < init_seeds.length(); i++) {
                JSONObject init_track_obj = init_seeds.getJSONObject(i);
                boolean res = compareObjects(track, init_track_obj);
                if(!res)
                    tracks_to_remove.add(track.getString("track_id").trim());
            }
        }

        return tracks_to_remove;
    }

    private boolean compareObjects(JSONObject track, JSONObject init_track_obj) {
        List<Object> keys = helper.keys(track);
        for(Object key : keys){
            if(!key.equals("track_id")){
                String key_name = key.toString().trim();
                String track_key_data = track.getString(key_name).trim();
                String init_key_data = init_track_obj.getString(key_name).trim();
                if(key.equals("release_year")) {
                    int init_year = Integer.parseInt(init_key_data);
                    int track_year = Integer.parseInt(track_key_data);
                    if((track_year >= (init_year-10)) && (track_year <= (init_year+10))){
                        return true;
                    }
                }else if(key.equals("tags")){
                    String init_tags [] = init_key_data.split(",");
                    String track_tags [] = track_key_data.split(",");
                    for(String val : track_tags){
                        for(int i = 0; i<init_tags.length; i++){
                            if(val.trim().equals(init_tags[i].trim())){
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

	public JSONArray removeTracks(String device_id, JSONObject response, ArrayList<String> track_ids) {
        int count = 0;
        ArrayList<Integer> indexes = new ArrayList<>();
        JSONArray tracks = response.getJSONArray(device_id);

        for(String track_id : track_ids) {
            Iterator<Object> itr = tracks.iterator();
            while (itr.hasNext()) {
                JSONObject track = (JSONObject) itr.next();
                String actual_track_id = track.getString("track_id").trim();
                if(actual_track_id.equals(track_id)){
                    indexes.add(count);
                }
            }
            count++;
        }

        for(Integer index : indexes){
            tracks.remove(index);
        }

        return tracks;
    }
}