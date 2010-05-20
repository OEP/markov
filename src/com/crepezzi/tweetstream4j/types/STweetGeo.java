/*
Copyright (c) 2010, John Crepezzi <john@crepezzi.com>
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the <organization> nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.crepezzi.tweetstream4j.types;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Class representing a twitter geo location for the twitter streaming api
 * @author jcrepezzi
 */
public class STweetGeo {

    private String type;
    private Double latitude, longitude;
    private JSONObject json;

    /**
     * Parse a twitter geo location from JSON object
     * @param obj the JSON object to parse
     * @return The resultant STweetGeo object.
     */
    static STweetGeo parseJSON(JSONObject obj) {
        STweetGeo stg = new STweetGeo();
        stg.json = obj;
        stg.type = obj.optString("type");

        //get coordinates
        JSONArray coords = obj.optJSONArray("coordinates");
        if (coords == null) return null; //we don't have a proper geo element
        stg.latitude = coords.getDouble(0);
        stg.longitude = coords.getDouble(1);

        return stg;
    }

    public JSONObject getJSON() {
        return this.json;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getType() {
        return type;
    }

    /**
     * Equals depends on latitude and longitude
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final STweetGeo other = (STweetGeo) obj;
        if (this.latitude != other.latitude && (this.latitude == null || !this.latitude.equals(other.latitude))) return false;
        if (this.longitude != other.longitude && (this.longitude == null || !this.longitude.equals(other.longitude))) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.latitude != null ? this.latitude.hashCode() : 0);
        hash = 79 * hash + (this.longitude != null ? this.longitude.hashCode() : 0);
        return hash;
    }

    /**
     * A debug string.
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return type + " " + latitude + " " + longitude;
    }
    
}
